package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreResponse;
import com.sgu.student_admission_system.dto.ExamScore.ExamScoreUpdateRequest;
import com.sgu.student_admission_system.entity.ConversionRule;
import com.sgu.student_admission_system.entity.ExamScore;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.ExamScoreMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.ConversionRuleRepository;
import com.sgu.student_admission_system.repository.ExamScoreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExamScoreService {

    ExamScoreRepository examScoreRepository;
    ExamScoreMapper examScoreMapper;
    ApplicantRepository applicantRepository;
    ConversionRuleRepository conversionRuleRepository;

    @Transactional
    public ExamScoreResponse createExamScore(ExamScoreCreationRequest request) {
        validateApplicantExists(request.getCccd());
        ConversionRule conversionRule = getConversionRule(request.getConversionCode());

        ExamScore examScore = examScoreMapper.toExamScore(request);
        BigDecimal standardizedScore = applyConversionRule(examScore, conversionRule);

        ExamScoreResponse response = examScoreMapper.toExamScoreResponse(
                examScoreRepository.save(examScore)
        );
        response.setConversionCode(conversionRule != null ? conversionRule.getConversionCode() : null);
        response.setStandardizedScore(standardizedScore);
        return response;
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

    private void validateApplicantExists(String cccd) {
        applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
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
}
