package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceUpdateRequest;
import com.sgu.student_admission_system.entity.AdmissionPreference;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.AdmissionPreferenceMapper;
import com.sgu.student_admission_system.repository.AdmissionPreferenceRepository;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.MajorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdmissionPreferenceService {

    AdmissionPreferenceRepository admissionPreferenceRepository;
    AdmissionPreferenceMapper admissionPreferenceMapper;
    ApplicantRepository applicantRepository;
    MajorRepository majorRepository;

    @Transactional
    public AdmissionPreferenceResponse createAdmissionPreference(AdmissionPreferenceCreationRequest request) {
        validateApplicantExists(request.getCccd());
        validateMajorExists(request.getMajorCode());

        validateDuplicatePriority(request.getCccd(), request.getPriorityOrder(), null);

        AdmissionPreference admissionPreference = admissionPreferenceMapper.toAdmissionPreference(request);

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(
                admissionPreferenceRepository.save(admissionPreference)
        );
    }

    public AdmissionPreferenceResponse getAdmissionPreference(Integer id) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(admissionPreference);
    }

    public List<AdmissionPreferenceResponse> getAllAdmissionPreferences() {
        return admissionPreferenceRepository.findAll()
                .stream()
                .map(admissionPreferenceMapper::toAdmissionPreferenceResponse)
                .toList();
    }

    @Transactional
    public AdmissionPreferenceResponse updateAdmissionPreference(Integer id, AdmissionPreferenceUpdateRequest request) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        validateMajorExists(request.getMajorCode());

        validateDuplicatePriority(admissionPreference.getCccd(), request.getPriorityOrder(), id);

        admissionPreferenceMapper.updateAdmissionPreference(admissionPreference, request);

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(
                admissionPreferenceRepository.save(admissionPreference)
        );
    }

    @Transactional
    public void deleteAdmissionPreference(Integer id) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        admissionPreferenceRepository.delete(admissionPreference);
    }

    private void validateApplicantExists(String cccd) {
        applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private void validateMajorExists(String majorCode) {
        majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private void validateDuplicatePriority(String cccd, Integer priorityOrder, Integer preferenceId) {
        boolean duplicated = preferenceId == null
                ? admissionPreferenceRepository.existsByCccdAndPriorityOrder(cccd, priorityOrder)
                : admissionPreferenceRepository.existsByCccdAndPriorityOrderAndIdNot(cccd, priorityOrder, preferenceId);

        if (duplicated) {
            throw new AppException(ErrorCode.DUPLICATE_ADMISSION_PREFERENCE_PRIORITY);
        }
    }
}
