package com.xiaomi.mone.log.manager.user;

import com.xiaomi.mone.log.manager.domain.IDMDept;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IdmMoneUserDetailServiceTest {
    private IdmMoneUserDetailService idmDept;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        idmDept = Ioc.ins().getBean(IdmMoneUserDetailService.class);
    }

    @Test
    public void queryDeptPersonIds() {
        List<String> mw6310 = idmDept.queryDeptPersonIds("MW6310");
    }

    @Test
    public void queryDeptPerson() {
        List<UseDetailInfo> mw6310 = idmDept.queryDeptPerson("MW6310");
        System.out.println(mw6310);
    }
}