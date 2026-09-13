package com.example.caseflow.repository;

import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByStatus(CaseStatus status);

    List<Case> findByStatus(CaseStatus status, Sort sort);

    Page<Case> findByStatus(CaseStatus status, Pageable pageable);
}
