package run.mone.mcp.dayu.servicequery.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Dayu 服务限流功能路由器
 * 根据用户输入自动路由到限流功能
 */
@Slf4j
@RequiredArgsConstructor
public class DayuServiceLimitFlowRouterFunction implements McpFunction {

    private String name = "dayu_service_limit_flow_router";
    private String desc = "Dayu 服务限流功能路由器，根据用户输入自动路由到相应的限流操作";
    
    private final DayuServiceLimitFlowFunction limitFlowFunction;
    
    // 限流相关的关键词模式
    private static final Pattern LIMIT_FLOW_PATTERNS = Pattern.compile(
        "(限流|限流规则|流量控制|rate.?limit|flow.?control|创建限流|删除限流|更新限流|查询限流|限流列表|限流配置|限流管理)", 
        Pattern.CASE_INSENSITIVE
    );
    
    // 操作类型识别模式
    private static final Pattern CREATE_PATTERNS = Pattern.compile(
        "(创建|新增|添加|设置|配置).*限流", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern DELETE_PATTERNS = Pattern.compile(
        "(删除|移除|取消).*限流", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern UPDATE_PATTERNS = Pattern.compile(
        "(更新|修改|编辑|调整).*限流", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern LIST_PATTERNS = Pattern.compile(
        "(查询|查看|列表|显示|获取).*限流", 
        Pattern.CASE_INSENSITIVE
    );


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
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
        log.info("DayuServiceLimitFlowRouterFunction received args: {}", args);
        String text = extractText(args);
        if (text.isEmpty()) {
            String msg = "思考过程\n我们需要限流相关的信息来执行操作。\n\n追问\n请提供限流相关的操作，例如：\n- 查询应用 xxx 的限流规则\n- 为服务 yyy 创建限流规则\n- 更新限流规则 ID zzz";
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(msg)),
                    false
            ));
        }

        // 检查是否包含限流相关关键词
        if (!LIMIT_FLOW_PATTERNS.matcher(text).find()) {
            // 不包含限流关键词，返回空结果让其他路由器处理
            return Flux.empty();
        }
        
        // 解析用户意图并构建参数
        Map<String, Object> limitFlowArgs = parseUserIntent(text);
        
        // 路由到限流功能
        return limitFlowFunction.apply(limitFlowArgs);
    }
    
    /**
     * 解析用户输入，提取限流操作参数
     */
    private Map<String, Object> parseUserIntent(String userInput) {
        Map<String, Object> args = new java.util.HashMap<>();
        
        // 确定操作类型
        String action = determineAction(userInput);
        args.put("action", action);
        
        // 提取应用名称（从用户输入中查找）
        String app = extractAppName(userInput);
        if (app != null) {
            args.put("app", app);
        }
        
        // 提取服务名称
        String service = extractServiceName(userInput);
        if (service != null) {
            args.put("service", service);
        }
        
        // 提取方法名称
        String method = extractMethodName(userInput);
        if (method != null) {
            args.put("method", method);
        }
        
        // 提取限流配置参数
        extractLimitFlowConfig(userInput, args);
        
        log.info("解析用户意图结果: {}", args);
        return args;
    }
    
    /**
     * 确定操作类型
     */
    private String determineAction(String userInput) {
        if (CREATE_PATTERNS.matcher(userInput).find()) {
            return "create";
        } else if (DELETE_PATTERNS.matcher(userInput).find()) {
            return "delete";
        } else if (UPDATE_PATTERNS.matcher(userInput).find()) {
            return "update";
        } else if (LIST_PATTERNS.matcher(userInput).find()) {
            return "list";
        } else {
            // 默认查询列表
            return "list";
        }
    }
    
    /**
     * 提取应用名称
     */
    private String extractAppName(String userInput) {
        // 查找常见的应用名称模式
        java.util.regex.Pattern appPattern = java.util.regex.Pattern.compile(
            "(?:应用|app)[:：]?\\s*([a-zA-Z0-9_-]+)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = appPattern.matcher(userInput);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // 如果没有明确的应用名称，尝试从上下文推断
        // 这里可以根据实际情况添加更多逻辑
        return null;
    }
    
    /**
     * 提取服务名称
     */
    private String extractServiceName(String userInput) {
        java.util.regex.Pattern servicePattern = java.util.regex.Pattern.compile(
            "(?:服务|service)[:：]?\\s*([a-zA-Z0-9_.-]+)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = servicePattern.matcher(userInput);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * 提取方法名称
     */
    private String extractMethodName(String userInput) {
        java.util.regex.Pattern methodPattern = java.util.regex.Pattern.compile(
            "(?:方法|method)[:：]?\\s*([a-zA-Z0-9_.-]+)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = methodPattern.matcher(userInput);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * 提取限流配置参数
     */
    private void extractLimitFlowConfig(String userInput, Map<String, Object> args) {
        // 提取QPS/线程数阈值
        java.util.regex.Pattern qpsPattern = java.util.regex.Pattern.compile(
            "(?:QPS|qps)[:：]?\\s*(\\d+)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher qpsMatcher = qpsPattern.matcher(userInput);
        if (qpsMatcher.find()) {
            args.put("grade", 1); // QPS类型
            args.put("count", Integer.parseInt(qpsMatcher.group(1)));
        }
        
        // 提取线程数阈值
        java.util.regex.Pattern threadPattern = java.util.regex.Pattern.compile(
            "(?:线程数|thread)[:：]?\\s*(\\d+)", 
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher threadMatcher = threadPattern.matcher(userInput);
        if (threadMatcher.find()) {
            args.put("grade", 0); // 线程数类型
            args.put("count", Integer.parseInt(threadMatcher.group(1)));
        }
        
        // 提取限流类型
        if (userInput.contains("集群限流") || userInput.contains("cluster")) {
            args.put("clusterMode", true);
        } else if (userInput.contains("单机限流") || userInput.contains("local")) {
            args.put("clusterMode", false);
        }
        
        // 提取限流效果
        if (userInput.contains("快速失败") || userInput.contains("快速")) {
            args.put("controlBehavior", 0);
        } else if (userInput.contains("冷启动") || userInput.contains("预热")) {
            args.put("controlBehavior", 1);
        } else if (userInput.contains("排队等待") || userInput.contains("排队")) {
            args.put("controlBehavior", 2);
        }
        
        // 提取是否启用
        if (userInput.contains("启用") || userInput.contains("开启")) {
            args.put("enabled", true);
        } else if (userInput.contains("禁用") || userInput.contains("关闭")) {
            args.put("enabled", false);
        }
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
