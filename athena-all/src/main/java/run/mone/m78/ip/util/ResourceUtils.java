package run.mone.m78.ip.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import run.mone.m78.ip.common.Const;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/6/15 17:35
 */
public class ResourceUtils {

    private static Gson gson = new Gson();

    public static final String USER_HOME_ATHENA_FILE_NAME = ".athena.json";

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
        return config;
    }


    private static Map<String, String> defaultConfig = new HashMap<>() {
        {
            put(Const.VISION, "false");
            put(Const.DEBUG, "false");
            put(Const.OPEN_GUIDE, "false");
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
            put(Const.ENABLE_ATHENA_STATUS_BAR, "false");
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


    public static void putConfigIfAbsent(String key, String value) {
        config.putIfAbsent(key, value);
    }

}
