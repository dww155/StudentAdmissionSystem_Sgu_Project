package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreResponse;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreUpdateRequest;
import com.sgu.student_admission_system.service.ExamScoreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamScoreController {

    ExamScoreService examScoreService;

    @PostMapping
    public ApiResponse<ExamScoreResponse> createExamScore(@RequestBody @Valid ExamScoreCreationRequest request) {
        ExamScoreResponse response = examScoreService.createExamScore(request);
        return new ApiResponse<>(response, "Exam score created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<ExamScoreResponse> getExamScore(@PathVariable Integer id) {
        ExamScoreResponse response = examScoreService.getExamScore(id);
        return new ApiResponse<>(response, "Get exam score successfully");
    }

    @GetMapping
    public ApiResponse<List<ExamScoreResponse>> getAllExamScores() {
        List<ExamScoreResponse> response = examScoreService.getAllExamScores();
        return new ApiResponse<>(response, "Get all exam scores successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ExamScoreResponse> updateExamScore(
            @PathVariable Integer id,
            @RequestBody @Valid ExamScoreUpdateRequest request
    ) {
        ExamScoreResponse response = examScoreService.updateExamScore(id, request);
        return new ApiResponse<>(response, "Exam score updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExamScore(@PathVariable Integer id) {
        examScoreService.deleteExamScore(id);
        return new ApiResponse<>(null, "Exam score deleted successfully");
    }
}
