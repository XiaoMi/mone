package run.mone.hive.schema;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class TaskResult {
    private String content;
    private boolean success;

    public TaskResult(String content, boolean success) {
        this.content = content;
        this.success = success;
    }
} 