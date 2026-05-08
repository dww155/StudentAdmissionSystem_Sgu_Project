package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.EnglishCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EnglishCertificationRepository extends JpaRepository<EnglishCertification, Long> {

    boolean existsByApplicant_Cccd(String cccd);

    Optional<EnglishCertification> findByApplicant_Cccd(String cccd);

    List<EnglishCertification> findAllByApplicant_CccdIn(Collection<String> cccds);
}
