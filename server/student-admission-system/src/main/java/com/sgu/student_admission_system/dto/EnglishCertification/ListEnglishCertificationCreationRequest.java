package com.sgu.student_admission_system.dto.EnglishCertification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListEnglishCertificationCreationRequest {

    @NotEmpty(message = "INVALID_ENGLISH_CERTIFICATION_LIST")
    @Valid
    List<EnglishCertificationCreationRequest> englishCertificationCreationRequestList;
}
