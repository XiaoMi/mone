package run.mone.m78.service.service.multiModal.audio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.common.SafeRun;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j

/**
 * AudioStreamService类负责处理音频流数据，管理音频缓冲区，并检测静音状态。
 *
 * <p>该类使用LRU缓存机制来管理音频缓冲区，并定期清理长时间未使用的缓冲区。
 * 主要功能包括接收和处理音频数据、检测静音状态以及在静音或超时情况下返回处理结果。
 * </p>
 *
 * <p>主要组件和功能：
 * <ul>
 *   <li>lastReceivedMap：记录每个音频数据的最后接收时间。</li>
 *   <li>lruCache：使用LRU缓存机制管理音频缓冲区。</li>
 *   <li>silenceDetector：用于检测音频数据中的静音状态。</li>
 *   <li>init()：初始化方法，定期清理长时间未使用的缓冲区。</li>
 *   <li>processAudio()：处理音频数据并返回处理结果。</li>
 *   <li>getAndClearBuffer()：获取并清理缓冲区中的数据。</li>
 *   <li>cleanupBuffers()：定期清理长时间未使用的缓冲区。</li>
 * </ul>
 * </p>
 *
 * <p>该类通过注解@Service和@Slf4j进行标注，表明其为一个Spring服务类，并使用日志记录功能。</p>
 */

public class AudioStreamService {
    private Map<String, Long> lastReceivedMap = Collections.synchronizedMap(new LinkedHashMap<>());

    // 使用LRU缓存来管理音频缓冲区
    private Map<String, ByteBuffer> lruCache = Collections.synchronizedMap(
            new LinkedHashMap<String, ByteBuffer>(16, 0.75f, true) {
                private static final int MAX_ENTRIES = 200;

                protected boolean removeEldestEntry(Map.Entry<String, ByteBuffer> eldest) {
                    return size() > MAX_ENTRIES;
                }
            }
    );

    private static final int SILENCE_DURATION_THRESHOLD = 3000;
    private static final int BUFFER_SIZE = 1024 * 1024 * 4; // 4MB 缓冲区

    @Autowired
    private SilenceDetector silenceDetector;

    @PostConstruct
    private void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            SafeRun.run(() -> cleanupBuffers());
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 处理音频数据并返回处理结果
     *
     * @param userId        用户的唯一标识
     * @param audioProtocol 音频协议对象，包含音频数据和唯一标识
     * @return 处理后的音频数据，如果未形成完整数据则返回null
     */
    public String processAudio(String userId, AudioProtocol audioProtocol) {
        String uniqueKey = userId + "_" + audioProtocol.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // 更新最后接收时间
        lastReceivedMap.put(uniqueKey, currentTime);

        // 获取或创建缓冲区
        ByteBuffer buffer = lruCache.computeIfAbsent(uniqueKey, k -> ByteBuffer.allocate(BUFFER_SIZE));

        // 将新的音频数据追加到缓冲区
        if (buffer.remaining() >= audioProtocol.getPayload().length) {
            buffer.put(audioProtocol.getPayload());
        } else {
            log.warn("Buffer overflow for key: {}. Discarding new data.", uniqueKey);
            // 可以考虑在这里实现缓冲区的扩容策略
        }

        // 判断是否符合静音阈值
        if (silenceDetector.isSilence(audioProtocol.getPayload())) {
            return getAndClearBuffer(uniqueKey);
        }

        // 检查是否超过3秒未接收数据
        if (currentTime - lastReceivedMap.get(uniqueKey) >= SILENCE_DURATION_THRESHOLD) {
            return getAndClearBuffer(uniqueKey);
        }

        // 尚未形成一句完整的话
        return null;
    }

    private String getAndClearBuffer(String uniqueKey) {
        ByteBuffer buffer = lruCache.remove(uniqueKey);
        lastReceivedMap.remove(uniqueKey);
        if (buffer != null) {
            buffer.flip();
            byte[] result = new byte[buffer.limit()];
            buffer.get(result);

            //todo, return result
            return "你好，你是谁";
        }
        return "";
    }

    // 定期清理长时间未使用的缓冲区
    private void cleanupBuffers() {
        long currentTime = System.currentTimeMillis();
        lastReceivedMap.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > SILENCE_DURATION_THRESHOLD * 2) {
                lruCache.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

}