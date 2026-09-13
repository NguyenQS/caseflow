package com.example.caseflow.controller;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.dto.UpdateCaseStatusRequest;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public Page<Case> getCases(
            @RequestParam(required = false) Optional<CaseStatus> status,
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return caseService.getCases(status, sort, page, size);
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