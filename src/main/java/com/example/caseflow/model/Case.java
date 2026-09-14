package com.example.caseflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "caseEntity")
    private List<Comment> comments = new ArrayList<>();

    protected Case() {
    }

    public Case(String title, String description, CaseStatus status, LocalDateTime createdAt) {
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

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public List<Comment> getComments() {
        return comments;
    }
}