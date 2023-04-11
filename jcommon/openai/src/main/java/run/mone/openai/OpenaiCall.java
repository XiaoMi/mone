package run.mone.openai;

import com.google.common.collect.Lists;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.Builder;
import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;

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
public class OpenaiCall {


    private static OpenAiClient client(String apiKey) {
        String proxyAddr = System.getenv("open_api_proxy");
        Proxy proxy = null;
        if (null != proxyAddr && proxyAddr.length() > 0) {
//            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, 65522));
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());

        String key = System.getenv("open_api_key");
        if (null != apiKey) {
            key = apiKey;
        }

        OpenAiClient.Builder builer = OpenAiClient.builder()
                .apiKey(key)
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .interceptor(Arrays.asList(httpLoggingInterceptor))
                .apiHost("https://api.openai.com/");

        if (null != proxy) {
            builer.proxy(proxy);
        }
        return builer.build();
    }

    private static double[] getValue(String apiKey, String q) {
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

    public static String call(String apiKey, String context, String prompt, List<D> list) {
        List<C> result = new ArrayList<>();
        double[] value = getValue(apiKey, prompt);
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


    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        RealVector a = new ArrayRealVector(vectorA);
        RealVector b = new ArrayRealVector(vectorB);
        return a.cosine(b);
    }


}
