package run.mone.openapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.unfbx.chatgpt.entity.edits.Edit;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.entity.embeddings.Item;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.entity.whisper.WhisperResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import run.mone.openai.OpenaiCall;
import run.mone.openai.net.FakeDnsResolver;
import run.mone.openai.net.MyConnectionSocketFactory;
import run.mone.openai.net.MySSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/3/9 11:11
 */
public class OpenApiTest {

    private static String proxyAddr = System.getenv("open_api_proxy");

    private Gson gson = new Gson();

    @SneakyThrows
    int sum(int a, int b) {
        TimeUnit.SECONDS.sleep(3);
        return a + b;
    }

    @Test
    public void testSingle() {
        Single.just(sum(11, 22)).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onSuccess(Integer integer) {
                System.out.println(integer);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    @SneakyThrows
    @Test
    public void test0() {
        boolean useProxy = false;
        Proxy proxy = null;
        if (useProxy) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        }
        String key = System.getenv("open_api_key");
        OpenAiStreamClient client = new OpenAiStreamClient(key, 50, 50, 50, proxy);
        ChatCompletion completion = ChatCompletion.builder().messages(Lists.newArrayList(Message.builder().role(Message.Role.USER).content("史记的作者是谁?").build())).build();
        client.streamChatCompletion(completion, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if (data.equals("[DONE]")) {
                    System.out.println("");
                    return;
                }
                String obj = JSON.parseObject(data).getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content");
                if (null != obj) {
                    System.out.print(obj);
                }
            }
        });
        System.in.read();
    }

    @Test
    public void testCreateEdit() {
        client().edit(Edit.builder().input("zzy new").model("text-davinci-edit-001").instruction("zzy new").build());
    }


    private OpenAiClient client() {
//        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(System.getenv("open_api_key"))
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .apiHost("https://api.openai.com/")
//                .proxy(proxy)
                .build();
        return openAiClient;
    }


    @Test
    public void testMessage() {
        OpenAiClient openAiClient = client();
        List<Message> list = Lists.newArrayList(
                Message.builder().role(Message.Role.SYSTEM).content("根据注释获取api信息").build(),
                Message.builder().role(Message.Role.ASSISTANT).content("你好！有什么我可以帮助您的吗？").build(),
                Message.builder().role(Message.Role.USER).content("我给你一些api和注释的信息,请你记住.他们的格式是 注释:负责人:服务名:api").build(),
                Message.builder().role(Message.Role.USER).content("获取用户信息:zzy:UserService:User getUser(String id)").build(),
                Message.builder().role(Message.Role.USER).content("删除用户信息:zzy:UserService:void delUser(String id)").build(),
                Message.builder().role(Message.Role.USER).content("请告诉我获取用户信息的相关信息").build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(list).build();
        ChatCompletionResponse completions = openAiClient.chatCompletion(chatCompletion);
        completions.getChoices().forEach(System.out::println);
    }

    @Test
    public void testContext() {
        String q = "获取用户信息的相关信息";
        OpenAiClient openAiClient = client();
        String context = "我有一些api信息.信息是用':'隔开的,他们的格式是 注释:负责人:服务名:api.信息如下.获取用户信息:zzy:UserService:User getUser(String id).删除用户信息:zzy:UserService:void delUser(String id)\r\n" +
                "根据这些情况,我想知道:";
        List<Message> list = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(context + q).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(list).build();
        ChatCompletionResponse completions = openAiClient.chatCompletion(chatCompletion);
        completions.getChoices().forEach(System.out::println);
    }

    @Test
    public void testContext2() {
        String res = OpenaiCall.call(null, "我提供一个操作指南.内容的格式是命令->cmd.打开电视:open tv,播放音乐:start music,打开浏览器:open chrome.\r\n" +
                "我现在给你命令,请你给我cmd.我的命令是:%s", "播放音乐");
        System.out.println(res);
    }


    @Data
    class C implements Comparable<C> {
        private int id;
        private String content;
        private double v;

        @Override
        public int compareTo(@NotNull C o) {
            return this.v - o.v > 0 ? -1 : 1;
        }
    }

    @Test
    public void testSaveEmbedding() throws IOException {
        OpenAiClient client = client();
        AtomicInteger i = new AtomicInteger();

        list.stream().forEach(s -> {
            EmbeddingResponse res = client.embeddings(s);
            List<Item> data = res.getData();
            System.out.println(data + ":" + data.size());
            double[] array = data.get(0).getEmbedding().stream().mapToDouble(it -> it.doubleValue()).toArray();
            System.out.println(Arrays.toString(array));
            String str = gson.toJson(array);
            try {
                Files.write(Paths.get("/tmp/v" + (i.incrementAndGet())), str.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    @Test
    public void testQEmbedding() {
        OpenAiClient client = client();
        String q = "查询用户信息";
        EmbeddingResponse res = client.embeddings(q);
        List<BigDecimal> list = res.getData().get(0).getEmbedding();
        double[] d = list.stream().mapToDouble(it -> it.doubleValue()).toArray();
        System.out.println(Arrays.toString(d));
        Files.write(Paths.get("/tmp/q"), gson.toJson(d).getBytes());
    }


    private double[] getValue(String q) {
        OpenAiClient client = client();
        EmbeddingResponse res = client.embeddings(q);
        List<BigDecimal> list = res.getData().get(0).getEmbedding();
        double[] d = list.stream().mapToDouble(it -> it.doubleValue()).toArray();
        return d;
    }

    private List<String> list = Lists.newArrayList("获取用户信息:zzy:UserService:User getUser(String id)", "查询数据库信息:zzy:UserService:void delUser(String id)");

    @SneakyThrows
    private List<OpenaiCall.D> getDlist() {
        Type typeOfT = new TypeToken<double[]>() {
        }.getType();
        List<OpenaiCall.D> l = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            String c = new String(Files.readAllBytes(Paths.get("/tmp/v" + i)));
            double[] cc = gson.fromJson(c, typeOfT);
            OpenaiCall.D o = OpenaiCall.D.builder().value(cc).content(list.get(i - 1)).build();
            l.add(o);
        }
        return l;
    }


    @SneakyThrows
    @Test
    public void testEmbedding2() {
        Type typeOfT = new TypeToken<double[]>() {
        }.getType();
        String question = "获取User信息";
        double[] qq = getValue(question);
        System.out.println(qq);
        List<C> l = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            String c = new String(Files.readAllBytes(Paths.get("/tmp/v" + i)));
            double[] cc = gson.fromJson(c, typeOfT);
            double v = cosineSimilarity(cc, qq);
            System.out.println(v);
            C o = new C();
            o.setContent(list.get(i - 1));
            o.setId(i - 1);
            o.setV(v);
            l.add(o);
        }
        l = l.stream().sorted().collect(Collectors.toList());
        System.out.println(l);
        OpenAiClient client = client();
        String context = "我有一些api信息.信息是用':'隔开的.他们的格式是 注释:负责人:服务名:api.信息如下." + l.get(0).getContent() + "\r\n" +
                "根据这些情况,我想知道:" + question;
        System.out.println(context);

        List<Message> list = Lists.newArrayList(
                Message.builder().role(Message.Role.USER).content(context + question).build()
        );
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(list).build();
        ChatCompletionResponse completions = client.chatCompletion(chatCompletion);
        completions.getChoices().forEach(System.out::println);
    }


    @Test
    public void testCosineSimilarity() {
        double[] vectorA = {1, 2, 3};
        double[] vectorB = {4, 5, 6};
        // 计算余弦相似度
        double cosineSimilarity = cosineSimilarity(vectorA, vectorB);
        // 输出余弦相似度
        System.out.println("Cosine similarity: " + cosineSimilarity);
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        RealVector a = new ArrayRealVector(vectorA);
        RealVector b = new ArrayRealVector(vectorB);
        return a.cosine(b);
    }


    @Test
    public void test1() {
        OpenAiClient openAiClient = client();
        Completion completion = Completion.builder().prompt("调用chatgpt openapi 如何返回中文").build();
        CompletionResponse completions = openAiClient.completions(completion);
        Arrays.stream(completions.getChoices()).forEach(System.out::println);
    }

    @Test
    public void testListModels() {
        OpenAiClient openAiClient = client();
        openAiClient.models().forEach(it -> {
            System.out.println(it.getID());
        });
    }


    @SneakyThrows
    @Test
    public void test2() {
        InetSocketAddress addr = new InetSocketAddress(proxyAddr, 65522);
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("socks.address", addr);

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();

        HttpPost request = new HttpPost("https://api.openai.com/v1/completions");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + System.getenv("open_api_key"));

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-davinci-003");
        requestBody.put("prompt", "三国演义是一本好书吗?");
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
        System.out.println(text);
    }

    @Test
    public void testGenImage() {
        OpenAiClient openAiClient = client();
        ImageResponse res = openAiClient.genImages("三亚土拨鼠");
        res.getData().forEach(it -> {
            System.out.println(it);
        });
    }

    @Test
    public void testSpeechToText() {
        OpenAiClient openAiClient = client();
        WhisperResponse res = openAiClient.speechToTextTranscriptions(new File("/tmp/ghsy.mp3"));
        String text = res.getText();
        System.out.println(text);
        Assert.assertTrue(text.length() > 0);
    }

    @Test
    public void testCall() {
        String res = OpenaiCall.call(null, "我有一些api信息.信息是用':'隔开的.他们的格式是 注释:负责人:服务名:api.信息如下:%s.\r\n根据上边的内容,我想知道:%s的相关信息", "User", getDlist(), false, 1);
        System.out.println(res);
    }


    /**
     * 测试通过投喂的filter代码生成想要生成的代码
     */
    @SneakyThrows
    @Test
    public void testCreateFilter() {
        String str = new String(Files.readAllBytes(Paths.get("/tmp/filter")));
        String req = new String(Files.readAllBytes(Paths.get("/tmp/filter_req")));
        String res = OpenaiCall.call(null, str + "+\r\n" + "%s", req);
        System.out.println(res);
    }

    @Test
    @SneakyThrows
    public void testCreateFilter2() {
        String str = new String(Files.readAllBytes(Paths.get("/tmp/filter")));
        String req = new String(Files.readAllBytes(Paths.get("/tmp/filter_req")));
        Stopwatch sw = Stopwatch.createStarted();
        String res = OpenaiCall.callWithHttpClient(System.getenv("open_api_key"), str + "+\r\n" + req, System.getenv("open_api_proxy"));
        System.out.println(res);
        System.out.println(sw.elapsed(TimeUnit.SECONDS));
    }


}
