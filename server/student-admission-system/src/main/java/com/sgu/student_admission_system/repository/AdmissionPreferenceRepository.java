package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.AdmissionPreference;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AdmissionPreferenceRepository extends JpaRepository<AdmissionPreference, Integer> {
    boolean existsByApplicant_CccdAndPriorityOrder(String cccd, Integer priorityOrder);

    boolean existsByApplicant_CccdAndPriorityOrderAndIdNot(String cccd, Integer priorityOrder, Integer id);

    @EntityGraph(attributePaths = "applicant")
    List<AdmissionPreference> findAllByApplicant_CccdIn(Collection<String> cccds);

}
