package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppCapacityAutoAdjustExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public AppCapacityAutoAdjustExample() {
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

        public Criteria andAppIdIsNull() {
            addCriterion("app_id is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("app_id is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(Integer value) {
            addCriterion("app_id =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(Integer value) {
            addCriterion("app_id <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(Integer value) {
            addCriterion("app_id >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("app_id >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(Integer value) {
            addCriterion("app_id <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(Integer value) {
            addCriterion("app_id <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<Integer> values) {
            addCriterion("app_id in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<Integer> values) {
            addCriterion("app_id not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(Integer value1, Integer value2) {
            addCriterion("app_id between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(Integer value1, Integer value2) {
            addCriterion("app_id not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdIsNull() {
            addCriterion("pipeline_id is null");
            return (Criteria) this;
        }

        public Criteria andPipelineIdIsNotNull() {
            addCriterion("pipeline_id is not null");
            return (Criteria) this;
        }

        public Criteria andPipelineIdEqualTo(Integer value) {
            addCriterion("pipeline_id =", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdNotEqualTo(Integer value) {
            addCriterion("pipeline_id <>", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdGreaterThan(Integer value) {
            addCriterion("pipeline_id >", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("pipeline_id >=", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdLessThan(Integer value) {
            addCriterion("pipeline_id <", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdLessThanOrEqualTo(Integer value) {
            addCriterion("pipeline_id <=", value, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdIn(List<Integer> values) {
            addCriterion("pipeline_id in", values, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdNotIn(List<Integer> values) {
            addCriterion("pipeline_id not in", values, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdBetween(Integer value1, Integer value2) {
            addCriterion("pipeline_id between", value1, value2, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andPipelineIdNotBetween(Integer value1, Integer value2) {
            addCriterion("pipeline_id not between", value1, value2, "pipelineId");
            return (Criteria) this;
        }

        public Criteria andContainerIsNull() {
            addCriterion("container is null");
            return (Criteria) this;
        }

        public Criteria andContainerIsNotNull() {
            addCriterion("container is not null");
            return (Criteria) this;
        }

        public Criteria andContainerEqualTo(String value) {
            addCriterion("container =", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerNotEqualTo(String value) {
            addCriterion("container <>", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerGreaterThan(String value) {
            addCriterion("container >", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerGreaterThanOrEqualTo(String value) {
            addCriterion("container >=", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerLessThan(String value) {
            addCriterion("container <", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerLessThanOrEqualTo(String value) {
            addCriterion("container <=", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerLike(String value) {
            addCriterion("container like", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerNotLike(String value) {
            addCriterion("container not like", value, "container");
            return (Criteria) this;
        }

        public Criteria andContainerIn(List<String> values) {
            addCriterion("container in", values, "container");
            return (Criteria) this;
        }

        public Criteria andContainerNotIn(List<String> values) {
            addCriterion("container not in", values, "container");
            return (Criteria) this;
        }

        public Criteria andContainerBetween(String value1, String value2) {
            addCriterion("container between", value1, value2, "container");
            return (Criteria) this;
        }

        public Criteria andContainerNotBetween(String value1, String value2) {
            addCriterion("container not between", value1, value2, "container");
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

        public Criteria andMinInstanceIsNull() {
            addCriterion("min_instance is null");
            return (Criteria) this;
        }

        public Criteria andMinInstanceIsNotNull() {
            addCriterion("min_instance is not null");
            return (Criteria) this;
        }

        public Criteria andMinInstanceEqualTo(Integer value) {
            addCriterion("min_instance =", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceNotEqualTo(Integer value) {
            addCriterion("min_instance <>", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceGreaterThan(Integer value) {
            addCriterion("min_instance >", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceGreaterThanOrEqualTo(Integer value) {
            addCriterion("min_instance >=", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceLessThan(Integer value) {
            addCriterion("min_instance <", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceLessThanOrEqualTo(Integer value) {
            addCriterion("min_instance <=", value, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceIn(List<Integer> values) {
            addCriterion("min_instance in", values, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceNotIn(List<Integer> values) {
            addCriterion("min_instance not in", values, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceBetween(Integer value1, Integer value2) {
            addCriterion("min_instance between", value1, value2, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMinInstanceNotBetween(Integer value1, Integer value2) {
            addCriterion("min_instance not between", value1, value2, "minInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceIsNull() {
            addCriterion("max_instance is null");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceIsNotNull() {
            addCriterion("max_instance is not null");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceEqualTo(Integer value) {
            addCriterion("max_instance =", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceNotEqualTo(Integer value) {
            addCriterion("max_instance <>", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceGreaterThan(Integer value) {
            addCriterion("max_instance >", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_instance >=", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceLessThan(Integer value) {
            addCriterion("max_instance <", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceLessThanOrEqualTo(Integer value) {
            addCriterion("max_instance <=", value, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceIn(List<Integer> values) {
            addCriterion("max_instance in", values, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceNotIn(List<Integer> values) {
            addCriterion("max_instance not in", values, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceBetween(Integer value1, Integer value2) {
            addCriterion("max_instance between", value1, value2, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andMaxInstanceNotBetween(Integer value1, Integer value2) {
            addCriterion("max_instance not between", value1, value2, "maxInstance");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityIsNull() {
            addCriterion("auto_capacity is null");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityIsNotNull() {
            addCriterion("auto_capacity is not null");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityEqualTo(Integer value) {
            addCriterion("auto_capacity =", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityNotEqualTo(Integer value) {
            addCriterion("auto_capacity <>", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityGreaterThan(Integer value) {
            addCriterion("auto_capacity >", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityGreaterThanOrEqualTo(Integer value) {
            addCriterion("auto_capacity >=", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityLessThan(Integer value) {
            addCriterion("auto_capacity <", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityLessThanOrEqualTo(Integer value) {
            addCriterion("auto_capacity <=", value, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityIn(List<Integer> values) {
            addCriterion("auto_capacity in", values, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityNotIn(List<Integer> values) {
            addCriterion("auto_capacity not in", values, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityBetween(Integer value1, Integer value2) {
            addCriterion("auto_capacity between", value1, value2, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andAutoCapacityNotBetween(Integer value1, Integer value2) {
            addCriterion("auto_capacity not between", value1, value2, "autoCapacity");
            return (Criteria) this;
        }

        public Criteria andDependOnIsNull() {
            addCriterion("depend_on is null");
            return (Criteria) this;
        }

        public Criteria andDependOnIsNotNull() {
            addCriterion("depend_on is not null");
            return (Criteria) this;
        }

        public Criteria andDependOnEqualTo(Integer value) {
            addCriterion("depend_on =", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnNotEqualTo(Integer value) {
            addCriterion("depend_on <>", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnGreaterThan(Integer value) {
            addCriterion("depend_on >", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnGreaterThanOrEqualTo(Integer value) {
            addCriterion("depend_on >=", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnLessThan(Integer value) {
            addCriterion("depend_on <", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnLessThanOrEqualTo(Integer value) {
            addCriterion("depend_on <=", value, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnIn(List<Integer> values) {
            addCriterion("depend_on in", values, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnNotIn(List<Integer> values) {
            addCriterion("depend_on not in", values, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnBetween(Integer value1, Integer value2) {
            addCriterion("depend_on between", value1, value2, "dependOn");
            return (Criteria) this;
        }

        public Criteria andDependOnNotBetween(Integer value1, Integer value2) {
            addCriterion("depend_on not between", value1, value2, "dependOn");
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