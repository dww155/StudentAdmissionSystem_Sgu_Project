package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.entity.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CalculationService {
    private void calculateApplicant(Applicant applicant) {
        // VSat result list
        List<VsatResult> vSatResults = applicant.getVSatResults().stream().toList();

        // Priority bonus point
        PriorityBonusPoint priorityBonusPoint = applicant.getPriorityBonusPoint();

        // N1 certification
        EnglishCertification englishCertification = applicant.getEnglishCertification();

        // Sorted Preference list
        Set<AdmissionPreference> preferences = applicant.getPreferences();
        List<AdmissionPreference> preferenceSortedList = preferences.stream().sorted(Comparator.comparing(AdmissionPreference::getPriorityOrder)).toList();

        // process every preference
        for (AdmissionPreference preference: preferenceSortedList) {
            // get major
            Major major = preference.getMajor();

            // get subject groups accepted for this major
            List<MajorSubjectGroup> majorSubjectGroups = major.getMajorSubjectGroups().stream().toList();

            for (MajorSubjectGroup majorSubjectGroup: majorSubjectGroups) {
                majorSubjectGroup.getSubjectCombination();
            }
        }
    }
}
