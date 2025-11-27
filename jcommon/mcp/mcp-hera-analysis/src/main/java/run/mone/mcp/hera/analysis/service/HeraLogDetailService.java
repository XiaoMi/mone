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
 * Hera日志详情查询服务实现类
 *
 * @author dingtao
 */
@Slf4j
@Service
public class HeraLogDetailService {

    @Value("${hera.log.detail.api.url}")
    private String heraLogDetailUrl;

    private final Gson gson = new Gson();

    /**
     * 查询Hera日志详情
     *
     * @param spaceId 空间ID
     * @param storeId 存储ID
     * @param input 搜索输入内容（可能包含双引号等特殊字符）
     * @param tailName 日志尾部名称
     * @param startTime 开始时间（毫秒时间戳字符串）
     * @param endTime 结束时间（毫秒时间戳字符串）
     * @return 格式化的日志查询结果
     */
    public String queryLogDetail(int spaceId, int storeId, String input, String tailName, String startTime, String endTime) {
        try {
            // 构建请求体
            JsonArray requestArray = new JsonArray();
            JsonObject requestBody = new JsonObject();

            requestBody.addProperty("spaceId", spaceId);
            requestBody.addProperty("storeId", storeId);

            // input字段可能包含双引号，使用gson会自动进行转义处理
            requestBody.addProperty("input", input);

            requestBody.addProperty("tailName", tailName);
            requestBody.addProperty("startTime", startTime);
            requestBody.addProperty("endTime", endTime);

            requestArray.add(requestBody);

            String requestJson = gson.toJson(requestArray);
            log.info("发送Hera日志详情查询请求，URL: {}, Body: {}", heraLogDetailUrl, requestJson);

            // 发送HTTP POST请求
            String responseBody = sendHttpPostRequest(heraLogDetailUrl, requestJson);
            log.info("接收Hera日志详情查询响应内容: {}", responseBody);

            // 直接返回响应内容
            return responseBody;

        } catch (Exception e) {
            log.error("查询Hera日志详情失败，spaceId: {}, storeId: {}, input: {}, tailName: {}",
                    spaceId, storeId, input, tailName, e);
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
            httpPost.setHeader("x-debug", "true");

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