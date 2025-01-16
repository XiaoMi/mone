package run.mone.m78.service.service.feature.router.asyncCallTask;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class TaskExecutor {

    private static ExecutorService executor = new ThreadPoolExecutor(50, 200, TimeUnit.SECONDS.toMillis(60), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));

    public static <R> void submit(String taskId,
                                  Callable<R> action,
                                  Consumer<R> onResult,
                                  Consumer<String> onFailure) {

        executor.submit(() -> {
            try {

                R result = action.call();
                onResult.accept(result);

            } catch (Throwable e) {
                log.error("[LoopExecutor.executeLoop], taskId: {}, error msg: {}", taskId, e.getMessage());
                onFailure.accept(taskId + ":" + e.getMessage());
            }
        });
    }

}
