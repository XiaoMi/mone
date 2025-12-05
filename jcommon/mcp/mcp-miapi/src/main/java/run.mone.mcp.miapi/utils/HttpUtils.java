package run.mone.mcp.miapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import run.mone.mcp.miapi.bo.GatewayResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HttpUtils {
    private static final String BASE_URL = System.getenv("gateway_host");
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public HttpUtils() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }


    public <T> String request(String url, Map<String, Object> params, Class<?> clazz) throws JsonProcessingException {
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(params),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();

        OkHttpClient miapiClient = client.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        try {
            try (Response response = miapiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("miapi mcp response: {}", responseBody);

                GatewayResponse<T> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(GatewayResponse.class, clazz)
                );
                String resultText = "";

                if (apiResponse.getCode() != 0) {
                    resultText = Optional.ofNullable(apiResponse.getDetailMsg()).orElse(apiResponse.getMessage());
                } else {
                    resultText = String.format("%s", apiResponse.getData());
                }


                return resultText;
            }
        }catch (Exception e) {
            log.error("HttpUtils.request error:", e);
        }
        return "";
    }
}
