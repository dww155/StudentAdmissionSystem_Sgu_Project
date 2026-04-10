package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.Major.ListMajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorResponse;
import com.sgu.student_admission_system.dto.Major.MajorUpdateRequest;
import com.sgu.student_admission_system.service.MajorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {

    MajorService majorService;

    @PostMapping
    public ApiResponse<MajorResponse> createMajor(@RequestBody @Valid MajorCreationRequest request) {
        MajorResponse response = majorService.createMajor(request);
        return new ApiResponse<>(response, "Major created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<MajorResponse>> createMajors(@RequestBody @Valid ListMajorCreationRequest request) {
        List<MajorResponse> response = majorService.createMajors(request);
        return new ApiResponse<>(response, "Majors created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<MajorResponse> getMajor(@PathVariable Integer id) {
        MajorResponse response = majorService.getMajor(id);
        return new ApiResponse<>(response, "Get major successfully");
    }

    @GetMapping
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        List<MajorResponse> response = majorService.getAllMajors();
        return new ApiResponse<>(response, "Get all majors successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<MajorResponse> updateMajor(
            @PathVariable Integer id,
            @RequestBody @Valid MajorUpdateRequest request
    ) {
        MajorResponse response = majorService.updateMajor(id, request);
        return new ApiResponse<>(response, "Major updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMajor(@PathVariable Integer id) {
        majorService.deleteMajor(id);
        return new ApiResponse<>(null, "Major deleted successfully");
    }
}
