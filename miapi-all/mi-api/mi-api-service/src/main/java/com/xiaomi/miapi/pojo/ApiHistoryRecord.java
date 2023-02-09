package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ApiHistoryRecord {
    private Integer id;

    private Integer projectId;

    private Integer groupId;

    private Integer apiId;

    private Integer apiProtocal;

    private String updateUser;

    private Date updateTime;

    private String updateMsg;

    private Boolean isNow;

    private String apiHistiryJson;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg == null ? null : updateMsg.trim();
    }

    public Boolean getIsNow() {
        return isNow;
    }

    public void setIsNow(Boolean isNow) {
        this.isNow = isNow;
    }

    public String getApiHistiryJson() {
        return apiHistiryJson;
    }

    public void setApiHistiryJson(String apiHistiryJson) {
        this.apiHistiryJson = apiHistiryJson == null ? null : apiHistiryJson.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        projectId("project_id", "projectId", "INTEGER", false),
        groupId("group_id", "groupId", "INTEGER", false),
        apiId("api_id", "apiId", "INTEGER", false),
        apiProtocal("api_protocal", "apiProtocal", "INTEGER", false),
        updateUser("update_user", "updateUser", "VARCHAR", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        updateMsg("update_msg", "updateMsg", "VARCHAR", false),
        isNow("is_now", "isNow", "BIT", false),
        apiHistiryJson("api_histiry_json", "apiHistiryJson", "LONGVARCHAR", false);

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