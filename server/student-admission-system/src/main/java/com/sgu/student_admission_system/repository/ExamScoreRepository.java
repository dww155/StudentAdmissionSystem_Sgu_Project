package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.ExamScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamScoreRepository extends JpaRepository<ExamScore, Integer> {
}
