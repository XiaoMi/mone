package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.List;

public class ApiEnvExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ApiEnvExample() {
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

        public Criteria andEnvNameIsNull() {
            addCriterion("env_name is null");
            return (Criteria) this;
        }

        public Criteria andEnvNameIsNotNull() {
            addCriterion("env_name is not null");
            return (Criteria) this;
        }

        public Criteria andEnvNameEqualTo(String value) {
            addCriterion("env_name =", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameNotEqualTo(String value) {
            addCriterion("env_name <>", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameGreaterThan(String value) {
            addCriterion("env_name >", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameGreaterThanOrEqualTo(String value) {
            addCriterion("env_name >=", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameLessThan(String value) {
            addCriterion("env_name <", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameLessThanOrEqualTo(String value) {
            addCriterion("env_name <=", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameLike(String value) {
            addCriterion("env_name like", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameNotLike(String value) {
            addCriterion("env_name not like", value, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameIn(List<String> values) {
            addCriterion("env_name in", values, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameNotIn(List<String> values) {
            addCriterion("env_name not in", values, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameBetween(String value1, String value2) {
            addCriterion("env_name between", value1, value2, "envName");
            return (Criteria) this;
        }

        public Criteria andEnvNameNotBetween(String value1, String value2) {
            addCriterion("env_name not between", value1, value2, "envName");
            return (Criteria) this;
        }

        public Criteria andHttpDomainIsNull() {
            addCriterion("http_domain is null");
            return (Criteria) this;
        }

        public Criteria andHttpDomainIsNotNull() {
            addCriterion("http_domain is not null");
            return (Criteria) this;
        }

        public Criteria andHttpDomainEqualTo(String value) {
            addCriterion("http_domain =", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainNotEqualTo(String value) {
            addCriterion("http_domain <>", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainGreaterThan(String value) {
            addCriterion("http_domain >", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainGreaterThanOrEqualTo(String value) {
            addCriterion("http_domain >=", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainLessThan(String value) {
            addCriterion("http_domain <", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainLessThanOrEqualTo(String value) {
            addCriterion("http_domain <=", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainLike(String value) {
            addCriterion("http_domain like", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainNotLike(String value) {
            addCriterion("http_domain not like", value, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainIn(List<String> values) {
            addCriterion("http_domain in", values, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainNotIn(List<String> values) {
            addCriterion("http_domain not in", values, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainBetween(String value1, String value2) {
            addCriterion("http_domain between", value1, value2, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andHttpDomainNotBetween(String value1, String value2) {
            addCriterion("http_domain not between", value1, value2, "httpDomain");
            return (Criteria) this;
        }

        public Criteria andEnvDescIsNull() {
            addCriterion("env_desc is null");
            return (Criteria) this;
        }

        public Criteria andEnvDescIsNotNull() {
            addCriterion("env_desc is not null");
            return (Criteria) this;
        }

        public Criteria andEnvDescEqualTo(String value) {
            addCriterion("env_desc =", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescNotEqualTo(String value) {
            addCriterion("env_desc <>", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescGreaterThan(String value) {
            addCriterion("env_desc >", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescGreaterThanOrEqualTo(String value) {
            addCriterion("env_desc >=", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescLessThan(String value) {
            addCriterion("env_desc <", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescLessThanOrEqualTo(String value) {
            addCriterion("env_desc <=", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescLike(String value) {
            addCriterion("env_desc like", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescNotLike(String value) {
            addCriterion("env_desc not like", value, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescIn(List<String> values) {
            addCriterion("env_desc in", values, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescNotIn(List<String> values) {
            addCriterion("env_desc not in", values, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescBetween(String value1, String value2) {
            addCriterion("env_desc between", value1, value2, "envDesc");
            return (Criteria) this;
        }

        public Criteria andEnvDescNotBetween(String value1, String value2) {
            addCriterion("env_desc not between", value1, value2, "envDesc");
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

        public Criteria andSysDefaultIsNull() {
            addCriterion("sys_default is null");
            return (Criteria) this;
        }

        public Criteria andSysDefaultIsNotNull() {
            addCriterion("sys_default is not null");
            return (Criteria) this;
        }

        public Criteria andSysDefaultEqualTo(Boolean value) {
            addCriterion("sys_default =", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultNotEqualTo(Boolean value) {
            addCriterion("sys_default <>", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultGreaterThan(Boolean value) {
            addCriterion("sys_default >", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultGreaterThanOrEqualTo(Boolean value) {
            addCriterion("sys_default >=", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultLessThan(Boolean value) {
            addCriterion("sys_default <", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultLessThanOrEqualTo(Boolean value) {
            addCriterion("sys_default <=", value, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultIn(List<Boolean> values) {
            addCriterion("sys_default in", values, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultNotIn(List<Boolean> values) {
            addCriterion("sys_default not in", values, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultBetween(Boolean value1, Boolean value2) {
            addCriterion("sys_default between", value1, value2, "sysDefault");
            return (Criteria) this;
        }

        public Criteria andSysDefaultNotBetween(Boolean value1, Boolean value2) {
            addCriterion("sys_default not between", value1, value2, "sysDefault");
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