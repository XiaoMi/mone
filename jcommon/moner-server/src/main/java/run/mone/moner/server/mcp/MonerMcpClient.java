package run.mone.moner.server.mcp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moner.server.common.GsonUtils;
import run.mone.moner.server.common.Result;
import run.mone.moner.server.common.Safe;

@Slf4j
public class MonerMcpClient {

    // TODO:
    // 1. mcp相关prompt管理与拼接 done
    // 2. 模型配置管理 done to be tested
    // 3. 增加websocketHandler处理来自athena的本地调用 todo
    // 4. 历史消息管理 done
    // 5. chrome athena observer 实现 done

    public static void mcpCall(List<Result> list, FromType from, MutableObject<String> toolResMsg, AtomicBoolean completion) {
        Safe.run(() -> {
            list.forEach(it -> {
                if (it.getTag().equals("use_mcp_tool")) {
                    String serviceName = it.getKeyValuePairs().get("server_name");
                    String toolName = it.getKeyValuePairs().get("tool_name");
                    Map<String, Object> toolArguments = GsonUtils.gson.fromJson(it.getKeyValuePairs().get("arguments"),
                            Map.class);
                    McpSchema.CallToolResult toolRes = McpHubHolder.get(from.getValue()).callTool(serviceName, toolName,
                            toolArguments);
                    log.info("toolRes:{}", toolRes);
                    // 返回信息到前台
                    McpSchema.TextContent textContent = (McpSchema.TextContent) toolRes.content().get(0);
                    toolResMsg.setValue("执行 %s %s\n结果:%s".formatted(serviceName, toolName, textContent));
                }

                if (it.getTag().equals("attempt_completion") || it.getTag().equals("ask_followup_question")) {
                    completion.set(true);
                }
            });
        });
    }

}
