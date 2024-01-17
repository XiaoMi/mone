package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class SlaRuleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SlaRuleExample() {
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

        public Criteria andSlaIdIsNull() {
            addCriterion("sla_id is null");
            return (Criteria) this;
        }

        public Criteria andSlaIdIsNotNull() {
            addCriterion("sla_id is not null");
            return (Criteria) this;
        }

        public Criteria andSlaIdEqualTo(Integer value) {
            addCriterion("sla_id =", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdNotEqualTo(Integer value) {
            addCriterion("sla_id <>", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdGreaterThan(Integer value) {
            addCriterion("sla_id >", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sla_id >=", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdLessThan(Integer value) {
            addCriterion("sla_id <", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdLessThanOrEqualTo(Integer value) {
            addCriterion("sla_id <=", value, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdIn(List<Integer> values) {
            addCriterion("sla_id in", values, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdNotIn(List<Integer> values) {
            addCriterion("sla_id not in", values, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdBetween(Integer value1, Integer value2) {
            addCriterion("sla_id between", value1, value2, "slaId");
            return (Criteria) this;
        }

        public Criteria andSlaIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sla_id not between", value1, value2, "slaId");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeIsNull() {
            addCriterion("rule_item_type is null");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeIsNotNull() {
            addCriterion("rule_item_type is not null");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeEqualTo(String value) {
            addCriterion("rule_item_type =", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeNotEqualTo(String value) {
            addCriterion("rule_item_type <>", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeGreaterThan(String value) {
            addCriterion("rule_item_type >", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeGreaterThanOrEqualTo(String value) {
            addCriterion("rule_item_type >=", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeLessThan(String value) {
            addCriterion("rule_item_type <", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeLessThanOrEqualTo(String value) {
            addCriterion("rule_item_type <=", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeLike(String value) {
            addCriterion("rule_item_type like", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeNotLike(String value) {
            addCriterion("rule_item_type not like", value, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeIn(List<String> values) {
            addCriterion("rule_item_type in", values, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeNotIn(List<String> values) {
            addCriterion("rule_item_type not in", values, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeBetween(String value1, String value2) {
            addCriterion("rule_item_type between", value1, value2, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemTypeNotBetween(String value1, String value2) {
            addCriterion("rule_item_type not between", value1, value2, "ruleItemType");
            return (Criteria) this;
        }

        public Criteria andRuleItemIsNull() {
            addCriterion("rule_item is null");
            return (Criteria) this;
        }

        public Criteria andRuleItemIsNotNull() {
            addCriterion("rule_item is not null");
            return (Criteria) this;
        }

        public Criteria andRuleItemEqualTo(String value) {
            addCriterion("rule_item =", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemNotEqualTo(String value) {
            addCriterion("rule_item <>", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemGreaterThan(String value) {
            addCriterion("rule_item >", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemGreaterThanOrEqualTo(String value) {
            addCriterion("rule_item >=", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemLessThan(String value) {
            addCriterion("rule_item <", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemLessThanOrEqualTo(String value) {
            addCriterion("rule_item <=", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemLike(String value) {
            addCriterion("rule_item like", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemNotLike(String value) {
            addCriterion("rule_item not like", value, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemIn(List<String> values) {
            addCriterion("rule_item in", values, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemNotIn(List<String> values) {
            addCriterion("rule_item not in", values, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemBetween(String value1, String value2) {
            addCriterion("rule_item between", value1, value2, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andRuleItemNotBetween(String value1, String value2) {
            addCriterion("rule_item not between", value1, value2, "ruleItem");
            return (Criteria) this;
        }

        public Criteria andCompareConditionIsNull() {
            addCriterion("compare_condition is null");
            return (Criteria) this;
        }

        public Criteria andCompareConditionIsNotNull() {
            addCriterion("compare_condition is not null");
            return (Criteria) this;
        }

        public Criteria andCompareConditionEqualTo(String value) {
            addCriterion("compare_condition =", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionNotEqualTo(String value) {
            addCriterion("compare_condition <>", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionGreaterThan(String value) {
            addCriterion("compare_condition >", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionGreaterThanOrEqualTo(String value) {
            addCriterion("compare_condition >=", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionLessThan(String value) {
            addCriterion("compare_condition <", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionLessThanOrEqualTo(String value) {
            addCriterion("compare_condition <=", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionLike(String value) {
            addCriterion("compare_condition like", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionNotLike(String value) {
            addCriterion("compare_condition not like", value, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionIn(List<String> values) {
            addCriterion("compare_condition in", values, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionNotIn(List<String> values) {
            addCriterion("compare_condition not in", values, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionBetween(String value1, String value2) {
            addCriterion("compare_condition between", value1, value2, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareConditionNotBetween(String value1, String value2) {
            addCriterion("compare_condition not between", value1, value2, "compareCondition");
            return (Criteria) this;
        }

        public Criteria andCompareValueIsNull() {
            addCriterion("compare_value is null");
            return (Criteria) this;
        }

        public Criteria andCompareValueIsNotNull() {
            addCriterion("compare_value is not null");
            return (Criteria) this;
        }

        public Criteria andCompareValueEqualTo(Integer value) {
            addCriterion("compare_value =", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueNotEqualTo(Integer value) {
            addCriterion("compare_value <>", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueGreaterThan(Integer value) {
            addCriterion("compare_value >", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("compare_value >=", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueLessThan(Integer value) {
            addCriterion("compare_value <", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueLessThanOrEqualTo(Integer value) {
            addCriterion("compare_value <=", value, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueIn(List<Integer> values) {
            addCriterion("compare_value in", values, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueNotIn(List<Integer> values) {
            addCriterion("compare_value not in", values, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueBetween(Integer value1, Integer value2) {
            addCriterion("compare_value between", value1, value2, "compareValue");
            return (Criteria) this;
        }

        public Criteria andCompareValueNotBetween(Integer value1, Integer value2) {
            addCriterion("compare_value not between", value1, value2, "compareValue");
            return (Criteria) this;
        }

        public Criteria andDegreeIsNull() {
            addCriterion("degree is null");
            return (Criteria) this;
        }

        public Criteria andDegreeIsNotNull() {
            addCriterion("degree is not null");
            return (Criteria) this;
        }

        public Criteria andDegreeEqualTo(Integer value) {
            addCriterion("degree =", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeNotEqualTo(Integer value) {
            addCriterion("degree <>", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeGreaterThan(Integer value) {
            addCriterion("degree >", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeGreaterThanOrEqualTo(Integer value) {
            addCriterion("degree >=", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeLessThan(Integer value) {
            addCriterion("degree <", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeLessThanOrEqualTo(Integer value) {
            addCriterion("degree <=", value, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeIn(List<Integer> values) {
            addCriterion("degree in", values, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeNotIn(List<Integer> values) {
            addCriterion("degree not in", values, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeBetween(Integer value1, Integer value2) {
            addCriterion("degree between", value1, value2, "degree");
            return (Criteria) this;
        }

        public Criteria andDegreeNotBetween(Integer value1, Integer value2) {
            addCriterion("degree not between", value1, value2, "degree");
            return (Criteria) this;
        }

        public Criteria andActionLevelIsNull() {
            addCriterion("action_level is null");
            return (Criteria) this;
        }

        public Criteria andActionLevelIsNotNull() {
            addCriterion("action_level is not null");
            return (Criteria) this;
        }

        public Criteria andActionLevelEqualTo(String value) {
            addCriterion("action_level =", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelNotEqualTo(String value) {
            addCriterion("action_level <>", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelGreaterThan(String value) {
            addCriterion("action_level >", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelGreaterThanOrEqualTo(String value) {
            addCriterion("action_level >=", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelLessThan(String value) {
            addCriterion("action_level <", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelLessThanOrEqualTo(String value) {
            addCriterion("action_level <=", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelLike(String value) {
            addCriterion("action_level like", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelNotLike(String value) {
            addCriterion("action_level not like", value, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelIn(List<String> values) {
            addCriterion("action_level in", values, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelNotIn(List<String> values) {
            addCriterion("action_level not in", values, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelBetween(String value1, String value2) {
            addCriterion("action_level between", value1, value2, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andActionLevelNotBetween(String value1, String value2) {
            addCriterion("action_level not between", value1, value2, "actionLevel");
            return (Criteria) this;
        }

        public Criteria andCtimeIsNull() {
            addCriterion("ctime is null");
            return (Criteria) this;
        }

        public Criteria andCtimeIsNotNull() {
            addCriterion("ctime is not null");
            return (Criteria) this;
        }

        public Criteria andCtimeEqualTo(Long value) {
            addCriterion("ctime =", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotEqualTo(Long value) {
            addCriterion("ctime <>", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeGreaterThan(Long value) {
            addCriterion("ctime >", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeGreaterThanOrEqualTo(Long value) {
            addCriterion("ctime >=", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeLessThan(Long value) {
            addCriterion("ctime <", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeLessThanOrEqualTo(Long value) {
            addCriterion("ctime <=", value, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeIn(List<Long> values) {
            addCriterion("ctime in", values, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotIn(List<Long> values) {
            addCriterion("ctime not in", values, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeBetween(Long value1, Long value2) {
            addCriterion("ctime between", value1, value2, "ctime");
            return (Criteria) this;
        }

        public Criteria andCtimeNotBetween(Long value1, Long value2) {
            addCriterion("ctime not between", value1, value2, "ctime");
            return (Criteria) this;
        }

        public Criteria andUtimeIsNull() {
            addCriterion("utime is null");
            return (Criteria) this;
        }

        public Criteria andUtimeIsNotNull() {
            addCriterion("utime is not null");
            return (Criteria) this;
        }

        public Criteria andUtimeEqualTo(Long value) {
            addCriterion("utime =", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotEqualTo(Long value) {
            addCriterion("utime <>", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeGreaterThan(Long value) {
            addCriterion("utime >", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeGreaterThanOrEqualTo(Long value) {
            addCriterion("utime >=", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeLessThan(Long value) {
            addCriterion("utime <", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeLessThanOrEqualTo(Long value) {
            addCriterion("utime <=", value, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeIn(List<Long> values) {
            addCriterion("utime in", values, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotIn(List<Long> values) {
            addCriterion("utime not in", values, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeBetween(Long value1, Long value2) {
            addCriterion("utime between", value1, value2, "utime");
            return (Criteria) this;
        }

        public Criteria andUtimeNotBetween(Long value1, Long value2) {
            addCriterion("utime not between", value1, value2, "utime");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNull() {
            addCriterion("creator is null");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNotNull() {
            addCriterion("creator is not null");
            return (Criteria) this;
        }

        public Criteria andCreatorEqualTo(String value) {
            addCriterion("creator =", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotEqualTo(String value) {
            addCriterion("creator <>", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThan(String value) {
            addCriterion("creator >", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThanOrEqualTo(String value) {
            addCriterion("creator >=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThan(String value) {
            addCriterion("creator <", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThanOrEqualTo(String value) {
            addCriterion("creator <=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLike(String value) {
            addCriterion("creator like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotLike(String value) {
            addCriterion("creator not like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorIn(List<String> values) {
            addCriterion("creator in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotIn(List<String> values) {
            addCriterion("creator not in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorBetween(String value1, String value2) {
            addCriterion("creator between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotBetween(String value1, String value2) {
            addCriterion("creator not between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andUpdaterIsNull() {
            addCriterion("updater is null");
            return (Criteria) this;
        }

        public Criteria andUpdaterIsNotNull() {
            addCriterion("updater is not null");
            return (Criteria) this;
        }

        public Criteria andUpdaterEqualTo(String value) {
            addCriterion("updater =", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotEqualTo(String value) {
            addCriterion("updater <>", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterGreaterThan(String value) {
            addCriterion("updater >", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterGreaterThanOrEqualTo(String value) {
            addCriterion("updater >=", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLessThan(String value) {
            addCriterion("updater <", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLessThanOrEqualTo(String value) {
            addCriterion("updater <=", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterLike(String value) {
            addCriterion("updater like", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotLike(String value) {
            addCriterion("updater not like", value, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterIn(List<String> values) {
            addCriterion("updater in", values, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotIn(List<String> values) {
            addCriterion("updater not in", values, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterBetween(String value1, String value2) {
            addCriterion("updater between", value1, value2, "updater");
            return (Criteria) this;
        }

        public Criteria andUpdaterNotBetween(String value1, String value2) {
            addCriterion("updater not between", value1, value2, "updater");
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