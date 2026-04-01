package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.authentication.IntrospectRequest;
import com.sgu.student_admission_system.dto.authentication.IntrospectResponse;
import com.sgu.student_admission_system.dto.authentication.LoginRequest;
import com.sgu.student_admission_system.dto.authentication.LoginResponse;
import com.sgu.student_admission_system.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);

        return new ApiResponse(response, "Login successfully");
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        IntrospectResponse response = authenticationService.introspect(request);

        return new ApiResponse(response, "Introspect successfully");
    }
}
