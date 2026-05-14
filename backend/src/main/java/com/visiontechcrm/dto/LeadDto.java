package com.visiontechcrm.dto;

import com.visiontechcrm.model.LeadStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDto {

    private Long id;

    @NotNull
    private Long customerId;

    private String customerName;

    private Long ownerUserId;

    private String ownerName;

    @NotBlank
    private String title;

    @PositiveOrZero
    private BigDecimal estimatedValue;

    private LeadStage stage;

    private LocalDate expectedClose;

    private LocalDateTime createdAt;
}
