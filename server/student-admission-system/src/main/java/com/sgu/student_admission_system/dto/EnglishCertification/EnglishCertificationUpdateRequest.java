package com.sgu.student_admission_system.dto.EnglishCertification;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnglishCertificationUpdateRequest {

    @NotBlank(message = "INVALID_CERTIFICATION_NAME")
    String certificationName;

    @NotNull(message = "INVALID_CERTIFICATION_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_CERTIFICATION_SCORE")
    BigDecimal certificationScore;

    @DecimalMin(value = "0.0", message = "INVALID_ENGLISH_CONVERSION_SCORE")
    BigDecimal conversionScore;

    @DecimalMin(value = "0.0", message = "INVALID_ENGLISH_BONUS_SCORE")
    BigDecimal bonusScore;
}
