
package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class WritePlanTest {

    private WritePlan writePlan;

    @BeforeEach
    void setUp() {
        writePlan = new WritePlan();
        writePlan.setLlm(new BaseLLM(LLMConfig.builder().json(true).build()));
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException {
        // Prepare test data
        List<Message> context = new ArrayList<>();
        context.add(new Message("Create a simple web application for user registration and login", "user"));
        int maxTasks = 5;

        // Execute the method
        CompletableFuture<Message> future = writePlan.run(context, maxTasks);

        // Wait for the result and verify
        Message result = future.get();

        System.out.println(result);

        assertNotNull(result);

    }
}
