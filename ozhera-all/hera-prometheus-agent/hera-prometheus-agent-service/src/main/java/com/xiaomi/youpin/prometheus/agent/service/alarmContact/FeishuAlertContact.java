package com.xiaomi.youpin.prometheus.agent.service.alarmContact;

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

/**
 * @author zhangxiaowei6
 */
@Slf4j
@Component
public class FeishuAlertContact extends BaseAlertContact {

    @Autowired
    RuleAlertDao dao;

    @NacosValue(value = "${hera.alertmanager.url}", autoRefreshed = true)
    private String silenceUrl;

    @Autowired
    private FeishuService feishuService;

    @Override
    public void Reach(AlertManagerFireResult fireResult) {
        List<Alerts> alerts = fireResult.getAlerts();
        GroupLabels groupLabels = fireResult.getGroupLabels();
        String alertName = groupLabels.getAlertname();
        log.info("SendAlert feishuReach begin send AlertName :{}", alertName);

        fireResult.getAlerts().stream().forEach(alert -> {
            try {
                // query responsible person
                String[] principals = dao.GetRuleAlertAtPeople(alertName);
                if (principals == null) {
                    log.info("SendAlert principals null alertName:{}", alertName);
                    return;
                }
                RuleAlertEntity ruleAlertEntity = dao.GetRuleAlertByAlertName(alert.getLabels().getAlertname());
                int priority = ruleAlertEntity.getPriority();
                Map<String, Object> map = new HashMap<>();
                map.put("priority", "P" + String.valueOf(priority));
                map.put("title", fireResult.getCommonAnnotations().getTitle());
                String alertOp = alert.getLabels().getAlert_op();
                String alertValue = alert.getLabels().getAlert_value();
                if (alertOp == null || alertOp.isEmpty()) {
                    alertOp = "";
                    alertValue = "";
                }
                map.put("alert_op", alertOp);
                map.put("alert_value", alertValue);
                map.put("application", alert.getLabels().getApplication());
                map.put("silence_url", silenceUrl);
                CommonLabels commonLabels = fireResult.getCommonLabels();
                Class clazz = commonLabels.getClass();
                Field[] fields = clazz.getDeclaredFields();
                StringBuilder sb = new StringBuilder();
                for (Field field : fields) {
                    // set access rights
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object fieldValue = null;
                    try {
                        // convert fieldValue to String
                        fieldValue = field.get(commonLabels); // 获取字段值
                        if (fieldValue == null) {
                            continue;
                        }
                        map.put(fieldName, field.get(commonLabels));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, Object> finalMap = transferNames(map);
                filterName(finalMap);
                finalMap.forEach(
                        (k, v) -> {
                            sb.append("**").append(k).append("**").append(": ").append(v).append("\n");
                        });

                String content = sb.toString();
                finalMap.put("content", content);
                String freeMarkerRes = FreeMarkerUtil.getContent("/feishu", "feishuCart.ftl", finalMap);

                feishuService.sendFeishu(freeMarkerRes, principals, null, true);
            } catch (Exception e) {
                log.error("SendAlert.feishuReach error:{}", e);
            }
        });

        log.info("SendAlert success AlertName:{}", alertName);
    }
}
