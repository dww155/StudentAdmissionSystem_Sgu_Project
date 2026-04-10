package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.SubjectCombination.ListSubjectCombinationCreationRequest;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationCreationRequest;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationUpdateRequest;
import com.sgu.student_admission_system.service.SubjectCombinationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject-combinations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectCombinationController {

    SubjectCombinationService subjectCombinationService;

    @PostMapping
    public ApiResponse<SubjectCombinationResponse> createSubjectCombination(
            @RequestBody @Valid SubjectCombinationCreationRequest request
    ) {
        SubjectCombinationResponse response = subjectCombinationService.createSubjectCombination(request);
        return new ApiResponse<>(response, "Subject combination created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<SubjectCombinationResponse>> createSubjectCombinations(
            @RequestBody @Valid ListSubjectCombinationCreationRequest request
    ) {
        List<SubjectCombinationResponse> response = subjectCombinationService.createSubjectCombinations(request);
        return new ApiResponse<>(response, "Subject combinations created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<SubjectCombinationResponse> getSubjectCombination(@PathVariable Integer id) {
        SubjectCombinationResponse response = subjectCombinationService.getSubjectCombination(id);
        return new ApiResponse<>(response, "Get subject combination successfully");
    }

    @GetMapping
    public ApiResponse<List<SubjectCombinationResponse>> getAllSubjectCombinations() {
        List<SubjectCombinationResponse> response = subjectCombinationService.getAllSubjectCombinations();
        return new ApiResponse<>(response, "Get all subject combinations successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<SubjectCombinationResponse> updateSubjectCombination(
            @PathVariable Integer id,
            @RequestBody @Valid SubjectCombinationUpdateRequest request
    ) {
        SubjectCombinationResponse response = subjectCombinationService.updateSubjectCombination(id, request);
        return new ApiResponse<>(response, "Subject combination updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSubjectCombination(@PathVariable Integer id) {
        subjectCombinationService.deleteSubjectCombination(id);
        return new ApiResponse<>(null, "Subject combination deleted successfully");
    }
}
