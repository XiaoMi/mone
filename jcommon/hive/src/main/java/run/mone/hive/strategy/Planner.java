package run.mone.hive.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.AskReview;
import run.mone.hive.actions.WritePlan;
import run.mone.hive.llm.LLM;
import run.mone.hive.memory.Memory;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.Plan;
import run.mone.hive.schema.Task;
import run.mone.hive.schema.TaskResult;
import run.mone.hive.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@Slf4j
public class Planner {

    private static final String STRUCTURAL_CONTEXT = """
            ## User Requirement
            %s
            ## Context
            %s
            ## Current Plan
            %s
            ## Current Task
            %s
            """;

    private static final String PLAN_STATUS = """
            ## Finished Tasks
            ### code
            ```python
            %s
            ```
            
            ### execution result
            %s
            
            ## Current Task
            %s
            
            ## Task Guidance
            Write complete code for 'Current Task'. And avoid duplicating code from 'Finished Tasks', such as repeated import of packages, reading data, etc.
            Specifically, %s
            """;

    private Plan plan;
    private Memory workingMemory;
    private boolean autoRun;
    private LLM llm;

    public Planner(String goal) {
        this(goal, null);
    }

    public Planner(String goal, Plan plan) {
        this.plan = plan != null ? plan : new Plan(goal);
        this.workingMemory = new Memory();
        this.autoRun = false;
    }

    public Task getCurrentTask() {
        return plan.getCurrentTask();
    }

    public String getCurrentTaskId() {
        return plan.getCurrentTaskId();
    }

    public CompletableFuture<Void> updatePlan(String goal, int maxTasks, int _maxRetries) {
        return CompletableFuture.runAsync(() -> {
            int maxRetries = _maxRetries;
            if (goal != null && !goal.isEmpty()) {
                this.plan = new Plan(goal);
            }

            boolean planConfirmed = false;
            while (!planConfirmed && maxRetries > 0) {
                try {
                    List<Message> context = getUsefulMemories();
                    String rsp = new WritePlan(this.llm).run(context, maxTasks).join().getContent();
                    workingMemory.add(new Message(rsp, "assistant", WritePlan.class.getName()));

                    // precheck plan before asking reviews
                    boolean[] result = preCheckUpdatePlanFromRsp(rsp, plan);
                    boolean isPlanValid = result[0];
                    if (!isPlanValid && maxRetries > 0) {
                        String errorMsg = "The generated plan is not valid, try regenerating";
                        log.warn(errorMsg);
                        workingMemory.add(new Message(errorMsg, "assistant", WritePlan.class.getName()));
                        maxRetries--;
                        continue;
                    }

                    Message[] reviewResult = askReview(null, true, "TASK_REVIEW").join();
                    planConfirmed = Boolean.parseBoolean(reviewResult[1].getContent());

                    if (planConfirmed) {
                        updatePlanFromRsp(rsp, plan);

                    }
                } catch (Exception e) {
                    log.error("Error updating plan", e);
                    maxRetries--;
                }
            }
            workingMemory.clear();
        });
    }

    public CompletableFuture<Void> processTaskResult(TaskResult taskResult) {
        return CompletableFuture.runAsync(() -> {
            try {
                Message[] reviewResult = askReview(taskResult).join();
                String review = reviewResult[0].getContent();
                boolean confirmed = Boolean.parseBoolean(reviewResult[1].getContent());

                if (confirmed) {
                    confirmTask(getCurrentTask(), taskResult, review).join();
                } else if (review.contains("redo")) {
                    // Simply pass, not confirming the result
                } else {
                    updatePlan(null, 3, 3).join();
                }
            } catch (Exception e) {
                log.error("Error processing task result", e);
            }
        });
    }

    public CompletableFuture<Message[]> askReview(TaskResult taskResult) {
        return askReview(taskResult, null, "TASK_REVIEW");
    }

    public CompletableFuture<Message[]> askReview(TaskResult taskResult, Boolean autoRun, String trigger) {
        return CompletableFuture.supplyAsync(() -> {
            boolean shouldAutoRun = autoRun != null ? autoRun : this.autoRun;
            if (!shouldAutoRun) {
                List<Message> context = getUsefulMemories();
                try {
                    Message[] result = new AskReview().run(context, plan, trigger).join();
                    if (!Boolean.parseBoolean(result[1].getContent())) {
                        workingMemory.add(new Message(result[0].getContent(), "user", AskReview.class.getName()));
                    }
                    return result;
                } catch (Exception e) {
                    log.error("Error asking for review", e);
                    throw new RuntimeException(e);
                }
            }
            boolean confirmed = taskResult != null ? taskResult.isSuccess() : true;
            return new Message[]{new Message("", "system"), new Message(String.valueOf(confirmed), "system")};
        });
    }

    private CompletableFuture<Void> confirmTask(Task task, TaskResult taskResult, String review) {
        return CompletableFuture.runAsync(() -> {
            task.updateTaskResult(taskResult);
            plan.finishCurrentTask();
            workingMemory.clear();

            boolean confirmedAndMore = review.toLowerCase().contains("confirm") &&
                    !review.toLowerCase().equals("confirm");
            if (confirmedAndMore) {
                workingMemory.add(new Message(review, "user", AskReview.class.getName()));
                updatePlan(null, 3, 3).join();
            }
        });
    }

    public List<Message> getUsefulMemories() {
        ObjectMapper mapper = new ObjectMapper();
        String userRequirement = plan.getGoal();
        String context = plan.getContext();
        List<Task> tasks = plan.getTasks();
        String tasksJson = "";
        try {
            tasksJson = mapper.writeValueAsString(tasks);
        } catch (Exception e) {
            log.error("Error serializing tasks", e);
        }
        String currentTask = plan.getCurrentTask() != null ? plan.getCurrentTask().toString() : "{}";

        String contextStr = String.format(STRUCTURAL_CONTEXT,
                userRequirement, context, tasksJson, currentTask);
        List<Message> contextMsg = new ArrayList<>();
        contextMsg.add(new Message(contextStr, "user"));
        contextMsg.addAll(workingMemory.get());

        return contextMsg;
    }

    public String getPlanStatus() {
        List<Task> finishedTasks = plan.getFinishedTasks();
        String codeWritten = finishedTasks.stream()
                .map(task -> CommonUtils.removeComments(task.getCode()))
                .collect(Collectors.joining("\n\n"));

        String taskResults = finishedTasks.stream()
                .map(Task::getResult)
                .collect(Collectors.joining("\n\n"));

        String taskTypeName = getCurrentTask().getTaskType();
        TaskType taskType = TaskType.getType(taskTypeName);
        String guidance = taskType != null ? taskType.getGuidance() : "";

        return String.format(PLAN_STATUS,
                codeWritten,
                taskResults,
                getCurrentTask().getInstruction(),
                guidance);
    }

    private boolean[] preCheckUpdatePlanFromRsp(String response, Plan plan) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var jsonNode = mapper.readTree(response);
            var tasksNode = jsonNode.get("tasks");
            return new boolean[]{tasksNode != null && tasksNode.isArray() && tasksNode.size() > 0, false};
        } catch (Exception e) {
            log.error("Error parsing plan response", e);
            return new boolean[]{false, false};
        }
    }

    private void updatePlanFromRsp(String response, Plan plan) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var jsonNode = mapper.readTree(response);
            var tasksNode = jsonNode.get("tasks");

            final List<Task> tasks = new ArrayList<>();
            tasksNode.forEach(taskNode -> {
                String id = taskNode.get("task_id").asText();
                String instruction = taskNode.get("instruction").asText();
                String taskType = taskNode.get("task_type").asText();
                List<String> dependentIds = mapper.convertValue(taskNode.get("dependent_task_ids"), new TypeReference<>() {
                });
                tasks.add(new Task(id, instruction, taskType, dependentIds));
            });
            plan.setTasks(tasks);
            plan.topologicalSort();
        } catch (Exception e) {
            log.error("Error updating plan from response", e);
            throw new RuntimeException("Failed to update plan", e);
        }
    }
}