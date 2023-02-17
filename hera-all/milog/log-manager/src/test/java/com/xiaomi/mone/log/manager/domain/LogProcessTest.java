package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LogProcessTest {
    private LogProcess logProcess;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        logProcess = Ioc.ins().getBean(LogProcess.class);
    }

    @Test
    public void updateAndGetLogProcess() {
        for (int j = 0; j < 3; j++) {
            String ip = "127.0.0.1." + j;
            UpdateLogProcessCmd cmd = new UpdateLogProcessCmd();
            List<UpdateLogProcessCmd.CollectDetail> collectDetailList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                UpdateLogProcessCmd.CollectDetail detail = new UpdateLogProcessCmd.CollectDetail();
                detail.setAppId(i * 1l);
                detail.setAppName("appName");
//                detail.setCollectTime(System.currentTimeMillis());
//                detail.setPointer(i * 10l);
//                detail.setPath("home/so/server" + i + ".log");
//                detail.setFileRowNumber( i * 10l);
//                detail.setPattern("/");
//                collectDetailList.add(detail);
            }
            cmd.setIp(ip);
            cmd.setCollectList(collectDetailList);
            logProcess.updateLogProcess(cmd);
        }
        logProcess.getAgentLogProcess("127.0.0.1").forEach(System.out::println);
        System.out.println("======================");
        logProcess.getAgentLogProcess("127.0.0.1").forEach(System.out::println);
        System.out.println("======================");
        logProcess.getAgentLogProcess("127.0.0.1").forEach(System.out::println);
    }
}