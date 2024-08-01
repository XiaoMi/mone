package com.xiaomi.youpin.tesla.ip.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.bo.robot.ProjectAiMessageManager;
import com.xiaomi.youpin.tesla.ip.bo.robot.Role;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.LocalAiService;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
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
        if (req.getCmd().equals("modifyPrompt")) {
            String prompt = req.getData().get("prompt");
            MutableObject<String> r = new MutableObject<>();
            r.setValue(prompt);
            changePrompt(project, prompt, r, req);
            return r.getValue();
        }
        return "";
    }

    //根据给定的提示更改提示信息，并在需要的情况下生成代码
    private static void changePrompt(Project project, String prompt, MutableObject<String> r, Req req) {
        //如果有长问答,长问答的优先级要更高
        if (!prompt.startsWith("?")) {
            boolean process = ProjectFsmManager.processMsg(project.getName(), prompt);
            if (process) {
                r.setValue("?$$$" + prompt);
                return;
            }
        } else {
            r.setValue(prompt);
            return;
        }

        //机器人指令
        if (prompt.startsWith(">>")) {
            String cmd = prompt.substring(2);
            VisionHandler.callBot(project, cmd);
            r.setValue("?$$$" + cmd);
            return;
        }


        //直接生成业务代码
        if (prompt.startsWith("//")) {
            removeSelection(project);
            AiCodeService.generateBizCode(req.getData().get("scope"), project, prompt);
            String message = prompt.substring(2);
            ProjectAiMessageManager.getInstance().appendMsg(project, AiChatMessage.builder().id(UUID.randomUUID().toString()).role(Role.user).message(message).data(message).build());
            //返回这种协议开头的,前端就不在问chatgpt了,只做显示
            r.setValue("?$$$" + message);
            return;
        }


        ApplicationManager.getApplication().invokeAndWait(() -> {
            Editor editor = CodeService.getEditor(project);
            if (null != editor) {
                r.setValue(prompt);

                //直接本地问chatgpt
                if (prompt.startsWith("::")) {
                    removeSelection(project);
                    String newPrompt = prompt.substring(2);
                    LocalAiService.localCall(project, newPrompt);
                    //返回这种协议开头的,前端就不在问chatgpt了,只做显示
                    r.setValue("?$$$" + newPrompt);
                }


                if (ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_SELECT_TEXT, "true").equals("true")) {
                    //选中编辑器中的文本
                    if (CodeService.isTextSelected(editor)) {
                        SelectionModel selectionModel = editor.getSelectionModel();
                        String selectedText = selectionModel.getSelectedText();
                        StringBuilder sb = new StringBuilder(prompt);
                        sb.append("\n```\n" + selectedText + "\n```\n");
                        r.setValue(sb.toString());
                    }

                    //选中Console中的文本
                    String text = ConsoleViewManager.getSelectedText(project);
                    if (StringUtils.isNotEmpty(text)) {
                        StringBuilder sb = new StringBuilder(prompt);
                        sb.append("\n```\n" + text + "\n```\n");
                        r.setValue(sb.toString());
                    }
                }

            }
        });


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
