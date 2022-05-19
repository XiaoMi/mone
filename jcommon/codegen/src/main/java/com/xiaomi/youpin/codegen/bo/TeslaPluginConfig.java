package com.xiaomi.youpin.codegen.bo;


import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
public class TeslaPluginConfig implements Serializable {

    private String mvnPath = "";
    private String javaPath = "";
    private String chatServer = "";
    private String token = "";
    private String dashServer = "";
    private String nickName = "";

    private String opsLocal = "";
    private String opsStaging = "";

    private String groupList = "";


    public String getMvnPath() {
        return mvnPath;
    }

    public void setMvnPath(String mvnPath) {
        this.mvnPath = mvnPath;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getChatServer() {
        return chatServer;
    }

    public void setChatServer(String chatServer) {
        this.chatServer = chatServer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDashServer() {
        return dashServer;
    }

    public void setDashServer(String dashServer) {
        this.dashServer = dashServer;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpsLocal() {
        return opsLocal;
    }

    public void setOpsLocal(String opsLocal) {
        this.opsLocal = opsLocal;
    }

    public String getOpsStaging() {
        return opsStaging;
    }

    public void setOpsStaging(String opsStaging) {
        this.opsStaging = opsStaging;
    }

    public String getGroupList() {
        return groupList;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }
}
