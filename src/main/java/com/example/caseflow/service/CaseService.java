package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.repository.CaseRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Comparator;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> getCases(
            Optional<CaseStatus> status,
            Optional<String> sort) {

        List<Case> cases = status
                .map(caseRepository::findByStatus)
                .orElseGet(caseRepository::findAll);

        if (sort.isPresent()) {
            String sortValue = sort.get();

            if (sortValue.equals("createdAt")) {
                return cases.stream()
                        .sorted(Comparator.comparing(Case::getCreatedAt))
                        .toList();
            }

            if (sortValue.equals("createdAt,desc")) {
                return cases.stream()
                        .sorted(
                                Comparator.comparing(Case::getCreatedAt)
                                        .reversed()
                        )
                        .toList();
            }
        }

        return cases;
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

    public Case updateStatus(Long id, CaseStatus status) {
        Case existingCase = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));

        existingCase.setStatus(status);

        return caseRepository.save(existingCase);
    }
}
