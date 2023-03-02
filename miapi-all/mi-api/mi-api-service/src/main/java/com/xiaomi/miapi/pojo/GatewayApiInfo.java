package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class GatewayApiInfo {
    private Long id;

    private String name;

    private String description;

    private String url;

    private String httpMethod;

    private String path;

    private Integer routeType;

    private String application;

    private String serviceName;

    private String methodName;

    private String serviceGroup;

    private String serviceVersion;

    private Integer status;

    private Integer invokeLimit;

    private Integer qpsLimit;

    private Integer timeout;

    private Long ctime;

    private Long utime;

    private Boolean allowMock;

    private String mockData;

    private String mockDataDesc;

    private String paramTemplate;

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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application == null ? null : application.trim();
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

    public Boolean getAllowMock() {
        return allowMock;
    }

    public void setAllowMock(Boolean allowMock) {
        this.allowMock = allowMock;
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData == null ? null : mockData.trim();
    }

    public String getMockDataDesc() {
        return mockDataDesc;
    }

    public void setMockDataDesc(String mockDataDesc) {
        this.mockDataDesc = mockDataDesc == null ? null : mockDataDesc.trim();
    }

    public String getParamTemplate() {
        return paramTemplate;
    }

    public void setParamTemplate(String paramTemplate) {
        this.paramTemplate = paramTemplate == null ? null : paramTemplate.trim();
    }

    public enum Column {
        id("id", "id", "BIGINT", false),
        name("name", "name", "VARCHAR", false),
        description("description", "description", "VARCHAR", false),
        url("url", "url", "VARCHAR", false),
        httpMethod("http_method", "httpMethod", "VARCHAR", false),
        path("path", "path", "VARCHAR", false),
        routeType("route_type", "routeType", "INTEGER", false),
        application("application", "application", "VARCHAR", false),
        serviceName("service_name", "serviceName", "VARCHAR", false),
        methodName("method_name", "methodName", "VARCHAR", false),
        serviceGroup("service_group", "serviceGroup", "VARCHAR", false),
        serviceVersion("service_version", "serviceVersion", "VARCHAR", false),
        status("status", "status", "INTEGER", false),
        invokeLimit("invoke_limit", "invokeLimit", "INTEGER", false),
        qpsLimit("qps_limit", "qpsLimit", "INTEGER", false),
        timeout("timeout", "timeout", "INTEGER", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        allowMock("allow_mock", "allowMock", "BIT", false),
        mockData("mock_data", "mockData", "VARCHAR", false),
        mockDataDesc("mock_data_desc", "mockDataDesc", "VARCHAR", false),
        paramTemplate("param_template", "paramTemplate", "LONGVARCHAR", false);

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