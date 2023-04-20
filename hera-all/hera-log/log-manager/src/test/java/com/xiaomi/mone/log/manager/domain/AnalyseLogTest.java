package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import com.xiaomi.mone.log.manager.model.vo.LogAnalyseDataQuery;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AnalyseLogTest {
    private AnalyseLog analyseLog;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        analyseLog = Ioc.ins().getBean(AnalyseLog.class);
    }

    @Test
    public void getData() throws IOException {
        LogAnalyseDataQuery query = new LogAnalyseDataQuery();
        query.setGraphId(1l);
        query.setStartTime(1661858725000l);
        query.setEndTime(1661862325000l);
        LogAnalyseDataDTO data = analyseLog.getData(query);
        System.out.println(data);
    }
}