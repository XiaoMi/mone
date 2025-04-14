package run.mone.hive.mcp.client;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableObject;
import reactor.core.publisher.FluxSink;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.McpResult;
import run.mone.hive.common.Result;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MonerMcpClient {

    public static void mcpCall( List<Result> list, String from, MutableObject<McpResult> callToolResult, AtomicBoolean completion, MonerMcpInterceptor monerMcpInterceptor, FluxSink sink) {
        Safe.run(() -> {
            for (Result it : list) {
                if (it.getTag().equals("use_mcp_tool")) {
                    String serviceName = it.getKeyValuePairs().get("server_name");
                    String toolName = it.getKeyValuePairs().get("tool_name");
                    String arguments = it.getKeyValuePairs().get("arguments");
                    Map<String, Object> toolArguments = GsonUtils.gson.fromJson(arguments, Map.class);

                    // 调用before方法并检查返回值
                    boolean shouldProceed = monerMcpInterceptor.before(toolName, toolArguments);

                    if (shouldProceed) {

                        McpSchema.CallToolResult toolRes = null;

                        //流式调用
                        if (toolName.startsWith("stream")) {
                            StringBuilder sb = new StringBuilder();
                            McpHubHolder.get(from)
                                    .callToolStream(serviceName, toolName, toolArguments)
                                    .doOnNext(tr -> Optional.ofNullable(sink).ifPresent(s -> {
                                        if (tr.content().get(0) instanceof McpSchema.TextContent tc) {
                                            //直接返回给前端
                                            s.next(tc.text());
                                            sb.append(tc.text());
                                        }
                                    }))
                                    .blockLast();
                            toolRes = new McpSchema.CallToolResult(Lists.newArrayList(new McpSchema.TextContent(sb.toString())), false);
                        } else {
                            // 只有当before返回true时才调用工具
                            toolRes = McpHubHolder.get(from).callTool(serviceName, toolName,
                                    toolArguments);

                        }


                        monerMcpInterceptor.after(toolName, toolRes);

                        log.info("执行{} 的 工具{}, toolRes:{}", serviceName, toolName, toolRes);
                        callToolResult.setValue(McpResult.builder().toolName(toolName).content(toolRes.content().get(0)).build());
                    } else {
                        // 如果before返回false，构造一个文本内容的结果
                        log.info("工具 {} 执行被拦截，不执行实际调用", toolName);
                        McpSchema.TextContent textContent = new McpSchema.TextContent("操作已取消，可以结束此轮操作。");
                        callToolResult.setValue(McpResult.builder().toolName(toolName).content(textContent).build());
                    }
                    // 确保只执行一个use_mcp_tool
                    return;
                } else if (it.getTag().equals("attempt_completion") || it.getTag().equals("ask_followup_question") || it.getTag().equals("chat")) {
                    completion.set(true);
                    return;
                }
            }
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

    private static String cleanJsonString(String json) {
        if (json == null) {
            return "{}";
        }

        // 移除所有控制字符
        json = json.replaceAll("[\\x00-\\x1F\\x7F]", "");

        // 移除零宽字符
        json = json.replaceAll("[\\u200B-\\u200D\\uFEFF]", "");

        // 移除其他可能导致问题的特殊字符
        json = json.replaceAll("[\\u2028\\u2029]", "");

        // 确保字符串是有效的 JSON 格式
        if (!json.trim().startsWith("{")) {
            json = "{" + json;
        }
        if (!json.trim().endsWith("}")) {
            json = json + "}";
        }

        return json;
    }

}