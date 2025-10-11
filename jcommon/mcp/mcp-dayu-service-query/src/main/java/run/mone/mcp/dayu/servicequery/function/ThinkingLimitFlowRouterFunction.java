package run.mone.mcp.dayu.servicequery.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.*;
import java.time.Duration;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class ThinkingLimitFlowRouterFunction implements McpFunction {

    private final DayuServiceLimitFlowFunction limitFlowFunction;
    // 记忆每个会话最近一次使用的 appName
    private final Map<String, String> sessionLastApp = new ConcurrentHashMap<>();
    
    // 限流相关关键词模式
    private static final Pattern LIMIT_FLOW_PATTERNS = Pattern.compile(
        "(限流|流控|流量控制|rate.?limit|flow.?control|throttle|熔断|circuit.?breaker|降级|degrade|保护|protection)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public String getName() {
        return "thinking_limit_flow_router";
    }

    @Override
    public String getDesc() {
        return "智能限流路由器（few-shot）：\n"
                + "示例：\n"
                + "用户：为dayu的order-service 创建限流 qps=100\n"
                + "思考：\n- 识别为创建限流\n- 抽取 appName=dayu serviceName=order-service qps=100\n- 参数完整，可执行\n";
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
        log.info("ThinkingLimitFlowRouterFunction received args: {}", args);
        String text = extractText(args);
        String sessionId = String.valueOf(args.getOrDefault("session_id", "default"));
        
        if (text.isEmpty()) {
            return createThinkingResponse("思考过程", 
                "我需要分析用户输入来理解限流相关的操作意图。\n" +
                "当前没有收到有效的输入文本，无法进行意图分析。",
                "追问", 
                "请提供限流相关的操作，例如：\n- 查询应用 xxx 的限流规则\n- 为服务 yyy 创建限流规则\n- 更新限流规则 ID zzz");
        }

        // 开始思考过程
        List<String> thinkingSteps = new ArrayList<>();
        
        // 思考步骤1：分析输入内容
        thinkingSteps.add("分析用户输入：\"" + text + "\"");
        
        // 思考步骤2：检查是否包含限流关键词
        boolean hasLimitFlowKeywords = LIMIT_FLOW_PATTERNS.matcher(text).find();
        // 放宽触发条件：出现“启用/禁用/关闭”且包含类似 FQCN 的服务名时，也视为限流意图
        if (!hasLimitFlowKeywords) {
            boolean actionWords = text.contains("禁用") || text.contains("启用") || text.contains("开启") || text.contains("关闭")
                    || text.toLowerCase().contains("enable") || text.toLowerCase().contains("disable");
            boolean looksLikeFqcn = java.util.regex.Pattern
                    .compile("[a-zA-Z_][a-zA-Z0-9_\\.]+\\.[a-zA-Z0-9_]+(?:[:：][a-zA-Z0-9_]+)?")
                    .matcher(text).find();
            // 只要有动作词就尝试走限流（容忍未出现“限流”二字），提高自然语句的可用性
            if (actionWords) {
                hasLimitFlowKeywords = true;
            }
            // 或者出现看似服务名也放行
            if (!hasLimitFlowKeywords && looksLikeFqcn) {
                hasLimitFlowKeywords = true;
            }
        }
        thinkingSteps.add("检查限流关键词：" + (hasLimitFlowKeywords ? "发现限流相关词汇" : "未发现限流相关词汇"));
        
        if (!hasLimitFlowKeywords) {
            thinkingSteps.add("结论：输入不包含限流相关关键词，返回空结果让其他路由器处理");
            return Flux.empty();
        }
        
        // 思考步骤3：解析用户意图
        thinkingSteps.add("开始解析用户意图...");
        Map<String, Object> limitFlowArgs = parseUserIntent(text);
        // 根据上下文补齐 appName：优先使用本轮解析，其次使用会话记忆
        if (limitFlowArgs.containsKey("appName")) {
            sessionLastApp.put(sessionId, String.valueOf(limitFlowArgs.get("appName")));
        } else {
            String remembered = sessionLastApp.get(sessionId);
            if (remembered != null && !remembered.isBlank()) {
                limitFlowArgs.put("appName", remembered);
                thinkingSteps.add("从会话上下文补齐 appName=" + remembered);
            }
        }
        thinkingSteps.add("解析结果：" + limitFlowArgs.toString());
        
        // 思考步骤4：确定操作类型
        String operationType = (String) limitFlowArgs.get("operation");
        thinkingSteps.add("识别操作类型：" + operationType);
        
        // 思考步骤5：验证参数完整性
        // 在缺少 id 但具备 service(+method) 时，尝试自动解析规则ID
        String opTry = String.valueOf(limitFlowArgs.getOrDefault("operation", ""));
        if (("update".equals(opTry) || "delete".equals(opTry))
                && !limitFlowArgs.containsKey("ruleId") && limitFlowArgs.containsKey("serviceName")) {
            thinkingSteps.add("尝试根据服务/方法自动匹配规则ID...");
            try {
                String appAuto = String.valueOf(limitFlowArgs.getOrDefault("appName", "dayu"));
                String svcAuto = String.valueOf(limitFlowArgs.get("serviceName"));
                String methodAuto = String.valueOf(limitFlowArgs.getOrDefault("method", ""));
                java.util.Optional<String> found = limitFlowFunction.resolveRuleId(appAuto, svcAuto, methodAuto.isBlank() ? null : methodAuto);
                if (found.isPresent()) {
                    limitFlowArgs.put("ruleId", found.get());
                    thinkingSteps.add("匹配到规则ID=" + found.get());
                } else {
                    thinkingSteps.add("未匹配到规则ID，将提示补充信息。");
                }
            } catch (Exception e) {
                thinkingSteps.add("匹配规则ID失败: " + e.getMessage());
            }
        }

        List<String> missingParams = validateParameters(limitFlowArgs);
        if (!missingParams.isEmpty()) {
            thinkingSteps.add("发现缺失参数：" + missingParams);
            return createThinkingResponse("思考过程", 
                String.join("\n", thinkingSteps) + "\n\n结论：参数不完整，需要用户提供更多信息",
                "追问", 
                "请提供以下缺失信息：" + String.join("、", missingParams));
        }
        
        thinkingSteps.add("所有参数验证通过，准备执行限流操作");
        
        // 将通用意图参数映射为底层工具所需参数
        Map<String, Object> mappedArgs = new HashMap<>();
        String op = (String) limitFlowArgs.getOrDefault("operation", "query");
        String action = switch (op) { case "query" -> "list"; case "create" -> "create"; case "update" -> "update"; case "delete" -> "delete"; default -> "list"; };
        if (limitFlowArgs.containsKey("appName")) mappedArgs.put("app", limitFlowArgs.get("appName"));
        if (limitFlowArgs.containsKey("serviceName")) mappedArgs.put("service", limitFlowArgs.get("serviceName"));
        if (limitFlowArgs.containsKey("ruleId")) mappedArgs.put("id", limitFlowArgs.get("ruleId"));
        if (limitFlowArgs.containsKey("qps")) { mappedArgs.put("grade", 1); mappedArgs.put("count", limitFlowArgs.get("qps")); }
        if (limitFlowArgs.containsKey("enabled")) mappedArgs.put("enabled", limitFlowArgs.get("enabled"));
        mappedArgs.put("action", action);

        // 流式返回思考步骤（接近打字机效果），再执行实际操作
        List<McpSchema.CallToolResult> stepResults = new ArrayList<>();
        stepResults.add(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("思考过程")), false));
        for (String step : thinkingSteps) {
            stepResults.add(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(step)), false));
        }
        stepResults.add(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("执行操作...")), false));

        return Flux.concat(
                Flux.fromIterable(stepResults).delayElements(Duration.ofMillis(120)),
                limitFlowFunction.apply(mappedArgs)
        );
    }
    
    /**
     * 创建包含思考过程的响应
     */
    private Flux<McpSchema.CallToolResult> createThinkingResponse(String thinkingTitle, String thinkingContent, 
                                                                String questionTitle, String questionContent) {
        String response = thinkingTitle + "\n" + thinkingContent + "\n\n" + questionTitle + "\n" + questionContent;
        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent(response)),
            false
        ));
    }
    
    /**
     * 解析用户输入，提取限流操作参数
     */
    private Map<String, Object> parseUserIntent(String userInput) {
        Map<String, Object> args = new HashMap<>();
        String input = userInput.toLowerCase();
        
        // 思考：分析操作类型
        if (input.contains("查询") || input.contains("查看") || input.contains("获取") || input.contains("list")) {
            args.put("operation", "query");
        } else if (input.contains("创建") || input.contains("新增") || input.contains("添加") || input.contains("create")) {
            args.put("operation", "create");
        } else if (input.contains("更新") || input.contains("修改") || input.contains("编辑") || input.contains("update")) {
            args.put("operation", "update");
        } else if (input.contains("删除") || input.contains("移除") || input.contains("delete")) {
            args.put("operation", "delete");
        } else if (input.contains("禁用") || input.contains("关闭") || input.contains("disable")) {
            args.put("operation", "update");
            args.put("enabled", false);
        } else if (input.contains("启用") || input.contains("开启") || input.contains("打开") || input.contains("enable")) {
            args.put("operation", "update");
            args.put("enabled", true);
        } else {
            args.put("operation", "query"); // 默认查询
        }
        
        // 思考：提取应用名称
        String appName = extractAppName(userInput);
        if (!appName.isEmpty()) {
            args.put("appName", appName);
        }
        
        // 思考：提取服务名称
        String serviceName = extractServiceName(userInput);
        if (!serviceName.isEmpty()) {
            args.put("serviceName", serviceName);
        }

        // 直接识别 FQCN（com.xxx.Class 或 com.xxx.Interface:method），便于“将 X 状态改为禁用”
        java.util.regex.Matcher fqcn = java.util.regex.Pattern
                .compile("([a-zA-Z_][a-zA-Z0-9_\\.]+\\.[a-zA-Z0-9_]+)(?::([a-zA-Z0-9_]+))?")
                .matcher(userInput);
        if (!args.containsKey("serviceName") && fqcn.find()) {
            args.put("serviceName", fqcn.group(1));
            if (fqcn.group(2) != null) args.put("method", fqcn.group(2));
        }

        // 补充兜底：如果出现“查询XXX的服务限流/限流”这类句式，自动把 XXX 当做应用名
        if (!args.containsKey("appName")) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("查询\s*([A-Za-z0-9_-]+)的?(服务)?限流")
                    .matcher(userInput);
            if (m.find()) {
                args.put("appName", m.group(1));
            }
        }

        // 再兜底：如果整句只包含一个明显的英文/数字 token，则作为应用名
        if (!args.containsKey("appName") && !args.containsKey("serviceName")) {
            java.util.regex.Matcher only = java.util.regex.Pattern
                    .compile("([A-Za-z0-9_-]{2,})")
                    .matcher(userInput);
            if (only.find()) {
                args.put("appName", only.group(1));
            }
        }
        
        // 思考：提取规则ID
        String ruleId = extractRuleId(userInput);
        if (!ruleId.isEmpty()) {
            args.put("ruleId", ruleId);
        }
        
        // 思考：提取限流参数
        extractLimitFlowParams(userInput, args);
        
        return args;
    }
    
    /**
     * 验证参数完整性
     */
    private List<String> validateParameters(Map<String, Object> args) {
        List<String> missing = new ArrayList<>();
        String operation = (String) args.get("operation");
        
        switch (operation) {
            case "query":
                if (!args.containsKey("appName") && !args.containsKey("serviceName")) {
                    missing.add("应用名称或服务名称");
                }
                break;
            case "create":
                if (!args.containsKey("appName")) missing.add("应用名称");
                if (!args.containsKey("serviceName")) missing.add("服务名称");
                break;
            case "update":
            case "delete":
                if (!args.containsKey("ruleId")) missing.add("规则ID");
                break;
        }
        
        return missing;
    }
    
    /**
     * 提取应用名称
     */
    private String extractAppName(String text) {
        // 匹配模式：应用 xxx、app xxx、应用名 xxx
        String[] patterns = {
            "应用[：:]?\\s*([A-Za-z0-9_-]+)",
            "app[：:]?\\s*([A-Za-z0-9_-]+)",
            "应用名[：:]?\\s*([A-Za-z0-9_-]+)",
            // 句式：查询<app>的限流 / 查询<app>的服务限流
            "查询\\s*([A-Za-z0-9_-]+)的?(?:服务)?限流"
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
     * 提取规则ID
     */
    private String extractRuleId(String text) {
        // 匹配模式：规则 xxx、rule xxx、ID xxx
        String[] patterns = {
            "规则[：:]?\\s*([A-Za-z0-9_-]+)",
            "rule[：:]?\\s*([A-Za-z0-9_-]+)",
            "id[：:]?\\s*([A-Za-z0-9_-]+)"
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
     * 提取限流参数
     */
    private void extractLimitFlowParams(String text, Map<String, Object> args) {
        // 提取QPS
        java.util.regex.Pattern qpsPattern = java.util.regex.Pattern.compile("(\\d+)\\s*qps", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher qpsMatcher = qpsPattern.matcher(text);
        if (qpsMatcher.find()) {
            args.put("qps", Integer.parseInt(qpsMatcher.group(1)));
        }
        
        // 提取并发数
        java.util.regex.Pattern concurrencyPattern = java.util.regex.Pattern.compile("(\\d+)\\s*并发", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher concurrencyMatcher = concurrencyPattern.matcher(text);
        if (concurrencyMatcher.find()) {
            args.put("concurrency", Integer.parseInt(concurrencyMatcher.group(1)));
        }
        
        // 提取启用状态
        if (text.contains("启用") || text.contains("开启") || text.contains("enable")) {
            args.put("enabled", true);
        } else if (text.contains("禁用") || text.contains("关闭") || text.contains("disable")) {
            args.put("enabled", false);
        }
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
}
