package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiRequestExp {
    private Integer id;

    private Integer apiId;

    private Integer requestParamExpType;

    private String codeGenExp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getRequestParamExpType() {
        return requestParamExpType;
    }

    public void setRequestParamExpType(Integer requestParamExpType) {
        this.requestParamExpType = requestParamExpType;
    }

    public String getCodeGenExp() {
        return codeGenExp;
    }

    public void setCodeGenExp(String codeGenExp) {
        this.codeGenExp = codeGenExp == null ? null : codeGenExp.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        apiId("api_id", "apiId", "INTEGER", false),
        requestParamExpType("request_param_exp_type", "requestParamExpType", "INTEGER", false),
        codeGenExp("code_gen_exp", "codeGenExp", "LONGVARCHAR", false);

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