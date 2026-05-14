package com.visiontechcrm.mapper;

import com.visiontechcrm.dto.CustomerDto;
import com.visiontechcrm.model.Customer;
import org.springframework.stereotype.Component;

/** Converts between {@link Customer} entities and {@link CustomerDto}. */
@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .industry(c.getIndustry())
                .contactPerson(c.getContactPerson())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public Customer toEntity(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getId())
                .companyName(dto.getCompanyName())
                .industry(dto.getIndustry())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
    }

    /** Copies editable fields onto an existing entity (used for PUT). */
    public void copyToEntity(CustomerDto dto, Customer target) {
        target.setCompanyName(dto.getCompanyName());
        target.setIndustry(dto.getIndustry());
        target.setContactPerson(dto.getContactPerson());
        target.setEmail(dto.getEmail());
        target.setPhone(dto.getPhone());
        target.setAddress(dto.getAddress());
    }
}
