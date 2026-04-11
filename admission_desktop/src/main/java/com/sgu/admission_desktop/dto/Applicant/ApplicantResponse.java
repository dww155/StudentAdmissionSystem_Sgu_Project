package com.sgu.admission_desktop.dto.Applicant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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