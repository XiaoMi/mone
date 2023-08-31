package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AlertManagerRulesExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public AlertManagerRulesExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit=limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset=offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andRuleIdIsNull() {
            addCriterion("rule_id is null");
            return (Criteria) this;
        }

        public Criteria andRuleIdIsNotNull() {
            addCriterion("rule_id is not null");
            return (Criteria) this;
        }

        public Criteria andRuleIdEqualTo(Integer value) {
            addCriterion("rule_id =", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdNotEqualTo(Integer value) {
            addCriterion("rule_id <>", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdGreaterThan(Integer value) {
            addCriterion("rule_id >", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("rule_id >=", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdLessThan(Integer value) {
            addCriterion("rule_id <", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdLessThanOrEqualTo(Integer value) {
            addCriterion("rule_id <=", value, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdIn(List<Integer> values) {
            addCriterion("rule_id in", values, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdNotIn(List<Integer> values) {
            addCriterion("rule_id not in", values, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdBetween(Integer value1, Integer value2) {
            addCriterion("rule_id between", value1, value2, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleIdNotBetween(Integer value1, Integer value2) {
            addCriterion("rule_id not between", value1, value2, "ruleId");
            return (Criteria) this;
        }

        public Criteria andRuleNameIsNull() {
            addCriterion("rule_name is null");
            return (Criteria) this;
        }

        public Criteria andRuleNameIsNotNull() {
            addCriterion("rule_name is not null");
            return (Criteria) this;
        }

        public Criteria andRuleNameEqualTo(String value) {
            addCriterion("rule_name =", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameNotEqualTo(String value) {
            addCriterion("rule_name <>", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameGreaterThan(String value) {
            addCriterion("rule_name >", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameGreaterThanOrEqualTo(String value) {
            addCriterion("rule_name >=", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameLessThan(String value) {
            addCriterion("rule_name <", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameLessThanOrEqualTo(String value) {
            addCriterion("rule_name <=", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameLike(String value) {
            addCriterion("rule_name like", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameNotLike(String value) {
            addCriterion("rule_name not like", value, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameIn(List<String> values) {
            addCriterion("rule_name in", values, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameNotIn(List<String> values) {
            addCriterion("rule_name not in", values, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameBetween(String value1, String value2) {
            addCriterion("rule_name between", value1, value2, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleNameNotBetween(String value1, String value2) {
            addCriterion("rule_name not between", value1, value2, "ruleName");
            return (Criteria) this;
        }

        public Criteria andRuleFnIsNull() {
            addCriterion("rule_fn is null");
            return (Criteria) this;
        }

        public Criteria andRuleFnIsNotNull() {
            addCriterion("rule_fn is not null");
            return (Criteria) this;
        }

        public Criteria andRuleFnEqualTo(String value) {
            addCriterion("rule_fn =", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnNotEqualTo(String value) {
            addCriterion("rule_fn <>", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnGreaterThan(String value) {
            addCriterion("rule_fn >", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnGreaterThanOrEqualTo(String value) {
            addCriterion("rule_fn >=", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnLessThan(String value) {
            addCriterion("rule_fn <", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnLessThanOrEqualTo(String value) {
            addCriterion("rule_fn <=", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnLike(String value) {
            addCriterion("rule_fn like", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnNotLike(String value) {
            addCriterion("rule_fn not like", value, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnIn(List<String> values) {
            addCriterion("rule_fn in", values, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnNotIn(List<String> values) {
            addCriterion("rule_fn not in", values, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnBetween(String value1, String value2) {
            addCriterion("rule_fn between", value1, value2, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleFnNotBetween(String value1, String value2) {
            addCriterion("rule_fn not between", value1, value2, "ruleFn");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalIsNull() {
            addCriterion("rule_interval is null");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalIsNotNull() {
            addCriterion("rule_interval is not null");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalEqualTo(Integer value) {
            addCriterion("rule_interval =", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalNotEqualTo(Integer value) {
            addCriterion("rule_interval <>", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalGreaterThan(Integer value) {
            addCriterion("rule_interval >", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalGreaterThanOrEqualTo(Integer value) {
            addCriterion("rule_interval >=", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalLessThan(Integer value) {
            addCriterion("rule_interval <", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalLessThanOrEqualTo(Integer value) {
            addCriterion("rule_interval <=", value, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalIn(List<Integer> values) {
            addCriterion("rule_interval in", values, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalNotIn(List<Integer> values) {
            addCriterion("rule_interval not in", values, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalBetween(Integer value1, Integer value2) {
            addCriterion("rule_interval between", value1, value2, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleIntervalNotBetween(Integer value1, Integer value2) {
            addCriterion("rule_interval not between", value1, value2, "ruleInterval");
            return (Criteria) this;
        }

        public Criteria andRuleAlertIsNull() {
            addCriterion("rule_alert is null");
            return (Criteria) this;
        }

        public Criteria andRuleAlertIsNotNull() {
            addCriterion("rule_alert is not null");
            return (Criteria) this;
        }

        public Criteria andRuleAlertEqualTo(String value) {
            addCriterion("rule_alert =", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertNotEqualTo(String value) {
            addCriterion("rule_alert <>", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertGreaterThan(String value) {
            addCriterion("rule_alert >", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertGreaterThanOrEqualTo(String value) {
            addCriterion("rule_alert >=", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertLessThan(String value) {
            addCriterion("rule_alert <", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertLessThanOrEqualTo(String value) {
            addCriterion("rule_alert <=", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertLike(String value) {
            addCriterion("rule_alert like", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertNotLike(String value) {
            addCriterion("rule_alert not like", value, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertIn(List<String> values) {
            addCriterion("rule_alert in", values, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertNotIn(List<String> values) {
            addCriterion("rule_alert not in", values, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertBetween(String value1, String value2) {
            addCriterion("rule_alert between", value1, value2, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleAlertNotBetween(String value1, String value2) {
            addCriterion("rule_alert not between", value1, value2, "ruleAlert");
            return (Criteria) this;
        }

        public Criteria andRuleForIsNull() {
            addCriterion("rule_for is null");
            return (Criteria) this;
        }

        public Criteria andRuleForIsNotNull() {
            addCriterion("rule_for is not null");
            return (Criteria) this;
        }

        public Criteria andRuleForEqualTo(String value) {
            addCriterion("rule_for =", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForNotEqualTo(String value) {
            addCriterion("rule_for <>", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForGreaterThan(String value) {
            addCriterion("rule_for >", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForGreaterThanOrEqualTo(String value) {
            addCriterion("rule_for >=", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForLessThan(String value) {
            addCriterion("rule_for <", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForLessThanOrEqualTo(String value) {
            addCriterion("rule_for <=", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForLike(String value) {
            addCriterion("rule_for like", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForNotLike(String value) {
            addCriterion("rule_for not like", value, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForIn(List<String> values) {
            addCriterion("rule_for in", values, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForNotIn(List<String> values) {
            addCriterion("rule_for not in", values, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForBetween(String value1, String value2) {
            addCriterion("rule_for between", value1, value2, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleForNotBetween(String value1, String value2) {
            addCriterion("rule_for not between", value1, value2, "ruleFor");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsIsNull() {
            addCriterion("rule_labels is null");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsIsNotNull() {
            addCriterion("rule_labels is not null");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsEqualTo(String value) {
            addCriterion("rule_labels =", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsNotEqualTo(String value) {
            addCriterion("rule_labels <>", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsGreaterThan(String value) {
            addCriterion("rule_labels >", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsGreaterThanOrEqualTo(String value) {
            addCriterion("rule_labels >=", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsLessThan(String value) {
            addCriterion("rule_labels <", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsLessThanOrEqualTo(String value) {
            addCriterion("rule_labels <=", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsLike(String value) {
            addCriterion("rule_labels like", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsNotLike(String value) {
            addCriterion("rule_labels not like", value, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsIn(List<String> values) {
            addCriterion("rule_labels in", values, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsNotIn(List<String> values) {
            addCriterion("rule_labels not in", values, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsBetween(String value1, String value2) {
            addCriterion("rule_labels between", value1, value2, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andRuleLabelsNotBetween(String value1, String value2) {
            addCriterion("rule_labels not between", value1, value2, "ruleLabels");
            return (Criteria) this;
        }

        public Criteria andPrincipalIsNull() {
            addCriterion("principal is null");
            return (Criteria) this;
        }

        public Criteria andPrincipalIsNotNull() {
            addCriterion("principal is not null");
            return (Criteria) this;
        }

        public Criteria andPrincipalEqualTo(String value) {
            addCriterion("principal =", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalNotEqualTo(String value) {
            addCriterion("principal <>", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalGreaterThan(String value) {
            addCriterion("principal >", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalGreaterThanOrEqualTo(String value) {
            addCriterion("principal >=", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalLessThan(String value) {
            addCriterion("principal <", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalLessThanOrEqualTo(String value) {
            addCriterion("principal <=", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalLike(String value) {
            addCriterion("principal like", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalNotLike(String value) {
            addCriterion("principal not like", value, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalIn(List<String> values) {
            addCriterion("principal in", values, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalNotIn(List<String> values) {
            addCriterion("principal not in", values, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalBetween(String value1, String value2) {
            addCriterion("principal between", value1, value2, "principal");
            return (Criteria) this;
        }

        public Criteria andPrincipalNotBetween(String value1, String value2) {
            addCriterion("principal not between", value1, value2, "principal");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterionForJDBCDate("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterionForJDBCDate("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterionForJDBCDate("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterionForJDBCDate("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterionForJDBCDate("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterionForJDBCDate("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterionForJDBCDate("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterionForJDBCDate("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterionForJDBCDate("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterionForJDBCDate("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterionForJDBCDate("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterionForJDBCDate("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}