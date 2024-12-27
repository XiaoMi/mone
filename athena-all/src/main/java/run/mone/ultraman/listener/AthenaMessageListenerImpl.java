package run.mone.ultraman.listener;

import com.intellij.openapi.application.ApplicationManager;
import lombok.extern.slf4j.Slf4j;
import run.mone.ultraman.listener.bo.AthenaMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/7/14 15:24
 */
@Slf4j
public class AthenaMessageListenerImpl implements AthenaMessageListener {

    private ConcurrentHashMap<String, Consumer> consumerMap = new ConcurrentHashMap<>();

    @Override
    public void handleMessage(AthenaMessage message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                log.info("Received message: " + message);
                if (message.getType().equals("reg")) {
                    consumerMap.put(message.getName(),message.getConsumer());
                }

                if (message.getType().equals("unreg")) {
                    consumerMap.get(message.getName()).accept(null);
                }

            }
        });
    }
}
