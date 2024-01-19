package run.mone.m78.ip.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.bo.PromptInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-14 14:50
 */
public class MenuAction extends ActionGroup {

    private List<PromptInfo> promptInfoList;

    public MenuAction(String text, List<PromptInfo> promptInfos) {
        super(text, true);
        this.promptInfoList = promptInfos;
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{};
    }
}
