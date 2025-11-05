package run.mone.mcp.dayu.servicequery.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 真正的思考过程路由器
 * 集成真实的思考逻辑，而不是硬编码的文本输出
 */
@Slf4j
@RequiredArgsConstructor
public class RealThinkingRouterFunction implements McpFunction {

    private final DayuServiceQueryFunction dayuServiceQueryFunction;
    
    // 思考状态管理
    private final Map<String, ThinkingContext> thinkingContexts = new HashMap<>();
    
    // 服务查询相关关键词
    private static final Pattern SERVICE_QUERY_PATTERNS = Pattern.compile(
        "(查询|搜索|search|query|服务|service|应用|application|ip|IP)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public String getName() {
        return "real_thinking_router";
    }

    @Override
    public String getDesc() {
        return "真正的思考过程路由器：服务查询 few-shot\n"
                + "示例：\n"
                + "用户：查询服务 inventory-service\n"
                + "思考：\n- 识别为服务查询\n- 抽取 serviceName=inventory-service pattern=service\n- 参数完整，可执行\n";
    }

    @Override
    public String getToolScheme() {
        return "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"text\": { \"type\": \"string\", \"description\": \"用户输入的自然语言\" },\n" +
                "    \"session_id\": { \"type\": \"string\", \"description\": \"会话ID，用于保持思考上下文\" }\n" +
                "  },\n" +
                "  \"required\": [\"text\"]\n" +
                "}";
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        log.info("RealThinkingRouterFunction received args: {}", args);
        
        String text = extractText(args);
        String sessionId = (String) args.getOrDefault("session_id", "default");
        
        if (text.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(
                    "思考过程\n" +
                    "我需要分析用户输入来理解查询意图。\n" +
                    "当前没有收到有效的输入文本，无法进行意图分析。\n\n" +
                    "追问\n" +
                    "请提供查询相关的信息，例如：\n" +
                    "- 查询服务 xxx\n" +
                    "- 查询应用 yyy\n" +
                    "- 查询包含IP 1.2.3.4"
                )),
                false
            ));
        }

        // 获取或创建思考上下文
        ThinkingContext context = thinkingContexts.computeIfAbsent(sessionId, k -> new ThinkingContext());
        
        // 开始真正的思考过程
        ThinkingResult thinkingResult = performRealThinking(text, context);
        
        // 根据思考结果决定下一步行动
        if (thinkingResult.shouldExecute()) {
            // 执行实际的查询操作
            Map<String, Object> queryArgs = thinkingResult.getQueryArgs();
            return Flux.concat(
                // 先返回思考过程
                Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        "思考过程\n" + thinkingResult.getThinkingSteps() + "\n\n执行查询..."
                    )),
                    false
                )),
                // 然后执行实际的查询功能
                dayuServiceQueryFunction.apply(queryArgs)
            );
        } else {
            // 返回思考过程和追问
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(
                    "思考过程\n" + thinkingResult.getThinkingSteps() + "\n\n追问\n" + thinkingResult.getFollowUpQuestion()
                )),
                false
            ));
        }
    }
    
    /**
     * 执行真正的思考过程
     */
    private ThinkingResult performRealThinking(String userInput, ThinkingContext context) {
        List<String> thinkingSteps = new ArrayList<>();
        Map<String, Object> queryArgs = new HashMap<>();
        
        // 思考步骤1：分析输入内容
        thinkingSteps.add("分析用户输入：\"" + userInput + "\"");
        
        // 思考步骤2：检查是否包含查询关键词
        boolean hasQueryKeywords = SERVICE_QUERY_PATTERNS.matcher(userInput).find();
        thinkingSteps.add("检查查询关键词：" + (hasQueryKeywords ? "发现查询相关词汇" : "未发现查询相关词汇"));
        
        if (!hasQueryKeywords) {
            thinkingSteps.add("结论：输入不包含查询相关关键词，需要用户提供更多信息");
            return new ThinkingResult(thinkingSteps, "请提供查询相关的信息，例如：查询服务 xxx、查询应用 yyy、查询包含IP 1.2.3.4", false, queryArgs);
        }
        
        // 思考步骤3：分析查询类型
        String queryType = analyzeQueryType(userInput);
        thinkingSteps.add("识别查询类型：" + queryType);
        
        // 思考步骤4：提取查询值
        String queryValue = extractQueryValue(userInput, queryType);
        thinkingSteps.add("提取查询值：" + (queryValue.isEmpty() ? "未找到有效查询值" : queryValue));
        
        if (queryValue.isEmpty()) {
            thinkingSteps.add("结论：无法从输入中提取有效的查询值");
            return new ThinkingResult(thinkingSteps, "请明确指定要查询的值，例如：查询服务 myapp、查询应用 myapp、查询包含IP 192.168.1.1", false, queryArgs);
        }
        
        // 思考步骤5：构建查询参数
        queryArgs.put("serviceName", queryValue);
        queryArgs.put("pattern", queryType);
        thinkingSteps.add("构建查询参数：pattern=" + queryType + ", serviceName=" + queryValue);
        
        // 思考步骤6：验证参数完整性
        if (isQueryArgsComplete(queryArgs)) {
            thinkingSteps.add("所有参数验证通过，准备执行查询");
            return new ThinkingResult(thinkingSteps, "", true, queryArgs);
        } else {
            thinkingSteps.add("参数验证失败，需要用户提供更多信息");
            return new ThinkingResult(thinkingSteps, "请提供完整的查询信息", false, queryArgs);
        }
    }
    
    /**
     * 分析查询类型
     */
    private String analyzeQueryType(String input) {
        String lowerInput = input.toLowerCase();
        
        if (lowerInput.contains("应用") || lowerInput.contains("application")) {
            return "application";
        } else if (lowerInput.contains("ip") || input.matches(".*\\b(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})){3}\\b.*")) {
            return "ip";
        } else {
            return "service";
        }
    }
    
    /**
     * 提取查询值
     */
    private String extractQueryValue(String input, String queryType) {
        switch (queryType) {
            case "application":
                return extractApplicationName(input);
            case "ip":
                return extractIpAddress(input);
            case "service":
            default:
                return extractServiceName(input);
        }
    }
    
    /**
     * 提取应用名称
     */
    private String extractApplicationName(String text) {
        // 匹配模式：应用 xxx、app xxx、应用名 xxx
        String[] patterns = {
            "应用[：:]?\\s*([A-Za-z0-9_-]+)",
            "app[：:]?\\s*([A-Za-z0-9_-]+)",
            "应用名[：:]?\\s*([A-Za-z0-9_-]+)"
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group(1);
            }
        }
        
        return "";
    }
    
    /**
     * 提取服务名称
     */
    private String extractServiceName(String text) {
        // 匹配模式：服务 xxx、service xxx、服务名 xxx
        String[] patterns = {
            "服务[：:]?\\s*([A-Za-z0-9_-]+)",
            "service[：:]?\\s*([A-Za-z0-9_-]+)",
            "服务名[：:]?\\s*([A-Za-z0-9_-]+)"
        };
        
        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group(1);
            }
        }
        
        return "";
    }
    
    /**
     * 提取IP地址
     */
    private String extractIpAddress(String text) {
        java.util.regex.Pattern ipPattern = java.util.regex.Pattern.compile(
            "\\b(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d{1,2})){3}\\b"
        );
        java.util.regex.Matcher matcher = ipPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    
    /**
     * 验证查询参数是否完整
     */
    private boolean isQueryArgsComplete(Map<String, Object> args) {
        return args.containsKey("serviceName") && 
               args.containsKey("pattern") && 
               !args.get("serviceName").toString().isEmpty();
    }
    
    /**
     * 从参数中提取文本内容
     */
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
    
    /**
     * 思考上下文
     */
    private static class ThinkingContext {
        private final List<String> thinkingHistory = new ArrayList<>();
        private final Map<String, Object> contextData = new HashMap<>();
        
        public void addThinkingStep(String step) {
            thinkingHistory.add(step);
        }
        
        public List<String> getThinkingHistory() {
            return new ArrayList<>(thinkingHistory);
        }
        
        public void setContextData(String key, Object value) {
            contextData.put(key, value);
        }
        
        public Object getContextData(String key) {
            return contextData.get(key);
        }
    }
    
    /**
     * 思考结果
     */
    private static class ThinkingResult {
        private final List<String> thinkingSteps;
        private final String followUpQuestion;
        private final boolean shouldExecute;
        private final Map<String, Object> queryArgs;
        
        public ThinkingResult(List<String> thinkingSteps, String followUpQuestion, boolean shouldExecute, Map<String, Object> queryArgs) {
            this.thinkingSteps = thinkingSteps;
            this.followUpQuestion = followUpQuestion;
            this.shouldExecute = shouldExecute;
            this.queryArgs = queryArgs;
        }
        
        public String getThinkingSteps() {
            return String.join("\n", thinkingSteps);
        }
        
        public String getFollowUpQuestion() {
            return followUpQuestion;
        }
        
        public boolean shouldExecute() {
            return shouldExecute;
        }
        
        public Map<String, Object> getQueryArgs() {
            return queryArgs;
        }
    }
}
