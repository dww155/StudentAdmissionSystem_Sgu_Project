package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Integer> {
}
