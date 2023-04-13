package run.mone.openai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.openai.net.FakeDnsResolver;
import run.mone.openai.net.MyConnectionSocketFactory;
import run.mone.openai.net.MySSLConnectionSocketFactory;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/4/11 14:37
 */
@Slf4j
public class OpenaiCall {

    private static String proxyAddr = System.getenv("open_api_proxy");


    private static OpenAiClient client(String apiKey) {
        String proxyAddr = System.getenv("open_api_proxy");
        Proxy proxy = null;
        if (null != proxyAddr && proxyAddr.length() > 0) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());

        String key = System.getenv("open_api_key");
        if (null != apiKey) {
            key = apiKey;
        }

        OpenAiClient.Builder builer = OpenAiClient.builder()
                .apiKey(key)
                .connectTimeout(5000)
                .writeTimeout(5000)
                .readTimeout(5000)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .apiHost("https://api.openai.com/");

        if (null != proxy) {
            builer.proxy(proxy);
        }
        return builer.build();
    }

    public static double[] getEmbeddings(String apiKey, String q) {
        EmbeddingResponse res = client(apiKey).embeddings(q);
        List<BigDecimal> list = res.getData().get(0).getEmbedding();
        double[] d = list.stream().mapToDouble(it -> it.doubleValue()).toArray();
        return d;
    }


    @Data
    static class C implements Comparable<C> {
        private String id;
        private String content;
        private double v;

        @Override
        public int compareTo(@NotNull C o) {
            return this.v - o.v > 0 ? -1 : 1;
        }
    }

    @Builder
    @Data
    public static class D {
        private double[] value;
        private String content;
        private String id;
    }

    /**
     * @param apiKey  chatgpt key
     * @param context 上下文
     * @param prompt  提示词
     * @param list
     * @param call
     * @return
     */
    public static String call(String apiKey, String context, String prompt, List<D> list, boolean call) {
        List<C> result = new ArrayList<>();
        double[] value = getEmbeddings(apiKey, prompt);
        for (int i = 0; i < list.size(); i++) {
            double[] qq = list.get(i).getValue();
            double v = cosineSimilarity(value, qq);
            C o = new C();
            o.setContent(list.get(i).getContent());
            o.setId(list.get(i).getId());
            o.setV(v);
            result.add(o);
        }
        List<C> l = result.stream().sorted().collect(Collectors.toList());
        OpenAiClient client = client(apiKey);
        String content = String.format(context, l.get(0).getContent(), prompt);
        if (!call) {
            return content;
        }
        List<Message> messages = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(content).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        ChatCompletionResponse completions = client.chatCompletion(chatCompletion);
        return completions.getChoices().get(0).getMessage().getContent();
    }


    public static String call(String apiKey, String context, String prompt) {
        OpenAiClient openAiClient = client(apiKey);
        String content = String.format(context, prompt);
        List<Message> list = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(content).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(list).build();
        ChatCompletionResponse completions = openAiClient.chatCompletion(chatCompletion);
        return completions.getChoices().get(0).getMessage().getContent();
    }

    @SneakyThrows
    public static String callWithHttpClient(String apiKey, String prompt, String proxy) {
        HttpClientBuilder builer = HttpClients.custom();
        HttpClientContext context = HttpClientContext.create();
        if (null == proxy) {
            proxy = System.getenv("open_api_proxy");
        }
        if (null != proxy && proxy.length() > 0) {
            log.info("call open api use proxy");
            InetSocketAddress addr = new InetSocketAddress(proxy, 65522);
            context.setAttribute("socks.address", addr);
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new MyConnectionSocketFactory())
                    .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
            builer.setConnectionManager(cm);
        }
        CloseableHttpClient client = builer.build();
        try {
            HttpPost request = new HttpPost("https://api.openai.com/v1/completions");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + apiKey);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", prompt);
            requestBody.put("max_tokens", 2000);
            requestBody.put("temperature", 0);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);

            StringEntity requestEntity = new StringEntity(requestBody.toString(), "utf8");
            request.setEntity(requestEntity);

            HttpResponse response = client.execute(request, context);
            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println(responseString);

            JSONObject obj = JSON.parseObject(responseString, JSONObject.class);
            JSONArray array = obj.getJSONArray("choices");
            String text = array.getJSONObject(0).getString("text");
            return text;
        } finally {
            client.close();
        }
    }


    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        RealVector a = new ArrayRealVector(vectorA);
        RealVector b = new ArrayRealVector(vectorB);
        return a.cosine(b);
    }


}
