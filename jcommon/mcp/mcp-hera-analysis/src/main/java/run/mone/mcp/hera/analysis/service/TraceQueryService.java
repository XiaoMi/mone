package run.mone.mcp.hera.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ozhera.trace.etl.domain.tracequery.Span;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Trace查询服务实现类
 * 用于查询全量trace数据
 *
 * @author dingtao
 */
@Slf4j
@Service
public class TraceQueryService {

    @Value("${trace.query.api.url}")
    private String traceQueryUrl;

    private final Gson gson = new Gson();

    /**
     * 根据环境和traceId查询全量trace数据，返回Span对象列表
     *
     * @param traceId 追踪ID
     * @param env 环境（staging/online）
     * @return Span列表
     * @throws Exception 查询或解析失败时抛出异常
     */
    public List<Span> queryTraceDataAsSpans(String traceId, String env) throws Exception {
        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("traceId", traceId);
        requestBody.addProperty("env", env);

        String requestJson = gson.toJson(requestBody);
        log.info("发送全量Trace查询请求，URL: {}, Body: {}", traceQueryUrl, requestJson);

        // 发送HTTP POST请求
        String responseBody = sendHttpPostRequest(traceQueryUrl, requestJson);
        log.info("接收Trace查询响应内容: {}", responseBody);

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
     * 根据环境和traceId查询全量trace数据
     *
     * @param traceId 追踪ID
     * @param env 环境（staging/online）
     * @return 格式化的trace查询结果
     */
    public String queryTraceData(String traceId, String env) {
        try {
            List<Span> spans = queryTraceDataAsSpans(traceId, env);
            // 返回格式化的 JSON 字符串
            return gson.toJson(spans);
        } catch (Exception e) {
            log.error("查询Trace数据失败，traceId: {}, env: {}", traceId, env, e);
            return "查询失败：" + e.getMessage();
        }
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
