package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ListConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleUpdateRequest;
import com.sgu.student_admission_system.entity.ConversionRule;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.ConversionRuleMapper;
import com.sgu.student_admission_system.repository.ConversionRuleRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConversionRuleService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    ConversionRuleRepository conversionRuleRepository;
    ConversionRuleMapper conversionRuleMapper;
    EntityManager entityManager;

    @Transactional
    public ConversionRuleResponse createConversionRule(ConversionRuleCreationRequest request) {
        ConversionRule conversionRule = conversionRuleMapper.toConversionRule(request);

        return conversionRuleMapper.toConversionRuleResponse(
                conversionRuleRepository.save(conversionRule)
        );
    }

    @Transactional
    public List<ConversionRuleResponse> createConversionRules(ListConversionRuleCreationRequest request) {
        List<ConversionRuleCreationRequest> conversionRuleCreationRequests = request.getConversionRuleCreationRequestList();

        if (conversionRuleCreationRequests.isEmpty()) {
            return List.of();
        }

        List<ConversionRuleResponse> conversionRuleResponses =
                new ArrayList<>(conversionRuleCreationRequests.size());

        for (int i = 0; i < conversionRuleCreationRequests.size(); i++) {
            ConversionRule conversionRule = conversionRuleMapper.toConversionRule(conversionRuleCreationRequests.get(i));
            ConversionRule savedConversionRule = conversionRuleRepository.save(conversionRule);
            conversionRuleResponses.add(conversionRuleMapper.toConversionRuleResponse(savedConversionRule));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (conversionRuleCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return conversionRuleResponses;
    }

    public ConversionRuleResponse getConversionRule(Integer id) {
        ConversionRule conversionRule = conversionRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSION_RULE_NOT_FOUND));

        return conversionRuleMapper.toConversionRuleResponse(conversionRule);
    }

    public List<ConversionRuleResponse> getAllConversionRules() {
        return conversionRuleRepository.findAll()
                .stream()
                .map(conversionRuleMapper::toConversionRuleResponse)
                .toList();
    }

    @Transactional
    public ConversionRuleResponse updateConversionRule(Integer id, ConversionRuleUpdateRequest request) {
        ConversionRule conversionRule = conversionRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSION_RULE_NOT_FOUND));

        conversionRuleMapper.updateConversionRule(conversionRule, request);

        return conversionRuleMapper.toConversionRuleResponse(
                conversionRuleRepository.save(conversionRule)
        );
    }

    @Transactional
    public void deleteConversionRule(Integer id) {
        ConversionRule conversionRule = conversionRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSION_RULE_NOT_FOUND));

        conversionRuleRepository.delete(conversionRule);
    }

    private void flushAndClear() {
        conversionRuleRepository.flush();
        entityManager.clear();
    }
}
