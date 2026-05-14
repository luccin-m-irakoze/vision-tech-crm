package com.visiontechcrm.repository;

import com.visiontechcrm.model.Lead;
import com.visiontechcrm.model.LeadStage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStage(LeadStage stage);

    long countByStage(LeadStage stage);
}
