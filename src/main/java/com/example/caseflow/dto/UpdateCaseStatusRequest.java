package com.example.caseflow.dto;

import com.example.caseflow.model.CaseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateCaseStatusRequest {

    @NotNull(message = "Status must not be null")
    private CaseStatus status;

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }
}
