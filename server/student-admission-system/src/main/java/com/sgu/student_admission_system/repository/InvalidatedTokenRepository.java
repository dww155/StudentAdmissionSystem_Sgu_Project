package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
