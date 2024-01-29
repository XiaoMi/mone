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
 * @author goodjava@qq.com
 * @date 2023/5/27 09:23
 */
public class DynamicActionGroup extends ActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        List<AnAction> actions = new ArrayList<>();
        List<PromptInfo> list = Prompt.promptList("plugin");
        list.stream().forEach(it -> actions.add(new DynamicAction(it.getDesc(), Prompt.getPromptType(it), it)));
        return actions.toArray(new AnAction[0]);
    }
}
