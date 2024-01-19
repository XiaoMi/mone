package run.mone.m78.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.common.PromptType;
import run.mone.m78.ip.service.PromptService;
import run.mone.m78.ip.bo.PromptInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 09:24
 */
public class DynamicAction extends AnAction implements DumbAware {


    private final String myText;

    private PromptType type;

    private PromptInfo promptInfo;


    public DynamicAction(String text, PromptType type, PromptInfo promptInfo) {
        super(text);
        myText = text;
        this.type = type;
        this.promptInfo = promptInfo;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        @Nullable Project project = event.getProject();
        @Nullable Module module = LangDataKeys.MODULE.getData(event.getDataContext());
        PromptService.dynamicInvoke(GenerateCodeReq.builder().project(project).module(module).promptInfo(this.promptInfo).promptName(this.promptInfo.getPromptName()).promptType(type).build());
    }


}
