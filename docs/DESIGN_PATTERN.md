# DESIGN_PATTERN.md — MVC in the Vision Technologies CRM

The Vision Technologies CRM is built end-to-end on the **Model-View-Controller
(MVC)** architectural pattern, applied consistently in both the Spring Boot
backend and the Vue.js 3 frontend.

> **Why MVC?**
> Separation of concerns. Persistence rules live in the *Model*; the
> presentation layer (HTML / Vue templates) lives in the *View*; the *Controller*
> is the glue — it accepts requests, calls business logic, and produces a
> response. This makes the code testable, replaceable, and easy to understand
> for new team members.

---

## 1. Model layer

The **Model** is the domain — it represents *what the business cares about*.

### 1.1 Backend models (JPA entities) — `backend/src/main/java/com/visiontechcrm/model/`

| File | Role |
|------|------|
| `User.java`             | Authenticated CRM user. Holds e-mail, BCrypt password hash, role enum. |
| `Role.java`             | Enum: `ADMIN`, `SALES`, `SUPPORT`, `MANAGER`. |
| `Customer.java`         | The VTC client organisation: company name, industry, address. |
| `Lead.java`             | A potential deal. Owns a `LeadStage` and an `estimatedValue`. |
| `LeadStage.java`        | Enum used to render the Kanban columns: `NEW → CONTACTED → QUALIFIED → PROPOSAL → CLOSED_WON / CLOSED_LOST`. |
| `Interaction.java`      | A logged call / e-mail / meeting / site visit. |
| `InteractionType.java`  | Enum used by the front-end "Log a call" dropdown. |
| `SupportTicket.java`    | A customer issue. Owns `Priority` and `TicketStatus`. |
| `Priority.java`         | Enum: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`. |
| `TicketStatus.java`     | Enum: `OPEN → IN_PROGRESS → ESCALATED → RESOLVED → CLOSED`. |
| `SalesOpportunity.java` | Forecasted weighted deal for revenue projection. |

Persistence is delegated to **Spring Data JPA repositories** —
`CustomerRepository`, `LeadRepository`, etc. The repository interfaces
*declare* the data access contract, the framework *generates* the implementation
at runtime. This keeps the Model layer free of SQL.

### 1.2 Frontend models (Pinia stores) — `frontend/src/stores/`

The Pinia stores are the front-end's view of the same domain:

| File | Role |
|------|------|
| `auth.js`     | Token + logged-in user state; orchestrates `/api/auth/login`. |
| `customer.js` | List, search, create, edit, delete customers. |
| `lead.js`     | Sales pipeline; exposes `byStage(stage)` for the Kanban board. |

Stores never touch the DOM. They contain pure state and async actions, which
makes them trivially unit-testable.

---

## 2. View layer

The **View** renders the model to the user. Vue.js Single-File Components
(SFCs) take the role of the View; the backend has no server-rendered views —
its "view" is the JSON returned by the REST controllers.

### Frontend views — `frontend/src/views/`

| File | Role |
|------|------|
| `LoginPage.vue`          | Form with e-mail + password, calls `auth.login`. |
| `DashboardPage.vue`      | Summary cards (customers, open leads, tickets) + recent activity. |
| `CustomersPage.vue`      | Table with search, add, edit, delete. |
| `CustomerDetailPage.vue` | Single customer + their interaction history. |
| `LeadsPage.vue`          | Drag-and-drop Kanban board (`NEW`, `CONTACTED`, …). |
| `InteractionsPage.vue`   | Log a call / e-mail form + history list. |
| `TicketsPage.vue`        | Ticket table with priority and status badges. |
| `ReportsPage.vue`        | Bar chart of monthly leads + pie chart of tickets. |

### Reusable view components — `frontend/src/components/`

`AppNavbar.vue`, `AppSidebar.vue`, `AppShell.vue`, `CustomerCard.vue`,
`LeadCard.vue`, `TicketBadge.vue`, `ConfirmModal.vue`, `LoadingSpinner.vue`,
`ErrorBanner.vue` — small, single-purpose components composed by the pages.

---

## 3. Controller layer

The **Controller** is the *traffic director*. It accepts user input,
invokes services, and chooses the response.

### 3.1 Backend controllers — `backend/src/main/java/com/visiontechcrm/controller/`

| File | Endpoints |
|------|-----------|
| `AuthController.java`        | `POST /api/auth/login`. |
| `CustomerController.java`    | `GET, POST, PUT, DELETE /api/customers[/{id}]`. |
| `LeadController.java`        | `GET, POST, PUT, DELETE /api/leads[/{id}]`, `PUT /api/leads/{id}/stage`. |
| `InteractionController.java` | `GET, POST, DELETE /api/interactions[/{id}]`. |
| `TicketController.java`      | `GET, POST, PUT, DELETE /api/tickets[/{id}]`, `PUT /api/tickets/{id}/escalate`, `PUT /api/tickets/{id}/resolve`. |
| `ReportController.java`      | `GET /api/reports/summary`. |

Controllers are deliberately **thin**: they parse the request, hand off to a
service, and return a `ResponseEntity`. They contain *no* business rules.

### 3.2 Backend services (business logic) — `backend/src/main/java/com/visiontechcrm/service/`

The "controller" role of MVC really lives here from a pattern-purity point of view.

| File | Responsibility |
|------|----------------|
| `AuthService.java`        | Authenticates credentials and signs a JWT. |
| `CustomerService.java`    | CRUD + search rules on `Customer`. |
| `LeadService.java`        | Pipeline rules: only legal stage transitions, default stage `NEW`. |
| `InteractionService.java` | Records interactions, retrieves recent activity per customer. |
| `TicketService.java`      | Lifecycle of `SupportTicket` (escalate, resolve, timestamps). |
| `ReportService.java`      | Aggregates counts for the dashboard. |

Each service has a **single responsibility**, depends on its repositories
through constructor injection, and is annotated `@Transactional` so JPA changes
commit or roll back atomically.

### 3.3 Frontend controllers (Vue Router + Pinia actions)

On the front-end, the *Controller* role is split between:

- `frontend/src/router/index.js` — maps URLs to views and enforces auth guards.
- Pinia **actions** in `auth.js`, `customer.js`, `lead.js` — these dispatch
  HTTP requests, mutate store state, and surface errors to the view.

---

## 4. Cross-cutting building blocks

| File | Pattern | Role |
|------|---------|------|
| `mapper/CustomerMapper.java`, `LeadMapper.java`, … | **Mapper / Adapter** | Convert entities ↔ DTOs so the model is not leaked to clients. |
| `dto/*.java` | **DTO** | Stable wire contract for the REST API. |
| `security/JwtUtil.java`, `JwtAuthFilter.java`, `CustomUserDetailsService.java` | **Chain of Responsibility** (Spring's filter chain) | Authenticate every request with one JWT pass. |
| `exception/GlobalExceptionHandler.java` | **Front Controller** + **Strategy** | Translates any exception into a uniform `ErrorResponse`. |
| `services/api.js` (front-end) | **Singleton** + **Interceptor** | One configured axios instance, JWT auto-attached. |
| `components/AppShell.vue` | **Composite View** | Wraps every page in a consistent navbar + sidebar layout. |

---

## 5. How a request flows through MVC

```
Browser  --HTTP-->  Vue View (LeadsPage.vue)
                    │
                    │ user drops a card on "QUALIFIED"
                    ▼
                 Pinia action  leads.moveStage(id, 'QUALIFIED')
                    │ axios PUT /api/leads/{id}/stage
                    ▼
backend     LeadController.updateStage(id, req)
                    │
                    ▼
            LeadService.updateStage(id, stage)
                    │
                    ▼
            LeadRepository  (Spring Data JPA)
                    │  UPDATE leads SET stage='QUALIFIED' WHERE id=?
                    ▼
                  MySQL
```

The View only knows about a Pinia action. The action only knows axios. The
controller only knows the service. The service only knows the repository. The
repository only knows JPA. Each layer can be replaced without rewriting the
others — the hallmark of a well-applied MVC pattern.
