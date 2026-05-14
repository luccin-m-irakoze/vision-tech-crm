package com.visiontechcrm.repository;

import com.visiontechcrm.model.SalesOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOpportunityRepository extends JpaRepository<SalesOpportunity, Long> {
}
