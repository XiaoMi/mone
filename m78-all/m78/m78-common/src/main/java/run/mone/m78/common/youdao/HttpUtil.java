package run.mone.m78.common.youdao;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HttpUtil {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();

    public static byte[] doGet(String url, Map<String, String[]> header, Map<String, String[]> params, String expectContentType) {
        Request.Builder builder = new Request.Builder();
        addHeader(builder, header);
        addUrlParam(builder, url, params);
        return requestExec(builder.build(), expectContentType);
    }

    public static byte[] doPost(String url, Map<String, String[]> header, Map<String, String[]> body, String expectContentType) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, header);
        addBodyParam(builder, body, "POST");
        return requestExec(builder.build(), expectContentType);
    }

    public static byte[] doPostV2(String url, String reqStr) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, reqStr);
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .readTimeout(Duration.ofSeconds(20))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                log.error("请求失败，url:{},状态码：{}", url, response.code());
                return null;
            }
        } catch (Exception e) {
            log.error("请求异常，url:{},err:{}", url, e);
            return null;
        }
    }

    private static void addHeader(Request.Builder builder, Map<String, String[]> header) {
        if (header == null) {
            return;
        }
        for (String key : header.keySet()) {
            String[] values = header.get(key);
            if (values != null) {
                for (String value : values) {
                    builder.addHeader(key, value);
                }
            }
        }
    }

    private static void addUrlParam(Request.Builder builder, String url, Map<String, String[]> params) {
        if (params == null) {
            return;
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            if (values != null) {
                for (String value : values) {
                    urlBuilder.addQueryParameter(key, value);
                }
            }
        }
        builder.url(urlBuilder.build());
    }

    private static void addBodyParam(Request.Builder builder, Map<String, String[]> body, String method) {
        if (body == null) {
            return;
        }
        FormBody.Builder formBodyBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (String key : body.keySet()) {
            String[] values = body.get(key);
            if (values != null) {
                for (String value : values) {
                    formBodyBuilder.add(key, value);
                }
            }
        }
        builder.method(method, formBodyBuilder.build());
    }

    private static byte[] requestExec(Request request, String expectContentType) {
        Objects.requireNonNull(request, "okHttp request is null");

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                ResponseBody body = response.body();
                if (body != null) {
                    String contentType = response.header("Content-Type");
                    if (contentType != null && !contentType.contains(expectContentType)) {
                        String res = new String(body.bytes(), StandardCharsets.UTF_8);
                        System.out.println(res);
                        return null;
                    }
                    return body.bytes();
                }
                System.out.println("response body is null");
            } else {
                System.out.println("request failed, http code: " + response.code());
            }
        } catch (IOException ioException) {
            System.out.println("request exec error: " + ioException.getMessage());
        }
        return null;
    }
}
