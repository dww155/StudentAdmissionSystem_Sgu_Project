package com.sgu.data_quick_insert.controller;

import com.sgu.data_quick_insert.model.ImportProfile;
import com.sgu.data_quick_insert.model.InsertResult;
import com.sgu.data_quick_insert.model.PreviewData;
import com.sgu.data_quick_insert.util.ExcelStreamingReader;
import com.sgu.data_quick_insert.util.JdbcBatchInsertService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {
    private static final int PREVIEW_LIMIT = 200;
    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/xt_bangquydoi?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root";

    @FXML
    private TabPane profileTabPane;
    @FXML
    private TextField templateColumnsField;
    @FXML
    private TextField excelPathField;
    @FXML
    private TextField sheetNameField;
    @FXML
    private TextField batchSizeField;
    @FXML
    private TextField jdbcUrlField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField tableNameField;
    @FXML
    private Button browseButton;
    @FXML
    private Button previewButton;
    @FXML
    private Button testConnectionButton;
    @FXML
    private Button insertButton;
    @FXML
    private ProgressIndicator busyIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<ObservableList<String>> previewTable;
    @FXML
    private TextArea logArea;

    private Path selectedExcelPath;
    private List<ImportProfile> profiles;

    @FXML
    public void initialize() {
        excelPathField.setEditable(false);
        templateColumnsField.setEditable(false);
        tableNameField.setEditable(false);
        logArea.setEditable(false);
        busyIndicator.setVisible(false);
        busyIndicator.setManaged(false);
        previewTable.setPlaceholder(new Label("Select an Excel file and click Preview."));

        batchSizeField.setText("1000");
        jdbcUrlField.setText(DEFAULT_JDBC_URL);
        usernameField.setText(DEFAULT_USERNAME);
        passwordField.setText(DEFAULT_PASSWORD);

        profiles = JdbcBatchInsertService.profiles();
        initTabs();
        statusLabel.setText("Ready");
    }

    private void initTabs() {
        profileTabPane.getTabs().clear();
        for (ImportProfile profile : profiles) {
            Tab tab = new Tab(profile.displayName());
            tab.setClosable(false);
            tab.setUserData(profile);
            Label label = new Label("Import profile for table: " + profile.tableName());
            label.setWrapText(true);
            tab.setContent(new VBox(label));
            profileTabPane.getTabs().add(tab);
        }

        profileTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                applySelectedProfile(newTab);
            }
        });

        if (!profileTabPane.getTabs().isEmpty()) {
            profileTabPane.getSelectionModel().select(0);
            applySelectedProfile(profileTabPane.getTabs().get(0));
        }
    }

    private void applySelectedProfile(Tab tab) {
        Object userData = tab.getUserData();
        if (!(userData instanceof ImportProfile profile)) {
            return;
        }
        tableNameField.setText(profile.tableName());
        templateColumnsField.setText(profile.columnHint());
        statusLabel.setText("Active tab: " + profile.displayName());
        appendLog("Switched to profile: " + profile.displayName() + " -> " + profile.tableName());
    }

    @FXML
    private void onBrowseExcel() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Excel file");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel workbook (*.xlsx)", "*.xlsx")
        );

        File selectedFile = chooser.showOpenDialog(getWindow());
        if (selectedFile == null) {
            return;
        }
        selectedExcelPath = selectedFile.toPath();
        excelPathField.setText(selectedExcelPath.toString());
        statusLabel.setText("Selected: " + selectedFile.getName());
        appendLog("Selected file: " + selectedExcelPath);
    }

    @FXML
    private void onPreview() {
        if (!ensureFileSelected()) {
            return;
        }

        Task<PreviewData> task = new Task<>() {
            @Override
            protected PreviewData call() throws Exception {
                return ExcelStreamingReader.preview(selectedExcelPath, sheetNameField.getText(), true, PREVIEW_LIMIT);
            }
        };

        task.setOnRunning(event -> {
            setBusy(true);
            statusLabel.setText("Loading preview...");
            appendLog("Loading preview (up to " + PREVIEW_LIMIT + " rows)...");
        });
        task.setOnSucceeded(event -> {
            setBusy(false);
            PreviewData preview = task.getValue();
            renderPreview(preview);
            statusLabel.setText("Preview loaded: " + preview.rows().size() + " row(s).");
            appendLog("Preview ready. Displaying " + preview.rows().size() + " row(s).");
        });
        task.setOnFailed(event -> {
            setBusy(false);
            Throwable error = task.getException();
            statusLabel.setText("Preview failed.");
            appendLog("Preview failed: " + rootMessage(error));
            showError("Could not load preview.", error);
        });

        runAsync(task);
    }

    @FXML
    private void onTestConnection() {
        String jdbcUrl = jdbcUrlField.getText();
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            showWarning("JDBC URL is required before testing connection.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JdbcBatchInsertService.testConnection(jdbcUrlField.getText(), usernameField.getText(), passwordField.getText());
                return null;
            }
        };

        task.setOnRunning(event -> {
            setBusy(true);
            statusLabel.setText("Testing database connection...");
            appendLog("Testing database connection...");
        });
        task.setOnSucceeded(event -> {
            setBusy(false);
            statusLabel.setText("Database connection successful.");
            appendLog("Database connection successful.");
            showInfo("Connection successful.");
        });
        task.setOnFailed(event -> {
            setBusy(false);
            Throwable error = task.getException();
            statusLabel.setText("Database connection failed.");
            appendLog("Connection failed: " + rootMessage(error));
            showError("Connection failed.", error);
        });

        runAsync(task);
    }

    @FXML
    private void onInsert() {
        if (!ensureFileSelected()) {
            return;
        }

        ImportProfile profile = selectedProfile();
        if (profile == null) {
            showWarning("Please select an import tab.");
            return;
        }

        int batchSize;
        try {
            batchSize = Integer.parseInt(batchSizeField.getText().trim());
            if (batchSize <= 0 || batchSize > 10000) {
                showWarning("Batch size must be between 1 and 10000.");
                return;
            }
        } catch (NumberFormatException ex) {
            showWarning("Batch size must be a number.");
            return;
        }

        Task<InsertResult> task = new Task<>() {
            @Override
            protected InsertResult call() throws Exception {
                return JdbcBatchInsertService.importByProfile(
                        profile,
                        selectedExcelPath,
                        sheetNameField.getText(),
                        tableNameField.getText(),
                        jdbcUrlField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        batchSize,
                        message -> Platform.runLater(() -> appendLog(message))
                );
            }
        };

        task.setOnRunning(event -> {
            setBusy(true);
            statusLabel.setText("Importing " + profile.displayName() + "...");
            appendLog("Started import for profile: " + profile.displayName());
        });
        task.setOnSucceeded(event -> {
            setBusy(false);
            InsertResult result = task.getValue();
            statusLabel.setText("Import completed.");
            appendLog("Completed. Inserted: " + result.insertedRows() + ", processed: " + result.processedRows() + ", skipped: " + result.skippedRows());
            showInfo("Import completed.\nProfile: " + profile.displayName() + "\nInserted rows: " + result.insertedRows() + "\nProcessed rows: " + result.processedRows() + "\nSkipped rows: " + result.skippedRows());
        });
        task.setOnFailed(event -> {
            setBusy(false);
            Throwable error = task.getException();
            statusLabel.setText("Import failed.");
            appendLog("Import failed: " + rootMessage(error));
            showError("Import failed.", error);
        });

        runAsync(task);
    }

    private ImportProfile selectedProfile() {
        Tab selectedTab = profileTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return null;
        }
        Object userData = selectedTab.getUserData();
        return userData instanceof ImportProfile profile ? profile : null;
    }

    private boolean ensureFileSelected() {
        if (selectedExcelPath == null) {
            showWarning("Please choose an Excel file first.");
            return false;
        }
        return true;
    }

    private void renderPreview(PreviewData preview) {
        previewTable.getColumns().clear();

        List<String> headers = preview.headers();
        for (int i = 0; i < headers.size(); i++) {
            final int index = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers.get(i));
            column.setCellValueFactory(cell -> {
                ObservableList<String> values = cell.getValue();
                String value = index < values.size() ? values.get(index) : "";
                return new ReadOnlyStringWrapper(value);
            });
            column.setPrefWidth(160);
            previewTable.getColumns().add(column);
        }

        ObservableList<ObservableList<String>> tableRows = FXCollections.observableArrayList();
        for (List<String> row : preview.rows()) {
            ObservableList<String> displayRow = FXCollections.observableArrayList(row);
            while (displayRow.size() < headers.size()) {
                displayRow.add("");
            }
            tableRows.add(displayRow);
        }
        previewTable.setItems(tableRows);
    }

    private void setBusy(boolean busy) {
        busyIndicator.setVisible(busy);
        busyIndicator.setManaged(busy);
        browseButton.setDisable(busy);
        previewButton.setDisable(busy);
        testConnectionButton.setDisable(busy);
        insertButton.setDisable(busy);
    }

    private void appendLog(String message) {
        String line = "[" + LOG_TIME_FORMAT.format(LocalTime.now()) + "] " + message;
        if (logArea.getText().isEmpty()) {
            logArea.setText(line);
        } else {
            logArea.appendText(System.lineSeparator() + line);
        }
    }

    private void runAsync(Task<?> task) {
        Thread worker = new Thread(task, "data-quick-insert-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String header, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(rootMessage(throwable));
        alert.showAndWait();
    }

    private static String rootMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error.";
        }
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String message = root.getMessage();
        return message == null || message.isBlank() ? root.toString() : message;
    }

    private Window getWindow() {
        if (previewTable.getScene() == null) {
            return null;
        }
        return previewTable.getScene().getWindow();
    }
}
