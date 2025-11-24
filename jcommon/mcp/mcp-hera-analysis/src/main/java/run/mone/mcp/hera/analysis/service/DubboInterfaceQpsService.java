package run.mone.mcp.hera.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Dubbo接口QPS查询服务实现类
 *
 * @author dingtao
 */
@Slf4j
@Service
public class DubboInterfaceQpsService {

    @Value("${dubbo.qps.api.url}")
    private String dubboQpsUrl;

    private final Gson gson = new Gson();

    /**
     * 获取Dubbo接口在指定时间段内的QPS信息
     *
     * @param appName 应用名称
     * @param serviceName Dubbo服务名称
     * @param methodName Dubbo方法名称
     * @param serverZone 服务器区域
     * @param startTimeSec 开始时间戳（秒）
     * @param endTimeSec 结束时间戳（秒）
     * @return 格式化的QPS信息字符串
     */
    public String getDubboInterfaceQps(String appName, String serviceName, String methodName,
                                       String serverZone, long startTimeSec, long endTimeSec) {
        try {
            // 构建请求URL
            String url = String.format("%s?appName=%s&serviceName=%s&methodName=%s&serverZone=%s&startTimeSec=%d&endTimeSec=%d",
                    dubboQpsUrl, appName, serviceName, methodName, serverZone, startTimeSec, endTimeSec);

            log.info("发送Dubbo接口QPS请求URL: {}", url);

            // 发送HTTP GET请求
            String responseBody = sendHttpRequest(url);
            log.info("接收Dubbo接口QPS响应内容: {}", responseBody);

            // 解析响应
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // 检查响应状态
            if (!jsonResponse.has("code") || jsonResponse.get("code").getAsInt() != 0) {
                String errorMsg = jsonResponse.has("message") ?
                    jsonResponse.get("message").getAsString() : "未知错误";
                return "查询Dubbo接口QPS失败：" + errorMsg;
            }

            // 提取数据
            JsonObject data = jsonResponse.has("data") ? jsonResponse.getAsJsonObject("data") : new JsonObject();

            double maxQps = data.has("maxQps") ? data.get("maxQps").getAsDouble() : 0.0;
            double avgQps = data.has("avgQps") ? data.get("avgQps").getAsDouble() : 0.0;
            double minQps = data.has("minQps") ? data.get("minQps").getAsDouble() : 0.0;

            // 格式化返回结果
            return String.format(
                    "应用：%s\nDubbo服务：%s\nDubbo方法：%s\n服务区域：%s\n时间范围：%d - %d\nQPS统计：\n  最大值：%.2f\n  平均值：%.2f\n  最小值：%.2f",
                    appName, serviceName, methodName, serverZone, startTimeSec, endTimeSec, maxQps, avgQps, minQps);

        } catch (Exception e) {
            log.error("查询应用{}的Dubbo接口{}.{}的QPS失败", appName, serviceName, methodName, e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 发送HTTP GET请求
     *
     * @param url 请求URL
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    private String sendHttpRequest(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
                } else {
                    throw new IOException("响应体为空");
                }
            }
        }
    }
}
