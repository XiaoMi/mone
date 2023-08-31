package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppCapacityAutoAdjustRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public AppCapacityAutoAdjustRecordExample() {
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

        public Criteria andNameSpaceIsNull() {
            addCriterion("name_space is null");
            return (Criteria) this;
        }

        public Criteria andNameSpaceIsNotNull() {
            addCriterion("name_space is not null");
            return (Criteria) this;
        }

        public Criteria andNameSpaceEqualTo(String value) {
            addCriterion("name_space =", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceNotEqualTo(String value) {
            addCriterion("name_space <>", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceGreaterThan(String value) {
            addCriterion("name_space >", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceGreaterThanOrEqualTo(String value) {
            addCriterion("name_space >=", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceLessThan(String value) {
            addCriterion("name_space <", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceLessThanOrEqualTo(String value) {
            addCriterion("name_space <=", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceLike(String value) {
            addCriterion("name_space like", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceNotLike(String value) {
            addCriterion("name_space not like", value, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceIn(List<String> values) {
            addCriterion("name_space in", values, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceNotIn(List<String> values) {
            addCriterion("name_space not in", values, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceBetween(String value1, String value2) {
            addCriterion("name_space between", value1, value2, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andNameSpaceNotBetween(String value1, String value2) {
            addCriterion("name_space not between", value1, value2, "nameSpace");
            return (Criteria) this;
        }

        public Criteria andReplicasIsNull() {
            addCriterion("replicas is null");
            return (Criteria) this;
        }

        public Criteria andReplicasIsNotNull() {
            addCriterion("replicas is not null");
            return (Criteria) this;
        }

        public Criteria andReplicasEqualTo(Integer value) {
            addCriterion("replicas =", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasNotEqualTo(Integer value) {
            addCriterion("replicas <>", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasGreaterThan(Integer value) {
            addCriterion("replicas >", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasGreaterThanOrEqualTo(Integer value) {
            addCriterion("replicas >=", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasLessThan(Integer value) {
            addCriterion("replicas <", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasLessThanOrEqualTo(Integer value) {
            addCriterion("replicas <=", value, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasIn(List<Integer> values) {
            addCriterion("replicas in", values, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasNotIn(List<Integer> values) {
            addCriterion("replicas not in", values, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasBetween(Integer value1, Integer value2) {
            addCriterion("replicas between", value1, value2, "replicas");
            return (Criteria) this;
        }

        public Criteria andReplicasNotBetween(Integer value1, Integer value2) {
            addCriterion("replicas not between", value1, value2, "replicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasIsNull() {
            addCriterion("set_replicas is null");
            return (Criteria) this;
        }

        public Criteria andSetReplicasIsNotNull() {
            addCriterion("set_replicas is not null");
            return (Criteria) this;
        }

        public Criteria andSetReplicasEqualTo(Integer value) {
            addCriterion("set_replicas =", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasNotEqualTo(Integer value) {
            addCriterion("set_replicas <>", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasGreaterThan(Integer value) {
            addCriterion("set_replicas >", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasGreaterThanOrEqualTo(Integer value) {
            addCriterion("set_replicas >=", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasLessThan(Integer value) {
            addCriterion("set_replicas <", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasLessThanOrEqualTo(Integer value) {
            addCriterion("set_replicas <=", value, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasIn(List<Integer> values) {
            addCriterion("set_replicas in", values, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasNotIn(List<Integer> values) {
            addCriterion("set_replicas not in", values, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasBetween(Integer value1, Integer value2) {
            addCriterion("set_replicas between", value1, value2, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andSetReplicasNotBetween(Integer value1, Integer value2) {
            addCriterion("set_replicas not between", value1, value2, "setReplicas");
            return (Criteria) this;
        }

        public Criteria andEnvIdIsNull() {
            addCriterion("env_id is null");
            return (Criteria) this;
        }

        public Criteria andEnvIdIsNotNull() {
            addCriterion("env_id is not null");
            return (Criteria) this;
        }

        public Criteria andEnvIdEqualTo(Integer value) {
            addCriterion("env_id =", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdNotEqualTo(Integer value) {
            addCriterion("env_id <>", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdGreaterThan(Integer value) {
            addCriterion("env_id >", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("env_id >=", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdLessThan(Integer value) {
            addCriterion("env_id <", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdLessThanOrEqualTo(Integer value) {
            addCriterion("env_id <=", value, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdIn(List<Integer> values) {
            addCriterion("env_id in", values, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdNotIn(List<Integer> values) {
            addCriterion("env_id not in", values, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdBetween(Integer value1, Integer value2) {
            addCriterion("env_id between", value1, value2, "envId");
            return (Criteria) this;
        }

        public Criteria andEnvIdNotBetween(Integer value1, Integer value2) {
            addCriterion("env_id not between", value1, value2, "envId");
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

        public Criteria andTimeIsNull() {
            addCriterion("time is null");
            return (Criteria) this;
        }

        public Criteria andTimeIsNotNull() {
            addCriterion("time is not null");
            return (Criteria) this;
        }

        public Criteria andTimeEqualTo(Long value) {
            addCriterion("time =", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotEqualTo(Long value) {
            addCriterion("time <>", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThan(Long value) {
            addCriterion("time >", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("time >=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThan(Long value) {
            addCriterion("time <", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThanOrEqualTo(Long value) {
            addCriterion("time <=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeIn(List<Long> values) {
            addCriterion("time in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotIn(List<Long> values) {
            addCriterion("time not in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeBetween(Long value1, Long value2) {
            addCriterion("time between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotBetween(Long value1, Long value2) {
            addCriterion("time not between", value1, value2, "time");
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