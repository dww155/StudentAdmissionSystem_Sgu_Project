package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.user.ChangePasswordRequest;
import com.sgu.student_admission_system.entity.User;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (newPassword.equals(confirmPassword))
            throw new AppException(ErrorCode.BAD_REQUEST);

        if (passwordEncoder.matches(oldPassword, admin.getPassword()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        admin.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(admin);
    }
}
