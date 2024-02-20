package run.mone.m78.ip.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import run.mone.m78.ip.bo.chatgpt.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/12/5 22:13
 */
@Slf4j
public class ProxyAiService {


    private static Gson gson = new Gson();

    public static JsonObject call(List<Message> messageList) {
        return call(messageList, 50000);
    }

    public static JsonObject call(String r, long time, boolean vip) {
        String pa = "jsonStr";
        String req = gson.toJson(pa);
        return call0(req, time, vip);
    }

    //调用ai proxy (调用的是json接口,返回的数据一定是json格式)
    public static JsonObject call0(String req, long time, boolean vip) {
        return null;
    }

    public static JsonObject call(List<Message> messageList, long time) {
        String pa = "jsonStr";
        String req = gson.toJson(pa);
        return call0(req, time, true);
    }

}
