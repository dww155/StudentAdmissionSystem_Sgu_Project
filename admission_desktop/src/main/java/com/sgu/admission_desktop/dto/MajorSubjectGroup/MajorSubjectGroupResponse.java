package com.sgu.admission_desktop.dto.MajorSubjectGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MajorSubjectGroupResponse {

    Integer id;
    String majorCode;
    String subjectCombinationCode;

    String mon1;
    Integer subject1Weight;
    String mon2;
    Integer subject2Weight;
    String mon3;
    Integer subject3Weight;
    String keyCode;
    Boolean n1;
    Boolean to;
    Boolean li;
    Boolean ho;
    Boolean si;
    Boolean va;
    Boolean su;
    Boolean di;
    Boolean ti;
    Boolean other;
    Boolean ktpl;
    BigDecimal deviation;
}
