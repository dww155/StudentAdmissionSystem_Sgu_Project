package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.VsatResult.ListVsatResultCreationRequest;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultCreationRequest;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultResponse;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultUpdateRequest;
import com.sgu.student_admission_system.service.VsatResultService;
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
@RequestMapping("/vsat-results")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VsatResultController {

    VsatResultService vsatResultService;

    @PostMapping
    public ApiResponse<VsatResultResponse> createVsatResult(
            @RequestBody @Valid VsatResultCreationRequest request
    ) {
        VsatResultResponse response = vsatResultService.createVsatResult(request);
        return new ApiResponse<>(response, "Vsat result created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<VsatResultResponse>> createVsatResults(
            @RequestBody @Valid ListVsatResultCreationRequest request
    ) {
        List<VsatResultResponse> response = vsatResultService.createVsatResults(request);
        return new ApiResponse<>(response, "Vsat results created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<VsatResultResponse> getVsatResult(@PathVariable Long id) {
        VsatResultResponse response = vsatResultService.getVsatResult(id);
        return new ApiResponse<>(response, "Get vsat result successfully");
    }

    @GetMapping("/cccd={cccd}")
    public ApiResponse<List<VsatResultResponse>> getVsatResultByCccd(@PathVariable String cccd) {
        List<VsatResultResponse> response = vsatResultService.getVsatResultByCccd(cccd);
        return new ApiResponse<>(response, "Get vsat result successfully");
    }

    @GetMapping
    public ApiResponse<List<VsatResultResponse>> getAllVsatResults() {
        List<VsatResultResponse> response = vsatResultService.getAllVsatResults();
        return new ApiResponse<>(response, "Get all vsat results successfully");
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<VsatResultResponse>> getVsatResultsPaginated(
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

        Page<VsatResultResponse> response = vsatResultService.getVsatResultsPaginated(pageable);
        return new ApiResponse<>(response, "Get vsat results paginated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<VsatResultResponse> updateVsatResult(
            @PathVariable Long id,
            @RequestBody @Valid VsatResultUpdateRequest request
    ) {
        VsatResultResponse response = vsatResultService.updateVsatResult(id, request);
        return new ApiResponse<>(response, "Vsat result updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVsatResult(@PathVariable Long id) {
        vsatResultService.deleteVsatResult(id);
        return new ApiResponse<>(null, "Vsat result deleted successfully");
    }
}
