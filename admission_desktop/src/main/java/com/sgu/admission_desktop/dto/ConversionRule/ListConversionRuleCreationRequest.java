package com.sgu.admission_desktop.dto.ConversionRule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListConversionRuleCreationRequest {
    @NotEmpty(message = "INVALID_CONVERSION_RULE_LIST")
    @Valid
    List<ConversionRuleCreationRequest> conversionRuleCreationRequestList;
}
