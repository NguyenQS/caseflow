package com.example.caseflow.repository;

import com.example.caseflow.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCaseEntityId(Long caseId);
}
