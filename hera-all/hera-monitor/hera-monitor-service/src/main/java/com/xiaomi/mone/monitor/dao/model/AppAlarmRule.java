package com.xiaomi.mone.monitor.dao.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Data
@ToString
public class AppAlarmRule {
    private Integer id;

    private Integer alarmId;

    private String alert;

    private String cname;

    private Integer metricType;

    private String forTime;

    private String annotations;

    private String ruleGroup;

    private String priority;

    private String env;

    private String op;

    private Float value;

    private Integer dataCount;

    private String sendInterval;

    private Integer projectId;

    private Integer iamId;

    private Integer templateId;

    private Integer ruleType;

    private Integer ruleStatus;

    private String remark;

    private String creater;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String expr;

    private String labels;

    private String alertTeam;

    private Integer strategyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert == null ? null : alert.trim();
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname == null ? null : cname.trim();
    }

    public Integer getMetricType() {
        return metricType;
    }

    public void setMetricType(Integer metricType) {
        this.metricType = metricType;
    }

    public String getForTime() {
        return forTime;
    }

    public void setForTime(String forTime) {
        this.forTime = forTime == null ? null : forTime.trim();
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations == null ? null : annotations.trim();
    }

    public String getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(String ruleGroup) {
        this.ruleGroup = ruleGroup == null ? null : ruleGroup.trim();
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority == null ? null : priority.trim();
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env == null ? null : env.trim();
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op == null ? null : op.trim();
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public String getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(String sendInterval) {
        this.sendInterval = sendInterval == null ? null : sendInterval.trim();
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getIamId() {
        return iamId;
    }

    public void setIamId(Integer iamId) {
        this.iamId = iamId;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(Integer ruleStatus) {
        this.ruleStatus = ruleStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
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

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr == null ? null : expr.trim();
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels == null ? null : labels.trim();
    }

    public String getAlertTeam() {
        return alertTeam;
    }

    public void setAlertTeam(String alertTeam) {
        this.alertTeam = alertTeam == null ? null : alertTeam.trim();
    }

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        alarmId("alarm_id", "alarmId", "INTEGER", false),
        alert("alert", "alert", "VARCHAR", false),
        cname("cname", "cname", "VARCHAR", false),
        metricType("metric_type", "metricType", "INTEGER", false),
        forTime("for_time", "forTime", "VARCHAR", false),
        annotations("annotations", "annotations", "VARCHAR", false),
        ruleGroup("rule_group", "ruleGroup", "VARCHAR", false),
        priority("priority", "priority", "VARCHAR", false),
        env("env", "env", "VARCHAR", false),
        op("op", "op", "VARCHAR", false),
        value("value", "value", "REAL", false),
        dataCount("data_count", "dataCount", "INTEGER", false),
        sendInterval("send_interval", "sendInterval", "VARCHAR", false),
        projectId("project_id", "projectId", "INTEGER", false),
        iamId("iam_id", "iamId", "INTEGER", false),
        templateId("template_id", "templateId", "INTEGER", false),
        ruleType("rule_type", "ruleType", "INTEGER", false),
        ruleStatus("rule_status", "ruleStatus", "INTEGER", false),
        remark("remark", "remark", "VARCHAR", false),
        creater("creater", "creater", "VARCHAR", false),
        status("status", "status", "INTEGER", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        expr("expr", "expr", "LONGVARCHAR", false),
        labels("labels", "labels", "LONGVARCHAR", false),
        alertTeam("alert_team", "alertTeam", "LONGVARCHAR", false),
        strategyId("strategy_id", "strategyId", "INTEGER", false);

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

    @Override
    public String toString() {
        return "AppAlarmRule{" +
                "id=" + id +
                ", alarmId=" + alarmId +
                ", alert='" + alert + '\'' +
                ", cname='" + cname + '\'' +
                ", metricType=" + metricType +
                ", forTime='" + forTime + '\'' +
                ", annotations='" + annotations + '\'' +
                ", ruleGroup='" + ruleGroup + '\'' +
                ", priority='" + priority + '\'' +
                ", env='" + env + '\'' +
                ", op='" + op + '\'' +
                ", value=" + value +
                ", dataCount=" + dataCount +
                ", sendInterval='" + sendInterval + '\'' +
                ", projectId=" + projectId +
                ", iamId=" + iamId +
                ", templateId=" + templateId +
                ", ruleType=" + ruleType +
                ", ruleStatus=" + ruleStatus +
                ", remark='" + remark + '\'' +
                ", creater='" + creater + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", expr='" + expr + '\'' +
                ", labels='" + labels + '\'' +
                ", alertTeam='" + alertTeam + '\'' +
                ", strategyId='" + strategyId + '\'' +
                '}';
    }
}