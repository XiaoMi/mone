package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiStatExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Long offset;

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getOffset() {
        return offset;
    }

    public ApiStatExample() {
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

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andSceneIdIsNull() {
            addCriterion("scene_id is null");
            return (Criteria) this;
        }

        public Criteria andSceneIdIsNotNull() {
            addCriterion("scene_id is not null");
            return (Criteria) this;
        }

        public Criteria andSceneIdEqualTo(Long value) {
            addCriterion("scene_id =", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotEqualTo(Long value) {
            addCriterion("scene_id <>", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdGreaterThan(Long value) {
            addCriterion("scene_id >", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdGreaterThanOrEqualTo(Long value) {
            addCriterion("scene_id >=", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdLessThan(Long value) {
            addCriterion("scene_id <", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdLessThanOrEqualTo(Long value) {
            addCriterion("scene_id <=", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdIn(List<Long> values) {
            addCriterion("scene_id in", values, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotIn(List<Long> values) {
            addCriterion("scene_id not in", values, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdBetween(Long value1, Long value2) {
            addCriterion("scene_id between", value1, value2, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotBetween(Long value1, Long value2) {
            addCriterion("scene_id not between", value1, value2, "sceneId");
            return (Criteria) this;
        }

        public Criteria andReportIdIsNull() {
            addCriterion("report_id is null");
            return (Criteria) this;
        }

        public Criteria andReportIdIsNotNull() {
            addCriterion("report_id is not null");
            return (Criteria) this;
        }

        public Criteria andReportIdEqualTo(String value) {
            addCriterion("report_id =", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotEqualTo(String value) {
            addCriterion("report_id <>", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdGreaterThan(String value) {
            addCriterion("report_id >", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdGreaterThanOrEqualTo(String value) {
            addCriterion("report_id >=", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLessThan(String value) {
            addCriterion("report_id <", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLessThanOrEqualTo(String value) {
            addCriterion("report_id <=", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdLike(String value) {
            addCriterion("report_id like", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotLike(String value) {
            addCriterion("report_id not like", value, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdIn(List<String> values) {
            addCriterion("report_id in", values, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotIn(List<String> values) {
            addCriterion("report_id not in", values, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdBetween(String value1, String value2) {
            addCriterion("report_id between", value1, value2, "reportId");
            return (Criteria) this;
        }

        public Criteria andReportIdNotBetween(String value1, String value2) {
            addCriterion("report_id not between", value1, value2, "reportId");
            return (Criteria) this;
        }

        public Criteria andApiIdIsNull() {
            addCriterion("api_id is null");
            return (Criteria) this;
        }

        public Criteria andApiIdIsNotNull() {
            addCriterion("api_id is not null");
            return (Criteria) this;
        }

        public Criteria andApiIdEqualTo(Long value) {
            addCriterion("api_id =", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotEqualTo(Long value) {
            addCriterion("api_id <>", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdGreaterThan(Long value) {
            addCriterion("api_id >", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdGreaterThanOrEqualTo(Long value) {
            addCriterion("api_id >=", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdLessThan(Long value) {
            addCriterion("api_id <", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdLessThanOrEqualTo(Long value) {
            addCriterion("api_id <=", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdIn(List<Long> values) {
            addCriterion("api_id in", values, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotIn(List<Long> values) {
            addCriterion("api_id not in", values, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdBetween(Long value1, Long value2) {
            addCriterion("api_id between", value1, value2, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotBetween(Long value1, Long value2) {
            addCriterion("api_id not between", value1, value2, "apiId");
            return (Criteria) this;
        }

        public Criteria andReqSuccIsNull() {
            addCriterion("req_succ is null");
            return (Criteria) this;
        }

        public Criteria andReqSuccIsNotNull() {
            addCriterion("req_succ is not null");
            return (Criteria) this;
        }

        public Criteria andReqSuccEqualTo(Integer value) {
            addCriterion("req_succ =", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccNotEqualTo(Integer value) {
            addCriterion("req_succ <>", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccGreaterThan(Integer value) {
            addCriterion("req_succ >", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_succ >=", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccLessThan(Integer value) {
            addCriterion("req_succ <", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccLessThanOrEqualTo(Integer value) {
            addCriterion("req_succ <=", value, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccIn(List<Integer> values) {
            addCriterion("req_succ in", values, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccNotIn(List<Integer> values) {
            addCriterion("req_succ not in", values, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccBetween(Integer value1, Integer value2) {
            addCriterion("req_succ between", value1, value2, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqSuccNotBetween(Integer value1, Integer value2) {
            addCriterion("req_succ not between", value1, value2, "reqSucc");
            return (Criteria) this;
        }

        public Criteria andReqFailIsNull() {
            addCriterion("req_fail is null");
            return (Criteria) this;
        }

        public Criteria andReqFailIsNotNull() {
            addCriterion("req_fail is not null");
            return (Criteria) this;
        }

        public Criteria andReqFailEqualTo(Integer value) {
            addCriterion("req_fail =", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailNotEqualTo(Integer value) {
            addCriterion("req_fail <>", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailGreaterThan(Integer value) {
            addCriterion("req_fail >", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_fail >=", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailLessThan(Integer value) {
            addCriterion("req_fail <", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailLessThanOrEqualTo(Integer value) {
            addCriterion("req_fail <=", value, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailIn(List<Integer> values) {
            addCriterion("req_fail in", values, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailNotIn(List<Integer> values) {
            addCriterion("req_fail not in", values, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailBetween(Integer value1, Integer value2) {
            addCriterion("req_fail between", value1, value2, "reqFail");
            return (Criteria) this;
        }

        public Criteria andReqFailNotBetween(Integer value1, Integer value2) {
            addCriterion("req_fail not between", value1, value2, "reqFail");
            return (Criteria) this;
        }

        public Criteria andTpsIsNull() {
            addCriterion("tps is null");
            return (Criteria) this;
        }

        public Criteria andTpsIsNotNull() {
            addCriterion("tps is not null");
            return (Criteria) this;
        }

        public Criteria andTpsEqualTo(Integer value) {
            addCriterion("tps =", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsNotEqualTo(Integer value) {
            addCriterion("tps <>", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsGreaterThan(Integer value) {
            addCriterion("tps >", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsGreaterThanOrEqualTo(Integer value) {
            addCriterion("tps >=", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsLessThan(Integer value) {
            addCriterion("tps <", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsLessThanOrEqualTo(Integer value) {
            addCriterion("tps <=", value, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsIn(List<Integer> values) {
            addCriterion("tps in", values, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsNotIn(List<Integer> values) {
            addCriterion("tps not in", values, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsBetween(Integer value1, Integer value2) {
            addCriterion("tps between", value1, value2, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsNotBetween(Integer value1, Integer value2) {
            addCriterion("tps not between", value1, value2, "tps");
            return (Criteria) this;
        }

        public Criteria andTpsMaxIsNull() {
            addCriterion("tps_max is null");
            return (Criteria) this;
        }

        public Criteria andTpsMaxIsNotNull() {
            addCriterion("tps_max is not null");
            return (Criteria) this;
        }

        public Criteria andTpsMaxEqualTo(Integer value) {
            addCriterion("tps_max =", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxNotEqualTo(Integer value) {
            addCriterion("tps_max <>", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxGreaterThan(Integer value) {
            addCriterion("tps_max >", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxGreaterThanOrEqualTo(Integer value) {
            addCriterion("tps_max >=", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxLessThan(Integer value) {
            addCriterion("tps_max <", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxLessThanOrEqualTo(Integer value) {
            addCriterion("tps_max <=", value, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxIn(List<Integer> values) {
            addCriterion("tps_max in", values, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxNotIn(List<Integer> values) {
            addCriterion("tps_max not in", values, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxBetween(Integer value1, Integer value2) {
            addCriterion("tps_max between", value1, value2, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andTpsMaxNotBetween(Integer value1, Integer value2) {
            addCriterion("tps_max not between", value1, value2, "tpsMax");
            return (Criteria) this;
        }

        public Criteria andRtIsNull() {
            addCriterion("rt is null");
            return (Criteria) this;
        }

        public Criteria andRtIsNotNull() {
            addCriterion("rt is not null");
            return (Criteria) this;
        }

        public Criteria andRtEqualTo(Integer value) {
            addCriterion("rt =", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtNotEqualTo(Integer value) {
            addCriterion("rt <>", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtGreaterThan(Integer value) {
            addCriterion("rt >", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtGreaterThanOrEqualTo(Integer value) {
            addCriterion("rt >=", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtLessThan(Integer value) {
            addCriterion("rt <", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtLessThanOrEqualTo(Integer value) {
            addCriterion("rt <=", value, "rt");
            return (Criteria) this;
        }

        public Criteria andRtIn(List<Integer> values) {
            addCriterion("rt in", values, "rt");
            return (Criteria) this;
        }

        public Criteria andRtNotIn(List<Integer> values) {
            addCriterion("rt not in", values, "rt");
            return (Criteria) this;
        }

        public Criteria andRtBetween(Integer value1, Integer value2) {
            addCriterion("rt between", value1, value2, "rt");
            return (Criteria) this;
        }

        public Criteria andRtNotBetween(Integer value1, Integer value2) {
            addCriterion("rt not between", value1, value2, "rt");
            return (Criteria) this;
        }

        public Criteria andRtMaxIsNull() {
            addCriterion("rt_max is null");
            return (Criteria) this;
        }

        public Criteria andRtMaxIsNotNull() {
            addCriterion("rt_max is not null");
            return (Criteria) this;
        }

        public Criteria andRtMaxEqualTo(Integer value) {
            addCriterion("rt_max =", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxNotEqualTo(Integer value) {
            addCriterion("rt_max <>", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxGreaterThan(Integer value) {
            addCriterion("rt_max >", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxGreaterThanOrEqualTo(Integer value) {
            addCriterion("rt_max >=", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxLessThan(Integer value) {
            addCriterion("rt_max <", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxLessThanOrEqualTo(Integer value) {
            addCriterion("rt_max <=", value, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxIn(List<Integer> values) {
            addCriterion("rt_max in", values, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxNotIn(List<Integer> values) {
            addCriterion("rt_max not in", values, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxBetween(Integer value1, Integer value2) {
            addCriterion("rt_max between", value1, value2, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRtMaxNotBetween(Integer value1, Integer value2) {
            addCriterion("rt_max not between", value1, value2, "rtMax");
            return (Criteria) this;
        }

        public Criteria andRt99IsNull() {
            addCriterion("rt99 is null");
            return (Criteria) this;
        }

        public Criteria andRt99IsNotNull() {
            addCriterion("rt99 is not null");
            return (Criteria) this;
        }

        public Criteria andRt99EqualTo(Integer value) {
            addCriterion("rt99 =", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99NotEqualTo(Integer value) {
            addCriterion("rt99 <>", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99GreaterThan(Integer value) {
            addCriterion("rt99 >", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99GreaterThanOrEqualTo(Integer value) {
            addCriterion("rt99 >=", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99LessThan(Integer value) {
            addCriterion("rt99 <", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99LessThanOrEqualTo(Integer value) {
            addCriterion("rt99 <=", value, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99In(List<Integer> values) {
            addCriterion("rt99 in", values, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99NotIn(List<Integer> values) {
            addCriterion("rt99 not in", values, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99Between(Integer value1, Integer value2) {
            addCriterion("rt99 between", value1, value2, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt99NotBetween(Integer value1, Integer value2) {
            addCriterion("rt99 not between", value1, value2, "rt99");
            return (Criteria) this;
        }

        public Criteria andRt90IsNull() {
            addCriterion("rt90 is null");
            return (Criteria) this;
        }

        public Criteria andRt90IsNotNull() {
            addCriterion("rt90 is not null");
            return (Criteria) this;
        }

        public Criteria andRt90EqualTo(Integer value) {
            addCriterion("rt90 =", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90NotEqualTo(Integer value) {
            addCriterion("rt90 <>", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90GreaterThan(Integer value) {
            addCriterion("rt90 >", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90GreaterThanOrEqualTo(Integer value) {
            addCriterion("rt90 >=", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90LessThan(Integer value) {
            addCriterion("rt90 <", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90LessThanOrEqualTo(Integer value) {
            addCriterion("rt90 <=", value, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90In(List<Integer> values) {
            addCriterion("rt90 in", values, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90NotIn(List<Integer> values) {
            addCriterion("rt90 not in", values, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90Between(Integer value1, Integer value2) {
            addCriterion("rt90 between", value1, value2, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt90NotBetween(Integer value1, Integer value2) {
            addCriterion("rt90 not between", value1, value2, "rt90");
            return (Criteria) this;
        }

        public Criteria andRt70IsNull() {
            addCriterion("rt70 is null");
            return (Criteria) this;
        }

        public Criteria andRt70IsNotNull() {
            addCriterion("rt70 is not null");
            return (Criteria) this;
        }

        public Criteria andRt70EqualTo(Integer value) {
            addCriterion("rt70 =", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70NotEqualTo(Integer value) {
            addCriterion("rt70 <>", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70GreaterThan(Integer value) {
            addCriterion("rt70 >", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70GreaterThanOrEqualTo(Integer value) {
            addCriterion("rt70 >=", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70LessThan(Integer value) {
            addCriterion("rt70 <", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70LessThanOrEqualTo(Integer value) {
            addCriterion("rt70 <=", value, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70In(List<Integer> values) {
            addCriterion("rt70 in", values, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70NotIn(List<Integer> values) {
            addCriterion("rt70 not in", values, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70Between(Integer value1, Integer value2) {
            addCriterion("rt70 between", value1, value2, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt70NotBetween(Integer value1, Integer value2) {
            addCriterion("rt70 not between", value1, value2, "rt70");
            return (Criteria) this;
        }

        public Criteria andRt50IsNull() {
            addCriterion("rt50 is null");
            return (Criteria) this;
        }

        public Criteria andRt50IsNotNull() {
            addCriterion("rt50 is not null");
            return (Criteria) this;
        }

        public Criteria andRt50EqualTo(Integer value) {
            addCriterion("rt50 =", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50NotEqualTo(Integer value) {
            addCriterion("rt50 <>", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50GreaterThan(Integer value) {
            addCriterion("rt50 >", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50GreaterThanOrEqualTo(Integer value) {
            addCriterion("rt50 >=", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50LessThan(Integer value) {
            addCriterion("rt50 <", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50LessThanOrEqualTo(Integer value) {
            addCriterion("rt50 <=", value, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50In(List<Integer> values) {
            addCriterion("rt50 in", values, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50NotIn(List<Integer> values) {
            addCriterion("rt50 not in", values, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50Between(Integer value1, Integer value2) {
            addCriterion("rt50 between", value1, value2, "rt50");
            return (Criteria) this;
        }

        public Criteria andRt50NotBetween(Integer value1, Integer value2) {
            addCriterion("rt50 not between", value1, value2, "rt50");
            return (Criteria) this;
        }

        public Criteria andConnDurationIsNull() {
            addCriterion("conn_duration is null");
            return (Criteria) this;
        }

        public Criteria andConnDurationIsNotNull() {
            addCriterion("conn_duration is not null");
            return (Criteria) this;
        }

        public Criteria andConnDurationEqualTo(Integer value) {
            addCriterion("conn_duration =", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationNotEqualTo(Integer value) {
            addCriterion("conn_duration <>", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationGreaterThan(Integer value) {
            addCriterion("conn_duration >", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationGreaterThanOrEqualTo(Integer value) {
            addCriterion("conn_duration >=", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationLessThan(Integer value) {
            addCriterion("conn_duration <", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationLessThanOrEqualTo(Integer value) {
            addCriterion("conn_duration <=", value, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationIn(List<Integer> values) {
            addCriterion("conn_duration in", values, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationNotIn(List<Integer> values) {
            addCriterion("conn_duration not in", values, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationBetween(Integer value1, Integer value2) {
            addCriterion("conn_duration between", value1, value2, "connDuration");
            return (Criteria) this;
        }

        public Criteria andConnDurationNotBetween(Integer value1, Integer value2) {
            addCriterion("conn_duration not between", value1, value2, "connDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationIsNull() {
            addCriterion("recv_duration is null");
            return (Criteria) this;
        }

        public Criteria andRecvDurationIsNotNull() {
            addCriterion("recv_duration is not null");
            return (Criteria) this;
        }

        public Criteria andRecvDurationEqualTo(Integer value) {
            addCriterion("recv_duration =", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationNotEqualTo(Integer value) {
            addCriterion("recv_duration <>", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationGreaterThan(Integer value) {
            addCriterion("recv_duration >", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationGreaterThanOrEqualTo(Integer value) {
            addCriterion("recv_duration >=", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationLessThan(Integer value) {
            addCriterion("recv_duration <", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationLessThanOrEqualTo(Integer value) {
            addCriterion("recv_duration <=", value, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationIn(List<Integer> values) {
            addCriterion("recv_duration in", values, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationNotIn(List<Integer> values) {
            addCriterion("recv_duration not in", values, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationBetween(Integer value1, Integer value2) {
            addCriterion("recv_duration between", value1, value2, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andRecvDurationNotBetween(Integer value1, Integer value2) {
            addCriterion("recv_duration not between", value1, value2, "recvDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationIsNull() {
            addCriterion("send_duration is null");
            return (Criteria) this;
        }

        public Criteria andSendDurationIsNotNull() {
            addCriterion("send_duration is not null");
            return (Criteria) this;
        }

        public Criteria andSendDurationEqualTo(Integer value) {
            addCriterion("send_duration =", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationNotEqualTo(Integer value) {
            addCriterion("send_duration <>", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationGreaterThan(Integer value) {
            addCriterion("send_duration >", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationGreaterThanOrEqualTo(Integer value) {
            addCriterion("send_duration >=", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationLessThan(Integer value) {
            addCriterion("send_duration <", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationLessThanOrEqualTo(Integer value) {
            addCriterion("send_duration <=", value, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationIn(List<Integer> values) {
            addCriterion("send_duration in", values, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationNotIn(List<Integer> values) {
            addCriterion("send_duration not in", values, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationBetween(Integer value1, Integer value2) {
            addCriterion("send_duration between", value1, value2, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andSendDurationNotBetween(Integer value1, Integer value2) {
            addCriterion("send_duration not between", value1, value2, "sendDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationIsNull() {
            addCriterion("wait_duration is null");
            return (Criteria) this;
        }

        public Criteria andWaitDurationIsNotNull() {
            addCriterion("wait_duration is not null");
            return (Criteria) this;
        }

        public Criteria andWaitDurationEqualTo(Integer value) {
            addCriterion("wait_duration =", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationNotEqualTo(Integer value) {
            addCriterion("wait_duration <>", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationGreaterThan(Integer value) {
            addCriterion("wait_duration >", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationGreaterThanOrEqualTo(Integer value) {
            addCriterion("wait_duration >=", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationLessThan(Integer value) {
            addCriterion("wait_duration <", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationLessThanOrEqualTo(Integer value) {
            addCriterion("wait_duration <=", value, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationIn(List<Integer> values) {
            addCriterion("wait_duration in", values, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationNotIn(List<Integer> values) {
            addCriterion("wait_duration not in", values, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationBetween(Integer value1, Integer value2) {
            addCriterion("wait_duration between", value1, value2, "waitDuration");
            return (Criteria) this;
        }

        public Criteria andWaitDurationNotBetween(Integer value1, Integer value2) {
            addCriterion("wait_duration not between", value1, value2, "waitDuration");
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

        public Criteria andApiUriIsNull() {
            addCriterion("api_uri is null");
            return (Criteria) this;
        }

        public Criteria andApiUriIsNotNull() {
            addCriterion("api_uri is not null");
            return (Criteria) this;
        }

        public Criteria andApiUriEqualTo(String value) {
            addCriterion("api_uri =", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriNotEqualTo(String value) {
            addCriterion("api_uri <>", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriGreaterThan(String value) {
            addCriterion("api_uri >", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriGreaterThanOrEqualTo(String value) {
            addCriterion("api_uri >=", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriLessThan(String value) {
            addCriterion("api_uri <", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriLessThanOrEqualTo(String value) {
            addCriterion("api_uri <=", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriLike(String value) {
            addCriterion("api_uri like", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriNotLike(String value) {
            addCriterion("api_uri not like", value, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriIn(List<String> values) {
            addCriterion("api_uri in", values, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriNotIn(List<String> values) {
            addCriterion("api_uri not in", values, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriBetween(String value1, String value2) {
            addCriterion("api_uri between", value1, value2, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiUriNotBetween(String value1, String value2) {
            addCriterion("api_uri not between", value1, value2, "apiUri");
            return (Criteria) this;
        }

        public Criteria andApiMethodIsNull() {
            addCriterion("api_method is null");
            return (Criteria) this;
        }

        public Criteria andApiMethodIsNotNull() {
            addCriterion("api_method is not null");
            return (Criteria) this;
        }

        public Criteria andApiMethodEqualTo(String value) {
            addCriterion("api_method =", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodNotEqualTo(String value) {
            addCriterion("api_method <>", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodGreaterThan(String value) {
            addCriterion("api_method >", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodGreaterThanOrEqualTo(String value) {
            addCriterion("api_method >=", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodLessThan(String value) {
            addCriterion("api_method <", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodLessThanOrEqualTo(String value) {
            addCriterion("api_method <=", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodLike(String value) {
            addCriterion("api_method like", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodNotLike(String value) {
            addCriterion("api_method not like", value, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodIn(List<String> values) {
            addCriterion("api_method in", values, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodNotIn(List<String> values) {
            addCriterion("api_method not in", values, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodBetween(String value1, String value2) {
            addCriterion("api_method between", value1, value2, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andApiMethodNotBetween(String value1, String value2) {
            addCriterion("api_method not between", value1, value2, "apiMethod");
            return (Criteria) this;
        }

        public Criteria andSerialIdIsNull() {
            addCriterion("serial_id is null");
            return (Criteria) this;
        }

        public Criteria andSerialIdIsNotNull() {
            addCriterion("serial_id is not null");
            return (Criteria) this;
        }

        public Criteria andSerialIdEqualTo(Long value) {
            addCriterion("serial_id =", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdNotEqualTo(Long value) {
            addCriterion("serial_id <>", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdGreaterThan(Long value) {
            addCriterion("serial_id >", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdGreaterThanOrEqualTo(Long value) {
            addCriterion("serial_id >=", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdLessThan(Long value) {
            addCriterion("serial_id <", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdLessThanOrEqualTo(Long value) {
            addCriterion("serial_id <=", value, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdIn(List<Long> values) {
            addCriterion("serial_id in", values, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdNotIn(List<Long> values) {
            addCriterion("serial_id not in", values, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdBetween(Long value1, Long value2) {
            addCriterion("serial_id between", value1, value2, "serialId");
            return (Criteria) this;
        }

        public Criteria andSerialIdNotBetween(Long value1, Long value2) {
            addCriterion("serial_id not between", value1, value2, "serialId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNull() {
            addCriterion("task_id is null");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNotNull() {
            addCriterion("task_id is not null");
            return (Criteria) this;
        }

        public Criteria andTaskIdEqualTo(Long value) {
            addCriterion("task_id =", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotEqualTo(Long value) {
            addCriterion("task_id <>", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThan(Long value) {
            addCriterion("task_id >", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThanOrEqualTo(Long value) {
            addCriterion("task_id >=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThan(Long value) {
            addCriterion("task_id <", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThanOrEqualTo(Long value) {
            addCriterion("task_id <=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIn(List<Long> values) {
            addCriterion("task_id in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotIn(List<Long> values) {
            addCriterion("task_id not in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdBetween(Long value1, Long value2) {
            addCriterion("task_id between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotBetween(Long value1, Long value2) {
            addCriterion("task_id not between", value1, value2, "taskId");
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