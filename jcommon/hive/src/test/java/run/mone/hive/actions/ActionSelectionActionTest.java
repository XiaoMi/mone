
package run.mone.hive.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ActionSelectionActionTest {

    private ActionSelectionAction actionSelectionAction;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LLM llm = new LLM(LLMConfig.builder().build()); // Using default configuration
        actionSelectionAction = new ActionSelectionAction(llm);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException, JsonProcessingException {
        // Prepare test data
        Map<String, String> roleInfo = new HashMap<>();
        roleInfo.put("name", "Developer");
        roleInfo.put("goal", "写一个python程序");
        roleInfo.put("actions", "FixBug,DebugError,RunCode,WritePRD,WriteCode");
        String roleInfoJson = objectMapper.writeValueAsString(roleInfo);

        // Create ActionReq
        ActionReq req = new ActionReq();
        req.setMessage(new Message(roleInfoJson, "user", "test"));

        // Execute run method
        CompletableFuture<Message> future = actionSelectionAction.run(req);
        Message result = future.get();

        // Verify results
        assertNotNull(result);
    }

    @Test
    void testRunWithInvalidJson() {
        // Prepare invalid test data
        String invalidJson = "This is not a valid JSON";

        // Create ActionReq with invalid data
        ActionReq req = new ActionReq();
        req.setMessage(new Message(invalidJson, "user", "test"));

        // Execute run method and expect an exception
        CompletableFuture<Message> future = actionSelectionAction.run(req);
        
        Exception exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Failed to select actions", exception.getCause().getMessage());
    }
}
