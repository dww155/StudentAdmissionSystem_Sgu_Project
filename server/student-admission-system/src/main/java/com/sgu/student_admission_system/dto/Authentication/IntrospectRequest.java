package com.sgu.student_admission_system.dto.Authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRequest {
    @NotBlank(message = "INVALID_TOKEN")
    String token;
}
