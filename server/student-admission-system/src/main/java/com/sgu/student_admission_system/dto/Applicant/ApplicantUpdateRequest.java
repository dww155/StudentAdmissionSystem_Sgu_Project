package com.sgu.student_admission_system.dto.Applicant;

import com.sgu.student_admission_system.validation.ValidGender;
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
public class ApplicantUpdateRequest {

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
    @ValidGender
    String gender;

    @NotBlank(message = "INVALID_BIRTH_PLACE")
    String birthPlace;

    @NotBlank(message = "INVALID_APPLICANT_TYPE")
    String applicantType;

    @NotBlank(message = "INVALID_REGION")
    String region;
}
