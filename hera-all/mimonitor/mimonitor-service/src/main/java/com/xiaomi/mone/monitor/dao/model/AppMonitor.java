package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AppMonitor {
    private Integer id;

    private Integer projectId;

    private Integer iamTreeId;

    private Integer iamTreeType;

    private String projectName;

    private Integer appSource;

    private String owner;

    private String careUser;

    private Integer alarmLevel;

    private Integer totalAlarm;

    private Integer exceptionNum;

    private Integer slowQueryNum;

    private Integer status;

    private Integer baseInfoId;

    private Date createTime;

    private Date updateTime;

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

    public Integer getIamTreeId() {
        return iamTreeId;
    }

    public void setIamTreeId(Integer iamTreeId) {
        this.iamTreeId = iamTreeId;
    }

    public Integer getIamTreeType() {
        return iamTreeType;
    }

    public void setIamTreeType(Integer iamTreeType) {
        this.iamTreeType = iamTreeType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    public Integer getAppSource() {
        return appSource;
    }

    public void setAppSource(Integer appSource) {
        this.appSource = appSource;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getCareUser() {
        return careUser;
    }

    public void setCareUser(String careUser) {
        this.careUser = careUser == null ? null : careUser.trim();
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Integer getTotalAlarm() {
        return totalAlarm;
    }

    public void setTotalAlarm(Integer totalAlarm) {
        this.totalAlarm = totalAlarm;
    }

    public Integer getExceptionNum() {
        return exceptionNum;
    }

    public void setExceptionNum(Integer exceptionNum) {
        this.exceptionNum = exceptionNum;
    }

    public Integer getSlowQueryNum() {
        return slowQueryNum;
    }

    public void setSlowQueryNum(Integer slowQueryNum) {
        this.slowQueryNum = slowQueryNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBaseInfoId() {
        return baseInfoId;
    }

    public void setBaseInfoId(Integer baseInfoId) {
        this.baseInfoId = baseInfoId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        projectId("project_id", "projectId", "INTEGER", false),
        iamTreeId("iam_tree_id", "iamTreeId", "INTEGER", false),
        iamTreeType("iam_tree_type", "iamTreeType", "INTEGER", false),
        projectName("project_name", "projectName", "VARCHAR", false),
        appSource("app_source", "appSource", "INTEGER", false),
        owner("owner", "owner", "VARCHAR", false),
        careUser("care_user", "careUser", "VARCHAR", false),
        alarmLevel("alarm_level", "alarmLevel", "INTEGER", false),
        totalAlarm("total_alarm", "totalAlarm", "INTEGER", false),
        exceptionNum("exception_num", "exceptionNum", "INTEGER", false),
        slowQueryNum("slow_query_num", "slowQueryNum", "INTEGER", false),
        status("status", "status", "INTEGER", false),
        baseInfoId("base_info_id", "baseInfoId", "INTEGER", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false);

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