package com.visiontechcrm.mapper;

import com.visiontechcrm.dto.LeadDto;
import com.visiontechcrm.model.Lead;
import org.springframework.stereotype.Component;

/** Converts {@link Lead} <-> {@link LeadDto}. */
@Component
public class LeadMapper {

    public LeadDto toDto(Lead l) {
        return LeadDto.builder()
                .id(l.getId())
                .customerId(l.getCustomer() != null ? l.getCustomer().getId() : null)
                .customerName(l.getCustomer() != null ? l.getCustomer().getCompanyName() : null)
                .ownerUserId(l.getOwner() != null ? l.getOwner().getId() : null)
                .ownerName(l.getOwner() != null ? l.getOwner().getFullName() : null)
                .title(l.getTitle())
                .estimatedValue(l.getEstimatedValue())
                .stage(l.getStage())
                .expectedClose(l.getExpectedClose())
                .createdAt(l.getCreatedAt())
                .build();
    }
}
