package run.mone.m78.service.prompt;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.service.bo.chatgpt.*;
import run.mone.m78.service.common.AthenaContext;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.HttpClient;
import run.mone.m78.service.common.SafeRun;

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


    private static Gson gson = new Gson();

    public static void flush() {
        init();
    }

    public static int size() {
        return promptMap.size();
    }


    public static void init() {
        log.info("init prompt");
        zAddr = reportPluginInfo();
        if (StringUtils.isEmpty(zAddr)) {
            return;
        }
        List<PromptInfo> list = getPrompt();
        if (list != null && list.size() > 0) {
            CopyOnWriteArrayList newPromptMeta = new CopyOnWriteArrayList(list);
            promptMeta = newPromptMeta;
        }

        loadFinish.set(true);
    }

    public static List<PromptInfo> getPrompt() {
        try {
            ZRequestPram param = new ZRequestPram();
            String zToken = Config.zToken;
            String url = zAddr + "/list";
            param.setType(64);
            param.setToken(zToken);
            String res = HttpClient.callHttpServer(url, "get_prompt_list", gson.toJson(param));
            List<ZPromptRes> data = gson.fromJson(res, new TypeToken<List<ZPromptRes>>() {
            }.getType());
            return convertZResToInfo(data);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return Lists.newArrayList();
        }
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
                .labels(MapUtils.isEmpty(v.getUserLabels()) ? v.getLabels() : v.getUserLabels())
                .collected(v.getCollected())
                .usedTimes(v.getUsedTimes())
                .meta(v.getMeta())
                .desc(v.getDescription())
                .addon(v.getAddon())
                .addon_metas(v.getAddon_metas())
                .collectedSort(v.getCollectedSort())
                .build()).collect(Collectors.toList());
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
        String aiProxy = Config.aiProxy;
        AthenaReq req = AthenaReq.builder()
                .userName(Config.nickName)
                .zzToken(Config.zToken)
                .version(new Version().toString())
                .os(System.getProperty("os.name").toLowerCase())
                .ideaVersion("m78")
                .time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .build();
        String res = HttpClient.callHttpServer(aiProxy + "/incluster_report", "incluster_report", gson.toJson(req));
        ZAddrRes zAddrRes = gson.fromJson(res, ZAddrRes.class);
        AthenaContext.ins().setZAddr(zAddrRes.getAddr());
        return zAddrRes;
    }

    private static void initConfig(ZAddrRes zAddrRes) {
        SafeRun.run(() -> {
            if (StringUtils.isNotEmpty(zAddrRes.getAthenaConfig())) {
                String config = zAddrRes.getAthenaConfig();
                Splitter.on(",").split(config).forEach(it -> {
                    List<String> list = Splitter.on(":").splitToList(it);
                    if (null != list && list.size() == 2) {
                    }
                });
            }
        });
    }

    public static PromptInfo getPromptInfo(String promptName) {
        return promptMeta.stream().filter(it -> it.getPromptName().equals(promptName)).findAny().get();
    }
}
