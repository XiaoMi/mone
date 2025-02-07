package run.mone.hive.schema;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class Task {
    private String id;
    private String instruction;
    private String code;
    private String taskType;

    private TaskResult result;
    private boolean success;
    private List<String> dependentTaskIds;
    private boolean finished;

    public Task(String taskId, String instruction, String taskType) {
        this.id = taskId;
        this.instruction = instruction;
        this.taskType = taskType;
        this.code = "";

        this.success = false;
        this.dependentTaskIds = new ArrayList<>();
        this.finished = false;
    }

    public Task(String taskId, String instruction, String taskType, List<String> dependentTaskIds) {
        this(taskId, instruction, taskType);
        this.dependentTaskIds = dependentTaskIds != null ? dependentTaskIds : new ArrayList<>();
    }


    public void updateTaskResult(TaskResult result) {
        this.result = result;
        this.success = result != null && result.isSuccess();
        this.finished = true;
    }

    public String getResult() {

        return result != null ? result.getContent() : "";
    }

    @Override
    public String toString() {
        return String.format("{\"id\": \"%s\", \"instruction\": \"%s\", \"taskType\": \"%s\", \"code\": \"%s\", \"dependentTaskIds\": %s, \"finished\": %b}",
                id, instruction, taskType, code, dependentTaskIds, finished);
    }

    public TaskResult execute(Function<Task, TaskResult> function) {
        return function.apply(this);
    }
}

 