package run.mone.ultraman.listener;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.wm.IdeFrame;
import run.mone.m78.ip.service.CodeService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.manager.InlayHintManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author goodjava@qq.com
 * @date 2023/7/27 22:34
 */
public class AthenaApplicationActivationListener implements ApplicationActivationListener {


    private AtomicBoolean init = new AtomicBoolean(false);


    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        if (init.compareAndSet(false, true)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                //用来捕获 editor中输入enter
                EditorActionManager actionManager = EditorActionManager.getInstance();
                EditorActionHandler originalEnterHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, new EditorActionHandler() {
                    @Override
                    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        String text = InlayHintManager.ins().getHintText();
                        if (StringUtils.isNotEmpty(text)) {
                            CodeService.insertCode(editor.getProject(), text, false);
                            CodeService.deleteCode(editor);
                            CodeService.formatCode(editor.getProject());
                            InlayHintManager.ins().dispose();
                        } else {
                            originalEnterHandler.execute(editor, caret, dataContext);
                        }
                    }
                });

                //用来捕获 editor中输入esc
                EditorActionHandler originalEscapeHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE);
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, new EditorActionHandler() {
                    @Override
                    public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        if (null != InlayHintManager.ins().getInlay()) {
                            CodeService.deleteCode(editor);
                            CodeService.formatCode(editor.getProject());
                            InlayHintManager.ins().dispose();
                        }
                        originalEscapeHandler.execute(editor, dataContext);
                    }
                });
            });
        }
    }
}
