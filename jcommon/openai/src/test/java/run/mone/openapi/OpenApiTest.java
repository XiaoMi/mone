package run.mone.openapi;

import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 2023/3/9 11:11
 */
public class OpenApiTest {

    @Test
    public void test1() {
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        //日志输出可以不添加
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(System.getenv("open_api_key"))
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
//                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build();
        CompletionResponse completions = openAiClient.completions("三国里最厉害的武将是谁?");
        Arrays.stream(completions.getChoices()).forEach(System.out::println);
    }

    @SneakyThrows
    @Test
    public void test2() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://api.openai.com/v1/completions");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + System.getenv("open_api_key"));

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-davinci-003");
        requestBody.put("prompt", "Hello");
        requestBody.put("max_tokens", 100);
        requestBody.put("temperature", 0);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.0);


        StringEntity requestEntity = new StringEntity(requestBody.toString());
        request.setEntity(requestEntity);

        HttpResponse response = httpClient.execute(request);
        String responseString = EntityUtils.toString(response.getEntity());


        System.out.println(responseString);

    }
}
