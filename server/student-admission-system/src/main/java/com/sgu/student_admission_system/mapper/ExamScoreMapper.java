package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreResponse;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreUpdateRequest;
import com.sgu.student_admission_system.entity.ExamScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExamScoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    ExamScore toExamScore(ExamScoreCreationRequest request);

    @Mapping(target = "conversionCode", ignore = true)
    @Mapping(target = "standardizedScore", ignore = true)
    @Mapping(target = "cccd", source = "applicant.cccd")
    ExamScoreResponse toExamScoreResponse(ExamScore examScore);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    void updateExamScore(@MappingTarget ExamScore examScore, ExamScoreUpdateRequest request);
}
