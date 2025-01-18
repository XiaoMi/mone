package run.mone.m78.client.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import run.mone.m78.client.bot.BotHttpClient;
import run.mone.m78.client.model.History;
import run.mone.m78.client.model.M78BotReq;
import run.mone.m78.client.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 15:57
 */
public class BotHttpClientTest {

    @Test
    public void testBotHttpClient() {
        BotHttpClient client = BotHttpClient.builder()
//                .url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("token").build();
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("北京今天的天气?")
                .build(), null);
        System.out.println(res);
    }

    @Test
    public void testBotHttpClientWithHistory() {
        BotHttpClient client = BotHttpClient.builder()
//                .url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("token").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("desc", " ");
        jsonObject.addProperty("input", "a+b=?");
        jsonObject.add("history", new Gson().toJsonTree(Lists.newArrayList(
                ImmutableMap.of("role", "user", "content", "a=1"),
                ImmutableMap.of("role", "user", "content", "b=2")
        )));
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("hi")
                .build(), jsonObject, BotHttpClient.DEFAULT_FUNCTION);
        System.out.println(res);
    }

    @Test
    public void testBotHttpClientWithHistory2() {
        BotHttpClient client = BotHttpClient.builder()
                .url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("token").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("desc", "");
        jsonObject.addProperty("input", "a+b=?");
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("hi")
                .build(), jsonObject, History.builder()
                .messages(Lists.newArrayList(Message.builder().role("user").content("a=1").build(),
                        Message.builder().role("user").content("b=2").build())).build(), BotHttpClient.DEFAULT_FUNCTION);
        System.out.println(res);
    }


    @Test
    public void testBotHttpClientWithHistory3() {
        List<String> list = new ArrayList<>();
//        String topic = "11.2 为什么大于 11.11";
//        String topic = "陆地上最大的生物是?";
//        String topic = "如何写好代码?";
//        String topic = "一个博客系统有那些必要的模块";
//        String topic = "人如何才能长寿";
        String topic = "书店系统前后端接口如何对齐,讨论都需要哪些接口";
        String input = "我们来讨论下:" + topic;
        int round = 5;
        for (int i = 0; i < round; i++) {
            String res = callBotWithInput("小明", input, list.stream().collect(Collectors.joining("\n")), topic, round, i + 1, "你是一名vue前端", "1.你需要列出后端给你提供的接口描述 2.你会充分采纳前端的意见 3.如果有些接口你觉得你实现更好,你可以建议给后端");
            if (StringUtils.isNotEmpty(input)) {
                list.add("小明:" + input);
                input = "";
            }
            list.add("小红:" + res);
            res = callBotWithInput("小红", "", list.stream().collect(Collectors.joining("\n")), topic, round, i + 1, "你是一名java后端", "1.你会列出具体的接口,比如/book/add 2.你会采纳前端提的需求,并给他生成接口 3.如果前端要求你去掉某个接口,你尽量同意 4.列出接口");
            list.add("小明:" + res);
        }

    }

    private static String callBotWithInput(String name, String input, String history, String topic, int round, int curRound, String character_setting, String main_points) {
        BotHttpClient client = BotHttpClient.builder()
                .url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("token").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("team", "小明 小红");
        jsonObject.addProperty("_history", history);
        jsonObject.addProperty("topic", topic);
        jsonObject.addProperty("desc", "");
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("character_setting", character_setting);
        jsonObject.addProperty("main_points", main_points);
        jsonObject.addProperty("input", input);
        jsonObject.addProperty("round", round);
        jsonObject.addProperty("cur_round", curRound);

        String res = client.callBot(M78BotReq.builder()
                .token("token")
                .botId("1")
                .userName("name")
                .input("")
                .build(), jsonObject, BotHttpClient.DEFAULT_FUNCTION);
        return res;
    }


    @Test
    public void testBotHttpClient2() {
        BotHttpClient client = BotHttpClient.builder().url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query").token("token").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("agent", "1.教师  2.日语翻译  3.程序员  4.客服 5.测试 ");
        jsonObject.addProperty("input", "有个文档要翻译成日语");
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("")
                .build(), jsonObject);
        System.out.println(res);
    }

    @Test
    public void testBotJsonFix() {
        BotHttpClient client = BotHttpClient.builder().url("http://127.0.0.1:8076/open-apis/v1/ai-plugin-new/feature/router/probot/query").token("").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("json", "[\"name\":\"123\"]\n");
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("")
                .build(), jsonObject);
        System.out.println(res);
    }

    @Test
    public void testBotJsonReq() {
        BotHttpClient client = BotHttpClient.builder()
                .url("http://127.0.0.1:8076/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("token").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("request", "下雨了\n" +
                "{\"weather\":\"\"}");
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("")
                .build(), jsonObject);
        System.out.println(res);
    }

    @Test
    public void testFlowChoose() {
        BotHttpClient client = BotHttpClient.builder().url("http://127.0.0.1:8076/open-apis/v1/ai-plugin-new/feature/router/probot/query").token("").build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("flow", "2.读取代办事项\n" +
                "3.编写业务代码\n" +
                "6.制定健身计划\n");
        jsonObject.addProperty("input", "帮我开发一个网站");
        String res = client.callBot(M78BotReq.builder()
                .botId("1")
                .userName("name")
                .input("")
                .build(), jsonObject);
        System.out.println(res);
    }
}
