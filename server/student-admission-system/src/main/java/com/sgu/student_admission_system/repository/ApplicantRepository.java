package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.Applicant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Integer> {
    boolean existsByCccd(String cccd);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByCccdIn(Collection<String> cccds);

    boolean existsByPhoneNumberIn(Collection<String> phoneNumbers);

    Optional<Applicant> findByCccd(String cccd);

    List<Applicant> findAllByCccdIn(Collection<String> cccds);

    @Query("select a.id from Applicant a")
    Page<Integer> findApplicantIds(Pageable pageable);

    @EntityGraph(attributePaths = {
            "englishCertification",
            "vSatResults",
            "priorityBonusPoint",
            "examScore",
            "preferences",
            "preferences.major",
            "preferences.major.majorSubjectGroups",
            "preferences.major.majorSubjectGroups.subjectCombination"
    })
    @Query("select distinct a from Applicant a where a.id in :ids")
    List<Applicant> findAllForCalculationByIdIn(@Param("ids") Collection<Integer> ids);

    @EntityGraph(attributePaths = {
            "englishCertification",
            "vSatResults",
            "priorityBonusPoint",
            "examScore",
            "preferences",
            "preferences.major",
            "preferences.major.majorSubjectGroups",
            "preferences.major.majorSubjectGroups.subjectCombination"
    })
    @Query("select a from Applicant a where a.cccd = :cccd")
    Optional<Applicant> findForCalculationByCccd(@Param("cccd") String cccd);
}
