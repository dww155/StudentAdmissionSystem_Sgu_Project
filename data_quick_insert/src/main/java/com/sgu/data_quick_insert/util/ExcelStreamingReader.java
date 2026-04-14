package com.sgu.data_quick_insert.util;

import com.sgu.data_quick_insert.model.PreviewData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ExcelStreamingReader {

    @FunctionalInterface
    public interface ExcelRowConsumer {
        void accept(long rowNumber, List<String> rowValues) throws Exception;
    }

    private ExcelStreamingReader() {
    }

    public static PreviewData preview(Path filePath, String sheetName, boolean firstRowHeader, int maxRows) throws Exception {
        if (maxRows <= 0) {
            throw new IllegalArgumentException("Preview row limit must be greater than 0.");
        }

        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        long[] scannedRows = new long[]{0L};
        boolean[] headerCaptured = new boolean[]{!firstRowHeader};

        int parserLimit = firstRowHeader ? maxRows + 1 : maxRows;
        readSheet(filePath, sheetName, parserLimit, (rowNumber, rowValues) -> {
            if (SqlIdentifierValidator.isBlankRow(rowValues)) {
                return;
            }
            if (firstRowHeader && !headerCaptured[0]) {
                headers.addAll(rowValues);
                headerCaptured[0] = true;
                return;
            }
            rows.add(new ArrayList<>(rowValues));
            scannedRows[0]++;
        });

        int maxColumnCount = Math.max(headers.size(), rows.stream().mapToInt(List::size).max().orElse(0));
        if (headers.isEmpty()) {
            for (int i = 0; i < maxColumnCount; i++) {
                headers.add("Column " + (i + 1));
            }
        } else if (headers.size() < maxColumnCount) {
            for (int i = headers.size(); i < maxColumnCount; i++) {
                headers.add("Column " + (i + 1));
            }
        }

        for (List<String> row : rows) {
            while (row.size() < headers.size()) {
                row.add("");
            }
        }
        return new PreviewData(headers, rows, scannedRows[0]);
    }

    public static void streamRows(Path filePath, String sheetName, ExcelRowConsumer consumer) throws Exception {
        readSheet(filePath, sheetName, -1, consumer);
    }

    private static void readSheet(Path filePath, String sheetName, int maxRows, ExcelRowConsumer consumer) throws Exception {
        Objects.requireNonNull(filePath, "Excel file path is required.");
        Objects.requireNonNull(consumer, "Row consumer is required.");
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Excel file not found: " + filePath);
        }
        if (!filePath.toString().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx files are supported.");
        }

        String requestedSheetName = sheetName == null ? "" : sheetName.trim();
        boolean sheetSpecified = !requestedSheetName.isEmpty();
        boolean sheetProcessed = false;

        try (OPCPackage pkg = OPCPackage.open(filePath.toFile(), PackageAccess.READ)) {
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
            DataFormatter formatter = new DataFormatter(Locale.getDefault());

            while (sheetIterator.hasNext()) {
                try (InputStream sheet = sheetIterator.next()) {
                    String currentSheet = sheetIterator.getSheetName();
                    if (sheetSpecified && !requestedSheetName.equals(currentSheet)) {
                        continue;
                    }

                    parseSheet(styles, sharedStrings, formatter, sheet, consumer, maxRows);
                    sheetProcessed = true;
                    break;
                }
            }

            if (!sheetProcessed) {
                if (sheetSpecified) {
                    throw new IllegalArgumentException("Sheet not found: '" + requestedSheetName + "'.");
                }
                throw new IllegalArgumentException("No sheet found in workbook.");
            }
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException("Invalid Excel file format.", e);
        }
    }

    private static void parseSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable sharedStrings,
            DataFormatter formatter,
            InputStream sheetInput,
            ExcelRowConsumer consumer,
            int maxRows
    ) throws Exception {
        XMLReader parser = XMLHelper.newXMLReader();
        SheetRowHandler rowHandler = new SheetRowHandler(consumer, maxRows);
        ContentHandler contentHandler = new XSSFSheetXMLHandler(
                styles,
                null,
                sharedStrings,
                rowHandler,
                formatter,
                false
        );
        parser.setContentHandler(contentHandler);

        try {
            parser.parse(new InputSource(sheetInput));
        } catch (StopSheetReadException ignored) {
            // Preview limit reached.
        } catch (ConsumerExecutionException e) {
            if (e.getCause() instanceof Exception ex) {
                throw ex;
            }
            throw e;
        }
    }

    private static final class SheetRowHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final ExcelRowConsumer consumer;
        private final int maxRows;

        private List<String> currentRowValues = new ArrayList<>();
        private int currentColumn = -1;
        private int emittedRows = 0;

        private SheetRowHandler(ExcelRowConsumer consumer, int maxRows) {
            this.consumer = consumer;
            this.maxRows = maxRows;
        }

        @Override
        public void startRow(int rowNum) {
            currentRowValues = new ArrayList<>();
            currentColumn = -1;
        }

        @Override
        public void endRow(int rowNum) {
            if (maxRows > 0 && emittedRows >= maxRows) {
                throw StopSheetReadException.INSTANCE;
            }
            try {
                consumer.accept(rowNum + 1L, trimTrailingBlankCells(currentRowValues));
            } catch (Exception ex) {
                throw new ConsumerExecutionException(ex);
            }
            emittedRows++;
            if (maxRows > 0 && emittedRows >= maxRows) {
                throw StopSheetReadException.INSTANCE;
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            int nextColumn = cellReference == null ? currentColumn + 1 : new CellReference(cellReference).getCol();
            for (int i = currentColumn + 1; i < nextColumn; i++) {
                currentRowValues.add("");
            }
            currentRowValues.add(formattedValue == null ? "" : formattedValue);
            currentColumn = nextColumn;
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // Not needed.
        }
    }

    private static List<String> trimTrailingBlankCells(List<String> values) {
        int end = values.size() - 1;
        while (end >= 0) {
            String value = values.get(end);
            if (value != null && !value.trim().isEmpty()) {
                break;
            }
            end--;
        }
        if (end < 0) {
            return List.of();
        }
        return new ArrayList<>(values.subList(0, end + 1));
    }

    private static final class StopSheetReadException extends RuntimeException {
        private static final StopSheetReadException INSTANCE = new StopSheetReadException();

        private StopSheetReadException() {
            super(null, null, false, false);
        }
    }

    private static final class ConsumerExecutionException extends RuntimeException {
        private ConsumerExecutionException(Exception cause) {
            super(cause);
        }
    }
}
