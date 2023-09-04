package com.xiaomi.mone.monitor;

import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.bootstrap.MiMonitorBootstrap;
import com.xiaomi.mone.monitor.dao.AlertGroupDao;
import com.xiaomi.mone.monitor.dao.model.AlertGroup;
import com.xiaomi.mone.monitor.dao.model.AlertGroupMember;
import com.xiaomi.mone.monitor.service.AlertGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/2/16 11:41
 */
@Slf4j
@SpringBootTest(classes = MiMonitorBootstrap.class)
public class AlertGroupServiceTest {

    @Autowired
    private AlertGroupService alertGroupService;

    @Test
    public void init() {
        alertGroupService.initAlertGroupData();
    }

    @Test
    public void sync() {
        Object obj = alertGroupService.sync("", "alert");
        System.out.println(obj);
    }

    @Test
    public void alertGroupCreate() {
        List<Long> memberIds = new ArrayList<>();
        memberIds.add(13199L);
        memberIds.add(12147L);
        AlertGroupParam param = new AlertGroupParam();
        param.setName("单元测试1");
        param.setNote("单元测试1");
        param.setMemberIds(memberIds);
       Object obj = alertGroupService.alertGroupCreate("", param);
       System.out.println(obj);
    }

    @Test
    public void alertGroupEdit() {
        List<Long> memberIds = new ArrayList<>();
        memberIds.add(13199L);
        memberIds.add(12147L);
        AlertGroupParam param = new AlertGroupParam();
        param.setId(6L);
        param.setName("单元测试2");
        param.setNote("单元测试2");
        param.setMemberIds(memberIds);
        Object obj = alertGroupService.alertGroupEdit("", param);
        System.out.println(obj);
    }

    @Test
    public void alertGroupDelete() {
        AlertGroupParam param = new AlertGroupParam();
        param.setId(6L);
        Object obj = alertGroupService.alertGroupDelete("", param);
        System.out.println(obj);
    }

    @Test
    public void alertGroupSearch() {
        AlertGroupParam param = new AlertGroupParam();
        param.pageQryInit();
        Object obj = alertGroupService.alertGroupSearch("", param);
        System.out.println(obj);
    }

}
