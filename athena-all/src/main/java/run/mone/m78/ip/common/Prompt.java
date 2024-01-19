package run.mone.m78.ip.common;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.bo.Tag;
import run.mone.m78.ip.bo.ZAddrRes;
import run.mone.m78.ip.bo.ZPromptRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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

    }

    public static List<PromptInfo> getPrompt() {
        return null;
    }


    public static PromptInfo getPrompt(int type, String name) {
        return null;
    }


    /**
     * 将Z的结果转换为实体
     *
     * @param data
     * @return
     */
    private static List<PromptInfo> convertZResToInfo(List<ZPromptRes> data) {
        return null;
    }

    public static List<PromptInfo> promptList(String tagName) {
        return null;
    }

    public static boolean containsTag(List<Tag> tags, String tagName) {
        return false;
    }

    /**
     * 查询是那种类型
     *
     * @param info
     * @return
     */
    public static PromptType getPromptType(PromptInfo info) {
        return null;
    }


    public static ZAddrRes zAddrRes() {
        return null;
    }

    private static void initConfig(ZAddrRes zAddrRes) {

    }


    public static PromptInfo getPromptInfo(String promptName) {
        return null;
    }

    public static List<PromptInfo> getPromptInfoByTag(String tag) {
        return null;
    }

    public static List<PromptInfo> getCollected() {
        return null;
    }

    /**
     * 获取使用次数最多的size个prompt
     *
     * @param size
     * @return
     */
    public static List<PromptInfo> getMostUsed(int size) {
        return null;
    }

    /**
     * 获取某个标签下使用次数最多的size个prompt
     *
     * @param size
     * @param tag
     * @return
     */
    public static List<PromptInfo> getMostUsed(int size, String tag) {
        return null;
    }


}
