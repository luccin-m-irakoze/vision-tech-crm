package com.visiontechcrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Transport object for {@link com.visiontechcrm.model.Customer}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;

    @NotBlank @Size(max = 160)
    private String companyName;

    @Size(max = 80)
    private String industry;

    @Size(max = 120)
    private String contactPerson;

    @Email @Size(max = 160)
    private String email;

    @Size(max = 40)
    private String phone;

    @Size(max = 255)
    private String address;

    private LocalDateTime createdAt;
}
