package com.xiaomi.mone.monitor.service.model.prometheus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/9/14 9:09 上午
 */
@Data
public class AlarmRuleTemplateRequest implements Serializable {

    private AppAlarmRuleTemplate template;
    private List<AppAlarmRule> alarmRules;

    public static void main(String[] args) {
        AppAlarmRuleTemplate template = new AppAlarmRuleTemplate();
        template.setName("testTemplate");
        template.setRemark("备注1");

        AppAlarmRule rule = new AppAlarmRule();
        rule.setAlert("cupUseRate");
        rule.setPriority("P0");
        JsonObject alertTeam1 = new JsonObject();
        alertTeam1.addProperty("id",2);
        alertTeam1.addProperty("type","oncall");
        alertTeam1.addProperty("name","falcon-oncal");
        rule.setAlertTeam(alertTeam1.toString());
        rule.setDataCount(3);
        rule.setOp(">");
        rule.setValue(90f);
        rule.setSendInterval("1h");

        AppAlarmRule rule1 = new AppAlarmRule();
        rule1.setAlert("memUseRate");
        rule1.setPriority("P0");
        JsonObject alertTeam = new JsonObject();
        alertTeam.addProperty("id",2);
        alertTeam.addProperty("type","oncall");
        alertTeam.addProperty("name","falcon-oncal");
        rule1.setAlertTeam(alertTeam.toString());
        rule1.setOp(">");
        rule1.setValue(90f);
        rule1.setSendInterval("1h");

        List<AppAlarmRule> list = new ArrayList<>();
        list.add(rule);
        list.add(rule1);

        AlarmRuleTemplateRequest request = new AlarmRuleTemplateRequest();
        request.setAlarmRules(list);
        request.setTemplate(template);

        System.out.println(new Gson().toJson(request));
    }
}
