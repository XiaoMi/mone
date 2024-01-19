package run.mone.m78.ip.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.util.LabelUtils;
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
        if (LabelUtils.getLabelValue(e.getProject(), Const.DISABLE_ACTION_GROUP, "false").equals("true")) {
            return new AnAction[]{};
        }
        List<PromptInfo> list = Prompt.getPromptInfoByTag("");
        list.addAll(Prompt.getCollected());
        return list.stream().map(it -> new DynamicAction(it.getDesc(), Prompt.getPromptType(it), it)).toArray(AnAction[]::new);
    }


}
