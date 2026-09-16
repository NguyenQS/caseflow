package com.example.caseflow.repository;

import com.example.caseflow.model.Case;
import com.example.caseflow.model.CaseStatus;
import com.example.caseflow.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@Transactional
class CommentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("caseflow-test")
                    .withUsername("caseflow")
                    .withPassword("caseflow");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldSaveAndFindCommentForCase() {

        Case caseEntity = new Case(
                "Address change",
                "Customer reported a new address",
                CaseStatus.OPEN,
                LocalDateTime.now()
        );

        Case savedCase = caseRepository.save(caseEntity);

        Comment comment = new Comment(
                "Customer called again",
                LocalDateTime.now(),
                savedCase
        );

        commentRepository.save(comment);

        List<Comment> result =
                commentRepository.findByCaseEntityId(savedCase.getId());

        assertEquals(1, result.size());
        assertEquals(
                "Customer called again",
                result.get(0).getText()
        );
    }
}
