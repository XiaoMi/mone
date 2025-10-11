package run.mone.mcp.dayu.servicequery.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DayuChatRouterFunction implements McpFunction {

    private final DayuServiceQueryFunction dayuServiceQueryFunction;
    private final ThinkingLimitFlowRouterFunction limitThinkingRouter;

    private static final java.util.regex.Pattern LIMIT_FLOW_PATTERNS = java.util.regex.Pattern.compile(
            "(限流|流控|流量控制|rate.?limit|flow.?control|throttle|熔断|circuit.?breaker|降级|degrade|保护|protection|QPS|qps|阈值|限流值|设置|修改|调整|禁用|启用|开启|关闭)",
            java.util.regex.Pattern.CASE_INSENSITIVE
    );

    @Override
    public String getName() {
        // 与客户端实际调用保持一致（见网络日志 tool_name）
        return "stream_mcp-dayu-service-query_chat";
    }

    @Override
    public String getDesc() {
        return "聊天兜底路由器：负责自然对话接入与友好引导，必要时交给思考/限流路由器";
    }

    @Override
    public String getToolScheme() {
        return "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"text\": { \"type\": \"string\", \"description\": \"用户输入的自然语言\" }\n" +
                "  },\n" +
                "  \"required\": [\"text\"]\n" +
                "}";
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        log.info("DayuChatRouterFunction received args: {}", args);
        String text = extractText(args);
        // 优先：如果命中限流相关语义，直接交给思考路由器，保证产生“思考过程”
        if (text != null && !text.isBlank() && LIMIT_FLOW_PATTERNS.matcher(text).find()) {
            return limitThinkingRouter.apply(Map.of("text", text));
        }
        if (text.isEmpty()) {
            String msg = "嗨，我在呢～想查些什么？\n\n你可以这样说：\n- 查询服务 <服务名>（如：查询服务 order-service）\n- 查询应用 <应用名>（如：查询应用 dayu-app）\n- 查询包含IP <IPv4>（如：查询包含IP 10.0.0.1）";
            return Flux.just(new McpSchema.CallToolResult(
                    java.util.List.of(new McpSchema.TextContent(msg)),
                    false
            ));
        }

        if (hasQueryIntent(text)) {
            Map<String, Object> q = new HashMap<>();
            String pattern = extractPattern(text);
            String filter = extractFilterByPattern(text, pattern);
            q.put("serviceName", filter);
            q.put("pattern", pattern);
            return dayuServiceQueryFunction.apply(q);
        }

        // 回退：取最后一个非空词作为候选服务名（支持包含 / % 等字符）
        String[] parts = text.trim().split("\\s+");
        if (parts.length > 0) {
            String candidate = parts[parts.length - 1];
            if (!candidate.isBlank() && candidate.matches("[A-Za-z0-9_./:%-]+")) {
                Map<String, Object> q = new HashMap<>();
                String pattern = extractPattern(text);
                String filter = extractFilterByPattern(candidate, pattern);
                q.put("serviceName", filter);
                q.put("pattern", pattern);
                log.info("未命中关键词，回退使用最后词作为筛选值: {}，pattern={} ", filter, pattern);
                return dayuServiceQueryFunction.apply(q);
            }
        }

        String guide = "嗨～我是Dayu服务治理助手！\n\n你可以这样问我：\n• 查询服务 <服务名>（如：查询服务 inventory-service）\n• 查询应用 <应用名>（如：查询应用 dayu-app）\n• 查询包含IP <地址>（如：查询包含IP 192.168.1.1）\n• 管理限流规则（如：禁用 com.xiaomi.dayu.HelloWorld 的限流）\n\n有什么需要帮助的吗？";
        return Flux.just(new McpSchema.CallToolResult(
                java.util.List.of(new McpSchema.TextContent(guide)),
                false
        ));
    }

    private boolean hasQueryIntent(String text) {
        String t = text.toLowerCase();
        return t.contains("查询服务") || t.contains("搜索服务") || t.contains("查询 ") || t.contains("搜索 ") || t.contains("service") || t.contains("servicename") || t.contains("服务名");
    }

    private String extractServiceName(String text) {
        // 简单抽取：取最后一个空白后的连续字母/数字/下划线/点号作为服务名，如果没抽到就返回全文
        String candidate = text.replaceAll(".*?[\\s：:]+([A-Za-z0-9_./:%-]{2,})$", "$1");
        if (candidate == null || candidate.isEmpty() || candidate.equals(text)) {
            return text;
        }
        return candidate;
    }

    private String extractPattern(String text) {
        String t = text.toLowerCase();
        // IP 识别：含关键字 ip 或出现 IPv4 格式
        if (t.contains("ip") || t.matches(".*\\b(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})){3}\\b.*")) {
            return "ip";
        }
        if (t.contains("查询应用") || t.contains("搜索应用") || t.contains("应用")) {
            return "application";
        }
        // 默认按服务查询
        return "service";
    }

    private String extractFilterByPattern(String text, String pattern) {
        if ("ip".equalsIgnoreCase(pattern)) {
            // 返回第一个 IPv4 匹配，若没有则回退整体抽取
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})){3}")
                    .matcher(text);
            if (m.find()) {
                return m.group();
            }
        }
        return extractServiceName(text);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> args) {
        if (args == null) return "";
        Object v;
        // 常见字段
        for (String k : new String[]{"text", "message", "msg", "content", "query", "q"}) {
            v = args.get(k);
            if (v instanceof String s && !s.isBlank()) return s.trim();
        }
        // messages: [{role: user, content|text: xxx}]
        Object messages = args.get("messages");
        if (messages instanceof java.util.List<?> list && !list.isEmpty()) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object item = list.get(i);
                if (item instanceof Map<?,?> m) {
                    Object role = m.get("role");
                    if (role != null && String.valueOf(role).toLowerCase().contains("user")) {
                        Object c = m.get("content");
                        if (c instanceof String cs && !cs.isBlank()) return cs.trim();
                        Object t = m.get("text");
                        if (t instanceof String ts && !ts.isBlank()) return ts.trim();
                    }
                }
            }
        }
        // 兜底：如果只有一个键值，返回其字符串
        if (args.size() == 1) {
            Object only = args.values().iterator().next();
            if (only != null) return String.valueOf(only).trim();
        }
        return "";
    }
}


