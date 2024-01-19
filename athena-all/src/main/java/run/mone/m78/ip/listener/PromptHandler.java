package run.mone.m78.ip.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.bo.robot.AiChatMessage;
import run.mone.m78.ip.bo.robot.ProjectAiMessageManager;
import run.mone.m78.ip.bo.robot.Role;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.service.LocalAiService;
import run.mone.m78.ip.util.ResourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import run.mone.ultraman.manager.ConsoleViewManager;
import run.mone.ultraman.service.AiCodeService;
import run.mone.ultraman.state.ProjectFsmManager;

import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 16:01
 */
public class PromptHandler {


    //修改用户的问题
    public static String handler(Project project, Req req) {
        return "";
    }

    //根据给定的提示更改提示信息，并在需要的情况下生成代码
    private static void changePrompt(Project project, String prompt, MutableObject<String> r, Req req) {

    }


    //移除选择
    private static void removeSelection(Project project) {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Editor editor = CodeService.getEditor(project);
            if (null != editor) {
                editor.getSelectionModel().removeSelection();
            }
        });
    }

}
