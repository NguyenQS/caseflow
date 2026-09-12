package com.example.caseflow.controller;

import com.example.caseflow.service.CaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CaseController.class)
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CaseService caseService;

    @Test
    void getCases_shouldReturnOk() throws Exception {
        when(caseService.getCases(
                Optional.empty(),
                Optional.empty()
        )).thenReturn(List.of());

        mockMvc.perform(get("/api/cases"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createCase_withBlankTitle_shouldReturnBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "",
                  "description": "Test description"
                }
                """;

        mockMvc.perform(
                        post("/api/cases")
                                .contentType("application/json")
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }
}