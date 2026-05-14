-- ============================================================================
-- Vision Technologies CRM - MySQL schema
-- ----------------------------------------------------------------------------
-- Hibernate (ddl-auto=update) is the authoritative DDL source while the app
-- is running. This file is shipped to MySQL on first container start
-- (docker-entrypoint-initdb.d) so the empty database boots with the right
-- tables and indexes even before the backend connects.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS vision_crm
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vision_crm;

-- ---- Users ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    full_name       VARCHAR(120) NOT NULL,
    email           VARCHAR(160) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    enabled         TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---- Customers ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    company_name    VARCHAR(160) NOT NULL,
    industry        VARCHAR(80),
    contact_person  VARCHAR(120),
    email           VARCHAR(160),
    phone           VARCHAR(40),
    address         VARCHAR(255),
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_customers_company (company_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---- Leads ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS leads (
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    customer_id      BIGINT         NOT NULL,
    owner_user_id    BIGINT,
    title            VARCHAR(160)   NOT NULL,
    estimated_value  DECIMAL(14,2)  DEFAULT 0,
    stage            VARCHAR(20)    NOT NULL DEFAULT 'NEW',
    expected_close   DATE,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_leads_stage (stage),
    CONSTRAINT fk_leads_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_leads_owner
        FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---- Interactions ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS interactions (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    customer_id   BIGINT       NOT NULL,
    user_id       BIGINT,
    type          VARCHAR(20)  NOT NULL,
    subject       VARCHAR(200) NOT NULL,
    notes         TEXT,
    occurred_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_interactions_customer (customer_id),
    CONSTRAINT fk_interactions_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_interactions_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---- Support tickets ------------------------------------------------------
CREATE TABLE IF NOT EXISTS support_tickets (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    customer_id    BIGINT       NOT NULL,
    assignee_id    BIGINT,
    subject        VARCHAR(200) NOT NULL,
    description    TEXT,
    priority       VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    status         VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at    DATETIME,
    PRIMARY KEY (id),
    INDEX idx_tickets_status (status),
    CONSTRAINT fk_tickets_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_tickets_assignee
        FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---- Sales opportunities --------------------------------------------------
CREATE TABLE IF NOT EXISTS sales_opportunities (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    customer_id    BIGINT        NOT NULL,
    name           VARCHAR(200)  NOT NULL,
    value          DECIMAL(14,2) NOT NULL DEFAULT 0,
    probability    INT           NOT NULL DEFAULT 0,
    expected_close DATE,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_opps_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
