package run.mone.hive.http;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.bo.CallReportDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 调用次数上报客户端
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Slf4j
public class CallCountReportClient {
    
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    
    private static String reportUrl = "http://localhost:8080/api/v1/report/call";
    
    /**
     * 设置上报URL
     * 
     * @param url 上报URL
     */
    public static void setReportUrl(String url) {
        reportUrl = url;
    }
    
    /**
     * 同步上报调用次数
     * 
     * @param reportDTO 上报数据
     * @return 是否成功
     */
    public static boolean report(CallReportDTO reportDTO) {
        try {
            String jsonBody = gson.toJson(reportDTO);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(reportUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.debug("Successfully reported call count for {}.{}", 
                        reportDTO.getClassName(), reportDTO.getMethodName());
                return true;
            } else {
                log.warn("Failed to report call count, status code: {}, response: {}", 
                        response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("Error reporting call count for {}.{}", 
                    reportDTO.getClassName(), reportDTO.getMethodName(), e);
            return false;
        }
    }
    
    /**
     * 异步上报调用次数
     * 
     * @param reportDTO 上报数据
     * @return CompletableFuture
     */
    public static CompletableFuture<Boolean> reportAsync(CallReportDTO reportDTO) {
        return CompletableFuture.supplyAsync(() -> report(reportDTO));
    }
}

