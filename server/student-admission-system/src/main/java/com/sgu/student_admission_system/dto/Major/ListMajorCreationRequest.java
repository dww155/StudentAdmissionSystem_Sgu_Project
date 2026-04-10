package com.sgu.student_admission_system.dto.Major;

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
public class ListMajorCreationRequest {
    @NotEmpty(message = "INVALID_MAJOR_LIST")
    @Valid
    List<MajorCreationRequest> majorCreationRequestList;
}
