package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantCreationRequest;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.Applicant.ListApplicantCreationRequest;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class StudentsPageController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<StudentRow> table;

    @FXML
    private TableColumn<StudentRow, String> colMaTs;

    @FXML
    private TableColumn<StudentRow, String> colHoTen;

    @FXML
    private TableColumn<StudentRow, String> colCccd;

    @FXML
    private TableColumn<StudentRow, String> colNgaySinh;

    @FXML
    private TableColumn<StudentRow, String> colEmail;

    @FXML
    private TableColumn<StudentRow, String> colSdt;

    private final ObservableList<StudentRow> master = FXCollections.observableArrayList();
    private final ObservableList<StudentRow> filtered = FXCollections.observableArrayList();
    private final ApplicantService applicantService = new ApplicantService();
    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("registrationNumber", "Registration number", "registration", "ma ts", "mats", "so bao danh"),
            ExcelImportUtil.ColumnDefinition.required("lastName", "Last name", "lastname", "ho lot", "ho"),
            ExcelImportUtil.ColumnDefinition.required("firstName", "First name", "firstname", "ten"),
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD", "citizenid"),
            ExcelImportUtil.ColumnDefinition.required("dateOfBirth", "Date of birth", "dob", "ngay sinh"),
            ExcelImportUtil.ColumnDefinition.required("email", "Email"),
            ExcelImportUtil.ColumnDefinition.required("phoneNumber", "Phone", "phone", "sdt", "so dien thoai"),
            ExcelImportUtil.ColumnDefinition.required("gender", "Gender", "gioi tinh"),
            ExcelImportUtil.ColumnDefinition.required("birthPlace", "Birth place", "noi sinh"),
            ExcelImportUtil.ColumnDefinition.required("applicantType", "Applicant type", "doi tuong"),
            ExcelImportUtil.ColumnDefinition.required("region", "Region", "khu vuc")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colCccd.setCellValueFactory(v -> v.getValue().cccdProperty());
        colNgaySinh.setCellValueFactory(v -> v.getValue().ngaySinhProperty());
        colEmail.setCellValueFactory(v -> v.getValue().emailProperty());
        colSdt.setCellValueFactory(v -> v.getValue().sdtProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(filtered);

        loadApplicants();
        applyFilter("");

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
    }

    @FXML
    private void onSearch() {
        applyFilter(searchField.getText());
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add applicant",
                        List.of(
                                "Registration number",
                                "Last name",
                                "First name",
                                "CCCD",
                                "Date of birth (yyyy-MM-dd)",
                                "Email",
                                "Phone",
                                "Gender",
                                "Birth place",
                                "Applicant type",
                                "Region"
                        )
                )
                .ifPresent(this::createApplicant);
    }

    @FXML
    private void onImport() {
        try {
            var importedApplicants = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import applicants",
                    IMPORT_COLUMNS,
                    this::toImportedApplicantRequest
            );

            if (importedApplicants.isEmpty()) {
                return;
            }

            List<ApplicantCreationRequest> requests = importedApplicants.get();
            applicantService.createBulk(
                    ListApplicantCreationRequest.builder()
                            .applicantCreationRequestList(requests)
                            .build()
            );
            loadApplicants();
            ControllerSupport.showInfo("Import applicants", "Imported " + requests.size() + " applicants from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import applicants failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import applicants failed", ControllerSupport.extractMessage(e));
        }
    }

    private void loadApplicants() {
        try {
            ApiResponse<List<ApplicantResponse>> response = applicantService.getAll();
            List<ApplicantResponse> applicants = response.getData() == null ? List.of() : response.getData();

            master.setAll(applicants.stream()
                    .map(this::toRow)
                    .toList());
            applyFilter(searchField == null ? "" : searchField.getText());
        } catch (Exception e) {
            master.clear();
            filtered.clear();
            ControllerSupport.showError("Load applicants failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createApplicant(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("registrationNumber", data.get("Registration number"));
            payload.put("lastName", data.get("Last name"));
            payload.put("firstName", data.get("First name"));
            payload.put("cccd", data.get("CCCD"));
            payload.put("dateOfBirth", ControllerSupport.parseDate(data.get("Date of birth (yyyy-MM-dd)"), "Date of birth"));
            payload.put("email", data.get("Email"));
            payload.put("phoneNumber", data.get("Phone"));
            payload.put("gender", data.get("Gender"));
            payload.put("birthPlace", data.get("Birth place"));
            payload.put("applicantType", data.get("Applicant type"));
            payload.put("region", data.get("Region"));

            ApplicantCreationRequest request = ControllerSupport.convert(payload, ApplicantCreationRequest.class);
            applicantService.create(request);
            loadApplicants();
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid applicant", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create applicant failed", ControllerSupport.extractMessage(e));
        }
    }

    private ApplicantCreationRequest toImportedApplicantRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("registrationNumber", data.get("registrationNumber"));
        payload.put("lastName", data.get("lastName"));
        payload.put("firstName", data.get("firstName"));
        payload.put("cccd", data.get("cccd"));
        payload.put("dateOfBirth", ControllerSupport.parseDate(data.get("dateOfBirth"), "Date of birth"));
        payload.put("email", data.get("email"));
        payload.put("phoneNumber", data.get("phoneNumber"));
        payload.put("gender", data.get("gender"));
        payload.put("birthPlace", data.get("birthPlace"));
        payload.put("applicantType", data.get("applicantType"));
        payload.put("region", data.get("region"));
        return ControllerSupport.convert(payload, ApplicantCreationRequest.class);
    }

    private void applyFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        filtered.setAll(master.filtered(r ->
                q.isEmpty()
                        || r.maTs().toLowerCase(Locale.ROOT).contains(q)
                        || r.hoTen().toLowerCase(Locale.ROOT).contains(q)
                        || r.cccd().toLowerCase(Locale.ROOT).contains(q)
        ));
    }

    private StudentRow toRow(ApplicantResponse applicant) {
        Map<String, Object> data = ControllerSupport.toMap(applicant);
        String lastName = ControllerSupport.safeString(data.get("lastName"));
        String firstName = ControllerSupport.safeString(data.get("firstName"));
        String fullName = (lastName + " " + firstName).trim();

        return new StudentRow(
                ControllerSupport.safeString(data.get("registrationNumber")),
                fullName,
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.safeString(data.get("dateOfBirth")),
                ControllerSupport.safeString(data.get("email")),
                ControllerSupport.safeString(data.get("phoneNumber"))
        );
    }
}
