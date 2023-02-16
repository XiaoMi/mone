package com.xiaomi.mone.log.manager.test;

import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.CreateOrUpdateSpaceCmd;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.dto.MilogAppEnvDTO;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogConfigNacosServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/19 13:04
 */
@Slf4j
public class MilogConfigServiceTest {

    @Test
    public void testCreateNameSpace() {
        Ioc.ins().init("com.xiaomi");
        LogSpaceServiceImpl milogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
        CreateOrUpdateSpaceCmd ms = new CreateOrUpdateSpaceCmd();
        ms.setSpaceName("上山打老虎楼");
        ms.setDescription("我是测试人员啊");
        milogSpaceService.newMilogSpace(ms);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDelNameSpace() {
        Ioc.ins().init("com.xiaomi");
        LogSpaceServiceImpl milogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
        milogSpaceService.deleteMilogSpace(2L);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testCreateLogTail() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl milogSpaceService = Ioc.ins().getBean(LogTailServiceImpl.class);
        MilogLogtailParam param = new MilogLogtailParam();
        param.setSpaceId(37L);
        param.setStoreId(1L);
        param.setParseType(1);
        param.setParseScript("|");
        param.setLogPath("/home/work/log/xxx/server.log");
        param.setValueList("0,1,2,3,4,5,6");
        milogSpaceService.newMilogLogTail(param);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDelLogTail() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl milogSpaceService = Ioc.ins().getBean(LogTailServiceImpl.class);
        milogSpaceService.deleteMilogLogTail(1L);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDubbo() {
        Ioc.ins().init("com.xiaomi");
        MilogConfigNacosServiceImpl service = Ioc.ins().getBean(MilogConfigNacosServiceImpl.class);
        log.info("机器信息:{}");
    }

    @Test
    public void testIp() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl service = Ioc.ins().getBean(LogTailServiceImpl.class);
        Result<List<MilogAppEnvDTO>> query = service.getEnInfosByAppId(305L, 1);
        log.info("机器信息:{}", new Gson().toJson(query.getData()));
        Assert.assertNotNull(query);
    }
}
