package run.mone.agentx.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;
import java.util.function.LongConsumer;

@Slf4j
@RequiredArgsConstructor
public class McpMessageSink implements FluxSink<String> {
    private final WebSocketSession session;

    @Override
    public FluxSink<String> next(String message) {
        try {
            WebSocketHolder.sendMessageSafely(session, message);
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
    public FluxSink<String> onRequest(LongConsumer consumer) {
        // 这里可以处理背压，但在WebSocket场景下通常不需要
        return this;
    }

    @Override
    public FluxSink<String> onCancel(Disposable disposable) {
        return null;
    }

    @Override
    public FluxSink<String> onDispose(Disposable disposable) {
        return null;
    }
} 