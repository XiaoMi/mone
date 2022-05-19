package com.xiaomi.data.push.dao.model;

public class ActionConfWithBLOBs extends ActionConf {
    private String mockdata;

    private String script;

    public String getMockdata() {
        return mockdata;
    }

    public void setMockdata(String mockdata) {
        this.mockdata = mockdata == null ? null : mockdata.trim();
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script == null ? null : script.trim();
    }
}