package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.ListAdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceUpdateRequest;
import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.service.AdmissionPreferenceService;
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
import java.util.Map;

@RestController
@RequestMapping("/admission-preferences")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdmissionPreferenceController {

    AdmissionPreferenceService admissionPreferenceService;

    @PostMapping
    public ApiResponse<AdmissionPreferenceResponse> createAdmissionPreference(
            @RequestBody @Valid AdmissionPreferenceCreationRequest request
    ) {
        AdmissionPreferenceResponse response = admissionPreferenceService.createAdmissionPreference(request);
        return new ApiResponse<>(response, "Admission preference created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<AdmissionPreferenceResponse>> createAdmissionPreferences(
            @RequestBody @Valid ListAdmissionPreferenceCreationRequest request
    ) {
        List<AdmissionPreferenceResponse> response = admissionPreferenceService.createAdmissionPreferences(request);
        return new ApiResponse<>(response, "Admission preferences created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<AdmissionPreferenceResponse> getAdmissionPreference(@PathVariable Integer id) {
        AdmissionPreferenceResponse response = admissionPreferenceService.getAdmissionPreference(id);
        return new ApiResponse<>(response, "Get admission preference successfully");
    }

    @GetMapping("/cccd={cccd}")
    public ApiResponse<List<AdmissionPreferenceResponse>> getAdmissionPreferenceByCccd(@PathVariable("cccd") String cccd) {
        List<AdmissionPreferenceResponse> response = admissionPreferenceService.getAdmissionPreferencesByCccd(cccd);
        return new ApiResponse<>(response, "Get admission preference successfully");
    }


    @GetMapping
    public ApiResponse<List<AdmissionPreferenceResponse>> getAllAdmissionPreferences() {
        List<AdmissionPreferenceResponse> response = admissionPreferenceService.getAllAdmissionPreferences();
        return new ApiResponse<>(response, "Get all admission preferences successfully");
    }

    @GetMapping("/count")
    public ApiResponse<Map<String, Long>> getAdmissionPreferenceCount() {
        long count = admissionPreferenceService.getAdmissionPreferenceCount();
        return new ApiResponse<>(Map.of("count", count), "Get admission preference count successfully");
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<AdmissionPreferenceResponse>> getAdmissionPreferencesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        String resolvedDirection = (sortDir != null && !sortDir.isBlank()) ? sortDir : direction;

        Sort sort;
        if ("desc".equalsIgnoreCase(resolvedDirection)) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AdmissionPreferenceResponse> response = admissionPreferenceService
                .getAdmissionPreferencesPaginated(pageable);
        return new ApiResponse<>(response, "Get admission preferences paginated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<AdmissionPreferenceResponse> updateAdmissionPreference(
            @PathVariable Integer id,
            @RequestBody @Valid AdmissionPreferenceUpdateRequest request
    ) {
        AdmissionPreferenceResponse response = admissionPreferenceService.updateAdmissionPreference(id, request);
        return new ApiResponse<>(response, "Admission preference updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAdmissionPreference(@PathVariable Integer id) {
        admissionPreferenceService.deleteAdmissionPreference(id);
        return new ApiResponse<>(null, "Admission preference deleted successfully");
    }
}
