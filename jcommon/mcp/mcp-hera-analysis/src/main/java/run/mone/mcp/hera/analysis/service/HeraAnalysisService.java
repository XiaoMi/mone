package run.mone.mcp.hera.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.StringUtils;
import run.mone.mcp.hera.analysis.model.HeraAnalysisRequest;
import run.mone.mcp.hera.analysis.model.HeraAnalysisResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Hera分析服务实现类
 *
 * @author dingtao
 */
@Slf4j
@Service
public class HeraAnalysisService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${hera.analysis.api.url}")
    private String apiUrl;

    @Value("${hera.analysis.api.auth}")
    private String authorization;

    @Value("${hera.analysis.api.username}")
    private String userName;

    @Value("${hera.analysis.api.flowId}")
    private String flowId;

    /**
     * 根据traceId分析根本原因
     *
     * @param traceId   追踪ID
     * @param env       环境
     * @return 分析结果
     */
    public String analyzeTraceRoot(String traceId, String env) {
        try {
            HeraAnalysisRequest request = new HeraAnalysisRequest();
            request.setFlowId(flowId);
            request.getInput().setTraceId(traceId);
            request.getInput().setEnv(env);
            request.setOperateCmd("testFlow");
            request.setUserName(userName);

            String requestBody = objectMapper.writeValueAsString(request);
            log.info("发送请求内容: {}", requestBody);

            String responseBody = sendHttpRequest(requestBody);
            log.info("接收响应内容: {}", responseBody);

            HeraAnalysisResponse response = parseResponse(responseBody);
            
            if (response.getCode() != 0) {
                return "分析失败：" + response.getMessage();
            }
            
            String result;
            if (!StringUtils.isEmpty(response.getData().getResult().getLlmResult())) {
                result = response.getData().getResult().getLlmResult();
            } else {
                result = response.getData().getResult().getCacheResult();
            }
            
            return result;
        } catch (Exception e) {
            log.error("分析traceId:{}失败", traceId, e);
            return "分析失败：" + e.getMessage();
        }
    }

    /**
     * 发送HTTP请求到Hera分析接口
     *
     * @param requestBody 请求体
     * @return 响应字符串
     * @throws IOException 网络异常
     */
    private String sendHttpRequest(String requestBody) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", authorization);
            httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
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
    private HeraAnalysisResponse parseResponse(String responseBody) throws IOException {
        return objectMapper.readValue(responseBody, HeraAnalysisResponse.class);
    }
} 