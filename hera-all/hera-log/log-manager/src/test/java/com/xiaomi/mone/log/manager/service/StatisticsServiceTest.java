package com.xiaomi.mone.log.manager.service;

import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.EsStatisticsKeyWord;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.xiaomi.mone.log.common.Constant.GSON;
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
        String str = "{\"spaceId\":\"34\",\"storeId\":81,\"area\":\"cn\",\"tail\":\"\",\"logstore\":\"tesla_gateway\",\"startTime\":1686813900596,\"endTime\":1686817500999,\"fullTextSearch\":\"\",\"page\":1,\"pageSize\":20,\"beginSortValue\":null,\"sortKey\":\"timestamp\",\"asc\":\"false\",\"appIds\":[452,1512,1512,754,1512,1512,1512,4,4,4,4,1512,1512]}";
        LogQuery logQuery = GSON.fromJson(str, LogQuery.class);
        Result<List<EsStatisticsKeyWord>> esStatisticsRation = statisticsService.queryEsStatisticsRation(logQuery);
        log.info("result:{}", GSON.toJson(esStatisticsRation));
    }

}
