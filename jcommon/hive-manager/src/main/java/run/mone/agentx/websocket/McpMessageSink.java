package run.mone.agentx.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.schema.Message;
import java.util.function.LongConsumer;

@Slf4j
@RequiredArgsConstructor
public class McpMessageSink implements FluxSink<Message> {
    private final WebSocketSession session;

    @Override
    public FluxSink<Message> next(Message message) {
        try {
            String jsonMessage = GsonUtils.gson.toJson(message);
            WebSocketHolder.sendMessageSafely(session, jsonMessage);
        } catch (Exception e) {
            log.error("Error sending message through WebSocket", e);
        }
        return this;
    }

    @Override
    public void complete() {
        try {
            WebSocketHolder.sendMessageSafely(session, "{\"type\": \"complete\"}");
        } catch (Exception e) {
            log.error("Error sending complete message", e);
        }
    }

    @Override
    public void error(Throwable t) {
        try {
            log.error("Error in MCP processing", t);
            WebSocketHolder.sendMessageSafely(session, 
                "{\"type\": \"error\", \"message\": \"" + t.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Error sending error message", e);
        }
    }

    @Override
    public Context currentContext() {
        return Context.empty();
    }

    @Override
    public boolean isCancelled() {
        return !session.isOpen();
    }



    @Override
    public long requestedFromDownstream() {
        return Long.MAX_VALUE;
    }


    @Override
    public FluxSink<Message> onRequest(LongConsumer consumer) {
        // 这里可以处理背压，但在WebSocket场景下通常不需要
        return this;
    }

    @Override
    public FluxSink<Message> onCancel(Disposable disposable) {
        return null;
    }

    @Override
    public FluxSink<Message> onDispose(Disposable disposable) {
        return null;
    }
} 