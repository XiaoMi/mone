package com.xiaomi.youpin.prometheus.agent.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import org.junit.Test;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.Alerts;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.CommonLabels;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.GroupLabels;
import com.xiaomi.youpin.prometheus.agent.service.FeishuService;
import com.xiaomi.youpin.prometheus.agent.util.DateUtil;
import com.xiaomi.youpin.prometheus.agent.util.FreeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class feishuTest {
    public static final Gson gson = new Gson();
    String body = "{\n" +
            "    \"receiver\":\"web\\\\.hook\",\n" +
            "    \"status\":\"firing\",\n" +
            "    \"alerts\":[\n" +
            "        {\n" +
            "            \"status\":\"firing\",\n" +
            "            \"labels\":{\n" +
            "                \"alert_key\":\"http_error_times\",\n" +
            "                \"alert_op\":\"\\u003e\",\n" +
            "                \"alert_value\":\"0.01\",\n" +
            "                \"alertname\":\"http_error_times-2-http_error_times-1682215881863\",\n" +
            "                \"app_iam_id\":\"null\",\n" +
            "                \"application\":\"2_hera_demo_client\",\n" +
            "                \"calert\":\"Http异常数\",\n" +
            "                \"errorCode\":\"500\",\n" +
            "                \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "                \"group_key\":\"2_hera_demo_client_/testError\",\n" +
            "                \"methodName\":\"/testError\",\n" +
            "                \"metrics\":\"httpError\",\n" +
            "                \"metrics_flag\":\"1\",\n" +
            "                \"project_id\":\"2\",\n" +
            "                \"project_name\":\"hera-demo-client\",\n" +
            "                \"send_interval\":\"5m\",\n" +
            "                \"serverEnv\":\"dev\",\n" +
            "                \"serverIp\":\"localhost\",\n" +
            "                \"system\":\"mione\"\n" +
            "            },\n" +
            "            \"annotations\":{\n" +
            "                \"summary\":\"接口类报警测试\",\n" +
            "                \"title\":\"hera-demo-client\\u0026Http异常数\"\n" +
            "            },\n" +
            "            \"startsAt\":\"2023-04-23T02:11:33.633Z\",\n" +
            "            \"endsAt\":\"0001-01-01T00:00:00Z\",\n" +
            "            \"generatorURL\":\"http://prometheus-78c68b6c66-l6brh:9090/graph?g0.expr\\u003dsum+by%28application%2C+system%2C+serverIp%2C+serviceName%2C+methodName%2C+sqlMethod%2C+errorCode%2C+service%2C+serverEnv%2C+sql%2C+dataSource%2C+functionModule%2C+functionName%29+%28sum_over_time%28staging_hera_httpError_total%7Bapplication%3D%222_hera_demo_client%22%2CserverEnv%3D~%22dev%22%7D%5B30s%5D%29%29+%3E+0.01\\u0026g0.tab\\u003d1\",\n" +
            "            \"fingerprint\":\"4523264a2f45c139\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"groupLabels\":{\n" +
            "        \"alertname\":\"http_error_times-2-http_error_times-1682215881863\",\n" +
            "        \"group_key\":\"2_hera_demo_client_/testError\"\n" +
            "    },\n" +
            "    \"commonLabels\":{\n" +
            "        \"alert_key\":\"http_error_times\",\n" +
            "        \"alert_op\":\"\\u003e\",\n" +
            "        \"alert_value\":\"0.01\",\n" +
            "        \"alertname\":\"http_error_times-2-http_error_times-1682215881863\",\n" +
            "        \"app_iam_id\":\"null\",\n" +
            "        \"application\":\"2_hera_demo_client\",\n" +
            "        \"calert\":\"Http异常数\",\n" +
            "        \"errorCode\":\"500\",\n" +
            "        \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "        \"group_key\":\"2_hera_demo_client_/testError\",\n" +
            "        \"methodName\":\"/testError\",\n" +
            "        \"metrics\":\"httpError\",\n" +
            "        \"metrics_flag\":\"1\",\n" +
            "        \"project_id\":\"2\",\n" +
            "        \"project_name\":\"hera-demo-client\",\n" +
            "        \"send_interval\":\"5m\",\n" +
            "        \"serverEnv\":\"dev\",\n" +
            "        \"serverIp\":\"localhost\",\n" +
            "        \"system\":\"mione\"\n" +
            "    },\n" +
            "    \"commonAnnotations\":{\n" +
            "        \"summary\":\"接口类报警测试\",\n" +
            "        \"title\":\"hera-demo-client\\u0026Http异常数\"\n" +
            "    },\n" +
            "    \"externalURL\":\"http://localhost:30903\",\n" +
            "    \"version\":\"4\",\n" +
            "    \"groupKey\":\"{}/{send_interval\\u003d\\\"5m\\\"}:{alertname\\u003d\\\"http_error_times-2-http_error_times-1682215881863\\\", group_key\\u003d\\\"2_hera_demo_client_/testError\\\"}\",\n" +
            "    \"truncatedAlerts\":0\n" +
            "}";

    @Test
    public void testFeishu() {
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        AlertManagerFireResult fireResult = gson.fromJson(body, AlertManagerFireResult.class);
        List<Alerts> alerts = fireResult.getAlerts();
        GroupLabels groupLabels = fireResult.getGroupLabels();
        String alertName = groupLabels.getAlertname();
        //查表看负责人
        String[] principals = {"zhangxiaowei6"};
        fireResult.getAlerts().stream().forEach(alert -> {
            int priority = 0;
            Map<String, Object> map = new HashMap<>();
            map.put("priority", "P" + String.valueOf(priority));
            map.put("title", fireResult.getCommonAnnotations().getTitle());
            map.put("alert_op", alert.getLabels().getAlert_op());
            map.put("alert_value", alert.getLabels().getAlert_value());
            map.put("application", alert.getLabels().getApplication());
            map.put("silence_url", "http:1123");
            //根据类别区分基础类、接口类、自定义类
            String serviceName = fireResult.getCommonLabels().getServiceName();
            String methodName = fireResult.getCommonLabels().getMethodName();
            CommonLabels commonLabels = fireResult.getCommonLabels();
            try {
                Class clazz = commonLabels.getClass();
                Field[] fields = clazz.getDeclaredFields();
                StringBuilder sb = new StringBuilder();
                for (Field field : fields) {
                    field.setAccessible(true); // 设置访问权限
                    String fieldName = field.getName();
                    if ("priority".equals(fieldName) || "title".equals(fieldName) || "alert_op".equals(fieldName) || "alert_value".equals(fieldName) || "application".equals(fieldName)) {
                        continue;
                    }
                    Object fieldValue = null;
                    try {
                        //将fieldValue转成String
                        fieldValue = field.get(commonLabels); // 获取字段值
                        if (fieldValue == null) {
                            continue;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    sb.append("**").append(fieldName).append("**").append(": ").append(fieldValue).append("\n");
                }
               String content = sb.toString();
                map.put("content", content);
                String freeMarkerRes = FreeMarkerUtil.getContent("/feishu", "feishuCart.ftl", map);
                System.out.println(freeMarkerRes);
                //feishuService.sendFeishu(content, principals, null, true);
            } catch (Exception e) {
            }
        });
    }
}
