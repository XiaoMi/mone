package com.xiaomi.youpin.tesla.ip.listener;

import java.util.Map;

public class Req {

    private String cmd;

    private Map<String,String> data;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
