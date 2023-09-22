package com.xiaomi.mone.monitor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bootstrap.MiMonitorBootstrap;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusAlertService;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/2/27 2:17 下午
 */
@Slf4j
@SpringBootTest(classes = MiMonitorBootstrap.class)
public class AlertTest {

    @Reference(registry = "registryConfig",check = false, interfaceClass = PrometheusAlertService.class,group="mistaging")
    PrometheusAlertService prometheusAlertService;

    @Test
    public void testAlertAdd(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cname", "gxh-zzytest-alert12");
        jsonObject.addProperty("alert", "alert-test1");
        jsonObject.addProperty("expr", "sum_over_time(staging_667_zzytest_jaeger_dubboConsumerError_total[95s])>0");
        jsonObject.addProperty("For", "30s");
        jsonObject.addProperty("enabled", 1);

        JsonObject jsonSummary = new JsonObject();
        jsonSummary.addProperty("summary", "test");
        jsonObject.add("annotations", jsonSummary);

        /**
         * rule-group
         */
        jsonObject.addProperty("group", "example");

        /**
         * priority
         */
        jsonObject.addProperty("priority", "P1");

        /**
         * env
         */
        JsonArray array = new JsonArray();
        array.add("staging");
        jsonObject.add("env", array);


        /**
         * labels
         */
        JsonObject labels = new JsonObject();
        labels.addProperty("send_interval","5m");
        labels.addProperty("app_iam_id","17465");
        labels.addProperty("project_id","667");
        labels.addProperty("project_name","zzytest");
        jsonObject.add("labels", labels);



        /**
         * alert Team
         */
        JsonObject jsonAlertTeam = new JsonObject();
        jsonAlertTeam.addProperty("id",5313);
        jsonAlertTeam.addProperty("type","oncall");
        jsonAlertTeam.addProperty("name","gxh-test");

        JsonArray array1 = new JsonArray();
        array1.add(jsonAlertTeam);

        jsonObject.add("alert_team", array1);

        List<String> alertMembersList = Lists.newArrayList("gaoxihui","dingtao");
        JsonArray alertMembers = new Gson().fromJson(JSON.toJSONString(alertMembersList), JsonArray.class);
        jsonObject.add("alert_member", array);

        List<String> alertAtMembersList = Lists.newArrayList("zhangxiaowei","dingtao");
        JsonArray atMembersArray = new Gson().fromJson(JSON.toJSONString(alertAtMembersList), JsonArray.class);
        jsonObject.add("alert_at_people", atMembersArray);

        RuleAlertParam ruleAlertParam = new Gson().fromJson(new Gson().toJson(jsonObject), RuleAlertParam.class);
        System.out.println("ruleAlertParam:==================" + new Gson().toJson(ruleAlertParam));
        com.xiaomi.youpin.prometheus.agent.result.Result ruleAlert = prometheusAlertService.createRuleAlert(ruleAlertParam);

        System.out.println("result:==================" + new Gson().toJson(ruleAlert));
    }

    @Test
    public void  testGetAalert(){
        com.xiaomi.youpin.prometheus.agent.result.Result result = prometheusAlertService.GetRuleAlert("26");
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testSearchAlert(){
        com.xiaomi.youpin.prometheus.agent.result.Result result = prometheusAlertService.GetRuleAlertList(10, 1);
        System.out.println("list === " + new Gson().toJson(result));
    }

    @Test
    public void testEnabledAlert(){
        com.xiaomi.youpin.prometheus.agent.result.Result result1 = prometheusAlertService.EnabledRuleAlert("26", "0");
        System.out.println("enabled alert result:"+new Gson().toJson(result1));
        com.xiaomi.youpin.prometheus.agent.result.Result result = prometheusAlertService.GetRuleAlert("26");
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testUpdateAlert(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cname", "gxh-zzytest-alert11Update");
        jsonObject.addProperty("alert", "alert-test1Update");
        jsonObject.addProperty("expr", "sum_over_time(staging_667_zzytest_jaeger_dubboConsumerError_total[95s])>10");
        jsonObject.addProperty("For", "300s");
        jsonObject.addProperty("enabled", 1);

        JsonObject jsonSummary = new JsonObject();
        jsonSummary.addProperty("summary", "test");
        jsonObject.add("annotations", jsonSummary);

        /**
         * rule-group
         */
        jsonObject.addProperty("group", "example");

        /**
         * priority
         */
        jsonObject.addProperty("priority", "P1");

        /**
         * env
         */
        JsonArray array = new JsonArray();
        array.add("staging");
        jsonObject.add("env", array);


        /**
         * labels
         */
        JsonObject labels = new JsonObject();
        labels.addProperty("send_interval","1m");
        labels.addProperty("app_iam_id","17465");
        labels.addProperty("project_id","667");
        labels.addProperty("project_name","zzytest");
        jsonObject.add("labels", labels);

        RuleAlertParam ruleAlertParam = new Gson().fromJson(new Gson().toJson(jsonObject), RuleAlertParam.class);

        com.xiaomi.youpin.prometheus.agent.result.Result result = prometheusAlertService.UpdateRuleAlert("26", ruleAlertParam);

        System.out.println(new Gson().toJson(result));

    }



}
