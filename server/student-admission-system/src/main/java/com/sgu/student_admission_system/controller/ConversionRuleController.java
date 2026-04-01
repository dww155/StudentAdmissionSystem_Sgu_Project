package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleUpdateRequest;
import com.sgu.student_admission_system.service.ConversionRuleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversion-rules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversionRuleController {

    ConversionRuleService conversionRuleService;

    @PostMapping
    public ApiResponse<ConversionRuleResponse> createConversionRule(
            @RequestBody @Valid ConversionRuleCreationRequest request
    ) {
        ConversionRuleResponse response = conversionRuleService.createConversionRule(request);
        return new ApiResponse<>(response, "Conversion rule created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ConversionRuleResponse> getConversionRule(@PathVariable Integer id) {
        ConversionRuleResponse response = conversionRuleService.getConversionRule(id);
        return new ApiResponse<>(response, "Get conversion rule successfully");
    }

    @GetMapping
    public ApiResponse<List<ConversionRuleResponse>> getAllConversionRules() {
        List<ConversionRuleResponse> response = conversionRuleService.getAllConversionRules();
        return new ApiResponse<>(response, "Get all conversion rules successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ConversionRuleResponse> updateConversionRule(
            @PathVariable Integer id,
            @RequestBody @Valid ConversionRuleUpdateRequest request
    ) {
        ConversionRuleResponse response = conversionRuleService.updateConversionRule(id, request);
        return new ApiResponse<>(response, "Conversion rule updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteConversionRule(@PathVariable Integer id) {
        conversionRuleService.deleteConversionRule(id);
        return new ApiResponse<>(null, "Conversion rule deleted successfully");
    }
}
