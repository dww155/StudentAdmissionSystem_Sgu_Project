package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceUpdateRequest;
import com.sgu.student_admission_system.entity.AdmissionPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdmissionPreferenceMapper {

    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "major", ignore = true)
    AdmissionPreference toAdmissionPreference(AdmissionPreferenceCreationRequest request);

    @Mapping(target = "cccd", source = "applicant.cccd")
    @Mapping(target = "majorCode", source = "major.majorCode")
    AdmissionPreferenceResponse toAdmissionPreferenceResponse(AdmissionPreference admissionPreference);

    @Mapping(target = "applicant", ignore = true)
    @Mapping(target = "major", ignore = true)
    void updateAdmissionPreference(@MappingTarget AdmissionPreference admissionPreference, AdmissionPreferenceUpdateRequest request);
}
