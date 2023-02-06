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

package com.xiaomi.youpin.gateway.group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
public class ApiRequest {

    /**
     * 版本号
     */
    private String version;

    /**
     * 是否返回原始数据
     */
    private boolean debug;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 分组调用的名称
     */
    private String groupName;

    /**
     * 分组调用的参数
     */
    private List<String[]>groupParams = new ArrayList<>();

    private String traceId;

    private List<MethodInfo> methods = new ArrayList<>();


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String[]> getGroupParams() {
        return groupParams;
    }

    public void setGroupParams(List<String[]> groupParams) {
        this.groupParams = groupParams;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "version='" + version + '\'' +
                ", debug=" + debug +
                ", channel='" + channel + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupParams=" + groupParams +
                ", traceId='" + traceId + '\'' +
                ", methods=" + methods +
                '}';
    }
}
