package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.Major.MajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.ListMajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorResponse;
import com.sgu.student_admission_system.dto.Major.MajorUpdateRequest;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.entity.SubjectCombination;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.MajorMapper;
import com.sgu.student_admission_system.repository.MajorRepository;
import com.sgu.student_admission_system.repository.SubjectCombinationRepository;
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
public class MajorService {

    MajorRepository majorRepository;
    MajorMapper majorMapper;

    SubjectCombinationRepository subjectCombinationRepository;

    @Transactional
    public MajorResponse createMajor(MajorCreationRequest request) {
        Major major = majorMapper.toMajor(request);

        SubjectCombination subjectCombination = subjectCombinationRepository.findByCode(request.getBaseCombination())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

        major.setBaseCombination(subjectCombination);

        return majorMapper.toMajorResponse(
                majorRepository.save(major)
        );
    }

    @Transactional
    public List<MajorResponse> createMajors(ListMajorCreationRequest request) {
        List<MajorCreationRequest> majorCreationRequests = request.getMajorCreationRequestList();

        List<Major> majors = majorCreationRequests
                .stream()
                .map(majorCreationRequest -> {
                    Major major = majorMapper.toMajor(majorCreationRequest);

                    SubjectCombination subjectCombination = subjectCombinationRepository
                            .findByCode(majorCreationRequest.getBaseCombination())
                            .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

                    major.setBaseCombination(subjectCombination);
                    return major;
                })
                .toList();

        List<Major> savedMajors = majorRepository.saveAll(majors);

        return savedMajors
                .stream()
                .map(majorMapper::toMajorResponse)
                .toList();
    }

    public MajorResponse getMajor(Integer id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        return majorMapper.toMajorResponse(major);
    }

    public List<MajorResponse> getAllMajors() {
        return majorRepository.findAll()
                .stream()
                .map(majorMapper::toMajorResponse)
                .toList();
    }

    @Transactional
    public MajorResponse updateMajor(Integer id, MajorUpdateRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        majorMapper.updateMajor(major, request);

        if (request.getBaseCombination() != null) {
            SubjectCombination subjectCombination = subjectCombinationRepository
                    .findByCode(request.getBaseCombination())
                    .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

            major.setBaseCombination(subjectCombination);
        }
        return majorMapper.toMajorResponse(
                majorRepository.save(major)
        );
    }

    @Transactional
    public void deleteMajor(Integer id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        majorRepository.delete(major);
    }
}
