package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class EoDubboApiInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EoDubboApiInfoExample() {
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

        public Criteria andApinameIsNull() {
            addCriterion("apiName is null");
            return (Criteria) this;
        }

        public Criteria andApinameIsNotNull() {
            addCriterion("apiName is not null");
            return (Criteria) this;
        }

        public Criteria andApinameEqualTo(String value) {
            addCriterion("apiName =", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameNotEqualTo(String value) {
            addCriterion("apiName <>", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameGreaterThan(String value) {
            addCriterion("apiName >", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameGreaterThanOrEqualTo(String value) {
            addCriterion("apiName >=", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameLessThan(String value) {
            addCriterion("apiName <", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameLessThanOrEqualTo(String value) {
            addCriterion("apiName <=", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameLike(String value) {
            addCriterion("apiName like", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameNotLike(String value) {
            addCriterion("apiName not like", value, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameIn(List<String> values) {
            addCriterion("apiName in", values, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameNotIn(List<String> values) {
            addCriterion("apiName not in", values, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameBetween(String value1, String value2) {
            addCriterion("apiName between", value1, value2, "apiname");
            return (Criteria) this;
        }

        public Criteria andApinameNotBetween(String value1, String value2) {
            addCriterion("apiName not between", value1, value2, "apiname");
            return (Criteria) this;
        }

        public Criteria andApidocnameIsNull() {
            addCriterion("apiDocName is null");
            return (Criteria) this;
        }

        public Criteria andApidocnameIsNotNull() {
            addCriterion("apiDocName is not null");
            return (Criteria) this;
        }

        public Criteria andApidocnameEqualTo(String value) {
            addCriterion("apiDocName =", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameNotEqualTo(String value) {
            addCriterion("apiDocName <>", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameGreaterThan(String value) {
            addCriterion("apiDocName >", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameGreaterThanOrEqualTo(String value) {
            addCriterion("apiDocName >=", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameLessThan(String value) {
            addCriterion("apiDocName <", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameLessThanOrEqualTo(String value) {
            addCriterion("apiDocName <=", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameLike(String value) {
            addCriterion("apiDocName like", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameNotLike(String value) {
            addCriterion("apiDocName not like", value, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameIn(List<String> values) {
            addCriterion("apiDocName in", values, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameNotIn(List<String> values) {
            addCriterion("apiDocName not in", values, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameBetween(String value1, String value2) {
            addCriterion("apiDocName between", value1, value2, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApidocnameNotBetween(String value1, String value2) {
            addCriterion("apiDocName not between", value1, value2, "apidocname");
            return (Criteria) this;
        }

        public Criteria andApiversionIsNull() {
            addCriterion("apiVersion is null");
            return (Criteria) this;
        }

        public Criteria andApiversionIsNotNull() {
            addCriterion("apiVersion is not null");
            return (Criteria) this;
        }

        public Criteria andApiversionEqualTo(String value) {
            addCriterion("apiVersion =", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionNotEqualTo(String value) {
            addCriterion("apiVersion <>", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionGreaterThan(String value) {
            addCriterion("apiVersion >", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionGreaterThanOrEqualTo(String value) {
            addCriterion("apiVersion >=", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionLessThan(String value) {
            addCriterion("apiVersion <", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionLessThanOrEqualTo(String value) {
            addCriterion("apiVersion <=", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionLike(String value) {
            addCriterion("apiVersion like", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionNotLike(String value) {
            addCriterion("apiVersion not like", value, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionIn(List<String> values) {
            addCriterion("apiVersion in", values, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionNotIn(List<String> values) {
            addCriterion("apiVersion not in", values, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionBetween(String value1, String value2) {
            addCriterion("apiVersion between", value1, value2, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApiversionNotBetween(String value1, String value2) {
            addCriterion("apiVersion not between", value1, value2, "apiversion");
            return (Criteria) this;
        }

        public Criteria andApigroupIsNull() {
            addCriterion("apiGroup is null");
            return (Criteria) this;
        }

        public Criteria andApigroupIsNotNull() {
            addCriterion("apiGroup is not null");
            return (Criteria) this;
        }

        public Criteria andApigroupEqualTo(String value) {
            addCriterion("apiGroup =", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupNotEqualTo(String value) {
            addCriterion("apiGroup <>", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupGreaterThan(String value) {
            addCriterion("apiGroup >", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupGreaterThanOrEqualTo(String value) {
            addCriterion("apiGroup >=", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupLessThan(String value) {
            addCriterion("apiGroup <", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupLessThanOrEqualTo(String value) {
            addCriterion("apiGroup <=", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupLike(String value) {
            addCriterion("apiGroup like", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupNotLike(String value) {
            addCriterion("apiGroup not like", value, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupIn(List<String> values) {
            addCriterion("apiGroup in", values, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupNotIn(List<String> values) {
            addCriterion("apiGroup not in", values, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupBetween(String value1, String value2) {
            addCriterion("apiGroup between", value1, value2, "apigroup");
            return (Criteria) this;
        }

        public Criteria andApigroupNotBetween(String value1, String value2) {
            addCriterion("apiGroup not between", value1, value2, "apigroup");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andApirespdecIsNull() {
            addCriterion("apiRespDec is null");
            return (Criteria) this;
        }

        public Criteria andApirespdecIsNotNull() {
            addCriterion("apiRespDec is not null");
            return (Criteria) this;
        }

        public Criteria andApirespdecEqualTo(String value) {
            addCriterion("apiRespDec =", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecNotEqualTo(String value) {
            addCriterion("apiRespDec <>", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecGreaterThan(String value) {
            addCriterion("apiRespDec >", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecGreaterThanOrEqualTo(String value) {
            addCriterion("apiRespDec >=", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecLessThan(String value) {
            addCriterion("apiRespDec <", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecLessThanOrEqualTo(String value) {
            addCriterion("apiRespDec <=", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecLike(String value) {
            addCriterion("apiRespDec like", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecNotLike(String value) {
            addCriterion("apiRespDec not like", value, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecIn(List<String> values) {
            addCriterion("apiRespDec in", values, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecNotIn(List<String> values) {
            addCriterion("apiRespDec not in", values, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecBetween(String value1, String value2) {
            addCriterion("apiRespDec between", value1, value2, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApirespdecNotBetween(String value1, String value2) {
            addCriterion("apiRespDec not between", value1, value2, "apirespdec");
            return (Criteria) this;
        }

        public Criteria andApimodelclassIsNull() {
            addCriterion("apiModelClass is null");
            return (Criteria) this;
        }

        public Criteria andApimodelclassIsNotNull() {
            addCriterion("apiModelClass is not null");
            return (Criteria) this;
        }

        public Criteria andApimodelclassEqualTo(String value) {
            addCriterion("apiModelClass =", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassNotEqualTo(String value) {
            addCriterion("apiModelClass <>", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassGreaterThan(String value) {
            addCriterion("apiModelClass >", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassGreaterThanOrEqualTo(String value) {
            addCriterion("apiModelClass >=", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassLessThan(String value) {
            addCriterion("apiModelClass <", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassLessThanOrEqualTo(String value) {
            addCriterion("apiModelClass <=", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassLike(String value) {
            addCriterion("apiModelClass like", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassNotLike(String value) {
            addCriterion("apiModelClass not like", value, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassIn(List<String> values) {
            addCriterion("apiModelClass in", values, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassNotIn(List<String> values) {
            addCriterion("apiModelClass not in", values, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassBetween(String value1, String value2) {
            addCriterion("apiModelClass between", value1, value2, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andApimodelclassNotBetween(String value1, String value2) {
            addCriterion("apiModelClass not between", value1, value2, "apimodelclass");
            return (Criteria) this;
        }

        public Criteria andAsyncIsNull() {
            addCriterion("async is null");
            return (Criteria) this;
        }

        public Criteria andAsyncIsNotNull() {
            addCriterion("async is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncEqualTo(Boolean value) {
            addCriterion("async =", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncNotEqualTo(Boolean value) {
            addCriterion("async <>", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncGreaterThan(Boolean value) {
            addCriterion("async >", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncGreaterThanOrEqualTo(Boolean value) {
            addCriterion("async >=", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncLessThan(Boolean value) {
            addCriterion("async <", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncLessThanOrEqualTo(Boolean value) {
            addCriterion("async <=", value, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncIn(List<Boolean> values) {
            addCriterion("async in", values, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncNotIn(List<Boolean> values) {
            addCriterion("async not in", values, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncBetween(Boolean value1, Boolean value2) {
            addCriterion("async between", value1, value2, "async");
            return (Criteria) this;
        }

        public Criteria andAsyncNotBetween(Boolean value1, Boolean value2) {
            addCriterion("async not between", value1, value2, "async");
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