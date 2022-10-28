package com.xiaomi.youpin;

import com.xiaomi.mone.tpc.api.service.MetaDataFacade;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.FlagAddParam;
import com.xiaomi.mone.tpc.common.param.FlagQryParam;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.gwdash.bootstrap.GwDashBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GwDashBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Slf4j
public class MetaDataFacadeTest {
    @Reference(check = true, group = "staging", interfaceClass = MetaDataFacade.class, version = "1.0")
    private MetaDataFacade metaDataFacade;

    @Test
    public void CreateTest() {
        FlagAddParam param = new FlagAddParam();
        param.setParentId(1L);
        param.setFlagName("rikaaa0928");
        param.setDesc("test");
        param.setFlagKey("tenant");
        param.setFlagVal("2");
        param.setAccount("wangzhidong1");
        param.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        metaDataFacade.add(param);
    }
    @Test
    public void TestGet(){
        FlagQryParam qryParam = new FlagQryParam();
        qryParam.setParentId(1L);
        qryParam.setFlagName("rikaaa0928");
        qryParam.setAccount("wangzhidong1");
        qryParam.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        qryParam.setPager(false);
        qryParam.setPage(0);
        qryParam.setPageSize(10);
        PageDataVo<FlagVo> res = metaDataFacade.list(qryParam).getData();
        log.debug("{}", res);
        qryParam.setFlagKey("tenant");
        res = metaDataFacade.list(qryParam).getData();
        log.debug("{}", res);
    }
}
