package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class WritePRD extends Action {

    public WritePRD() {
        super("WritePRD", "Write Product Requirement Document");
    }


    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Message> history = map.getHistory();
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