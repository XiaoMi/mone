package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class EoDubboApiInfo {
    private Integer id;

    private String apiname;

    private String apidocname;

    private String apiversion;

    private String apigroup;

    private String description;

    private String apirespdec;

    private String apimodelclass;

    private Boolean async;

    private String methodparaminfo;

    private String request;

    private String response;

    private String errorcodes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiname() {
        return apiname;
    }

    public void setApiname(String apiname) {
        this.apiname = apiname == null ? null : apiname.trim();
    }

    public String getApidocname() {
        return apidocname;
    }

    public void setApidocname(String apidocname) {
        this.apidocname = apidocname == null ? null : apidocname.trim();
    }

    public String getApiversion() {
        return apiversion;
    }

    public void setApiversion(String apiversion) {
        this.apiversion = apiversion == null ? null : apiversion.trim();
    }

    public String getApigroup() {
        return apigroup;
    }

    public void setApigroup(String apigroup) {
        this.apigroup = apigroup == null ? null : apigroup.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request == null ? null : request.trim();
    }

    public String getApirespdec() {
        return apirespdec;
    }

    public void setApirespdec(String apirespdec) {
        this.apirespdec = apirespdec == null ? null : apirespdec.trim();
    }

    public String getApimodelclass() {
        return apimodelclass;
    }

    public void setApimodelclass(String apimodelclass) {
        this.apimodelclass = apimodelclass == null ? null : apimodelclass.trim();
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getMethodparaminfo() {
        return methodparaminfo;
    }

    public void setMethodparaminfo(String methodparaminfo) {
        this.methodparaminfo = methodparaminfo == null ? null : methodparaminfo.trim();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response == null ? null : response.trim();
    }

    public String getErrorcodes() {
        return errorcodes;
    }

    public void setErrorcodes(String errorcodes) {
        this.errorcodes = errorcodes == null ? null : errorcodes.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        apiname("apiName", "apiname", "VARCHAR", false),
        apidocname("apiDocName", "apidocname", "VARCHAR", false),
        apiversion("apiVersion", "apiversion", "VARCHAR", false),
        apigroup("apiGroup", "apigroup", "VARCHAR", false),
        description("description", "description", "VARCHAR", false),
        apirespdec("apiRespDec", "apirespdec", "VARCHAR", false),
        apimodelclass("apiModelClass", "apimodelclass", "VARCHAR", false),
        async("async", "async", "BIT", false),
        methodparaminfo("methodParamInfo", "methodparaminfo", "LONGVARCHAR", false),
        response("response", "response", "LONGVARCHAR", false),
        request("request", "request", "LONGVARCHAR", false),
        errorcodes("errorCodes", "errorcodes", "LONGVARCHAR", false);

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