# Phase 4 — Software Test Plan

**Project:** Vision Technologies CRM
**Version:** 1.0
**Author:** MUNEZERO IRAKOZE Luccin · Student ID 25815
**Course:** SENG 8240 — Best Programming Practices and Design Patterns
**Date:** Semester II 2025 / 2026

---

## 1. Test Objectives

The objectives of testing are derived directly from the functional and
non-functional requirements of the CRM:

| # | Objective | Aligned requirement |
|---|-----------|---------------------|
| O1 | Verify that authentication issues a valid JWT and rejects bad credentials. | Login screen, JWT security. |
| O2 | Verify that customer CRUD operations succeed and persist to MySQL. | Customer Management. |
| O3 | Verify that the sales pipeline correctly transitions leads between stages. | Lead Management. |
| O4 | Verify that interactions are recorded against the correct customer and timestamped. | Interactions log. |
| O5 | Verify the support-ticket lifecycle: create → escalate → resolve. | Support Tickets. |
| O6 | Verify the dashboard and reports produce the correct aggregates. | Reporting. |
| O7 | Verify the application behaves correctly under invalid input, missing resources and expired tokens. | Robustness, error handling. |
| O8 | Verify the dockerised stack starts and is healthy. | Phase 3 — containerisation. |

## 2. Scope

### 2.1 In scope
- Backend REST API (`/api/auth`, `/api/customers`, `/api/leads`, `/api/interactions`, `/api/tickets`, `/api/reports`).
- Frontend Vue.js SPA pages: Login, Dashboard, Customers, Leads (Kanban), Interactions, Tickets, Reports.
- JWT authentication, role-based access, CORS configuration.
- Database persistence (MySQL) and seed data (`DataLoader.java`).
- Docker Compose stack health.

### 2.2 Out of scope
- E-mail / SMS gateway integration (mocked).
- Multi-tenancy / multi-currency.
- Internationalisation.
- Load testing beyond a basic smoke under JMeter.
- Penetration testing.

## 3. Test Types

| Type | Tooling | What it covers |
|------|---------|----------------|
| **Unit tests** | JUnit 5 + Mockito | Pure logic in services (`CustomerService`, `AuthService`, `LeadService`, `TicketService`, `ReportService`). |
| **Integration tests** | Spring Boot Test + `@SpringBootTest` + `MockMvc` | REST endpoints with an in-memory H2 database. |
| **End-to-end (manual)** | Browser at `localhost:5173` + checklist | The 20 test cases below executed against the live, dockerised stack. |
| **End-to-end (automated)** | Cypress (optional) | Smoke flows: login → dashboard → CRUD customer → drag lead → log interaction → escalate ticket. |
| **Performance** | JMeter | 50 concurrent users hitting `/api/customers` and `/api/auth/login`; track p95 latency and error rate. |
| **Security** | Postman + manual | JWT expiry, CORS, 401 on missing token, 403 on wrong role. |

## 4. Test Tools — justification

| Tool | Why |
|------|-----|
| **JUnit 5** | Modern testing standard for Java, parameterised tests, lifecycle annotations, ships with Spring Boot. |
| **Mockito** | Best-in-class mocking library, perfect for isolating service-layer logic from repositories. |
| **Spring Boot Test** | Brings up a real `ApplicationContext` for integration tests with one annotation. |
| **MockMvc** | Calls controllers without an actual HTTP server — fast feedback. |
| **Cypress** | Real-browser E2E with great DX; tests run on the same dockerised app the examiner uses. |
| **JMeter** | Industry-standard load-testing tool; produces clear HTML reports for performance bottlenecks. |
| **Postman** | Manual / exploratory API testing during development. |
| **GitHub Issues** | Used to track bugs; each test failure becomes an issue with reproduction steps and the failing test ID. |

## 5. Entry & Exit criteria

### Entry criteria
- All Phase 2 code compiles (`mvn -B verify`, `npm run build`).
- `docker compose up --build` runs to completion with all services healthy.
- Seed data is loaded (admin user, 4 customers, 4 leads, 3 tickets).
- Test data is available for negative cases.

### Exit criteria
- 100% of the 20 test cases in §8 are executed.
- All Critical / High severity defects are fixed.
- Unit-test pass rate ≥ 95%; integration-test pass rate = 100%.
- Performance: p95 latency < 500 ms for read endpoints under 50 concurrent users.
- Sign-off from the instructor.

## 6. Roles & Responsibilities

| Role | Person | Responsibility |
|------|--------|----------------|
| Developer / Tester | MUNEZERO IRAKOZE Luccin (ID 25815) | Writes code, unit tests, integration tests, runs the manual checklist. |
| Examiner | Rutarindwa Jean Pierre | Independent verification of all 20 test cases on the live system. |
| Tool | GitHub Issues | Defect tracker, severity & priority labels. |

## 7. Test Schedule

| Day | Activity |
|-----|----------|
| Day 1 | Write JUnit unit tests for all services. |
| Day 2 | Add Spring Boot integration tests for `/api/customers` and `/api/leads`. |
| Day 3 | Manual E2E walk-through of all 20 test cases on dockerised stack. |
| Day 4 | Fix discovered defects, re-run failing cases. |
| Day 5 | Performance smoke with JMeter, security checks, final report. |

---

## 8. Test Cases

| Test ID | Feature | Test Description | Preconditions | Test Steps | Expected Result | Actual Result | Pass/Fail |
|---------|---------|------------------|---------------|------------|-----------------|---------------|-----------|
| TC-01 | Login (valid credentials)         | Login with the seeded admin account should succeed.                          | App is running; seed data loaded.                          | 1. Open `/login`. 2. Enter `admin@visiontech.rw` / `Admin@123`. 3. Click *Sign in*.                                | JWT stored in `localStorage`; user redirected to `/dashboard`; navbar shows full name and role.            |           |          |
| TC-02 | Login (wrong password)            | Login with a wrong password must fail and stay on the login page.            | App is running.                                            | 1. Open `/login`. 2. Enter `admin@visiontech.rw` / `wrong`. 3. Click *Sign in*.                                    | Inline error "Invalid email or password"; no JWT stored; user stays on `/login`.                          |           |          |
| TC-03 | Create customer                    | Sales rep can create a new customer.                                         | Logged in.                                                 | 1. Go to `/customers`. 2. Click *+ New customer*. 3. Fill *Acme Ltd*, industry *IT*, e-mail *a@b.rw*. 4. Save. | New row visible in the table; record persisted in `customers` table; `created_at` populated.              |           |          |
| TC-04 | Edit customer                      | Editing a customer updates the persisted record.                             | TC-03 passed.                                              | 1. Click *Edit* on *Acme Ltd*. 2. Change industry to *Banking*. 3. Save.                                       | Table row reflects *Banking*; backend returns 200 OK; DB shows updated industry.                          |           |          |
| TC-05 | Delete customer                    | Deleting a customer removes it from list and DB.                             | TC-03 passed.                                              | 1. Click *Delete* on *Acme Ltd*. 2. Confirm in modal.                                                            | Customer disappears from table; backend returns 204 No Content; DB row gone.                              |           |          |
| TC-06 | Search customer                    | Searching by partial company name filters the table.                          | Seed data loaded.                                          | 1. Open `/customers`. 2. Type `bank` in the search box. 3. Click *Search*.                                       | Only customers whose company name or e-mail contains "bank" are shown (e.g. Bank of Kigali).             |           |          |
| TC-07 | Create lead                        | Creating a lead from the Leads page persists it in stage `NEW`.              | Logged in; at least one customer exists.                   | 1. Go to `/leads`. 2. Click *+ New lead*. 3. Pick customer, title *"Office CCTV"*, value 5,000,000. 4. Save.   | New card appears in the *New* column; backend returns 201 Created.                                        |           |          |
| TC-08 | Move lead stage                    | Dragging a card from *New* to *Qualified* updates its stage.                 | TC-07 passed.                                              | 1. Drag the new card from *New* to *Qualified*.                                                                  | Card now sits in *Qualified*; DB stage column = `QUALIFIED`; PUT `/api/leads/{id}/stage` returned 200.   |           |          |
| TC-09 | Log call interaction               | Logging a call attaches it to the correct customer.                          | Logged in; customer exists.                                | 1. Go to `/interactions`. 2. Pick customer, type *CALL*, subject *"Follow-up"*, notes. 3. Save.                 | New entry appears at top of *Recent activity*; persisted in `interactions` table with `type='CALL'`.     |           |          |
| TC-10 | Send email interaction             | Same as TC-09 but `type=EMAIL`; available on the customer detail page too.   | Logged in.                                                 | 1. Repeat TC-09 with type *EMAIL*. 2. Open the customer's detail page.                                          | Email interaction is visible both on the global list and on the customer detail history.                |           |          |
| TC-11 | Create support ticket              | Support engineer can create a ticket.                                        | Logged in.                                                 | 1. Go to `/tickets`. 2. Click *+ New ticket*. 3. Pick customer, subject, priority *HIGH*. 4. Save.              | Ticket appears in the table with priority badge *High* and status *Open*.                                 |           |          |
| TC-12 | Escalate ticket                    | Escalating a ticket sets status to `ESCALATED` and bumps priority if low/medium. | TC-11 passed.                                            | 1. Click *Escalate* on the new ticket.                                                                            | Status badge changes to *Escalated*; if priority was Medium it is now High.                              |           |          |
| TC-13 | Resolve ticket                     | Resolving a ticket sets status `RESOLVED` and stamps `resolved_at`.          | TC-11 passed.                                              | 1. Click *Resolve* on a ticket.                                                                                   | Status badge *Resolved*; `resolved_at` is set in the DB.                                                  |           |          |
| TC-14 | Generate monthly report            | The Reports page shows a bar chart of monthly leads.                          | Some leads exist.                                          | 1. Go to `/reports`. 2. Inspect the *Monthly leads* card.                                                          | Bar chart renders with one bar per year-month; numbers match the DB.                                       |           |          |
| TC-15 | Export report                      | Reports can be exported as CSV.                                              | TC-14.                                                     | 1. Click *Export CSV*. 2. Save the file.                                                                          | CSV is downloaded with rows: Metric, Value; opens cleanly in Excel / Numbers.                            |           |          |
| TC-16 | Dashboard data loads               | Dashboard cards show non-zero counts after login.                            | Seed data.                                                 | 1. Log in. 2. Land on `/dashboard`.                                                                                | All four stat cards show numbers matching `GET /api/reports/summary`.                                     |           |          |
| TC-17 | JWT token expiry handling           | An expired token forces re-login.                                            | Logged in; manually set token in `localStorage` to an expired JWT (or shrink `app.jwt.expiration-ms` to `1000`). | 1. Wait for expiry. 2. Click a navbar link.                                                                       | Axios interceptor receives 401, clears state, redirects to `/login` with `?redirect=...`.                |           |          |
| TC-18 | CORS validation                    | An OPTIONS preflight from `localhost:5173` is allowed; from another origin is blocked. | App running.                                       | 1. From DevTools, fetch `http://localhost:8080/api/customers` from origin `http://evil.example`.                  | Browser blocks request because the response lacks the right `Access-Control-Allow-Origin`.               |           |          |
| TC-19 | 404 on missing resource            | The API returns a clean 404 JSON body for unknown IDs.                       | Logged in.                                                 | 1. `curl -H "Authorization: Bearer <jwt>" http://localhost:8080/api/customers/99999`.                            | HTTP 404 with `ErrorResponse` JSON `{ "status":404, "error":"Not Found", "message":"Customer not found with id: 99999" }`. |           |          |
| TC-20 | Docker health check passes         | The dockerised stack reports healthy.                                        | Docker running.                                            | 1. `docker compose up --build`. 2. After ~30 s run `docker compose ps`.                                          | All three containers show **Up** and **healthy**; logs contain `Started VisionTechCrmApplication`.       |           |          |
