package run.mone.mcp.coder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CoderServiceTest {

    @Autowired
    private CoderService coderService;

    @Autowired
    private LLM llm;

    @BeforeEach
    void setUp() {
        assertNotNull(coderService);
        assertNotNull(llm);
    }

    @Test
    void testAnswerTechQuestion() {
        String question = "What is dependency injection in Spring?";
        String answer = coderService.answerTechQuestion(question);
        assertNotNull(answer);
    }

    @Test
    void testWriteCode() {
        String description = "Write a Java function to calculate the factorial of a number";
        String generatedCode = coderService.writeCode(description);

        assertNotNull(generatedCode);
    }


}