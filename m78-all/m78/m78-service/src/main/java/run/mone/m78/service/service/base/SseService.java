package run.mone.m78.service.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static run.mone.m78.api.constant.CommonConstant.SSE_TIMEOUT;

/**
 * @author goodjava@qq.com
 * @date 2023/5/25 14:08
 */
@Service
@Slf4j
public class SseService {

    private static final Map<String, SseEmitter> SSE_EMITTERS = new HashMap<>();

    private ExecutorService ssePool = Executors.newFixedThreadPool(100);

    /**
     * 注册一个SseEmitter实例，设置60秒的超时时间，并为完成、超时和错误事件添加日志记录，将其存储在SSE_EMITTERS映射中，并返回该实例。
     */
    public SseEmitter registerSseEmitter(String id) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitter.onCompletion(() -> log.info("SseEmitter is completed"));
        emitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        emitter.onError((ex) -> log.info("SseEmitter got error:" + ex.getMessage()));
        SSE_EMITTERS.put(id, emitter);
        try {
            emitter.send(SseEmitter.event().name("start").data(id));
        } catch (IOException e) {
            log.error("registerSseEmitter send [start] error", e);
        }
        return emitter;
    }

    /**
     * 发送消息到指定的SSE连接
     *
     * @param id SSE连接的唯一标识符
     * @param data 要发送的数据
     */
	public void sendMessage(String id, Object data) {
        SseEmitter emitter = SSE_EMITTERS.get(id);
        if (emitter == null) {
            log.warn("emitter is null.{}", id);
            return;
        }
        try {
            emitter.send(data.toString(), MediaType.parseMediaType(MediaType.TEXT_EVENT_STREAM_VALUE));
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 完成指定ID的SSE连接
     *
     * @param id SSE连接的唯一标识符
     */
	public void complete(String id) {
        SseEmitter emitter = SSE_EMITTERS.get(id);
        if (emitter == null) {
            log.warn("emitter is null.{}", id);
            return;
        }
        log.info("id:{} complete", id);
        try {
            emitter.send(SseEmitter.event().name("end").data(id));
        } catch (IOException e) {
            log.error("registerSseEmitter send [end] error", e);
        }
        emitter.complete();
        SSE_EMITTERS.remove(id);
    }

    /**
     * 提交一个任务并注册一个SseEmitter
     *
     * @param id 标识符，用于注册SseEmitter
     * @param task 要提交的任务
     * @return 注册的SseEmitter
     */
	public SseEmitter submit(String id, Runnable task) {
        SseEmitter emitter = registerSseEmitter(id);
        ssePool.submit(task);
        return emitter;
    }




}
