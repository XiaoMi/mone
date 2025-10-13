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
import run.mone.hive.roles.ReactorRole;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j
public class MonerMcpClient {

    public static McpResult mcpCall(ReactorRole role, ToolDataInfo toolDataInfo, String from, MonerMcpInterceptor monerMcpInterceptor, FluxSink sink, Function<String, McpFunction> f) {
        return Safe.call(() -> {
            String serviceName = toolDataInfo.getKeyValuePairs().get("server_name");
            String toolName = toolDataInfo.getKeyValuePairs().get("tool_name");
            String arguments = toolDataInfo.getKeyValuePairs().get("arguments");
            String internalName = toolDataInfo.getKeyValuePairs().getOrDefault(Const.USER_INTERNAL_NAME, "");
            if (StringUtils.isEmpty(internalName) && role != null) {
                internalName = role.getRoleConfig().getOrDefault(Const.USER_INTERNAL_NAME, "");
            }

            Map<String, Object> toolArguments = GsonUtils.gson.fromJson(arguments, Map.class);
            if (StringUtils.isNotEmpty(toolDataInfo.getFrom())) {
                toolArguments.put(Constants.FROM, toolDataInfo.getFrom());
            }
            toolArguments.put(Const.USER_ID, toolDataInfo.getUserId());
            toolArguments.put(Const.USER_INTERNAL_NAME, internalName);
            toolArguments.put(Const.AGENT_ID, toolDataInfo.getAgentId());
            Safe.run(() -> {
                toolArguments.put(Const.OWNER_ID, toolDataInfo.getUserId()+"_"+toolDataInfo.getAgentId());
            });
            if (null != toolDataInfo.getRole()) {
                toolArguments.put(Const.ROLE, toolDataInfo.getRole());
            }

            AtomicBoolean error = new AtomicBoolean(false);
            // 调用before方法并检查返回值
            boolean shouldProceed = monerMcpInterceptor.before(toolName, toolArguments);
            if (shouldProceed) {
                McpSchema.CallToolResult toolRes = null;
                //流式调用
                if (toolName.startsWith("stream")) {
                    StringBuilder sb = new StringBuilder();
                    //内部调用(直接调用本地的mcp)
                    if (serviceName.equals(Const.INTERNAL_SERVER)) {
                        log.info("call internal mcp:{} type:{}", toolName, "stream");
                        internalCall(sink, f, toolName, toolArguments, sb, error);
                    } else {
                        //外部的mcp
                        call(role, from, sink, serviceName, toolName, toolArguments, sb, error);
                    }
                    log.debug("res:{}", sb);
                    toolRes = new McpSchema.CallToolResult(Lists.newArrayList(new McpSchema.TextContent(sb.toString())), false);
                } else { //非流式调用
                    if (serviceName.equals(Const.INTERNAL_SERVER)) {
                        log.info("call internal mcp:{} type:{}", toolName, "internal");
                        McpFunction function = f.apply(toolName);
                        Flux<McpSchema.CallToolResult> flux = function.apply(toolArguments);
                        String c = function.formatResult(((McpSchema.TextContent) flux.blockLast().content().get(0)).text());
                        return McpResult.builder().toolName(toolName).content(new McpSchema.TextContent(c)).error(error.get()).build();
                    }
                    // 只有当before返回true时才调用工具
                    McpHub hub = null;
                    if (null != role) {
                        //调用绑定在这个用户的mcp
                        hub = role.getMcpHub();
                    } else {
                        //主要用来调用chat
                        hub = McpHubHolder.get(from);
                    }
                    if (null == hub) {
                        return McpResult.builder().toolName(toolName).content(new McpSchema.TextContent("mcpHub is null:" + from)).build();
                    }
                    try {
                        toolRes = hub.callTool(serviceName, toolName,
                                toolArguments);
                    } catch (Throwable ex) {
                        log.error(ex.getMessage(),ex);
                        error.set(true);
                    }
                }
                monerMcpInterceptor.after(toolName, toolRes);
                return McpResult.builder().toolName(toolName).content(toolRes.content().get(0)).error(error.get()).build();
            } else {
                log.info("工具 {} 执行被拦截，不执行实际调用", toolName);
                McpSchema.TextContent textContent = new McpSchema.TextContent("操作已取消，可以结束此轮操作。");
                return McpResult.builder().toolName(toolName).content(textContent).build();
            }
        });
    }

    private static void call(ReactorRole role, String from, FluxSink sink, String serviceName, String toolName, Map<String, Object> toolArguments, StringBuilder sb, AtomicBoolean error) {
        Safe.run(() -> {
            McpHub hub = null;
            if (null != role) {
                //调用绑定在这个用户的mcp
                hub = role.getMcpHub();
            } else {
                //主要用来调用chat
                hub = McpHubHolder.get(from);
            }
            hub.callToolStream(serviceName, toolName, toolArguments)
                    .doOnNext(tr -> {
                                if (tr.content().get(0) instanceof McpSchema.TextContent tc) {
                                    Optional.ofNullable(sink).ifPresent(s -> {
                                        //直接返回给前端
                                        s.next(tc.text());
                                        sb.append(tc.text());
                                    });
                                    if (null == sink) {
                                        sb.append(tc.text());
                                    }
                                }
                            }
                    )
                    .doOnError(ex -> {
                        error.set(true);
                        sb.append(ex.getMessage());
                        sink.next(ex.getMessage());
                    })
                    .blockLast();
        });
    }

    private static void internalCall(FluxSink sink, Function<String, McpFunction> f, String toolName, Map<String, Object> toolArguments, StringBuilder sb, AtomicBoolean error) {
        McpFunction function = f.apply(toolName);
        Flux<McpSchema.CallToolResult> flux = function.apply(toolArguments);
        flux.doOnNext(tr -> Optional.ofNullable(sink).ifPresent(s -> {
            if (tr.content().get(0) instanceof McpSchema.TextContent tc) {
                //直接返回给前端
                s.next(tc.text());
                sb.append(tc.text());
            }
        })).doOnError(e->{
            error.set(true);
                })
                .blockLast();
    }


}