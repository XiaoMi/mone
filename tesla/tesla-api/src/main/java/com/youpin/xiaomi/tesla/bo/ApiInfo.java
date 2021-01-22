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

package com.youpin.xiaomi.tesla.bo;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Getter
public class ApiInfo implements Serializable {

    private final Long id;
    private final Date updateTime;
    private final ModifyType modifyType;
    private final String url;
    private final String path;

    private int flag;

    /**
     * 路由的类型
     * RouteType
     */
    private final Integer routeType;

    private final DubboApiInfo dubboApiInfo;

    private final ResponseConfig responseConfig;

    private final GroupConfig groupConfig;

    /**
     * 过滤器信息
     */
    private final Map<String,FilterInfo> filterInfoMap;

    /**
     * 插件信息(业务插件)
     */
    private final PlugInfo plugInfo;

    /**
     * 超时设置
     */
    private final int timeout;

    /**
     * 脚本信息
     */
    private final ScriptInfo scriptInfo;

    /**
     * 基于并发的控制
     */
    private final int invokeLimit;

    /**
     * 基于时间的控制(秒)
     */
    private final int rateInvokeLimit;

    private final String httpMethod;

    private final String token;

    /**
     * 数据源id列表
     */
    private final String dsIds;

    /**
     * 基于ip的防刷限制
     */
    private final int ipAntiBrushLimit;

    /**
     * 基于uid的防刷限制
     */
    private final int uidAntiBrushLimit;

    /**
     * filter 参数
     */
    private final String filterParams;

    /**
     * 缓存时间(单位毫秒)
     */
    private final long cacheTime;

    /**
     * 重新设置权限
     */
    public final void setPermission(int permission) {
        flag = permission;
    }

    /**
     * 添加一项或多项权限
     */
    public final void enable(int permission) {
        flag |= permission;
    }

    /**
     * 删除一项或多项权限
     */
    public final void disable(int permission) {
        flag &= ~permission;
    }

    /**
     * 是否拥某些权限
     */
    public final boolean isAllow(int permission) {
        return (flag & permission) == permission;
    }

    /**
     * 是否禁用了某些权限
     */
    public final boolean isNotAllow(int permission) {
        return (flag & permission) == 0;
    }

    /**
     * 是否仅仅拥有某些权限
     */
    public final boolean isOnlyAllow(int permission) {
        return flag == permission;
    }

    public ApiInfo(Long id, Date updateTime, ModifyType modifyType, String url, String path, int flag, Integer routeType, DubboApiInfo dubboApiInfo, ResponseConfig responseConfig, GroupConfig groupConfig, Map<String, FilterInfo> filterInfoMap, PlugInfo plugInfo, int timeout, ScriptInfo scriptInfo, int invokeLimit, int rateInvokeLimit, String httpMethod, String token, String dsIds, int ipAntiBrushLimit, int uidAntiBrushLimit, String filterParams, long cacheTime) {
        this.id = id;
        this.updateTime = updateTime;
        this.modifyType = modifyType;
        this.url = url;
        this.path = path;
        this.flag = flag;
        this.routeType = routeType;
        this.dubboApiInfo = dubboApiInfo;
        this.responseConfig = responseConfig;
        this.groupConfig = groupConfig;
        this.filterInfoMap = filterInfoMap;
        this.plugInfo = plugInfo;
        this.timeout = timeout;
        this.scriptInfo = scriptInfo;
        this.invokeLimit = invokeLimit;
        this.rateInvokeLimit = rateInvokeLimit;
        this.httpMethod = httpMethod;
        this.token = token;
        this.dsIds = dsIds;
        this.ipAntiBrushLimit = ipAntiBrushLimit;
        this.uidAntiBrushLimit = uidAntiBrushLimit;
        this.filterParams = filterParams;
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return "ApiInfo{" +
                "id=" + id +
                ", updateTime=" + updateTime +
                ", modifyType=" + modifyType +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", flag=" + flag +
                ", routeType=" + routeType +
                ", dubboApiInfo=" + dubboApiInfo +
                ", responseConfig=" + responseConfig +
                ", groupConfig=" + groupConfig +
                ", filterInfoMap=" + filterInfoMap +
                ", plugInfo=" + plugInfo +
                ", timeout=" + timeout +
                ", scriptInfo=" + scriptInfo +
                ", invokeLimit=" + invokeLimit +
                ", rateInvokeLimit=" + rateInvokeLimit +
                ", httpMethod='" + httpMethod + '\'' +
                ", token='" + token + '\'' +
                ", dsIds='" + dsIds + '\'' +
                ", ipAntiBrushLimit=" + ipAntiBrushLimit +
                ", uidAntiBrushLimit=" + uidAntiBrushLimit +
                ", filterParams='" + filterParams + '\'' +
                ", cacheTime=" + cacheTime +
                '}';
    }
}
