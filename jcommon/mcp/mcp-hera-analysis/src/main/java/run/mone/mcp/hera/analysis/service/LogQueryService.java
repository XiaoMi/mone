package run.mone.mcp.hera.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 日志查询服务实现类
 *
 * @author dingtao
 */
@Slf4j
@Service
public class LogQueryService {

    @Value("${log.query.api.url}")
    private String logQueryUrl;

    private final Gson gson = new Gson();

    /**
     * 查询日志
     *
     * @param level 日志级别（ERROR, WARN, INFO等）
     * @param projectId 项目ID
     * @param envId 环境ID
     * @param startTime 开始时间（毫秒时间戳）
     * @param endTime 结束时间（毫秒时间戳）
     * @return 格式化的日志查询结果
     */
    public String queryLogs(String level, int projectId, int envId, long startTime, long endTime) {
        try {
            // 构建请求体
            JsonArray requestArray = new JsonArray();
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("level", level);
            requestBody.addProperty("startTime", String.valueOf(startTime));
            requestBody.addProperty("endTime", String.valueOf(endTime));
            requestBody.addProperty("envId", envId);
            requestBody.addProperty("projectId", projectId);
            requestArray.add(requestBody);

            String requestJson = gson.toJson(requestArray);
            log.info("发送日志查询请求，URL: {}, Body: {}", logQueryUrl, requestJson);

            // 发送HTTP POST请求
            String responseBody = sendHttpPostRequest(logQueryUrl, requestJson);
            log.info("接收日志查询响应内容: {}", responseBody);

            // 直接返回响应内容
            return responseBody;

        } catch (Exception e) {
            log.error("查询日志失败，projectId: {}, envId: {}, level: {}", projectId, envId, level, e);
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
