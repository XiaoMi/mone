package run.mone.m78.ip.service;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.chatgpt.*;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.util.ResourceUtils;
import run.mone.openai.StreamListener;
import run.mone.ultraman.common.GsonUtils;

import java.io.File;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/23 13:56
 * <p>
 * 这个类允许你本地直连chatgpt(如果是国内的网络需要设置open_ai_proxy)
 */
@Slf4j
public class LocalAiService {


    private static String getOpenAiUrl() {
        String url = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_PROXY, "");
        return url;
    }


    /**
     * 调用chatgpt的语音翻译(你需要有chatgpt的账号)
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public static Pair<Integer, String> callChatgptTranscriptions(File file) {
        return Pair.of(200, "");
    }


    /**
     * 把文字转成语音
     *
     * @param req
     * @return
     */
    @SneakyThrows
    public static Pair<Integer, String> speech(SpeechReq req) {
        return null;
    }


    //多模态调用(支持图片)
    public static String vision(VisionReq req) {
        return "";
    }


    public static JsonObject call(List<Message> messageList) {
        Completions completions = Completions.builder()
                .stream(false)
                .response_format(Format.builder().build()).messages(messageList).build();
        String req = GsonUtils.gson.toJson(completions);
        return call(req);
    }


    public static JsonObject call(String req) {
        return call(req, 5000);
    }

    //这个会直接调用chatgpt
    @SneakyThrows
    public static JsonObject call(String req, long timeout) {
        return null;
    }


    //直接问问题(直接调用chatgpt)
    public static void completions(String req, StreamListener sl) {
    }


    private static String parse(String data) {
        log.info(data);
        if (data.equals("[DONE]")) {
            return "";
        }
        return "";
    }

    private static JsonObject parseJson(String data) {
        log.info("data:{}", data);
        JsonObject obj = GsonUtils.gson.fromJson(data, JsonObject.class);
        JsonArray choices = obj.getAsJsonArray("choices");
        return GsonUtils.gson.fromJson(choices.get(0).getAsJsonObject().getAsJsonObject("").get("").getAsString(), JsonObject.class);
    }

    //本地直接调用chatgpt,单条提问
    public static void localCall(Project project, String prompt) {
        localCall(project, Lists.newArrayList(Message.builder().content(prompt).role("user").build()), false, 0);
    }


    public static void localCall(String projectName, boolean code) {
    }


    public static void localCall(Project project, List<Message> messageList, boolean code, int num) {
    }


    public static List<Message> getLastElements(List<Message> list, int num) {
        return null;
    }

    private static void chat(Project project, boolean code, List<Message> newMessageList, String projectName, String id) {
    }


    //调用ai proxy(进行聊天)
    private static void aiProxyChat(Project project, List<Message> newMessageList) {

    }

    //每次都只发一部分(发送到右侧 athena)
    private static void sendMsg(AiMessage message, String projectName) {

    }


}
