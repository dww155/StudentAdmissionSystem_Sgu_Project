package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.ListMajorSubjectGroupCreationRequest;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.admission_desktop.service.MajorService;
import com.sgu.admission_desktop.service.MajorSubjectGroupService;
import com.sgu.admission_desktop.service.SubjectCombinationService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MajorSubjectsPageController implements Initializable {

    @FXML
    private TableView<MajorSubjectRow> table;

    @FXML
    private TableColumn<MajorSubjectRow, String> colMaNganh;

    @FXML
    private TableColumn<MajorSubjectRow, String> colTenNganh;

    @FXML
    private TableColumn<MajorSubjectRow, String> colMaToHop;

    @FXML
    private TableColumn<MajorSubjectRow, String> colTenToHop;

    private final ObservableList<MajorSubjectRow> items = FXCollections.observableArrayList();
    private final MajorSubjectGroupService majorSubjectGroupService = new MajorSubjectGroupService();
    private final MajorService majorService = new MajorService();
    private final SubjectCombinationService subjectCombinationService = new SubjectCombinationService();

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "MANGANH", "major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.optional("majorName", "TEN_NGANHCHUAN", "major name", "ten nganh chuan"),
            ExcelImportUtil.ColumnDefinition.required("subjectDefinition", "MA_TO_HOP", "ma to hop"),
            ExcelImportUtil.ColumnDefinition.optional("keyCode", "tb_keys", "key code", "tb keys"),
            ExcelImportUtil.ColumnDefinition.optional("subjectCombinationCode", "TEN_TO_HOP", "subject combination code", "ten to hop"),
            ExcelImportUtil.ColumnDefinition.optional("baseFlag", "Gốc", "goc"),
            ExcelImportUtil.ColumnDefinition.required("deviation", "Độ lệch", "do lech", "deviation")
    );

    private Map<String, String> majorNameByCode = Map.of();
    private Map<String, String> subjectNameByCode = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaNganh.setCellValueFactory(v -> v.getValue().maNganhProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTenToHop.setCellValueFactory(v -> v.getValue().tenToHopProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadMajorSubjectGroups();
    }

    private void loadMajorSubjectGroups() {
        try {
            majorNameByCode = loadMajorNames();
            subjectNameByCode = loadSubjectNames();

            ApiResponse<List<MajorSubjectGroupResponse>> response = majorSubjectGroupService.getAll();
            List<MajorSubjectGroupResponse> groups = response.getData() == null ? List.of() : response.getData();

            items.setAll(groups.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
            ControllerSupport.showError("Load major-subject links failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add major-subject link",
                        List.of(
                                "Major code",
                                "Subject combination code",
                                "Subject 1",
                                "Weight 1",
                                "Subject 2",
                                "Weight 2",
                                "Subject 3",
                                "Weight 3",
                                "Key code",
                                "Deviation"
                        )
                )
                .ifPresent(this::createMajorSubjectGroup);
    }

    @FXML
    private void onImport() {
        try {
            var importedGroups = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import subject groups",
                    IMPORT_COLUMNS,
                    this::toImportedMajorSubjectGroupRequest
            );

            if (importedGroups.isEmpty()) {
                return;
            }

            List<MajorSubjectGroupCreationRequest> requests = importedGroups.get();
            majorSubjectGroupService.createBulk(
                    ListMajorSubjectGroupCreationRequest.builder()
                            .majorSubjectGroupCreationRequestList(requests)
                            .build()
            );
            loadMajorSubjectGroups();
            ControllerSupport.showInfo("Import subject groups", "Imported " + requests.size() + " subject groups from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import subject groups failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import subject groups failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createMajorSubjectGroup(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("majorCode", data.get("Major code"));
            payload.put("subjectCombinationCode", data.get("Subject combination code"));
            payload.put("mon1", data.get("Subject 1"));
            payload.put("subject1Weight", ControllerSupport.parseInt(data.get("Weight 1"), "Weight 1"));
            payload.put("mon2", data.get("Subject 2"));
            payload.put("subject2Weight", ControllerSupport.parseInt(data.get("Weight 2"), "Weight 2"));
            payload.put("mon3", data.get("Subject 3"));
            payload.put("subject3Weight", ControllerSupport.parseInt(data.get("Weight 3"), "Weight 3"));
            payload.put("keyCode", data.get("Key code"));
            payload.put("deviation", ControllerSupport.parseDecimal(data.get("Deviation"), "Deviation"));

            payload.put("n1", false);
            payload.put("to", false);
            payload.put("li", false);
            payload.put("ho", false);
            payload.put("si", false);
            payload.put("va", false);
            payload.put("su", false);
            payload.put("di", false);
            payload.put("ti", false);
            payload.put("other", false);
            payload.put("ktpl", false);

            MajorSubjectGroupCreationRequest request = ControllerSupport.convert(payload, MajorSubjectGroupCreationRequest.class);
            majorSubjectGroupService.create(request);
            loadMajorSubjectGroups();
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid major-subject link", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create major-subject link failed", ControllerSupport.extractMessage(e));
        }
    }

    private MajorSubjectGroupCreationRequest toImportedMajorSubjectGroupRequest(Map<String, String> data) {
        ParsedSubjectGroup parsedSubjectGroup = parseSubjectDefinition(
                requireText(data.get("subjectDefinition"), "MA_TO_HOP")
        );

        String majorCode = requireText(data.get("majorCode"), "MANGANH");
        String subjectCombinationCode = firstNonBlank(
                data.get("subjectCombinationCode"),
                parsedSubjectGroup.subjectCombinationCode()
        );
        SubjectFlags flags = buildSubjectFlags(parsedSubjectGroup.subjects());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("majorCode", majorCode);
        payload.put("subjectCombinationCode", subjectCombinationCode);
        payload.put("mon1", parsedSubjectGroup.subjects().get(0).subjectCode());
        payload.put("subject1Weight", parsedSubjectGroup.subjects().get(0).weight());
        payload.put("mon2", parsedSubjectGroup.subjects().get(1).subjectCode());
        payload.put("subject2Weight", parsedSubjectGroup.subjects().get(1).weight());
        payload.put("mon3", parsedSubjectGroup.subjects().get(2).subjectCode());
        payload.put("subject3Weight", parsedSubjectGroup.subjects().get(2).weight());
        payload.put("keyCode", firstNonBlank(data.get("keyCode"), majorCode + "_" + subjectCombinationCode));
        payload.put("n1", flags.n1());
        payload.put("to", flags.to());
        payload.put("li", flags.li());
        payload.put("ho", flags.ho());
        payload.put("si", flags.si());
        payload.put("va", flags.va());
        payload.put("su", flags.su());
        payload.put("di", flags.di());
        payload.put("ti", flags.ti());
        payload.put("other", flags.other());
        payload.put("ktpl", flags.ktpl());
        payload.put("deviation", ControllerSupport.parseDecimal(data.get("deviation"), "Độ lệch"));
        return ControllerSupport.convert(payload, MajorSubjectGroupCreationRequest.class);
    }

    private Map<String, String> loadMajorNames() {
        ApiResponse<List<MajorResponse>> response = majorService.getAll();
        List<MajorResponse> majors = response.getData() == null ? List.of() : response.getData();
        return majors.stream()
                .map(ControllerSupport::toMap)
                .collect(Collectors.toMap(
                        item -> ControllerSupport.safeString(item.get("majorCode")),
                        item -> ControllerSupport.safeString(item.get("majorName")),
                        (left, right) -> left
                ));
    }

    private Map<String, String> loadSubjectNames() {
        ApiResponse<List<SubjectCombinationResponse>> response = subjectCombinationService.getAll();
        List<SubjectCombinationResponse> subjectCombinations = response.getData() == null ? List.of() : response.getData();
        return subjectCombinations.stream()
                .map(ControllerSupport::toMap)
                .collect(Collectors.toMap(
                        item -> ControllerSupport.safeString(item.get("code")),
                        item -> ControllerSupport.safeString(item.get("name")),
                        (left, right) -> left
                ));
    }

    private MajorSubjectRow toRow(MajorSubjectGroupResponse group) {
        Map<String, Object> data = ControllerSupport.toMap(group);
        String majorCode = ControllerSupport.safeString(data.get("majorCode"));
        String subjectCode = ControllerSupport.safeString(data.get("subjectCombinationCode"));

        return new MajorSubjectRow(
                majorCode,
                majorNameByCode.getOrDefault(majorCode, majorCode),
                subjectCode,
                subjectNameByCode.getOrDefault(subjectCode, subjectCode)
        );
    }

    private ParsedSubjectGroup parseSubjectDefinition(String value) {
        String trimmed = value.trim();
        int openIndex = trimmed.indexOf('(');
        int closeIndex = trimmed.lastIndexOf(')');
        if (openIndex <= 0 || closeIndex <= openIndex) {
            throw new IllegalArgumentException("MA_TO_HOP must look like B03(TO-3,VA-3,SI-1).");
        }

        String subjectCombinationCode = trimmed.substring(0, openIndex).trim();
        String content = trimmed.substring(openIndex + 1, closeIndex).trim();
        String[] parts = content.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("MA_TO_HOP must contain exactly 3 weighted subjects.");
        }

        List<ParsedWeightedSubject> subjects = List.of(
                parseWeightedSubject(parts[0], 1),
                parseWeightedSubject(parts[1], 2),
                parseWeightedSubject(parts[2], 3)
        );
        return new ParsedSubjectGroup(subjectCombinationCode, subjects);
    }

    private ParsedWeightedSubject parseWeightedSubject(String value, int index) {
        String trimmed = value.trim();
        int separatorIndex = trimmed.lastIndexOf('-');
        if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
            throw new IllegalArgumentException("Subject " + index + " must look like TO-3.");
        }

        String subjectCode = trimmed.substring(0, separatorIndex).trim().toUpperCase(Locale.ROOT);
        int weight = ControllerSupport.parseInt(trimmed.substring(separatorIndex + 1).trim(), "Weight " + index);
        return new ParsedWeightedSubject(subjectCode, weight);
    }

    private SubjectFlags buildSubjectFlags(List<ParsedWeightedSubject> subjects) {
        boolean n1 = false;
        boolean to = false;
        boolean li = false;
        boolean ho = false;
        boolean si = false;
        boolean va = false;
        boolean su = false;
        boolean di = false;
        boolean ti = false;
        boolean other = false;
        boolean ktpl = false;

        for (ParsedWeightedSubject subject : subjects) {
            String code = subject.subjectCode();
            switch (code) {
                case "N1", "NN" -> n1 = true;
                case "TO" -> to = true;
                case "LI" -> li = true;
                case "HO" -> ho = true;
                case "SI" -> si = true;
                case "VA" -> va = true;
                case "SU" -> su = true;
                case "DI" -> di = true;
                case "TI" -> ti = true;
                case "KTPL", "GDCD" -> ktpl = true;
                default -> other = true;
            }
        }

        return new SubjectFlags(n1, to, li, ho, si, va, su, di, ti, other, ktpl);
    }

    private String requireText(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = ControllerSupport.trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private record ParsedSubjectGroup(String subjectCombinationCode, List<ParsedWeightedSubject> subjects) {
    }

    private record ParsedWeightedSubject(String subjectCode, int weight) {
    }

    private record SubjectFlags(
            boolean n1,
            boolean to,
            boolean li,
            boolean ho,
            boolean si,
            boolean va,
            boolean su,
            boolean di,
            boolean ti,
            boolean other,
            boolean ktpl
    ) {
    }
}
