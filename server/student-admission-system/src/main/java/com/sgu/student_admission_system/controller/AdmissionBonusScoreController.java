package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreUpdateRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.ListAdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.service.AdmissionBonusScoreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admission-bonus-scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdmissionBonusScoreController {

    AdmissionBonusScoreService admissionBonusScoreService;

    @PostMapping
    public ApiResponse<AdmissionBonusScoreResponse> createAdmissionBonusScore(
            @RequestBody @Valid AdmissionBonusScoreCreationRequest request
    ) {
        AdmissionBonusScoreResponse response = admissionBonusScoreService.createAdmissionBonusScore(request);
        return new ApiResponse<>(response, "Admission bonus score created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<AdmissionBonusScoreResponse>> createAdmissionBonusScores(
            @RequestBody @Valid ListAdmissionBonusScoreCreationRequest request
    ) {
        List<AdmissionBonusScoreResponse> response = admissionBonusScoreService.createAdmissionBonusScores(request);
        return new ApiResponse<>(response, "Admission bonus scores created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<AdmissionBonusScoreResponse> getAdmissionBonusScore(@PathVariable Integer id) {
        AdmissionBonusScoreResponse response = admissionBonusScoreService.getAdmissionBonusScore(id);
        return new ApiResponse<>(response, "Get admission bonus score successfully");
    }

    @GetMapping
    public ApiResponse<List<AdmissionBonusScoreResponse>> getAllAdmissionBonusScores() {
        List<AdmissionBonusScoreResponse> response = admissionBonusScoreService.getAllAdmissionBonusScores();
        return new ApiResponse<>(response, "Get all admission bonus scores successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<AdmissionBonusScoreResponse> updateAdmissionBonusScore(
            @PathVariable Integer id,
            @RequestBody @Valid AdmissionBonusScoreUpdateRequest request
    ) {
        AdmissionBonusScoreResponse response = admissionBonusScoreService.updateAdmissionBonusScore(id, request);
        return new ApiResponse<>(response, "Admission bonus score updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAdmissionBonusScore(@PathVariable Integer id) {
        admissionBonusScoreService.deleteAdmissionBonusScore(id);
        return new ApiResponse<>(null, "Admission bonus score deleted successfully");
    }
}
