package com.xiaomi.mone.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 12:05
 */
@Data
@TableName("hera_app_base_info")
public class HeraAppBaseInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String bindId;

    private Integer bindType;

    private String appName;

    private String appCname;

    private Integer appType;

    private String appLanguage;

    private Integer platformType;

    private String appSignId;

    private Integer iamTreeId;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String envsMap;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId == null ? null : bindId.trim();
    }

    public Integer getBindType() {
        return bindType;
    }

    public void setBindType(Integer bindType) {
        this.bindType = bindType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getAppCname() {
        return appCname;
    }

    public void setAppCname(String appCname) {
        this.appCname = appCname == null ? null : appCname.trim();
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public void setAppLanguage(String appLanguage) {
        this.appLanguage = appLanguage == null ? null : appLanguage.trim();
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public String getAppSignId() {
        return appSignId;
    }

    public void setAppSignId(String appSignId) {
        this.appSignId = appSignId == null ? null : appSignId.trim();
    }

    public Integer getIamTreeId() {
        return iamTreeId;
    }

    public void setIamTreeId(Integer iamTreeId) {
        this.iamTreeId = iamTreeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getEnvsMap() {
        return envsMap;
    }

    public void setEnvsMap(String envsMap) {
        this.envsMap = envsMap == null ? null : envsMap.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        bindId("bind_id", "bindId", "VARCHAR", false),
        bindType("bind_type", "bindType", "INTEGER", false),
        appName("app_name", "appName", "VARCHAR", false),
        appCname("app_cname", "appCname", "VARCHAR", false),
        appType("app_type", "appType", "INTEGER", false),
        appLanguage("app_language", "appLanguage", "VARCHAR", false),
        platformType("platform_type", "platformType", "INTEGER", false),
        appSignId("app_sign_id", "appSignId", "VARCHAR", false),
        iamTreeId("iam_tree_id", "iamTreeId", "INTEGER", false),
        status("status", "status", "INTEGER", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        envsMap("envs_map", "envsMap", "LONGVARCHAR", false);

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

        public static Column[] excludes(Column... excludes) {
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

    public AppBaseInfo toAppBaseInfo() {
        AppBaseInfo appBaseInfo = new AppBaseInfo();
        appBaseInfo.setId(this.id);
        appBaseInfo.setBindId(this.bindId);
        appBaseInfo.setAppName(this.appName);
        appBaseInfo.setAppCname(this.appCname);
        appBaseInfo.setPlatformType(this.platformType);
        appBaseInfo.setAppType(this.appType);
        return appBaseInfo;
    }
}
