package run.mone.mcp.hera.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mcp.hera.analysis.api.IDubboInterfaceQpsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Dubbo接口QPS查询服务实现类
 */
@Slf4j
@Service
@DubboService(timeout = 10000, group = "${dubbo.group}", version = "1.0")
public class DubboInterfaceQpsService implements IDubboInterfaceQpsService {

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
    @Override
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

            // 提取Prometheus响应中的数据
            JsonObject dataWrapper = jsonResponse.has("data") ? jsonResponse.getAsJsonObject("data") : new JsonObject();
            JsonObject promData = dataWrapper.has("data") ? dataWrapper.getAsJsonObject("data") : new JsonObject();

            // 解析时间序列数据
            // 注意：每个数据点是30秒内的平均QPS
            double maxQps = 0.0;
            double minQps = Double.MAX_VALUE;
            double avgQps = 0.0;
            double totalQps = 0.0;
            int dataCount = 0;

            if (promData.has("result") && promData.get("result").isJsonArray()) {
                JsonArray resultArray = promData.getAsJsonArray("result");

                List<Double> allValues = new ArrayList<>();

                for (int i = 0; i < resultArray.size(); i++) {
                    JsonObject result = resultArray.get(i).getAsJsonObject();

                    if (result.has("values") && result.get("values").isJsonArray()) {
                        JsonArray valuesArray = result.getAsJsonArray("values");

                        for (int j = 0; j < valuesArray.size(); j++) {
                            JsonArray valueEntry = valuesArray.get(j).getAsJsonArray();
                            if (valueEntry.size() >= 2) {
                                // 第二个元素是QPS值（每30秒的平均QPS）
                                String qpsStr = valueEntry.get(1).getAsString();
                                double qpsValue = Double.parseDouble(qpsStr);
                                allValues.add(qpsValue);
                                totalQps += qpsValue;
                            }
                        }
                    }
                }

                // 计算统计信息
                if (!allValues.isEmpty()) {
                    DoubleSummaryStatistics stats = allValues.stream()
                            .mapToDouble(Double::doubleValue)
                            .summaryStatistics();

                    maxQps = stats.getMax();
                    minQps = stats.getMin();
                    avgQps = stats.getAverage();
                    dataCount = allValues.size();
                }
            }

            // 格式化返回结果
            return String.format(
                    "应用：%s\nDubbo服务：%s\nDubbo方法：%s\n服务区域：%s\n时间范围：%d - %d\n\n数据统计信息：\n  数据点数：%d（每点采样间隔：30秒）\n  总时间长：%d秒\n\nQPS统计（每30秒的平均值）：\n  最大值：%.2f QPS\n  平均值：%.2f QPS\n  最小值：%.2f QPS\n  所有点总和：%.2f",
                    appName, serviceName, methodName, serverZone, startTimeSec, endTimeSec,
                    dataCount, (endTimeSec - startTimeSec), maxQps, avgQps, minQps, totalQps);

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
