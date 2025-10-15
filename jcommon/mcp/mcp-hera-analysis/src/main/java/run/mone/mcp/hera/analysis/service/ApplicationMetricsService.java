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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * 线程池，用于并发执行HTTP请求
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 获取应用指标信息
     * 并发查询QPS、CPU和Heap三种指标
     *
     * @param application 应用名称（项目ID和项目名称的组合）
     * @return 格式化的指标信息字符串
     */
    public String getApplicationMetrics(String application) {
        try {
            // 计算时间戳（秒级）
            long endTime = System.currentTimeMillis() / 1000;
            long startTime = endTime - 300;

            // 并发发送三个请求：qps、cpu、heap
            CompletableFuture<ApplicationMetricsResponse> qpsFuture = CompletableFuture.supplyAsync(
                    () -> fetchMetrics(application, startTime, endTime, "qps"), executorService);
            
            CompletableFuture<ApplicationMetricsResponse> cpuFuture = CompletableFuture.supplyAsync(
                    () -> fetchMetrics(application, startTime, endTime, "cpu"), executorService);
            
            CompletableFuture<ApplicationMetricsResponse> heapFuture = CompletableFuture.supplyAsync(
                    () -> fetchMetrics(application, startTime, endTime, "heap"), executorService);

            // 等待所有请求完成
            CompletableFuture.allOf(qpsFuture, cpuFuture, heapFuture).join();

            // 获取三个响应结果
            ApplicationMetricsResponse qpsResponse = qpsFuture.get();
            ApplicationMetricsResponse cpuResponse = cpuFuture.get();
            ApplicationMetricsResponse heapResponse = heapFuture.get();

            // 检查响应状态
            if (qpsResponse.getCode() != 0) {
                return "查询QPS失败：" + qpsResponse.getMessage();
            }
            if (cpuResponse.getCode() != 0) {
                return "查询CPU失败：" + cpuResponse.getMessage();
            }
            if (heapResponse.getCode() != 0) {
                return "查询Heap失败：" + heapResponse.getMessage();
            }

            // 解析QPS数据并保留一位小数
            double maxQps = Math.round(qpsResponse.getData().getMaxServerQps() * 10.0) / 10.0;
            double avgQps = Math.round(qpsResponse.getData().getAvgServerQps() * 10.0) / 10.0;

            // 解析CPU数据（百分比）
            double maxCpu = cpuResponse.getData().getMax();
            double avgCpu = cpuResponse.getData().getAvg();

            // 解析Heap数据（MB）
            double maxHeap = heapResponse.getData().getMax();
            double avgHeap = heapResponse.getData().getAvg();
            double heapQuota = heapResponse.getData().getHeapQuota();

            // 拼接返回字符串
            return String.format(
                    "当前应用：%s，近五分钟的指标如下：\n" +
                    "QPS - 最大：%.1f，平均：%.1f\n" +
                    "CPU使用率 - 最大：%.2f%%，平均：%.2f%%\n" +
                    "堆内存使用 - 最大：%.2f MB，平均：%.2f MB",
                    application, maxQps, avgQps, maxCpu, avgCpu, maxHeap, avgHeap);
        } catch (Exception e) {
            log.error("查询应用{}指标失败", application, e);
            return "查询失败：" + e.getMessage();
        }
    }

    /**
     * 获取指定类型的应用指标
     *
     * @param application 应用名称
     * @param startTime 开始时间戳（秒）
     * @param endTime 结束时间戳（秒）
     * @param type 指标类型（qps、cpu、heap）
     * @return 指标响应对象
     */
    private ApplicationMetricsResponse fetchMetrics(String application, long startTime, long endTime, String type) {
        try {
            // 构建请求URL
            String url = String.format("%s?application=%s&startTime=%d&endTime=%d&searchType=application&type=%s",
                    metricsUrl, application, startTime, endTime, type);
            
            log.info("发送{}请求URL: {}", type, url);

            // 发送HTTP GET请求
            String responseBody = sendHttpRequest(url);
            log.info("接收{}响应内容: {}", type, responseBody);

            // 解析响应
            return parseResponse(responseBody);
        } catch (Exception e) {
            log.error("查询应用{}的{}指标失败", application, type, e);
            // 返回错误响应
            ApplicationMetricsResponse errorResponse = new ApplicationMetricsResponse();
            errorResponse.setCode(-1);
            errorResponse.setMessage("请求失败：" + e.getMessage());
            return errorResponse;
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

