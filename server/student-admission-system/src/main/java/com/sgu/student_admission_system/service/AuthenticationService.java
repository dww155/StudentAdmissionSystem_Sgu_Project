package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.Authentication.IntrospectRequest;
import com.sgu.student_admission_system.dto.Authentication.IntrospectResponse;
import com.sgu.student_admission_system.dto.Authentication.LoginRequest;
import com.sgu.student_admission_system.dto.Authentication.LoginResponse;
import com.sgu.student_admission_system.entity.User;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.repository.UserRepository;
import com.sgu.student_admission_system.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    PasswordEncoder passwordEncoder;

    UserRepository userRepository;

    JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (isAuthenticated) {
            String token = jwtUtil.tokenGenerator(user);

            return new LoginResponse(true, token);
        }
        return LoginResponse.builder()
                .authenticated(false)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        String token = request.getToken();

        boolean isValid;
        try {
            jwtUtil.verifyToken(token, false);
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
}
