package com.xiaomi.mone.monitor.dao.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Data
@ToString
public class AppCapacityAutoAdjust {
    private Integer id;

    private Integer appId;

    private Integer pipelineId;

    private String container;

    private Integer status;

    private Integer minInstance;

    private Integer maxInstance;

    private Integer autoCapacity;

    private Integer dependOn;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container == null ? null : container.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMinInstance() {
        return minInstance;
    }

    public void setMinInstance(Integer minInstance) {
        this.minInstance = minInstance;
    }

    public Integer getMaxInstance() {
        return maxInstance;
    }

    public void setMaxInstance(Integer maxInstance) {
        this.maxInstance = maxInstance;
    }

    public Integer getAutoCapacity() {
        return autoCapacity;
    }

    public void setAutoCapacity(Integer autoCapacity) {
        this.autoCapacity = autoCapacity;
    }

    public Integer getDependOn() {
        return dependOn;
    }

    public void setDependOn(Integer dependOn) {
        this.dependOn = dependOn;
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
        appId("app_id", "appId", "INTEGER", false),
        pipelineId("pipeline_id", "pipelineId", "INTEGER", false),
        container("container", "container", "VARCHAR", false),
        status("status", "status", "INTEGER", false),
        minInstance("min_instance", "minInstance", "INTEGER", false),
        maxInstance("max_instance", "maxInstance", "INTEGER", false),
        autoCapacity("auto_capacity", "autoCapacity", "INTEGER", false),
        dependOn("depend_on", "dependOn", "INTEGER", false),
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