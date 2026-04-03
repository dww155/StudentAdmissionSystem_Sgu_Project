package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreUpdateRequest;
import com.sgu.student_admission_system.entity.AdmissionBonusScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdmissionBonusScoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "subjectCombination", ignore = true)
    AdmissionBonusScore toAdmissionBonusScore(AdmissionBonusScoreCreationRequest request);

    @Mapping(target = "cccd", source = "applicant.cccd")
    @Mapping(target = "majorCode", source = "major.majorCode")
    @Mapping(target = "subjectCombinationCode", source = "subjectCombination.code")
    AdmissionBonusScoreResponse toAdmissionBonusScoreResponse(AdmissionBonusScore admissionBonusScore);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "subjectCombination", ignore = true)
    void updateAdmissionBonusScore(@MappingTarget AdmissionBonusScore admissionBonusScore, AdmissionBonusScoreUpdateRequest request);
}
