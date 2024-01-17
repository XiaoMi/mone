package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class AgentApplyInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AgentApplyInfoExample() {
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

        public Criteria andApplyUserIsNull() {
            addCriterion("apply_user is null");
            return (Criteria) this;
        }

        public Criteria andApplyUserIsNotNull() {
            addCriterion("apply_user is not null");
            return (Criteria) this;
        }

        public Criteria andApplyUserEqualTo(String value) {
            addCriterion("apply_user =", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserNotEqualTo(String value) {
            addCriterion("apply_user <>", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserGreaterThan(String value) {
            addCriterion("apply_user >", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserGreaterThanOrEqualTo(String value) {
            addCriterion("apply_user >=", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserLessThan(String value) {
            addCriterion("apply_user <", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserLessThanOrEqualTo(String value) {
            addCriterion("apply_user <=", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserLike(String value) {
            addCriterion("apply_user like", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserNotLike(String value) {
            addCriterion("apply_user not like", value, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserIn(List<String> values) {
            addCriterion("apply_user in", values, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserNotIn(List<String> values) {
            addCriterion("apply_user not in", values, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserBetween(String value1, String value2) {
            addCriterion("apply_user between", value1, value2, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyUserNotBetween(String value1, String value2) {
            addCriterion("apply_user not between", value1, value2, "applyUser");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdIsNull() {
            addCriterion("apply_org_id is null");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdIsNotNull() {
            addCriterion("apply_org_id is not null");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdEqualTo(String value) {
            addCriterion("apply_org_id =", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdNotEqualTo(String value) {
            addCriterion("apply_org_id <>", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdGreaterThan(String value) {
            addCriterion("apply_org_id >", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdGreaterThanOrEqualTo(String value) {
            addCriterion("apply_org_id >=", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdLessThan(String value) {
            addCriterion("apply_org_id <", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdLessThanOrEqualTo(String value) {
            addCriterion("apply_org_id <=", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdLike(String value) {
            addCriterion("apply_org_id like", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdNotLike(String value) {
            addCriterion("apply_org_id not like", value, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdIn(List<String> values) {
            addCriterion("apply_org_id in", values, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdNotIn(List<String> values) {
            addCriterion("apply_org_id not in", values, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdBetween(String value1, String value2) {
            addCriterion("apply_org_id between", value1, value2, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgIdNotBetween(String value1, String value2) {
            addCriterion("apply_org_id not between", value1, value2, "applyOrgId");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameIsNull() {
            addCriterion("apply_org_name is null");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameIsNotNull() {
            addCriterion("apply_org_name is not null");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameEqualTo(String value) {
            addCriterion("apply_org_name =", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameNotEqualTo(String value) {
            addCriterion("apply_org_name <>", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameGreaterThan(String value) {
            addCriterion("apply_org_name >", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameGreaterThanOrEqualTo(String value) {
            addCriterion("apply_org_name >=", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameLessThan(String value) {
            addCriterion("apply_org_name <", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameLessThanOrEqualTo(String value) {
            addCriterion("apply_org_name <=", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameLike(String value) {
            addCriterion("apply_org_name like", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameNotLike(String value) {
            addCriterion("apply_org_name not like", value, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameIn(List<String> values) {
            addCriterion("apply_org_name in", values, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameNotIn(List<String> values) {
            addCriterion("apply_org_name not in", values, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameBetween(String value1, String value2) {
            addCriterion("apply_org_name between", value1, value2, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andApplyOrgNameNotBetween(String value1, String value2) {
            addCriterion("apply_org_name not between", value1, value2, "applyOrgName");
            return (Criteria) this;
        }

        public Criteria andAgentIpIsNull() {
            addCriterion("agent_ip is null");
            return (Criteria) this;
        }

        public Criteria andAgentIpIsNotNull() {
            addCriterion("agent_ip is not null");
            return (Criteria) this;
        }

        public Criteria andAgentIpEqualTo(String value) {
            addCriterion("agent_ip =", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotEqualTo(String value) {
            addCriterion("agent_ip <>", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpGreaterThan(String value) {
            addCriterion("agent_ip >", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpGreaterThanOrEqualTo(String value) {
            addCriterion("agent_ip >=", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLessThan(String value) {
            addCriterion("agent_ip <", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLessThanOrEqualTo(String value) {
            addCriterion("agent_ip <=", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLike(String value) {
            addCriterion("agent_ip like", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotLike(String value) {
            addCriterion("agent_ip not like", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpIn(List<String> values) {
            addCriterion("agent_ip in", values, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotIn(List<String> values) {
            addCriterion("agent_ip not in", values, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpBetween(String value1, String value2) {
            addCriterion("agent_ip between", value1, value2, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotBetween(String value1, String value2) {
            addCriterion("agent_ip not between", value1, value2, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameIsNull() {
            addCriterion("agent_hostname is null");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameIsNotNull() {
            addCriterion("agent_hostname is not null");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameEqualTo(String value) {
            addCriterion("agent_hostname =", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameNotEqualTo(String value) {
            addCriterion("agent_hostname <>", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameGreaterThan(String value) {
            addCriterion("agent_hostname >", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameGreaterThanOrEqualTo(String value) {
            addCriterion("agent_hostname >=", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameLessThan(String value) {
            addCriterion("agent_hostname <", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameLessThanOrEqualTo(String value) {
            addCriterion("agent_hostname <=", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameLike(String value) {
            addCriterion("agent_hostname like", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameNotLike(String value) {
            addCriterion("agent_hostname not like", value, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameIn(List<String> values) {
            addCriterion("agent_hostname in", values, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameNotIn(List<String> values) {
            addCriterion("agent_hostname not in", values, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameBetween(String value1, String value2) {
            addCriterion("agent_hostname between", value1, value2, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andAgentHostnameNotBetween(String value1, String value2) {
            addCriterion("agent_hostname not between", value1, value2, "agentHostname");
            return (Criteria) this;
        }

        public Criteria andApplyStatusIsNull() {
            addCriterion("apply_status is null");
            return (Criteria) this;
        }

        public Criteria andApplyStatusIsNotNull() {
            addCriterion("apply_status is not null");
            return (Criteria) this;
        }

        public Criteria andApplyStatusEqualTo(Integer value) {
            addCriterion("apply_status =", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusNotEqualTo(Integer value) {
            addCriterion("apply_status <>", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusGreaterThan(Integer value) {
            addCriterion("apply_status >", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("apply_status >=", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusLessThan(Integer value) {
            addCriterion("apply_status <", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusLessThanOrEqualTo(Integer value) {
            addCriterion("apply_status <=", value, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusIn(List<Integer> values) {
            addCriterion("apply_status in", values, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusNotIn(List<Integer> values) {
            addCriterion("apply_status not in", values, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusBetween(Integer value1, Integer value2) {
            addCriterion("apply_status between", value1, value2, "applyStatus");
            return (Criteria) this;
        }

        public Criteria andApplyStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("apply_status not between", value1, value2, "applyStatus");
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