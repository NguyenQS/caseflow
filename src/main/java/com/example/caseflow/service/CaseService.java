package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.repository.CaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public Page<Case> getCases(
            Optional<CaseStatus> status,
            Optional<String> sort,
            int page,
            int size) {

        Sort sorting = parseSort(sort);

        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        return status
                .map(selectedStatus ->
                        caseRepository.findByStatus(selectedStatus, pageable)
                )
                .orElseGet(() ->
                        caseRepository.findAll(pageable)
                );
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

    private Sort parseSort(Optional<String> sort) {

        if (sort.isEmpty()) {
            return Sort.unsorted();
        }

        if (sort.get().equals("createdAt")) {
            return Sort.by("createdAt").ascending();
        }

        if (sort.get().equals("createdAt,desc")) {
            return Sort.by("createdAt").descending();
        }

        return Sort.unsorted();
    }
}
