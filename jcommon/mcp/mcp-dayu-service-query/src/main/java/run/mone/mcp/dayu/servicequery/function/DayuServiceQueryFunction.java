package run.mone.mcp.dayu.servicequery.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ParseException;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Dayu 服务查询功能实现
 * 提供基于服务名搜索的服务列表查询功能
 */
@Data
@Slf4j
public class DayuServiceQueryFunction implements McpFunction {

    private String name = "dayu_service_query";
    private String desc = "查询 Dayu 微服务治理中心的服务列表，支持按服务名搜索";
    
    private static final String DEFAULT_DAYU_BASE_URL = "http://mone.test.mi.com/dayu";
    private String dayuBaseUrl;
    private String authToken;
    private String cookie; // 可选，用于携带 SSO 等登录态
    private ObjectMapper objectMapper;

    private String serviceQueryToolSchema = """
            {
                "type": "object",
                "properties": {
                    "serviceName": {
                        "type": "string",
                        "description": "要搜索的服务名称，支持模糊匹配"
                    },
                    "group": {
                        "type": "string",
                        "description": "服务分组，可选"
                    },
                    "application": {
                        "type": "string", 
                        "description": "所属应用，可选"
                    },
                    "page": {
                        "type": "integer",
                        "description": "页码，从1开始，默认为1"
                    },
                    "pageSize": {
                        "type": "integer",
                        "description": "每页大小，默认为10"
                    },
                    "myParticipations": {
                        "type": "boolean",
                        "description": "是否只查询我参与的服务，默认为false"
                    }
                },
                "required": ["serviceName"]
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
        return serviceQueryToolSchema;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        McpSchema.CallToolResult result = doQuery(args);
        return Flux.just(result);
    }

    // 原有同步实现移动到私有方法，保持现有逻辑
    private McpSchema.CallToolResult doQuery(Map<String, Object> args) {
        // 这里保留原先返回 CallToolResult 的实现体（查询 dayu 并构造结果）
        // 方法体已在文件中存在的 apply 实现中；仅签名变化，这里直接复用原逻辑。
        return applyInternal(args);
    }

    // 将原同步 apply 实现迁移至此
    private McpSchema.CallToolResult applyInternal(Map<String, Object> args) {
        try {
            String serviceName = (String) args.get("serviceName");
            if (serviceName == null || serviceName.trim().isEmpty()) {
                throw new IllegalArgumentException("服务名称不能为空");
            }

            String group = (String) args.get("group");
            String application = (String) args.get("application");
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer pageSize = (Integer) args.getOrDefault("pageSize", 10);
            Boolean myParticipations = (Boolean) args.getOrDefault("myParticipations", false);
            String pattern = String.valueOf(args.getOrDefault("pattern", "service"));

            log.info("查询服务: serviceName={}, group={}, application={}, page={}, pageSize={}, myParticipations={}", 
                    serviceName, group, application, page, pageSize, myParticipations);

            // 构建查询参数（遵循 Dayu 网关：service 接口 + pattern & filter）
            Map<String, String> queryParams = new HashMap<>();
            // Dayu 网关要求用 filter 作为服务/应用查询参数
            queryParams.put("filter", serviceName);
            queryParams.put("pattern", pattern);
            queryParams.put("module", "0");
            // pageNum 从 1 开始，pageSize 默认为 10
            queryParams.put("pageNum", String.valueOf(page));
            queryParams.put("pageSize", String.valueOf(pageSize));
            // self=false 固定
            queryParams.put("self", "false");
            if (group != null && !group.trim().isEmpty()) {
                queryParams.put("group", group);
            }
            if (application != null && !application.trim().isEmpty()) {
                queryParams.put("application", application);
            }
            // 旧字段保留兼容（若后端既支持 page/pageSize 也支持 pageNum/pageSize）
            queryParams.put("page", String.valueOf(page));
            queryParams.put("pageSize", String.valueOf(pageSize));
            if (myParticipations) {
                queryParams.put("myParticipations", "true");
            }

            // 调用 Dayu API
            String result = queryDayuServices(queryParams);
            
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)),
                    false
            );

        } catch (Exception ex) {
            log.error("查询 Dayu 服务时发生错误", ex);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("查询失败: " + ex.getMessage())),
                    true
            );
        }
    }

    public DayuServiceQueryFunction(String dayuBaseUrl, String authToken, String cookie) {
        this.dayuBaseUrl = dayuBaseUrl;
        this.authToken = authToken;
        this.cookie = cookie;
        this.objectMapper = new ObjectMapper();
    }

    public DayuServiceQueryFunction() {
    }

    private String queryDayuServices(Map<String, String> queryParams) throws IOException, ParseException {
        String baseUrl = resolveBaseUrl();
        String token = resolveAuthToken();
        String cookieHeader = resolveCookie();
        String queryPath = resolveQueryPath();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构建查询 URL
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            if (!baseUrl.endsWith("/")) {
                urlBuilder.append("/");
            }
            urlBuilder.append(queryPath);
            
            // 添加查询参数
            boolean first = !queryPath.contains("?");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    urlBuilder.append(first ? "?" : "&");
                    urlBuilder.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                    first = false;
                }
            }

            // 追加白名单 token，免 cookie
            if (first) {
                urlBuilder.append("?");
            } else {
                urlBuilder.append("&");
            }
            urlBuilder.append("token=white_token");

            String url = urlBuilder.toString();
            // 打印最终访问地址（含路径与参数）
            log.info("调用 Dayu API 最终URL: {}", url);
            // 脱敏日志：仅提示是否携带 token/cookie
            log.info("Auth header present: {} , Cookie present: {}",
                    (token != null && !token.isBlank()), (cookieHeader != null && !cookieHeader.isBlank()));

            HttpGet httpGet = new HttpGet(url);
            
            // 添加认证头
            if (token != null && !token.trim().isEmpty()) {
                httpGet.setHeader("Authorization", "Bearer " + token);
            }
            if (cookieHeader != null && !cookieHeader.trim().isEmpty()) {
                httpGet.setHeader("Cookie", cookieHeader);
            }
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getCode();
                String contentType = response.getEntity() != null && response.getEntity().getContentType() != null
                        ? response.getEntity().getContentType()
                        : "";
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200) {
                    // 非 JSON（常见为登录页 HTML），直接回传可读提示
                    if (contentType != null && !contentType.toLowerCase().contains("application/json")
                            || (responseBody != null && responseBody.startsWith("<"))) {
                        return "请求URL: " + url + "\n" +
                                "Dayu 接口返回非 JSON 内容，可能需要鉴权或检查地址。\n" +
                                "Content-Type: " + contentType + "\n" +
                                "预览: " + responseBody.substring(0, Math.min(300, responseBody.length()));
                    }
                    return "请求URL: " + url + "\n" + formatServiceListResponse(responseBody);
                } else {
                    throw new RuntimeException("请求URL: " + url + "; Dayu API 调用失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

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

    private String resolveQueryPath() {
        String path = System.getProperty("dayu.query-path", "");
        if (path == null || path.isBlank()) {
            path = System.getenv().getOrDefault("DAYU_QUERY_PATH", "");
        }
        if (path == null || path.isBlank()) {
            // 默认仅资源名，参数由调用方统一拼装
            path = "service";
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String formatServiceListResponse(String responseBody) {
        try {
            // 解析 JSON 响应
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            // 兼容两种返回结构：
            // A) { data: [ ... ], total, page, pageSize }
            // B) { data: { content: [ ... ], totalElements, number, size } }
            List<Map<String, Object>> services = null;
            Integer total = null;
            Integer page = null;
            Integer pageSize = null;

            Object dataObj = response.get("data");
            if (dataObj instanceof List) {
                // 结构 A
                services = (List<Map<String, Object>>) dataObj;
                total = safeInt(response.get("total"), 0);
                page = safeInt(response.get("page"), 1);
                pageSize = safeInt(response.get("pageSize"), 10);
            } else if (dataObj instanceof Map) {
                // 结构 B（分页对象）
                Map<String, Object> pageObj = (Map<String, Object>) dataObj;
                Object content = pageObj.get("content");
                if (content instanceof List) {
                    services = (List<Map<String, Object>>) content;
                }
                total = safeInt(pageObj.get("totalElements"), 0);
                page = safeInt(pageObj.get("number"), 1);
                pageSize = safeInt(pageObj.get("size"), 10);
            }

            StringBuilder result = new StringBuilder();
            result.append("=== Dayu 服务查询结果 ===\n");
            result.append("总记录数: ").append(total).append("\n");
            int totalPages = pageSize == null || pageSize == 0 ? 1 : (total + pageSize - 1) / pageSize;
            result.append("当前页: ").append(page).append("/").append(totalPages).append("\n");
            result.append("每页大小: ").append(pageSize).append("\n\n");

            if (services == null || services.isEmpty()) {
                result.append("未找到匹配的数据\n");
            } else {
                // 采用更友好的表格样式（Markdown 兼容）
                result.append("服务列表:\n");
                result.append("| 序号 | 服务名 | 分组 | 版本 | 所属应用 | 实例数 |\n");
                result.append("| --- | --- | --- | --- | --- | --- |\n");

                int idx = 1;
                for (Map<String, Object> service : services) {
                    String serviceName = firstNonBlank(service,
                            "serviceName", "name", "service", "service_name");
                    String group = firstNonBlank(service, "group", "groupName", "group_id", "groupId");
                    String version = firstNonBlank(service, "version", "ver", "serviceVersion");
                    String application = firstNonBlank(service, "application", "app", "applicationName", "appName");
                    int instanceCount = safeInt(service.get("instanceCount"), 0);

                    result.append("| ").append(idx++).append(" | ")
                            .append(escapeTable(serviceName)).append(" | ")
                            .append(escapeTable(group)).append(" | ")
                            .append(escapeTable(version)).append(" | ")
                            .append(escapeTable(application)).append(" | ")
                            .append(instanceCount).append(" |\n");
                }
            }

            return result.toString();

        } catch (Exception e) {
            log.error("解析 Dayu API 响应失败", e);
            return "解析响应失败: " + e.getMessage() + "\n原始响应: " + responseBody;
        }
    }

    private int safeInt(Object obj, int def) {
        if (obj == null) return def;
        try {
            if (obj instanceof Number) return ((Number) obj).intValue();
            return Integer.parseInt(String.valueOf(obj));
        } catch (Exception ignore) {
            return def;
        }
    }

    private String firstNonBlank(Map<String, Object> map, String... keys) {
        for (String k : keys) {
            Object v = map.get(k);
            if (v != null) {
                String s = String.valueOf(v).trim();
                if (!s.isEmpty()) return s;
            }
        }
        return "";
    }

    private String escapeTable(String s) {
        if (s == null) return "";
        // 简单转义竖线，防止破坏表格
        return s.replace("|", "\\|");
    }
}
