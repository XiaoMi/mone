
package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionNodeTest {

    private ActionNode actionNode;
    private BaseLLM mockLLM;

    @BeforeEach
    void setUp() {
        mockLLM = mock(BaseLLM.class);
        actionNode = ActionNode.builder()
                .key("testNode")
                .instruction("Test instruction")
                .llm(mockLLM)
                .build();
    }

    @Test
    public void testRun() {
        ActionNode an = new ActionNode();
        an.setLlm(new BaseLLM(LLMConfig.builder().debug(false).build()));
        an.setContext("11+22=?");
        Message msg = an.run().join();
        System.out.println(msg);
    }

    @Test
    void testRunSuccessfully() {
        // Arrange
        String mockResponse = "[CONTENT]Test content[/CONTENT]";
        when(mockLLM.ask(anyString())).thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act
        CompletableFuture<Message> result = actionNode.run();

        // Assert
        assertNotNull(result);
        Message message = assertDoesNotThrow(() -> result.get());
        assertEquals("Test content", message.getContent());
        assertEquals("ActionNode", message.getRole());
        assertEquals(ActionNode.class.getName(), message.getCauseBy());
    }

    @Test
    void testRunWithEmptyContent() {
        // Arrange
        String mockResponse = "[CONTENT][/CONTENT]";
        when(mockLLM.ask(anyString())).thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act & Assert
        CompletableFuture<Message> result = actionNode.run();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> result.get());
        assertTrue(exception.getMessage().contains("Empty content"));
    }

    @Test
    void testRunWithInvalidFormat() {
        // Arrange
        String mockResponse = "Invalid format response";
        when(mockLLM.ask(anyString())).thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act & Assert
        CompletableFuture<Message> result = actionNode.run();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> result.get());
        assertTrue(exception.getMessage().contains("Content format error"));
    }

    // ... other methods ...
}
