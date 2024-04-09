package run.mone.local.docean.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.http.okhttp.OkHttpClientBuilder;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 14:17
 */
@Slf4j
public class HttpUtils {


    //使用okhttp进行一次post请求,参数是JsonObject(class)
    public static JsonElement postJson(String url, JsonObject json) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //返回一个JsonElement
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseData = response.body().string();
            log.info("ai response:{}", responseData);
            if (StringUtils.isEmpty(responseData)) {
                return null;
            }
            JsonParser parser = new JsonParser();
            return parser.parse(responseData);
        }

    }

    public static String buildUrlWithParameters(String baseUrl, Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return baseUrl;
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (!isFirst) {
                queryStringBuilder.append("&");
            }
            try {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String encodedValue = URLEncoder.encode((String) entry.getValue(), StandardCharsets.UTF_8);
                queryStringBuilder.append(encodedKey).append("=").append(encodedValue);
            } catch (Exception e) {
                // 异常返回空值
                return null;
            }
            isFirst = false;
        }

        String queryString = queryStringBuilder.toString();
        if (!queryString.isEmpty()) {
            if (!baseUrl.contains("?")) {
                baseUrl += "?";
            } else {
                // 如果URL已经包含查询参数，确保它们之间有一个'&'
                if (!baseUrl.endsWith("&")) {
                    baseUrl += "&";
                }
            }
            baseUrl += queryString;
        }

        return baseUrl;
    }

}
