package run.mone.hive.mcp.client;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.McpResult;
import run.mone.hive.common.Result;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class MonerMcpClient {


    public static McpResult mcpCall(Result it, String from, MonerMcpInterceptor monerMcpInterceptor, FluxSink sink) {
        return Safe.call(() -> {
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
                            .doOnComplete(() -> Optional.ofNullable(sink).ifPresent(FluxSink::complete))
                            .blockLast();
                    toolRes = new McpSchema.CallToolResult(Lists.newArrayList(new McpSchema.TextContent(sb.toString())), false);
                } else {
                    // 只有当before返回true时才调用工具
                    toolRes = McpHubHolder.get(from).callTool(serviceName, toolName,
                            toolArguments);
                }
                monerMcpInterceptor.after(toolName, toolRes);
                return McpResult.builder().toolName(toolName).content(toolRes.content().get(0)).build();
            } else {
                log.info("工具 {} 执行被拦截，不执行实际调用", toolName);
                McpSchema.TextContent textContent = new McpSchema.TextContent("操作已取消，可以结束此轮操作。");
                return McpResult.builder().toolName(toolName).content(textContent).build();
            }
        });
    }


}