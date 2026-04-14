package com.sgu.student_admission_system.service;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApplicantService {

    ApplicantMapper applicantMapper;
    ApplicantRepository applicantRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    PasswordUtil passwordUtil;

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
                .password(passwordEncoder.encode(PasswordUtil.generatePassword()))
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
//        validateUniqueApplicants(applicantCreationRequests);

        Role userRole = roleRepository
                .findById(RoleConstant.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        List<Applicant> applicants = applicantCreationRequests
                .stream()
                .map(
                        applicantCreationRequest -> {
                            User user = User.builder()
                                    .username(applicantCreationRequest.getCccd())
                                    .password(passwordEncoder.encode(PasswordUtil.generatePassword()))
                                    .roles(Set.of(userRole))
                                    .build();

                            Applicant applicant = applicantMapper.toApplicant(applicantCreationRequest);

                            applicant.setUser(user);

                            return applicant;
                        }
                )
                .toList();

        List<Applicant> savedApplicants = applicantRepository.saveAll(applicants);

        return savedApplicants.stream().map(applicantMapper::toApplicantResponse).toList();
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
}
