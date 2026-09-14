package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCommentRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.Comment;
import com.example.caseflow.repository.CaseRepository;
import com.example.caseflow.repository.CommentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CaseRepository caseRepository;
    private final CommentRepository commentRepository;

    public CommentService(
            CaseRepository caseRepository,
            CommentRepository commentRepository) {

        this.caseRepository = caseRepository;
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Long caseId, CreateCommentRequest request) {

        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(caseId));

        Comment comment = new Comment(
                request.getText(),
                LocalDateTime.now(),
                caseEntity
        );

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsForCase(Long caseId) {

        if (!caseRepository.existsById(caseId)) {
            throw new CaseNotFoundException(caseId);
        }

        return commentRepository.findByCaseEntityId(caseId);
    }
}