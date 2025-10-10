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

    @Override
    public String getName() {
        // 与客户端实际调用保持一致（见网络日志 tool_name）
        return "stream_mcp-dayu-service-query_chat";
    }

    @Override
    public String getDesc() {
        return "路由对话到 dayu_service_query，当用户表达包含查询服务意图时触发";
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
        if (text.isEmpty()) {
            String msg = "思考过程\n我们需要一个筛选值（filter）来查询 Dayu，通常来自你的自然语言，如：查询服务 xxx/查询应用 yyy/查询包含IP 1.2.3.4。\n\n追问\n为了继续，请提供具体的查询值（服务名/应用名/IPv4）。";
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

        String guide = "思考过程\n未从你的输入中可靠提取到筛选值（filter）。Dayu 需要 pattern 与 filter 组合：service/application/ip。\n\n追问\n请告知你要查询的类型与值：\n- 查询服务 <服务名>\n- 查询应用 <应用名>\n- 查询包含IP <IPv4地址>";
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


