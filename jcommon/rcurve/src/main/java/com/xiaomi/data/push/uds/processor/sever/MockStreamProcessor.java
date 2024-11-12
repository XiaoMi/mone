package com.xiaomi.data.push.uds.processor.sever;

import com.xiaomi.data.push.uds.handler.MessageTypes;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.StreamCallback;
import com.xiaomi.data.push.uds.processor.UdsProcessor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2024/11/7 11:53
 */
public class MockStreamProcessor implements UdsProcessor<UdsCommand, UdsCommand> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, ScheduledFuture<?>> activeStreams = new ConcurrentHashMap<>();

    @Override
    public boolean isStreamProcessor() {
        return true;
    }

    @Override
    public String cmd() {
        return "stream";
    }

    @Override
    public UdsCommand processRequest(UdsCommand udsCommand) {
        return null;
    }


    @Override
    public void processStream(UdsCommand request, StreamCallback callback) {

        String streamId = request.getAttachments().getOrDefault(
                MessageTypes.STREAM_ID_KEY,
                UUID.randomUUID().toString()
        );

        AtomicInteger counter = new AtomicInteger(0);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                int currentCount = counter.incrementAndGet();

                if (currentCount <= 10) {
                    callback.onContent(String.valueOf(currentCount));

                    if (currentCount == 10) {
                        callback.onComplete();
                        ScheduledFuture<?> scheduledFuture = activeStreams.remove(streamId);
                        if (scheduledFuture != null) {
                            scheduledFuture.cancel(false);
                        }
                    }
                }
            } catch (Exception e) {
                callback.onError(e);
                ScheduledFuture<?> scheduledFuture = activeStreams.remove(streamId);
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        activeStreams.put(streamId, future);

    }
}
