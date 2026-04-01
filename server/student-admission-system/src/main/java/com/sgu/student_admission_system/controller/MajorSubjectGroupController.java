package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupUpdateRequest;
import com.sgu.student_admission_system.service.MajorSubjectGroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major-subject-groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorSubjectGroupController {

    MajorSubjectGroupService majorSubjectGroupService;

    @PostMapping
    public ApiResponse<MajorSubjectGroupResponse> createMajorSubjectGroup(
            @RequestBody @Valid MajorSubjectGroupCreationRequest request
    ) {
        MajorSubjectGroupResponse response = majorSubjectGroupService.createMajorSubjectGroup(request);
        return new ApiResponse<>(response, "Major subject group created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<MajorSubjectGroupResponse> getMajorSubjectGroup(@PathVariable Integer id) {
        MajorSubjectGroupResponse response = majorSubjectGroupService.getMajorSubjectGroup(id);
        return new ApiResponse<>(response, "Get major subject group successfully");
    }

    @GetMapping
    public ApiResponse<List<MajorSubjectGroupResponse>> getAllMajorSubjectGroups() {
        List<MajorSubjectGroupResponse> response = majorSubjectGroupService.getAllMajorSubjectGroups();
        return new ApiResponse<>(response, "Get all major subject groups successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<MajorSubjectGroupResponse> updateMajorSubjectGroup(
            @PathVariable Integer id,
            @RequestBody @Valid MajorSubjectGroupUpdateRequest request
    ) {
        MajorSubjectGroupResponse response = majorSubjectGroupService.updateMajorSubjectGroup(id, request);
        return new ApiResponse<>(response, "Major subject group updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMajorSubjectGroup(@PathVariable Integer id) {
        majorSubjectGroupService.deleteMajorSubjectGroup(id);
        return new ApiResponse<>(null, "Major subject group deleted successfully");
    }
}
