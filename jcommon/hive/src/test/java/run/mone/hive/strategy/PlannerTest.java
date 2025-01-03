
package run.mone.hive.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.Plan;
import run.mone.hive.schema.Task;
import run.mone.hive.schema.TaskResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class PlannerTest {

    private Planner planner;
    private static final String TEST_GOAL = "Write a simple Hello World program";

    @BeforeEach
    void setUp() {
        planner = new Planner(TEST_GOAL);
        planner.setLlm(new LLM(LLMConfig.builder().json(true).build()));
    }

    @Test
    void testPlannerInitialization() {
        assertNotNull(planner);
        assertEquals(TEST_GOAL, planner.getPlan().getGoal());
    }

    @Test
    void testGetCurrentTask() {
        Task currentTask = planner.getCurrentTask();
        assertNull(currentTask); // Initially, there should be no current task
    }

    @Test
    void testUpdatePlan() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = planner.updatePlan(TEST_GOAL, 3, 3);
        future.get(); // Wait for the future to complete

        Plan updatedPlan = planner.getPlan();
        assertNotNull(updatedPlan);
        assertFalse(updatedPlan.getTasks().isEmpty());
    }

    @Test
    void testProcessTaskResult() throws ExecutionException, InterruptedException {
        // First, update the plan to have some tasks
        planner.updatePlan(TEST_GOAL, 3, 3).get();

        // Create a sample TaskResult
        TaskResult taskResult = new TaskResult("Task completed successfully", true);

        // Process the task result
        CompletableFuture<Void> future = planner.processTaskResult(taskResult);
        future.get(); // Wait for the future to complete

        // Check if the current task has been updated
        Task currentTask = planner.getCurrentTask();
        assertNotNull(currentTask);
    }

    @Test
    void testGetUsefulMemories() {
        List<Message> memories = planner.getUsefulMemories();
        assertFalse(memories.isEmpty());
        assertTrue(memories.get(0).getContent().contains(TEST_GOAL));
    }

    @Test
    void testGetPlanStatus() {
        String planStatus = planner.getPlanStatus();
        assertNotNull(planStatus);
        assertTrue(planStatus.contains("Finished Tasks"));
        assertTrue(planStatus.contains("Current Task"));
    }
}
