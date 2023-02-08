package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiTestCase {
    private Integer id;

    private Integer accountId;

    private Integer apiId;

    private Integer apiProtocal;

    private String httpMethod;

    private String url;

    private Integer requestTimeout;

    private String httpHeaders;

    private String caseName;

    private String httpDomian;

    private Integer envId;

    private Integer httpReqBodyType;

    private String dubboInterface;

    private String dubboMethodName;

    private String dubboGroup;

    private String dubboVersion;

    private String dubboAddr;

    private String dubboParamType;

    private Boolean dubboIsGeneric;

    private Integer dubboRetryTime;

    private Boolean dubboUseAttachment;

    private String dubboAttachment;

    private String dubboEnv;

    private Boolean useX5Filter;

    private String x5AppKey;

    private Integer x5AppId;

    private Integer caseGroupId;

    private String grpcPackageName;

    private String grpcInterfaceName;

    private String grpcMethodName;

    private String grpcServerAddr;

    private String grpcAppName;

    private String httpRequestBody;

    private String dubboParamBody;

    private String grpcParamBody;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getApiProtocal() {
        return apiProtocal;
    }

    public void setApiProtocal(Integer apiProtocal) {
        this.apiProtocal = apiProtocal;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod == null ? null : httpMethod.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public String getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(String httpHeaders) {
        this.httpHeaders = httpHeaders == null ? null : httpHeaders.trim();
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName == null ? null : caseName.trim();
    }

    public String getHttpDomian() {
        return httpDomian;
    }

    public void setHttpDomian(String httpDomian) {
        this.httpDomian = httpDomian == null ? null : httpDomian.trim();
    }

    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public Integer getHttpReqBodyType() {
        return httpReqBodyType;
    }

    public void setHttpReqBodyType(Integer httpReqBodyType) {
        this.httpReqBodyType = httpReqBodyType;
    }

    public String getDubboInterface() {
        return dubboInterface;
    }

    public void setDubboInterface(String dubboInterface) {
        this.dubboInterface = dubboInterface == null ? null : dubboInterface.trim();
    }

    public String getDubboMethodName() {
        return dubboMethodName;
    }

    public void setDubboMethodName(String dubboMethodName) {
        this.dubboMethodName = dubboMethodName == null ? null : dubboMethodName.trim();
    }

    public String getDubboGroup() {
        return dubboGroup;
    }

    public void setDubboGroup(String dubboGroup) {
        this.dubboGroup = dubboGroup == null ? null : dubboGroup.trim();
    }

    public String getDubboVersion() {
        return dubboVersion;
    }

    public void setDubboVersion(String dubboVersion) {
        this.dubboVersion = dubboVersion == null ? null : dubboVersion.trim();
    }

    public String getDubboAddr() {
        return dubboAddr;
    }

    public void setDubboAddr(String dubboAddr) {
        this.dubboAddr = dubboAddr == null ? null : dubboAddr.trim();
    }

    public String getDubboParamType() {
        return dubboParamType;
    }

    public void setDubboParamType(String dubboParamType) {
        this.dubboParamType = dubboParamType == null ? null : dubboParamType.trim();
    }

    public Boolean getDubboIsGeneric() {
        return dubboIsGeneric;
    }

    public void setDubboIsGeneric(Boolean dubboIsGeneric) {
        this.dubboIsGeneric = dubboIsGeneric;
    }

    public Integer getDubboRetryTime() {
        return dubboRetryTime;
    }

    public void setDubboRetryTime(Integer dubboRetryTime) {
        this.dubboRetryTime = dubboRetryTime;
    }

    public Boolean getDubboUseAttachment() {
        return dubboUseAttachment;
    }

    public void setDubboUseAttachment(Boolean dubboUseAttachment) {
        this.dubboUseAttachment = dubboUseAttachment;
    }

    public String getDubboAttachment() {
        return dubboAttachment;
    }

    public void setDubboAttachment(String dubboAttachment) {
        this.dubboAttachment = dubboAttachment == null ? null : dubboAttachment.trim();
    }

    public String getDubboEnv() {
        return dubboEnv;
    }

    public void setDubboEnv(String dubboEnv) {
        this.dubboEnv = dubboEnv == null ? null : dubboEnv.trim();
    }

    public Boolean getUseX5Filter() {
        return useX5Filter;
    }

    public void setUseX5Filter(Boolean useX5Filter) {
        this.useX5Filter = useX5Filter;
    }

    public String getX5AppKey() {
        return x5AppKey;
    }

    public void setX5AppKey(String x5AppKey) {
        this.x5AppKey = x5AppKey == null ? null : x5AppKey.trim();
    }

    public Integer getX5AppId() {
        return x5AppId;
    }

    public void setX5AppId(Integer x5AppId) {
        this.x5AppId = x5AppId;
    }

    public Integer getCaseGroupId() {
        return caseGroupId;
    }

    public void setCaseGroupId(Integer caseGroupId) {
        this.caseGroupId = caseGroupId;
    }

    public String getGrpcPackageName() {
        return grpcPackageName;
    }

    public void setGrpcPackageName(String grpcPackageName) {
        this.grpcPackageName = grpcPackageName == null ? null : grpcPackageName.trim();
    }

    public String getGrpcInterfaceName() {
        return grpcInterfaceName;
    }

    public void setGrpcInterfaceName(String grpcInterfaceName) {
        this.grpcInterfaceName = grpcInterfaceName == null ? null : grpcInterfaceName.trim();
    }

    public String getGrpcMethodName() {
        return grpcMethodName;
    }

    public void setGrpcMethodName(String grpcMethodName) {
        this.grpcMethodName = grpcMethodName == null ? null : grpcMethodName.trim();
    }

    public String getGrpcServerAddr() {
        return grpcServerAddr;
    }

    public void setGrpcServerAddr(String grpcServerAddr) {
        this.grpcServerAddr = grpcServerAddr == null ? null : grpcServerAddr.trim();
    }

    public String getGrpcAppName() {
        return grpcAppName;
    }

    public void setGrpcAppName(String grpcAppName) {
        this.grpcAppName = grpcAppName == null ? null : grpcAppName.trim();
    }

    public String getHttpRequestBody() {
        return httpRequestBody;
    }

    public void setHttpRequestBody(String httpRequestBody) {
        this.httpRequestBody = httpRequestBody == null ? null : httpRequestBody.trim();
    }

    public String getDubboParamBody() {
        return dubboParamBody;
    }

    public void setDubboParamBody(String dubboParamBody) {
        this.dubboParamBody = dubboParamBody == null ? null : dubboParamBody.trim();
    }

    public String getGrpcParamBody() {
        return grpcParamBody;
    }

    public void setGrpcParamBody(String grpcParamBody) {
        this.grpcParamBody = grpcParamBody == null ? null : grpcParamBody.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        accountId("account_id", "accountId", "INTEGER", false),
        apiId("api_id", "apiId", "INTEGER", false),
        apiProtocal("api_protocal", "apiProtocal", "INTEGER", false),
        httpMethod("http_method", "httpMethod", "VARCHAR", false),
        url("url", "url", "VARCHAR", false),
        requestTimeout("request_timeout", "requestTimeout", "INTEGER", false),
        httpHeaders("http_headers", "httpHeaders", "VARCHAR", false),
        caseName("case_name", "caseName", "VARCHAR", false),
        httpDomian("http_domian", "httpDomian", "VARCHAR", false),
        envId("env_id", "envId", "INTEGER", false),
        httpReqBodyType("http_req_body_type", "httpReqBodyType", "INTEGER", false),
        dubboInterface("dubbo_interface", "dubboInterface", "VARCHAR", false),
        dubboMethodName("dubbo_method_name", "dubboMethodName", "VARCHAR", false),
        dubboGroup("dubbo_group", "dubboGroup", "VARCHAR", false),
        dubboVersion("dubbo_version", "dubboVersion", "VARCHAR", false),
        dubboAddr("dubbo_addr", "dubboAddr", "VARCHAR", false),
        dubboParamType("dubbo_param_type", "dubboParamType", "VARCHAR", false),
        dubboIsGeneric("dubbo_is_generic", "dubboIsGeneric", "BIT", false),
        dubboRetryTime("dubbo_retry_time", "dubboRetryTime", "INTEGER", false),
        dubboUseAttachment("dubbo_use_attachment", "dubboUseAttachment", "BIT", false),
        dubboAttachment("dubbo_attachment", "dubboAttachment", "VARCHAR", false),
        dubboEnv("dubbo_env", "dubboEnv", "VARCHAR", false),
        useX5Filter("use_x5_filter", "useX5Filter", "BIT", false),
        x5AppKey("x5_app_key", "x5AppKey", "VARCHAR", false),
        x5AppId("x5_app_id", "x5AppId", "INTEGER", false),
        caseGroupId("case_group_id", "caseGroupId", "INTEGER", false),
        grpcPackageName("grpc_package_name", "grpcPackageName", "VARCHAR", false),
        grpcInterfaceName("grpc_interface_name", "grpcInterfaceName", "VARCHAR", false),
        grpcMethodName("grpc_method_name", "grpcMethodName", "VARCHAR", false),
        grpcServerAddr("grpc_server_addr", "grpcServerAddr", "VARCHAR", false),
        grpcAppName("grpc_app_name", "grpcAppName", "VARCHAR", false),
        httpRequestBody("http_request_body", "httpRequestBody", "LONGVARCHAR", false),
        dubboParamBody("dubbo_param_body", "dubboParamBody", "LONGVARCHAR", false),
        grpcParamBody("grpc_param_body", "grpcParamBody", "LONGVARCHAR", false);

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