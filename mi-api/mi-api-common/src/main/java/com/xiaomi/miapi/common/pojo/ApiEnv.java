package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiEnv {
    private Integer id;

    private String envName;

    private String httpDomain;

    private String envDesc;

    private Integer projectId;

    private Boolean sysDefault;

    private String headers;

    private String reqParamFormData;

    private String reqParamRaw;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName == null ? null : envName.trim();
    }

    public String getHttpDomain() {
        return httpDomain;
    }

    public void setHttpDomain(String httpDomain) {
        this.httpDomain = httpDomain == null ? null : httpDomain.trim();
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc == null ? null : envDesc.trim();
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Boolean getSysDefault() {
        return sysDefault;
    }

    public void setSysDefault(Boolean sysDefault) {
        this.sysDefault = sysDefault;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers == null ? null : headers.trim();
    }

    public String getReqParamFormData() {
        return reqParamFormData;
    }

    public void setReqParamFormData(String reqParamFormData) {
        this.reqParamFormData = reqParamFormData == null ? null : reqParamFormData.trim();
    }

    public String getReqParamRaw() {
        return reqParamRaw;
    }

    public void setReqParamRaw(String reqParamRaw) {
        this.reqParamRaw = reqParamRaw == null ? null : reqParamRaw.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        envName("env_name", "envName", "VARCHAR", false),
        httpDomain("http_domain", "httpDomain", "VARCHAR", false),
        envDesc("env_desc", "envDesc", "VARCHAR", false),
        projectId("project_id", "projectId", "INTEGER", false),
        sysDefault("sys_default", "sysDefault", "BIT", false),
        headers("headers", "headers", "LONGVARCHAR", false),
        reqParamFormData("req_param_form_data", "reqParamFormData", "LONGVARCHAR", false),
        reqParamRaw("req_param_raw", "reqParamRaw", "LONGVARCHAR", false);

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