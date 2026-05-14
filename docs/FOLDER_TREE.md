# Phase 2A — Complete Folder Tree

```
vision-tech-crm/
├── docker-compose.yml
├── README.md
├── .gitignore
├── docs/
│   ├── PHASE1.md
│   ├── FOLDER_TREE.md
│   ├── DESIGN_PATTERN.md
│   ├── CODING_STANDARDS.md
│   ├── TEST_PLAN.md
│   ├── GIT_GUIDE.md
│   └── presentations/
│       ├── FULL_DOC_DECK.md
│       └── PRESENTATION_DECK.md
│
├── backend/                              # Spring Boot 3 REST API
│   ├── pom.xml
│   ├── Dockerfile
│   ├── .env.example
│   └── src/
│       ├── main/
│       │   ├── java/com/visiontechcrm/
│       │   │   ├── VisionTechCrmApplication.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   ├── CorsConfig.java
│       │   │   │   └── DataLoader.java
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── CustomerController.java
│       │   │   │   ├── LeadController.java
│       │   │   │   ├── InteractionController.java
│       │   │   │   ├── TicketController.java
│       │   │   │   └── ReportController.java
│       │   │   ├── dto/
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── LoginResponse.java
│       │   │   │   ├── CustomerDto.java
│       │   │   │   ├── LeadDto.java
│       │   │   │   ├── InteractionDto.java
│       │   │   │   ├── TicketDto.java
│       │   │   │   ├── StageUpdateRequest.java
│       │   │   │   └── ReportSummaryDto.java
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── ErrorResponse.java
│       │   │   ├── mapper/
│       │   │   │   ├── CustomerMapper.java
│       │   │   │   ├── LeadMapper.java
│       │   │   │   ├── InteractionMapper.java
│       │   │   │   └── TicketMapper.java
│       │   │   ├── model/
│       │   │   │   ├── User.java
│       │   │   │   ├── Role.java
│       │   │   │   ├── Customer.java
│       │   │   │   ├── Lead.java
│       │   │   │   ├── LeadStage.java
│       │   │   │   ├── Interaction.java
│       │   │   │   ├── InteractionType.java
│       │   │   │   ├── SupportTicket.java
│       │   │   │   ├── Priority.java
│       │   │   │   ├── TicketStatus.java
│       │   │   │   └── SalesOpportunity.java
│       │   │   ├── repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── CustomerRepository.java
│       │   │   │   ├── LeadRepository.java
│       │   │   │   ├── InteractionRepository.java
│       │   │   │   ├── SupportTicketRepository.java
│       │   │   │   └── SalesOpportunityRepository.java
│       │   │   ├── security/
│       │   │   │   ├── JwtUtil.java
│       │   │   │   ├── JwtAuthFilter.java
│       │   │   │   └── CustomUserDetailsService.java
│       │   │   └── service/
│       │   │       ├── AuthService.java
│       │   │       ├── CustomerService.java
│       │   │       ├── LeadService.java
│       │   │       ├── InteractionService.java
│       │   │       ├── TicketService.java
│       │   │       └── ReportService.java
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── schema.sql
│       │       └── data.sql
│       └── test/java/com/visiontechcrm/service/
│           ├── CustomerServiceTest.java
│           └── AuthServiceTest.java
│
└── frontend/                             # Vue.js 3 SPA
    ├── package.json
    ├── vite.config.js
    ├── tailwind.config.js
    ├── postcss.config.js
    ├── index.html
    ├── Dockerfile
    ├── nginx.conf
    ├── .env.example
    └── src/
        ├── main.js
        ├── App.vue
        ├── assets/
        │   └── main.css
        ├── router/
        │   └── index.js
        ├── services/
        │   └── api.js
        ├── stores/
        │   ├── auth.js
        │   ├── customer.js
        │   └── lead.js
        ├── components/
        │   ├── AppShell.vue
        │   ├── AppNavbar.vue
        │   ├── AppSidebar.vue
        │   ├── CustomerCard.vue
        │   ├── LeadCard.vue
        │   ├── TicketBadge.vue
        │   ├── ConfirmModal.vue
        │   ├── LoadingSpinner.vue
        │   └── ErrorBanner.vue
        └── views/
            ├── LoginPage.vue
            ├── DashboardPage.vue
            ├── CustomersPage.vue
            ├── CustomerDetailPage.vue
            ├── LeadsPage.vue
            ├── InteractionsPage.vue
            ├── TicketsPage.vue
            └── ReportsPage.vue
```
