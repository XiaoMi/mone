package run.mone.m78.ip.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.ParamDialogReq;
import run.mone.m78.ip.dialog.ParamTableDialog;
import run.mone.m78.ip.bo.PromptInfo;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/6/17 09:22
 */
public class LabelUtils {


    public static String getLabelValue(Project project, PromptInfo promptInfo, String key, String defaultValue) {
        Map<String, String> map = ResourceUtils.getAthenaConfig(project);
        String rkey = Joiner.on("@").join(promptInfo.getPromptName(), key);
        if (map.containsKey(rkey)) {
            return map.get(rkey);
        }
        return promptInfo.getLabels().getOrDefault(key, defaultValue);
    }

    public static boolean isOpen(Project project, PromptInfo promptInfo, String key) {
        return getLabelValue(project, promptInfo, key, "false").equals("true");
    }


    public static String getLabelValue(Project project, String key, String defaultValue) {
        //用户配置的优先级最高
        Map<String, String> map = ResourceUtils.getAthenaConfig(project);
        return map.getOrDefault(key, defaultValue);
    }

    public static boolean open(Project project, String key) {
        return open(project, key, "false");
    }

    public static boolean open(Project project, String key, String defaultValue) {
        //用户配置的优先级最高
        Map<String, String> map = ResourceUtils.getAthenaConfig(project);
        String value = map.getOrDefault(key, defaultValue);
        return value.equals("true");
    }

    public static boolean open(String key) {
        return open(null, key);
    }

    public static void showLabelConfigUi(Project project) {
        Map<String, String> map = ResourceUtils.getAthenaConfig(project);
        ParamTableDialog paramTable = new ParamTableDialog(ParamDialogReq.builder().title("config").build(), project, map, Maps.newHashMap(), null);
        paramTable.show();

    }


}
