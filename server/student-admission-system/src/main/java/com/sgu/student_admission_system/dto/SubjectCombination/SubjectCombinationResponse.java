package com.sgu.student_admission_system.dto.SubjectCombination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectCombinationResponse {

    Integer id;
    String code;
    String mon1;
    String mon2;
    String mon3;
    String name;
}
