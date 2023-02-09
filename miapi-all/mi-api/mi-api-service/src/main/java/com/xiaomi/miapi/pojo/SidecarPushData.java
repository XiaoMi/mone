package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class SidecarPushData {
    private Integer id;

    private String address;

    private String sidecarapimoduleinfo;

    private String sidecarapimodulelistandapiinfo;

    private String sidecarapiparamsresponseinfo;

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

    public String getSidecarapimoduleinfo() {
        return sidecarapimoduleinfo;
    }

    public void setSidecarapimoduleinfo(String sidecarapimoduleinfo) {
        this.sidecarapimoduleinfo = sidecarapimoduleinfo == null ? null : sidecarapimoduleinfo.trim();
    }

    public String getSidecarapimodulelistandapiinfo() {
        return sidecarapimodulelistandapiinfo;
    }

    public void setSidecarapimodulelistandapiinfo(String sidecarapimodulelistandapiinfo) {
        this.sidecarapimodulelistandapiinfo = sidecarapimodulelistandapiinfo == null ? null : sidecarapimodulelistandapiinfo.trim();
    }

    public String getSidecarapiparamsresponseinfo() {
        return sidecarapiparamsresponseinfo;
    }

    public void setSidecarapiparamsresponseinfo(String sidecarapiparamsresponseinfo) {
        this.sidecarapiparamsresponseinfo = sidecarapiparamsresponseinfo == null ? null : sidecarapiparamsresponseinfo.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        address("address", "address", "VARCHAR", false),
        sidecarapimoduleinfo("sidecarApiModuleInfo", "sidecarapimoduleinfo", "LONGVARCHAR", false),
        sidecarapimodulelistandapiinfo("sidecarApiModuleListAndApiInfo", "sidecarapimodulelistandapiinfo", "LONGVARCHAR", false),
        sidecarapiparamsresponseinfo("sidecarApiParamsResponseInfo", "sidecarapiparamsresponseinfo", "LONGVARCHAR", false);

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