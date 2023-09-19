package com.xiaomi.youpin.prometheus.agent.test;

import com.google.gson.Gson;
import com.xiaomi.youpin.feishu.FeiShu;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.Alerts;
import com.xiaomi.youpin.prometheus.agent.util.DateUtil;
import com.xiaomi.youpin.prometheus.agent.util.FreeMarkerUtil;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class feishuCartTest {

    public static final Gson gson = new Gson();

    public String testInterfaceAlert = "{\n" +
            "    \"receiver\":\"web\\\\.hook\",\n" +
            "    \"status\":\"firing\",\n" +
            "    \"alerts\":[\n" +
            "        {\n" +
            "            \"status\":\"firing\",\n" +
            "            \"labels\":{\n" +
            "                \"alert_key\":\"dubbo_slow_query\",\n" +
            "                \"alert_op\":\"\\u003e\",\n" +
            "                \"alert_value\":\"0.01\",\n" +
            "                \"alertname\":\"dubbo_slow_query-2-dubbo_slow_query-1678418640443\",\n" +
            "                \"app_iam_id\":\"null\",\n" +
            "                \"application\":\"2_hera_demo_client\",\n" +
            "                \"calert\":\"DubboConsumer慢查询数\",\n" +
            "                \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "                \"group_key\":\"2_hera_demo_client_health\",\n" +
            "                \"methodName\":\"health\",\n" +
            "                \"metrics\":\"dubboConsumerSlowQuery\",\n" +
            "                \"metrics_flag\":\"2\",\n" +
            "                \"project_id\":\"2\",\n" +
            "                \"project_name\":\"hera-demo-client\",\n" +
            "                \"send_interval\":\"5m\",\n" +
            "                \"serverEnv\":\"dev\",\n" +
            "                \"serverIp\":\"localhost\",\n" +
            "                \"serviceName\":\"com.xiaomi.youpin.zxw_test2.api.service.DubboHealthService\",\n" +
            "                \"system\":\"mione\"\n" +
            "            },\n" +
            "            \"annotations\":{\n" +
            "                \"summary\":\"test\",\n" +
            "                \"title\":\"hera-demo-client\\u0026DubboConsumer慢查询数\"\n" +
            "            },\n" +
            "            \"startsAt\":\"2023-03-10T06:56:48.633Z\",\n" +
            "            \"endsAt\":\"0001-01-01T00:00:00Z\",\n" +
            "            \"generatorURL\":\"http://prometheus-77575dcccc-klj72:9090/graph?g0.expr\\u003dsum+by%28application%2C+system%2C+serverIp%2C+serviceName%2C+methodName%2C+sqlMethod%2C+errorCode%2C+service%2C+serverEnv%2C+functionModule%2C+functionName%29+%28sum_over_time%28staging_hera_dubboConsumerSlowQuery_total%7Bapplication%3D%222_hera_demo_client%22%7D%5B30s%5D%29%29+%3E+0.01\\u0026g0.tab\\u003d1\",\n" +
            "            \"fingerprint\":\"44675f6c50a64f5c\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"groupLabels\":{\n" +
            "        \"alertname\":\"dubbo_slow_query-2-dubbo_slow_query-1678418640443\"\n" +
            "    },\n" +
            "    \"commonLabels\":{\n" +
            "        \"alert_key\":\"dubbo_slow_query\",\n" +
            "        \"alert_op\":\"\\u003e\",\n" +
            "        \"alert_value\":\"0.01\",\n" +
            "        \"alertname\":\"dubbo_slow_query-2-dubbo_slow_query-1678418640443\",\n" +
            "        \"app_iam_id\":\"null\",\n" +
            "        \"application\":\"2_hera_demo_client\",\n" +
            "        \"calert\":\"DubboConsumer慢查询数\",\n" +
            "        \"exceptViewLables\":\"detailRedirectUrl.paramType\",\n" +
            "        \"group_key\":\"2_hera_demo_client_health\",\n" +
            "        \"methodName\":\"health\",\n" +
            "        \"metrics\":\"dubboConsumerSlowQuery\",\n" +
            "        \"metrics_flag\":\"2\",\n" +
            "        \"project_id\":\"2\",\n" +
            "        \"project_name\":\"hera-demo-client\",\n" +
            "        \"send_interval\":\"5m\",\n" +
            "        \"serverEnv\":\"dev\",\n" +
            "        \"serverIp\":\"localhost\",\n" +
            "        \"serviceName\":\"com.xiaomi.youpin.zxw_test2.api.service.DubboHealthService\",\n" +
            "        \"system\":\"mione\"\n" +
            "    },\n" +
            "    \"commonAnnotations\":{\n" +
            "        \"summary\":\"test\",\n" +
            "        \"title\":\"hera-demo-client\\u0026DubboConsumer慢查询数\"\n" +
            "    },\n" +
            "    \"externalURL\":\"http://alertmanager-c9cf6b74b-tqlcb:9093\",\n" +
            "    \"version\":\"4\",\n" +
            "    \"groupKey\":\"{}:{alertname\\u003d\\\"dubbo_slow_query-2-dubbo_slow_query-1678418640443\\\"}\",\n" +
            "    \"truncatedAlerts\":0\n" +
            "}";

    public String testBasicAlert = "{\n" +
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
    public void testFeishuInterfaceAlertCard() {
        AlertManagerFireResult alertManagerFireResult = gson.fromJson(testInterfaceAlert, AlertManagerFireResult.class);
        List<Alerts> alerts = alertManagerFireResult.getAlerts();
        alertManagerFireResult.getAlerts().stream().forEach(alert -> {
            System.out.println(DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
            //ai:Convert UTC time to yyyy-mm-dd format using Java code.
            Map<String, Object> map = new HashMap<>();
            map.put("title", alertManagerFireResult.getCommonAnnotations().getTitle());
            map.put("priority", "p0");
            map.put("alert_op", alert.getLabels().getAlert_op());
            map.put("alert_value", alert.getLabels().getAlert_value());
            map.put("application", alert.getLabels().getApplication());
            map.put("ip", alert.getLabels().getServerIp());
            map.put("start_time", DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
            map.put("silence_url", "http://localhost:80");
            map.put("serviceName", alert.getLabels().getServiceName());
            map.put("methodName", alert.getLabels().getMethodName());
            try {
                String content = FreeMarkerUtil.getContent("/feishu", "feishuInterfalCart.ftl", map);
                System.out.println(content);
                FeiShu feiShu = new FeiShu("xxx", "xxx");
                feiShu.sendCardByEmail("xxx", content);
            } catch (Exception e) {

            }
        });
    }

    @Test
    public void testFeishuBasicAlertCard() {
        AlertManagerFireResult alertManagerFireResult = gson.fromJson(testBasicAlert, AlertManagerFireResult.class);
        List<Alerts> alerts = alertManagerFireResult.getAlerts();
        alertManagerFireResult.getAlerts().stream().forEach(alert -> {
            System.out.println(DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
            //ai:Convert UTC time to yyyy-mm-dd format using Java code.
            Map<String, Object> map = new HashMap<>();
            map.put("title", alertManagerFireResult.getCommonAnnotations().getTitle());
            map.put("priority", "p0");
            map.put("alert_op", alert.getLabels().getAlert_op());
            map.put("alert_value", alert.getLabels().getAlert_value());
            map.put("application", alert.getLabels().getApplication());
            map.put("ip", alert.getLabels().getIp());
            map.put("start_time", DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
            map.put("silence_url", "http://localhost:80");
            map.put("pod", alert.getLabels().getPod());
            try {
                String content = FreeMarkerUtil.getContent("/feishu", "feishuBasicCart.ftl", map);
                System.out.println(content);
                FeiShu feiShu = new FeiShu("xxxx", "xxx");
                feiShu.sendCardByEmail("xxx@xiaomi.com", content);
            } catch (Exception e) {

            }
        });
    }
}
