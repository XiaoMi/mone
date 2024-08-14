package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.xiaomi.youpin.tesla.ip.common.Const;
import lombok.SneakyThrows;
import org.HdrHistogram.AtomicHistogram;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/6/15 17:35
 */
public class ResourceUtils {

    private static Gson gson = new Gson();

    public static final String USER_HOME_ATHENA_FILE_NAME = ".athena.json";

    private static final String ULTRAMAN_URL_PREFIX = "http://localhost/ultraman";

    public static String readResources(Project project, Module module) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            PsiDirectory directory = PsiClassUtils.getSourceDirectory(project, module.getName(), false, "resources");
            VirtualFile resourcesFolder = directory.getVirtualFile();
            if (resourcesFolder != null) {
                for (VirtualFile file : resourcesFolder.getChildren()) {
                    if (!file.isDirectory() && file.getName().startsWith("athena_")) {
                        String text = FileDocumentManager.getInstance().getDocument(file).getText();
                        stringBuilder.append(text);
                        stringBuilder.append("\n");
                    }
                }
            }
            return stringBuilder.toString();
        } catch (Throwable ignore) {

        }
        return "";
    }


    private static ConcurrentHashMap<String, String> config = new ConcurrentHashMap<>();


    /**
     * 获取用户的雅典娜配置
     *
     * @param project
     * @return
     */
    @SneakyThrows
    public static Map<String, String> getAthenaConfig(Project project, boolean refresh) {
        if (!config.isEmpty() && !refresh) {
            return config;
        }
        config.clear();
        String str = "";
        if (null != project) {
            str = project.getBasePath() + File.separator + "athena.json";
            getConfig(str);
        }
        str = System.getProperty("user.home") + File.separator + USER_HOME_ATHENA_FILE_NAME;
        config.putAll(defaultConfig);
        getConfig(str);

        //todo 未来版本删除
        String dashUrl = config.get(Const.CONF_DASH_URL);
        if (dashUrl.startsWith(ULTRAMAN_URL_PREFIX)) {
            config.put(Const.CONF_DASH_URL, defaultConfig.get(Const.CONF_DASH_URL));
            String content = new GsonBuilder().setPrettyPrinting().create().toJson(config);
            FileUtils.writeConfig(content, ResourceUtils.USER_HOME_ATHENA_FILE_NAME);
        }

        // 把OPEN_GUIDE强制置为true  todo 未来版本删除?
        String openGuide = config.get(Const.OPEN_GUIDE);
        if ("false".equals(openGuide)) {
            config.put(Const.OPEN_GUIDE, "true");
            String content = new GsonBuilder().setPrettyPrinting().create().toJson(config);
            FileUtils.writeConfig(content, ResourceUtils.USER_HOME_ATHENA_FILE_NAME);
        }

        return config;
    }


    private static Map<String, String> defaultConfig = new LinkedHashMap<>() {
        {
            put(Const.VISION, "false");
            put(Const.DEBUG, "false");
            put(Const.OPEN_GUIDE, "true");
            put(Const.DISABLE_ACTION_GROUP, "false");
            put(Const.BIZ_WRITE, "false");
            put(Const.OPEN_AI_KEY, "");
            put(Const.OPEN_AI_PROXY, "");
            put(Const.OPEN_AI_LOCAL, "true");
            put(Const.OPEN_AI_TEST, "false");
            put(Const.OPEN_SELECT_TEXT, "true");
            put(Const.OPEN_AI_MODEL, "gpt-4-1106-preview");//gpt-3.5-turbo
            put(Const.DISABLE_SEARCH, "false");
            put(Const.AI_PROXY_CHAT, "true");
            put(Const.ENABLE_ATHENA_STATUS_BAR, "true");
            //调用bot
            put(Const.USE_BOT, "true");
            put(Const.BOT_URL, "https://localhost/open-apis/ai-plugin-new/feature/router/probot/query");
            // inline 补全
            put(Const.INLAY, "true");
            put(Const.INLAY_DELAY, "200");
            put(Const.INLAY_BOT_ID, "160004");
            put(Const.INLAY_SCOPE, "method");
            //关闭整个代码补全功能(只留下chat)
            put(Const.DISABLE_CODE_COMPLETION, Const.FALSE);
            // 内置配置
            put(Const.CONF_NICK_NAME, "nick_mone");
            put(Const.CONF_DASH_URL, "http://127.0.0.1/ultraman/#/code");
            put(Const.CONF_AI_PROXY_URL, "http://127.0.0.1");
            //允许flow回调回来的端口号
            put(Const.CONF_PORT, "6666");
            put(Const.CONF_M78_URL, "https://localhost/open-apis/ai-plugin-new");
            put(Const.CONF_M78_SPEECH2TEXT, "/speechToText2");
            put(Const.CONF_M78_TEXT2SPEECH, "/textToSpeech");
            put(Const.CONF_M78_UPLOAD_CODE_INFO, "/uploadCodeInfo");
            // 右侧聊天是否调用BOT配置
            put(Const.CONF_CHAT_USE_BOT, "true");
            put(Const.CONF_M78_CODE_GEN_WITH_ENTER, "false");

//            put(Const.CONF_COMPLETION_TYPE, "0");
        }
    };


    private static void getConfig(String str) throws IOException {
        if (new File(str).exists()) {
            String s = Files.readString(Paths.get(str));
            Type typeOfT = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> m = gson.fromJson(s, typeOfT);
            if (null != m && m.size() > 0) {
                config.putAll(m);
            }
        }
    }


    public static Map<String, String> getAthenaConfig(Project project) {
        return getAthenaConfig(project, false);
    }


    public static Map<String, String> getAthenaConfig() {
        return getAthenaConfig(null, false);
    }

    public static boolean isOpen(String key) {
        return Boolean.valueOf(ResourceUtils.getAthenaConfig().getOrDefault(key, "false"));
    }

    public static boolean has(String key) {
        return ResourceUtils.getAthenaConfig().containsKey(key);
    }

    public static String get(String key, String defaultValue) {
        return ResourceUtils.getAthenaConfig().getOrDefault(key, defaultValue);
    }


    public static void putConfigIfAbsent(String key, String value) {
        config.putIfAbsent(key, value);
    }

    public static boolean checkDisableCodeCompletionStatus() {
        return isOpen(Const.DISABLE_CODE_COMPLETION);
    }


}
