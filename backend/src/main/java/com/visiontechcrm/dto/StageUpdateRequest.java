package com.visiontechcrm.dto;

import com.visiontechcrm.model.LeadStage;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Body of PUT /api/leads/{id}/stage. */
@Data
public class StageUpdateRequest {

    @NotNull
    private LeadStage stage;
}
