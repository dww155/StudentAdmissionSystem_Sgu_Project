package com.sgu.admission_desktop.dto.VsatResult;

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
public class ListVsatResultCreationRequest {

    @NotEmpty(message = "INVALID_VSAT_RESULT_LIST")
    @Valid
    List<VsatResultCreationRequest> vsatResultCreationRequestList;
}
