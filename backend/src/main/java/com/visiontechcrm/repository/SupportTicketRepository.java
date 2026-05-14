package com.visiontechcrm.repository;

import com.visiontechcrm.model.SupportTicket;
import com.visiontechcrm.model.TicketStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByStatus(TicketStatus status);

    long countByStatus(TicketStatus status);
}
