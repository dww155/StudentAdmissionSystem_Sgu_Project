package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.student_admission_system.dto.ExamScore.ListExamScoreCreationRequest;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreResponse;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreUpdateRequest;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.ConversionRule;
import com.sgu.student_admission_system.entity.ExamScore;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.ExamScoreMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.ConversionRuleRepository;
import com.sgu.student_admission_system.repository.ExamScoreRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExamScoreService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    ExamScoreRepository examScoreRepository;
    ExamScoreMapper examScoreMapper;
    ApplicantRepository applicantRepository;
    ConversionRuleRepository conversionRuleRepository;
    EntityManager entityManager;

    @Transactional
    public ExamScoreResponse createExamScore(ExamScoreCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        ConversionRule conversionRule = getConversionRule(request.getConversionCode());

        ExamScore examScore = examScoreMapper.toExamScore(request);
        examScore.setApplicant(applicant);

        // standardize score
        BigDecimal standardizedScore = applyConversionRule(examScore, conversionRule);

        ExamScoreResponse response = examScoreMapper.toExamScoreResponse(
                examScoreRepository.save(examScore)
        );

        response.setConversionCode(conversionRule != null ? conversionRule.getConversionCode() : null);
        response.setStandardizedScore(standardizedScore);
        return response;
    }

    @Transactional
    public List<ExamScoreResponse> createExamScores(ListExamScoreCreationRequest request) {
        List<ExamScoreCreationRequest> examScoreCreationRequests = request.getExamScoreCreationRequestList();

        if (examScoreCreationRequests.isEmpty()) {
            return List.of();
        }

        Map<String, Applicant> applicantMap = getApplicantMap(examScoreCreationRequests);
        List<ExamScoreResponse> examScoreResponses = new ArrayList<>(examScoreCreationRequests.size());

        for (int i = 0; i < examScoreCreationRequests.size(); i++) {
            ExamScoreCreationRequest creationRequest = examScoreCreationRequests.get(i);

            ExamScore examScore = examScoreMapper.toExamScore(creationRequest);
            examScore.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));

            ExamScore savedExamScore = examScoreRepository.save(examScore);

            ExamScoreResponse response = examScoreMapper.toExamScoreResponse(savedExamScore);
            response.setStandardizedScore(null);
            response.setConversionCode(null);
            examScoreResponses.add(response);

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (examScoreCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return examScoreResponses;
    }

    public ExamScoreResponse getExamScore(Integer id) {
        ExamScore examScore = examScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_SCORE_NOT_FOUND));

        ExamScoreResponse response = examScoreMapper.toExamScoreResponse(examScore);
        response.setStandardizedScore(null);
        response.setConversionCode(null);
        return response;
    }

    public List<ExamScoreResponse> getAllExamScores() {
        return examScoreRepository.findAll()
                .stream()
                .map(examScore -> {
                    ExamScoreResponse response = examScoreMapper.toExamScoreResponse(examScore);
                    response.setStandardizedScore(null);
                    response.setConversionCode(null);
                    return response;
                })
                .toList();
    }

    @Transactional
    public ExamScoreResponse updateExamScore(Integer id, ExamScoreUpdateRequest request) {
        ExamScore examScore = examScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_SCORE_NOT_FOUND));

        ConversionRule conversionRule = getConversionRule(request.getConversionCode());

        examScoreMapper.updateExamScore(examScore, request);
        BigDecimal standardizedScore = applyConversionRule(examScore, conversionRule);

        ExamScoreResponse response = examScoreMapper.toExamScoreResponse(
                examScoreRepository.save(examScore)
        );
        response.setConversionCode(conversionRule != null ? conversionRule.getConversionCode() : null);
        response.setStandardizedScore(standardizedScore);
        return response;
    }

    @Transactional
    public void deleteExamScore(Integer id) {
        ExamScore examScore = examScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_SCORE_NOT_FOUND));

        examScoreRepository.delete(examScore);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Map<String, Applicant> getApplicantMap(List<ExamScoreCreationRequest> examScoreCreationRequests) {
        Set<String> cccds = examScoreCreationRequests.stream()
                .map(ExamScoreCreationRequest::getCccd)
                .collect(Collectors.toSet());

        return applicantRepository.findAllByCccdIn(cccds)
                .stream()
                .collect(Collectors.toMap(Applicant::getCccd, Function.identity()));
    }

    private Applicant getApplicantFromMap(Map<String, Applicant> applicantMap, String cccd) {
        Applicant applicant = applicantMap.get(cccd);
        if (applicant == null) {
            throw new AppException(ErrorCode.APPLICANT_NOT_FOUND);
        }

        return applicant;
    }

    private ConversionRule getConversionRule(String conversionCode) {
        if (conversionCode == null || conversionCode.isBlank()) {
            return null;
        }

        return conversionRuleRepository.findByConversionCode(conversionCode)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSION_RULE_NOT_FOUND));
    }

    private BigDecimal applyConversionRule(ExamScore examScore, ConversionRule conversionRule) {
        if (conversionRule == null) {
            return null;
        }

        BigDecimal rawScore = getRawScoreBySubject(examScore, conversionRule.getSubject());
        if (rawScore == null) {
            return null;
        }

        String percentile = conversionRule.getPercentile();
        if (percentile == null || percentile.isBlank()) {
            return rawScore;
        }

        String percentileKey = percentile.trim().toUpperCase(Locale.ROOT);
        BigDecimal standardizedScore = switch (percentileKey) {
            case "A" -> conversionRule.getDiemA();
            case "B" -> conversionRule.getDiemB();
            case "C" -> conversionRule.getDiemC();
            case "D" -> conversionRule.getDiemD();
            default -> rawScore;
        };

        return standardizedScore != null ? standardizedScore : rawScore;
    }

    private BigDecimal getRawScoreBySubject(ExamScore examScore, String subject) {
        if (subject == null || subject.isBlank()) {
            return null;
        }

        String normalizedSubject = subject.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedSubject) {
            case "TO" -> examScore.getTo();
            case "LI" -> examScore.getLi();
            case "HO" -> examScore.getHo();
            case "SI" -> examScore.getSi();
            case "SU" -> examScore.getSu();
            case "DI" -> examScore.getDi();
            case "VA" -> examScore.getVa();
            case "N1_THI" -> examScore.getN1Thi();
            case "N1_CC" -> examScore.getN1Cc();
            case "CNCN" -> examScore.getCncn();
            case "CNNN" -> examScore.getCnnn();
            case "TI" -> examScore.getTi();
            case "KTPL" -> examScore.getKtpl();
            case "NL1" -> examScore.getNl1();
            case "NK1" -> examScore.getNk1();
            case "NK2" -> examScore.getNk2();
            default -> null;
        };
    }

    private void flushAndClear() {
        examScoreRepository.flush();
        entityManager.clear();
    }
}
