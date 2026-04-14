package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.Applicant.ApplicantCreationRequest;
import com.sgu.student_admission_system.dto.Applicant.ApplicantResponse;
import com.sgu.student_admission_system.dto.Applicant.ApplicantUpdateRequest;
import com.sgu.student_admission_system.dto.Applicant.ListApplicantCreationRequest;
import com.sgu.student_admission_system.service.ApplicantService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicant")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicantController {

    ApplicantService applicantService;

    @PostMapping
    public ApiResponse<ApplicantResponse> createApplicant(@RequestBody @Valid ApplicantCreationRequest request) {

        ApplicantResponse response = applicantService.createApplicant(request);
        return new ApiResponse<>(response, "Applicant created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<ApplicantResponse>> createApplicants(@RequestBody @Valid ListApplicantCreationRequest request) {

        List<ApplicantResponse> response = applicantService.createApplicants(request);
        return new ApiResponse<>(response, "Applicant created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicantResponse> getApplicant(@PathVariable Integer id) {
        ApplicantResponse response = applicantService.getApplicant(id);
        return new ApiResponse<>(response, "Get applicant successfully");
    }

    @GetMapping
    public ApiResponse<List<ApplicantResponse>> getAllApplicants() {
        List<ApplicantResponse> response = applicantService.getAllApplicants();
        return new ApiResponse<>(response, "Get all applicants successfully");
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<ApplicantResponse>> getApplicantsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ApplicantResponse> response = applicantService.getApplicantsPaginated(pageable);
        return new ApiResponse<>(response, "Get applicants paginated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicantResponse> updateApplicant(
            @PathVariable Integer id,
            @RequestBody @Valid ApplicantUpdateRequest request) {

        ApplicantResponse response = applicantService.updateApplicant(id, request);
        return new ApiResponse<>(response, "Applicant updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteApplicant(@PathVariable Integer id) {
        applicantService.deleteApplicant(id);
        return new ApiResponse<>(null, "Applicant deleted successfully");
    }
}
