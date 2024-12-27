package com.xiaomi.youpin.tesla.ip.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.Tag;
import com.xiaomi.youpin.tesla.ip.common.ConfigCenter;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author caobaoyu
 * @author goodjava@qq.com
 * @description:
 * @date 2023-06-14 14:15
 */
public class MenuActionGroup extends ActionGroup {

    public MenuActionGroup() {
        super("Prompt", true);
    }

    @Override
    public AnAction @NotNull [] getChildren(AnActionEvent e) {
        if (ResourceUtils.checkDisableCodeCompletionStatus() || LabelUtils.getLabelValue(e.getProject(), Const.DISABLE_ACTION_GROUP, "false").equals("true")) {
            return new AnAction[]{};
        }
//        Map<String, Tag> menuTag = ConfigCenter.getMenuTag();
//        Map<String, List<PromptInfo>> tagPromptInfo = new LinkedHashMap<>();
//        tagPromptInfo.put("Collection", Prompt.getCollected());
//        menuTag.keySet().forEach(tag -> tagPromptInfo.put(tag, Prompt.getPromptInfoByTag(tag)));
//        List<MenuAction> actions = tagPromptInfo.entrySet().stream().map((entry) -> new MenuAction(entry.getKey(), entry.getValue())).toList();
//        return actions.toArray(new AnAction[0]);
        //第一个版本之显示系统级别的了
        List<PromptInfo> list = Prompt.getPromptInfoByTag("system");
        list.addAll(Prompt.getCollected());
        return list.stream().map(it -> new DynamicAction(it.getDesc(), Prompt.getPromptType(it), it)).toArray(AnAction[]::new);
    }


}
