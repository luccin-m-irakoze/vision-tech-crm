# Phase 3 — Docker & Version Control Guide

This document covers two things the marking guide explicitly tests:

1. The Docker containerisation process — what it is, why we use it, and how the
   Vision Technologies CRM is dockerised.
2. Setting up a **Version Control System (Git)** that captures every part of
   the application under development, with a clear branching and commit-message
   strategy.

---

## Part A — The Docker containerisation process

> A **container** is a lightweight, isolated process that packages an
> application *together with all its dependencies* — the runtime, libraries,
> OS packages, configuration — into a single, portable unit. Unlike a virtual
> machine, a container shares the host OS kernel, so it starts in milliseconds
> and uses a fraction of the resources.

### Why we use Docker

| Without Docker                                                  | With Docker                                          |
|------------------------------------------------------------------|------------------------------------------------------|
| "It runs on my laptop" — env mismatches between machines.       | Same image runs identically everywhere.              |
| Manual install of Java 17, Node 18, MySQL 8, configuration…     | `docker compose up --build` and it just works.       |
| Hard to reset state.                                             | `docker compose down -v` and start fresh.            |
| Hard to deploy.                                                  | Push the image, pull it anywhere — same artefact.    |

### The general steps to dockerise an application

1. **Choose a base image.** A small, well-maintained image with the right
   runtime — e.g. `eclipse-temurin:17-jdk-alpine` for Java, `node:18-alpine`
   for building the frontend, `nginx:alpine` for serving it, `mysql:8.0` for
   the database.
2. **Write a Dockerfile** that lists, step-by-step, what should go inside the
   image: copy source, install dependencies, run the build, expose the port,
   declare the start command. Order steps from *least* to *most* frequently
   changing to maximise layer caching.
3. **Use multi-stage builds** for compiled languages: a heavy "builder" stage
   compiles the code, then a slim "runtime" stage receives only the resulting
   artefact (JAR or `/dist`). The final image is small and contains no
   build tools.
4. **Build the image** locally: `docker build -t vtc-backend ./backend`.
5. **Compose multiple containers** with `docker-compose.yml`, declaring the
   services, networks, named volumes, environment variables, port mappings
   and health-checks.
6. **Run** with `docker compose up --build`. Compose builds images, creates
   the network and the volume, starts the containers in dependency order, and
   streams their logs to the console.
7. **Iterate.** Edit code → rebuild only the affected service: `docker
   compose up --build backend`.

### How the Vision Technologies CRM is dockerised

| Container   | Image base                                | Job                                                     |
|-------------|-------------------------------------------|---------------------------------------------------------|
| `vtc-mysql` | `mysql:8.0`                               | Persists data in named volume `mysql_data`; runs `schema.sql` on first start. |
| `vtc-backend` | multi-stage: `maven:3.9.6-eclipse-temurin-17` → `eclipse-temurin:17-jdk-alpine` | Builds the Spring Boot fat-jar, then runs it on port 8080. |
| `vtc-frontend` | multi-stage: `node:18-alpine` → `nginx:alpine` | Runs `npm run build`, then NGINX serves `/dist` on port 80 and proxies `/api/*` to the backend. |

All three services share the bridge network `vtc-net`; ports `5173`, `8080`,
`3306` are mapped to the host so the examiner can poke at each one directly.
Health-checks on MySQL and the backend prevent the frontend from receiving
traffic before the API is ready.

The full compose file is at the project root: `docker-compose.yml`. To run:

```bash
docker compose up --build
```

---

## Part B — Git version control setup

We use **Git** as the VCS because it is the de-facto standard, ships with
GitHub / GitLab / Bitbucket integration, and supports the branching workflow
described below.

### B.1 Install Git

| OS       | Install                                          |
|----------|--------------------------------------------------|
| Windows  | <https://git-scm.com/download/win> (includes Git Bash) |
| macOS    | `brew install git` or `xcode-select --install`    |
| Linux    | `sudo apt install git` (Debian/Ubuntu) or `sudo dnf install git` (Fedora) |

Verify: `git --version` should print something like `git version 2.44`.

Configure once per machine:

```bash
git config --global user.name  "Your Full Name"
git config --global user.email "you@example.com"
git config --global init.defaultBranch main
git config --global core.autocrlf input   # Windows users: 'true'
```

### B.2 Initialise the repository

From the project root (`vision-tech-crm/`):

```bash
git init
git branch -m main                # ensure default branch is 'main'
```

### B.3 `.gitignore`

A combined `.gitignore` for Java + Maven + Node + Vue + Docker + IDE/OS noise
is already shipped at the project root. Highlights:

```gitignore
# Java / Maven
target/
*.class
.idea/

# Node / Vue / Vite
node_modules/
dist/
.vite/

# Env / Secrets
.env
backend/.env
frontend/.env

# OS
.DS_Store
Thumbs.db
```

### B.4 First commit

```bash
git add .
git commit -m "chore: initial commit — scaffolding and documentation"
```

### B.5 Branching strategy

We follow a simplified **Git Flow**:

```
main            ← stable, deployable
└── develop     ← integration branch for the next release
    ├── feature/auth
    ├── feature/customers
    ├── feature/leads
    ├── feature/interactions
    ├── feature/tickets
    └── feature/reports
```

Create the long-lived branches:

```bash
git checkout -b develop
git push -u origin develop
```

Create a feature branch:

```bash
git checkout develop
git checkout -b feature/auth
# … work …
git add .
git commit -m "feat(auth): add JWT login endpoint and front-end login page"
git push -u origin feature/auth
```

Merge a finished feature into develop via a Pull Request, then merge
`develop` into `main` at release time.

### B.6 Commit-message convention (Conventional Commits)

Every commit message begins with a *type*, optionally a *scope*, then a short
imperative description:

| Type     | Use for                                              | Example |
|----------|------------------------------------------------------|---------|
| `feat`   | A new user-facing feature                            | `feat(leads): add drag-and-drop Kanban board` |
| `fix`    | A bug fix                                            | `fix(auth): expire JWT after 24 h, not 24 s` |
| `docs`   | Documentation only                                   | `docs(readme): add docker compose instructions` |
| `style`  | Formatting / whitespace / no logic change            | `style(controller): reformat per Google Java Style` |
| `refactor` | Code change that neither fixes nor adds a feature  | `refactor(service): extract LeadMapper` |
| `test`   | Adding or fixing tests                               | `test(customer): add JUnit 5 CustomerServiceTest` |
| `chore`  | Build, tooling, CI                                   | `chore: bump spring-boot to 3.2.5` |
| `perf`   | Performance improvement                              | `perf(reports): cache monthly aggregation` |

Examples actually used in this project:

```
feat(auth): implement JWT login and Spring Security filter
feat(customers): CRUD endpoints + Vue table with search/add/edit/delete
feat(leads): Kanban board with drag-and-drop and stage transitions
feat(tickets): escalate and resolve endpoints
fix(cors): allow Authorization header from http://localhost:5173
docs(phase1): add 5 Mermaid diagrams and problem statement
docs(phase4): write test plan with 20 cases
test(auth): add AuthServiceTest with Mockito
chore(docker): multi-stage Dockerfiles for backend and frontend
```

### B.7 Pushing to GitHub or GitLab

1. Create an **empty** repo on GitHub (`vision-tech-crm`) — *do not* tick
   "Add README" or it will conflict.
2. Add the remote and push:

   ```bash
   git remote add origin https://github.com/<your-username>/vision-tech-crm.git
   git push -u origin main
   git push -u origin develop
   ```

For GitLab the URL is `https://gitlab.com/<your-username>/vision-tech-crm.git`.

### B.8 Useful commands cheat-sheet

```bash
git status                          # what's changed
git log --oneline --graph --all     # branch graph
git diff                            # unstaged changes
git diff --staged                   # staged changes
git checkout -b feature/<name>      # new feature branch
git merge --no-ff feature/<name>    # merge keeping commit history
git stash; git stash pop            # park changes temporarily
git revert <sha>                    # undo a public commit safely
git tag -a v1.0.0 -m "First release"; git push origin v1.0.0
```

### B.9 Capturing every part of the application

The `.gitignore` is intentionally narrow: it ignores **only** build output,
node_modules, IDE cruft, secrets and OS noise. **Everything else** —
source code, configuration templates (`.env.example`), `docker-compose.yml`,
the `Dockerfile`s, the schema, the documentation, the Mermaid diagrams,
the test plan — is tracked by Git. A fresh clone of the repository is
sufficient to reproduce the entire system from scratch with `docker
compose up --build`.
