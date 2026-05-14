# PHASE 1 — System Analysis & Design

**Project:** Customer Relationship Management (CRM) System
**Case Study:** Vision Technologies Company Ltd (VTC)
**Course:** SENG 8240 — Best Programming Practices and Design Patterns
**Instructor:** Rutarindwa Jean Pierre

---

## 1. Case Study Analysis — Vision Technologies Company Ltd (VTC)

Vision Technologies Company Ltd (VTC) is an East-African technology and infrastructure
integrator that delivers turnkey **Security Systems, IT & Telecommunications, ICT and
Low-Voltage Systems** to corporate, government and mission-critical clients. The company
also provides end-to-end **data-centre design, build and support** services, covering
structured cabling, CCTV and access-control, fire-detection systems, networking, server
rooms, and managed support contracts. Its customer base is highly heterogeneous — banks,
telecom operators, ministries, hospitals, hotels and SMEs — and engagements run from
short break-fix jobs to multi-year managed-service contracts.

Today, VTC's customer-facing operations are managed through scattered tools: e-mail
threads, Excel sheets, WhatsApp groups, paper visit-reports, and a few isolated
spreadsheets per sales executive. As the company grows across Rwanda, Kenya, Uganda and
Burundi, this approach has begun to break down. Sales leads from trade fairs and the
website are lost between people; nobody can say with certainty how many open
opportunities exist, what their pipeline value is, or which engineer last visited which
client. Support tickets raised by phone are sometimes never logged, SLAs are missed, and
managers have no consolidated view of revenue forecasts, customer health, or technician
utilisation. Audit and ISO-compliance reviews are painful because activity history is
not centralised.

A purpose-built **CRM** is therefore required to (a) consolidate all customers, contacts
and contracts in a single source of truth, (b) track the full sales pipeline from raw
lead to closed deal, (c) capture every customer interaction (calls, e-mails, visits),
(d) manage support tickets with priority and SLA tracking, and (e) produce real-time
dashboards and reports for management. The proposed system — built with **Vue.js 3**,
**Spring Boot 3**, **MySQL 8** and **Docker** — will give VTC the operational visibility
and customer intimacy it needs to scale profitably across East Africa.

*Word count: ~300.*

---

## 2. Functional Diagram — VTC Internal Workflow

```mermaid
flowchart TD
    A[Marketing & Trade Fairs] -->|Raw Leads| B[Sales Pipeline]
    A1[Website Enquiry Form] --> B
    A2[Referral / Cold Call] --> B

    subgraph SP[Sales Pipeline]
        B --> B1[New Lead]
        B1 --> B2[Contacted]
        B2 --> B3[Qualified]
        B3 --> B4[Proposal Sent]
        B4 --> B5[Negotiation]
        B5 -->|Won| B6[Closed-Won]
        B5 -->|Lost| B7[Closed-Lost]
    end

    B6 --> C[Customer Account Created]
    C --> D[Project Delivery]
    D --> E[Customer Support]

    subgraph SUP[Customer Support]
        E --> E1[Ticket Logged]
        E1 --> E2{Priority?}
        E2 -->|High| E3[Escalate to L2 Engineer]
        E2 -->|Normal| E4[Assign to Technician]
        E3 --> E5[Resolution]
        E4 --> E5
        E5 --> E6[Customer Sign-off]
    end

    E6 --> F[Interactions Log]
    B --> F
    C --> F

    F --> G[Reporting & Analytics]
    G --> G1[Sales Forecast]
    G --> G2[Ticket SLA Report]
    G --> G3[Customer Health]
    G --> G4[Technician Utilisation]

    G --> H[Management Dashboard]
```

---

## 3. Problem Statement — Five Pain Points

Without a CRM, Vision Technologies currently faces the following problems:

1. **Fragmented Customer Data.** Customer information is scattered across e-mail,
   spreadsheets, paper site-survey forms and individual sales reps' phones. There is
   no single, reliable source of truth for who the customer is, what they bought, and
   who owns the relationship.

2. **Lost Sales Leads & No Pipeline Visibility.** Leads from trade-fairs, the website
   and referrals are not consistently captured. Management cannot answer basic
   questions such as "how many qualified opportunities are open?" or "what is the
   expected revenue this quarter?", so forecasting is guesswork.

3. **Unstructured Support Workflow & SLA Breaches.** Support requests arrive by phone,
   WhatsApp and e-mail and are not logged in one place. Tickets are forgotten, priority
   is not enforced, and SLA breaches on mission-critical contracts (data centres, banks)
   damage VTC's reputation.

4. **No Interaction History per Customer.** When an engineer visits or a sales rep
   calls, the activity is rarely recorded against the customer record. New staff have
   no context; account managers repeat questions; cross-sell and up-sell opportunities
   are missed.

5. **Manual, Late and Inconsistent Reporting.** Monthly sales, ticket and revenue
   reports are stitched together by hand from many Excel files. Reports arrive late,
   numbers do not reconcile across departments, and ISO / audit reviews are painful
   because activity history cannot be reconstructed.

---

## 4. Object-Oriented Analysis & Design Diagrams

### 4.1 Use Case Diagram

```mermaid
flowchart LR
    Admin([System Administrator])
    Sales([Sales Representative])
    Support([Support Engineer])
    Manager([Manager])
    Customer([Customer])

    subgraph CRM[Vision Technologies CRM]
        UC1((Login / Logout))
        UC2((Manage Users & Roles))
        UC3((Manage Customers))
        UC4((Capture Lead))
        UC5((Move Lead Stage))
        UC6((Log Call / Email Interaction))
        UC7((Create Support Ticket))
        UC8((Escalate Ticket))
        UC9((Resolve Ticket))
        UC10((View Dashboard))
        UC11((Generate Reports))
        UC12((Export Reports))
    end

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC10
    Admin --> UC11

    Sales --> UC1
    Sales --> UC3
    Sales --> UC4
    Sales --> UC5
    Sales --> UC6
    Sales --> UC10

    Support --> UC1
    Support --> UC6
    Support --> UC7
    Support --> UC8
    Support --> UC9

    Manager --> UC1
    Manager --> UC10
    Manager --> UC11
    Manager --> UC12

    Customer -. raises .-> UC7
```

### 4.2 Class Diagram

```mermaid
classDiagram
    class User {
        +Long id
        +String fullName
        +String email
        +String passwordHash
        +Role role
        +login()
        +logout()
    }

    class Role {
        <<enumeration>>
        ADMIN
        SALES
        SUPPORT
        MANAGER
    }

    class Customer {
        +Long id
        +String companyName
        +String industry
        +String contactPerson
        +String email
        +String phone
        +String address
        +LocalDateTime createdAt
        +addInteraction()
        +addLead()
    }

    class Lead {
        +Long id
        +String title
        +BigDecimal estimatedValue
        +LeadStage stage
        +LocalDate expectedClose
        +moveStage(LeadStage)
    }

    class LeadStage {
        <<enumeration>>
        NEW
        CONTACTED
        QUALIFIED
        PROPOSAL
        CLOSED_WON
        CLOSED_LOST
    }

    class Interaction {
        +Long id
        +InteractionType type
        +String subject
        +String notes
        +LocalDateTime occurredAt
    }

    class InteractionType {
        <<enumeration>>
        CALL
        EMAIL
        MEETING
        SITE_VISIT
    }

    class SupportTicket {
        +Long id
        +String subject
        +String description
        +Priority priority
        +TicketStatus status
        +LocalDateTime createdAt
        +LocalDateTime resolvedAt
        +escalate()
        +resolve()
    }

    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        CRITICAL
    }

    class TicketStatus {
        <<enumeration>>
        OPEN
        IN_PROGRESS
        ESCALATED
        RESOLVED
        CLOSED
    }

    class SalesOpportunity {
        +Long id
        +String name
        +BigDecimal value
        +Integer probability
        +LocalDate expectedClose
    }

    User "1" --> "*" Lead : owns
    User "1" --> "*" SupportTicket : assignedTo
    Customer "1" --> "*" Lead
    Customer "1" --> "*" Interaction
    Customer "1" --> "*" SupportTicket
    Customer "1" --> "*" SalesOpportunity
    Lead --> LeadStage
    Interaction --> InteractionType
    SupportTicket --> Priority
    SupportTicket --> TicketStatus
    User --> Role
```

### 4.3 Activity Diagram — Lead Capture to Customer Conversion

```mermaid
flowchart TD
    Start([Start]) --> A[Sales rep receives enquiry]
    A --> B[Open CRM and click 'New Lead']
    B --> C[Enter customer & lead details]
    C --> D{Validation OK?}
    D -- No --> C
    D -- Yes --> E[System saves Lead in stage 'NEW']
    E --> F[Sales rep contacts prospect]
    F --> G[Log call / email interaction]
    G --> H{Qualified?}
    H -- No --> I[Move stage to 'CONTACTED']
    I --> F
    H -- Yes --> J[Move stage to 'QUALIFIED']
    J --> K[Prepare & send proposal]
    K --> L[Move stage to 'PROPOSAL']
    L --> M{Customer accepts?}
    M -- No --> N[Move to 'CLOSED_LOST']
    N --> End1([End])
    M -- Yes --> O[Move to 'CLOSED_WON']
    O --> P[System creates Customer account]
    P --> Q[Hand-over to Delivery team]
    Q --> End2([End])
```

### 4.4 Sequence Diagram — Sales Rep Logs a Call & Updates Lead Stage

```mermaid
sequenceDiagram
    actor Sales as Sales Rep
    participant UI as Vue.js Frontend
    participant API as Spring Boot REST API
    participant SVC as LeadService / InteractionService
    participant DB as MySQL

    Sales->>UI: Open lead detail page
    UI->>API: GET /api/leads/{id}
    API->>SVC: getLead(id)
    SVC->>DB: SELECT * FROM leads WHERE id=?
    DB-->>SVC: Lead row
    SVC-->>API: LeadDto
    API-->>UI: 200 OK + LeadDto
    UI-->>Sales: Render lead details

    Sales->>UI: Submit "Log Call" form
    UI->>API: POST /api/interactions  (JWT in header)
    API->>SVC: createInteraction(dto)
    SVC->>DB: INSERT INTO interactions (...)
    DB-->>SVC: id
    SVC-->>API: InteractionDto
    API-->>UI: 201 Created

    Sales->>UI: Drag lead card to 'QUALIFIED' column
    UI->>API: PUT /api/leads/{id}/stage  body:{stage:'QUALIFIED'}
    API->>SVC: updateStage(id, QUALIFIED)
    SVC->>DB: UPDATE leads SET stage='QUALIFIED' WHERE id=?
    DB-->>SVC: rows affected
    SVC-->>API: LeadDto (updated)
    API-->>UI: 200 OK
    UI-->>Sales: Kanban board refreshes
```

### 4.5 Component Diagram

```mermaid
flowchart LR
    subgraph Client[Client Browser]
        V[Vue.js 3 SPA<br/>Vite + Pinia + Vue Router]
    end

    subgraph DockerHost[Docker Host]
        subgraph FE[Frontend Container]
            NGINX[NGINX :80<br/>serves /dist + proxies /api]
        end
        subgraph BE[Backend Container]
            SB[Spring Boot 3 :8080<br/>REST API + JWT + JPA]
        end
        subgraph DBC[Database Container]
            MY[(MySQL 8 :3306<br/>vision_crm)]
        end
        VOL[(named volume<br/>mysql_data)]
    end

    subgraph External[External Services]
        SMTP[SMTP / E-mail Gateway]
        SMS[SMS Gateway]
    end

    V <-->|HTTPS| NGINX
    NGINX -->|/api/*| SB
    SB -->|JDBC| MY
    MY --- VOL
    SB -. notifications .-> SMTP
    SB -. alerts .-> SMS
```

---

*End of Phase 1 documentation.*
