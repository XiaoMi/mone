package com.xiaomi.youpin.tesla.ip.common;

import com.google.common.base.Splitter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.Version;
import run.mone.ultraman.common.SafeRun;
import run.mone.ultraman.http.HttpClient;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @author caobaoyu
 * @date 2023/4/15 08:38
 */
@Slf4j
public class Prompt {

    private static Map<String, String> promptMap = new HashMap<>();

    private static CopyOnWriteArrayList<PromptInfo> promptMeta = new CopyOnWriteArrayList<>();

    private static String zAddr = "";

    private static AtomicBoolean loadFinish = new AtomicBoolean();

    public static String get(String key) {
        return promptMap.get(key);
    }

    public static List<PromptInfo> getPromptMeta() {
        return promptMeta;
    }

    public static boolean isLoadFinish() {
        return loadFinish.get();
    }


    private static Gson gson = new Gson();

    public static void flush() {
        init();
    }

    public static int size() {
        return promptMap.size();
    }


    public static void init() {
        zAddr = reportPluginInfo();
        if (StringUtils.isEmpty(zAddr)) {
            return;
        }
        promptMeta.clear();
        promptMeta.addAll(getPrompt());
        ConfigCenter.init(zAddr);
        loadFinish.set(true);
    }

    public static List<PromptInfo> getPrompt() {
        try {
            ZRequestPram param = new ZRequestPram();
            String zToken = ConfigUtils.getConfig().getzToken();
            String url = zAddr + "/list";
            param.setType(64);
            param.setToken(zToken);
            String res = HttpClient.callHttpServer(url, "get_prompt_list", gson.toJson(param));
            List<ZPromptRes> data = gson.fromJson(res, new TypeToken<List<ZPromptRes>>() {
            }.getType());
            return convertZResToInfo(data);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            NotificationCenter.notice("get prompt error:" + ex.getMessage(), NotificationType.ERROR, true);
            return Lists.newArrayList();
        }
    }


    public static PromptInfo getPrompt(int type, String name) {
        try {
            ZRequestPram param = new ZRequestPram();
            String zToken = ConfigUtils.getConfig().getzToken();
            String url = zAddr + "/list";
            param.setType(type);
            param.setName(name);
            param.setToken(zToken);
            String res = HttpClient.callHttpServer(url, "get_prompt:" + name, gson.toJson(param));
            List<ZPromptRes> data = gson.fromJson(res, new TypeToken<List<ZPromptRes>>() {
            }.getType());
            List<PromptInfo> list = convertZResToInfo(data);
            if (list.size() > 0) {
                return list.get(0);
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            NotificationCenter.notice("get prompt error:" + ex.getMessage(), NotificationType.ERROR, true);
        }
        return null;
    }


    /**
     * 将Z的结果转换为实体
     *
     * @param data
     * @return
     */
    private static List<PromptInfo> convertZResToInfo(List<ZPromptRes> data) {
        return data.stream().map(v -> PromptInfo.builder()
                .id(v.getId())
                .promptName(v.getName())
                .tags(v.getTags())
                .data(v.getData())
                .labels(getLabels(v))
                .collected(v.getCollected())
                .usedTimes(v.getUsedTimes())
                .meta(v.getMeta())
                .desc(v.getDescription())
                .addon(v.getAddon())
                .addon_metas(v.getAddon_metas())
                .collectedSort(v.getCollectedSort())
                .build()).toList();
    }

    //用user的覆盖即可
    private static Map<String, String> getLabels(ZPromptRes v) {
        if (MapUtils.isNotEmpty(v.getUserLabels())) {
            v.getLabels().putAll(v.getUserLabels());
        }
        return v.getLabels();
    }

    public static List<PromptInfo> promptList(String tagName) {
        return promptMeta.stream().filter(it -> {
            List<Tag> tags = it.getTags();
            if (null == tags) {
                return false;
            }
            return tags.stream().filter(tag -> tag.getName().equals(tagName)).findAny().isPresent();
        }).collect(Collectors.toList());
    }

    public static boolean containsTag(List<Tag> tags, String tagName) {
        return tags.stream().filter(tag -> tag.getName().equals(tagName)).findAny().isPresent();
    }

    /**
     * 查询是那种类型
     *
     * @param info
     * @return
     */
    public static PromptType getPromptType(PromptInfo info) {
        List<Tag> tags = info.getTags();
        if (null == tags) {
            return PromptType.modifyMethod;
        }

        for (PromptType type : PromptType.values()) {
            if (containsTag(info.getTags(), type.name())) {
                return type;
            }
        }
        return PromptType.modifyMethod;
    }


    public static String reportPluginInfo() {
        try {
            ZAddrRes zAddrRes = zAddrRes();
            initConfig(zAddrRes);
            return zAddrRes.getAddr();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return "";
        }
    }

    public static ZAddrRes zAddrRes() {
        String aiProxy = ResourceUtils.getAthenaConfig().get(Const.CONF_AI_PROXY_URL);
        AthenaReq req = AthenaReq.builder()
                .userName(ConfigUtils.getConfig().getNickName())
                .zzToken(ConfigUtils.getConfig().getzToken())
                .version(new Version().toString())
                .os(System.getProperty("os.name").toLowerCase())
                .ideaVersion(ApplicationInfo.getInstance().getBuild().asString())
                .time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .build();
        String res = HttpClient.callHttpServer(aiProxy + "/report", "report", gson.toJson(req));
        ZAddrRes zAddrRes = gson.fromJson(res, ZAddrRes.class);
        AthenaContext.ins().setZAddr(zAddrRes.getAddr());
        AthenaContext.ins().setModels(zAddrRes.getModelsV2());
        AthenaContext.ins().setUserName(zAddrRes.getUserName());
        AthenaContext.ins().convertModelsToModelMap();
        return zAddrRes;
    }

    private static void initConfig(ZAddrRes zAddrRes) {
        SafeRun.run(() -> {
            if (StringUtils.isNotEmpty(zAddrRes.getAthenaConfig())) {
                String config = zAddrRes.getAthenaConfig();
                Splitter.on(",").split(config).forEach(it -> {
                    List<String> list = Splitter.on(":").splitToList(it);
                    if (null != list && list.size() == 2) {
                        ResourceUtils.putConfigIfAbsent(list.get(0), list.get(1));
                    }
                });
            }
        });
    }


    public static PromptInfo getPromptInfo(String promptName) {
        return promptMeta.stream().filter(it -> it.getPromptName().equals(promptName)).findAny().get();
    }

    public static List<PromptInfo> getPromptInfoByTag(String tag) {
        return promptMeta.stream().filter(promptInfo -> containsTag(promptInfo.getTags(), tag)).collect(Collectors.toList());
    }

    public static List<PromptInfo> getCollected() {
        return promptMeta.stream().filter(PromptInfo::isCollected).sorted(Comparator.comparing(PromptInfo::getCollectedSort)).collect(Collectors.toList());
    }

    /**
     * 获取使用次数最多的size个prompt
     *
     * @param size
     * @return
     */
    public static List<PromptInfo> getMostUsed(int size) {
        return promptMeta.stream().sorted(Comparator.comparing(PromptInfo::getUsedTimes).reversed()).limit(size).collect(Collectors.toList());
    }

    /**
     * 获取某个标签下使用次数最多的size个prompt
     *
     * @param size
     * @param tag
     * @return
     */
    public static List<PromptInfo> getMostUsed(int size, String tag) {
        return promptMeta.stream().filter(promptInfo -> containsTag(promptInfo.getTags(), tag))
                .sorted(Comparator.comparing(PromptInfo::getUsedTimes).reversed()).limit(size).collect(Collectors.toList());
    }

    public static String getzAddr() {
        return zAddr;
    }

}
