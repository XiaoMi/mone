package com.xiaomi.mone.monitor;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.monitor.bootstrap.MiMonitorBootstrap;
import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustDao;
import com.xiaomi.mone.monitor.dao.GrafanaTemplateDao;
import com.xiaomi.mone.monitor.dao.HeraAppRoleDao;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
import com.xiaomi.mone.monitor.dao.model.HeraAppRole;
import com.xiaomi.mone.monitor.service.impl.AAImpl;
import com.xiaomi.mone.monitor.service.kubernetes.CapacityAdjustMessageService;
import com.xiaomi.mone.monitor.dao.model.*;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppGrafanaMappingService;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.ComputeTimerService;
import com.xiaomi.mone.monitor.service.HeraBaseInfoService;
import com.xiaomi.mone.monitor.service.http.MoneSpec;
import com.xiaomi.mone.monitor.service.http.RequestParam;
import com.xiaomi.mone.monitor.service.model.*;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author zhanggaofeng1
 */
@Slf4j
@SpringBootTest(classes = MiMonitorBootstrap.class)
public class AppMonitorServiceTest {

    @Autowired
    HeraAppRoleDao heraAppRoleDao;

    @Autowired
    AppMonitorService appMonitorService;

    @Autowired
    ComputeTimerService computeTimerService;

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Autowired
    GrafanaTemplateDao grafanaTemplateDao;

    @Autowired
    CapacityAdjustMessageService capacityAdjustMessageService;

    @Autowired
    AppCapacityAutoAdjustDao appCapacityAutoAdjustDao;

    @Test
    public void test11111(){
        AppCapacityAutoAdjust autoQuery = new AppCapacityAutoAdjust();
        autoQuery.setStatus(0);         //0表示开启状态
        autoQuery.setAutoCapacity(1);  //开启自动扩容的
        List<AppCapacityAutoAdjust> result = appCapacityAutoAdjustDao.query(autoQuery, null, null);
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testCapacityMessage(){
        MoneSpec moneSpec = new MoneSpec();
        moneSpec.setReplicas(10);
        moneSpec.setSetReplicas(11);
        capacityAdjustMessageService.product(moneSpec);
    }
    public void testEnvMapping(){
        String staging = heraBaseInfoService.getArea("663", 0,"staging");
        System.out.println(staging);
    }

    @Test
    public void testCapacityK8s(){
        RequestParam param = new RequestParam();
        MoneSpec moneSpec = new MoneSpec();
        moneSpec.setReplicas(1);
        moneSpec.setSetReplicas(2);
        moneSpec.setEnvID(1);
        moneSpec.setNamespace("test1");
        moneSpec.setContainer("12-0-101");
        moneSpec.init();
        param.setMoneSpec(moneSpec);
        param.init(11);
//        String s = capacityService.capacityAdjust(param);
//        System.out.println("result===" + s);
    }

    @Test
    public void testComputeScore(){
        AlarmHealthQuery query = new AlarmHealthQuery();
        query.setOwner("");
        query.setAppSource(0);
        Result result = appMonitorService.selectAppAlarmHealth(query);
        System.out.println(new Gson().toJson(result));
    }


    @Test
    public void testGetAppMembers(){
        Result members = heraBaseInfoService.getAppMembersByAppId("667", 0, "");
        System.out.println(new Gson().toJson(members));
    }


    @Test
    public void testQueryHeraBaseByPlatType() {

        HeraAppRole heraAppRole = new HeraAppRole();
        heraAppRole.setAppId("601");
        List<HeraAppRole> query = heraAppRoleDao.query(heraAppRole, 0, 5);
        System.out.println(query.stream().map(t->t.getUser()).collect(Collectors.toList()));
    }

    @Test
    public void testGrafanaCreate(){

        HeraAppBaseInfo baseInfo = new HeraAppBaseInfo();
        baseInfo.setBindId("");   //
        baseInfo.setAppName("");   //
        baseInfo.setAppType(0);   //
        baseInfo.setPlatformType(0);  //
        baseInfo.setAppLanguage("java");
        appGrafanaMappingService.createTmpByAppBaseInfo(baseInfo);
    }

    @Test
    public void testGrafanaTemplate(){
        GrafanaTemplate template = new GrafanaTemplate();
        template.setLanguage(0);
        template.setPlatform(1);
        template.setAppType(0);
        List<GrafanaTemplate> search = grafanaTemplateDao.search(template);
        System.out.println(new Gson().toJson(search));
    }



    @Test
    public void testHeraBaseInfo(){
        HeraAppBaseQuery query = new HeraAppBaseQuery();
        query.setParticipant("");
//        query.setAppName("zzy");
//        query.setPlatformType(1);
    }

    public static void main(String[] args) {
        HeraAppBaseQuery query = new HeraAppBaseQuery();
        query.setAppName("zzy");
        System.out.println(new Gson().toJson(query));
    }


    @Test
    public void testHeraAppRoleDao(){
        HeraAppRole role = new HeraAppRole();
        role.setAppId("110");
        role.setAppPlatform(1);
        role.setUser("gxhtest");
        role.setStatus(1);
        role.setRole(0);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        int i = heraAppRoleDao.create(role);

        System.out.println("insert hera app role result:"+i);

        role.setRole(1);
        heraAppRoleDao.update(role);
        System.out.println("update hera app role result:"+i);
    }

    @Test
    public void testExecutor(){
//        appGrafanaMappingService.exeReloadTemplate(100);
    }

    public void testDelAppByUser(){
        Result<String> result = appMonitorService.deleteByUser(30601, 2, "");
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void listApp() throws IOException {

        Object data = appMonitorService.listApp(null, "", 1, 10);
        log.info("result : {}", new Gson().toJson(data));
    }

    @Test
    public void listAppDistinct() throws IOException {

        Object data = appMonitorService.listAppDistinct("", null,1, 10);
        log.info("result : {}", new Gson().toJson(data));
    }


    @Test
    public void create() {
        AppMonitor app = new AppMonitor();
        app.setOwner("");
        app.setProjectName("zzytest");
        app.setProjectId(667);
        app.setIamTreeId(17533);
        Result<String> stringResult = appMonitorService.create(app);
    }

    @Test
    public void createWithBaseInfo() {
        AppMonitorModel app = new AppMonitorModel();

        app.setOwner("");
        app.setProjectName("gxh-testx04");
        app.setProjectCName("gxh-testx04");
        app.setProjectId(0);
        app.setIamTreeId(0);
        app.setAppSource(0);
        app.setAppLanguage("java");
        app.setAppType(0);
        app.setBindType(1);
        app.setEnvMapping("");
        Result<String> stringResult = appMonitorService.createWithBaseInfo(app,"");
        System.out.println(new Gson().toJson(stringResult));
    }


}
