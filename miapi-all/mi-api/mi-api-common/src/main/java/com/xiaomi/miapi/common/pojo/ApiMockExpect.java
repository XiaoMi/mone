package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ApiMockExpect {
    private Integer id;

    private String mockExpName;

    private Integer mockDataType;

    private String paramsMd5;

    private Integer apiId;

    private Boolean isDefault;

    private String updateUser;

    private Date updateTime;

    private Boolean enable;

    private Integer mockRequestParamType;

    private String proxyUrl;

    private Boolean useMockScript;

    private String mockParams;

    private String mockData;

    private String mockRule;

    private String mockRequestRaw;

    private String mockScript;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMockExpName() {
        return mockExpName;
    }

    public void setMockExpName(String mockExpName) {
        this.mockExpName = mockExpName == null ? null : mockExpName.trim();
    }

    public Integer getMockDataType() {
        return mockDataType;
    }

    public void setMockDataType(Integer mockDataType) {
        this.mockDataType = mockDataType;
    }

    public String getParamsMd5() {
        return paramsMd5;
    }

    public void setParamsMd5(String paramsMd5) {
        this.paramsMd5 = paramsMd5 == null ? null : paramsMd5.trim();
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getMockRequestParamType() {
        return mockRequestParamType;
    }

    public void setMockRequestParamType(Integer mockRequestParamType) {
        this.mockRequestParamType = mockRequestParamType;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl == null ? null : proxyUrl.trim();
    }

    public Boolean getUseMockScript() {
        return useMockScript;
    }

    public void setUseMockScript(Boolean useMockScript) {
        this.useMockScript = useMockScript;
    }

    public String getMockParams() {
        return mockParams;
    }

    public void setMockParams(String mockParams) {
        this.mockParams = mockParams == null ? null : mockParams.trim();
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData == null ? null : mockData.trim();
    }

    public String getMockRule() {
        return mockRule;
    }

    public void setMockRule(String mockRule) {
        this.mockRule = mockRule == null ? null : mockRule.trim();
    }

    public String getMockRequestRaw() {
        return mockRequestRaw;
    }

    public void setMockRequestRaw(String mockRequestRaw) {
        this.mockRequestRaw = mockRequestRaw == null ? null : mockRequestRaw.trim();
    }

    public String getMockScript() {
        return mockScript;
    }

    public void setMockScript(String mockScript) {
        this.mockScript = mockScript == null ? null : mockScript.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        mockExpName("mock_exp_name", "mockExpName", "VARCHAR", false),
        mockDataType("mock_data_type", "mockDataType", "INTEGER", false),
        paramsMd5("params_md5", "paramsMd5", "VARCHAR", false),
        apiId("api_id", "apiId", "INTEGER", false),
        isDefault("is_default", "isDefault", "BIT", false),
        updateUser("update_user", "updateUser", "VARCHAR", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        enable("enable", "enable", "BIT", false),
        mockRequestParamType("mock_request_param_type", "mockRequestParamType", "INTEGER", false),
        proxyUrl("proxy_url", "proxyUrl", "VARCHAR", false),
        useMockScript("use_mock_script", "useMockScript", "BIT", false),
        mockParams("mock_params", "mockParams", "LONGVARCHAR", false),
        mockData("mock_data", "mockData", "LONGVARCHAR", false),
        mockRule("mock_rule", "mockRule", "LONGVARCHAR", false),
        mockRequestRaw("mock_request_raw", "mockRequestRaw", "LONGVARCHAR", false),
        mockScript("mock_script", "mockScript", "LONGVARCHAR", false);

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