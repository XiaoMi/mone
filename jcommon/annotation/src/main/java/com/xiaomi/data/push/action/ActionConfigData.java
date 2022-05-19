package com.xiaomi.data.push.action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class ActionConfigData {

    private Map<String, ActionInfo> actionConfMap = new HashMap<>();

    private String version;

    private String time;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, ActionInfo> getActionConfMap() {
        return actionConfMap;
    }

    public void setActionConfMap(Map<String, ActionInfo> actionConfMap) {
        this.actionConfMap = actionConfMap;
    }
}
