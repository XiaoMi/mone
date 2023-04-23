package com.xiaomi.mone.monitor;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.xiaomi.mone.monitor.bootstrap.MiMonitorBootstrap;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppAlarmStrategyDao;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.service.AlarmStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest(classes = MiMonitorBootstrap.class)
public class AppAlarmStrategyDaoImplTest {

    @Autowired
    private AppAlarmStrategyDao appAlarmStrategyDao;
    @Autowired
    private AppAlarmRuleDao appAlarmRuleDao;
    @Autowired
    private AlarmStrategyService alarmStrategyService;

    @Test
    public void insert() {
        AlarmStrategy strategy = new AlarmStrategy();
        strategy.setAppId(1);
        strategy.setAppName("test");
        strategy.setCreater("zgf");
        strategy.setDesc("this is a test");
        strategy.setStrategyName("test");
        strategy.setStrategyType(1);
        strategy.setGroup3("test3");
        strategy.setGroup4("test4");
        strategy.setGroup5("test5");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("includeEnvs","测试1,测试2");
        jsonObject.addProperty("exceptEnvs","测试3,测试4");
        strategy.setEnvs(jsonObject.toString());

        boolean result = appAlarmStrategyDao.insert(strategy);
        System.err.println(result);
    }


    @Test
    public void updateById() {
        AlarmStrategy strategy = new AlarmStrategy();
        strategy.setAppId(2);
        strategy.setAppName("test1");
        strategy.setCreater("zgf1");
        strategy.setDesc("this is a test1");
        strategy.setStrategyType(2);
        strategy.setId(3);
        boolean result = appAlarmStrategyDao.updateById(strategy);
        System.err.println(result);
    }

    @Test
    public void searchByCond() {
        AlarmStrategy strategy = new AlarmStrategy();
        strategy.setCreater("");
        strategy.setAppName("tpc");
        strategy.setStrategyName("zgf");
        Object result = appAlarmStrategyDao.searchByCond("",true,strategy, 1, 30,null,null);
        System.err.println(new Gson().toJson(result));
    }


    @Test
    public void selectByStrategyId() {
        List<AppAlarmRule> list = new ArrayList<>();
        AppAlarmRule rule = new AppAlarmRule();
        rule.setStrategyId(1);
        rule.setAlarmId(1);
        rule.setAlert("t");
        rule.setAlertTeam("{}");
        rule.setAnnotations("{}");
        rule.setCname("test");
        rule.setCreater("zgf");
        rule.setCreateTime(new Date());
        rule.setDataCount(1);
        rule.setEnv("env");
        rule.setExpr("expr");
        rule.setForTime("30s");
        rule.setIamId(1);
        rule.setLabels("");
        rule.setMetricType(1);
        rule.setOp("=");
        rule.setPriority("1");
        rule.setProjectId(1);
        rule.setRemark("test");
        rule.setRuleGroup("test");
        rule.setRuleStatus(1);
        rule.setRuleType(1);
        rule.setSendInterval("1");
        rule.setStatus(1);
        rule.setTemplateId(1);
        rule.setUpdateTime(new Date());
        rule.setValue(2f);
        list.add(rule);
        int result = appAlarmRuleDao.delete(rule);
        System.err.println(list);
    }

}
