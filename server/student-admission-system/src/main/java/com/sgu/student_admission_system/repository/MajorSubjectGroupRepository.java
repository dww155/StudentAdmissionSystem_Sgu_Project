package com.sgu.student_admission_system.repository;

import com.sgu.student_admission_system.entity.MajorSubjectGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorSubjectGroupRepository extends JpaRepository<MajorSubjectGroup, Integer> {
    boolean existsByMajor_MajorCodeAndSubjectCombination_Code(String majorCode, String subjectCombinationCode);

    boolean existsByMajor_MajorCodeAndSubjectCombination_CodeAndIdNot(String majorCode, String subjectCombinationCode, Integer id);

    @EntityGraph(attributePaths = {"major", "subjectCombination"})
    List<MajorSubjectGroup> findAllByMajor_MajorCodeInAndSubjectCombination_CodeIn(
            Iterable<String> majorCodes,
            Iterable<String> subjectCombinationCodes
    );

}
