package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ListConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleUpdateRequest;
import com.sgu.student_admission_system.entity.ConversionRule;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.ConversionRuleMapper;
import com.sgu.student_admission_system.repository.ConversionRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConversionRuleService {

    ConversionRuleRepository conversionRuleRepository;
    ConversionRuleMapper conversionRuleMapper;

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

        List<ConversionRule> conversionRules = conversionRuleCreationRequests
                .stream()
                .map(conversionRuleMapper::toConversionRule)
                .toList();

        List<ConversionRule> savedConversionRules = conversionRuleRepository.saveAll(conversionRules);

        return savedConversionRules
                .stream()
                .map(conversionRuleMapper::toConversionRuleResponse)
                .toList();
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
}
