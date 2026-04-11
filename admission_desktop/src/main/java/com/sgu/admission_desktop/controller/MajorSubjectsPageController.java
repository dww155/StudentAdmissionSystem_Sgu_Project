package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.admission_desktop.service.MajorService;
import com.sgu.admission_desktop.service.MajorSubjectGroupService;
import com.sgu.admission_desktop.service.SubjectCombinationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
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

            // Optional flags in API schema.
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
}
