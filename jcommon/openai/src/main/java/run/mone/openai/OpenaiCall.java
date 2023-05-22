package run.mone.openai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.edits.Edit;
import com.unfbx.chatgpt.entity.edits.EditResponse;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
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
import org.jetbrains.annotations.Nullable;
import run.mone.openai.common.MutableObject;
import run.mone.openai.net.FakeDnsResolver;
import run.mone.openai.net.MyConnectionSocketFactory;
import run.mone.openai.net.MySSLConnectionSocketFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/4/11 14:37
 */
@Slf4j
public class OpenaiCall {

    private static String proxyAddr = System.getenv("open_api_proxy");


    public static OpenAiClient client(String apiKey) {
        return client(apiKey, null);
    }

    public static OpenAiClient client(String apiKey, String openApiHost) {
        String proxyAddr = System.getenv("open_api_proxy");
        Proxy proxy = null;
        if (null != proxyAddr && proxyAddr.length() > 0) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());

        String key = System.getenv("open_api_key");
        if (null != apiKey && apiKey.length() > 0) {
            key = apiKey;
        }

        String host = "https://api.openai.com/";
        String hostAddr = System.getenv("open_api_host");
        if (null != hostAddr && hostAddr.length() > 0) {
            log.info("use open aip host:{}", hostAddr);
            host = hostAddr;
        }

        if (null != openApiHost && openApiHost.length() > 0) {
            host = openApiHost;
        }

        OpenAiClient.Builder builer = OpenAiClient.builder()
                .apiKey(key)
                .connectTimeout(5000)
                .writeTimeout(5000)
                .readTimeout(5000)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .apiHost(host);

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
    public static String call(String apiKey, String context, String prompt, List<D> list, boolean call, int num) {
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
        List<C> l = result.stream().sorted().limit(num).collect(Collectors.toList());
        //直接拿到近似的,不再发到openai询问答案了
        if (!call) {
            return l.stream().map(it -> it.getContent()).collect(Collectors.joining("\r\n"));
        }
        String content = String.format(context, l.get(0).getContent(), prompt);
        OpenAiClient client = client(apiKey);
        List<Message> messages = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(content).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        ChatCompletionResponse completions = client.chatCompletion(chatCompletion);
        return completions.getChoices().get(0).getMessage().getContent();
    }


    public static String call(String apiKey, String context, String prompt) {
        return call(apiKey, null, context, prompt);
    }

    /**
     * 调用
     *
     * @param apiKey
     * @param proxy   这个proxy是nginx的反向代理
     * @param context
     * @param prompt
     * @return
     */
    public static String call(String apiKey, String proxy, String context, String prompt) {
        return call(apiKey, proxy, context, prompt, 0.2f);
    }

    public static void callStream(String apiKey, String openApiHost, String context, String[] prompt, StreamListener listener) {
        callStream(apiKey, openApiHost, context, prompt, listener, ReqConfig.builder().build());
    }

    public static String editor(String apiKey, Edit edit) {
        OpenAiClient client = new OpenAiClient(apiKey);
        EditResponse res = client.edit(edit);
        return res.getChoices()[0].getText();
    }

    /**
     * 流式返回结果
     *
     * @param apiKey
     * @param openApiHost
     * @param context
     * @param prompt
     * @param listener
     * @param config
     */
    public static void callStream(String apiKey, String openApiHost, String context, String[] prompt, StreamListener listener, ReqConfig config) {
        List<Message> messages = Lists.newArrayList(Message.builder().role(Message.Role.USER).content(String.format(context, prompt)).build());
        callStream(apiKey, openApiHost, messages, listener, config);
    }


    /**
     * 支持多伦问答
     *
     * @param apiKey
     * @param openApiHost
     * @param messages
     * @param listener
     * @param config
     */
    public static void callStream(String apiKey, String openApiHost, List<Message> messages, StreamListener listener, ReqConfig config) {
        OpenAiStreamClient client = new OpenAiStreamClient(apiKey, 50, 50, 50);

        if (null != openApiHost) {
            try {
                Field field = client.getClass().getDeclaredField("apiHost");
                field.setAccessible(true);
                field.set(client, openApiHost);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        ChatCompletion.ChatCompletionBuilder builder = ChatCompletion.builder().messages(messages);

        if (config.getMaxTokens() > 0) {
            builder.maxTokens(config.getMaxTokens());
        }
        builder.model(config.getModel());
        builder.temperature(config.getTemperature());

        ChatCompletion completion = builder.build();
        client.streamChatCompletion(completion, new EventSourceListener() {

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                listener.begin();
            }

            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String str) {
                String data = parse(str);
                listener.onEvent(data);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                listener.end();
            }
        });
    }


    private static OpenAiStreamClient streamClient(String apiKey, String openApiHost) {
        OpenAiStreamClient client = new OpenAiStreamClient(apiKey, 50, 50, 50);

        if (null != openApiHost) {
            try {
                Field field = client.getClass().getDeclaredField("apiHost");
                field.setAccessible(true);
                field.set(client, openApiHost);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }


    /**
     * 有多个问题,其中的部分答案会当做问题继续提问,最后一个问题是流式返回
     *
     * @param apiKey
     * @param openApiHost
     * @param messages    (这里会包含若干个Ask)
     * @param listener
     * @param config
     */
    public static void callStreamWithAsk(String apiKey, String openApiHost, List<Message> messages, StreamListener listener, ReqConfig config) {
        ChatCompletion.ChatCompletionBuilder builder = ChatCompletion.builder();

        if (config.getMaxTokens() > 0) {
            builder.maxTokens(config.getMaxTokens());
        }
        builder.model(config.getModel());
        builder.temperature(config.getTemperature());

        List<Message> messageList = new ArrayList<>();
        int i = 0;

        while (true) {
            MutableObject<Ask> mo = new MutableObject<>();
            for (; i < messages.size(); i++) {
                Message m = messages.get(i);
                if (m instanceof Ask) {
                    Ask ask = (Ask) m;
                    if (!ask.isFinish()) {
                        messageList.add(ask);
                        mo.setData(ask);
                        i++;
                        break;
                    }
                }
                messageList.add(m);
            }

            while (mo.getData().isBegin()){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //最后一个问题
            if (mo.getData().isStream()) {
                List<Message> qList = messageList.stream().map(it -> {
                    if (it instanceof Ask) {
                        Ask a = (Ask) it;
                        return Message.builder().content(a.getContent()).role(Message.Role.USER).build();
                    }
                    return it;
                }).collect(Collectors.toList());

                builder.messages(qList);

                streamClient(apiKey, openApiHost).streamChatCompletion(qList, new EventSourceListener() {

                    @Override
                    public void onOpen(EventSource eventSource, Response response) {
                        listener.begin();
                    }

                    @Override
                    public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String str) {
                        String data = parse(str);
                        listener.onEvent(data);
                    }

                    @Override
                    public void onClosed(EventSource eventSource) {
                        listener.end();
                    }
                });
                break;
            } else {
                //这里会阻塞
                List<Message> qList = messageList.stream().map(it -> {
                    if (it instanceof Ask) {
                        Ask a = (Ask) it;
                        return Message.builder().content(a.getContent()).role(Message.Role.USER).build();
                    }
                    return it;
                }).collect(Collectors.toList());
                ChatCompletionResponse res = client(apiKey, openApiHost).chatCompletion(qList);
                String resMessage = res.getChoices().get(0).getMessage().getContent();
                messageList.add(Message.builder().role(Message.Role.ASSISTANT).content(resMessage).build());
                mo.getData().setFinish(true);
                mo.getData().getAskListener().end(resMessage);
            }

        }
    }


    private static String parse(String data) {
        if (data.equals("[DONE]")) {
            return "";
        }
        String obj = JSON.parseObject(data).getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content");
        if (null != obj) {
            return obj;
        }
        return "";
    }


    public static String call(String apiKey, String proxy, String context, String prompt, double temperature) {
        OpenAiClient openAiClient = client(apiKey, proxy);
        String content = String.format(context, prompt);
        List<Message> list = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(content).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().temperature(temperature).messages(list).build();
        ChatCompletionResponse completions = openAiClient.chatCompletion(chatCompletion);
        return completions.getChoices().get(0).getMessage().getContent();
    }


    public static String call(String apiKey, String proxy, List<Message> messageList, double temperature) {
        OpenAiClient openAiClient = client(apiKey, proxy);
        ChatCompletion chatCompletion = ChatCompletion.builder().temperature(temperature).messages(messageList).build();
        ChatCompletionResponse completions = openAiClient.chatCompletion(chatCompletion);
        return completions.getChoices().get(0).getMessage().getContent();
    }


    /**
     * 最原始的调用方式,做技术储备,不建议直接使用
     *
     * @param apiKey
     * @param prompt
     * @param proxy
     * @return
     */
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
