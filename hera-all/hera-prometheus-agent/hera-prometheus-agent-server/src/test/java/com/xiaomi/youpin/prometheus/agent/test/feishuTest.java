package com.xiaomi.youpin.prometheus.agent.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.prometheus.agent.bootstrap.PrometheusAgentBootstrap;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.Alerts;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.CommonLabels;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.GroupLabels;
import com.xiaomi.youpin.prometheus.agent.service.FeishuService;
import com.xiaomi.youpin.prometheus.agent.service.alarmContact.FeishuAlertContact;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.RuleAlertService;
import com.xiaomi.youpin.prometheus.agent.util.DateUtil;
import com.xiaomi.youpin.prometheus.agent.util.FreeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author zhangxiaowei6
 */
@SpringBootTest(classes = PrometheusAgentBootstrap.class)
public class feishuTest {

    public static final Gson gson = new Gson();
    @Autowired
    FeishuAlertContact feishuAlertContact;

    String body = "{\n" +
            "    \"receiver\":\"web\\\\.hook\",\n" +
            "    \"status\":\"firing\",\n" +
            "    \"alerts\":[\n" +
            "        {\n" +
            "            \"status\":\"firing\",\n" +
            "            \"labels\":{\n" +
            "                \"alert_key\":\"k8s_container_cpu_use_rate\",\n" +
            "                \"alert_op\":\"\\u003e\",\n" +
            "                \"alert_value\":\"0.01\",\n" +
            "                \"alertname\":\"k8s_container_cpu_use_rate-2-k8s_container_cpu_use_rate-1678682933270\",\n" +
            "                \"app_iam_id\":\"null\",\n" +
            "                \"application\":\"2_hera_demo_client\",\n" +
            "                \"calert\":\"k8s容器机CPU使用率\",\n" +
            "                \"container\":\"hera-demo-client-container\",\n" +
            "                \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "                \"group_key\":\"localhost:5195\",\n" +
            "                \"image\":\"sha256:e3adf286245db8a00e28fa4a3b37d505e6f49efa8cdd5c7d05c3a73fbfc9501c\",\n" +
            "                \"instance\":\"localhost:5195\",\n" +
            "                \"ip\":\"localhost\",\n" +
            "                \"job\":\"mione-china-cadvisor-k8s\",\n" +
            "                \"name\":\"k8s_hera-demo-client-container_hera-demo-client-59446dd69f-kjdsv_hera-namespace_502ea675-c131-4379-9f3d-f0d7949a9967_0\",\n" +
            "                \"namespace\":\"hera-namespace\",\n" +
            "                \"pod\":\"hera-demo-client-59446dd69f-kjdsv\",\n" +
            "                \"project_id\":\"2\",\n" +
            "                \"project_name\":\"hera-demo-client\",\n" +
            "                \"restartCounts\":\"0\",\n" +
            "                \"send_interval\":\"5m\",\n" +
            "                \"serverEnv\":\"dev\",\n" +
            "                \"system\":\"mione\"\n" +
            "            },\n" +
            "            \"annotations\":{\n" +
            "                \"summary\":\"test\",\n" +
            "                \"title\":\"hera-demo-client\\u0026k8s容器机CPU使用率\"\n" +
            "            },\n" +
            "            \"startsAt\":\"2023-03-13T07:35:33.633Z\",\n" +
            "            \"endsAt\":\"0001-01-01T00:00:00Z\",\n" +
            "            \"generatorURL\":\"http://prometheus-74bb956ff4-ss7t9:9090/graph?g0.expr\\u003drate%28container_cpu_user_seconds_total%7Bapplication%3D%222_hera_demo_client%22%2Cimage%21%3D%22%22%2Csystem%3D%22mione%22%7D%5B1m%5D%29+%2A+100+%3E+0.009999999776482582\\u0026g0.tab\\u003d1\",\n" +
            "            \"fingerprint\":\"cbd34f56f02de7a0\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"groupLabels\":{\n" +
            "        \"alertname\":\"k8s_container_cpu_use_rate-2-k8s_container_cpu_use_rate-1678682933270\",\n" +
            "        \"group_key\":\"localhost:5195\"\n" +
            "    },\n" +
            "    \"commonLabels\":{\n" +
            "        \"alert_key\":\"k8s_container_cpu_use_rate\",\n" +
            "        \"alert_op\":\"\\u003e\",\n" +
            "        \"alert_value\":\"0.01\",\n" +
            "        \"alertname\":\"k8s_container_cpu_use_rate-2-k8s_container_cpu_use_rate-1678682933270\",\n" +
            "        \"app_iam_id\":\"null\",\n" +
            "        \"application\":\"2_hera_demo_client\",\n" +
            "        \"calert\":\"k8s容器机CPU使用率\",\n" +
            "        \"container\":\"hera-demo-client-container\",\n" +
            "        \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "        \"group_key\":\"localhost:5195\",\n" +
            "        \"image\":\"sha256:e3adf286245db8a00e28fa4a3b37d505e6f49efa8cdd5c7d05c3a73fbfc9501c\",\n" +
            "        \"instance\":\"localhost:5195\",\n" +
            "        \"ip\":\"localhost\",\n" +
            "        \"job\":\"mione-china-cadvisor-k8s\",\n" +
            "        \"name\":\"k8s_hera-demo-client-container_hera-demo-client-59446dd69f-kjdsv_hera-namespace_502ea675-c131-4379-9f3d-f0d7949a9967_0\",\n" +
            "        \"namespace\":\"hera-namespace\",\n" +
            "        \"pod\":\"hera-demo-client-59446dd69f-kjdsv\",\n" +
            "        \"project_id\":\"2\",\n" +
            "        \"project_name\":\"hera-demo-client\",\n" +
            "        \"restartCounts\":\"0\",\n" +
            "        \"send_interval\":\"5m\",\n" +
            "        \"serverEnv\":\"dev\",\n" +
            "        \"system\":\"mione\"\n" +
            "    },\n" +
            "    \"commonAnnotations\":{\n" +
            "        \"summary\":\"test\",\n" +
            "        \"title\":\"hera-demo-client\\u0026k8s容器机CPU使用率\"\n" +
            "    },\n" +
            "    \"externalURL\":\"http://localhost:30903\",\n" +
            "    \"version\":\"4\",\n" +
            "    \"groupKey\":\"{}/{send_interval\\u003d\\\"5m\\\"}:{alertname\\u003d\\\"k8s_container_cpu_use_rate-2-k8s_container_cpu_use_rate-1678682933270\\\", group_key\\u003d\\\"localhost:5195\\\"}\",\n" +
            "    \"truncatedAlerts\":0\n" +
            "}";

    @Test
    public void testAlertManagerSendfeishu() {
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        AlertManagerFireResult fireResult = gson.fromJson(body, AlertManagerFireResult.class);
        feishuAlertContact.Reach(fireResult);
    }
}
