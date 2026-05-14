# Presentation Deck — Phase 1 + Phase 4 (Max 14 Slides)

> Use this outline for the **live, verbal** examiner presentation.
> Each slide has 3 short bullets and 3–4 sentences of **Speaker Notes**
> you can read aloud.

---

## Slide 1 — Title

- **Vision Technologies CRM** — final exam project
- Course: SENG 8240 — Best Programming Practices and Design Patterns
- MUNEZERO IRAKOZE Luccin · Student ID 25815 · Instructor: Rutarindwa Jean Pierre

**Speaker notes (3–4 sentences):**
Good morning. My final-exam project is a Customer Relationship Management
system built for Vision Technologies Company Ltd. Today I will walk you
through Phase 1 — the system analysis — and Phase 4 — the test plan that
shows the application actually works. The application itself is fully
running in Docker so you can interact with it after this presentation.

---

## Slide 2 — Case study: Vision Technologies Company Ltd

- East-African integrator: Security Systems, IT & Telecom, ICT, Low-Voltage.
- Data-centre design, build and support across Rwanda, Kenya, Uganda, Burundi.
- Customers: banks, telecoms, hospitals, manufacturing, government.

**Speaker notes:**
Vision Technologies — VTC — is a real Rwandan integrator that installs and
supports CCTV, access control, fire detection, networking and data-centre
infrastructure for mission-critical clients. They have grown from a single
office to four countries. Their challenge is no longer technical
delivery — it is keeping track of who their customers are, what is being
sold to them, and how well they are being supported.

---

## Slide 3 — Functional Diagram of VTC

- Sales pipeline: NEW → CONTACTED → QUALIFIED → PROPOSAL → CLOSED_WON / LOST.
- Customer support: Logged → Prioritised → Escalated / Resolved.
- Reporting feeds the management dashboard.

**Speaker notes:**
This is how VTC works internally today, conceptually. Leads come from
marketing, the website and trade fairs. They progress through five sales
stages until a deal is won or lost. Once a customer is on-boarded, the
support team takes over with prioritised tickets. Every interaction is
supposed to be logged so the next person knows the context.

---

## Slide 4 — Problem statement (the 5 pain points)

- Fragmented customer data; lost leads; SLA breaches on tickets.
- No interaction history per customer.
- Manual, late, inconsistent reporting.

**Speaker notes:**
VTC currently runs all of this through e-mail, Excel and WhatsApp. When a
sales rep leaves, his pipeline goes with him. Support tickets raised by
phone are sometimes never logged, so SLAs are missed and the company's
reputation suffers. Reports take days to compile and the numbers never
quite match between departments. A CRM solves all five problems by being
the single source of truth.

---

## Slide 5 — Use Case Diagram

- Actors: Admin, Sales Rep, Support Engineer, Manager, Customer.
- Core use cases: Login, Customers, Leads, Interactions, Tickets, Reports.
- Each role sees only what it needs.

**Speaker notes:**
The system identifies four internal roles and one external actor — the
customer who raises tickets. Sales reps mostly use the Customers, Leads and
Interactions screens. Support engineers live in the Tickets screen.
Managers focus on the Dashboard and Reports. The Admin manages users. This
maps directly to the four seeded demo accounts in the running application.

---

## Slide 6 — Class Diagram

- Six entities: User, Customer, Lead, Interaction, SupportTicket, SalesOpportunity.
- Five enumerations: Role, LeadStage, InteractionType, Priority, TicketStatus.
- Customer is the aggregate root for Leads, Interactions and Tickets.

**Speaker notes:**
The Class Diagram is the contract between the business and the database.
Every Lead, Interaction, Ticket and Opportunity belongs to exactly one
Customer. Enumerations like LeadStage make illegal states unrepresentable
— there is no way to have a Lead whose stage is "blah". This same shape
appears in the JPA entities and in the Pinia stores on the frontend.

---

## Slide 7 — Activity Diagram — Lead to Customer

- Sales rep captures lead → contacts → qualifies → sends proposal.
- Branches: lost → CLOSED_LOST; accepted → CLOSED_WON → Customer created.
- Hand-over to delivery team at the end.

**Speaker notes:**
This is the happy path from a raw enquiry to a paying customer. The
diamond decision points in the diagram correspond to actions in the live
app: a sales rep moves the Kanban card from one column to the next. When
the card reaches CLOSED_WON, the customer record gets a tag and ownership
moves to the delivery team. The activity diagram is therefore not
decorative — it is the actual workflow encoded in the code.

---

## Slide 8 — Sequence Diagram — Log call & update stage

- Sales Rep → Vue UI → REST API → Service → MySQL.
- POST /api/interactions logs the call.
- PUT /api/leads/{id}/stage moves the Kanban card.

**Speaker notes:**
Here is exactly what happens technically when Alice logs a call and drags
a lead from Qualified to Proposal. The Vue page sends two REST calls with
the JWT in the Authorization header. The Spring Boot controller delegates
to the service, which uses Spring Data JPA to update MySQL. Every step is
verified by my unit and integration tests in Phase 4.

---

## Slide 9 — Component Diagram

- Three Docker containers: Vue/NGINX, Spring Boot, MySQL.
- Private bridge network; named volume for the DB.
- One command brings the whole stack up.

**Speaker notes:**
The deployment view is intentionally simple. The frontend container is
NGINX serving a static Vue bundle and proxying /api to the backend
container. The backend container is a fat JAR built from Java 17. The
database is MySQL 8 with a named volume so data survives restarts. The
examiner only needs Docker and Git installed to run this — `docker compose
up --build` does everything else.

---

## Slide 10 — Test plan — objectives & scope

- Eight objectives tied 1-to-1 to system requirements.
- In scope: every page, every REST endpoint, JWT, CORS, Docker health.
- Out of scope: SMS/e-mail gateway, multi-tenancy, penetration testing.

**Speaker notes:**
Phase 4 is the proof that the project works. Objectives O1 to O8 each map
to a concrete requirement from Phase 1 — for example O3 verifies that the
sales pipeline only allows legal stage transitions. The scope is honest:
we are testing the things we built and explicitly excluding integrations
we did not implement. This avoids over-promising.

---

## Slide 11 — Test types & tools (with justification)

- Unit (JUnit 5 + Mockito), Integration (Spring Boot Test), E2E (manual + Cypress), Performance (JMeter).
- Tools chosen because they are industry standard and ship with Spring Boot.
- Defects logged in GitHub Issues with severity / priority labels.

**Speaker notes:**
Each test type catches a different class of bug. Unit tests catch logic
mistakes inside one service. Integration tests catch wiring mistakes
between controller, service and repository. End-to-end tests catch UX
breakage. Performance tests catch slow queries. Together they form a
testing pyramid — broad, fast at the bottom, narrow and slow at the top.

---

## Slide 12 — Sample test cases (excerpt)

- TC-01 Login with valid credentials → JWT issued.
- TC-08 Move lead stage by drag-and-drop → stage updated in DB.
- TC-12 Escalate ticket → status ESCALATED, priority bumped if needed.

**Speaker notes:**
I wrote 20 test cases in total, covering authentication, every CRUD
operation, the ticket lifecycle, the dashboard and the reports. Each case
states preconditions, exact steps, and the expected result. The Actual
Result and Pass/Fail columns are left blank so the examiner — or my future
self — can record real outcomes during execution.

---

## Slide 13 — JUnit skeletons in action

- `CustomerServiceTest`: 9 tests for CRUD + search + error paths.
- `AuthServiceTest`: 3 tests for login OK / bad password / missing user.
- All run with `mvn test` inside the backend Docker image.

**Speaker notes:**
To show the test plan is not just paper, I implemented JUnit 5 skeletons
for two services. They use Mockito to mock the repositories, which keeps
each test fast and focused on the service's own logic. They run inside the
Docker build, so a green build literally means the unit tests passed.

---

## Slide 14 — Demo & Q&A

- Live demo: login → dashboard → customers → leads → tickets → reports.
- Repo: `vision-tech-crm` on GitHub; one command runs everything.
- Thank you — questions?

**Speaker notes:**
That concludes the slides. With your permission I will switch to the
browser and walk through the live application running in Docker on my
laptop. I will log in as Alice the sales rep, create a customer, drag a
lead across the Kanban board, escalate a ticket and export a CSV report.
After that I am happy to answer any questions.
