package com.example.caseflow.controller;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.dto.UpdateCaseStatusRequest;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public List<Case> getCases(
            @RequestParam(required = false) Optional<CaseStatus> status,
            @RequestParam(required = false) Optional<String> sort) {

        return caseService.getCases(status, sort);
    }

    @GetMapping("/{id}")
    public Case getCaseById(@PathVariable Long id) {
        return caseService.getCaseById(id);
    }

    @PostMapping
    public Case createCase(@Valid @RequestBody CreateCaseRequest request) {
        return caseService.createCase(request);
    }

    @PatchMapping("/{id}/status")
    public Case updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request) {

        return caseService.updateStatus(id, request.getStatus());
    }
}