package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.constant.RoleConstant;
import com.sgu.student_admission_system.dto.Applicant.ApplicantCreationRequest;
import com.sgu.student_admission_system.dto.Applicant.ApplicantResponse;
import com.sgu.student_admission_system.dto.Applicant.ApplicantUpdateRequest;
import com.sgu.student_admission_system.dto.Applicant.ListApplicantCreationRequest;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.Role;
import com.sgu.student_admission_system.entity.User;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.ApplicantMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.RoleRepository;
import com.sgu.student_admission_system.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApplicantService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    ApplicantMapper applicantMapper;
    ApplicantRepository applicantRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    PasswordUtil passwordUtil;

    EntityManager entityManager;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicantResponse createApplicant(ApplicantCreationRequest request) {
        validateUniqueApplicant(request.getCccd(), request.getPhoneNumber());

        Applicant applicantEntity = applicantMapper.toApplicant(request);

        Role userRole = roleRepository
                .findById(RoleConstant.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(request.getCccd())
                .password("matkhausieumanh, khong the bi doan")
                .roles(Set.of(userRole))
                .build();

        applicantEntity.setUser(user);

        return applicantMapper.toApplicantResponse(
                applicantRepository.save(applicantEntity)
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApplicantResponse> createApplicants(ListApplicantCreationRequest request) {
        List<ApplicantCreationRequest> applicantCreationRequests = request.getApplicantCreationRequestList();
        validateUniqueApplicants(applicantCreationRequests);

        if (!roleRepository.existsById(RoleConstant.USER_ROLE)) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        List<ApplicantResponse> applicantResponses = new ArrayList<>(applicantCreationRequests.size());

        for (int i = 0; i < applicantCreationRequests.size(); i++) {
            ApplicantCreationRequest applicantCreationRequest = applicantCreationRequests.get(i);
            Role userRoleRef = entityManager.getReference(Role.class, RoleConstant.USER_ROLE);

            User user = User.builder()
                    .username(applicantCreationRequest.getCccd())
                    .password("matkhausieumanh, khong the bi doan")
                    .roles(Set.of(userRoleRef))
                    .build();

            Applicant applicant = applicantMapper.toApplicant(applicantCreationRequest);
            applicant.setUser(user);

            Applicant savedApplicant = applicantRepository.save(applicant);
            applicantResponses.add(applicantMapper.toApplicantResponse(savedApplicant));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (!applicantCreationRequests.isEmpty() && applicantCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return applicantResponses;
    }

    public ApplicantResponse getApplicant(Integer id) {
        return applicantMapper.toApplicantResponse(
                applicantRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND))
        );
    }

    public List<ApplicantResponse> getAllApplicants() {
        return applicantRepository.findAll()
                .stream()
                .map(applicantMapper::toApplicantResponse)
                .toList();
    }

    public Page<ApplicantResponse> getApplicantsPaginated(Pageable pageable) {
        return applicantRepository.findAll(pageable)
                .map(applicantMapper::toApplicantResponse);
    }

    @Transactional
    public ApplicantResponse updateApplicant(Integer id, ApplicantUpdateRequest request) {
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));

        applicantMapper.updateApplicant(applicant, request);

        return applicantMapper.toApplicantResponse(
                applicantRepository.save(applicant)
        );
    }

    @Transactional
    public void deleteApplicant(Integer id) {
        Applicant applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));

        applicantRepository.delete(applicant);
    }

    private void validateUniqueApplicant(String cccd, String phoneNumber) {
        if (applicantRepository.existsByCccd(cccd)) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (applicantRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void validateUniqueApplicants(List<ApplicantCreationRequest> applicantCreationRequests) {
        Set<String> cccds = new HashSet<>();
        Set<String> phoneNumbers = new HashSet<>();

        for (ApplicantCreationRequest applicantCreationRequest : applicantCreationRequests) {
            if (!cccds.add(applicantCreationRequest.getCccd())) {
                throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
            }

            if (!phoneNumbers.add(applicantCreationRequest.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
        }

        if (applicantRepository.existsByCccdIn(cccds)) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (applicantRepository.existsByPhoneNumberIn(phoneNumbers)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void flushAndClear() {
        applicantRepository.flush();
        entityManager.clear();
    }
}
