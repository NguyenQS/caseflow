package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCaseRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.repository.CaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaseServiceTest {

    private CaseRepository caseRepository;
    private CaseService caseService;

    @BeforeEach
    void setUp() {
        caseRepository = mock(CaseRepository.class);
        caseService = new CaseService(caseRepository);
    }

    @Test
    void getAllCases_shouldReturnCasesFromRepository() {
        Case firstCase = new Case(
                "Address change",
                "Customer reported a new address",
                com.example.caseflow.model.CaseStatus.OPEN,
                java.time.LocalDateTime.now()
        );

        when(caseRepository.findAll(any(Sort.class)))
            .thenReturn(List.of(firstCase));

        List<Case> result = caseService.getCases(
                Optional.empty(),
                Optional.empty()
        );

        assertEquals(1, result.size());
        assertEquals("Address change", result.get(0).getTitle());
    }

    @Test
    void getCaseById_shouldReturnExistingCase() {
        Case existingCase = new Case(
                "Missing document",
                "Customer needs to submit a document",
                com.example.caseflow.model.CaseStatus.OPEN,
                java.time.LocalDateTime.now()
        );

        when(caseRepository.findById(1L))
                .thenReturn(Optional.of(existingCase));

        Case result = caseService.getCaseById(1L);

        assertEquals("Missing document", result.getTitle());
    }

    @Test
    void getCaseById_shouldThrowWhenCaseDoesNotExist() {
        when(caseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CaseNotFoundException.class,
                () -> caseService.getCaseById(999L)
        );
    }

    @Test
    void createCase_shouldSaveNewCase() {
        CreateCaseRequest request = new CreateCaseRequest();
        request.setTitle("Insurance document");
        request.setDescription("Customer submitted a missing document");

        Case savedCase = new Case(
                request.getTitle(),
                request.getDescription(),
                com.example.caseflow.model.CaseStatus.OPEN,
                java.time.LocalDateTime.now()
        );

        when(caseRepository.save(any(Case.class)))
                .thenReturn(savedCase);

        Case result = caseService.createCase(request);

        assertEquals("Insurance document", result.getTitle());
        verify(caseRepository).save(any(Case.class));
    }

    @Test
    void updateStatus_shouldChangeAndSaveStatus() {
        Case existingCase = new Case(
                "Address change",
                "Customer reported a new address",
                com.example.caseflow.model.CaseStatus.OPEN,
                java.time.LocalDateTime.now()
        );

        when(caseRepository.findById(1L))
                .thenReturn(Optional.of(existingCase));

        when(caseRepository.save(existingCase))
                .thenReturn(existingCase);

        Case result = caseService.updateStatus(
                1L,
                com.example.caseflow.model.CaseStatus.IN_PROGRESS
        );

        assertEquals(
                com.example.caseflow.model.CaseStatus.IN_PROGRESS,
                result.getStatus()
        );

        verify(caseRepository).save(existingCase);
    }
}
