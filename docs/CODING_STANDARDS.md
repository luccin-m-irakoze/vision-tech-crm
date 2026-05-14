# CODING_STANDARDS.md

This document records the coding standards the Vision Technologies CRM project
follows. Backend Java code follows the **Google Java Style Guide**
(<https://google.github.io/styleguide/javaguide.html>); frontend Vue code
follows the **Vue.js Style Guide** (<https://vuejs.org/style-guide/>).

---

## 1. Backend — Google Java Style Guide

### 1.1 Source-file structure (§3)

- One top-level class per file.
- Order: package → imports → class.
- Imports are **fully qualified** (no wildcard imports).
- Source encoding is **UTF-8**.

**Example** — `backend/src/main/java/com/visiontechcrm/controller/CustomerController.java`

```java
package com.visiontechcrm.controller;

import com.visiontechcrm.dto.CustomerDto;
import com.visiontechcrm.service.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
// no wildcard "import org.springframework.web.bind.annotation.*;"
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
```

### 1.2 Naming (§5)

| Element | Convention | Example |
|---------|------------|---------|
| Package | lower-case, no underscores | `com.visiontechcrm.service` |
| Class / Interface / Enum | `UpperCamelCase` | `CustomerService`, `LeadStage` |
| Method / Variable | `lowerCamelCase` | `findById`, `estimatedValue` |
| Constant | `UPPER_SNAKE_CASE` | `MONTH_FMT` in `ReportService` |
| Type parameter | single capital letter | `T`, `R` |

### 1.3 Indentation & formatting (§4)

- **4 spaces** per indent (no tabs).
- Line length ≤ **100 characters**.
- Braces always present, even for single-statement `if`.
- One statement per line.

**Example** — `backend/src/main/java/com/visiontechcrm/service/CustomerService.java`

```java
public CustomerDto update(Long id, CustomerDto dto) {
    Customer existing =
            customerRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    customerMapper.copyToEntity(dto, existing);
    return customerMapper.toDto(customerRepository.save(existing));
}
```

### 1.4 Javadoc (§7)

- Every public class has a short Javadoc explaining what it does.
- Only **non-obvious** logic gets a comment — never narrate the syntax.

**Example** — `JwtUtil.java`

```java
/**
 * Encapsulates all JSON Web Token concerns (signing, parsing, validation).
 *
 * <p>Kept deliberately small so it can be unit-tested without Spring context.
 */
@Component
public class JwtUtil { ... }
```

### 1.5 Single Responsibility & small methods

- Each class addresses one concern (controller, service, repository, mapper).
- `LeadService.updateStage(...)` exists *only* to encapsulate the legal way of
  changing a lead's pipeline column — it is 3 lines long.
- No god-classes; cyclomatic complexity is intentionally low.

### 1.6 Exception handling

- Domain errors throw `ResourceNotFoundException`; everything else is caught by
  `GlobalExceptionHandler` and converted to a uniform JSON payload.
- Never swallow exceptions silently. Never `catch (Exception e) { }`.

---

## 2. Frontend — Vue.js Style Guide

### 2.1 Component naming (Priority A)

- **Multi-word component names**: every component has at least two words to
  avoid collision with HTML elements. Examples:
  `CustomerCard.vue`, `LeadCard.vue`, `AppNavbar.vue`, `AppSidebar.vue`,
  `LoadingSpinner.vue`.
- File names use `PascalCase.vue`.

### 2.2 Single-file component order

Each `.vue` file follows the standard order: `<template>` → `<script setup>` →
`<style>` (when needed). Composition API + `<script setup>` is used
consistently — this is the recommended modern style.

**Example** — `frontend/src/components/CustomerCard.vue`

```vue
<template>
  <div class="card hover:shadow-md transition">…</div>
</template>

<script setup>
defineProps({ customer: { type: Object, required: true } })
</script>
```

### 2.3 Prop definitions (Priority A)

Props are always defined as objects with type, required, and default — never
as bare arrays.

**Example** — `frontend/src/components/TicketBadge.vue`

```js
const props = defineProps({
  kind:  { type: String, required: true },
  value: { type: String, required: true }
})
```

### 2.4 v-for with key (Priority A)

Every `v-for` uses a unique `:key`.

**Example** — `frontend/src/views/CustomersPage.vue`

```vue
<tr v-for="c in store.items" :key="c.id">…</tr>
```

### 2.5 Avoid `v-if` with `v-for` (Priority A)

In our table rows we always pre-filter the data in the Pinia store or a
computed property; we never combine `v-if` with `v-for` on the same element.

### 2.6 Self-closing components & camelCase props (Priority B)

`<LoadingSpinner />`, `<ErrorBanner :message="error" />` — components with no
children are self-closing in templates, in line with the official style guide.

### 2.7 Single-instance state via Pinia (Priority B)

Cross-page state (auth token, customer list, leads list) lives in Pinia
stores. Components never reach into `localStorage` directly except through
`auth.js`, keeping global state in one place.

### 2.8 Tailwind utility classes

UI styling uses **Tailwind CSS** utility classes plus a small set of
project-wide `@layer components` rules in `frontend/src/assets/main.css`
(`.btn-primary`, `.input`, `.card`, `.badge`), giving consistent typography
and colours across every screen.

---

## 3. Tooling

| Layer | Tool | Purpose |
|-------|------|---------|
| Backend | Maven + `spring-boot-maven-plugin` | Build and run. |
| Backend | Lombok annotation processor | Reduce boilerplate (`@Getter`, `@Builder`, `@Data`). |
| Backend | Spring Boot Validation (`@Valid`, `jakarta.validation`) | Input validation on DTOs. |
| Frontend | Vite | Dev server + production build. |
| Frontend | Tailwind CSS | Utility-first styling. |
| Frontend | Pinia | State management. |
| Frontend | Vue Router 4 | SPA routing with auth guards. |
| Frontend | Axios | HTTP client with global JWT interceptor. |

---

## 4. Project-wide rules

1. **Meaningful names** everywhere (`estimatedValue`, not `v`; `customerId`,
   not `cid`).
2. **No dead code, no over-commenting** — comments explain *why*, not *what*.
3. **One responsibility per class / component / store**.
4. **All forms validate inputs before submitting** and show inline error
   messages (`LoginPage.vue`, `CustomersPage.vue`, `LeadsPage.vue`,
   `TicketsPage.vue`, `InteractionsPage.vue`).
5. **All pages handle three states** — loading (`LoadingSpinner.vue`),
   error (`ErrorBanner.vue`), and success.
