package run.mone.mcp.custommodel.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.mcp.custommodel.model.IntentRequest;
import run.mone.mcp.custommodel.model.NormalizeRequest;
import run.mone.mcp.custommodel.model.PredictRequest;

import java.io.IOException;
import java.util.List;
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

    @Value("${custom.model.predict.url:http://localhost:8080/predict}")
    private String predictUrl;

    @Value("${custom.model.predict.model-type:bert}")
    private String predictModelType;

    @Value("${custom.model.predict.version:finetune-bert-20250602-6a4d6703}")
    private String predictVersion;

    @Value("${custom.model.predict.top-k:1}")
    private Integer predictTopK;

    public CustomModelService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String recognizeIntent(String userMessage) {
        IntentRequest request = new IntentRequest();
        request.setUser_message(userMessage);

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
        request.setUser_message(userMessage);

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

    public String predictTexts(List<String> texts) {
        PredictRequest request = new PredictRequest();
        request.setModel_type(predictModelType);
        request.setVersion(predictVersion);
        request.setTexts(texts);
        request.setTop_k(predictTopK);

        try {
            RequestBody body = RequestBody.create(
                    gson.toJson(request), 
                    MediaType.parse("application/json")
            );

            Request httpRequest = new Request.Builder()
                    .url(predictUrl)
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
            log.error("Error calling text prediction service", e);
            throw new RuntimeException("Failed to call text prediction service", e);
        }
    }
}