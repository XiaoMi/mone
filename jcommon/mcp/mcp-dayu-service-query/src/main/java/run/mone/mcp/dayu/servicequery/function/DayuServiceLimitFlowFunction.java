package run.mone.mcp.dayu.servicequery.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ParseException;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Dayu 服务限流功能实现
 * 提供基于服务限流规则的增删改查功能
 */
@Data
@Slf4j
public class DayuServiceLimitFlowFunction implements McpFunction {

    private String name = "dayu_service_limit_flow";
    private String desc = "管理 Dayu 微服务治理中心的服务限流规则，支持创建、查询、更新、删除限流规则";
    
    private static final String DEFAULT_DAYU_BASE_URL = "http://mone.test.mi.com/dayu";
    private String dayuBaseUrl;
    private String authToken;
    private String cookie;
    private ObjectMapper objectMapper;

    // 限流规则查询工具Schema
    private String limitFlowQueryToolSchema = """
            {
                "type": "object",
                "properties": {
                    "action": {
                        "type": "string",
                        "enum": ["list", "create", "update", "delete", "detail"],
                        "description": "操作类型：list-查询限流规则列表，create-创建限流规则，update-更新限流规则，delete-删除限流规则，detail-查看限流规则详情"
                    },
                    "app": {
                        "type": "string",
                        "description": "应用名称，必填"
                    },
                    "service": {
                        "type": "string",
                        "description": "服务名称，创建/更新/删除时必填"
                    },
                    "method": {
                        "type": "string",
                        "description": "方法名称，可选"
                    },
                    "id": {
                        "type": "string",
                        "description": "限流规则ID，更新/删除/详情时必填"
                    },
                    "clusterMode": {
                        "type": "boolean",
                        "description": "限流类型：false-单机限流，true-集群限流"
                    },
                    "grade": {
                        "type": "integer",
                        "description": "条件类型：0-线程数，1-QPS"
                    },
                    "count": {
                        "type": "integer",
                        "description": "阈值"
                    },
                    "controlBehavior": {
                        "type": "integer",
                        "description": "限流效果：0-快速失败，1-冷启动，2-排队等待"
                    },
                    "strategy": {
                        "type": "integer",
                        "description": "限流模式：0-直接限流"
                    },
                    "thresholdType": {
                        "type": "integer",
                        "description": "阈值模式：0-单机均摊，1-总体阈值（仅集群限流时有效）"
                    },
                    "warmUpPeriodSec": {
                        "type": "integer",
                        "description": "预热时长（秒），冷启动时有效"
                    },
                    "maxQueueingTimeMs": {
                        "type": "integer",
                        "description": "超时时间（毫秒），排队等待时有效"
                    },
                    "fallbackClass": {
                        "type": "string",
                        "description": "降级服务类名"
                    },
                    "fallbackMethod": {
                        "type": "string",
                        "description": "降级方法名"
                    },
                    "enabled": {
                        "type": "boolean",
                        "description": "是否启用限流规则"
                    }
                },
                "required": ["action", "app"]
            }
            """;

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
        return limitFlowQueryToolSchema;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        McpSchema.CallToolResult result = doLimitFlowOperation(args);
        return Flux.just(result);
    }

    private McpSchema.CallToolResult doLimitFlowOperation(Map<String, Object> args) {
        try {
            String action = (String) args.get("action");
            String app = (String) args.get("app");
            
            if (action == null || action.trim().isEmpty()) {
                throw new IllegalArgumentException("操作类型不能为空");
            }
            if (app == null || app.trim().isEmpty()) {
                throw new IllegalArgumentException("应用名称不能为空");
            }

            log.info("执行限流操作: action={}, app={}", action, app);

            String result;
            switch (action.toLowerCase()) {
                case "list":
                    result = listLimitFlowRules(app);
                    break;
                case "create":
                    result = createLimitFlowRule(args);
                    break;
                case "update":
                    result = updateLimitFlowRule(args);
                    break;
                case "delete":
                    result = deleteLimitFlowRule(args);
                    break;
                case "detail":
                    result = getLimitFlowRuleDetail(args);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的操作类型: " + action);
            }
            
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)),
                    false
            );

        } catch (Exception ex) {
            log.error("执行限流操作时发生错误", ex);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("操作失败: " + ex.getMessage())),
                    true
            );
        }
    }

    // 查询限流规则列表
    private String listLimitFlowRules(String app) throws IOException, ParseException {
        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "/v2/flow/rules?app=" + URLEncoder.encode(app, StandardCharsets.UTF_8) + "&token=white_token";
            log.info("查询限流规则列表URL: {}", url);

            HttpGet httpGet = new HttpGet(url);
            addCommonHeaders(httpGet, token, cookieHeader);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200) {
                    if (isHtml(responseBody)) {
                        return "查询成功但收到HTML页面，疑似未登录或认证失效。\n"
                                + "请配置 dayu.auth-token 或 dayu.cookie 后重试。\n"
                                + "当前URL: " + url;
                    }
                    return formatLimitFlowListResponse(responseBody, app);
                } else {
                    throw new RuntimeException("查询限流规则列表失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    // 供路由器调用：根据 service(+method) 在列表中定位规则ID
    public Optional<String> resolveRuleId(String app, String service, String method)
            throws IOException, ParseException {
        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "/v2/flow/rules?app=" + URLEncoder.encode(app, StandardCharsets.UTF_8);
            HttpGet httpGet = new HttpGet(url);
            addCommonHeaders(httpGet, token, cookieHeader);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                if (statusCode != 200 || isHtml(responseBody)) {
                    return Optional.empty();
                }
                Map<String, Object> resp = objectMapper.readValue(responseBody, Map.class);
                List<Map<String, Object>> rules = (List<Map<String, Object>>) resp.get("data");
                if (rules == null) return Optional.empty();
                for (Map<String, Object> rule : rules) {
                    String svc = String.valueOf(rule.getOrDefault("service", ""));
                    String mth = String.valueOf(rule.getOrDefault("method", ""));
                    if (svc.equals(service) &&
                            (method == null || method.isBlank() || mth.equals(method))) {
                        Object idObj = rule.get("id");
                        if (idObj != null) return Optional.of(String.valueOf(idObj));
                    }
                }
                return Optional.empty();
            }
        }
    }

    private boolean isHtml(String body) {
        if (body == null) return false;
        String b = body.stripLeading().toLowerCase();
        return b.startsWith("<!doctype") || b.startsWith("<html") || b.contains("<body");
    }

    // 创建限流规则
    private String createLimitFlowRule(Map<String, Object> args) throws IOException, ParseException {
        String app = (String) args.get("app");
        String service = (String) args.get("service");
        
        if (service == null || service.trim().isEmpty()) {
            throw new IllegalArgumentException("服务名称不能为空");
        }

        Map<String, Object> flowRule = buildFlowRuleFromArgs(args);
        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "/v2/flow/rule?app=" + URLEncoder.encode(app, StandardCharsets.UTF_8) + 
                       "&method=" + URLEncoder.encode((String) args.getOrDefault("method", ""), StandardCharsets.UTF_8) +
                       "&service=" + URLEncoder.encode(service, StandardCharsets.UTF_8) +
                       "&token=white_token";
            log.info("创建限流规则URL: {}", url);

            HttpPost httpPost = new HttpPost(url);
            addCommonHeaders(httpPost, token, cookieHeader);
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(flowRule), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200 || statusCode == 201) {
                    return "限流规则创建成功\n" + formatFlowRuleInfo(flowRule);
                } else {
                    throw new RuntimeException("创建限流规则失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    // 更新限流规则
    private String updateLimitFlowRule(Map<String, Object> args) throws IOException, ParseException {
        String app = (String) args.get("app");
        String id = (String) args.get("id");
        
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("限流规则ID不能为空");
        }

        Map<String, Object> flowRule = buildFlowRuleFromArgs(args);
        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "/v2/flow/rule/" + id + "?app=" + URLEncoder.encode(app, StandardCharsets.UTF_8) +
                       "&method=" + URLEncoder.encode((String) args.getOrDefault("method", ""), StandardCharsets.UTF_8) +
                       "&service=" + URLEncoder.encode((String) args.getOrDefault("service", ""), StandardCharsets.UTF_8) +
                       "&token=white_token";
            log.info("更新限流规则URL: {}", url);

            HttpPut httpPut = new HttpPut(url);
            addCommonHeaders(httpPut, token, cookieHeader);
            httpPut.setEntity(new StringEntity(objectMapper.writeValueAsString(flowRule), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200) {
                    return "限流规则更新成功\n" + formatFlowRuleInfo(flowRule);
                } else {
                    throw new RuntimeException("更新限流规则失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    // 删除限流规则
    private String deleteLimitFlowRule(Map<String, Object> args) throws IOException, ParseException {
        String app = (String) args.get("app");
        String id = (String) args.get("id");
        
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("限流规则ID不能为空");
        }

        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = baseUrl + "/v2/flow/rule/" + id + "?app=" + URLEncoder.encode(app, StandardCharsets.UTF_8) + "&token=white_token";
            log.info("删除限流规则URL: {}", url);

            HttpDelete httpDelete = new HttpDelete(url);
            addCommonHeaders(httpDelete, token, cookieHeader);

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200 || statusCode == 204) {
                    return "限流规则删除成功，规则ID: " + id;
                } else {
                    throw new RuntimeException("删除限流规则失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    // 获取限流规则详情
    private String getLimitFlowRuleDetail(Map<String, Object> args) throws IOException, ParseException {
        String app = (String) args.get("app");
        String id = (String) args.get("id");
        
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("限流规则ID不能为空");
        }

        // 通过列表接口获取详情（假设列表接口返回完整信息）
        String listResult = listLimitFlowRules(app);
        return "限流规则详情（从列表获取）:\n" + listResult;
    }

    // 构建限流规则对象
    private Map<String, Object> buildFlowRuleFromArgs(Map<String, Object> args) {
        Map<String, Object> flowRule = new HashMap<>();
        
        // 基本信息
        flowRule.put("app", args.get("app"));
        flowRule.put("resource", args.get("service"));
        flowRule.put("method", args.getOrDefault("method", ""));
        
        // 限流配置
        flowRule.put("clusterMode", args.getOrDefault("clusterMode", false));
        flowRule.put("grade", args.getOrDefault("grade", 1)); // 默认QPS
        flowRule.put("count", args.getOrDefault("count", 100)); // 默认阈值100
        flowRule.put("controlBehavior", args.getOrDefault("controlBehavior", 0)); // 默认快速失败
        flowRule.put("strategy", args.getOrDefault("strategy", 0)); // 默认直接限流
        Object enabledObj = args.getOrDefault("enabled", true);
        boolean enabled = enabledObj instanceof Boolean ? (Boolean) enabledObj : true;
        flowRule.put("isClose", enabled ? 0 : 1); // 0-启用，1-关闭
        
        // 集群配置
        Map<String, Object> clusterConfig = new HashMap<>();
        clusterConfig.put("fallbackToLocalWhenFail", true);
        if ((Boolean) args.getOrDefault("clusterMode", false)) {
            clusterConfig.put("thresholdType", args.getOrDefault("thresholdType", 0));
        }
        flowRule.put("clusterConfig", clusterConfig);
        
        // 限流效果相关配置
        Integer controlBehavior = (Integer) args.getOrDefault("controlBehavior", 0);
        if (controlBehavior == 1) { // 冷启动
            flowRule.put("warmUpPeriodSec", args.getOrDefault("warmUpPeriodSec", 10));
        } else if (controlBehavior == 2) { // 排队等待
            flowRule.put("maxQueueingTimeMs", args.getOrDefault("maxQueueingTimeMs", 500));
        }
        
        // 降级配置
        flowRule.put("fallbackClass", args.getOrDefault("fallbackClass", ""));
        flowRule.put("fallbackMethod", args.getOrDefault("fallbackMethod", ""));
        flowRule.put("limitApp", "default");
        
        // 如果有ID，说明是更新操作
        if (args.get("id") != null) {
            flowRule.put("id", args.get("id"));
        }
        
        return flowRule;
    }

    // 添加通用请求头
    private void addCommonHeaders(org.apache.hc.client5.http.classic.methods.HttpUriRequest request, 
                                 String token, String cookieHeader) {
        if (token != null && !token.trim().isEmpty()) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        if (cookieHeader != null && !cookieHeader.trim().isEmpty()) {
            request.setHeader("Cookie", cookieHeader);
        }
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
    }

    // 格式化限流规则列表响应
    private String formatLimitFlowListResponse(String responseBody, String app) {
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> rules = (List<Map<String, Object>>) response.get("data");
            
            if (rules == null || rules.isEmpty()) {
                return "应用 " + app + " 暂无限流规则";
            }

            StringBuilder result = new StringBuilder();
            result.append("限流规则 · ").append(app).append("  共").append(rules.size()).append("条\n");
            result.append("────────────────────────────────────────────────────────────────────────\n");
            // 表头
            result.append(pad("#", 3)).append(" ")
                  .append(pad("状态", 4)).append("  ")
                  .append(pad("服务(方法)", 46)).append("  ")
                  .append(pad("类型", 4)).append(" ")
                  .append(pad("阈值", 6)).append("  ")
                  .append(pad("group", 10)).append("  ")
                  .append(pad("ID", 8)).append("\n");
            result.append("────────────────────────────────────────────────────────────────────────\n");

            for (int i = 0; i < rules.size(); i++) {
                Map<String, Object> rule = rules.get(i);
                String service = String.valueOf(rule.getOrDefault("service", ""));
                String method = String.valueOf(rule.getOrDefault("method", ""));
                String group = String.valueOf(rule.getOrDefault("dubboGroup", ""));
                int grade = ((Number) rule.getOrDefault("grade", 1)).intValue();
                Number count = (Number) rule.getOrDefault("count", 0);
                int isClose = ((Number) rule.getOrDefault("isClose", 0)).intValue();
                Object id = rule.get("id");

                String statusIcon = isClose == 0 ? "✅" : "⛔";
                String serviceCol = method == null || method.isEmpty() ? service : (service + "(" + method + ")");

                result.append(pad(String.valueOf(i + 1), 3)).append(" ")
                      .append(pad(statusIcon, 4)).append("  ")
                      .append(pad(truncate(serviceCol, 46), 46)).append("  ")
                      .append(pad(grade == 0 ? "线程" : "QPS", 4)).append(" ")
                      .append(pad(String.valueOf(count), 6)).append("  ")
                      .append(pad(truncate(group, 10), 10)).append("  ")
                      .append(pad(String.valueOf(id), 8)).append("\n");
            }

            result.append("────────────────────────────────────────────────────────────────────────\n");
            result.append("提示: 发送 ‘禁用 <服务全名> 的限流’ 或 ‘将 <服务全名> 的状态改为启用/禁用’ 可直接更新状态\n");
            return result.toString();

        } catch (Exception e) {
            log.error("解析限流规则列表响应失败", e);
            return "解析响应失败: " + e.getMessage() + "\n原始响应: " + responseBody;
        }
    }

    private String pad(String s, int width) {
        if (s == null) s = "";
        int len = s.length();
        if (len >= width) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    private String truncate(String s, int width) {
        if (s == null) return "";
        if (s.length() <= width) return s;
        if (width <= 1) return s.substring(0, width);
        return s.substring(0, width - 1) + "…";
    }

    // 格式化限流规则信息
    private String formatFlowRuleInfo(Map<String, Object> rule) {
        StringBuilder info = new StringBuilder();
        
        // 基本信息
        info.append("  服务: ").append(rule.getOrDefault("service", "")).append("\n");
        info.append("  方法: ").append(rule.getOrDefault("method", "")).append("\n");
        info.append("  资源: ").append(rule.getOrDefault("resource", "")).append("\n");
        
        // 限流类型
        Boolean clusterMode = (Boolean) rule.getOrDefault("clusterMode", false);
        if (clusterMode) {
            info.append("  限流类型: 集群限流");
            Map<String, Object> clusterConfig = (Map<String, Object>) rule.get("clusterConfig");
            if (clusterConfig != null) {
                Integer thresholdType = (Integer) clusterConfig.getOrDefault("thresholdType", 0);
                info.append(" (").append(thresholdType == 1 ? "总体阈值" : "单机均摊").append(")");
            }
        } else {
            info.append("  限流类型: 单机限流");
        }
        info.append("\n");
        
        // 限流条件
        Integer grade = (Integer) rule.getOrDefault("grade", 1);
        Integer count = (Integer) rule.getOrDefault("count", 0);
        info.append("  限流条件: 当").append(grade == 0 ? "线程数" : "QPS").append("为").append(count).append("时\n");
        
        // 限流效果
        Integer controlBehavior = (Integer) rule.getOrDefault("controlBehavior", 0);
        String effectDesc = "";
        switch (controlBehavior) {
            case 0:
                effectDesc = "快速失败";
                break;
            case 1:
                effectDesc = "冷启动，预热时长" + rule.getOrDefault("warmUpPeriodSec", 0) + "s";
                break;
            case 2:
                effectDesc = "排队等待，超时时间" + rule.getOrDefault("maxQueueingTimeMs", 0) + "ms";
                break;
        }
        info.append("  限流效果: ").append(effectDesc).append("\n");
        
        // 降级配置
        String fallbackClass = (String) rule.getOrDefault("fallbackClass", "");
        String fallbackMethod = (String) rule.getOrDefault("fallbackMethod", "");
        if (!fallbackClass.isEmpty() || !fallbackMethod.isEmpty()) {
            info.append("  降级配置: ").append(fallbackClass).append(".").append(fallbackMethod).append("\n");
        }
        
        // 状态
        Integer isClose = (Integer) rule.getOrDefault("isClose", 0);
        info.append("  状态: ").append(isClose == 0 ? "启用" : "关闭").append("\n");
        
        // 规则ID
        if (rule.get("id") != null) {
            info.append("  规则ID: ").append(rule.get("id")).append("\n");
        }
        
        return info.toString();
    }

    // 解析基础URL
    private String resolveBaseUrl() {
        String base = this.dayuBaseUrl;
        if (base == null || base.isBlank()) {
            base = System.getProperty("dayu.base-url", "");
        }
        if (base.isBlank()) {
            base = System.getenv().getOrDefault("DAYU_BASE_URL", "");
        }
        if (base.isBlank()) {
            base = DEFAULT_DAYU_BASE_URL;
        }
        if (!base.startsWith("http")) {
            base = "http://" + base;
        }
        if (this.dayuBaseUrl == null || !this.dayuBaseUrl.equals(base)) {
            log.info("Dayu base-url 使用: {}", base);
        }
        return base;
    }

    // 解析认证Token
    private String resolveAuthToken() {
        String token = this.authToken;
        if (token == null || token.isBlank()) {
            token = System.getProperty("dayu.auth-token", "");
        }
        if (token.isBlank()) {
            token = System.getenv().getOrDefault("DAYU_AUTH_TOKEN", "");
        }
        return token;
    }

    // 解析Cookie
    private String resolveCookie() {
        String ck = this.cookie;
        if (ck == null || ck.isBlank()) {
            ck = System.getProperty("dayu.cookie", "");
        }
        if (ck.isBlank()) {
            ck = System.getenv().getOrDefault("DAYU_COOKIE", "");
        }
        // 兼容 hive.manager.cookie
        if (ck.isBlank()) {
            ck = System.getProperty("hive.manager.cookie", "");
        }
        if (ck.isBlank()) {
            ck = System.getenv().getOrDefault("HIVE_MANAGER_COOKIE", "");
        }
        return ck;
    }

    public DayuServiceLimitFlowFunction(String dayuBaseUrl, String authToken, String cookie) {
        this.dayuBaseUrl = dayuBaseUrl;
        this.authToken = authToken;
        this.cookie = cookie;
        this.objectMapper = new ObjectMapper();
    }

    public DayuServiceLimitFlowFunction() {
        this.objectMapper = new ObjectMapper();
    }
}
