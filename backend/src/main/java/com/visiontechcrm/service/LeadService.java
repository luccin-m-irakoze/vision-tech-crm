package com.visiontechcrm.service;

import com.visiontechcrm.dto.LeadDto;
import com.visiontechcrm.exception.ResourceNotFoundException;
import com.visiontechcrm.mapper.LeadMapper;
import com.visiontechcrm.model.Lead;
import com.visiontechcrm.model.LeadStage;
import com.visiontechcrm.repository.CustomerRepository;
import com.visiontechcrm.repository.LeadRepository;
import com.visiontechcrm.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for the sales pipeline. */
@Service
@Transactional
public class LeadService {

    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    public LeadService(
            LeadRepository leadRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.leadMapper = leadMapper;
    }

    @Transactional(readOnly = true)
    public List<LeadDto> findAll() {
        return leadRepository.findAll().stream().map(leadMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LeadDto findById(Long id) {
        return leadMapper.toDto(loadOrThrow(id));
    }

    public LeadDto create(LeadDto dto) {
        Lead lead =
                Lead.builder()
                        .customer(
                                customerRepository
                                        .findById(dto.getCustomerId())
                                        .orElseThrow(
                                                () ->
                                                        new ResourceNotFoundException(
                                                                "Customer", dto.getCustomerId())))
                        .owner(
                                dto.getOwnerUserId() == null
                                        ? null
                                        : userRepository.findById(dto.getOwnerUserId()).orElse(null))
                        .title(dto.getTitle())
                        .estimatedValue(
                                dto.getEstimatedValue() == null
                                        ? BigDecimal.ZERO
                                        : dto.getEstimatedValue())
                        .stage(dto.getStage() == null ? LeadStage.NEW : dto.getStage())
                        .expectedClose(dto.getExpectedClose())
                        .build();
        return leadMapper.toDto(leadRepository.save(lead));
    }

    public LeadDto update(Long id, LeadDto dto) {
        Lead lead = loadOrThrow(id);
        lead.setTitle(dto.getTitle());
        lead.setEstimatedValue(
                dto.getEstimatedValue() == null ? BigDecimal.ZERO : dto.getEstimatedValue());
        lead.setExpectedClose(dto.getExpectedClose());
        if (dto.getStage() != null) {
            lead.setStage(dto.getStage());
        }
        if (dto.getOwnerUserId() != null) {
            userRepository.findById(dto.getOwnerUserId()).ifPresent(lead::setOwner);
        }
        return leadMapper.toDto(leadRepository.save(lead));
    }

    /** Moves a lead to a new Kanban stage. Encapsulates the only legal way to change stage. */
    public LeadDto updateStage(Long id, LeadStage stage) {
        Lead lead = loadOrThrow(id);
        lead.setStage(stage);
        return leadMapper.toDto(leadRepository.save(lead));
    }

    public void delete(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead", id);
        }
        leadRepository.deleteById(id);
    }

    private Lead loadOrThrow(Long id) {
        return leadRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", id));
    }
}
