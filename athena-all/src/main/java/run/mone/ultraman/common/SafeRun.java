package run.mone.ultraman.common;

/**
 * @author goodjava@qq.com
 * @date 2023/4/19 14:30
 */
public class SafeRun {

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
