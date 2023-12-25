package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class SlaRule {
    private Integer id;

    private Integer slaId;

    private String name;

    private String ruleItemType;

    private String ruleItem;

    private String compareCondition;

    private Integer compareValue;

    private Integer degree;

    private String actionLevel;

    private Long ctime;

    private Long utime;

    private String creator;

    private String updater;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSlaId() {
        return slaId;
    }

    public void setSlaId(Integer slaId) {
        this.slaId = slaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRuleItemType() {
        return ruleItemType;
    }

    public void setRuleItemType(String ruleItemType) {
        this.ruleItemType = ruleItemType == null ? null : ruleItemType.trim();
    }

    public String getRuleItem() {
        return ruleItem;
    }

    public void setRuleItem(String ruleItem) {
        this.ruleItem = ruleItem == null ? null : ruleItem.trim();
    }

    public String getCompareCondition() {
        return compareCondition;
    }

    public void setCompareCondition(String compareCondition) {
        this.compareCondition = compareCondition == null ? null : compareCondition.trim();
    }

    public Integer getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(Integer compareValue) {
        this.compareValue = compareValue;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public String getActionLevel() {
        return actionLevel;
    }

    public void setActionLevel(String actionLevel) {
        this.actionLevel = actionLevel == null ? null : actionLevel.trim();
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater == null ? null : updater.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        slaId("sla_id", "slaId", "INTEGER", false),
        name("name", "name", "VARCHAR", false),
        ruleItemType("rule_item_type", "ruleItemType", "VARCHAR", false),
        ruleItem("rule_item", "ruleItem", "VARCHAR", false),
        compareCondition("compare_condition", "compareCondition", "VARCHAR", false),
        compareValue("compare_value", "compareValue", "INTEGER", false),
        degree("degree", "degree", "INTEGER", false),
        actionLevel("action_level", "actionLevel", "VARCHAR", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        creator("creator", "creator", "VARCHAR", false),
        updater("updater", "updater", "VARCHAR", false);

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