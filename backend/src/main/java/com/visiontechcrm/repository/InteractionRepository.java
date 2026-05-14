package com.visiontechcrm.repository;

import com.visiontechcrm.model.Interaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findByCustomerIdOrderByOccurredAtDesc(Long customerId);

    List<Interaction> findTop20ByOrderByOccurredAtDesc();
}
