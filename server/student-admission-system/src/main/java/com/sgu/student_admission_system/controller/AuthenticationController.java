package com.sgu.student_admission_system.controller;

import com.sgu.student_admission_system.dto.ApiResponse;
import com.sgu.student_admission_system.dto.Authentication.IntrospectRequest;
import com.sgu.student_admission_system.dto.Authentication.IntrospectResponse;
import com.sgu.student_admission_system.dto.Authentication.LoginRequest;
import com.sgu.student_admission_system.dto.Authentication.LoginResponse;
import com.sgu.student_admission_system.service.AuthenticationService;
import jakarta.validation.Valid;
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
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authenticationService.login(request);

        return new ApiResponse(response, "Login successfully");
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) {
        IntrospectResponse response = authenticationService.introspect(request);

        return new ApiResponse(response, "Introspect successfully");
    }
}
