package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LogTemplateServiceImplTest {
    private LogTemplateServiceImpl logTemplateService;

    @Before
    public void initFiled() {
        Ioc.ins().init("com.xiaomi");
        logTemplateService = Ioc.ins().getBean(LogTemplateServiceImpl.class);
    }

    @Test
    public void getLogTemplateList() {
        Result<List<LogTemplateDTO>> logTemplateList = logTemplateService.getLogTemplateList("cn");
        System.out.println(logTemplateList.getData());
    }

    @Test
    public void getLogTemplateById() {
        Result<LogTemplateDetailDTO> logtemplate = logTemplateService.getLogTemplateById(84);
        System.out.println(logtemplate);
    }

}