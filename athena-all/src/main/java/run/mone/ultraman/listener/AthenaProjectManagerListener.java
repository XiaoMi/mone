package run.mone.ultraman.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import run.mone.m78.ip.bo.robot.AiMessageManager;
import run.mone.m78.ip.bo.robot.ProjectAiMessageManager;
import run.mone.m78.ip.common.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.state.FsmManager;
import run.mone.ultraman.state.ProjectFsmManager;

/**
 * @author goodjava@qq.com
 * @date 2023/6/25 23:29
 */
@Slf4j
public class AthenaProjectManagerListener implements ProjectManagerListener {


    @Override
    public void projectOpened(Project project) {
        log.info("Project reopened: " + project.getName());
        AthenaContext.ins().getProjectMap().put(project.getName(), project);
        AthenaContext.ins().setGptModel(ConfigUtils.getConfig().getModel());
        ProjectAiMessageManager.getInstance().putMessageManager(project.getName(), new AiMessageManager());
        FsmManager fsmManager = new FsmManager();
        fsmManager.setProject(project.getName());
        fsmManager.init();
        ProjectFsmManager.map.put(project.getName(), fsmManager);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        AthenaContext.ins().getProjectMap().remove(project.getName());
        ProjectAiMessageManager.getInstance().removeMessageManager(project.getName());
        ProjectFsmManager.map.remove(project.getName());
    }
}
