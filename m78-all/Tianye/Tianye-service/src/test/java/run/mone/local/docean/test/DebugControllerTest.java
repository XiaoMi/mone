package run.mone.local.docean.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.fsm.debug.DebugController;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/3/25 11:54
 */
public class DebugControllerTest {


    @SneakyThrows
    @Test
    public void test1() {
        DebugController controller = new DebugController(false);
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            controller.setDebug(false);
        }).start();

        controller.waitForDebug();
        System.out.println("finish");
    }
}
