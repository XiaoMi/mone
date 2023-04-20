package com.xiaomi.mone.log.manager;

import com.xiaomi.mone.log.manager.common.helper.MilogAccessHelper;
import com.xiaomi.mone.log.manager.model.bo.AccessMilogParam;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/25 15:22
 */
@Slf4j
public class MilogAccessHelperTest {

    @Test
    public void test1() {
        Ioc.ins().init("com.xiaomi");
        MilogAccessHelper milogAccessHelper = Ioc.ins().getBean(MilogAccessHelper.class);
        AccessMilogParam milogParam = new AccessMilogParam();
        log.info("result:{}", milogAccessHelper.validParam(milogParam));
    }

}
