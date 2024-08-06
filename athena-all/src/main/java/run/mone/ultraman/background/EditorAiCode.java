package run.mone.ultraman.background;

import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.bo.MessageConsumer;
import com.xiaomi.youpin.tesla.ip.common.Safe;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class EditorAiCode extends MessageConsumer {

    private static Gson gson = new Gson();

    private ProgressIndicator progressIndicator;

    private CountDownLatch countDownLatch;

    private Date beginTime;

    private float totalTime = 5.0f;

    private MessageConsumer consumer;

    public EditorAiCode(ProgressIndicator progressIndicator, CountDownLatch countDownLatch, MessageConsumer consumer) {
        this.progressIndicator = progressIndicator;
        this.countDownLatch = countDownLatch;
        this.consumer = consumer;
    }

    @Override
    public void begin(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        beginTime = new Date();
        consumer.begin(message);
    }

    @Override
    public void onEvent(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        if (!progressIndicator.isCanceled()) {
            long sencodes = getSecondsBetween(this.beginTime.toInstant(), Instant.now());
            progressIndicator.setFraction(Math.min(sencodes / totalTime, 0.99));
            consumer.onEvent(message);
        }
    }

    public long getSecondsBetween(Instant past, Instant present) {
        return Duration.between(past, present).getSeconds();
    }

    @Override
    public void end(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        if (!progressIndicator.isCanceled()) {
            consumer.end(message);
            progressIndicator.setFraction(1);
        }
        Safe.run(() -> {
            if (progressIndicator.isRunning()) {
                progressIndicator.stop();
            }
        });
        countDownLatch.countDown();
    }

    @Override
    public void failure(AiMessage message) {
        Safe.run(() -> {
            if (progressIndicator.isRunning()) {
                progressIndicator.stop();
            }
        });
        countDownLatch.countDown();
    }


}
