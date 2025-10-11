package run.mone.mcp.hera.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mcp.hera.analysis.model.ApplicationMetricsResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 应用指标监控服务实现类
 *
 * @author dingtao
 */
@Slf4j
@Service
public class ApplicationMetricsService {

    @Value("${metrics.api.url}")
    private String metricsUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取应用指标信息
     *
     * @param application 应用名称（项目ID和项目名称的组合）
     * @return 格式化的指标信息字符串
     */
    public String getApplicationMetrics(String application) {
        try {
            // 计算时间戳（秒级）
            long endTime = System.currentTimeMillis() / 1000;
            long startTime = endTime - 60;

            // 构建请求URL
            String url = String.format("%s?application=%s&startTime=%d&endTime=%d&searchType=application&type=qps",
                    metricsUrl, application, startTime, endTime);
            
            log.info("发送请求URL: {}", url);

            // 发送HTTP GET请求
            String responseBody = sendHttpRequest(url);
            log.info("接收响应内容: {}", responseBody);

            // 解析响应
            ApplicationMetricsResponse response = parseResponse(responseBody);
            
            if (response.getCode() != 0) {
                return "查询失败：" + response.getMessage();
            }
            
            // 解析数据并取整
            int maxQps = (int) Math.round(response.getData().getMaxServerQps());
            int avgQps = (int) Math.round(response.getData().getAvgServerQps());
            
            // 拼接返回字符串
            return String.format("当前应用：%s，近一分钟的最大请求QPS为%d，平均请求QPS为%d", 
                    application, maxQps, avgQps);
        } catch (Exception e) {
            log.error("查询应用{}指标失败", application, e);
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

    /**
     * 解析接口返回的JSON响应
     *
     * @param responseBody 响应体
     * @return 解析后的响应对象
     * @throws IOException JSON解析异常
     */
    private ApplicationMetricsResponse parseResponse(String responseBody) throws IOException {
        return objectMapper.readValue(responseBody, ApplicationMetricsResponse.class);
    }
}

