package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AlertManagerRules {
    private Integer ruleId;

    private String ruleName;

    private String ruleFn;

    private Integer ruleInterval;

    private String ruleAlert;

    private String ruleFor;

    private String ruleLabels;

    private String principal;

    private Date createTime;

    private Date updateTime;

    private String ruleExpr;

    private String ruleAnnotations;

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName == null ? null : ruleName.trim();
    }

    public String getRuleFn() {
        return ruleFn;
    }

    public void setRuleFn(String ruleFn) {
        this.ruleFn = ruleFn == null ? null : ruleFn.trim();
    }

    public Integer getRuleInterval() {
        return ruleInterval;
    }

    public void setRuleInterval(Integer ruleInterval) {
        this.ruleInterval = ruleInterval;
    }

    public String getRuleAlert() {
        return ruleAlert;
    }

    public void setRuleAlert(String ruleAlert) {
        this.ruleAlert = ruleAlert == null ? null : ruleAlert.trim();
    }

    public String getRuleFor() {
        return ruleFor;
    }

    public void setRuleFor(String ruleFor) {
        this.ruleFor = ruleFor == null ? null : ruleFor.trim();
    }

    public String getRuleLabels() {
        return ruleLabels;
    }

    public void setRuleLabels(String ruleLabels) {
        this.ruleLabels = ruleLabels == null ? null : ruleLabels.trim();
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal == null ? null : principal.trim();
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

    public String getRuleExpr() {
        return ruleExpr;
    }

    public void setRuleExpr(String ruleExpr) {
        this.ruleExpr = ruleExpr == null ? null : ruleExpr.trim();
    }

    public String getRuleAnnotations() {
        return ruleAnnotations;
    }

    public void setRuleAnnotations(String ruleAnnotations) {
        this.ruleAnnotations = ruleAnnotations == null ? null : ruleAnnotations.trim();
    }

    public enum Column {
        ruleId("rule_id", "ruleId", "INTEGER", false),
        ruleName("rule_name", "ruleName", "VARCHAR", false),
        ruleFn("rule_fn", "ruleFn", "VARCHAR", false),
        ruleInterval("rule_interval", "ruleInterval", "INTEGER", false),
        ruleAlert("rule_alert", "ruleAlert", "VARCHAR", false),
        ruleFor("rule_for", "ruleFor", "VARCHAR", false),
        ruleLabels("rule_labels", "ruleLabels", "VARCHAR", false),
        principal("principal", "principal", "VARCHAR", false),
        createTime("create_time", "createTime", "DATE", false),
        updateTime("update_time", "updateTime", "DATE", false),
        ruleExpr("rule_expr", "ruleExpr", "LONGVARCHAR", false),
        ruleAnnotations("rule_annotations", "ruleAnnotations", "LONGVARCHAR", false);

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