
package run.mone.hive.actions.python;

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

class WritePythonCodeTest {

    private WritePythonCode writePythonCode;
    private LLM llm;

    @BeforeEach
    void setUp() {
        llm = new LLM(LLMConfig.builder().json(false).build());
        writePythonCode = new WritePythonCode();
        writePythonCode.setLlm(llm);
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException {
        ActionReq req = new ActionReq();
//        String msg = "Calculate the sum of two numbers";
        String msg = "计算一个excel有多少个工作区";
        req.setMessage(new Message(msg));
        req.setRole(Role.builder().name("user").build());

        CompletableFuture<Message> future = writePythonCode.run(req, );
        Message result = future.get();

        assertNotNull(result);
    }

    @Test
    void testRunWithComplexRequirement() throws ExecutionException, InterruptedException {
        ActionReq req = new ActionReq();
        req.setMessage(new Message("Create a function that calculates the factorial of a number"));

        CompletableFuture<Message> future = writePythonCode.run(req, );
        Message result = future.get();

        assertNotNull(result);
        assertTrue(result.getContent().contains("def execute(params):"));
        assertTrue(result.getContent().contains("factorial"));
        assertTrue(result.getContent().contains("return"));
    }
}
