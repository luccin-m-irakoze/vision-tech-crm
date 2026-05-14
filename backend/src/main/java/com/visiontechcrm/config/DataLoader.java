package com.visiontechcrm.config;

import com.visiontechcrm.model.Customer;
import com.visiontechcrm.model.Interaction;
import com.visiontechcrm.model.InteractionType;
import com.visiontechcrm.model.Lead;
import com.visiontechcrm.model.LeadStage;
import com.visiontechcrm.model.Priority;
import com.visiontechcrm.model.Role;
import com.visiontechcrm.model.SupportTicket;
import com.visiontechcrm.model.TicketStatus;
import com.visiontechcrm.model.User;
import com.visiontechcrm.repository.CustomerRepository;
import com.visiontechcrm.repository.InteractionRepository;
import com.visiontechcrm.repository.LeadRepository;
import com.visiontechcrm.repository.SupportTicketRepository;
import com.visiontechcrm.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds the database with realistic demo data on first boot.
 *
 * <p>This makes the application "examiner-ready" — admin@visiontech.rw / Admin@123 logs in
 * straight away and the dashboard, customers, leads and tickets pages have content.
 */
@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository users,
            CustomerRepository customers,
            LeadRepository leads,
            InteractionRepository interactions,
            SupportTicketRepository tickets,
            PasswordEncoder encoder) {

        return args -> {
            if (users.count() > 0) {
                return;
            }

            // ---- Users ----
            User admin =
                    users.save(
                            User.builder()
                                    .fullName("System Administrator")
                                    .email("admin@visiontech.rw")
                                    .passwordHash(encoder.encode("Admin@123"))
                                    .role(Role.ADMIN)
                                    .enabled(true)
                                    .createdAt(LocalDateTime.now())
                                    .build());

            User sales =
                    users.save(
                            User.builder()
                                    .fullName("Alice Uwase")
                                    .email("alice@visiontech.rw")
                                    .passwordHash(encoder.encode("Sales@123"))
                                    .role(Role.SALES)
                                    .enabled(true)
                                    .createdAt(LocalDateTime.now())
                                    .build());

            User support =
                    users.save(
                            User.builder()
                                    .fullName("Eric Habimana")
                                    .email("eric@visiontech.rw")
                                    .passwordHash(encoder.encode("Support@123"))
                                    .role(Role.SUPPORT)
                                    .enabled(true)
                                    .createdAt(LocalDateTime.now())
                                    .build());

            users.save(
                    User.builder()
                            .fullName("Diane Manzi")
                            .email("diane@visiontech.rw")
                            .passwordHash(encoder.encode("Manager@123"))
                            .role(Role.MANAGER)
                            .enabled(true)
                            .createdAt(LocalDateTime.now())
                            .build());

            // ---- Customers ----
            Customer bk =
                    customers.save(
                            Customer.builder()
                                    .companyName("Bank of Kigali")
                                    .industry("Banking")
                                    .contactPerson("Patrick Nshuti")
                                    .email("patrick@bk.rw")
                                    .phone("+250788000111")
                                    .address("KN 4 Av, Kigali")
                                    .createdAt(LocalDateTime.now())
                                    .build());

            Customer mtn =
                    customers.save(
                            Customer.builder()
                                    .companyName("MTN Rwanda")
                                    .industry("Telecommunications")
                                    .contactPerson("Solange Iradukunda")
                                    .email("solange@mtn.co.rw")
                                    .phone("+250788000222")
                                    .address("Nyarutarama, Kigali")
                                    .createdAt(LocalDateTime.now())
                                    .build());

            Customer kmc =
                    customers.save(
                            Customer.builder()
                                    .companyName("King Faisal Hospital")
                                    .industry("Healthcare")
                                    .contactPerson("Dr. Jean Bizimana")
                                    .email("jean@kfh.rw")
                                    .phone("+250788000333")
                                    .address("Kacyiru, Kigali")
                                    .createdAt(LocalDateTime.now())
                                    .build());

            customers.save(
                    Customer.builder()
                            .companyName("Inyange Industries")
                            .industry("Manufacturing")
                            .contactPerson("Aline Mukamana")
                            .email("aline@inyange.rw")
                            .phone("+250788000444")
                            .address("Masaka, Kigali")
                            .createdAt(LocalDateTime.now())
                            .build());

            // ---- Leads ----
            leads.save(
                    Lead.builder()
                            .customer(bk)
                            .owner(sales)
                            .title("Data Centre Cooling Upgrade")
                            .estimatedValue(new BigDecimal("85000000"))
                            .stage(LeadStage.PROPOSAL)
                            .expectedClose(LocalDate.now().plusDays(30))
                            .createdAt(LocalDateTime.now())
                            .build());

            leads.save(
                    Lead.builder()
                            .customer(mtn)
                            .owner(sales)
                            .title("Branch CCTV Modernisation (32 sites)")
                            .estimatedValue(new BigDecimal("120000000"))
                            .stage(LeadStage.QUALIFIED)
                            .expectedClose(LocalDate.now().plusDays(60))
                            .createdAt(LocalDateTime.now())
                            .build());

            leads.save(
                    Lead.builder()
                            .customer(kmc)
                            .owner(sales)
                            .title("Access Control & Visitor Management")
                            .estimatedValue(new BigDecimal("45000000"))
                            .stage(LeadStage.NEW)
                            .expectedClose(LocalDate.now().plusDays(90))
                            .createdAt(LocalDateTime.now())
                            .build());

            leads.save(
                    Lead.builder()
                            .customer(bk)
                            .owner(sales)
                            .title("Fire Detection System - HQ")
                            .estimatedValue(new BigDecimal("28000000"))
                            .stage(LeadStage.CONTACTED)
                            .expectedClose(LocalDate.now().plusDays(45))
                            .createdAt(LocalDateTime.now())
                            .build());

            // ---- Interactions ----
            interactions.save(
                    Interaction.builder()
                            .customer(bk)
                            .user(sales)
                            .type(InteractionType.CALL)
                            .subject("Discovery call with BK procurement")
                            .notes("Discussed scope of cooling upgrade. Send proposal by Friday.")
                            .occurredAt(LocalDateTime.now().minusDays(2))
                            .build());

            interactions.save(
                    Interaction.builder()
                            .customer(mtn)
                            .user(sales)
                            .type(InteractionType.MEETING)
                            .subject("Site survey at MTN HQ")
                            .notes("32 branches mapped. Need bill of quantities for cameras.")
                            .occurredAt(LocalDateTime.now().minusDays(1))
                            .build());

            // ---- Tickets ----
            tickets.save(
                    SupportTicket.builder()
                            .customer(bk)
                            .assignee(support)
                            .subject("CCTV camera offline - HQ data centre")
                            .description("Camera #14 is offline since 06:00 today.")
                            .priority(Priority.HIGH)
                            .status(TicketStatus.IN_PROGRESS)
                            .createdAt(LocalDateTime.now().minusHours(4))
                            .build());

            tickets.save(
                    SupportTicket.builder()
                            .customer(kmc)
                            .assignee(support)
                            .subject("Access card reader unresponsive - Ward C")
                            .description("Reader does not beep on tap.")
                            .priority(Priority.CRITICAL)
                            .status(TicketStatus.ESCALATED)
                            .createdAt(LocalDateTime.now().minusHours(8))
                            .build());

            tickets.save(
                    SupportTicket.builder()
                            .customer(mtn)
                            .subject("Request: add 2 new users to access system")
                            .description("Onboard 2 new security officers.")
                            .priority(Priority.LOW)
                            .status(TicketStatus.OPEN)
                            .createdAt(LocalDateTime.now().minusDays(1))
                            .build());
        };
    }
}
