package run.mone.mcp.hera.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ozhera.trace.etl.domain.jaegeres.JaegerAttribute;
import org.apache.ozhera.trace.etl.domain.jaegeres.JaegerLogs;
import org.apache.ozhera.trace.etl.domain.jaegeres.JaegerProcess;
import org.apache.ozhera.trace.etl.domain.tracequery.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.CustomConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.hera.analysis.constant.Prompts;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根因Span查询服务实现类
 * 用于分析trace链路，提取异常根因节点的相关信息
 *
 * @author dingtao
 */
@Slf4j
@Service
public class RootExceptionSpanService {

    @Value("${trace.query.section.api.url}")
    private String traceSectionQueryUrl;

    private LLM llm;

    @PostConstruct
    private void init() {
        LLMProvider llmProvider = LLMProvider.valueOf(System.getenv("LLM_PROVIDER"));
        LLMConfig config = LLMConfig.builder().llmProvider(llmProvider).build().custom();
        llm = new LLM(config);
    }

    private final Gson gson = new Gson();

    /**
     * 根据环境和traceId查询异常根因span信息
     *
     * @param traceId 追踪ID
     * @param env 环境（staging/online）
     * @return 格式化的根因span信息
     */
    public String queryRootExceptionSpan(String traceId, String env) {
        try {
            // 1. 查询部门trace数据
            List<Span> spans = querySectionTraceData(traceId, env);

            if (spans == null || spans.isEmpty()) {
                log.warn("未找到traceId: {} 对应的trace数据", traceId);
                return "未找到对应的trace数据";
            }

            log.info("查询到 {} 个span，开始使用AI分析异常节点", spans.size());

            // 2. 将spans转换为JSON供LLM分析
            String spansJson = gson.toJson(spans);

            // 3. 调用LLM分析，获取根因spanId
            String prompt = Prompts.CODE_FIX_ANALYSIS_PROMPT + "\n" + spansJson;
            String llmResponse = llm.chat(prompt);

            log.info("LLM分析结果: {}", llmResponse);

            // 4. 从LLM响应中解析spanId
            String rootCauseSpanId = extractSpanIdFromXml(llmResponse);

            if (StringUtils.isBlank(rootCauseSpanId)) {
                log.warn("LLM无法识别异常根因节点，traceId: {}", traceId);
                return "无法识别异常根因节点";
            }

            log.info("识别到根因spanId: {}", rootCauseSpanId);

            // 5. 根据spanId查找对应的span
            Span rootExceptionSpan = null;
            for (Span span : spans) {
                if (rootCauseSpanId.equals(span.getSpanID())) {
                    rootExceptionSpan = span;
                    break;
                }
            }

            if (rootExceptionSpan == null) {
                log.warn("未找到spanId: {} 对应的span", rootCauseSpanId);
                return "未找到对应的根因节点";
            }

            // 6. 提取关键信息
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("traceId", traceId);
            result.addProperty("spanId", rootExceptionSpan.getSpanID());

            // 提取projectId（从serviceName中提取）
            String projectId = extractProjectId(rootExceptionSpan);
            if (projectId != null) {
                result.addProperty("projectId", projectId);
            }

            // 提取envId
            String envId = extractEnvId(rootExceptionSpan);
            if (envId != null) {
                result.addProperty("envId", envId);
            }

            // 提取异常堆栈
            String stacktrace = extractStacktrace(rootExceptionSpan);
            if (stacktrace != null) {
                result.addProperty("stacktrace", stacktrace);
            }

            // 计算结束时间
            long startTime = rootExceptionSpan.getStartTime();
            long duration = rootExceptionSpan.getDuration();
            long endTime = (startTime + duration) / 1000; // 转换为毫秒
            result.addProperty("endTime", endTime);

            log.info("成功分析根因span，spanId: {}", rootExceptionSpan.getSpanID());

            return gson.toJson(result);

        } catch (Exception e) {
            log.error("查询根因span失败，traceId: {}, env: {}", traceId, env, e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 从LLM返回的XML中提取spanId
     *
     * @param xmlResponse LLM返回的XML响应
     * @return 提取的spanId，如果解析失败则返回null
     */
    private String extractSpanIdFromXml(String xmlResponse) {
        if (StringUtils.isBlank(xmlResponse)) {
            return null;
        }

        try {
            // 使用正则表达式提取 <spanId> 标签中的内容
            Pattern pattern = Pattern.compile("<spanId>\\s*([^<]*)\\s*</spanId>");
            Matcher matcher = pattern.matcher(xmlResponse);

            if (matcher.find()) {
                String spanId = matcher.group(1).trim();
                return StringUtils.isNotBlank(spanId) ? spanId : null;
            }

            log.warn("无法从XML中提取spanId，XML内容: {}", xmlResponse);
            return null;

        } catch (Exception e) {
            log.error("解析XML失败", e);
            return null;
        }
    }

    /**
     * 提取projectId（从serviceName中提取）
     *
     * @param span span数据
     * @return projectId
     */
    private String extractProjectId(Span span) {
        JaegerProcess process = span.getProcess();
        if (process != null) {
            String serviceName = process.getServiceName();
            if (serviceName != null && serviceName.contains("-")) {
                return serviceName.split("-")[0];
            }
        }
        return null;
    }

    /**
     * 提取envId（从process的tags中提取）
     *
     * @param span span数据
     * @return envId
     */
    private String extractEnvId(Span span) {
        JaegerProcess process = span.getProcess();
        if (process != null) {
            List<JaegerAttribute> tags = process.getTags();
            if (tags != null) {
                for (JaegerAttribute tag : tags) {
                    if ("service.env.id".equals(tag.getKey())) {
                        return tag.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 提取异常堆栈（从logs中提取）
     *
     * @param span span数据
     * @return 异常堆栈
     */
    private String extractStacktrace(Span span) {
        List<JaegerLogs> logs = span.getLogs();
        if (logs != null) {
            for (JaegerLogs log : logs) {
                List<JaegerAttribute> fields = log.getFields();
                if (fields != null) {
                    for (JaegerAttribute field : fields) {
                        if ("exception.stacktrace".equals(field.getKey())) {
                            return field.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查询部门trace数据
     *
     * @param traceId 追踪ID
     * @param env 环境（staging/online）
     * @return Span列表
     * @throws Exception 查询或解析失败时抛出异常
     */
    private List<Span> querySectionTraceData(String traceId, String env) throws Exception {
        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("traceId", traceId);
        requestBody.addProperty("env", env);

        String requestJson = gson.toJson(requestBody);
        log.info("发送部门Trace查询请求，URL: {}, Body: {}", traceSectionQueryUrl, requestJson);

        // 发送HTTP POST请求
        String responseBody = sendHttpPostRequest(traceSectionQueryUrl, requestJson);
        log.info("接收部门Trace查询响应内容: {}", responseBody);

        // 解析响应 - 可能是数组或包含data字段的对象
        JsonElement jsonElement = gson.fromJson(responseBody, JsonElement.class);

        List<Span> spans;
        if (jsonElement.isJsonArray()) {
            // 如果直接是数组
            Type listType = new TypeToken<List<Span>>(){}.getType();
            spans = gson.fromJson(jsonElement, listType);
        } else if (jsonElement.isJsonObject()) {
            // 如果是对象，尝试提取data字段
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // 尝试多个可能的字段名
            JsonElement dataElement = null;
            if (jsonObject.has("data")) {
                dataElement = jsonObject.get("data");
            } else if (jsonObject.has("spans")) {
                dataElement = jsonObject.get("spans");
            } else if (jsonObject.has("result")) {
                dataElement = jsonObject.get("result");
            }

            if (dataElement != null && dataElement.isJsonArray()) {
                Type listType = new TypeToken<List<Span>>(){}.getType();
                spans = gson.fromJson(dataElement, listType);
            } else {
                throw new IllegalStateException("响应格式错误：无法找到span数组。响应内容: " + responseBody);
            }
        } else {
            throw new IllegalStateException("响应格式错误：既不是数组也不是对象");
        }

        if (spans == null || spans.isEmpty()) {
            throw new IllegalStateException("未找到对应的trace数据");
        }

        return spans;
    }

    /**
     * 发送HTTP POST请求
     *
     * @param url 请求URL
     * @param jsonBody JSON请求体
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    private String sendHttpPostRequest(String url, String jsonBody) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");

            // 设置请求体
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                } else {
                    throw new IOException("响应体为空");
                }
            }
        }
    }

}
