package com.visiontechcrm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visiontechcrm.dto.CustomerDto;
import com.visiontechcrm.exception.ResourceNotFoundException;
import com.visiontechcrm.mapper.CustomerMapper;
import com.visiontechcrm.model.Customer;
import com.visiontechcrm.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link CustomerService}.
 *
 * <p>Repositories are mocked with Mockito so the test exercises only the
 * service's own logic (validation, exception translation, mapping orchestration).
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;

    @InjectMocks private CustomerService customerService;

    private Customer sample;
    private CustomerDto sampleDto;

    @BeforeEach
    void setUp() {
        sample =
                Customer.builder()
                        .id(1L)
                        .companyName("Bank of Kigali")
                        .industry("Banking")
                        .email("contact@bk.rw")
                        .phone("+250788000111")
                        .createdAt(LocalDateTime.now())
                        .build();

        sampleDto =
                CustomerDto.builder()
                        .id(1L)
                        .companyName("Bank of Kigali")
                        .industry("Banking")
                        .email("contact@bk.rw")
                        .phone("+250788000111")
                        .build();
    }

    @Test
    @DisplayName("findAll() returns mapped customers")
    void findAll_returnsAll() {
        when(customerRepository.findAll()).thenReturn(List.of(sample));
        when(customerMapper.toDto(sample)).thenReturn(sampleDto);

        List<CustomerDto> result = customerService.findAll();

        assertThat(result).hasSize(1).first().extracting(CustomerDto::getCompanyName).isEqualTo("Bank of Kigali");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() throws when customer does not exist")
    void findById_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findById() returns DTO when customer exists")
    void findById_found_returnsDto() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(customerMapper.toDto(sample)).thenReturn(sampleDto);

        CustomerDto result = customerService.findById(1L);

        assertThat(result.getCompanyName()).isEqualTo("Bank of Kigali");
    }

    @Test
    @DisplayName("create() persists the entity and returns the saved DTO")
    void create_savesAndReturnsDto() {
        when(customerMapper.toEntity(sampleDto)).thenReturn(sample);
        when(customerRepository.save(sample)).thenReturn(sample);
        when(customerMapper.toDto(sample)).thenReturn(sampleDto);

        CustomerDto created = customerService.create(sampleDto);

        assertThat(created.getId()).isEqualTo(1L);
        verify(customerRepository).save(sample);
    }

    @Test
    @DisplayName("update() copies DTO fields onto the existing entity and saves")
    void update_existingCustomer_updatesFields() {
        CustomerDto patch =
                CustomerDto.builder().companyName("BK").industry("Finance").build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(customerRepository.save(any(Customer.class))).thenReturn(sample);
        when(customerMapper.toDto(sample)).thenReturn(sampleDto);

        customerService.update(1L, patch);

        verify(customerMapper).copyToEntity(patch, sample);
        verify(customerRepository).save(sample);
    }

    @Test
    @DisplayName("update() throws when target customer does not exist")
    void update_missing_throws() {
        when(customerRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(42L, sampleDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() throws ResourceNotFoundException on missing id")
    void delete_missing_throws() {
        when(customerRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.delete(7L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete() forwards to repository when id exists")
    void delete_existing_deletes() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("search() with blank query falls back to findAll")
    void search_blank_returnsAll() {
        when(customerRepository.findAll()).thenReturn(List.of(sample));
        when(customerMapper.toDto(sample)).thenReturn(sampleDto);

        List<CustomerDto> result = customerService.search("   ");

        assertThat(result).hasSize(1);
        verify(customerRepository).findAll();
    }
}
