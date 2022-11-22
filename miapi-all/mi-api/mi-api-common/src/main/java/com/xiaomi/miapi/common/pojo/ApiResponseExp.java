package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiResponseExp {
    private Integer id;

    private Integer apiId;

    private Integer respGenExpType;

    private String respGenExp;

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

    public Integer getRespGenExpType() {
        return respGenExpType;
    }

    public void setRespGenExpType(Integer respGenExpType) {
        this.respGenExpType = respGenExpType;
    }

    public String getRespGenExp() {
        return respGenExp;
    }

    public void setRespGenExp(String respGenExp) {
        this.respGenExp = respGenExp == null ? null : respGenExp.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        apiId("api_id", "apiId", "INTEGER", false),
        respGenExpType("resp_gen_exp_type", "respGenExpType", "INTEGER", false),
        respGenExp("resp_gen_exp", "respGenExp", "LONGVARCHAR", false);

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