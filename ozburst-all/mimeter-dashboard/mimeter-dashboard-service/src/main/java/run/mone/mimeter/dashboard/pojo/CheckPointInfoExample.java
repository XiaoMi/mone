package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class CheckPointInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CheckPointInfoExample() {
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

        public Criteria andCheckTypeIsNull() {
            addCriterion("check_type is null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIsNotNull() {
            addCriterion("check_type is not null");
            return (Criteria) this;
        }

        public Criteria andCheckTypeEqualTo(Integer value) {
            addCriterion("check_type =", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotEqualTo(Integer value) {
            addCriterion("check_type <>", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThan(Integer value) {
            addCriterion("check_type >", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_type >=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThan(Integer value) {
            addCriterion("check_type <", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeLessThanOrEqualTo(Integer value) {
            addCriterion("check_type <=", value, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeIn(List<Integer> values) {
            addCriterion("check_type in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotIn(List<Integer> values) {
            addCriterion("check_type not in", values, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeBetween(Integer value1, Integer value2) {
            addCriterion("check_type between", value1, value2, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("check_type not between", value1, value2, "checkType");
            return (Criteria) this;
        }

        public Criteria andCheckObjIsNull() {
            addCriterion("check_obj is null");
            return (Criteria) this;
        }

        public Criteria andCheckObjIsNotNull() {
            addCriterion("check_obj is not null");
            return (Criteria) this;
        }

        public Criteria andCheckObjEqualTo(String value) {
            addCriterion("check_obj =", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjNotEqualTo(String value) {
            addCriterion("check_obj <>", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjGreaterThan(String value) {
            addCriterion("check_obj >", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjGreaterThanOrEqualTo(String value) {
            addCriterion("check_obj >=", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjLessThan(String value) {
            addCriterion("check_obj <", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjLessThanOrEqualTo(String value) {
            addCriterion("check_obj <=", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjLike(String value) {
            addCriterion("check_obj like", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjNotLike(String value) {
            addCriterion("check_obj not like", value, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjIn(List<String> values) {
            addCriterion("check_obj in", values, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjNotIn(List<String> values) {
            addCriterion("check_obj not in", values, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjBetween(String value1, String value2) {
            addCriterion("check_obj between", value1, value2, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckObjNotBetween(String value1, String value2) {
            addCriterion("check_obj not between", value1, value2, "checkObj");
            return (Criteria) this;
        }

        public Criteria andCheckConditionIsNull() {
            addCriterion("check_condition is null");
            return (Criteria) this;
        }

        public Criteria andCheckConditionIsNotNull() {
            addCriterion("check_condition is not null");
            return (Criteria) this;
        }

        public Criteria andCheckConditionEqualTo(Integer value) {
            addCriterion("check_condition =", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionNotEqualTo(Integer value) {
            addCriterion("check_condition <>", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionGreaterThan(Integer value) {
            addCriterion("check_condition >", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionGreaterThanOrEqualTo(Integer value) {
            addCriterion("check_condition >=", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionLessThan(Integer value) {
            addCriterion("check_condition <", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionLessThanOrEqualTo(Integer value) {
            addCriterion("check_condition <=", value, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionIn(List<Integer> values) {
            addCriterion("check_condition in", values, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionNotIn(List<Integer> values) {
            addCriterion("check_condition not in", values, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionBetween(Integer value1, Integer value2) {
            addCriterion("check_condition between", value1, value2, "checkCondition");
            return (Criteria) this;
        }

        public Criteria andCheckConditionNotBetween(Integer value1, Integer value2) {
            addCriterion("check_condition not between", value1, value2, "checkCondition");
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