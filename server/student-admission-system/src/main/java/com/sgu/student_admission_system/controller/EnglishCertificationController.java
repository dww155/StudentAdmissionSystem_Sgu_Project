package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationCreationRequest;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationResponse;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationUpdateRequest;
import com.sgu.student_admission_system.dto.EnglishCertification.ListEnglishCertificationCreationRequest;
import com.sgu.student_admission_system.service.EnglishCertificationService;
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
@RequestMapping("/english-certifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnglishCertificationController {

    EnglishCertificationService englishCertificationService;

    @PostMapping
    public ApiResponse<EnglishCertificationResponse> createEnglishCertification(
            @RequestBody @Valid EnglishCertificationCreationRequest request
    ) {
        EnglishCertificationResponse response = englishCertificationService.createEnglishCertification(request);
        return new ApiResponse<>(response, "English certification created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<EnglishCertificationResponse>> createEnglishCertifications(
            @RequestBody @Valid ListEnglishCertificationCreationRequest request
    ) {
        List<EnglishCertificationResponse> response = englishCertificationService.createEnglishCertifications(request);
        return new ApiResponse<>(response, "English certifications created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<EnglishCertificationResponse> getEnglishCertification(@PathVariable Long id) {
        EnglishCertificationResponse response = englishCertificationService.getEnglishCertification(id);
        return new ApiResponse<>(response, "Get english certification successfully");
    }

    @GetMapping("/cccd={cccd}")
    public ApiResponse<EnglishCertificationResponse> getEnglishCertificationByCccd(@PathVariable String cccd) {
        EnglishCertificationResponse response = englishCertificationService.getEnglishCertificationByCccd(cccd);
        return new ApiResponse<>(response, "Get english certification successfully");
    }

    @GetMapping
    public ApiResponse<List<EnglishCertificationResponse>> getAllEnglishCertifications() {
        List<EnglishCertificationResponse> response = englishCertificationService.getAllEnglishCertifications();
        return new ApiResponse<>(response, "Get all english certifications successfully");
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<EnglishCertificationResponse>> getEnglishCertificationsPaginated(
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

        Page<EnglishCertificationResponse> response = englishCertificationService.getEnglishCertificationsPaginated(pageable);
        return new ApiResponse<>(response, "Get english certifications paginated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<EnglishCertificationResponse> updateEnglishCertification(
            @PathVariable Long id,
            @RequestBody @Valid EnglishCertificationUpdateRequest request
    ) {
        EnglishCertificationResponse response = englishCertificationService.updateEnglishCertification(id, request);
        return new ApiResponse<>(response, "English certification updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEnglishCertification(@PathVariable Long id) {
        englishCertificationService.deleteEnglishCertification(id);
        return new ApiResponse<>(null, "English certification deleted successfully");
    }
}
