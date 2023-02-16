package com.xiaomi.mone.monitor.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppMonitorExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public AppMonitorExample() {
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

        public Criteria andIamTreeIdIsNull() {
            addCriterion("iam_tree_id is null");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdIsNotNull() {
            addCriterion("iam_tree_id is not null");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdEqualTo(Integer value) {
            addCriterion("iam_tree_id =", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdNotEqualTo(Integer value) {
            addCriterion("iam_tree_id <>", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdGreaterThan(Integer value) {
            addCriterion("iam_tree_id >", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("iam_tree_id >=", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdLessThan(Integer value) {
            addCriterion("iam_tree_id <", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdLessThanOrEqualTo(Integer value) {
            addCriterion("iam_tree_id <=", value, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdIn(List<Integer> values) {
            addCriterion("iam_tree_id in", values, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdNotIn(List<Integer> values) {
            addCriterion("iam_tree_id not in", values, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdBetween(Integer value1, Integer value2) {
            addCriterion("iam_tree_id between", value1, value2, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andIamTreeIdNotBetween(Integer value1, Integer value2) {
            addCriterion("iam_tree_id not between", value1, value2, "iamTreeId");
            return (Criteria) this;
        }

        public Criteria andProjectNameIsNull() {
            addCriterion("project_name is null");
            return (Criteria) this;
        }

        public Criteria andProjectNameIsNotNull() {
            addCriterion("project_name is not null");
            return (Criteria) this;
        }

        public Criteria andProjectNameEqualTo(String value) {
            addCriterion("project_name =", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andAppSourceEqualTo(Integer value) {
            addCriterion("app_source =", value, "appSource");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotEqualTo(String value) {
            addCriterion("project_name <>", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameGreaterThan(String value) {
            addCriterion("project_name >", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameGreaterThanOrEqualTo(String value) {
            addCriterion("project_name >=", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLessThan(String value) {
            addCriterion("project_name <", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLessThanOrEqualTo(String value) {
            addCriterion("project_name <=", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLike(String value) {
            addCriterion("project_name like", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotLike(String value) {
            addCriterion("project_name not like", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameIn(List<String> values) {
            addCriterion("project_name in", values, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotIn(List<String> values) {
            addCriterion("project_name not in", values, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameBetween(String value1, String value2) {
            addCriterion("project_name between", value1, value2, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotBetween(String value1, String value2) {
            addCriterion("project_name not between", value1, value2, "projectName");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNull() {
            addCriterion("owner is null");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNotNull() {
            addCriterion("owner is not null");
            return (Criteria) this;
        }

        public Criteria andOwnerEqualTo(String value) {
            addCriterion("owner =", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotEqualTo(String value) {
            addCriterion("owner <>", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThan(String value) {
            addCriterion("owner >", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThanOrEqualTo(String value) {
            addCriterion("owner >=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThan(String value) {
            addCriterion("owner <", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThanOrEqualTo(String value) {
            addCriterion("owner <=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLike(String value) {
            addCriterion("owner like", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotLike(String value) {
            addCriterion("owner not like", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerIn(List<String> values) {
            addCriterion("owner in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotIn(List<String> values) {
            addCriterion("owner not in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerBetween(String value1, String value2) {
            addCriterion("owner between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotBetween(String value1, String value2) {
            addCriterion("owner not between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andCareUserIsNull() {
            addCriterion("care_user is null");
            return (Criteria) this;
        }

        public Criteria andCareUserIsNotNull() {
            addCriterion("care_user is not null");
            return (Criteria) this;
        }

        public Criteria andCareUserEqualTo(String value) {
            addCriterion("care_user =", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserNotEqualTo(String value) {
            addCriterion("care_user <>", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserGreaterThan(String value) {
            addCriterion("care_user >", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserGreaterThanOrEqualTo(String value) {
            addCriterion("care_user >=", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserLessThan(String value) {
            addCriterion("care_user <", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserLessThanOrEqualTo(String value) {
            addCriterion("care_user <=", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserLike(String value) {
            addCriterion("care_user like", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserNotLike(String value) {
            addCriterion("care_user not like", value, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserIn(List<String> values) {
            addCriterion("care_user in", values, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserNotIn(List<String> values) {
            addCriterion("care_user not in", values, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserBetween(String value1, String value2) {
            addCriterion("care_user between", value1, value2, "careUser");
            return (Criteria) this;
        }

        public Criteria andCareUserNotBetween(String value1, String value2) {
            addCriterion("care_user not between", value1, value2, "careUser");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelIsNull() {
            addCriterion("alarm_level is null");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelIsNotNull() {
            addCriterion("alarm_level is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelEqualTo(Integer value) {
            addCriterion("alarm_level =", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelNotEqualTo(Integer value) {
            addCriterion("alarm_level <>", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelGreaterThan(Integer value) {
            addCriterion("alarm_level >", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("alarm_level >=", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelLessThan(Integer value) {
            addCriterion("alarm_level <", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelLessThanOrEqualTo(Integer value) {
            addCriterion("alarm_level <=", value, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelIn(List<Integer> values) {
            addCriterion("alarm_level in", values, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelNotIn(List<Integer> values) {
            addCriterion("alarm_level not in", values, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelBetween(Integer value1, Integer value2) {
            addCriterion("alarm_level between", value1, value2, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andAlarmLevelNotBetween(Integer value1, Integer value2) {
            addCriterion("alarm_level not between", value1, value2, "alarmLevel");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmIsNull() {
            addCriterion("total_alarm is null");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmIsNotNull() {
            addCriterion("total_alarm is not null");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmEqualTo(Integer value) {
            addCriterion("total_alarm =", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmNotEqualTo(Integer value) {
            addCriterion("total_alarm <>", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmGreaterThan(Integer value) {
            addCriterion("total_alarm >", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_alarm >=", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmLessThan(Integer value) {
            addCriterion("total_alarm <", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmLessThanOrEqualTo(Integer value) {
            addCriterion("total_alarm <=", value, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmIn(List<Integer> values) {
            addCriterion("total_alarm in", values, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmNotIn(List<Integer> values) {
            addCriterion("total_alarm not in", values, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmBetween(Integer value1, Integer value2) {
            addCriterion("total_alarm between", value1, value2, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andTotalAlarmNotBetween(Integer value1, Integer value2) {
            addCriterion("total_alarm not between", value1, value2, "totalAlarm");
            return (Criteria) this;
        }

        public Criteria andExceptionNumIsNull() {
            addCriterion("exception_num is null");
            return (Criteria) this;
        }

        public Criteria andExceptionNumIsNotNull() {
            addCriterion("exception_num is not null");
            return (Criteria) this;
        }

        public Criteria andExceptionNumEqualTo(Integer value) {
            addCriterion("exception_num =", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumNotEqualTo(Integer value) {
            addCriterion("exception_num <>", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumGreaterThan(Integer value) {
            addCriterion("exception_num >", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("exception_num >=", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumLessThan(Integer value) {
            addCriterion("exception_num <", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumLessThanOrEqualTo(Integer value) {
            addCriterion("exception_num <=", value, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumIn(List<Integer> values) {
            addCriterion("exception_num in", values, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumNotIn(List<Integer> values) {
            addCriterion("exception_num not in", values, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumBetween(Integer value1, Integer value2) {
            addCriterion("exception_num between", value1, value2, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andExceptionNumNotBetween(Integer value1, Integer value2) {
            addCriterion("exception_num not between", value1, value2, "exceptionNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumIsNull() {
            addCriterion("slow_query_num is null");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumIsNotNull() {
            addCriterion("slow_query_num is not null");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumEqualTo(Integer value) {
            addCriterion("slow_query_num =", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumNotEqualTo(Integer value) {
            addCriterion("slow_query_num <>", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumGreaterThan(Integer value) {
            addCriterion("slow_query_num >", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("slow_query_num >=", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumLessThan(Integer value) {
            addCriterion("slow_query_num <", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumLessThanOrEqualTo(Integer value) {
            addCriterion("slow_query_num <=", value, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumIn(List<Integer> values) {
            addCriterion("slow_query_num in", values, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumNotIn(List<Integer> values) {
            addCriterion("slow_query_num not in", values, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumBetween(Integer value1, Integer value2) {
            addCriterion("slow_query_num between", value1, value2, "slowQueryNum");
            return (Criteria) this;
        }

        public Criteria andSlowQueryNumNotBetween(Integer value1, Integer value2) {
            addCriterion("slow_query_num not between", value1, value2, "slowQueryNum");
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