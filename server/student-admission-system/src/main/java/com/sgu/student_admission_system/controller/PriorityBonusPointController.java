package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.ListPriorityBonusPointCreationRequest;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointCreationRequest;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointResponse;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointUpdateRequest;
import com.sgu.student_admission_system.service.PriorityBonusPointService;
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
@RequestMapping("/priority-bonus-points")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriorityBonusPointController {

    PriorityBonusPointService priorityBonusPointService;

    @PostMapping
    public ApiResponse<PriorityBonusPointResponse> createPriorityBonusPoint(
            @RequestBody @Valid PriorityBonusPointCreationRequest request
    ) {
        PriorityBonusPointResponse response = priorityBonusPointService.createPriorityBonusPoint(request);
        return new ApiResponse<>(response, "Priority bonus point created successfully");
    }

    @PostMapping("/bulk")
    public ApiResponse<List<PriorityBonusPointResponse>> createPriorityBonusPoints(
            @RequestBody @Valid ListPriorityBonusPointCreationRequest request
    ) {
        List<PriorityBonusPointResponse> response = priorityBonusPointService.createPriorityBonusPoints(request);
        return new ApiResponse<>(response, "Priority bonus points created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<PriorityBonusPointResponse> getPriorityBonusPoint(@PathVariable Integer id) {
        PriorityBonusPointResponse response = priorityBonusPointService.getPriorityBonusPoint(id);
        return new ApiResponse<>(response, "Get priority bonus point successfully");
    }

    @GetMapping("/cccd={cccd}")
    public ApiResponse<PriorityBonusPointResponse> getPriorityBonusPointByCccd(@PathVariable String cccd) {
        PriorityBonusPointResponse response = priorityBonusPointService.getPriorityBonusPointByCccd(cccd);
        return new ApiResponse<>(response, "Get priority bonus point successfully");
    }

    @GetMapping
    public ApiResponse<List<PriorityBonusPointResponse>> getAllPriorityBonusPoints() {
        List<PriorityBonusPointResponse> response = priorityBonusPointService.getAllPriorityBonusPoints();
        return new ApiResponse<>(response, "Get all priority bonus points successfully");
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<PriorityBonusPointResponse>> getPriorityBonusPointsPaginated(
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
        Page<PriorityBonusPointResponse> response = priorityBonusPointService.getPriorityBonusPointsPaginated(pageable);

        return new ApiResponse<>(response, "Get priority bonus points paginated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<PriorityBonusPointResponse> updatePriorityBonusPoint(
            @PathVariable Integer id,
            @RequestBody @Valid PriorityBonusPointUpdateRequest request
    ) {
        PriorityBonusPointResponse response = priorityBonusPointService.updatePriorityBonusPoint(id, request);
        return new ApiResponse<>(response, "Priority bonus point updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePriorityBonusPoint(@PathVariable Integer id) {
        priorityBonusPointService.deletePriorityBonusPoint(id);
        return new ApiResponse<>(null, "Priority bonus point deleted successfully");
    }
}
