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

@Service
@Slf4j
public class HeraLogService {

    @Value("${hera.log.create.api.url}")
    private String heraLogCreateUrl;

    private final Gson gson = new Gson();

    public String createLogByMiline(Long projectId, Long envId, String tailName, String logPath) {
        try {
            // 构建请求体
            JsonArray requestArray = new JsonArray();
            JsonObject requestBody = new JsonObject();

            requestBody.addProperty("projectId", projectId);
            requestBody.addProperty("envId", envId);

            if (tailName != null && !tailName.isEmpty()) {
                requestBody.addProperty("tailName", tailName);
            }

            if (logPath != null && !logPath.isEmpty()) {
                requestBody.addProperty("logPath", logPath);
            }

            requestArray.add(requestBody);

            String requestJson = gson.toJson(requestArray);
            log.info("发送Hera日志创建请求，URL: {}, Body: {}", heraLogCreateUrl, requestJson);

            // 发送HTTP POST请求
            String responseBody = sendHttpPostRequest(heraLogCreateUrl, requestJson);
            log.info("接收Hera日志创建响应内容: {}", responseBody);

            // 直接返回响应内容
            return responseBody;

        } catch (Exception e) {
            log.error("创建Hera日志失败，projectId: {}, envId: {}",
                    projectId, envId);
            return "创建日志失败：" + e.getMessage();
        }
    }

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
