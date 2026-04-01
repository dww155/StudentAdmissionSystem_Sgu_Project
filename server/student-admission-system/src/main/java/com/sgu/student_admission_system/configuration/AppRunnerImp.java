package com.sgu.student_admission_system.configuration;

import com.sgu.student_admission_system.constant.RoleConstant;
import com.sgu.student_admission_system.entity.Role;
import com.sgu.student_admission_system.entity.User;
import com.sgu.student_admission_system.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppRunnerImp implements ApplicationRunner {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("initializing server...");
        if (!userRepository.existsByUsername(RoleConstant.ADMIN_NAME)) {
            // create roles
            Role adminRole = Role.builder()
                    .name(RoleConstant.ADMIN_ROLE)
                    .description("this is admin role")
                    .build();

            Role userRole = Role.builder()
                    .name(RoleConstant.USER_ROLE)
                    .description("this is user role")
                    .build();

            // create admin
            User admin = User.builder()
                    .username(RoleConstant.ADMIN_NAME)
                    .password(passwordEncoder.encode("Asdf1234!"))
                    .roles(Set.of(adminRole, userRole))
                    .build();

            userRepository.save(admin);
            log.info("successfully created an admin user with name: \"admin\", password: \"Asdf1234!\" ^3^");
        }
        log.info("initialize server successfully ^3^");
        log.info("swagger: localhost:8080/sas/swagger-ui/index.html");
    }
}
