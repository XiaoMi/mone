package run.mone.ultraman.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.manager.InlayHintManager;
import run.mone.ultraman.listener.AthenaApplicationActivationListener;

/**
 * @author zhangxiaowei6
 * @Date 2024/6/20 14:36
 */

@Slf4j
public class RegenerateCompletionAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 获取编辑器实例
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 此时如果有inlay内容则用户输入后dispose
        InlayHintManager.ins().dispose(text -> {
            if (StringUtils.isNotEmpty(text)) {
                CodeService.deleteCode(editor);
                CodeService.formatCode(editor.getProject());
            }
        });
        // 再次触发
        AthenaApplicationActivationListener.handlePluginInEditor(editor);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 设置动作在编辑器中可用
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }
}
