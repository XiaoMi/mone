package run.mone.ultraman.listener;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.wm.IdeFrame;
import com.xiaomi.youpin.tesla.ip.bo.Action;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaInspection;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.SafeRun;
import run.mone.ultraman.listener.bo.CompletionEnum;
import run.mone.ultraman.manager.InlayHintManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.xiaomi.youpin.tesla.ip.common.Const.CONF_M78_CODE_GEN_WITH_ENTER;

/**
 * @author goodjava@qq.com
 * @date 2023/7/27 22:34
 */
@Slf4j
public class AthenaApplicationActivationListener implements ApplicationActivationListener {


    //保证只加载一次
    private AtomicBoolean init = new AtomicBoolean(false);

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        if (init.compareAndSet(false, true)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {

                TypedAction typedAction = TypedAction.getInstance();
                TypedActionHandler originalHandler = typedAction.getHandler();

                //处理输入的,为以后的接口做准备
                typedAction.setupHandler((editor, charTyped, dataContext) -> {
                    // 在这里处理你的输入字符
                    if (Character.isLetterOrDigit(charTyped)) {
                        // 如果输入的是字母或数字，执行一些操作
                        log.debug("Typed letter or digit: " + charTyped);
                        setActionType(editor, "letterOrDigit");

                        @Nullable AtomicInteger id = editor.getUserData(CaretHoverEditorFactoryListener.EDIT_INCR_ID_KEY);
                        if (null != id) {
                            id.incrementAndGet();
                        }

                        // 此时如果有inlay内容则用户输入后dispose
                        InlayHintManager.ins().dispose();
                       /* InlayHintManager.ins().dispose(text -> {
                            if (StringUtils.isNotEmpty(text)) {
                                CodeService.deleteCode(editor);
                                CodeService.formatCode(editor.getProject());
                            }
                        });*/
                        // 再次触发
                        handlePluginInEditor(editor);
                    }
                    // 确保调用原始处理程序，以便其他功能（如文本输入）不会被破坏
                    originalHandler.execute(editor, charTyped, dataContext);
                });

                EditorActionManager actionManager = EditorActionManager.getInstance();

                //用来捕获 editor中输入回车
                EditorActionHandler enterHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, new EditorActionHandler() {
                    @Override
                    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        setActionType(editor, "enter");

                        enterHandler.execute(editor, caret, dataContext);

                        if (!ResourceUtils.isOpen(CONF_M78_CODE_GEN_WITH_ENTER)) {
                            return;
                        }
                        String comment = CodeService.getPreviousLineIfComment(editor);
                        log.info("comment:{}", comment);
//                        UltramanConsole.append(editor.getProject(), "" + comment, false);
                        //上一行是注释,并且结尾是(class)
                        if (check(comment)) {
                            //直接调用生成代码
                            PromptInfo promptInfo = Prompt.getPromptInfo(Const.GENERATE_CODE);
                            if (AthenaInspection.isBotUsageConfigured(promptInfo)) {
                                //调用bot生成代码
                                HashMap<String, String> params = Maps.newHashMap();
                                params.put("__code", comment.trim());
                                params.put("__skip_enter", "true");
                                AthenaInspection.invokePrompt(editor.getProject(), promptInfo.getPromptName(), params);
                            }
                        }
                    }

                    private static boolean check(String comment) {
                        return StringUtils.isNotEmpty(comment) && comment.trim().endsWith(")");
                    }
                });


                //用来捕获 editor中输入tab
                EditorActionHandler originalTabHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_TAB);
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_TAB, new EditorActionHandler() {
                    @Override
                    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        originalTabHandler.execute(editor, caret, dataContext);
                        InlayHintManager.ins().dispose((text) -> {
                            if (StringUtils.isNotEmpty(text)) {
                                CodeService.insertCode(editor.getProject(), text, false, InlayHintManager.ins().getCaretOffsetWhenTriggered(), editor);
                                CompletionEnum completionType = ConfigUtils.getConfig().getCompletionMode();
                                @Nullable CaretHoverPlugin plugin = editor.getUserData(CaretHoverEditorFactoryListener.PLUGIN_KEY);
                                if (plugin == null || CompletionEnum.MULTI_LINE == completionType) {
                                    //移动到方法末尾
                                    CodeService.moveCaretToMethodEnd(editor, CodeService.getMethod(editor.getProject()));
                                } else {
                                    CodeService.moveCaretToEndOfLine(editor);
                                }
                                // 代码统计
                                CodeUtils.uploadCodeGenInfo(Action.INLAY.getCode(), text, "", editor.getProject());
                            }
                        });
                    }
                });

                //用来捕获 editor中输入esc
                EditorActionHandler originalEscapeHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ESCAPE);
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ESCAPE, new EditorActionHandler() {
                    @Override
                    public void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        InlayHintManager.ins().dispose();
                       /* InlayHintManager.ins().dispose(text -> {
                            if (StringUtils.isNotEmpty(text)) {
                                CodeService.deleteCode(editor);
                                CodeService.formatCode(editor.getProject());
                            } else {
                                originalEscapeHandler.execute(editor, dataContext);
                            }
                        });*/
                        // 再次触发
                        AthenaApplicationActivationListener.handlePluginInEditor(editor);
                    }
                });

                EditorActionHandler originalBackspaceHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE);
                EditorActionHandler myBackspaceHandler = new EditorActionHandler() {
                    @Override
                    protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                        // 在这里处理删除事件
                        log.debug("Backspace key pressed");
                        // 调用原始处理程序以保持默认行为(保证先影响了)
                        originalBackspaceHandler.execute(editor, caret, dataContext);
                        handlePluginInEditor(editor);

                    }
                };
                actionManager.setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, myBackspaceHandler);


            });
        }
    }

    private static void setActionType(Editor editor, String type) {
        @Nullable AtomicReference<String> actionType = editor.getUserData(CaretHoverEditorFactoryListener.EDIT_ACTION_TYPE);
        if (null != actionType) {
            actionType.set(type);
        }
    }

    public static void handlePluginInEditor(@NotNull Editor editor) {
        SafeRun.run(() -> {
            CaretHoverPlugin plugin = editor.getUserData(CaretHoverEditorFactoryListener.PLUGIN_KEY);
            if (null != plugin) {
                //处理退出的逻辑
                plugin.handleCaretPositionChangedEvent();
            }
        });
    }
}
