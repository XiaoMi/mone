package com.xiaomi.mone.app.job;

import com.xiaomi.mone.app.service.HeraAppEnvService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/29 17:12
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class HeraAppEnvIpJob {

    @Autowired
    private HeraAppEnvService heraAppEnvService;

    @Value("${job_start_flag}")
    public String jobStartFlag;

    /**
     * 从1分钟开始后每2分钟执行一次
     */
    @Scheduled(cron = "0 1/2 * * * ?")
    public void init() {
        log.info("HeraAppEnvIpJob:{},HeraAppEnvIpJob execute！ time:{}", jobStartFlag, LocalDateTime.now());
        heraAppEnvService.fetchIpsOpByApp(Strings.EMPTY);
    }
}
