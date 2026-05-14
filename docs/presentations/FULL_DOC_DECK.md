# Full Documentation Deck — Vision Technologies CRM (Phases 1–4)

> Use this outline to build a PowerPoint deck for printed submission.
> Each slide lists title, bullet points and a *Notes / Visuals* line telling
> you which diagram or screenshot to place on that slide.

---

## SECTION 0 — Cover & Overview

### Slide 1 — Title

- **Title:** Vision Technologies CRM
- **Subtitle:** Final Exam Project — Best Programming Practices and Design Patterns (SENG 8240)
- **Author:** MUNEZERO IRAKOZE Luccin · Student ID 25815
- **Instructor:** Rutarindwa Jean Pierre
- **Academic year:** 2025 / 2026, Semester II
- *Notes / Visuals:* VTC logo placeholder; AUCA logo.

### Slide 2 — Project overview

- A web-based Customer Relationship Management system for Vision Technologies Company Ltd.
- Built with **Vue.js 3 + Spring Boot 3 + MySQL 8 + Docker**.
- Applies the **MVC** architectural pattern end-to-end.
- Delivered in 4 phases: Analysis → Prototype → Containerisation → Testing.
- *Notes / Visuals:* High-level architecture diagram (use `docs/PHASE1.md` Component Diagram).

### Slide 3 — Agenda

- Phase 1 — Case study & system analysis.
- Phase 2 — Software prototype, design pattern, coding standards.
- Phase 3 — Dockerisation & version control.
- Phase 4 — Software test plan.
- Live demo (login → dashboard → leads → tickets → reports).
- *Notes / Visuals:* simple text slide.

---

## SECTION 1 — Phase 1: System Analysis & Design

### Slide 4 — Case study: Vision Technologies Company Ltd (VTC)

- East-African integrator: **Security Systems, IT & Telecom, ICT, Low-Voltage**.
- Data-centre design, build and support.
- Customers: banks, telecoms, hospitals, manufacturing, government.
- Operates across Rwanda, Kenya, Uganda, Burundi.
- *Notes / Visuals:* world map / East-Africa map with VTC office pins.

### Slide 5 — Current pain points (why a CRM?)

- Fragmented customer data across Excel, WhatsApp, e-mail.
- Lost leads; no pipeline visibility.
- Unstructured support workflow, SLA breaches.
- No interaction history per customer.
- Manual, late, inconsistent reporting.
- *Notes / Visuals:* paste the 5-point Problem Statement from `docs/PHASE1.md`.

### Slide 6 — Functional Diagram of VTC

- Marketing → Sales pipeline (NEW → CLOSED-WON / LOST) → Customer account.
- Customer support: ticket logged → priority routing → escalation/resolution.
- Reporting & analytics feed management dashboard.
- *Notes / Visuals:* render the **Functional Flowchart** Mermaid block from `PHASE1.md §2`.

### Slide 7 — Use Case Diagram

- Actors: Admin, Sales Rep, Support Engineer, Manager, Customer.
- Core use cases: Login, Manage Customers, Capture Lead, Move Stage, Log Interaction, Create / Escalate / Resolve Ticket, View Dashboard, Reports.
- *Notes / Visuals:* render the **Use Case** Mermaid block from `PHASE1.md §4.1`.

### Slide 8 — Class Diagram

- Entities: User, Customer, Lead, Interaction, SupportTicket, SalesOpportunity.
- Enumerations: Role, LeadStage, InteractionType, Priority, TicketStatus.
- Relationships: Customer 1—* Lead, Customer 1—* Interaction, Customer 1—* Ticket.
- *Notes / Visuals:* render the **Class** Mermaid block from `PHASE1.md §4.2`.

### Slide 9 — Activity Diagram — Lead → Customer

- Sales rep receives enquiry → creates lead → contacts → qualifies → proposal → wins → customer created.
- Branches for *Closed-Lost* and *not-qualified* loops.
- *Notes / Visuals:* render the **Activity** Mermaid block from `PHASE1.md §4.3`.

### Slide 10 — Sequence Diagram — Log call & update stage

- Sales rep → Vue UI → REST API → Service → MySQL.
- Shows GET lead, POST interaction, PUT stage flow.
- *Notes / Visuals:* render the **Sequence** Mermaid block from `PHASE1.md §4.4`.

### Slide 11 — Component Diagram

- Vue.js SPA → NGINX → Spring Boot REST API → MySQL.
- Docker host containerises all three services; named volume `mysql_data`.
- External services: SMTP, SMS gateways.
- *Notes / Visuals:* render the **Component** Mermaid block from `PHASE1.md §4.5`.

---

## SECTION 2 — Phase 2: Prototype, Design Pattern, Coding Standards

### Slide 12 — Tech stack

- Frontend: Vue 3 (Composition API), Vite, Vue Router 4, Pinia, Tailwind CSS, Chart.js.
- Backend: Spring Boot 3, Spring Security (JWT), Spring Data JPA, Lombok, MapStruct-style mappers.
- Database: MySQL 8 with InnoDB; schema in `backend/src/main/resources/schema.sql`.
- Build & run: Maven, npm, Docker Compose.
- *Notes / Visuals:* logos.

### Slide 13 — Folder structure

- Show the two top-level folders `backend/` and `frontend/` and one or two levels.
- Highlight: `controller`, `service`, `repository`, `model`, `dto`, `mapper`, `security`, `config`, `exception` packages on the backend.
- Highlight: `views`, `components`, `stores`, `router`, `services` folders on the frontend.
- *Notes / Visuals:* screenshot of the tree from `docs/FOLDER_TREE.md`.

### Slide 14 — Prototype screenshots (1/4) — Login

- Email + password form with inline validation.
- Demo credentials shown for the examiner.
- *Notes / Visuals:* screenshot of `LoginPage.vue`.

### Slide 15 — Prototype screenshots (2/4) — Dashboard

- 4 KPI cards: customers, open leads, open tickets, closed-won leads.
- Leads-by-stage breakdown + recent activity feed.
- *Notes / Visuals:* screenshot of `DashboardPage.vue`.

### Slide 16 — Prototype screenshots (3/4) — Leads Kanban + Customers table

- Drag-and-drop Kanban with five stages.
- Customer table with search, add, edit, delete.
- *Notes / Visuals:* two screenshots side-by-side: `LeadsPage.vue` + `CustomersPage.vue`.

### Slide 17 — Prototype screenshots (4/4) — Tickets + Reports

- Ticket table with priority and status badges, Escalate / Resolve / Delete actions.
- Reports page with bar chart and pie chart, CSV export.
- *Notes / Visuals:* screenshots of `TicketsPage.vue` + `ReportsPage.vue`.

### Slide 18 — User journeys

- Login → Dashboard → Customers → Add → Edit → Delete → Logout.
- Login → Leads → Create lead → Drag to "Qualified" → Reports.
- Login → Tickets → Create → Escalate → Resolve.
- *Notes / Visuals:* a horizontal flow with screen thumbnails.

### Slide 19 — Design pattern: MVC overview

- **Model** — JPA entities + Pinia stores.
- **View** — Vue Single-File Components + JSON returned by the REST API.
- **Controller** — Spring `@RestController` classes + service layer + Vue Router & store actions.
- Cross-cutting: DTOs, Mappers, JWT filter, GlobalExceptionHandler.
- *Notes / Visuals:* MVC diagram (block-arrow drawing).

### Slide 20 — MVC in code — Model

- `User`, `Customer`, `Lead`, `Interaction`, `SupportTicket`, `SalesOpportunity` JPA entities.
- Pinia stores: `auth.js`, `customer.js`, `lead.js`.
- *Notes / Visuals:* reference `DESIGN_PATTERN.md §1`.

### Slide 21 — MVC in code — View

- All Vue pages in `frontend/src/views/`.
- Reusable components: `AppNavbar`, `AppSidebar`, `CustomerCard`, `LeadCard`, `TicketBadge`, `ConfirmModal`, `LoadingSpinner`, `ErrorBanner`.
- *Notes / Visuals:* reference `DESIGN_PATTERN.md §2`.

### Slide 22 — MVC in code — Controller

- REST controllers map URL → service → response.
- Service classes hold business logic; repository interfaces handle persistence.
- *Notes / Visuals:* small UML or flowchart (or copy the "How a request flows" block from `DESIGN_PATTERN.md §5`).

### Slide 23 — Coding standards (backend)

- **Google Java Style Guide**: 4-space indent, ≤ 100 cols, UpperCamelCase classes, lowerCamelCase methods, no wildcard imports.
- Lombok used for boilerplate (`@Getter`, `@Builder`, `@Data`).
- Javadoc on every public class; comments only for non-obvious logic.
- *Notes / Visuals:* reference `CODING_STANDARDS.md §1`. Show a small snippet.

### Slide 24 — Coding standards (frontend)

- **Vue.js Style Guide**: multi-word component names, `<script setup>`, typed props, `:key` on `v-for`.
- Tailwind utility classes + project-wide `@layer components`.
- *Notes / Visuals:* reference `CODING_STANDARDS.md §2`. Show a small `CustomerCard.vue` snippet.

---

## SECTION 3 — Phase 3: Dockerisation & Version Control

### Slide 25 — What is a container?

- A lightweight, isolated process that bundles app + dependencies.
- Shares host kernel → starts in ms, small footprint.
- Same image runs identically on every machine.
- *Notes / Visuals:* container-vs-VM diagram.

### Slide 26 — Why Docker for this project?

- Eliminates "works on my laptop" issues.
- Examiner needs only Docker + Git to run the system.
- Volumes guarantee data persists across container restarts.
- Compose orchestrates the three services with one command.
- *Notes / Visuals:* a "before vs after" comparison table from `GIT_GUIDE.md Part A`.

### Slide 27 — Dockerisation steps (general)

- 1. Choose base image (`eclipse-temurin:17-jdk-alpine`, `node:18-alpine`, `nginx:alpine`, `mysql:8`).
- 2. Write a Dockerfile (multi-stage for compiled languages).
- 3. Build with `docker build`.
- 4. Compose multiple containers with `docker-compose.yml`.
- 5. Run with `docker compose up --build`.
- 6. Iterate.
- *Notes / Visuals:* reference `GIT_GUIDE.md Part A`.

### Slide 28 — docker-compose architecture

- Three services: `mysql`, `backend`, `frontend`, on private network `vtc-net`.
- Named volume `mysql_data` keeps the database between restarts.
- Health checks on MySQL + backend gate the frontend container.
- *Notes / Visuals:* the Component Diagram (`PHASE1.md §4.5`) OR a custom compose-services diagram.

### Slide 29 — Backend Dockerfile

- Stage 1: Maven + JDK 17 builds the fat-jar (`mvn -B -DskipTests package`).
- Stage 2: `eclipse-temurin:17-jdk-alpine` runs the jar.
- `HEALTHCHECK` calls `/actuator/health` every 20 s.
- *Notes / Visuals:* paste `backend/Dockerfile`.

### Slide 30 — Frontend Dockerfile + NGINX

- Stage 1: `node:18-alpine` runs `npm run build`.
- Stage 2: `nginx:alpine` serves `/dist` and proxies `/api/*` to backend.
- SPA fallback `try_files $uri $uri/ /index.html`.
- *Notes / Visuals:* paste `frontend/Dockerfile` + `nginx.conf` excerpt.

### Slide 31 — Git setup & branching

- `git init`, `.gitignore` for Java + Vue + Docker, first commit.
- Branches: `main` (stable), `develop` (integration), `feature/*` per feature.
- Push to GitHub via `git remote add origin … && git push -u origin main develop`.
- *Notes / Visuals:* branching graph (text or Mermaid `gitGraph`).

### Slide 32 — Conventional Commits

- `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`.
- Examples used in this project (from `GIT_GUIDE.md §B.6`).
- *Notes / Visuals:* screenshot of `git log --oneline --graph --all`.

---

## SECTION 4 — Phase 4: Software Test Plan

### Slide 33 — Test plan summary

- Objectives align 1-to-1 with system requirements (auth, customers, leads, tickets, reports, robustness, Docker health).
- Scope: backend API, frontend SPA, JWT/CORS, MySQL persistence, Docker stack.
- *Notes / Visuals:* see `TEST_PLAN.md §1 + §2`.

### Slide 34 — Test types & tools

- Unit: JUnit 5 + Mockito.
- Integration: Spring Boot Test + MockMvc + H2.
- E2E: manual checklist + optional Cypress.
- Performance: JMeter, 50 concurrent users.
- Defect tracking: GitHub Issues with severity / priority labels.
- *Notes / Visuals:* see `TEST_PLAN.md §3 + §4`.

### Slide 35 — Entry / exit criteria & schedule

- Entry: code compiles, docker stack healthy, seed data loaded.
- Exit: 100% test cases run; ≥ 95% unit-test pass; all High/Critical defects fixed.
- 5-day execution schedule.
- *Notes / Visuals:* see `TEST_PLAN.md §5 + §7`.

### Slide 36 — Test case table excerpt

- Show first 8 of the 20 cases (TC-01 → TC-08) as a 5-column table:
  ID, Feature, Description, Expected Result, Pass/Fail.
- *Notes / Visuals:* extracted from `TEST_PLAN.md §8`.

### Slide 37 — JUnit skeletons

- `CustomerServiceTest.java`: findAll, findById (not found / found), create, update, delete, search.
- `AuthServiceTest.java`: login OK / bad password / missing user.
- *Notes / Visuals:* paste a 10-line snippet from each test class.

---

## SECTION 5 — Closing

### Slide 38 — Live demo checklist

- Login as Alice.
- Create + delete a customer.
- Drag a lead.
- Log a call.
- Escalate a ticket.
- View reports + export CSV.
- *Notes / Visuals:* list with checkboxes; screenshots in the appendix.

### Slide 39 — Lessons learned

- MVC keeps each layer small and replaceable.
- Docker eliminated env friction.
- DTOs + mappers protect the model from API consumers.
- Conventional Commits make `git log` a project timeline.
- *Notes / Visuals:* 1-line takeaways.

### Slide 40 — Q & A — Thank you

- Repo URL, instructor name, demo login.
- "Questions?"
- *Notes / Visuals:* contact / repo QR code (optional).
