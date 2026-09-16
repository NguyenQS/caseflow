package com.example.caseflow.controller;

import com.example.caseflow.model.Comment;
import com.example.caseflow.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {

        Comment comment = new Comment(
                "Customer called again",
                LocalDateTime.now(),
                null
        );

        when(commentService.createComment(
                eq(1L),
                any()
        )).thenReturn(comment);

        mockMvc.perform(post("/api/cases/1/comments")
                        .contentType("application/json")
                        .content("""
                                {
                                  "text": "Customer called again"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text")
                        .value("Customer called again"));
    }

    @Test
    void createComment_shouldReturnBadRequestForBlankText() throws Exception {

        mockMvc.perform(post("/api/cases/1/comments")
                        .contentType("application/json")
                        .content("""
                                {
                                  "text": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getComments_shouldReturnComments() throws Exception {

        Comment comment = new Comment(
                "Customer called again",
                LocalDateTime.now(),
                null
        );

        when(commentService.getCommentsForCase(1L))
                .thenReturn(List.of(comment));

        mockMvc.perform(get("/api/cases/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text")
                        .value("Customer called again"));
    }
}
