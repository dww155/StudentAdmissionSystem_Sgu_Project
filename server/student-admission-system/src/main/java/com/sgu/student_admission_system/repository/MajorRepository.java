package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    boolean existsByMajorCode(String majorCode);

    List<Major> findAllByMajorCode(Iterable<String> majorCode);

    Optional<Major> findByMajorCode(String majorCode);
}
