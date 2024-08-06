package com.xiaomi.youpin.tesla.ip.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.ip.bo.AiCodeRes;
import com.xiaomi.youpin.tesla.ip.bo.Tag;
import com.xiaomi.youpin.tesla.ip.bo.ZPromptRes;
import com.xiaomi.youpin.tesla.ip.bo.ZRequestPram;
import lombok.Data;
import lombok.Getter;
import run.mone.ultraman.http.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author caobaoyu
 * @description: 从Z获取一些基础配置 对应 -> 68 Athena の cfg center
 * @date 2023-05-30 11:02
 */
@Data
public class ConfigCenter {


    private static Map<String, AiCodeRes> copyrightingMap = new HashMap<>();

    @Getter
    private static List<String> guide = new ArrayList<>();

    private static List<Tag> menuTag = new ArrayList<>();

    public static Map<String, Tag> getMenuTag() {
        return menuTag.stream().collect(Collectors.toMap(Tag::getName, Function.identity()));
    }

    public static void init(String zAddr) {
        guide.clear();
        menuTag.clear();
        buildCopyrighting(zAddr);
    }

    private static void buildCopyrighting(String zAddr) {
        Safe.run(() -> {
            ZRequestPram param = ZRequestPram.builder().token(ConfigUtils.getConfig().getzToken()).type(68).build();
            String url = zAddr + "/list";
            Gson gson = new Gson();
            String httpRes = HttpClient.callZServer(url, "copyrighting", gson.toJson(param));
            List<ZPromptRes> data = gson.fromJson(httpRes, new TypeToken<List<ZPromptRes>>() {
            }.getType());
            Map<String, ZPromptRes> collect = data.stream().collect(Collectors.toMap(ZPromptRes::getName, Function.identity()));
            List<Map<String, AiCodeRes>> res = gson.fromJson(collect.get("copyrighting").getMeta(), new TypeToken<List<Map<String, AiCodeRes>>>() {
            }.getType());
            res.forEach(l -> copyrightingMap.putAll(l));
            guide.addAll(gson.fromJson(collect.get("guide").getData(), new TypeToken<List<String>>() {
            }.getType()));
            menuTag.addAll(gson.fromJson(collect.get("menu").getData(), new TypeToken<List<Tag>>() {
            }.getType()));
        });

    }

    public static AiCodeRes build(String type) {
        return copyrightingMap.get(type);
    }


}
