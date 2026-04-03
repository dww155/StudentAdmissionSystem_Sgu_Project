package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Integer> {
    boolean existsByCccd(String cccd);

    Optional<Applicant> findByCccd(String cccd);
}
