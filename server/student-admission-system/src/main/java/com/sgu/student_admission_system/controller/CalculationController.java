package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.service.CalculationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/calculations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculationController {

    CalculationService calculationService;

    @PostMapping("/applicants/{cccd}")
    public ApiResponse<Void> calculateByApplicant(@PathVariable String cccd) {
        calculationService.calculateByApplicantCccd(cccd);
        return new ApiResponse<>(null, "Calculated admission scores for applicant successfully");
    }

    @PostMapping("/applicants/recalculate-all")
    public ApiResponse<Map<String, Integer>> recalculateAllApplicants() {
        int processedCount = calculationService.calculateAllApplicants();
        return new ApiResponse<>(Map.of("processedCount", processedCount), "Calculated admission scores for all applicants successfully");
    }
}
