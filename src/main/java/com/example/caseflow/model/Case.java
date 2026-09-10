package com.example.caseflow.model;

import java.time.LocalDateTime;

public class Case {

    private Long id;
    private String title;
    private String description;
    private CaseStatus status;
    private LocalDateTime createdAt;

    public Case(Long id, String title, String description, CaseStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
