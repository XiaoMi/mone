package run.mone.m78.client.bot;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import run.mone.m78.client.flow.DefaultFlowMsgConsumer;
import run.mone.m78.client.model.M78Message;
import run.mone.m78.client.model.M78MessageType;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 16:39
 */
@Slf4j
public class DefaultBotMsgConsumer implements Consumer<M78Message> {

    private final DefaultFlowMsgConsumer flowMsgConsumer = new DefaultFlowMsgConsumer();

    private Stopwatch sw;

    public DefaultBotMsgConsumer(Stopwatch sw) {
        this.sw = sw;
    }

    @Override
    public void accept(M78Message msg) {
        log.info("接收到消息：{}", msg);
        switch (msg.getCategory()) {
            case bot:
                handleBotCategory(msg);
                break;
            case flow:
                handleFlowCategory(msg);
                break;
            default:
                log.warn("未知消息类型：{}, 不做处理", msg.getCategory());
        }
    }

    private void handleBotCategory(M78Message msg) {
        doHandleBotCategory(msg);
    }


    private void handleFlowCategory(M78Message msg) {
        if (doHandleFlowCategory(msg)) {
            return;
        }
        flowMsgConsumer.accept(msg);
    }

    protected void doHandleBotCategory(M78Message msg) {
        processGeneratedMsg(msg, Context.builder().build());
    }

    protected boolean doHandleFlowCategory(M78Message msg) {
        return false;
    }


    protected void processGeneratedMsg(M78Message msg, Context context) {
        handleBeginMsg(msg, context);
        handleContentMsg(msg, context);
        handleEndMsg(msg, context);
        handleError(msg, context);
    }

    protected void handleBeginMsg(@NotNull M78Message msg, Context context) {
        log.info("handleBeginMsg: {}, context:{}", msg.getId(), context);
        if (msg.getType().equals(M78MessageType.begin)) {
            // TODO you may override this
        }
    }

    protected void handleContentMsg(M78Message msg, Context context) {
        log.info("handleContentMsg: {}, context:{}", msg.getId(), context);
        if (msg.getType().equals(M78MessageType.process)) {
            // TODO you may override this
        }
    }

    protected void handleEndMsg(M78Message msg, Context context) {
        log.info("handleEndMsg: {}, context:{}", msg.getId(), context);
        if (msg.getType().equals(M78MessageType.success)) {
            // TODO you may override this
        }
    }

    protected void handleError(M78Message msg, Context context) {
        log.info("handleError: {}, context:{}", msg.getId(), context);
        if (msg.getType().equals(M78MessageType.failure)) {
            // TODO you may override this
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Context {
        private Map<String, Object> context;
    }
}
