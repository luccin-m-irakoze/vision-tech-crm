package com.visiontechcrm.service;

import com.visiontechcrm.dto.CustomerDto;
import com.visiontechcrm.exception.ResourceNotFoundException;
import com.visiontechcrm.mapper.CustomerMapper;
import com.visiontechcrm.model.Customer;
import com.visiontechcrm.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Encapsulates all business rules around the {@code Customer} aggregate. */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(customerMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CustomerDto findById(Long id) {
        Customer c =
                customerRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return customerMapper.toDto(c);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        return customerRepository
                .findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public CustomerDto create(CustomerDto dto) {
        Customer saved = customerRepository.save(customerMapper.toEntity(dto));
        return customerMapper.toDto(saved);
    }

    public CustomerDto update(Long id, CustomerDto dto) {
        Customer existing =
                customerRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerMapper.copyToEntity(dto, existing);
        return customerMapper.toDto(customerRepository.save(existing));
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        customerRepository.deleteById(id);
    }
}
