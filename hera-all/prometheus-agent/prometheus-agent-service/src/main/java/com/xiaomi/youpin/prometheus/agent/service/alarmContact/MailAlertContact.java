package com.xiaomi.youpin.prometheus.agent.service.alarmContact;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.Alerts;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.GroupLabels;
import com.xiaomi.youpin.prometheus.agent.util.DateUtil;
import com.xiaomi.youpin.prometheus.agent.util.FreeMarkerUtil;
import com.xiaomi.youpin.prometheus.agent.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zhangxiaowei6
 */
@Slf4j
@Component
public class MailAlertContact extends BaseAlertContact {
    @Autowired
    RuleAlertDao dao;

    @Autowired
    private MailUtil mailUtil;

    @NacosValue(value = "${hera.alertmanager.url}", autoRefreshed = true)
    private String silenceUrl;

    @Override
    public void Reach(AlertManagerFireResult fireResult) {
        List<Alerts> alerts = fireResult.getAlerts();
        GroupLabels groupLabels = fireResult.getGroupLabels();
        String alertName = groupLabels.getAlertname();
        log.info("SendAlert mailReach begin send AlertName :{}", alertName);
        //查表看负责人
        String[] principals = dao.GetRuleAlertAtPeople(alertName);
        if (principals == null) {
            log.info("SendAlert principals null alertName:{}", alertName);
            return;
        }
        fireResult.getAlerts().stream().forEach(alert -> {
            RuleAlertEntity ruleAlertEntity = dao.GetRuleAlertByAlertName(alert.getLabels().getAlertname());
            int priority = ruleAlertEntity.getPriority();
            Map<String, Object> map = new HashMap<>();
            String priorityStr = "P" + String.valueOf(priority);
            map.put("priority", priorityStr);
            map.put("title", fireResult.getCommonAnnotations().getTitle());
            map.put("alert_op", alert.getLabels().getAlert_op());
            map.put("alert_value", alert.getLabels().getAlert_value());
            map.put("application", alert.getLabels().getApplication());
            //Distinguish between basic classes, interface classes, and custom classes based on categories
            String serviceName = fireResult.getCommonLabels().getServiceName();
            try {
                String content = "";
                //get priority
                if (!StringUtils.isBlank(serviceName)) {
                    //接口型
                    map.put("ip", alert.getLabels().getServerIp());
                    map.put("start_time", DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
                    map.put("silence_url", silenceUrl);
                    map.put("serviceName", alert.getLabels().getServiceName());
                    map.put("methodName", alert.getLabels().getMethodName());
                    content = FreeMarkerUtil.getContentExceptJson("/mail", "mailInterfal.ftl", map);
                } else {
                    //基础型
                    map.put("ip", alert.getLabels().getIp());
                    map.put("start_time", DateUtil.Time2YYMMdd(alert.getStartsAt().toString()));
                    map.put("silence_url", silenceUrl);
                    map.put("pod", alert.getLabels().getPod());
                    content = FreeMarkerUtil.getContentExceptJson("/mail", "mailBasic.ftl", map);
                }
                String title = String.format("[%s][Hera]  %s %s %s", priorityStr, fireResult.getCommonAnnotations().getTitle(), alert.getLabels().getAlert_op(), alert.getLabels().getAlert_value());
                //send mail
                mailUtil.sendMailToUserArray(new ArrayList<>(Arrays.asList(principals)), title, content);
            } catch (Exception e) {
                log.error("SendAlert.mailReach error:{}", e);
            }
        });
    }
}
