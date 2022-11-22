package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;

@Table("api_info")
@Data
public class ApiInfo {
    @org.nutz.dao.entity.annotation.Column("id")
    private Long id;

    @org.nutz.dao.entity.annotation.Column("name")
    private String name;

    @org.nutz.dao.entity.annotation.Column("description")
    private String description;

    @org.nutz.dao.entity.annotation.Column("url")
    private String url;

    @org.nutz.dao.entity.annotation.Column("http_method")
    private String httpMethod;

    @org.nutz.dao.entity.annotation.Column("path")
    private String path;

    @org.nutz.dao.entity.annotation.Column("route_type")
    private Integer routeType;

    @org.nutz.dao.entity.annotation.Column("group_id")
    private Integer groupId;

    @org.nutz.dao.entity.annotation.Column("service_name")
    private String serviceName;

    @org.nutz.dao.entity.annotation.Column("method_name")
    private String methodName;

    @org.nutz.dao.entity.annotation.Column("service_group")
    private String serviceGroup;

    @org.nutz.dao.entity.annotation.Column("service_version")
    private String serviceVersion;

    @org.nutz.dao.entity.annotation.Column("status")
    private Integer status;

    @org.nutz.dao.entity.annotation.Column("creator")
    private String creator;

    @org.nutz.dao.entity.annotation.Column("updater")
    private String updater;

    @org.nutz.dao.entity.annotation.Column("content_type")
    private String contentType;

    @org.nutz.dao.entity.annotation.Column("flag")
    private Integer flag;

    @org.nutz.dao.entity.annotation.Column("invoke_limit")
    private Integer invokeLimit;

    @org.nutz.dao.entity.annotation.Column("qps_limit")
    private Integer qpsLimit;

    @org.nutz.dao.entity.annotation.Column("timeout")
    private Integer timeout;

    @org.nutz.dao.entity.annotation.Column("cache_expire")
    private Integer cacheExpire;

    @org.nutz.dao.entity.annotation.Column("token")
    private String token;

    @org.nutz.dao.entity.annotation.Column("ctime")
    private Long ctime;

    @org.nutz.dao.entity.annotation.Column("utime")
    private Long utime;

    @org.nutz.dao.entity.annotation.Column("plugin_name")
    private String pluginName;

    @org.nutz.dao.entity.annotation.Column("ds_ids")
    private String dsIds;

    @org.nutz.dao.entity.annotation.Column("ip_anti_brush_limit")
    private Integer ipAntiBrushLimit;

    @org.nutz.dao.entity.annotation.Column("uid_anti_brush_limit")
    private Integer uidAntiBrushLimit;

    @org.nutz.dao.entity.annotation.Column("priority")
    private Integer priority;

    @org.nutz.dao.entity.annotation.Column("application")
    private String application;

    @org.nutz.dao.entity.annotation.Column("param_template")
    private String paramTemplate;

    @org.nutz.dao.entity.annotation.Column("filter_params")
    private String filterParams;

    @org.nutz.dao.entity.annotation.Column("attachment")
    private String attachment;

    @org.nutz.dao.entity.annotation.Column("app_src")
    private Integer appSrc;

    @org.nutz.dao.entity.annotation.Column("api_src")
    private Integer apiSrc;

    @org.nutz.dao.entity.annotation.Column("tenement")
    private String tenement;

    public Integer getAppSrc() {
        return appSrc;
    }

    public void setAppSrc(Integer appSrc) {
        this.appSrc = appSrc;
    }

    public Integer getApiSrc() {
        return apiSrc;
    }

    public void setApiSrc(Integer apiSrc) {
        this.apiSrc = apiSrc;
    }

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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application == null ? null : application.trim();
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
        application("application", "application", "VARCHAR", false),
        paramTemplate("param_template", "paramTemplate", "LONGVARCHAR", false),
        filterParams("filter_params", "filterParams", "LONGVARCHAR", false),
        appSrc("app_src", "appSrc", "INTEGER", false),
        apiSrc("apiSrc", "apiSrc", "INTEGER", false);

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