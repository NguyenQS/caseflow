package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Case getCaseById(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
    }

    public Case createCase(CreateCaseRequest request) {
        Case newCase = new Case(
                request.getTitle(),
                request.getDescription(),
                CaseStatus.OPEN,
                LocalDateTime.now()
        );

        return caseRepository.save(newCase);
    }
}
