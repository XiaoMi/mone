package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AlertManagerRulesMapper;
import com.xiaomi.mone.monitor.dao.model.AlertManagerRules;
import com.xiaomi.mone.monitor.dao.model.AlertManagerRulesExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AlertManagerRulesDao {

    @Autowired
    private AlertManagerRulesMapper alertManagerRulesMapper;

    //插入alertmanager一条报警规则
    public int insertAlert(AlertManagerRules alertManagerRules) {
        alertManagerRules.setCreateTime(new Date());
        alertManagerRules.setUpdateTime(new Date());
        try {
            int result = alertManagerRulesMapper.insert(alertManagerRules);
            if (result < 0) {
                log.warn("[AlertManagerRulesDao.insert] failed to insert AlertManagerRulesMapper: {}", alertManagerRules.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AlertManagerRulesDao.insert] failed to insert AlertManagerRulesMapper: {}, err: {}", alertManagerRules.toString(), e);
            return 0;
        }
        return 1;
    }

    //查找alertmanager报警负责人
    public String[] getPrincipal(String alertName) {
        AlertManagerRulesExample example = new AlertManagerRulesExample();
        example.createCriteria().andRuleAlertEqualTo(alertName);
        //查db找负责人
        List<AlertManagerRules> alertManagerRules = alertManagerRulesMapper.selectByExample(example);
        for (AlertManagerRules amr : alertManagerRules
        ) {
            String tmpPrincipals = amr.getPrincipal();
            if (StringUtils.isNotEmpty(tmpPrincipals)) {
                return tmpPrincipals.split(",");
            }
        }


        return new String[]{};
    }
}

