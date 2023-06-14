package com.xiaomi.mone.log.manager.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getConfigFromNanos;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/9 14:38
 */
@Slf4j
public class StatisticsServiceTest {

    private StatisticsServiceImpl statisticsService;
    private Gson gson;

    @Before
    public void before() {
        getConfigFromNanos();
        Ioc.ins().init("com.xiaomi");
        statisticsService = Ioc.ins().getBean(StatisticsServiceImpl.class);
        gson = new Gson();
    }

    @Test
    public void queryEsStatisticsRationTest() {
        statisticsService.queryEsStatisticsRation(81L);
    }

}
