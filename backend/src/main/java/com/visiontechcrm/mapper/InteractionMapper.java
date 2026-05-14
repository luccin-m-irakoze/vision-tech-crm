package com.visiontechcrm.mapper;

import com.visiontechcrm.dto.InteractionDto;
import com.visiontechcrm.model.Interaction;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {

    public InteractionDto toDto(Interaction i) {
        return InteractionDto.builder()
                .id(i.getId())
                .customerId(i.getCustomer() != null ? i.getCustomer().getId() : null)
                .customerName(
                        i.getCustomer() != null ? i.getCustomer().getCompanyName() : null)
                .userId(i.getUser() != null ? i.getUser().getId() : null)
                .userName(i.getUser() != null ? i.getUser().getFullName() : null)
                .type(i.getType())
                .subject(i.getSubject())
                .notes(i.getNotes())
                .occurredAt(i.getOccurredAt())
                .build();
    }
}
