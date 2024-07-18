package run.mone.ultraman.statusbar;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Consumer;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.Tag;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.EditorUtils;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.AthenaInspection;
import run.mone.ultraman.bo.ClientData;
import run.mone.ultraman.statusbar.bo.PopupItem;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.tesla.ip.common.Const.GENERATE_CODE;
import static com.xiaomi.youpin.tesla.ip.common.Const.UNIT_TEST;
import static run.mone.ultraman.statusbar.PopUpReq.POP_ORIGIN_LINE_MARK;
import static run.mone.ultraman.statusbar.PopUpReq.POP_ORIGIN_STATUS_BAR;

/**
 * @author goodjava@qq.com
 * @date 2023/7/18 23:41
 */
@Slf4j
public class AthenaStatusBarWidget implements StatusBarWidget {

    private final Project project;

    private StatusBar statusBar;

    public AthenaStatusBarWidget(Project project) {
        this.project = project;
    }


    @Override
    public @NonNls @NotNull String ID() {
        return "AthenaStatusBarWidget";
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public void dispose() {
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {

        return new StatusBarWidget.TextPresentation() {

            @Override
            public @Nullable @NlsContexts.Tooltip String getTooltipText() {
                return Const.PLUGIN_NAME;
            }

            @Override
            public @Nullable Consumer<MouseEvent> getClickConsumer() {
                return (MouseEvent e) -> {
                    if (!LabelUtils.open(Const.ENABLE_ATHENA_STATUS_BAR)) {
                        return;
                    }
                    popUp(project, statusBar, e, PopUpReq.builder().build());
                };
            }

            @Override
            public @NotNull @NlsContexts.Label String getText() {
                return Const.PLUGIN_NAME;
            }

            @Override
            public float getAlignment() {
                return 0;
            }
        };
    }


    //弹出插件的界面
    public static void popUp(Project project, StatusBar statusBar, MouseEvent mouseEvent, PopUpReq req) {
        if (!Objects.isNull(req.getPsiMethod())) {
            CodeService.moveCaretToMethod(CodeService.getEditor(project), req.getPsiMethod());
        }
        if (!Objects.isNull(req.getPsiComment())) {
            CodeService.moveCaretToOffset(CodeService.getEditor(project), req.getOffset());
        }

        List<PromptInfo> collectedList = new ArrayList<>();
        if (POP_ORIGIN_STATUS_BAR == req.getOrigin()) {
            collectedList.add(Prompt.getPromptInfo(GENERATE_CODE));
            collectedList.addAll(Prompt.getPromptInfoByTag("method_inspection"));
            collectedList.addAll(Prompt.getPromptInfoByTag("status_bar"));
        } else if (POP_ORIGIN_LINE_MARK == req.getOrigin()) {
            if (req.getPsiComment() != null) {
                collectedList.add(Prompt.getPromptInfo(GENERATE_CODE));
            } else {
                collectedList.addAll(Prompt.getPromptInfoByTag("method_inspection"));
            }
        }
        if (collectedList.isEmpty()) {
            collectedList.add(PromptInfo.builder()
                            .tags(ImmutableList.of(Tag.builder().name("popup").build()))
                            .promptName("无操作")
                            .desc("无操作")
                    .build());
        }
        List<PopupItem> list = collectedList.stream()
                .filter(it -> it.getTags()
                        .stream()
                        .map(Tag::getName)
                        .toList()
                        .contains("popup"))
                .map(it -> PopupItem.builder().name(it.getPromptName()).desc(it.getDesc()).build()).collect(Collectors.toList());
        ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<>("Mione AI Code助手", list) {
            @Override
            public PopupStep onChosen(PopupItem selectedValue, boolean finalChoice) {
                String pName = selectedValue.getName();
                //移动光标位置
                if (GENERATE_CODE.equals(pName)) {
                    EditorUtils.moveCaretToCommentEnd(EditorUtils.getEditor(project));
                }
                if (UNIT_TEST.equals(pName)) {
                    doFinalStep(() -> ApplicationManager.getApplication().runReadAction(() -> {
                        executePrompt(pName, project);
                    }));
                } else {
                    ApplicationManager.getApplication().executeOnPooledThread(() -> {
                        executePrompt(pName, project);
                    });
                }
                return super.onChosen(selectedValue, finalChoice);
            }
        });

        if (mouseEvent != null) {
            listPopup.showInScreenCoordinates(mouseEvent.getComponent(), mouseEvent.getLocationOnScreen());
        } else if (statusBar != null) {
            Rectangle r = statusBar.getComponent().getBounds();
            Point p = new Point(r.x + r.width, r.y + r.height);
            RelativePoint showPoint = new RelativePoint(statusBar.getComponent(), p);
            listPopup.show(showPoint);
        } else {
            Editor editor = EditorUtils.getEditor(project);
            if (editor instanceof EditorEx) {
                EditorEx editorEx = (EditorEx) editor;
                LogicalPosition logicalPosition = editorEx.getCaretModel().getLogicalPosition();
                VisualPosition visualPosition = editorEx.logicalToVisualPosition(logicalPosition);
                Point point = editorEx.visualPositionToXY(visualPosition);
                RelativePoint showPoint = new RelativePoint(editorEx.getContentComponent(), point);
                listPopup.show(showPoint);
            }
        }
    }

    private static void executePrompt(String promptName, Project project) {
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        PromptType promptType = Prompt.getPromptType(promptInfo);
        ClientData clientData = AthenaContext.ins().getClientData(project.getName());
        if (AthenaInspection.isBotUsageConfigured(promptInfo)) {
            promptType = PromptType.executeBot;
        }
        PromptService.dynamicInvoke(GenerateCodeReq.builder()
                .scope(clientData.getScope())
                .project(project)
                .projectName(project.getName())
                .promptInfo(promptInfo)
                .promptType(promptType)
                .promptName(promptName)
                .build());
    }

    private static boolean isCursorInComment(Project project) {
        Editor editor = CodeService.getEditor(project);
        if (null != editor) {
            boolean isInComment = CodeService.isCursorInComment(editor);
            log.debug("is in comment:{}", isInComment);
            return isInComment;
        }
        return false;
    }
}
