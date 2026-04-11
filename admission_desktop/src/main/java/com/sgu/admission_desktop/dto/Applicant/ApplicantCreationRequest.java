package com.sgu.admission_desktop.dto.Applicant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicantCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    @NotBlank(message = "INVALID_REGISTRATION_NUMBER")
    String registrationNumber;

    @NotBlank(message = "INVALID_LAST_NAME")
    String lastName;

    @NotBlank(message = "INVALID_FIRST_NAME")
    String firstName;

    @NotNull(message = "INVALID_DATE_OF_BIRTH")
    LocalDate dateOfBirth;

    @NotBlank(message = "INVALID_PHONE_NUMBER")
    String phoneNumber;

    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    String email;

    @NotNull(message = "INVALID_GENDER")
    String gender;

    @NotBlank(message = "INVALID_BIRTH_PLACE")
    String birthPlace;

    @NotBlank(message = "INVALID_APPLICANT_TYPE")
    String applicantType;

    @NotBlank(message = "INVALID_REGION")
    String region;
}
