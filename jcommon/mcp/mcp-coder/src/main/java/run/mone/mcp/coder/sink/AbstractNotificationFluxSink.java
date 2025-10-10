package run.mone.mcp.coder.sink;

import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;

import java.util.function.LongConsumer;

/**
 * FluxSink 抽象实现类
 * 提供基础的 FluxSink 实现，子类只需实现具体的消息发送逻辑
 * 
 * @author goodjava@qq.com
 * @date 2025/10/9
 */
public abstract class AbstractNotificationFluxSink implements FluxSink<String> {

    @Override
    public FluxSink<String> next(String message) {
        sendNotification(message);
        return this;
    }

    @Override
    public void complete() {
        // 子类可以重写此方法实现完成逻辑
    }

    @Override
    public void error(Throwable e) {
        // 子类可以重写此方法实现错误处理逻辑
    }

    @Override
    public Context currentContext() {
        return null;
    }

    @Override
    public long requestedFromDownstream() {
        return 0;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public FluxSink<String> onRequest(LongConsumer consumer) {
        return null;
    }

    @Override
    public FluxSink<String> onCancel(Disposable d) {
        return null;
    }

    @Override
    public FluxSink<String> onDispose(Disposable d) {
        return null;
    }

    /**
     * 抽象方法：发送通知消息
     * 子类需要实现具体的消息发送逻辑
     * 
     * @param message 要发送的消息内容
     */
    protected abstract void sendNotification(String message);
}
