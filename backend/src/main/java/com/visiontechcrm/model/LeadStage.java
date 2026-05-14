package com.visiontechcrm.model;

/** Stages of the sales pipeline (Kanban columns on the front-end). */
public enum LeadStage {
    NEW,
    CONTACTED,
    QUALIFIED,
    PROPOSAL,
    CLOSED_WON,
    CLOSED_LOST
}
