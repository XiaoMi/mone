package run.mone.openapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
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
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/3/9 11:11
 */
public class OpenApiTest {

    private static String proxyAddr = System.getenv("open_api_proxy");

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
    public void test1() {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        //日志输出可以不添加
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(System.getenv("open_api_key"))
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build();
        Completion completion = Completion.builder().prompt("调用chatgpt openapi 如何返回中文").build();
        CompletionResponse completions = openAiClient.completions(completion);
        Arrays.stream(completions.getChoices()).forEach(System.out::println);
    }

    @Test
    public void testListModels() {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(System.getenv("open_api_key"))
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build();
        openAiClient.models().forEach(it->{
            System.out.println(it.getID());
        });
    }


    static class FakeDnsResolver implements DnsResolver {
        @Override
        public InetAddress[] resolve(String host) throws UnknownHostException {
            // Return some fake DNS record for every request, we won't be using it
            return new InetAddress[]{InetAddress.getByAddress(new byte[]{1, 1, 1, 1})};
        }
    }

    // HttpClient 支持socks5代理的自定义类
    static class MyConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress, HttpContext context) throws IOException {
            InetSocketAddress unresolvedRemote = InetSocketAddress.createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        }
    }

    static class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {
        public MySSLConnectionSocketFactory(final SSLContext sslContext) {
            super(sslContext);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                    InetSocketAddress localAddress, HttpContext context) throws IOException {
            InetSocketAddress unresolvedRemote = InetSocketAddress.createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        }
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
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
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

        ImageResponse res = openAiClient.genImages("青蛙");
        res.getData().forEach(it->{
            System.out.println(it);
        });
    }
}
