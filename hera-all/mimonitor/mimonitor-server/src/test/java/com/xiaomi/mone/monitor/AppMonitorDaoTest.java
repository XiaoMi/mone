/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor;

import com.google.gson.Gson;
import com.xiaomi.mone.monitor.bootstrap.MiMonitorBootstrap;
import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustDao;
import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustRecordDao;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.model.*;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.helper.ProjectHelper;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@SpringBootTest(classes = MiMonitorBootstrap.class)
public class AppMonitorDaoTest {

    @Autowired
    AppMonitorDao appMonitorDao;

    @Autowired
    private ProjectHelper projectHelper;

    @Autowired
    AppCapacityAutoAdjustDao appCapacityAutoAdjustDao;

    @Autowired
    AppCapacityAutoAdjustRecordDao appCapacityAutoAdjustRecordDao;

    @Reference(registry = "registryConfig",check = false,version = "1.0",interfaceClass = UserOrgFacade.class,group="staging")
    UserOrgFacade iGatewayOpenApi;

    @Test
    public void testGetAllDeptUseHera(){
        Set<String> set = new HashSet<>();
        List<AppMonitor> allApps = appMonitorDao.getAllApps(1, 5000);
        allApps.forEach(t-> {
            if(StringUtils.isNotBlank(t.getOwner())){
                set.add(t.getOwner());
            }
            if(StringUtils.isNotBlank(t.getCareUser())){
                set.add(t.getCareUser());
            }
        });

        Set<String> depts = new HashSet<>();
        for(String account : set){
            NullParam param = new NullParam();
            param.setAccount(account);
            param.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            com.xiaomi.youpin.infra.rpc.Result<OrgInfoVo> orgByAccount = iGatewayOpenApi.getOrgByAccount(param);
            if(orgByAccount == null || orgByAccount.getData() == null){
                log.info("nodata found for account:{}",account);
            }else{

                String namePath = orgByAccount.getData().getNamePath();
                if(StringUtils.isNotBlank(namePath)){
                    String[] split = namePath.split("/");
                    if(split != null && split.length > 1){
                        depts.add(split[1]);
                    }
                }

            }

        }

        for(String dept : depts){
                System.out.println(dept);
        }

    }

    @Test
    public void testCapacityAdjustRecordQuery(){
        AppCapacityAutoAdjustRecord condition = new AppCapacityAutoAdjustRecord();
        condition.setNameSpace("test1");
        condition.setContainer("12-0-101");
        condition.setEnvId(1);
        List<AppCapacityAutoAdjustRecord> query = appCapacityAutoAdjustRecordDao.query(condition, 1, 1,false);
        if(CollectionUtils.isNotEmpty(query)){
            System.out.println(query.get(0).toString());
        }
    }

    @Test
    public void testInsertAppCapacityAutoAdjust(){
        AppCapacityAutoAdjust appCapacityAutoAdjust = new AppCapacityAutoAdjust();
        appCapacityAutoAdjust.setAppId(667);
        appCapacityAutoAdjust.setAutoCapacity(1);
        appCapacityAutoAdjust.setContainer("667_0_1");
        appCapacityAutoAdjust.setDependOn(1);
        appCapacityAutoAdjust.setMaxInstance(10);
        appCapacityAutoAdjust.setMinInstance(2);
        appCapacityAutoAdjust.setPipelineId(1);
        int i = appCapacityAutoAdjustDao.create(appCapacityAutoAdjust);
        System.out.println("has create record num : " + i);
    }

    @Test
    public void testSelectAppHealth(){
        AlarmHealthQuery query = new AlarmHealthQuery();
        query.setOwner("");
//        query.setProjectId(607551);
        query.setAppSource(0);
//        query.setAppName("mi");
        List<AlarmHealthResult> alarmHealthResults = appMonitorDao.selectAppHealth(query);
        System.out.println(new Gson().toJson(Result.success(alarmHealthResults)));
    }

    @Test
    public void accessLogSys() {
        Object obj = projectHelper.accessLogSys("zzytest", 667L,0);
        System.err.println(obj);
    }

    
    public void insert() {
        AppMonitor app = new AppMonitor();
        int result = appMonitorDao.create(app);
        System.err.println(result);
    }
    
    
    @Test
    public void getByAppId() {
        AppMonitor app = appMonitorDao.getByAppId(667);
        System.err.println(app);
    }

    @Test
    public void getMyAndCareAppList() {
        Object obj = appMonitorDao.getMyAndCareAppList("","test", 1, 2, true);
        System.err.println(obj);
    }
    
}
