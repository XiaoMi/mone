/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
