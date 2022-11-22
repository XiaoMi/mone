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

import com.xiaomi.youpin.gateway.group.filter.GateWayFilter;

import java.util.Arrays;

/**
 * @author goodjava@qq.com
 */
public class MethodInfo {

    /**
     * 相当于别名(前端不用再关心参数类型 服务名称 方法名称等)
     */
    private String cmd;
    /**
     * 可以理解问spring 容器中 bean 的name
     */
    private String serviceName;
    /**
     * 调用的方法名称
     */
    private String methodName;
    /**
     * 超时时间判定(单位毫秒)
     */
    private long timeout = 600;
    /**
     * 参数
     */
    private String[] params;
    /**
     * 参数类型
     */
    private String[] paramTypes;


    private transient GateWayFilter filter;

    public MethodInfo() {
    }


    public MethodInfo(String serviceName, String methodName, long timeout, String[] params, String[] paramTypes, GateWayFilter filter) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.timeout = timeout;
        this.params = params;
        this.paramTypes = paramTypes;
        this.filter = filter;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(String[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public GateWayFilter getFilter() {
        return filter;
    }

    public void setFilter(GateWayFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "cmd='" + cmd + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", timeout=" + timeout +
                ", params=" + Arrays.toString(params) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", filter=" + filter +
                '}';
    }
}
