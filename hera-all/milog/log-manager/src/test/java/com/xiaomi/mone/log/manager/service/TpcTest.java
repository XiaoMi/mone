package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/2/13 15:50
 */
@Slf4j
public class TpcTest {
    private Tpc tpc;

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        tpc = Ioc.ins().getBean(Tpc.class);
        //模拟用户登陆
        AuthUserVo authUserVo = new AuthUserVo();
        authUserVo.setAccount("zhangsan29");
        authUserVo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        MoneUserContext.setCurrentUser(authUserVo);
    }

    @Test
    public void testHandleRemoteTpcId() {
        tpc.handleRemoteTpcId("logger");
    }

}
