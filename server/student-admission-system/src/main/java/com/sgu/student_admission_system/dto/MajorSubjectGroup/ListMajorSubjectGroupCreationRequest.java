package com.sgu.student_admission_system.dto.MajorSubjectGroup;

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
public class ListMajorSubjectGroupCreationRequest {
    @NotEmpty(message = "INVALID_MAJOR_SUBJECT_GROUP_LIST")
    @Valid
    List<MajorSubjectGroupCreationRequest> majorSubjectGroupCreationRequestList;
}
