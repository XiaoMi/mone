package run.mone.hive.actions.programmer;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class WriteTest extends Action {

    public WriteTest() {
        super("WriteTest", "Write test cases for the code");
    }


    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            List<Message> history = (List<Message>) map.getHistory();
            try {
                String testCode = generateTestCode(history);
                return Message.builder()
                        .content(testCode)
                        .role("WriteTest")
                        .causeBy(this.getClass().getName())
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate test code", e);
            }
        });
    }

    private String generateTestCode(List<Message> history) {
        // 使用LLM生成测试代码
        return "# Test Cases\n...";
    }
} 