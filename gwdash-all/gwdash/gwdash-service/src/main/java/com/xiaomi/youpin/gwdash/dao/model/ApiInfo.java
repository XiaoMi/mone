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

package com.xiaomi.youpin.gwdash.dao.model;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiInfo {
    private Long id;

    private String name;

    private String description;

    private String url;

    private String httpMethod;

    private String path;

    private Integer routeType;

    private Integer groupId;

    private String serviceName;

    private String methodName;

    private String serviceGroup;

    private String serviceVersion;

    private Integer status;

    private String creator;

    private String updater;

    private String contentType;

    private Integer flag;

    private Integer invokeLimit;

    private Integer qpsLimit;

    private Integer timeout;

    private Integer cacheExpire;

    private String token;

    private Long ctime;

    private Long utime;

    private String pluginName;

    private String dsIds;

    private Integer ipAntiBrushLimit;

    private Integer uidAntiBrushLimit;

    private Integer priority;

    private String paramTemplate;

    private String filterParams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod == null ? null : httpMethod.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName == null ? null : methodName.trim();
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup == null ? null : serviceGroup.trim();
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion == null ? null : serviceVersion.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater == null ? null : updater.trim();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getInvokeLimit() {
        return invokeLimit;
    }

    public void setInvokeLimit(Integer invokeLimit) {
        this.invokeLimit = invokeLimit;
    }

    public Integer getQpsLimit() {
        return qpsLimit;
    }

    public void setQpsLimit(Integer qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getCacheExpire() {
        return cacheExpire;
    }

    public void setCacheExpire(Integer cacheExpire) {
        this.cacheExpire = cacheExpire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName == null ? null : pluginName.trim();
    }

    public String getDsIds() {
        return dsIds;
    }

    public void setDsIds(String dsIds) {
        this.dsIds = dsIds == null ? null : dsIds.trim();
    }

    public Integer getIpAntiBrushLimit() {
        return ipAntiBrushLimit;
    }

    public void setIpAntiBrushLimit(Integer ipAntiBrushLimit) {
        this.ipAntiBrushLimit = ipAntiBrushLimit;
    }

    public Integer getUidAntiBrushLimit() {
        return uidAntiBrushLimit;
    }

    public void setUidAntiBrushLimit(Integer uidAntiBrushLimit) {
        this.uidAntiBrushLimit = uidAntiBrushLimit;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getParamTemplate() {
        return paramTemplate;
    }

    public void setParamTemplate(String paramTemplate) {
        this.paramTemplate = paramTemplate == null ? null : paramTemplate.trim();
    }

    public String getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(String filterParams) {
        this.filterParams = filterParams == null ? null : filterParams.trim();
    }

    public enum Column {
        id("id", "id", "BIGINT", false),
        name("name", "name", "VARCHAR", false),
        description("description", "description", "VARCHAR", false),
        url("url", "url", "VARCHAR", false),
        httpMethod("http_method", "httpMethod", "VARCHAR", false),
        path("path", "path", "VARCHAR", false),
        routeType("route_type", "routeType", "TINYINT", false),
        groupId("group_id", "groupId", "INTEGER", false),
        serviceName("service_name", "serviceName", "VARCHAR", false),
        methodName("method_name", "methodName", "VARCHAR", false),
        serviceGroup("service_group", "serviceGroup", "VARCHAR", false),
        serviceVersion("service_version", "serviceVersion", "VARCHAR", false),
        status("status", "status", "INTEGER", false),
        creator("creator", "creator", "VARCHAR", false),
        updater("updater", "updater", "VARCHAR", false),
        contentType("content_type", "contentType", "VARCHAR", false),
        flag("flag", "flag", "INTEGER", false),
        invokeLimit("invoke_limit", "invokeLimit", "INTEGER", false),
        qpsLimit("qps_limit", "qpsLimit", "INTEGER", false),
        timeout("timeout", "timeout", "INTEGER", false),
        cacheExpire("cache_expire", "cacheExpire", "INTEGER", false),
        token("token", "token", "VARCHAR", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        pluginName("plugin_name", "pluginName", "VARCHAR", false),
        dsIds("ds_ids", "dsIds", "VARCHAR", false),
        ipAntiBrushLimit("ip_anti_brush_limit", "ipAntiBrushLimit", "INTEGER", false),
        uidAntiBrushLimit("uid_anti_brush_limit", "uidAntiBrushLimit", "INTEGER", false),
        priority("priority", "priority", "INTEGER", false),
        paramTemplate("param_template", "paramTemplate", "LONGVARCHAR", false),
        filterParams("filter_params", "filterParams", "LONGVARCHAR", false);

        private static final String BEGINNING_DELIMITER = "\"";

        private static final String ENDING_DELIMITER = "\"";

        private final String column;

        private final boolean isColumnNameDelimited;

        private final String javaProperty;

        private final String jdbcType;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String getJavaProperty() {
            return this.javaProperty;
        }

        public String getJdbcType() {
            return this.jdbcType;
        }

        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        public static Column[] all() {
            return Column.values();
        }

        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}