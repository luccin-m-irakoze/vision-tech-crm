# Vision Technologies Company Ltd — CRM System


| Layer    | Stack                                          |
|----------|------------------------------------------------|
| Frontend | Vue.js 3 + Vite + Pinia + Vue Router + Tailwind |
| Backend  | Spring Boot 3 + Spring Security (JWT) + Spring Data JPA + Lombok |
| Database | MySQL 8                                         |
| Runtime  | Docker + Docker Compose                         |

---

## 1. Prerequisites

Install the following on your machine:

- **Docker Desktop** (or Docker Engine + Compose v2) — <https://www.docker.com/>
- **Git** — <https://git-scm.com/>

That is the *only* requirement. You do **not** need Java, Maven, Node or
MySQL installed locally — everything is containerised.

---

## 2. Clone the repository

```bash
git clone https://github.com/<your-username>/vision-tech-crm.git
cd vision-tech-crm
```

If you received the source as a folder, simply `cd` into it.

---

## 3. Run with Docker

From the project root:

```bash
docker compose up --build
```

What this does:

1. Builds the Spring Boot fat-jar inside a Maven + JDK 17 container.
2. Builds the Vue SPA with Vite, then copies `/dist` into an NGINX image.
3. Pulls MySQL 8 and bootstraps `vision_crm` with the schema in
   `backend/src/main/resources/schema.sql`.
4. Wires the three containers on a private bridge network with health-checks.

The first build takes a few minutes. Subsequent builds are cached.

To run in the background: `docker compose up --build -d`
To stop: `docker compose down`
To wipe the database volume: `docker compose down -v`

---

## 4. Access the application

Open your browser at:

| URL                         | What you'll see                |
|-----------------------------|--------------------------------|
| <http://localhost:5173>     | Vue.js CRM front-end           |
| <http://localhost:8080/api> | Spring Boot REST API (JSON)    |
| `localhost:3306`            | MySQL (user `crm_user` / `crm_pass`) |

### Demo accounts

The application seeds itself on first start; use any of these to log in:

| E-mail                      | Password    | Role    |
|-----------------------------|-------------|---------|
| `admin@visiontech.rw`       | `Admin@123` | ADMIN   |
| `alice@visiontech.rw`       | `Sales@123` | SALES   |
| `eric@visiontech.rw`        | `Support@123`| SUPPORT|
| `diane@visiontech.rw`       | `Manager@123`| MANAGER|

### A typical examiner walk-through

1. Log in as **alice@visiontech.rw**.
2. Land on the **Dashboard** — see live counts for customers, leads and tickets.
3. Go to **Customers** → add `New Bank Ltd` → save → edit → delete.
4. Go to **Leads** → drag the *MTN CCTV* card from *Qualified* to *Proposal*.
5. Go to **Interactions** → log a call against *Bank of Kigali*.
6. Go to **Tickets** → escalate the highest-priority open ticket.
7. Go to **Reports** → inspect the bar chart of monthly leads and the pie
   chart of tickets by status → click *Export CSV*.

---

## 5. Local development (without Docker)

If you want to hack on the code:

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend expects MySQL on `localhost:3306` with database `vision_crm`,
user `crm_user`, password `crm_pass`. Override via env vars
(`SPRING_DATASOURCE_URL`, …) or edit `application.properties`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server runs on <http://localhost:5173> and proxies `/api/*`
to `http://localhost:8080` (configurable via `VITE_API_PROXY`).

---

## 6. Project layout

See [`docs/FOLDER_TREE.md`](docs/FOLDER_TREE.md) for the full tree.

| Folder           | Contents                                |
|------------------|-----------------------------------------|
| `backend/`       | Spring Boot 3 service.                  |
| `frontend/`      | Vue.js 3 SPA.                           |
| `docker-compose.yml` | All three containers wired together. |
| `docs/`          | Phase 1 → 4 documentation (Mermaid).    |

---

## 7. Documentation index

| File                              | Phase | What it covers |
|-----------------------------------|-------|----------------|
| [`docs/PHASE1.md`](docs/PHASE1.md)               | 1 | Case study, problem statement, 5 UML diagrams. |
| [`docs/DESIGN_PATTERN.md`](docs/DESIGN_PATTERN.md) | 2 | How MVC is applied. |
| [`docs/CODING_STANDARDS.md`](docs/CODING_STANDARDS.md) | 2 | Google Java Style + Vue.js Style Guide. |
| [`docs/GIT_GUIDE.md`](docs/GIT_GUIDE.md)         | 3 | VCS setup, branching, conventional commits. |
| [`docs/TEST_PLAN.md`](docs/TEST_PLAN.md)         | 4 | Test plan + 20 test cases. |
| [`docs/presentations/FULL_DOC_DECK.md`](docs/presentations/FULL_DOC_DECK.md) | All | Full documentation deck outline. |
| [`docs/presentations/PRESENTATION_DECK.md`](docs/presentations/PRESENTATION_DECK.md) | 1+4 | Live-presentation deck (≤14 slides). |

---
