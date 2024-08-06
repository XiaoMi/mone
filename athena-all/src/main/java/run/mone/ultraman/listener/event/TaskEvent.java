package run.mone.ultraman.listener.event;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.background.AiBackgroundTask;

/**
 * @author goodjava@qq.com
 * @date 2024/6/1 21:48
 */
@Data
@Builder
@Slf4j
public class TaskEvent {

    public static final Topic<TaskEventListener> TOPIC = Topic.create("TaskEvent", TaskEventListener.class);

    private String message;

    private long time;


    public interface TaskEventListener {
        void onEvent(TaskEvent event);
    }

    public static class TaskEventListenerImpl implements TaskEventListener {

        private Project project;

        private AiBackgroundTask task;

        public TaskEventListenerImpl(Project project) {
            this.project = project;
        }

        @Override
        public void onEvent(TaskEvent event) {
            if (event.getMessage().equals("begin")) {
                this.task = new AiBackgroundTask(project, "Athena");
                ProgressManager.getInstance().run(task);
            }
            if (event.getMessage().equals("end")) {
                if (null != this.task) {
                    this.task.cancel();
                    log.info("use time:{} s", event.getTime());
                }
            }
        }
    }

}
