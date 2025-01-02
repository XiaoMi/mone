package run.mone.hive.actions;

import run.mone.hive.schema.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class WritePRD extends Action {

    public WritePRD() {
        super("WritePRD", "Write Product Requirement Document");
    }

    @Override
    public CompletableFuture<Message> run() {
        return null;
    }

    @Override
    public CompletableFuture<Message> run(List<Message> history) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prd = generatePRD(history);
                return Message.builder()
                    .content(prd)
                    .role("WritePRD")
                    .causeBy(this.getClass().getName())
                    .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate PRD", e);
            }
        });
    }

    private String generatePRD(List<Message> history) {
        // 使用LLM生成PRD文档
        return "# Product Requirements Document\n...";
    }
} 