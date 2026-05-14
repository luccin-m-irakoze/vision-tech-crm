package com.visiontechcrm.dto;

import com.visiontechcrm.model.Priority;
import com.visiontechcrm.model.TicketStatus;
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
public class TicketDto {

    private Long id;

    @NotNull
    private Long customerId;

    private String customerName;

    private Long assigneeId;

    private String assigneeName;

    @NotBlank
    private String subject;

    private String description;

    private Priority priority;

    private TicketStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
