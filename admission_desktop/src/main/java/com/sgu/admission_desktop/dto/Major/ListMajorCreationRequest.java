package com.sgu.admission_desktop.dto.Major;

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
