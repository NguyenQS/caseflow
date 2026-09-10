package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import org.springframework.stereotype.Service;
import com.example.caseflow.exception.CaseNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaseService {

    private final List<Case> cases = new ArrayList<>();
    private long nextId = 1;

    public List<Case> getAllCases() {
        return cases;
    }

    public Case createCase(CreateCaseRequest request) {
        Case newCase = new Case(
            nextId++,
            request.getTitle(),
            request.getDescription(),
            CaseStatus.OPEN,
            LocalDateTime.now()
        );

        cases.add(newCase);

        return newCase;
    }

    public Case getCaseById(Long id) {
        return cases.stream()
            .filter(caseItem -> caseItem.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new CaseNotFoundException(id));
    }
}
