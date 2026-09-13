package com.example.caseflow.repository;

import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseRepository extends JpaRepository<Case, Long> {
    Page<Case> findByStatus(CaseStatus status, Pageable pageable);
}
