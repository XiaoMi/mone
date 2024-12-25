package run.mone.hive.schema;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class Plan {

    private String goal;
    private String context;
    private List<Task> tasks;
    private Map<String, Task> taskMap;

    private int currentTaskIndex;

    private String currentTaskId;

    public Plan(String goal) {
        this.goal = goal;
        this.context = "";
        this.tasks = new ArrayList<>();
        this.taskMap = new HashMap<>();
        this.currentTaskIndex = -1;
    }


    public Task getCurrentTask() {
        if (currentTaskIndex >= 0 && currentTaskIndex < tasks.size()) {
            return tasks.get(currentTaskIndex);
        }
        return null;
    }

    public String getCurrentTaskId() {
        Task currentTask = getCurrentTask();
        return currentTask != null ? currentTask.getId() : null;
    }

    public void finishCurrentTask() {
        currentTaskIndex++;
    }

    public List<Task> getFinishedTasks() {
        if (currentTaskIndex <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tasks.subList(0, currentTaskIndex));
    }

    public void addTask(Task task) {
        tasks.add(task);
        taskMap.put(task.getId(), task);
        if (currentTaskIndex == -1) {
            currentTaskIndex = 0;
        }
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        this.taskMap = new HashMap<>();
        for (Task task : tasks) {
            taskMap.put(task.getId(), task);
        }
        if (!tasks.isEmpty() && currentTaskIndex == -1) {
            currentTaskIndex = 0;
        }
    }

    public List<Task> topologicalSort() {
        List<Task> sortedTasks = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (Task task : tasks) {
            visit(task.getId(), sortedTasks, visited);
        }

        this.tasks = sortedTasks;
        return sortedTasks;
    }

    private void visit(String taskId, List<Task> sortedTasks, Set<String> visited) {
        if (visited.contains(taskId)) {
            return;
        }
        visited.add(taskId);

        Task task = taskMap.get(taskId);
        if (task != null) {
            for (String dependentId : task.getDependentTaskIds()) {
                visit(dependentId, sortedTasks, visited);
            }
            sortedTasks.add(task);
        }
    }


    public boolean isCompleted() {
        return currentTaskIndex >= tasks.size();
    }

    public void updateCurrentTask() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!task.isFinished()) {
                currentTaskIndex = i;
                currentTaskId = task.getId();
                return;
            }
        }
        currentTaskIndex = tasks.size(); // all tasks finished
    }

    public Task currentTask() {
        return this.taskMap.get(this.currentTaskId);
    }

    public void reset() {
        currentTaskIndex = tasks.isEmpty() ? -1 : 0;
    }
}
 