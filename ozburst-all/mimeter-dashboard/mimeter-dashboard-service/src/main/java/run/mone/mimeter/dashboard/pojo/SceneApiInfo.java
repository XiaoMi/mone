package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class SceneApiInfo {
    private Integer id;

    private Integer sceneId;

    private Integer apiOrder;

    private String apiName;

    private Integer sourceType;

    private Integer apiType;

    private Integer requestMethod;

    private Integer requestTimeout;

    private Boolean needLogin;

    private Integer tokenType;

    private String contentType;

    private Integer nacosType;

    private String serviceName;

    private String methodName;

    private String paramTypeList;

    private String dubboGroup;

    private String dubboVersion;

    private Integer serialLinkId;

    private String apiUrl;

    private String apiHeader;

    private String requestParamInfo;

    private String outputParamInfo;

    private String requestBody;

    private String dubboParamJson;

    private String checkPoint;

    private String filterCondition;

    private String apiTspAuth;

    private String apiTrafficInfo;

    private String apiX5Info;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getApiOrder() {
        return apiOrder;
    }

    public void setApiOrder(Integer apiOrder) {
        this.apiOrder = apiOrder;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName == null ? null : apiName.trim();
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getApiType() {
        return apiType;
    }

    public void setApiType(Integer apiType) {
        this.apiType = apiType;
    }

    public Integer getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(Integer requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Boolean getNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(Boolean needLogin) {
        this.needLogin = needLogin;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    public Integer getNacosType() {
        return nacosType;
    }

    public void setNacosType(Integer nacosType) {
        this.nacosType = nacosType;
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

    public String getParamTypeList() {
        return paramTypeList;
    }

    public void setParamTypeList(String paramTypeList) {
        this.paramTypeList = paramTypeList == null ? null : paramTypeList.trim();
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

    public Integer getSerialLinkId() {
        return serialLinkId;
    }

    public void setSerialLinkId(Integer serialLinkId) {
        this.serialLinkId = serialLinkId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl == null ? null : apiUrl.trim();
    }

    public String getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(String apiHeader) {
        this.apiHeader = apiHeader == null ? null : apiHeader.trim();
    }

    public String getRequestParamInfo() {
        return requestParamInfo;
    }

    public void setRequestParamInfo(String requestParamInfo) {
        this.requestParamInfo = requestParamInfo == null ? null : requestParamInfo.trim();
    }

    public String getOutputParamInfo() {
        return outputParamInfo;
    }

    public void setOutputParamInfo(String outputParamInfo) {
        this.outputParamInfo = outputParamInfo == null ? null : outputParamInfo.trim();
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody == null ? null : requestBody.trim();
    }

    public String getDubboParamJson() {
        return dubboParamJson;
    }

    public void setDubboParamJson(String dubboParamJson) {
        this.dubboParamJson = dubboParamJson == null ? null : dubboParamJson.trim();
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint == null ? null : checkPoint.trim();
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition == null ? null : filterCondition.trim();
    }

    public String getApiTspAuth() {
        return apiTspAuth;
    }

    public void setApiTspAuth(String apiTspAuth) {
        this.apiTspAuth = apiTspAuth == null ? null : apiTspAuth.trim();
    }

    public String getApiTrafficInfo() {
        return apiTrafficInfo;
    }

    public void setApiTrafficInfo(String apiTrafficInfo) {
        this.apiTrafficInfo = apiTrafficInfo == null ? null : apiTrafficInfo.trim();
    }

    public String getApiX5Info() {
        return apiX5Info;
    }

    public void setApiX5Info(String apiX5Info) {
        this.apiX5Info = apiX5Info == null ? null : apiX5Info.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        sceneId("scene_id", "sceneId", "INTEGER", false),
        apiOrder("api_order", "apiOrder", "INTEGER", false),
        apiName("api_name", "apiName", "VARCHAR", false),
        sourceType("source_type", "sourceType", "INTEGER", false),
        apiType("api_type", "apiType", "INTEGER", false),
        requestMethod("request_method", "requestMethod", "INTEGER", false),
        requestTimeout("request_timeout", "requestTimeout", "INTEGER", false),
        needLogin("need_login", "needLogin", "BIT", false),
        tokenType("token_type", "tokenType", "INTEGER", false),
        contentType("content_type", "contentType", "VARCHAR", false),
        nacosType("nacos_type", "nacosType", "INTEGER", false),
        serviceName("service_name", "serviceName", "VARCHAR", false),
        methodName("method_name", "methodName", "VARCHAR", false),
        paramTypeList("param_type_list", "paramTypeList", "VARCHAR", false),
        dubboGroup("dubbo_group", "dubboGroup", "VARCHAR", false),
        dubboVersion("dubbo_version", "dubboVersion", "VARCHAR", false),
        serialLinkId("serial_link_id", "serialLinkId", "INTEGER", false),
        apiUrl("api_url", "apiUrl", "LONGVARCHAR", false),
        apiHeader("api_header", "apiHeader", "LONGVARCHAR", false),
        requestParamInfo("request_param_info", "requestParamInfo", "LONGVARCHAR", false),
        outputParamInfo("output_param_info", "outputParamInfo", "LONGVARCHAR", false),
        requestBody("request_body", "requestBody", "LONGVARCHAR", false),
        dubboParamJson("dubbo_param_json", "dubboParamJson", "LONGVARCHAR", false),
        checkPoint("check_point", "checkPoint", "LONGVARCHAR", false),
        filterCondition("filter_condition", "filterCondition", "LONGVARCHAR", false),
        apiTspAuth("api_tsp_auth", "apiTspAuth", "LONGVARCHAR", false),
        apiTrafficInfo("api_traffic_info", "apiTrafficInfo", "LONGVARCHAR", false),
        apiX5Info("api_x5_info", "apiX5Info", "LONGVARCHAR", false);

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