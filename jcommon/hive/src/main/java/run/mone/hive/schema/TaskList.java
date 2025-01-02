package run.mone.hive.schema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a list of tasks to be completed
 */
@Data
@NoArgsConstructor
public class TaskList {
    public static final String KEY = "tasks";
    
    @JsonProperty(KEY)
    private List<String> tasks;
    
    private String status;
    private String priority;
    private String assignee;

    public TaskList(List<String> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    /**
     * Add a new task to the list
     */
    public void addTask(String task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    /**
     * Remove a task from the list
     */
    public void removeTask(String task) {
        if (tasks != null) {
            tasks.remove(task);
        }
    }

    /**
     * Check if the task list is empty
     */
    public boolean isEmpty() {
        return tasks == null || tasks.isEmpty();
    }

    /**
     * Get the number of tasks
     */
    public int size() {
        return tasks != null ? tasks.size() : 0;
    }

    /**
     * Clear all tasks
     */
    public void clear() {
        if (tasks != null) {
            tasks.clear();
        }
    }

    /**
     * Get tasks as array
     */
    public String[] toArray() {
        return tasks != null ? tasks.toArray(new String[0]) : new String[0];
    }

    @Override
    public String toString() {
        return "TaskList{" +
                "tasks=" + tasks +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", assignee='" + assignee + '\'' +
                '}';
    }
} 