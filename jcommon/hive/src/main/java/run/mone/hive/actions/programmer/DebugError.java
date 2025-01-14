
package run.mone.hive.actions.programmer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RunCodeContext;

import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class DebugError extends Action {
    private RunCodeContext context;

    public DebugError() {
        super("DebugError", "Debug the code and provide error analysis");
    }

    public DebugError(RunCodeContext context) {
        this();
        this.context = context;
    }

    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String debugResult = analyzeAndDebug(this.context.getCode(), this.context.getTestCode(), this.context.getExecutionResult());
                return Message.builder()
                    .content(debugResult)
                    .role("DebugError")
                    .causeBy(this.getClass().getName())
                    .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to debug error", e);
            }
        });
    }

    private String analyzeAndDebug(String code, String testCode, String executionResult) {
        // 这里应该实现实际的调试和错误分析逻辑
        // 可以使用静态代码分析工具或其他方法来分析代码
        // 为了示例，我们只返回一个模拟的调试结果
        return "Debug analysis result:\n" +
               "1. In function_xyz, there's a potential off-by-one error in the loop condition.\n" +
               "2. The test case 'test_function_xyz' is failing due to this error.\n" +
               "3. Suggested fix: Change 'for (int i = 0; i < n; i++)' to 'for (int i = 0; i <= n; i++)'.\n" +
               "4. After applying the fix, re-run the tests to confirm the issue is resolved.";
    }
}
