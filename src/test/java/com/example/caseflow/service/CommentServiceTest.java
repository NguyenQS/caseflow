package com.example.caseflow.service;

import com.example.caseflow.dto.CreateCommentRequest;
import com.example.caseflow.exception.CaseNotFoundException;
import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.model.Comment;
import com.example.caseflow.repository.CaseRepository;
import com.example.caseflow.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CaseRepository caseRepository;
    private CommentRepository commentRepository;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        caseRepository = mock(CaseRepository.class);
        commentRepository = mock(CommentRepository.class);

        commentService = new CommentService(
                caseRepository,
                commentRepository
        );
    }

    @Test
    void createComment_shouldSaveCommentForExistingCase() {

        Case existingCase = new Case(
                "Address change",
                "Customer reported a new address",
                CaseStatus.OPEN,
                LocalDateTime.now()
        );

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Customer called again");

        when(caseRepository.findById(1L))
                .thenReturn(Optional.of(existingCase));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.createComment(1L, request);

        assertEquals("Customer called again", result.getText());
        assertEquals(existingCase, result.getCaseEntity());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_shouldThrowWhenCaseDoesNotExist() {

        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Customer called again");

        when(caseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CaseNotFoundException.class,
                () -> commentService.createComment(999L, request)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsForCase_shouldReturnComments() {

        Comment comment = mock(Comment.class);

        when(caseRepository.existsById(1L))
                .thenReturn(true);

        when(commentRepository.findByCaseEntityId(1L))
                .thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsForCase(1L);

        assertEquals(1, result.size());

        verify(commentRepository).findByCaseEntityId(1L);
    }
}