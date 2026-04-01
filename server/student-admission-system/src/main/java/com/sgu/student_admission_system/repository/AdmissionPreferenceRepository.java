package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.AdmissionPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionPreferenceRepository extends JpaRepository<AdmissionPreference, Integer> {
}
