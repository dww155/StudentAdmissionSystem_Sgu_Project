package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.VsatResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VsatResultRepository extends JpaRepository<VsatResult, Long> {

    boolean existsByApplicant_Cccd(String cccd);

    List<VsatResult> findAllByApplicant_Cccd(String cccd);

    List<VsatResult> findAllByApplicant_CccdIn(Collection<String> cccds);
}
