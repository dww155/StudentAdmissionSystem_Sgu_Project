package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Integer> {

}
