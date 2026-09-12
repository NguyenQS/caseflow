package com.example.caseflow.repository;

import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByStatus(CaseStatus status);
}
