package run.mone.ultraman.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.statusbar.AthenaStatusBarWidget;
import run.mone.ultraman.statusbar.PopUpReq;

/**
 * @author HawickMason@xiaomi.com
 * @date 6/22/24 15:16
 */
public class AthenaStatusBarAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        AthenaStatusBarWidget.popUp(project, null, null, PopUpReq.builder().build());
    }
}
