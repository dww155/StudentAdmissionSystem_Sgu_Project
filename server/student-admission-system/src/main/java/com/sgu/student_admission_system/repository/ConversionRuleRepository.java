package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.ConversionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversionRuleRepository extends JpaRepository<ConversionRule, Integer> {
    Optional<ConversionRule> findByConversionCode(String conversionCode);

}
