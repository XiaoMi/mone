
package run.mone.hive.actions.python;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FixPythonBugTest {

    private FixPythonBug fixPythonBug;
    private LLM llm;

    @BeforeEach
    void setUp() {
        llm = new LLM(LLMConfig.builder().json(false).build());
        fixPythonBug = new FixPythonBug();
        fixPythonBug.setLlm(llm);
    }

    @Test
    void testFixBugWithSyntaxError() throws ExecutionException, InterruptedException {
        String buggyCode = """
                def add_numbers(a, b)
                    return a + b
                
                result = add_numbers(3, 4)
                print(result)
                """;

        ActionReq req = new ActionReq();
        req.setMessage(new Message(buggyCode));

        CompletableFuture<Message> future = fixPythonBug.run(req);
        Message result = future.get();

        assertNotNull(result);
    }

    @Test
    void testFixBugWithLogicError() throws ExecutionException, InterruptedException {
        String buggyCode = """
                def divide_numbers(a, b):
                    return a - b  # Bug: subtraction instead of division
                
                result = divide_numbers(10, 2)
                print(result)
                """;

        JsonObject jo = new JsonObject();
        jo.addProperty("code",buggyCode);
        jo.addProperty("error","计算出来的数字不对,帮我看看问题");

        ActionReq req = new ActionReq();
        req.setMessage(new Message(jo.toString()));
        req.setRole(Role.builder().name("user").build());

        CompletableFuture<Message> future = fixPythonBug.run(req);
        Message result = future.get();

        assertNotNull(result);
        assertTrue(result.getContent().contains("return a / b"));
    }

    @Test
    void testFixBugWithNoError() throws ExecutionException, InterruptedException {
        String correctCode = """
                def multiply_numbers(a, b):
                    return a * b
                
                result = multiply_numbers(3, 4)
                print(result)
                """;

        ActionReq req = new ActionReq();
        req.setMessage(new Message(correctCode));

        CompletableFuture<Message> future = fixPythonBug.run(req);
        Message result = future.get();

        assertNotNull(result);
        assertEquals(correctCode, result.getContent());
    }

    // ... existing code ...
}
