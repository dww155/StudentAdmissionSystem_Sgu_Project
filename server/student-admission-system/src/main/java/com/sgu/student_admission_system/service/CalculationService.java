package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.entity.*;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.repository.AdmissionPreferenceRepository;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.ConversionRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CalculationService {
    ConversionRuleRepository conversionRuleRepository;
    ApplicantRepository applicantRepository;
    AdmissionPreferenceRepository admissionPreferenceRepository;

    @Transactional
    public void calculateByApplicantCccd(String cccd) {
        Applicant applicant = applicantRepository.findForCalculationByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
        List<ConversionRule> conversionRules = getConversionRules();

        calculateApplicant(applicant, conversionRules);

        if (applicant.getPreferences() != null && !applicant.getPreferences().isEmpty()) {
            admissionPreferenceRepository.saveAll(applicant.getPreferences());
        }
    }

    @Transactional
    public int calculateAllApplicants() {
        final int pageSize = 500;
        int pageNumber = 0;
        int processedCount = 0;
        List<ConversionRule> conversionRules = getConversionRules();

        Page<Integer> applicantIdPage;
        do {
            applicantIdPage = applicantRepository.findApplicantIds(PageRequest.of(pageNumber, pageSize));
            List<AdmissionPreference> updatedPreferences = new ArrayList<>();
            List<Applicant> applicants = getApplicantsForCalculation(applicantIdPage.getContent());

            for (Applicant applicant : applicants) {
                calculateApplicant(applicant, conversionRules);
                if (applicant.getPreferences() != null && !applicant.getPreferences().isEmpty()) {
                    updatedPreferences.addAll(applicant.getPreferences());
                }
                processedCount++;
            }

            if (!updatedPreferences.isEmpty()) {
                admissionPreferenceRepository.saveAll(updatedPreferences);
            }

            pageNumber++;
        } while (applicantIdPage.hasNext());

        return processedCount;
    }

    private List<ConversionRule> getConversionRules() {
        return conversionRuleRepository.findAll();
    }

    private List<Applicant> getApplicantsForCalculation(List<Integer> applicantIds) {
        if (applicantIds == null || applicantIds.isEmpty()) {
            return List.of();
        }

        List<Applicant> fetchedApplicants = applicantRepository.findAllForCalculationByIdIn(applicantIds);
        Map<Integer, Applicant> applicantById = new HashMap<>();
        for (Applicant applicant : fetchedApplicants) {
            applicantById.put(applicant.getId(), applicant);
        }

        List<Applicant> orderedApplicants = new ArrayList<>(applicantIds.size());
        for (Integer applicantId : applicantIds) {
            Applicant applicant = applicantById.get(applicantId);
            if (applicant != null) {
                orderedApplicants.add(applicant);
            }
        }

        return orderedApplicants;
    }

    private void calculateApplicant(Applicant applicant, List<ConversionRule> conversionRules) {
        // main point
        ExamScore examScore = applicant.getExamScore();
        if (examScore == null) {
            return;
        }

        // VSat result list
        List<VsatResult> vSatResults = applicant.getVSatResults() == null
                ? List.of()
                : applicant.getVSatResults().stream().toList();

        // Priority bonus point
        PriorityBonusPoint priorityBonusPoint = applicant.getPriorityBonusPoint();

        // N1 certification
        EnglishCertification englishCertification = applicant.getEnglishCertification();

        // Sorted Preference list
        Set<AdmissionPreference> preferences = applicant.getPreferences() == null ? Set.of() : applicant.getPreferences();
        List<AdmissionPreference> preferenceSortedList = preferences.stream().sorted(Comparator.comparing(AdmissionPreference::getPriorityOrder)).toList();

        Map<String, BigDecimal> examScoresBySubject = buildExamSubjectScoreMap(examScore);
        Map<String, BigDecimal> bestVsatConvertedBySubject = buildBestVsatConvertedScores(vSatResults, conversionRules);

        // process every preference
        for (AdmissionPreference preference: preferenceSortedList) {
            // get major
            Major major = preference.getMajor();
            if (major == null || major.getMajorSubjectGroups() == null || major.getMajorSubjectGroups().isEmpty()) {
                continue;
            }

            // get subject groups accepted for this major
            List<MajorSubjectGroup> majorSubjectGroups = major.getMajorSubjectGroups().stream().toList();
            BigDecimal bestAdmissionScore = null;
            BigDecimal bestRawScore = BigDecimal.ZERO;
            BigDecimal bestBonusScore = BigDecimal.ZERO;
            String bestSubjectGroup = null;

            for (MajorSubjectGroup majorSubjectGroup: majorSubjectGroups) {
                List<String> subjects = List.of(majorSubjectGroup.getMon1(), majorSubjectGroup.getMon2(), majorSubjectGroup.getMon3());
                List<Integer> weights = List.of(majorSubjectGroup.getSubject1Weight(), majorSubjectGroup.getSubject2Weight(), majorSubjectGroup.getSubject3Weight());

                BigDecimal subjectGroupBonus = BigDecimal.ZERO;
                BigDecimal weightedRawScore = BigDecimal.ZERO;
                boolean hasSubjectBonusApplied = false;

                for (int i = 0; i < subjects.size(); i++) {
                    String subject = normalizeSubject(subjects.get(i));
                    int weight = weights.get(i) == null ? 1 : weights.get(i);
                    BigDecimal subjectScore = resolveSubjectScore(subject, examScoresBySubject, bestVsatConvertedBySubject, examScore, englishCertification, majorSubjectGroup);

                    BigDecimal perSubjectBonus = resolveSubjectPriorityBonus(priorityBonusPoint, subject, subjects);
                    if (perSubjectBonus != null) {
                        subjectScore = subjectScore.add(perSubjectBonus);
                        hasSubjectBonusApplied = true;
                    }

                    weightedRawScore = weightedRawScore.add(subjectScore.multiply(BigDecimal.valueOf(weight)));
                }
                if (!hasSubjectBonusApplied) {
                    subjectGroupBonus = resolveSubjectGroupBonus(priorityBonusPoint);
                }

                BigDecimal admissionScore = weightedRawScore.add(subjectGroupBonus);
                if (bestAdmissionScore == null || admissionScore.compareTo(bestAdmissionScore) > 0) {
                    bestAdmissionScore = admissionScore;
                    bestRawScore = weightedRawScore;
                    bestBonusScore = subjectGroupBonus;
                    bestSubjectGroup = majorSubjectGroup.getSubjectCombination() != null
                            ? majorSubjectGroup.getSubjectCombination().getCode()
                            : null;
                }
            }

            System.out.println(bestAdmissionScore);
            System.out.println(bestRawScore);
            System.out.println(bestBonusScore);
            System.out.println(bestSubjectGroup);

            preference.setExamScore(bestRawScore.setScale(5, RoundingMode.HALF_UP));
            preference.setBonusScore(bestBonusScore.setScale(2, RoundingMode.HALF_UP));
            preference.setAdmissionScore((bestAdmissionScore == null ? BigDecimal.ZERO : bestAdmissionScore).setScale(5, RoundingMode.HALF_UP));
            preference.setSubjectGroup(bestSubjectGroup);
            preference.setMethod(examScore.getMethod());
        }
    }

    private Map<String, BigDecimal> buildExamSubjectScoreMap(ExamScore examScore) {
        Map<String, BigDecimal> result = new java.util.HashMap<>();
        putIfNotNull(result, "TO", examScore.getTo());
        putIfNotNull(result, "TOA", examScore.getTo());
        putIfNotNull(result, "LI", examScore.getLi());
        putIfNotNull(result, "HO", examScore.getHo());
        putIfNotNull(result, "SI", examScore.getSi());
        putIfNotNull(result, "SU", examScore.getSu());
        putIfNotNull(result, "DI", examScore.getDi());
        putIfNotNull(result, "VA", examScore.getVa());
        putIfNotNull(result, "N1", examScore.getN1Thi());
        putIfNotNull(result, "N1_THI", examScore.getN1Thi());
        putIfNotNull(result, "N1_CC", examScore.getN1Cc());
        putIfNotNull(result, "CNCN", examScore.getCncn());
        putIfNotNull(result, "CNNN", examScore.getCnnn());
        putIfNotNull(result, "TI", examScore.getTi());
        putIfNotNull(result, "KTPL", examScore.getKtpl());
        putIfNotNull(result, "NL1", examScore.getNl1());
        putIfNotNull(result, "NK1", examScore.getNk1());
        putIfNotNull(result, "NK2", examScore.getNk2());
        return result;
    }

    private void putIfNotNull(Map<String, BigDecimal> target, String key, BigDecimal value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    private Map<String, BigDecimal> buildBestVsatConvertedScores(List<VsatResult> vSatResults, List<ConversionRule> conversionRules) {
        java.util.Map<String, BigDecimal> result = new java.util.HashMap<>();

        for (VsatResult vSatResult : vSatResults) {
            if (vSatResult.getDiem() == null || vSatResult.getMaMonThi() == null) {
                continue;
            }
            String subject = normalizeSubject(vSatResult.getMaMonThi());
            BigDecimal convertedScore = convertScoreByPercentileTable(vSatResult.getDiem(), subject, conversionRules);
            result.merge(subject, convertedScore, BigDecimal::max);
        }

        return result;
    }

    private BigDecimal convertScoreByPercentileTable(BigDecimal x, String subject, List<ConversionRule> conversionRules) {
        for (ConversionRule rule : conversionRules) {
            if (rule.getSubject() == null || !normalizeSubject(rule.getSubject()).equals(subject)) {
                continue;
            }
            BigDecimal a = rule.getDiemA();
            BigDecimal b = rule.getDiemB();
            BigDecimal c = rule.getDiemC();
            BigDecimal d = rule.getDiemD();

            if (a == null || b == null || c == null || d == null) {
                continue;
            }
            if (x.compareTo(a) < 0 || x.compareTo(b) > 0) {
                continue;
            }
            if (b.compareTo(a) == 0) {
                return c;
            }

            // y = c + ((x-a)/(b-a)) * (d-c)
            BigDecimal ratio = x.subtract(a).divide(b.subtract(a), 10, RoundingMode.HALF_UP);
            return c.add(ratio.multiply(d.subtract(c))).setScale(5, RoundingMode.HALF_UP);
        }
        return x;
    }

    private BigDecimal resolveSubjectScore(
            String subject,
            Map<String, BigDecimal> examScoresBySubject,
            Map<String, BigDecimal> bestVsatConvertedBySubject,
            ExamScore examScore,
            EnglishCertification englishCertification,
            MajorSubjectGroup majorSubjectGroup
    ) {
        BigDecimal baseExamScore = examScoresBySubject.getOrDefault(subject, BigDecimal.ZERO);
        BigDecimal bestVsatScore = bestVsatConvertedBySubject.get(subject);
        BigDecimal bestScore = bestVsatScore == null ? baseExamScore : baseExamScore.max(bestVsatScore);

        if ("N1".equals(subject) || Boolean.TRUE.equals(majorSubjectGroup.getN1())) {
            BigDecimal certScore = englishCertification != null ? englishCertification.getConversionScore() : null;
            BigDecimal n1Thi = examScore.getN1Thi();
            if (certScore != null && n1Thi != null) {
                return certScore.max(n1Thi);
            }
            if (certScore != null) {
                return certScore;
            }
            if (n1Thi != null) {
                return n1Thi;
            }
        }

        return bestScore;
    }

    private BigDecimal resolveSubjectPriorityBonus(PriorityBonusPoint priorityBonusPoint, String currentSubject, List<String> subjectsInGroup) {
        if (priorityBonusPoint == null || priorityBonusPoint.getSubjectCode() == null || priorityBonusPoint.getBonusPointForSubject() == null) {
            return null;
        }

        String bonusSubject = normalizeSubject(priorityBonusPoint.getSubjectCode());
        boolean bonusSubjectInsideGroup = subjectsInGroup.stream()
                .map(this::normalizeSubject)
                .anyMatch(bonusSubject::equals);

        if (bonusSubjectInsideGroup && bonusSubject.equals(currentSubject)) {
            return priorityBonusPoint.getBonusPointForSubject();
        }
        return null;
    }

    private BigDecimal resolveSubjectGroupBonus(PriorityBonusPoint priorityBonusPoint) {
        if (priorityBonusPoint == null || priorityBonusPoint.getBonusPointForSubjectGroup() == null) {
            return BigDecimal.ZERO;
        }
        return priorityBonusPoint.getBonusPointForSubjectGroup();
    }

    private String normalizeSubject(String subject) {
        if (subject == null) {
            return "";
        }
        String normalized = subject.trim().toUpperCase(Locale.ROOT);
        if ("TOA".equals(normalized)) {
            return "TO";
        }
        return normalized;
    }
}
