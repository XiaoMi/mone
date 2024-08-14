package run.mone.ultraman.background;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/6/1 21:10
 */
public class AiBackgroundTask extends Task.Backgroundable {

    private volatile boolean isCancelled = false;

    private CountDownLatch latch = new CountDownLatch(1);

    public AiBackgroundTask(Project project, String title) {
        super(project, title);
    }

    @Override
    public void run(ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(false);
        progressIndicator.setFraction(0.0);
        while (!isCancelled && !progressIndicator.isCanceled()) {
            try {
                latch.await(1, TimeUnit.MINUTES);
            } catch (Throwable ignore) {

            } finally {
                break;
            }
        }
    }

    public void cancel() {
        isCancelled = true;
        latch.countDown();
    }

}
