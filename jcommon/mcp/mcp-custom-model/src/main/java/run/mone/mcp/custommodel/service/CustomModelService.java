package run.mone.mcp.custommodel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mcp.custommodel.model.ChatRequest;
import run.mone.mcp.custommodel.model.ChatResponse;
import run.mone.mcp.custommodel.model.IntentRequest;
import run.mone.mcp.custommodel.model.NormalizeRequest;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CustomModelService {
    private static final Gson gson = new Gson();
    private final OkHttpClient client;
    
    @Value("${custom.model.intent.url:http://localhost:8080/recognize_intent}")
    private String intentUrl;
    
    @Value("${custom.model.normalize.url:http://localhost:8080/normalize_question}")
    private String normalizeUrl;

    public CustomModelService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String recognizeIntent(String userMessage) {
        IntentRequest request = new IntentRequest();
        request.setUserMessage(userMessage);

        try {
            RequestBody body = RequestBody.create(
                    gson.toJson(request), 
                    MediaType.parse("application/json")
            );

            Request httpRequest = new Request.Builder()
                    .url(intentUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                return response.body().string();
            }
        } catch (Exception e) {
            log.error("Error calling intent recognition service", e);
            throw new RuntimeException("Failed to call intent recognition service", e);
        }
    }

    public String normalizeQuestion(String userMessage) {
        NormalizeRequest request = new NormalizeRequest();
        request.setUserMessage(userMessage);

        try {
            RequestBody body = RequestBody.create(
                    gson.toJson(request), 
                    MediaType.parse("application/json")
            );

            Request httpRequest = new Request.Builder()
                    .url(normalizeUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                return response.body().string();
            }
        } catch (Exception e) {
            log.error("Error calling question normalization service", e);
            throw new RuntimeException("Failed to call question normalization service", e);
        }
    }
}