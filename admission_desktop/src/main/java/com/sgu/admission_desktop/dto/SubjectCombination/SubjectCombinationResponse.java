package com.sgu.admission_desktop.dto.SubjectCombination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
