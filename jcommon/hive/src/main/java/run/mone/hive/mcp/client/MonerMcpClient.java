package run.mone.hive.mcp.client;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import run.mone.hive.common.*;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.hub.McpHubHolder;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class MonerMcpClient {

    public static McpResult mcpCall(ToolDataInfo toolDataInfo, String from, MonerMcpInterceptor monerMcpInterceptor, FluxSink sink, Function<String, McpFunction> f) {
        return Safe.call(() -> {
            String serviceName = toolDataInfo.getKeyValuePairs().get("server_name");
            String toolName = toolDataInfo.getKeyValuePairs().get("tool_name");
            String arguments = toolDataInfo.getKeyValuePairs().get("arguments");
            Map<String, Object> toolArguments = GsonUtils.gson.fromJson(arguments, Map.class);

            if (StringUtils.isNotEmpty(toolDataInfo.getFrom())) {
                toolArguments.put(Constants.FROM, toolDataInfo.getFrom());
            }

            toolArguments.put(Const.USER_ID, toolDataInfo.getUserId());
            toolArguments.put(Const.AGENT_ID, toolDataInfo.getAgentId());
            if (null != toolDataInfo.getRole()) {
                toolArguments.put(Const.ROLE, toolDataInfo.getRole());
            }

            // 调用before方法并检查返回值
            boolean shouldProceed = monerMcpInterceptor.before(toolName, toolArguments);
            if (shouldProceed) {
                McpSchema.CallToolResult toolRes = null;
                //流式调用
                if (toolName.startsWith("stream")) {
                    StringBuilder sb = new StringBuilder();
                    //内部调用(直接调用本地的mcp)
                    if (serviceName.equals(Const.INTERNAL_SERVER)) {
                        log.info("call internal mcp:{}", toolName);
                        McpFunction function = f.apply(toolName);
                        Flux<McpSchema.CallToolResult> flux = function.apply(toolArguments);
                        flux.doOnNext(tr -> Optional.ofNullable(sink).ifPresent(s -> {
                            if (tr.content().get(0) instanceof McpSchema.TextContent tc) {
                                //直接返回给前端
                                s.next(tc.text());
                                sb.append(tc.text());
                            }
                        })).blockLast();
                    } else {
                        //外部的mcp
                        Safe.run(() -> {
                            McpHubHolder.get(from)
                                    .callToolStream(serviceName, toolName, toolArguments)
                                    .doOnNext(tr -> Optional.ofNullable(sink).ifPresent(s -> {
                                        if (tr.content().get(0) instanceof McpSchema.TextContent tc) {
                                            //直接返回给前端
                                            s.next(tc.text());
                                            sb.append(tc.text());
                                        }
                                    }))
                                    .doOnError(ex -> {
                                        sb.append(ex.getMessage());
                                        sink.next(ex.getMessage());
                                    })
                                    .blockLast();
                        });
                    }

                    log.debug("res:{}", sb);
                    toolRes = new McpSchema.CallToolResult(Lists.newArrayList(new McpSchema.TextContent(sb.toString())), false);
                } else {
                    // 只有当before返回true时才调用工具
                    McpHub mcpHub = McpHubHolder.get(from);
                    if (null == mcpHub) {
                        return McpResult.builder().toolName(toolName).content(new McpSchema.TextContent("mcpHub is null:" + from)).build();
                    }

                    toolRes = mcpHub.callTool(serviceName, toolName,
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