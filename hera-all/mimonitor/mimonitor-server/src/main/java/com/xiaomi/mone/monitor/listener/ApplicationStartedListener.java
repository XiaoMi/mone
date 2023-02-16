package com.xiaomi.mone.monitor.listener;

import com.xiaomi.mone.monitor.service.rocketmq.RocketMqHeraAppConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author gaoxihui
 * @date 2021/7/9 10:23 下午
 */
@Slf4j
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

        log.info("accept and process ApplicationStartedEvent ... ");
        RocketMqHeraAppConsumer rocketMqConsumerHera = (RocketMqHeraAppConsumer) applicationStartedEvent.getApplicationContext().getBean("rocketMqConsumerHera");
        rocketMqConsumerHera.start();
        log.info("process ApplicationStartedEvent finish ... ");
    }

}
