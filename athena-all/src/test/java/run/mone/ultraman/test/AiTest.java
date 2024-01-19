package run.mone.ultraman.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import run.mone.m78.ip.bo.chatgpt.Completions;
import run.mone.m78.ip.bo.chatgpt.Format;
import run.mone.m78.ip.bo.chatgpt.Message;
import run.mone.m78.ip.service.LocalAiService;
import org.junit.Test;

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
                .model("gpt-4-1106-preview").messages(Lists.newArrayList(Message.builder().role("system").content("你是我的ai助手,请返回json格式数据").build(),Message.builder().role("user").content("1+1=?").build())).build();
        JsonObject res = LocalAiService.call(gson.toJson(completions));
        System.out.println(res);
    }

}
