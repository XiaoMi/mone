package run.mone.ultraman.statusbar;

import com.intellij.openapi.application.ApplicationManager;
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
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.common.PromptType;
import run.mone.m78.ip.service.PromptService;
import run.mone.m78.ip.util.LabelUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.ClientData;
import run.mone.ultraman.statusbar.bo.PopupItem;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/7/18 23:41
 */
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
                return "Athena";
            }

            @Override
            public @Nullable Consumer<MouseEvent> getClickConsumer() {
                return (MouseEvent e) -> {

                    if (!LabelUtils.open(Const.ENABLE_ATHENA_STATUS_BAR)) {
                        return;
                    }

                    List<PromptInfo> collectedList = Prompt.getCollected();
                    collectedList.addAll(Prompt.getPromptInfoByTag("system"));
                    List<PopupItem> list = collectedList.stream().filter(it -> it.getTags().stream().map(i -> i.getName()).collect(Collectors.toList()).contains("popup")).map(it -> PopupItem.builder().name(it.getPromptName()).desc(it.getDesc()).build()).collect(Collectors.toList());
                    ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<PopupItem>("Prompt", list) {
                        @Override
                        public PopupStep onChosen(PopupItem selectedValue, boolean finalChoice) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                PromptInfo info = Prompt.getPromptInfo(selectedValue.getName());
                                PromptType type = Prompt.getPromptType(info);
                                ClientData clientData = AthenaContext.ins().getClientData(project.getName());
                                PromptService.dynamicInvoke(GenerateCodeReq.builder()
                                        .scope(clientData.getScope())
                                        .project(project)
                                        .projectName(project.getName())
                                        .promptInfo(info).promptType(type)
                                        .promptName(selectedValue.getName())
                                        .build());
                            });
                            return super.onChosen(selectedValue, finalChoice);
                        }
                    });
                    Rectangle r = statusBar.getComponent().getBounds();
                    Point p = new Point(r.x + r.width, r.y + r.height);
                    RelativePoint showPoint = new RelativePoint(statusBar.getComponent(), p);
                    listPopup.show(showPoint);
                };
            }

            @Override
            public @NotNull @NlsContexts.Label String getText() {
                return "Athena";
            }

            @Override
            public float getAlignment() {
                return 0;
            }
        };
    }
}
