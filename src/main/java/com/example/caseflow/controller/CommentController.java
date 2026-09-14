package com.example.caseflow.controller;

import com.example.caseflow.dto.CreateCommentRequest;
import com.example.caseflow.model.Comment;
import com.example.caseflow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment createComment(
            @PathVariable Long caseId,
            @Valid @RequestBody CreateCommentRequest request) {

        return commentService.createComment(caseId, request);
    }

    @GetMapping
    public List<Comment> getComments(@PathVariable Long caseId) {
        return commentService.getCommentsForCase(caseId);
    }
}