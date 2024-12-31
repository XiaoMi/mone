package run.mone.ultraman.listener;

import com.intellij.execution.Executor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Key;
import com.intellij.util.messages.MessageBusConnection;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiMessageManager;
import com.xiaomi.youpin.tesla.ip.bo.robot.ProjectAiMessageManager;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Safe;
import com.xiaomi.youpin.tesla.ip.util.NetUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.listener.event.TaskEvent;
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

        Safe.run(() -> {
            // 订阅事件
            RunContentWithExecutorListener myListener = new RunContentWithExecutorListener() {
                @Override
                public void contentSelected(RunContentDescriptor descriptor, Executor executor) {

                    @Nullable ProcessHandler processHandler = descriptor.getProcessHandler();
                    if (processHandler != null) {
                        // 添加 ProcessListener 来监听输出
                        processHandler.addProcessListener(new ProcessAdapter() {
                            @Override
                            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                                // 这里可以捕获输出信息
                                String text = event.getText();
                                // 根据 outputType 区分 stdout 和 stderr
                                if (outputType == ProcessOutputTypes.STDOUT) {
                                    System.out.println("STDOUT: " + text);
                                } else if (outputType == ProcessOutputTypes.STDERR) {
                                    System.out.println("STDERR: " + text);
                                }
                            }
                        });
                    }

                }

                @Override
                public void contentRemoved(RunContentDescriptor descriptor, Executor executor) {
                    // 处理内容被移除的事件
                }
            };
            MessageBusConnection connection = project.getMessageBus().connect();
            connection.subscribe(RunContentManager.TOPIC, myListener);
            connection.subscribe(TaskEvent.TOPIC, new TaskEvent.TaskEventListenerImpl(project));
        });


        AthenaContext.ins().getProjectMap().put(project.getName(), project);
        AthenaContext.ins().setGptModel(ConfigUtils.getConfig().getModel());
        AthenaContext.ins().setNoChatModel(ConfigUtils.getConfig().getNoChatModel());
        AthenaContext.ins().setLocalAddress(NetUtils.getLocalHost());
        AthenaContext.ins().setLocalPort(ResourceUtils.getAthenaConfig().get(Const.CONF_PORT));
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
