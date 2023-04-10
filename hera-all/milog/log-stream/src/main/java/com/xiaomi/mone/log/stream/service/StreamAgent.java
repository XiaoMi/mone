package com.xiaomi.mone.log.stream.service;

import com.alibaba.nacos.client.config.utils.SnapShotSwitch;
import com.xiaomi.mone.log.stream.compensate.MqMessageConsume;
import com.xiaomi.mone.log.stream.compensate.RocketMqMessageConsume;
import com.xiaomi.mone.log.stream.config.ConfigManager;
import com.xiaomi.mone.log.stream.config.MilogConfigListener;
import com.xiaomi.mone.log.stream.job.JobManager;
import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Boolean.FALSE;

@Service
@Slf4j
public class StreamAgent {

    @Resource
    private ConfigManager configManager;

    @Resource
    private JobManager jobManager;

    public void init() {
        try {
            log.info("start");
            if (EsPlugin.InitEsConfig()) {
                SnapShotSwitch.setIsSnapShot(FALSE);
                configManager.listenMilogStreamConfig();
                /**
                 * start compensate mq msg job
                 */
                startCompensateMq();
            } else {
                System.exit(1);
            }
            graceShutdown();
        } catch (Exception e) {
            log.error("服务初始化异常", e);
        }

    }

    private void startCompensateMq() {
        MqMessageConsume talosMqMessageConsume = new RocketMqMessageConsume();
        talosMqMessageConsume.consume();
    }

    private void graceShutdown() {
        //关闭操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stream shutdown!");
            ConcurrentHashMap<Long, MilogConfigListener> listeners = configManager.getListeners();
            listeners.values().forEach(milogConfigListener -> {
                milogConfigListener.getJobManager().stopAllJob();
            });
        }));
    }

}
