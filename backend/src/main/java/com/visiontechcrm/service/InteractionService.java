package com.visiontechcrm.service;

import com.visiontechcrm.dto.InteractionDto;
import com.visiontechcrm.exception.ResourceNotFoundException;
import com.visiontechcrm.mapper.InteractionMapper;
import com.visiontechcrm.model.Interaction;
import com.visiontechcrm.repository.CustomerRepository;
import com.visiontechcrm.repository.InteractionRepository;
import com.visiontechcrm.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final InteractionMapper interactionMapper;

    public InteractionService(
            InteractionRepository interactionRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            InteractionMapper interactionMapper) {
        this.interactionRepository = interactionRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.interactionMapper = interactionMapper;
    }

    @Transactional(readOnly = true)
    public List<InteractionDto> findAll() {
        return interactionRepository.findTop20ByOrderByOccurredAtDesc().stream()
                .map(interactionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteractionDto> findByCustomer(Long customerId) {
        return interactionRepository.findByCustomerIdOrderByOccurredAtDesc(customerId).stream()
                .map(interactionMapper::toDto)
                .toList();
    }

    public InteractionDto create(InteractionDto dto) {
        Interaction interaction =
                Interaction.builder()
                        .customer(
                                customerRepository
                                        .findById(dto.getCustomerId())
                                        .orElseThrow(
                                                () ->
                                                        new ResourceNotFoundException(
                                                                "Customer", dto.getCustomerId())))
                        .user(
                                dto.getUserId() == null
                                        ? null
                                        : userRepository.findById(dto.getUserId()).orElse(null))
                        .type(dto.getType())
                        .subject(dto.getSubject())
                        .notes(dto.getNotes())
                        .occurredAt(
                                dto.getOccurredAt() == null
                                        ? LocalDateTime.now()
                                        : dto.getOccurredAt())
                        .build();
        return interactionMapper.toDto(interactionRepository.save(interaction));
    }

    public void delete(Long id) {
        if (!interactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Interaction", id);
        }
        interactionRepository.deleteById(id);
    }
}
