package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppAlarmRuleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public AppAlarmRuleExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIsNull() {
            addCriterion("alarm_id is null");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIsNotNull() {
            addCriterion("alarm_id is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmIdEqualTo(Integer value) {
            addCriterion("alarm_id =", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotEqualTo(Integer value) {
            addCriterion("alarm_id <>", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdGreaterThan(Integer value) {
            addCriterion("alarm_id >", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("alarm_id >=", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdLessThan(Integer value) {
            addCriterion("alarm_id <", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdLessThanOrEqualTo(Integer value) {
            addCriterion("alarm_id <=", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIn(List<Integer> values) {
            addCriterion("alarm_id in", values, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotIn(List<Integer> values) {
            addCriterion("alarm_id not in", values, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdBetween(Integer value1, Integer value2) {
            addCriterion("alarm_id between", value1, value2, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotBetween(Integer value1, Integer value2) {
            addCriterion("alarm_id not between", value1, value2, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlertIsNull() {
            addCriterion("alert is null");
            return (Criteria) this;
        }

        public Criteria andAlertIsNotNull() {
            addCriterion("alert is not null");
            return (Criteria) this;
        }

        public Criteria andAlertEqualTo(String value) {
            addCriterion("alert =", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertNotEqualTo(String value) {
            addCriterion("alert <>", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertGreaterThan(String value) {
            addCriterion("alert >", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertGreaterThanOrEqualTo(String value) {
            addCriterion("alert >=", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertLessThan(String value) {
            addCriterion("alert <", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertLessThanOrEqualTo(String value) {
            addCriterion("alert <=", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertLike(String value) {
            addCriterion("alert like", value, "alert");
            return (Criteria) this;
        }

        public Criteria andLabelsLike(String value) {
            addCriterion("labels like", value, "labels");
            return (Criteria) this;
        }

        public Criteria andAlertNotLike(String value) {
            addCriterion("alert not like", value, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertIn(List<String> values) {
            addCriterion("alert in", values, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertNotIn(List<String> values) {
            addCriterion("alert not in", values, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertBetween(String value1, String value2) {
            addCriterion("alert between", value1, value2, "alert");
            return (Criteria) this;
        }

        public Criteria andAlertNotBetween(String value1, String value2) {
            addCriterion("alert not between", value1, value2, "alert");
            return (Criteria) this;
        }

        public Criteria andCnameIsNull() {
            addCriterion("cname is null");
            return (Criteria) this;
        }

        public Criteria andCnameIsNotNull() {
            addCriterion("cname is not null");
            return (Criteria) this;
        }

        public Criteria andCnameEqualTo(String value) {
            addCriterion("cname =", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameNotEqualTo(String value) {
            addCriterion("cname <>", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameGreaterThan(String value) {
            addCriterion("cname >", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameGreaterThanOrEqualTo(String value) {
            addCriterion("cname >=", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameLessThan(String value) {
            addCriterion("cname <", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameLessThanOrEqualTo(String value) {
            addCriterion("cname <=", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameLike(String value) {
            addCriterion("cname like", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameNotLike(String value) {
            addCriterion("cname not like", value, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameIn(List<String> values) {
            addCriterion("cname in", values, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameNotIn(List<String> values) {
            addCriterion("cname not in", values, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameBetween(String value1, String value2) {
            addCriterion("cname between", value1, value2, "cname");
            return (Criteria) this;
        }

        public Criteria andCnameNotBetween(String value1, String value2) {
            addCriterion("cname not between", value1, value2, "cname");
            return (Criteria) this;
        }

        public Criteria andMetricTypeIsNull() {
            addCriterion("metric_type is null");
            return (Criteria) this;
        }

        public Criteria andMetricTypeIsNotNull() {
            addCriterion("metric_type is not null");
            return (Criteria) this;
        }

        public Criteria andMetricTypeEqualTo(Integer value) {
            addCriterion("metric_type =", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeNotEqualTo(Integer value) {
            addCriterion("metric_type <>", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeGreaterThan(Integer value) {
            addCriterion("metric_type >", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("metric_type >=", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeLessThan(Integer value) {
            addCriterion("metric_type <", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeLessThanOrEqualTo(Integer value) {
            addCriterion("metric_type <=", value, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeIn(List<Integer> values) {
            addCriterion("metric_type in", values, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeNotIn(List<Integer> values) {
            addCriterion("metric_type not in", values, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeBetween(Integer value1, Integer value2) {
            addCriterion("metric_type between", value1, value2, "metricType");
            return (Criteria) this;
        }

        public Criteria andMetricTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("metric_type not between", value1, value2, "metricType");
            return (Criteria) this;
        }

        public Criteria andForTimeIsNull() {
            addCriterion("for_time is null");
            return (Criteria) this;
        }

        public Criteria andForTimeIsNotNull() {
            addCriterion("for_time is not null");
            return (Criteria) this;
        }

        public Criteria andForTimeEqualTo(String value) {
            addCriterion("for_time =", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeNotEqualTo(String value) {
            addCriterion("for_time <>", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeGreaterThan(String value) {
            addCriterion("for_time >", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeGreaterThanOrEqualTo(String value) {
            addCriterion("for_time >=", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeLessThan(String value) {
            addCriterion("for_time <", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeLessThanOrEqualTo(String value) {
            addCriterion("for_time <=", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeLike(String value) {
            addCriterion("for_time like", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeNotLike(String value) {
            addCriterion("for_time not like", value, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeIn(List<String> values) {
            addCriterion("for_time in", values, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeNotIn(List<String> values) {
            addCriterion("for_time not in", values, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeBetween(String value1, String value2) {
            addCriterion("for_time between", value1, value2, "forTime");
            return (Criteria) this;
        }

        public Criteria andForTimeNotBetween(String value1, String value2) {
            addCriterion("for_time not between", value1, value2, "forTime");
            return (Criteria) this;
        }

        public Criteria andAnnotationsIsNull() {
            addCriterion("annotations is null");
            return (Criteria) this;
        }

        public Criteria andAnnotationsIsNotNull() {
            addCriterion("annotations is not null");
            return (Criteria) this;
        }

        public Criteria andAnnotationsEqualTo(String value) {
            addCriterion("annotations =", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsNotEqualTo(String value) {
            addCriterion("annotations <>", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsGreaterThan(String value) {
            addCriterion("annotations >", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsGreaterThanOrEqualTo(String value) {
            addCriterion("annotations >=", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsLessThan(String value) {
            addCriterion("annotations <", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsLessThanOrEqualTo(String value) {
            addCriterion("annotations <=", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsLike(String value) {
            addCriterion("annotations like", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsNotLike(String value) {
            addCriterion("annotations not like", value, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsIn(List<String> values) {
            addCriterion("annotations in", values, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsNotIn(List<String> values) {
            addCriterion("annotations not in", values, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsBetween(String value1, String value2) {
            addCriterion("annotations between", value1, value2, "annotations");
            return (Criteria) this;
        }

        public Criteria andAnnotationsNotBetween(String value1, String value2) {
            addCriterion("annotations not between", value1, value2, "annotations");
            return (Criteria) this;
        }

        public Criteria andRuleGroupIsNull() {
            addCriterion("rule_group is null");
            return (Criteria) this;
        }

        public Criteria andRuleGroupIsNotNull() {
            addCriterion("rule_group is not null");
            return (Criteria) this;
        }

        public Criteria andRuleGroupEqualTo(String value) {
            addCriterion("rule_group =", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupNotEqualTo(String value) {
            addCriterion("rule_group <>", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupGreaterThan(String value) {
            addCriterion("rule_group >", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupGreaterThanOrEqualTo(String value) {
            addCriterion("rule_group >=", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupLessThan(String value) {
            addCriterion("rule_group <", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupLessThanOrEqualTo(String value) {
            addCriterion("rule_group <=", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupLike(String value) {
            addCriterion("rule_group like", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupNotLike(String value) {
            addCriterion("rule_group not like", value, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupIn(List<String> values) {
            addCriterion("rule_group in", values, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupNotIn(List<String> values) {
            addCriterion("rule_group not in", values, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupBetween(String value1, String value2) {
            addCriterion("rule_group between", value1, value2, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andRuleGroupNotBetween(String value1, String value2) {
            addCriterion("rule_group not between", value1, value2, "ruleGroup");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNull() {
            addCriterion("priority is null");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNotNull() {
            addCriterion("priority is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityEqualTo(String value) {
            addCriterion("priority =", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotEqualTo(String value) {
            addCriterion("priority <>", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThan(String value) {
            addCriterion("priority >", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThanOrEqualTo(String value) {
            addCriterion("priority >=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThan(String value) {
            addCriterion("priority <", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThanOrEqualTo(String value) {
            addCriterion("priority <=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLike(String value) {
            addCriterion("priority like", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotLike(String value) {
            addCriterion("priority not like", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityIn(List<String> values) {
            addCriterion("priority in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotIn(List<String> values) {
            addCriterion("priority not in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityBetween(String value1, String value2) {
            addCriterion("priority between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotBetween(String value1, String value2) {
            addCriterion("priority not between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andEnvIsNull() {
            addCriterion("env is null");
            return (Criteria) this;
        }

        public Criteria andEnvIsNotNull() {
            addCriterion("env is not null");
            return (Criteria) this;
        }

        public Criteria andEnvEqualTo(String value) {
            addCriterion("env =", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotEqualTo(String value) {
            addCriterion("env <>", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvGreaterThan(String value) {
            addCriterion("env >", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvGreaterThanOrEqualTo(String value) {
            addCriterion("env >=", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLessThan(String value) {
            addCriterion("env <", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLessThanOrEqualTo(String value) {
            addCriterion("env <=", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvLike(String value) {
            addCriterion("env like", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotLike(String value) {
            addCriterion("env not like", value, "env");
            return (Criteria) this;
        }

        public Criteria andEnvIn(List<String> values) {
            addCriterion("env in", values, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotIn(List<String> values) {
            addCriterion("env not in", values, "env");
            return (Criteria) this;
        }

        public Criteria andEnvBetween(String value1, String value2) {
            addCriterion("env between", value1, value2, "env");
            return (Criteria) this;
        }

        public Criteria andEnvNotBetween(String value1, String value2) {
            addCriterion("env not between", value1, value2, "env");
            return (Criteria) this;
        }

        public Criteria andOpIsNull() {
            addCriterion("op is null");
            return (Criteria) this;
        }

        public Criteria andOpIsNotNull() {
            addCriterion("op is not null");
            return (Criteria) this;
        }

        public Criteria andOpEqualTo(String value) {
            addCriterion("op =", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpNotEqualTo(String value) {
            addCriterion("op <>", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpGreaterThan(String value) {
            addCriterion("op >", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpGreaterThanOrEqualTo(String value) {
            addCriterion("op >=", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpLessThan(String value) {
            addCriterion("op <", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpLessThanOrEqualTo(String value) {
            addCriterion("op <=", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpLike(String value) {
            addCriterion("op like", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpNotLike(String value) {
            addCriterion("op not like", value, "op");
            return (Criteria) this;
        }

        public Criteria andOpIn(List<String> values) {
            addCriterion("op in", values, "op");
            return (Criteria) this;
        }

        public Criteria andOpNotIn(List<String> values) {
            addCriterion("op not in", values, "op");
            return (Criteria) this;
        }

        public Criteria andOpBetween(String value1, String value2) {
            addCriterion("op between", value1, value2, "op");
            return (Criteria) this;
        }

        public Criteria andOpNotBetween(String value1, String value2) {
            addCriterion("op not between", value1, value2, "op");
            return (Criteria) this;
        }

        public Criteria andValueIsNull() {
            addCriterion("value is null");
            return (Criteria) this;
        }

        public Criteria andValueIsNotNull() {
            addCriterion("value is not null");
            return (Criteria) this;
        }

        public Criteria andValueEqualTo(Float value) {
            addCriterion("value =", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotEqualTo(Float value) {
            addCriterion("value <>", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThan(Float value) {
            addCriterion("value >", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueGreaterThanOrEqualTo(Float value) {
            addCriterion("value >=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThan(Float value) {
            addCriterion("value <", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueLessThanOrEqualTo(Float value) {
            addCriterion("value <=", value, "value");
            return (Criteria) this;
        }

        public Criteria andValueIn(List<Float> values) {
            addCriterion("value in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotIn(List<Float> values) {
            addCriterion("value not in", values, "value");
            return (Criteria) this;
        }

        public Criteria andValueBetween(Float value1, Float value2) {
            addCriterion("value between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andValueNotBetween(Float value1, Float value2) {
            addCriterion("value not between", value1, value2, "value");
            return (Criteria) this;
        }

        public Criteria andDataCountIsNull() {
            addCriterion("data_count is null");
            return (Criteria) this;
        }

        public Criteria andDataCountIsNotNull() {
            addCriterion("data_count is not null");
            return (Criteria) this;
        }

        public Criteria andDataCountEqualTo(Integer value) {
            addCriterion("data_count =", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountNotEqualTo(Integer value) {
            addCriterion("data_count <>", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountGreaterThan(Integer value) {
            addCriterion("data_count >", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("data_count >=", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountLessThan(Integer value) {
            addCriterion("data_count <", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountLessThanOrEqualTo(Integer value) {
            addCriterion("data_count <=", value, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountIn(List<Integer> values) {
            addCriterion("data_count in", values, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountNotIn(List<Integer> values) {
            addCriterion("data_count not in", values, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountBetween(Integer value1, Integer value2) {
            addCriterion("data_count between", value1, value2, "dataCount");
            return (Criteria) this;
        }

        public Criteria andDataCountNotBetween(Integer value1, Integer value2) {
            addCriterion("data_count not between", value1, value2, "dataCount");
            return (Criteria) this;
        }

        public Criteria andSendIntervalIsNull() {
            addCriterion("send_interval is null");
            return (Criteria) this;
        }

        public Criteria andSendIntervalIsNotNull() {
            addCriterion("send_interval is not null");
            return (Criteria) this;
        }

        public Criteria andSendIntervalEqualTo(String value) {
            addCriterion("send_interval =", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalNotEqualTo(String value) {
            addCriterion("send_interval <>", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalGreaterThan(String value) {
            addCriterion("send_interval >", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalGreaterThanOrEqualTo(String value) {
            addCriterion("send_interval >=", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalLessThan(String value) {
            addCriterion("send_interval <", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalLessThanOrEqualTo(String value) {
            addCriterion("send_interval <=", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalLike(String value) {
            addCriterion("send_interval like", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalNotLike(String value) {
            addCriterion("send_interval not like", value, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalIn(List<String> values) {
            addCriterion("send_interval in", values, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalNotIn(List<String> values) {
            addCriterion("send_interval not in", values, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalBetween(String value1, String value2) {
            addCriterion("send_interval between", value1, value2, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andSendIntervalNotBetween(String value1, String value2) {
            addCriterion("send_interval not between", value1, value2, "sendInterval");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNull() {
            addCriterion("project_id is null");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNotNull() {
            addCriterion("project_id is not null");
            return (Criteria) this;
        }

        public Criteria andProjectIdEqualTo(Integer value) {
            addCriterion("project_id =", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotEqualTo(Integer value) {
            addCriterion("project_id <>", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThan(Integer value) {
            addCriterion("project_id >", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("project_id >=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThan(Integer value) {
            addCriterion("project_id <", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThanOrEqualTo(Integer value) {
            addCriterion("project_id <=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdIn(List<Integer> values) {
            addCriterion("project_id in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotIn(List<Integer> values) {
            addCriterion("project_id not in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdBetween(Integer value1, Integer value2) {
            addCriterion("project_id between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotBetween(Integer value1, Integer value2) {
            addCriterion("project_id not between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andIamIdIsNull() {
            addCriterion("iam_id is null");
            return (Criteria) this;
        }

        public Criteria andIamIdIsNotNull() {
            addCriterion("iam_id is not null");
            return (Criteria) this;
        }

        public Criteria andIamIdEqualTo(Integer value) {
            addCriterion("iam_id =", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdNotEqualTo(Integer value) {
            addCriterion("iam_id <>", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdGreaterThan(Integer value) {
            addCriterion("iam_id >", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("iam_id >=", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdLessThan(Integer value) {
            addCriterion("iam_id <", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdLessThanOrEqualTo(Integer value) {
            addCriterion("iam_id <=", value, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdIn(List<Integer> values) {
            addCriterion("iam_id in", values, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdNotIn(List<Integer> values) {
            addCriterion("iam_id not in", values, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdBetween(Integer value1, Integer value2) {
            addCriterion("iam_id between", value1, value2, "iamId");
            return (Criteria) this;
        }

        public Criteria andIamIdNotBetween(Integer value1, Integer value2) {
            addCriterion("iam_id not between", value1, value2, "iamId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNull() {
            addCriterion("template_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNotNull() {
            addCriterion("template_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdEqualTo(Integer value) {
            addCriterion("template_id =", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotEqualTo(Integer value) {
            addCriterion("template_id <>", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThan(Integer value) {
            addCriterion("template_id >", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("template_id >=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThan(Integer value) {
            addCriterion("template_id <", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThanOrEqualTo(Integer value) {
            addCriterion("template_id <=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIn(List<Integer> values) {
            addCriterion("template_id in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotIn(List<Integer> values) {
            addCriterion("template_id not in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdBetween(Integer value1, Integer value2) {
            addCriterion("template_id between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotBetween(Integer value1, Integer value2) {
            addCriterion("template_id not between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andRuleTypeIsNull() {
            addCriterion("rule_type is null");
            return (Criteria) this;
        }

        public Criteria andRuleTypeIsNotNull() {
            addCriterion("rule_type is not null");
            return (Criteria) this;
        }

        public Criteria andRuleTypeEqualTo(Integer value) {
            addCriterion("rule_type =", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeNotEqualTo(Integer value) {
            addCriterion("rule_type <>", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeGreaterThan(Integer value) {
            addCriterion("rule_type >", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("rule_type >=", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeLessThan(Integer value) {
            addCriterion("rule_type <", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeLessThanOrEqualTo(Integer value) {
            addCriterion("rule_type <=", value, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeIn(List<Integer> values) {
            addCriterion("rule_type in", values, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeNotIn(List<Integer> values) {
            addCriterion("rule_type not in", values, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeBetween(Integer value1, Integer value2) {
            addCriterion("rule_type between", value1, value2, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("rule_type not between", value1, value2, "ruleType");
            return (Criteria) this;
        }

        public Criteria andRuleStatusIsNull() {
            addCriterion("rule_status is null");
            return (Criteria) this;
        }

        public Criteria andRuleStatusIsNotNull() {
            addCriterion("rule_status is not null");
            return (Criteria) this;
        }

        public Criteria andRuleStatusEqualTo(Integer value) {
            addCriterion("rule_status =", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusNotEqualTo(Integer value) {
            addCriterion("rule_status <>", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusGreaterThan(Integer value) {
            addCriterion("rule_status >", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("rule_status >=", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusLessThan(Integer value) {
            addCriterion("rule_status <", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusLessThanOrEqualTo(Integer value) {
            addCriterion("rule_status <=", value, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusIn(List<Integer> values) {
            addCriterion("rule_status in", values, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusNotIn(List<Integer> values) {
            addCriterion("rule_status not in", values, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusBetween(Integer value1, Integer value2) {
            addCriterion("rule_status between", value1, value2, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRuleStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("rule_status not between", value1, value2, "ruleStatus");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andCreaterIsNull() {
            addCriterion("creater is null");
            return (Criteria) this;
        }

        public Criteria andCreaterIsNotNull() {
            addCriterion("creater is not null");
            return (Criteria) this;
        }

        public Criteria andCreaterEqualTo(String value) {
            addCriterion("creater =", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterNotEqualTo(String value) {
            addCriterion("creater <>", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterGreaterThan(String value) {
            addCriterion("creater >", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterGreaterThanOrEqualTo(String value) {
            addCriterion("creater >=", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterLessThan(String value) {
            addCriterion("creater <", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterLessThanOrEqualTo(String value) {
            addCriterion("creater <=", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterLike(String value) {
            addCriterion("creater like", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterNotLike(String value) {
            addCriterion("creater not like", value, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterIn(List<String> values) {
            addCriterion("creater in", values, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterNotIn(List<String> values) {
            addCriterion("creater not in", values, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterBetween(String value1, String value2) {
            addCriterion("creater between", value1, value2, "creater");
            return (Criteria) this;
        }

        public Criteria andCreaterNotBetween(String value1, String value2) {
            addCriterion("creater not between", value1, value2, "creater");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStrategyIdEqualTo(Integer value) {
            addCriterion("strategy_id =", value, "strategyId");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
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
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
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
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
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