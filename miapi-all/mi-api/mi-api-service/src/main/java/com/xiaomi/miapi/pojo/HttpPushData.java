package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class HttpPushData {
    private Integer id;

    private String address;

    private String httpapimoduleinfo;

    private String httpapimodulelistandapiinfo;

    private String httpapiparamsresponseinfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getHttpapimoduleinfo() {
        return httpapimoduleinfo;
    }

    public void setHttpapimoduleinfo(String httpapimoduleinfo) {
        this.httpapimoduleinfo = httpapimoduleinfo == null ? null : httpapimoduleinfo.trim();
    }

    public String getHttpapimodulelistandapiinfo() {
        return httpapimodulelistandapiinfo;
    }

    public void setHttpapimodulelistandapiinfo(String httpapimodulelistandapiinfo) {
        this.httpapimodulelistandapiinfo = httpapimodulelistandapiinfo == null ? null : httpapimodulelistandapiinfo.trim();
    }

    public String getHttpapiparamsresponseinfo() {
        return httpapiparamsresponseinfo;
    }

    public void setHttpapiparamsresponseinfo(String httpapiparamsresponseinfo) {
        this.httpapiparamsresponseinfo = httpapiparamsresponseinfo == null ? null : httpapiparamsresponseinfo.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        address("address", "address", "VARCHAR", false),
        httpapimoduleinfo("httpApiModuleInfo", "httpapimoduleinfo", "LONGVARCHAR", false),
        httpapimodulelistandapiinfo("httpApiModuleListAndApiInfo", "httpapimodulelistandapiinfo", "LONGVARCHAR", false),
        httpapiparamsresponseinfo("httpApiParamsResponseInfo", "httpapiparamsresponseinfo", "LONGVARCHAR", false);

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