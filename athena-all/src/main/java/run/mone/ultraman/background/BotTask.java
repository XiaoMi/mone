package run.mone.ultraman.background;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2024/6/2 22:29
 */
public class BotTask extends Task.Backgroundable {

    public BotTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title) {
        super(project, title);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(false);


    }
}
