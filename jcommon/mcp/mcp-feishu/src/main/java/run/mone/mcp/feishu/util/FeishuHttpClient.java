package run.mone.mcp.feishu.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class FeishuHttpClient {
    private final String appId;
    private final String appSecret;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private String accessToken;
    private long tokenExpireTime;

    private static final String BASE_URL = "https://open.feishu.cn/open-apis";

    public FeishuHttpClient(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> post(String path, Map<String, Object> body) throws Exception {
        ensureAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + path,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("API request failed with status: " + response.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("code") || !Integer.valueOf(0).equals(responseBody.get("code"))) {
            throw new Exception("API request failed: " + responseBody);
        }

        return responseBody;
    }

    // 给我写一个get请求
    public Map<String, Object> get(String path, Map<String, String> queryParams) throws Exception {
        ensureAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + path);
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("API request failed with status: " + response.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("code") || !Integer.valueOf(0).equals(responseBody.get("code"))) {
            throw new Exception("API request failed: " + responseBody);
        }

        return responseBody;
    }


    private void ensureAccessToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "app_id", appId,
                "app_secret", appSecret
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/auth/v3/tenant_access_token/internal",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed to get access token");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("tenant_access_token")) {
            throw new Exception("Invalid token response");
        }

        accessToken = (String) responseBody.get("tenant_access_token");
        int expireIn = ((Number) responseBody.get("expire")).intValue();
        tokenExpireTime = System.currentTimeMillis() + (expireIn - 60) * 1000L; // 提前60秒过期
    }
} 