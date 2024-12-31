package run.mone.ultraman.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Completions;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Format;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Message;
import com.xiaomi.youpin.tesla.ip.service.LocalAiService;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.openai.StreamListener;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 21:25
 */
public class AiTest {

    private Gson gson = new Gson();


    @Test
    public void testCall() {
        Completions completions = Completions.builder()
                .stream(false)
                .response_format(Format.builder().build())
//                .model("gpt-4-1106-preview")
                .model("gpt-4o")
                .messages(Lists.newArrayList(Message.builder().role("system").content("你是我的ai助手,请返回json格式数据").build(),Message.builder().role("user").content("1+1=?").build())).build();
        JsonObject res = LocalAiService.call(gson.toJson(completions));
        System.out.println(res);
    }

    @SneakyThrows
    @Test
    public void testCall2() {
        Completions completions = Completions.builder()
                .stream(true)
                .response_format(Format.builder().build())
//                .model("gpt-4-1106-preview")
                .model("gpt-4o")
                .messages(Lists.newArrayList(Message.builder().role("system").content("你是我的ai助手,请返回json格式数据").build(),Message.builder().role("user").content("1+1=?").build())).build();
        LocalAiService.completions(gson.toJson(completions), str -> {
            System.out.println(str);
        });

        TimeUnit.MINUTES.sleep(1);
    }

}
