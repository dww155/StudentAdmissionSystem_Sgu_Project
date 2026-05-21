package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.service.AdmissionPreferenceService;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.service.MajorService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalApplicantsLabel;
    @FXML
    private Label totalMajorsLabel;
    @FXML
    private Label totalWishesLabel;
    @FXML
    private Label recalcStatusLabel;
    @FXML
    private Button recalculateButton;

    private final ApplicantService applicantService = new ApplicantService();
    private final MajorService majorService = new MajorService();
    private final AdmissionPreferenceService admissionPreferenceService = new AdmissionPreferenceService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardStats();
    }

    @FXML
    private void onRefreshStats() {
        loadDashboardStats();
    }

    @FXML
    private void onRecalculateAllApplicants() {
        recalculateButton.setDisable(true);
        recalcStatusLabel.setText("Dang tinh lai diem cho tat ca thi sinh...");

        Task<ApiResponse<Map<String, Integer>>> task = new Task<>() {
            @Override
            protected ApiResponse<Map<String, Integer>> call() {
                return applicantService.recalculateAll();
            }
        };

        task.setOnSucceeded(event -> {
            recalculateButton.setDisable(false);
            ApiResponse<Map<String, Integer>> response = task.getValue();
            int processedCount = 0;
            if (response != null && response.getData() != null) {
                processedCount = response.getData().getOrDefault("processedCount", 0);
            }
            String message = response == null ? "Tinh diem thanh cong." : response.getMessage();
            recalcStatusLabel.setText(message + " So ho so da xu ly: " + processedCount);
            loadDashboardStats();
        });

        task.setOnFailed(event -> {
            recalculateButton.setDisable(false);
            recalcStatusLabel.setText("Tinh lai diem that bai: " + ControllerSupport.extractMessage(task.getException()));
        });

        Thread thread = new Thread(task, "dashboard-recalculate-all-task");
        thread.setDaemon(true);
        thread.start();
    }

    private void loadDashboardStats() {
        totalApplicantsLabel.setText("...");
        totalMajorsLabel.setText("...");
        totalWishesLabel.setText("...");

        Task<DashboardStats> task = new Task<>() {
            @Override
            protected DashboardStats call() {
                int applicants = extractCount(applicantService.count());
                int majors = extractCount(majorService.count());
                int wishes = extractCount(admissionPreferenceService.count());
                return new DashboardStats(applicants, majors, wishes);
            }
        };

        task.setOnSucceeded(event -> {
            DashboardStats stats = task.getValue();
            totalApplicantsLabel.setText(String.valueOf(stats.totalApplicants()));
            totalMajorsLabel.setText(String.valueOf(stats.totalMajors()));
            totalWishesLabel.setText(String.valueOf(stats.totalWishes()));
        });

        task.setOnFailed(event -> {
            totalApplicantsLabel.setText("-");
            totalMajorsLabel.setText("-");
            totalWishesLabel.setText("-");
            recalcStatusLabel.setText("Khong tai duoc thong ke: " + ControllerSupport.extractMessage(task.getException()));
        });

        Thread thread = new Thread(task, "dashboard-load-stats-task");
        thread.setDaemon(true);
        thread.start();
    }

    private int extractCount(ApiResponse<Map<String, Long>> response) {
        if (response == null || response.getData() == null) {
            return 0;
        }
        Long count = response.getData().get("count");
        return count == null ? 0 : count.intValue();
    }

    private record DashboardStats(int totalApplicants, int totalMajors, int totalWishes) {
    }
}
