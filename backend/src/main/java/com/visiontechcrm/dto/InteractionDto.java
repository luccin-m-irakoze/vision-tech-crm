package com.visiontechcrm.dto;

import com.visiontechcrm.model.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionDto {

    private Long id;

    @NotNull
    private Long customerId;

    private String customerName;

    private Long userId;

    private String userName;

    @NotNull
    private InteractionType type;

    @NotBlank
    private String subject;

    private String notes;

    private LocalDateTime occurredAt;
}
