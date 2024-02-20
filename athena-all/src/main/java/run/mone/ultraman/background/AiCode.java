package run.mone.ultraman.background;

import com.google.gson.Gson;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.MessageConsumer;
import run.mone.m78.ip.common.ChromeUtils;
import run.mone.m78.ip.common.Safe;
import run.mone.m78.ip.util.UltramanConsole;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/6/25 14:27
 */
@Slf4j
public class AiCode extends MessageConsumer {

    private static Gson gson = new Gson();

    private ProgressIndicator progressIndicator;

    private CountDownLatch countDownLatch;

    private Date beginTime;

    private float totalTime = 5.0f;

    protected StringBuilder sb = new StringBuilder();

    @Setter
    private Project project;


    private Consumer<AiMessage> consumer;


    private String messageId = UUID.randomUUID().toString();


    public String getMessageId() {
        return this.messageId;
    }



    public AiCode(ProgressIndicator progressIndicator, CountDownLatch countDownLatch) {
        this.progressIndicator = progressIndicator;
        this.countDownLatch = countDownLatch;
    }

    public AiCode(ProgressIndicator progressIndicator, CountDownLatch countDownLatch, Project project) {
        this.progressIndicator = progressIndicator;
        this.countDownLatch = countDownLatch;
        this.project = project;
    }

    public AiCode(ProgressIndicator progressIndicator, CountDownLatch countDownLatch, Project project, Consumer<AiMessage> consumer) {
        this.progressIndicator = progressIndicator;
        this.countDownLatch = countDownLatch;
        this.project = project;
        this.consumer = consumer;
    }

    @Override
    public void begin(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        beginTime = new Date();
        messageId = message.getId();
        if (null != this.project) {
            UltramanConsole.append(project, "\ncode generate begin \n");
        }
        if (null != consumer) {
            consumer.accept(message);
        }
        Safe.run(() -> ChromeUtils.call(message.getProjectName(), "setResultCode", str));
    }

    @Override
    public void onEvent(AiMessage message) {
        sb.append(message.getText());
        String str = gson.toJson(message);
        log.info(str);
        if (!progressIndicator.isCanceled()) {
            long sencodes = getSecondsBetween(this.beginTime.toInstant(), Instant.now());
            progressIndicator.setFraction(Math.min(sencodes / totalTime, 0.99));
            if (null != this.project) {
                UltramanConsole.append(project, deocde(str), false);
            }
            if (null != consumer) {
                this.consumer.accept(message);
            }
            Safe.run(() -> ChromeUtils.call(message.getProjectName(), "setResultCode", str));
        }
    }


    @Data
    class T {
        private String text;
    }


    private String deocde(String str) {
        try {
            T t = gson.fromJson(str, T.class);
            return t.getText();
        } catch (Throwable ex) {
            return "";
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
            Safe.run(() -> ChromeUtils.call(message.getProjectName(), "setResultCode", str));
            progressIndicator.setFraction(1);
        }
        Safe.run(() -> {
            if (progressIndicator.isRunning()) {
                progressIndicator.stop();
            }
        });
        if (null != this.project) {
            UltramanConsole.append(project, "\n\ncode generate end\n\n");
        }
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


    public String getText() {
        return sb.toString();
    }

    public String getMarkDownText() {
        return "```\n" + sb.toString() + "\n```";
    }


}
