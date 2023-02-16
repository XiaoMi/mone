package com.xiaomi.mone.log.manager.job;

import cn.hutool.core.io.FileUtil;
import com.xiaomi.mone.log.manager.porcessor.PingProcessor;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/4 17:05
 */
@Component
@Slf4j
public class AgentHearJob {

    /**
     * 一个agent的简单报警报警，长时间没有心跳链接，会发送报警信息，开发介入查看（2m）
     */
    private static final Integer MAX_INTERRUPT_TIME = 2;

    private static final Integer MAX_SEND_NUM = 10;

    private static Map<String, Integer> alarmMap = new ConcurrentHashMap<>(64);

    @Value("$job_start_flag")
    public String jobStartFlag;

    @Value("$agent.heart.senders")
    private String agentHeartSenders;

    @Value("$server.type")
    private String env;

    public void init() {
        log.info("misSwitch:{},AgentHearJob execute！ time:{}", jobStartFlag, LocalDateTime.now());
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        long initDelay = 0;
        long intervalTime = 1;
        if (Boolean.parseBoolean(jobStartFlag) == Boolean.TRUE) {
            scheduledExecutor.scheduleAtFixedRate(() -> {
                PingProcessor.agentHeartTimeStampMap.entrySet().stream().forEach(keyTimeEntry -> {
                    if (Instant.now().toEpochMilli() - keyTimeEntry.getValue() >
                            TimeUnit.MINUTES.toMillis(MAX_INTERRUPT_TIME)) {
                        alarmMap.putIfAbsent(keyTimeEntry.getKey(), 0);
                        alarmMap.compute(keyTimeEntry.getKey(), (key, oldKey) -> oldKey + 1);
                        if (alarmMap.get(keyTimeEntry.getKey()) < MAX_SEND_NUM) {
                            // 发送报警
                            String template = FileUtil.readString("agentHeart.json", CharsetUtil.UTF_8.name());
//                            feishuService.sendFeishu(String.format(template, keyTimeEntry.getKey(), env), agentHeartSenders.split(SYMBOL_COMMA), null, true);
                        }
                    }
                });

            }, initDelay, intervalTime, TimeUnit.MINUTES);
        }
    }
}
