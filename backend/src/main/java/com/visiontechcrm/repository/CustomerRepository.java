package com.visiontechcrm.repository;

import com.visiontechcrm.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String companyName, String email);
}
