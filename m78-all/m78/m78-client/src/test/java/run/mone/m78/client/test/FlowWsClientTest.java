package run.mone.m78.client.test;

import org.junit.jupiter.api.Test;
import run.mone.m78.client.flow.FlowWsClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/26/24 2:18 PM
 */
public class FlowWsClientTest {

    /**
     * 测试执行流程的方法,连通性测试,本地m78会报找不到agent
     *
     * @throws InterruptedException 如果线程在等待、睡眠或占用时被中断
     */
    @Test
    public void testExecuteFlow() throws InterruptedException {
        FlowWsClient flowWsClient = FlowWsClient.builder().build();
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("scale", "256");
        String userName = "name";
        assertDoesNotThrow(() -> {
            flowWsClient.start(System.out::println, userName);
            flowWsClient.executeFlow(userName, "127", inputs);
        });
        TimeUnit.SECONDS.sleep(20);
    }
}