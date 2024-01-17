package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class MibenchTaskExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MibenchTaskExample() {
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

        public Criteria andQpsIsNull() {
            addCriterion("qps is null");
            return (Criteria) this;
        }

        public Criteria andQpsIsNotNull() {
            addCriterion("qps is not null");
            return (Criteria) this;
        }

        public Criteria andQpsEqualTo(Integer value) {
            addCriterion("qps =", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsNotEqualTo(Integer value) {
            addCriterion("qps <>", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsGreaterThan(Integer value) {
            addCriterion("qps >", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsGreaterThanOrEqualTo(Integer value) {
            addCriterion("qps >=", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsLessThan(Integer value) {
            addCriterion("qps <", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsLessThanOrEqualTo(Integer value) {
            addCriterion("qps <=", value, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsIn(List<Integer> values) {
            addCriterion("qps in", values, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsNotIn(List<Integer> values) {
            addCriterion("qps not in", values, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsBetween(Integer value1, Integer value2) {
            addCriterion("qps between", value1, value2, "qps");
            return (Criteria) this;
        }

        public Criteria andQpsNotBetween(Integer value1, Integer value2) {
            addCriterion("qps not between", value1, value2, "qps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsIsNull() {
            addCriterion("origin_qps is null");
            return (Criteria) this;
        }

        public Criteria andOriginQpsIsNotNull() {
            addCriterion("origin_qps is not null");
            return (Criteria) this;
        }

        public Criteria andOriginQpsEqualTo(Integer value) {
            addCriterion("origin_qps =", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsNotEqualTo(Integer value) {
            addCriterion("origin_qps <>", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsGreaterThan(Integer value) {
            addCriterion("origin_qps >", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsGreaterThanOrEqualTo(Integer value) {
            addCriterion("origin_qps >=", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsLessThan(Integer value) {
            addCriterion("origin_qps <", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsLessThanOrEqualTo(Integer value) {
            addCriterion("origin_qps <=", value, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsIn(List<Integer> values) {
            addCriterion("origin_qps in", values, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsNotIn(List<Integer> values) {
            addCriterion("origin_qps not in", values, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsBetween(Integer value1, Integer value2) {
            addCriterion("origin_qps between", value1, value2, "originQps");
            return (Criteria) this;
        }

        public Criteria andOriginQpsNotBetween(Integer value1, Integer value2) {
            addCriterion("origin_qps not between", value1, value2, "originQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsIsNull() {
            addCriterion("max_qps is null");
            return (Criteria) this;
        }

        public Criteria andMaxQpsIsNotNull() {
            addCriterion("max_qps is not null");
            return (Criteria) this;
        }

        public Criteria andMaxQpsEqualTo(Integer value) {
            addCriterion("max_qps =", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsNotEqualTo(Integer value) {
            addCriterion("max_qps <>", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsGreaterThan(Integer value) {
            addCriterion("max_qps >", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_qps >=", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsLessThan(Integer value) {
            addCriterion("max_qps <", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsLessThanOrEqualTo(Integer value) {
            addCriterion("max_qps <=", value, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsIn(List<Integer> values) {
            addCriterion("max_qps in", values, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsNotIn(List<Integer> values) {
            addCriterion("max_qps not in", values, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsBetween(Integer value1, Integer value2) {
            addCriterion("max_qps between", value1, value2, "maxQps");
            return (Criteria) this;
        }

        public Criteria andMaxQpsNotBetween(Integer value1, Integer value2) {
            addCriterion("max_qps not between", value1, value2, "maxQps");
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

        public Criteria andSceneIdEqualTo(Integer value) {
            addCriterion("scene_id =", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotEqualTo(Integer value) {
            addCriterion("scene_id <>", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdGreaterThan(Integer value) {
            addCriterion("scene_id >", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_id >=", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdLessThan(Integer value) {
            addCriterion("scene_id <", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdLessThanOrEqualTo(Integer value) {
            addCriterion("scene_id <=", value, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdIn(List<Integer> values) {
            addCriterion("scene_id in", values, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotIn(List<Integer> values) {
            addCriterion("scene_id not in", values, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdBetween(Integer value1, Integer value2) {
            addCriterion("scene_id between", value1, value2, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSceneIdNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_id not between", value1, value2, "sceneId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdIsNull() {
            addCriterion("serial_link_id is null");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdIsNotNull() {
            addCriterion("serial_link_id is not null");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdEqualTo(Integer value) {
            addCriterion("serial_link_id =", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdNotEqualTo(Integer value) {
            addCriterion("serial_link_id <>", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdGreaterThan(Integer value) {
            addCriterion("serial_link_id >", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("serial_link_id >=", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdLessThan(Integer value) {
            addCriterion("serial_link_id <", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdLessThanOrEqualTo(Integer value) {
            addCriterion("serial_link_id <=", value, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdIn(List<Integer> values) {
            addCriterion("serial_link_id in", values, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdNotIn(List<Integer> values) {
            addCriterion("serial_link_id not in", values, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdBetween(Integer value1, Integer value2) {
            addCriterion("serial_link_id between", value1, value2, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSerialLinkIdNotBetween(Integer value1, Integer value2) {
            addCriterion("serial_link_id not between", value1, value2, "serialLinkId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdIsNull() {
            addCriterion("scene_api_id is null");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdIsNotNull() {
            addCriterion("scene_api_id is not null");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdEqualTo(Integer value) {
            addCriterion("scene_api_id =", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdNotEqualTo(Integer value) {
            addCriterion("scene_api_id <>", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdGreaterThan(Integer value) {
            addCriterion("scene_api_id >", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_api_id >=", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdLessThan(Integer value) {
            addCriterion("scene_api_id <", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdLessThanOrEqualTo(Integer value) {
            addCriterion("scene_api_id <=", value, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdIn(List<Integer> values) {
            addCriterion("scene_api_id in", values, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdNotIn(List<Integer> values) {
            addCriterion("scene_api_id not in", values, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdBetween(Integer value1, Integer value2) {
            addCriterion("scene_api_id between", value1, value2, "sceneApiId");
            return (Criteria) this;
        }

        public Criteria andSceneApiIdNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_api_id not between", value1, value2, "sceneApiId");
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

        public Criteria andTimeEqualTo(Integer value) {
            addCriterion("time =", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotEqualTo(Integer value) {
            addCriterion("time <>", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThan(Integer value) {
            addCriterion("time >", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("time >=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThan(Integer value) {
            addCriterion("time <", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeLessThanOrEqualTo(Integer value) {
            addCriterion("time <=", value, "time");
            return (Criteria) this;
        }

        public Criteria andTimeIn(List<Integer> values) {
            addCriterion("time in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotIn(List<Integer> values) {
            addCriterion("time not in", values, "time");
            return (Criteria) this;
        }

        public Criteria andTimeBetween(Integer value1, Integer value2) {
            addCriterion("time between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("time not between", value1, value2, "time");
            return (Criteria) this;
        }

        public Criteria andAgentNumIsNull() {
            addCriterion("agent_num is null");
            return (Criteria) this;
        }

        public Criteria andAgentNumIsNotNull() {
            addCriterion("agent_num is not null");
            return (Criteria) this;
        }

        public Criteria andAgentNumEqualTo(Integer value) {
            addCriterion("agent_num =", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumNotEqualTo(Integer value) {
            addCriterion("agent_num <>", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumGreaterThan(Integer value) {
            addCriterion("agent_num >", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("agent_num >=", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumLessThan(Integer value) {
            addCriterion("agent_num <", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumLessThanOrEqualTo(Integer value) {
            addCriterion("agent_num <=", value, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumIn(List<Integer> values) {
            addCriterion("agent_num in", values, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumNotIn(List<Integer> values) {
            addCriterion("agent_num not in", values, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumBetween(Integer value1, Integer value2) {
            addCriterion("agent_num between", value1, value2, "agentNum");
            return (Criteria) this;
        }

        public Criteria andAgentNumNotBetween(Integer value1, Integer value2) {
            addCriterion("agent_num not between", value1, value2, "agentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumIsNull() {
            addCriterion("finish_agent_num is null");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumIsNotNull() {
            addCriterion("finish_agent_num is not null");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumEqualTo(Integer value) {
            addCriterion("finish_agent_num =", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumNotEqualTo(Integer value) {
            addCriterion("finish_agent_num <>", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumGreaterThan(Integer value) {
            addCriterion("finish_agent_num >", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("finish_agent_num >=", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumLessThan(Integer value) {
            addCriterion("finish_agent_num <", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumLessThanOrEqualTo(Integer value) {
            addCriterion("finish_agent_num <=", value, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumIn(List<Integer> values) {
            addCriterion("finish_agent_num in", values, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumNotIn(List<Integer> values) {
            addCriterion("finish_agent_num not in", values, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumBetween(Integer value1, Integer value2) {
            addCriterion("finish_agent_num between", value1, value2, "finishAgentNum");
            return (Criteria) this;
        }

        public Criteria andFinishAgentNumNotBetween(Integer value1, Integer value2) {
            addCriterion("finish_agent_num not between", value1, value2, "finishAgentNum");
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

        public Criteria andStateIsNull() {
            addCriterion("state is null");
            return (Criteria) this;
        }

        public Criteria andStateIsNotNull() {
            addCriterion("state is not null");
            return (Criteria) this;
        }

        public Criteria andStateEqualTo(Integer value) {
            addCriterion("state =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(Integer value) {
            addCriterion("state <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(Integer value) {
            addCriterion("state >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("state >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(Integer value) {
            addCriterion("state <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(Integer value) {
            addCriterion("state <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<Integer> values) {
            addCriterion("state in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<Integer> values) {
            addCriterion("state not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(Integer value1, Integer value2) {
            addCriterion("state between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(Integer value1, Integer value2) {
            addCriterion("state not between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("version is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("version is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(Integer value) {
            addCriterion("version =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Integer value) {
            addCriterion("version <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Integer value) {
            addCriterion("version >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
            addCriterion("version >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Integer value) {
            addCriterion("version <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Integer value) {
            addCriterion("version <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Integer> values) {
            addCriterion("version in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Integer> values) {
            addCriterion("version not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Integer value1, Integer value2) {
            addCriterion("version between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Integer value1, Integer value2) {
            addCriterion("version not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andSuccessNumIsNull() {
            addCriterion("success_num is null");
            return (Criteria) this;
        }

        public Criteria andSuccessNumIsNotNull() {
            addCriterion("success_num is not null");
            return (Criteria) this;
        }

        public Criteria andSuccessNumEqualTo(Long value) {
            addCriterion("success_num =", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumNotEqualTo(Long value) {
            addCriterion("success_num <>", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumGreaterThan(Long value) {
            addCriterion("success_num >", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumGreaterThanOrEqualTo(Long value) {
            addCriterion("success_num >=", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumLessThan(Long value) {
            addCriterion("success_num <", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumLessThanOrEqualTo(Long value) {
            addCriterion("success_num <=", value, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumIn(List<Long> values) {
            addCriterion("success_num in", values, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumNotIn(List<Long> values) {
            addCriterion("success_num not in", values, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumBetween(Long value1, Long value2) {
            addCriterion("success_num between", value1, value2, "successNum");
            return (Criteria) this;
        }

        public Criteria andSuccessNumNotBetween(Long value1, Long value2) {
            addCriterion("success_num not between", value1, value2, "successNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumIsNull() {
            addCriterion("failure_num is null");
            return (Criteria) this;
        }

        public Criteria andFailureNumIsNotNull() {
            addCriterion("failure_num is not null");
            return (Criteria) this;
        }

        public Criteria andFailureNumEqualTo(Long value) {
            addCriterion("failure_num =", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumNotEqualTo(Long value) {
            addCriterion("failure_num <>", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumGreaterThan(Long value) {
            addCriterion("failure_num >", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumGreaterThanOrEqualTo(Long value) {
            addCriterion("failure_num >=", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumLessThan(Long value) {
            addCriterion("failure_num <", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumLessThanOrEqualTo(Long value) {
            addCriterion("failure_num <=", value, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumIn(List<Long> values) {
            addCriterion("failure_num in", values, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumNotIn(List<Long> values) {
            addCriterion("failure_num not in", values, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumBetween(Long value1, Long value2) {
            addCriterion("failure_num between", value1, value2, "failureNum");
            return (Criteria) this;
        }

        public Criteria andFailureNumNotBetween(Long value1, Long value2) {
            addCriterion("failure_num not between", value1, value2, "failureNum");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIsNull() {
            addCriterion("task_type is null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIsNotNull() {
            addCriterion("task_type is not null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeEqualTo(Integer value) {
            addCriterion("task_type =", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotEqualTo(Integer value) {
            addCriterion("task_type <>", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThan(Integer value) {
            addCriterion("task_type >", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("task_type >=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThan(Integer value) {
            addCriterion("task_type <", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThanOrEqualTo(Integer value) {
            addCriterion("task_type <=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIn(List<Integer> values) {
            addCriterion("task_type in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotIn(List<Integer> values) {
            addCriterion("task_type not in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeBetween(Integer value1, Integer value2) {
            addCriterion("task_type between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("task_type not between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdIsNull() {
            addCriterion("parent_task_id is null");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdIsNotNull() {
            addCriterion("parent_task_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdEqualTo(Integer value) {
            addCriterion("parent_task_id =", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdNotEqualTo(Integer value) {
            addCriterion("parent_task_id <>", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdGreaterThan(Integer value) {
            addCriterion("parent_task_id >", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("parent_task_id >=", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdLessThan(Integer value) {
            addCriterion("parent_task_id <", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdLessThanOrEqualTo(Integer value) {
            addCriterion("parent_task_id <=", value, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdIn(List<Integer> values) {
            addCriterion("parent_task_id in", values, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdNotIn(List<Integer> values) {
            addCriterion("parent_task_id not in", values, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdBetween(Integer value1, Integer value2) {
            addCriterion("parent_task_id between", value1, value2, "parentTaskId");
            return (Criteria) this;
        }

        public Criteria andParentTaskIdNotBetween(Integer value1, Integer value2) {
            addCriterion("parent_task_id not between", value1, value2, "parentTaskId");
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

        public Criteria andReqParamTypeIsNull() {
            addCriterion("req_param_type is null");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeIsNotNull() {
            addCriterion("req_param_type is not null");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeEqualTo(Integer value) {
            addCriterion("req_param_type =", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeNotEqualTo(Integer value) {
            addCriterion("req_param_type <>", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeGreaterThan(Integer value) {
            addCriterion("req_param_type >", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("req_param_type >=", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeLessThan(Integer value) {
            addCriterion("req_param_type <", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeLessThanOrEqualTo(Integer value) {
            addCriterion("req_param_type <=", value, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeIn(List<Integer> values) {
            addCriterion("req_param_type in", values, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeNotIn(List<Integer> values) {
            addCriterion("req_param_type not in", values, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeBetween(Integer value1, Integer value2) {
            addCriterion("req_param_type between", value1, value2, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andReqParamTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("req_param_type not between", value1, value2, "reqParamType");
            return (Criteria) this;
        }

        public Criteria andOkIsNull() {
            addCriterion("ok is null");
            return (Criteria) this;
        }

        public Criteria andOkIsNotNull() {
            addCriterion("ok is not null");
            return (Criteria) this;
        }

        public Criteria andOkEqualTo(Boolean value) {
            addCriterion("ok =", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkNotEqualTo(Boolean value) {
            addCriterion("ok <>", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkGreaterThan(Boolean value) {
            addCriterion("ok >", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkGreaterThanOrEqualTo(Boolean value) {
            addCriterion("ok >=", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkLessThan(Boolean value) {
            addCriterion("ok <", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkLessThanOrEqualTo(Boolean value) {
            addCriterion("ok <=", value, "ok");
            return (Criteria) this;
        }

        public Criteria andOkIn(List<Boolean> values) {
            addCriterion("ok in", values, "ok");
            return (Criteria) this;
        }

        public Criteria andOkNotIn(List<Boolean> values) {
            addCriterion("ok not in", values, "ok");
            return (Criteria) this;
        }

        public Criteria andOkBetween(Boolean value1, Boolean value2) {
            addCriterion("ok between", value1, value2, "ok");
            return (Criteria) this;
        }

        public Criteria andOkNotBetween(Boolean value1, Boolean value2) {
            addCriterion("ok not between", value1, value2, "ok");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumIsNull() {
            addCriterion("connect_task_num is null");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumIsNotNull() {
            addCriterion("connect_task_num is not null");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumEqualTo(Integer value) {
            addCriterion("connect_task_num =", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumNotEqualTo(Integer value) {
            addCriterion("connect_task_num <>", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumGreaterThan(Integer value) {
            addCriterion("connect_task_num >", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("connect_task_num >=", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumLessThan(Integer value) {
            addCriterion("connect_task_num <", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumLessThanOrEqualTo(Integer value) {
            addCriterion("connect_task_num <=", value, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumIn(List<Integer> values) {
            addCriterion("connect_task_num in", values, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumNotIn(List<Integer> values) {
            addCriterion("connect_task_num not in", values, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumBetween(Integer value1, Integer value2) {
            addCriterion("connect_task_num between", value1, value2, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andConnectTaskNumNotBetween(Integer value1, Integer value2) {
            addCriterion("connect_task_num not between", value1, value2, "connectTaskNum");
            return (Criteria) this;
        }

        public Criteria andDebugRtIsNull() {
            addCriterion("debug_rt is null");
            return (Criteria) this;
        }

        public Criteria andDebugRtIsNotNull() {
            addCriterion("debug_rt is not null");
            return (Criteria) this;
        }

        public Criteria andDebugRtEqualTo(Integer value) {
            addCriterion("debug_rt =", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtNotEqualTo(Integer value) {
            addCriterion("debug_rt <>", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtGreaterThan(Integer value) {
            addCriterion("debug_rt >", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtGreaterThanOrEqualTo(Integer value) {
            addCriterion("debug_rt >=", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtLessThan(Integer value) {
            addCriterion("debug_rt <", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtLessThanOrEqualTo(Integer value) {
            addCriterion("debug_rt <=", value, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtIn(List<Integer> values) {
            addCriterion("debug_rt in", values, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtNotIn(List<Integer> values) {
            addCriterion("debug_rt not in", values, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtBetween(Integer value1, Integer value2) {
            addCriterion("debug_rt between", value1, value2, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugRtNotBetween(Integer value1, Integer value2) {
            addCriterion("debug_rt not between", value1, value2, "debugRt");
            return (Criteria) this;
        }

        public Criteria andDebugSizeIsNull() {
            addCriterion("debug_size is null");
            return (Criteria) this;
        }

        public Criteria andDebugSizeIsNotNull() {
            addCriterion("debug_size is not null");
            return (Criteria) this;
        }

        public Criteria andDebugSizeEqualTo(Integer value) {
            addCriterion("debug_size =", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeNotEqualTo(Integer value) {
            addCriterion("debug_size <>", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeGreaterThan(Integer value) {
            addCriterion("debug_size >", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeGreaterThanOrEqualTo(Integer value) {
            addCriterion("debug_size >=", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeLessThan(Integer value) {
            addCriterion("debug_size <", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeLessThanOrEqualTo(Integer value) {
            addCriterion("debug_size <=", value, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeIn(List<Integer> values) {
            addCriterion("debug_size in", values, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeNotIn(List<Integer> values) {
            addCriterion("debug_size not in", values, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeBetween(Integer value1, Integer value2) {
            addCriterion("debug_size between", value1, value2, "debugSize");
            return (Criteria) this;
        }

        public Criteria andDebugSizeNotBetween(Integer value1, Integer value2) {
            addCriterion("debug_size not between", value1, value2, "debugSize");
            return (Criteria) this;
        }

        public Criteria andBenchModeIsNull() {
            addCriterion("bench_mode is null");
            return (Criteria) this;
        }

        public Criteria andBenchModeIsNotNull() {
            addCriterion("bench_mode is not null");
            return (Criteria) this;
        }

        public Criteria andBenchModeEqualTo(Integer value) {
            addCriterion("bench_mode =", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeNotEqualTo(Integer value) {
            addCriterion("bench_mode <>", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeGreaterThan(Integer value) {
            addCriterion("bench_mode >", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeGreaterThanOrEqualTo(Integer value) {
            addCriterion("bench_mode >=", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeLessThan(Integer value) {
            addCriterion("bench_mode <", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeLessThanOrEqualTo(Integer value) {
            addCriterion("bench_mode <=", value, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeIn(List<Integer> values) {
            addCriterion("bench_mode in", values, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeNotIn(List<Integer> values) {
            addCriterion("bench_mode not in", values, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeBetween(Integer value1, Integer value2) {
            addCriterion("bench_mode between", value1, value2, "benchMode");
            return (Criteria) this;
        }

        public Criteria andBenchModeNotBetween(Integer value1, Integer value2) {
            addCriterion("bench_mode not between", value1, value2, "benchMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeIsNull() {
            addCriterion("increase_mode is null");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeIsNotNull() {
            addCriterion("increase_mode is not null");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeEqualTo(Integer value) {
            addCriterion("increase_mode =", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeNotEqualTo(Integer value) {
            addCriterion("increase_mode <>", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeGreaterThan(Integer value) {
            addCriterion("increase_mode >", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeGreaterThanOrEqualTo(Integer value) {
            addCriterion("increase_mode >=", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeLessThan(Integer value) {
            addCriterion("increase_mode <", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeLessThanOrEqualTo(Integer value) {
            addCriterion("increase_mode <=", value, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeIn(List<Integer> values) {
            addCriterion("increase_mode in", values, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeNotIn(List<Integer> values) {
            addCriterion("increase_mode not in", values, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeBetween(Integer value1, Integer value2) {
            addCriterion("increase_mode between", value1, value2, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreaseModeNotBetween(Integer value1, Integer value2) {
            addCriterion("increase_mode not between", value1, value2, "increaseMode");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentIsNull() {
            addCriterion("increase_percent is null");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentIsNotNull() {
            addCriterion("increase_percent is not null");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentEqualTo(Integer value) {
            addCriterion("increase_percent =", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentNotEqualTo(Integer value) {
            addCriterion("increase_percent <>", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentGreaterThan(Integer value) {
            addCriterion("increase_percent >", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentGreaterThanOrEqualTo(Integer value) {
            addCriterion("increase_percent >=", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentLessThan(Integer value) {
            addCriterion("increase_percent <", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentLessThanOrEqualTo(Integer value) {
            addCriterion("increase_percent <=", value, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentIn(List<Integer> values) {
            addCriterion("increase_percent in", values, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentNotIn(List<Integer> values) {
            addCriterion("increase_percent not in", values, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentBetween(Integer value1, Integer value2) {
            addCriterion("increase_percent between", value1, value2, "increasePercent");
            return (Criteria) this;
        }

        public Criteria andIncreasePercentNotBetween(Integer value1, Integer value2) {
            addCriterion("increase_percent not between", value1, value2, "increasePercent");
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