package com.xiaomi.youpin.prometheus.agent.service.alarmContact;

import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class BaseAlertContact {

     Map<String, String> NAME_MAP = new HashMap<String, String>() {
        {
            put("application", "应用");

            put("calert", "报警中文名称");

            put("send_interval", "发送间隔");

            put("container", "容器");

            put("namespace", "命名空间");

            put("restartCounts", "重启次数");

            put("alert_key", "关键词");
        }
    };
    String[] ALERT_INVISIBLE_LIST = new String[]{"system", "exceptViewLables", "app_iam_id", "metrics_flag", "group_key", "job", "image"};

    void Reach(AlertManagerFireResult fireResult) {

    }

    void filterName(Map<String, Object> map) {
        List<Map.Entry<String, Object>> entries = new ArrayList<>(map.entrySet());
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            for (String s : ALERT_INVISIBLE_LIST) {
                if (StringUtils.containsIgnoreCase(key, s)) {
                    map.remove(key);
                }
            }
        }
    }

    Map<String, Object> transferNames(Map<String, Object> map) {
        List<Map.Entry<String, Object>> entries = new ArrayList<>(map.entrySet());
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if (NAME_MAP.containsKey(key)) {
                Object value = entry.getValue();
                String newKey = NAME_MAP.get(key);
                map.remove(key);
                map.put(newKey, value);
            }
        }
        return map;
    }
}
