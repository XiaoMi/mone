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

    public static void mcpCall(List<Result> list, FromType from, MutableObject<Content> callToolResult, AtomicBoolean completion) {
        Safe.run(() -> {
            list.forEach(it -> {
                if (it.getTag().equals("use_mcp_tool")) {
                    String serviceName = it.getKeyValuePairs().get("server_name");
                    String toolName = it.getKeyValuePairs().get("tool_name");
                    Map<String, Object> toolArguments = GsonUtils.gson.fromJson(it.getKeyValuePairs().get("arguments"),
                            Map.class);
                    McpSchema.CallToolResult toolRes = McpHubHolder.get(from.getValue()).callTool(serviceName, toolName,
                            toolArguments);
                    log.info("执行{} 的 工具{}, toolRes:{}", serviceName, toolName, toolRes);
                    
                    // toolResMsg.setValue("执行 %s %s\n结果:%s".formatted(
                    //     serviceName, toolName, resultBuilder.toString().trim()));

                    callToolResult.setValue(toolRes.content().get(0));
                }

                if (it.getTag().equals("attempt_completion") || it.getTag().equals("ask_followup_question")) {
                    completion.set(true);
                }
            });
        });
    }

    public static String parseContent(McpSchema.Content content) {
        if (content instanceof McpSchema.TextContent) {
            return ((McpSchema.TextContent) content).text();
        } else if (content instanceof McpSchema.ImageContent) {
            McpSchema.ImageContent imageContent = (McpSchema.ImageContent) content;
            return imageContent.data();
        } 
        return "不支持的内容类型";
    }

}
