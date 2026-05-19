package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.PriorityBonusPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PriorityBonusPointRepository extends JpaRepository<PriorityBonusPoint, Integer> {

    boolean existsByApplicant_Cccd(String cccd);

    Optional<PriorityBonusPoint> findByApplicant_Cccd(String cccd);

    List<PriorityBonusPoint> findAllByApplicant_CccdIn(Collection<String> cccds);
}
