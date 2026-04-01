package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.Major.MajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorResponse;
import com.sgu.student_admission_system.dto.Major.MajorUpdateRequest;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.MajorMapper;
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
public class MajorService {

    MajorRepository majorRepository;
    MajorMapper majorMapper;

    @Transactional
    public MajorResponse createMajor(MajorCreationRequest request) {
        Major major = majorMapper.toMajor(request);

        return majorMapper.toMajorResponse(
                majorRepository.save(major)
        );
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
