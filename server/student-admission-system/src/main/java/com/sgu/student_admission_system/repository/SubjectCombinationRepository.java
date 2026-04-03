package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.SubjectCombination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectCombinationRepository extends JpaRepository<SubjectCombination, Integer> {
    boolean existsByCode(String code);

    Optional<SubjectCombination> findByCode(String code);
}
