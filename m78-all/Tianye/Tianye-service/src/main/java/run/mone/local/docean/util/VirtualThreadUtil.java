package run.mone.local.docean.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangping17
 * @date 2024/3/11 15:10
 */
public class VirtualThreadUtil {

    public static ExecutorService getExecutorService() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        return executorService;
    }
}
