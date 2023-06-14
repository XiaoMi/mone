package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.LogStructureEnum;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getConfigFromNanos;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/13 15:06
 */
@Slf4j
public class LogTailServiceImplTest {

    private LogTailServiceImpl logTailService;
    private Gson gson;

    @Before
    public void before() {
        getConfigFromNanos();
        Ioc.ins().init("com.xiaomi");
        logTailService = Ioc.ins().getBean(LogTailServiceImpl.class);
        gson = new Gson();
    }

    @Test
    public void deleteConfigRemoteTest() {
        Long spaceId = 2L;
        Long id = 284L;
        String motorRoomEn = "cn";
        LogStructureEnum logStructureEnum = LogStructureEnum.TAIL;
        logTailService.deleteConfigRemote(spaceId, id, motorRoomEn, logStructureEnum);
        log.info("deleteConfigRemoteTest");
    }
}
