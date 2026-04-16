package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Major;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Integer> {
    boolean existsByMajorCode(String majorCode);

    @EntityGraph(attributePaths = "baseCombination")
    List<Major> findAllByMajorCodeIn(Collection<String> majorCodes);

    @EntityGraph(attributePaths = "baseCombination")
    Optional<Major> findByMajorCode(String majorCode);
}
