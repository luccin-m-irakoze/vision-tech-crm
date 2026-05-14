package com.visiontechcrm.mapper;

import com.visiontechcrm.dto.TicketDto;
import com.visiontechcrm.model.SupportTicket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDto toDto(SupportTicket t) {
        return TicketDto.builder()
                .id(t.getId())
                .customerId(t.getCustomer() != null ? t.getCustomer().getId() : null)
                .customerName(t.getCustomer() != null ? t.getCustomer().getCompanyName() : null)
                .assigneeId(t.getAssignee() != null ? t.getAssignee().getId() : null)
                .assigneeName(t.getAssignee() != null ? t.getAssignee().getFullName() : null)
                .subject(t.getSubject())
                .description(t.getDescription())
                .priority(t.getPriority())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .resolvedAt(t.getResolvedAt())
                .build();
    }
}
