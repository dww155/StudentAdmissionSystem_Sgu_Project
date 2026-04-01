package com.sgu.student_admission_system.dto.Applicant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicantResponse {

    Integer id;

    String cccd;
    String registrationNumber;

    String lastName;
    String firstName;

    LocalDate dateOfBirth;

    String phoneNumber;
    String email;

    String gender;
    String birthPlace;

    String applicantType;
    String region;

    UUID userId;

    LocalDate updatedAt;
}