package com.sgu.student_admission_system.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class testapi {
    @GetMapping("/hello")
    public String hello() {
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(grantedAuthority -> grantedAuthority.toString()).toList();

        authorities.forEach(System.out::println);
        return "hello";
    }
}
