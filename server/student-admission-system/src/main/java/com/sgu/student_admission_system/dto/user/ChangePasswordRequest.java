package com.sgu.student_admission_system.dto.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, max = 100, message = "INVALID_PASSWORD")
    String oldPassword;

    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, max = 100, message = "INVALID_PASSWORD")
    String newPassword;

    @NotBlank(message = "INVALID_PASSWORD")
    @Size(min = 8, max = 100, message = "INVALID_PASSWORD")
    String confirmPassword;

    @AssertTrue(message = "PASSWORD_MISMATCH")
    public boolean isPasswordMatched() {
        if (newPassword == null || confirmPassword == null) {
            return true;
        }
        return newPassword.equals(confirmPassword);
    }
}
