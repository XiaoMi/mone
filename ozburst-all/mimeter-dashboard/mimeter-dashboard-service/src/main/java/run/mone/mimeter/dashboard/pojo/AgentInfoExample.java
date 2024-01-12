package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class AgentInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AgentInfoExample() {
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

        public Criteria andServerNameIsNull() {
            addCriterion("server_name is null");
            return (Criteria) this;
        }

        public Criteria andServerNameIsNotNull() {
            addCriterion("server_name is not null");
            return (Criteria) this;
        }

        public Criteria andServerNameEqualTo(String value) {
            addCriterion("server_name =", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameNotEqualTo(String value) {
            addCriterion("server_name <>", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameGreaterThan(String value) {
            addCriterion("server_name >", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameGreaterThanOrEqualTo(String value) {
            addCriterion("server_name >=", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameLessThan(String value) {
            addCriterion("server_name <", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameLessThanOrEqualTo(String value) {
            addCriterion("server_name <=", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameLike(String value) {
            addCriterion("server_name like", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameNotLike(String value) {
            addCriterion("server_name not like", value, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameIn(List<String> values) {
            addCriterion("server_name in", values, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameNotIn(List<String> values) {
            addCriterion("server_name not in", values, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameBetween(String value1, String value2) {
            addCriterion("server_name between", value1, value2, "serverName");
            return (Criteria) this;
        }

        public Criteria andServerNameNotBetween(String value1, String value2) {
            addCriterion("server_name not between", value1, value2, "serverName");
            return (Criteria) this;
        }

        public Criteria andIpIsNull() {
            addCriterion("ip is null");
            return (Criteria) this;
        }

        public Criteria andIpIsNotNull() {
            addCriterion("ip is not null");
            return (Criteria) this;
        }

        public Criteria andIpEqualTo(String value) {
            addCriterion("ip =", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotEqualTo(String value) {
            addCriterion("ip <>", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThan(String value) {
            addCriterion("ip >", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThanOrEqualTo(String value) {
            addCriterion("ip >=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThan(String value) {
            addCriterion("ip <", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThanOrEqualTo(String value) {
            addCriterion("ip <=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLike(String value) {
            addCriterion("ip like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotLike(String value) {
            addCriterion("ip not like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpIn(List<String> values) {
            addCriterion("ip in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotIn(List<String> values) {
            addCriterion("ip not in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpBetween(String value1, String value2) {
            addCriterion("ip between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotBetween(String value1, String value2) {
            addCriterion("ip not between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andPortIsNull() {
            addCriterion("port is null");
            return (Criteria) this;
        }

        public Criteria andPortIsNotNull() {
            addCriterion("port is not null");
            return (Criteria) this;
        }

        public Criteria andPortEqualTo(Integer value) {
            addCriterion("port =", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotEqualTo(Integer value) {
            addCriterion("port <>", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortGreaterThan(Integer value) {
            addCriterion("port >", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortGreaterThanOrEqualTo(Integer value) {
            addCriterion("port >=", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortLessThan(Integer value) {
            addCriterion("port <", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortLessThanOrEqualTo(Integer value) {
            addCriterion("port <=", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortIn(List<Integer> values) {
            addCriterion("port in", values, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotIn(List<Integer> values) {
            addCriterion("port not in", values, "port");
            return (Criteria) this;
        }

        public Criteria andPortBetween(Integer value1, Integer value2) {
            addCriterion("port between", value1, value2, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotBetween(Integer value1, Integer value2) {
            addCriterion("port not between", value1, value2, "port");
            return (Criteria) this;
        }

        public Criteria andCpuIsNull() {
            addCriterion("cpu is null");
            return (Criteria) this;
        }

        public Criteria andCpuIsNotNull() {
            addCriterion("cpu is not null");
            return (Criteria) this;
        }

        public Criteria andCpuEqualTo(Integer value) {
            addCriterion("cpu =", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuNotEqualTo(Integer value) {
            addCriterion("cpu <>", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuGreaterThan(Integer value) {
            addCriterion("cpu >", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuGreaterThanOrEqualTo(Integer value) {
            addCriterion("cpu >=", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuLessThan(Integer value) {
            addCriterion("cpu <", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuLessThanOrEqualTo(Integer value) {
            addCriterion("cpu <=", value, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuIn(List<Integer> values) {
            addCriterion("cpu in", values, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuNotIn(List<Integer> values) {
            addCriterion("cpu not in", values, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuBetween(Integer value1, Integer value2) {
            addCriterion("cpu between", value1, value2, "cpu");
            return (Criteria) this;
        }

        public Criteria andCpuNotBetween(Integer value1, Integer value2) {
            addCriterion("cpu not between", value1, value2, "cpu");
            return (Criteria) this;
        }

        public Criteria andMemIsNull() {
            addCriterion("mem is null");
            return (Criteria) this;
        }

        public Criteria andMemIsNotNull() {
            addCriterion("mem is not null");
            return (Criteria) this;
        }

        public Criteria andMemEqualTo(Long value) {
            addCriterion("mem =", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemNotEqualTo(Long value) {
            addCriterion("mem <>", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemGreaterThan(Long value) {
            addCriterion("mem >", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemGreaterThanOrEqualTo(Long value) {
            addCriterion("mem >=", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemLessThan(Long value) {
            addCriterion("mem <", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemLessThanOrEqualTo(Long value) {
            addCriterion("mem <=", value, "mem");
            return (Criteria) this;
        }

        public Criteria andMemIn(List<Long> values) {
            addCriterion("mem in", values, "mem");
            return (Criteria) this;
        }

        public Criteria andMemNotIn(List<Long> values) {
            addCriterion("mem not in", values, "mem");
            return (Criteria) this;
        }

        public Criteria andMemBetween(Long value1, Long value2) {
            addCriterion("mem between", value1, value2, "mem");
            return (Criteria) this;
        }

        public Criteria andMemNotBetween(Long value1, Long value2) {
            addCriterion("mem not between", value1, value2, "mem");
            return (Criteria) this;
        }

        public Criteria andUseCpuIsNull() {
            addCriterion("use_cpu is null");
            return (Criteria) this;
        }

        public Criteria andUseCpuIsNotNull() {
            addCriterion("use_cpu is not null");
            return (Criteria) this;
        }

        public Criteria andUseCpuEqualTo(Integer value) {
            addCriterion("use_cpu =", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuNotEqualTo(Integer value) {
            addCriterion("use_cpu <>", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuGreaterThan(Integer value) {
            addCriterion("use_cpu >", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuGreaterThanOrEqualTo(Integer value) {
            addCriterion("use_cpu >=", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuLessThan(Integer value) {
            addCriterion("use_cpu <", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuLessThanOrEqualTo(Integer value) {
            addCriterion("use_cpu <=", value, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuIn(List<Integer> values) {
            addCriterion("use_cpu in", values, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuNotIn(List<Integer> values) {
            addCriterion("use_cpu not in", values, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuBetween(Integer value1, Integer value2) {
            addCriterion("use_cpu between", value1, value2, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseCpuNotBetween(Integer value1, Integer value2) {
            addCriterion("use_cpu not between", value1, value2, "useCpu");
            return (Criteria) this;
        }

        public Criteria andUseMemIsNull() {
            addCriterion("use_mem is null");
            return (Criteria) this;
        }

        public Criteria andUseMemIsNotNull() {
            addCriterion("use_mem is not null");
            return (Criteria) this;
        }

        public Criteria andUseMemEqualTo(Long value) {
            addCriterion("use_mem =", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemNotEqualTo(Long value) {
            addCriterion("use_mem <>", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemGreaterThan(Long value) {
            addCriterion("use_mem >", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemGreaterThanOrEqualTo(Long value) {
            addCriterion("use_mem >=", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemLessThan(Long value) {
            addCriterion("use_mem <", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemLessThanOrEqualTo(Long value) {
            addCriterion("use_mem <=", value, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemIn(List<Long> values) {
            addCriterion("use_mem in", values, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemNotIn(List<Long> values) {
            addCriterion("use_mem not in", values, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemBetween(Long value1, Long value2) {
            addCriterion("use_mem between", value1, value2, "useMem");
            return (Criteria) this;
        }

        public Criteria andUseMemNotBetween(Long value1, Long value2) {
            addCriterion("use_mem not between", value1, value2, "useMem");
            return (Criteria) this;
        }

        public Criteria andHostnameIsNull() {
            addCriterion("hostname is null");
            return (Criteria) this;
        }

        public Criteria andHostnameIsNotNull() {
            addCriterion("hostname is not null");
            return (Criteria) this;
        }

        public Criteria andHostnameEqualTo(String value) {
            addCriterion("hostname =", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameNotEqualTo(String value) {
            addCriterion("hostname <>", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameGreaterThan(String value) {
            addCriterion("hostname >", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameGreaterThanOrEqualTo(String value) {
            addCriterion("hostname >=", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameLessThan(String value) {
            addCriterion("hostname <", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameLessThanOrEqualTo(String value) {
            addCriterion("hostname <=", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameLike(String value) {
            addCriterion("hostname like", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameNotLike(String value) {
            addCriterion("hostname not like", value, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameIn(List<String> values) {
            addCriterion("hostname in", values, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameNotIn(List<String> values) {
            addCriterion("hostname not in", values, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameBetween(String value1, String value2) {
            addCriterion("hostname between", value1, value2, "hostname");
            return (Criteria) this;
        }

        public Criteria andHostnameNotBetween(String value1, String value2) {
            addCriterion("hostname not between", value1, value2, "hostname");
            return (Criteria) this;
        }

        public Criteria andClientDescIsNull() {
            addCriterion("client_desc is null");
            return (Criteria) this;
        }

        public Criteria andClientDescIsNotNull() {
            addCriterion("client_desc is not null");
            return (Criteria) this;
        }

        public Criteria andClientDescEqualTo(String value) {
            addCriterion("client_desc =", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescNotEqualTo(String value) {
            addCriterion("client_desc <>", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescGreaterThan(String value) {
            addCriterion("client_desc >", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescGreaterThanOrEqualTo(String value) {
            addCriterion("client_desc >=", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescLessThan(String value) {
            addCriterion("client_desc <", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescLessThanOrEqualTo(String value) {
            addCriterion("client_desc <=", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescLike(String value) {
            addCriterion("client_desc like", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescNotLike(String value) {
            addCriterion("client_desc not like", value, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescIn(List<String> values) {
            addCriterion("client_desc in", values, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescNotIn(List<String> values) {
            addCriterion("client_desc not in", values, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescBetween(String value1, String value2) {
            addCriterion("client_desc between", value1, value2, "clientDesc");
            return (Criteria) this;
        }

        public Criteria andClientDescNotBetween(String value1, String value2) {
            addCriterion("client_desc not between", value1, value2, "clientDesc");
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

        public Criteria andEnableIsNull() {
            addCriterion("enable is null");
            return (Criteria) this;
        }

        public Criteria andEnableIsNotNull() {
            addCriterion("enable is not null");
            return (Criteria) this;
        }

        public Criteria andEnableEqualTo(Boolean value) {
            addCriterion("enable =", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotEqualTo(Boolean value) {
            addCriterion("enable <>", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThan(Boolean value) {
            addCriterion("enable >", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableGreaterThanOrEqualTo(Boolean value) {
            addCriterion("enable >=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThan(Boolean value) {
            addCriterion("enable <", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableLessThanOrEqualTo(Boolean value) {
            addCriterion("enable <=", value, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableIn(List<Boolean> values) {
            addCriterion("enable in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotIn(List<Boolean> values) {
            addCriterion("enable not in", values, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableBetween(Boolean value1, Boolean value2) {
            addCriterion("enable between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andEnableNotBetween(Boolean value1, Boolean value2) {
            addCriterion("enable not between", value1, value2, "enable");
            return (Criteria) this;
        }

        public Criteria andNodeIpIsNull() {
            addCriterion("node_ip is null");
            return (Criteria) this;
        }

        public Criteria andNodeIpIsNotNull() {
            addCriterion("node_ip is not null");
            return (Criteria) this;
        }

        public Criteria andNodeIpEqualTo(String value) {
            addCriterion("node_ip =", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpNotEqualTo(String value) {
            addCriterion("node_ip <>", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpGreaterThan(String value) {
            addCriterion("node_ip >", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpGreaterThanOrEqualTo(String value) {
            addCriterion("node_ip >=", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpLessThan(String value) {
            addCriterion("node_ip <", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpLessThanOrEqualTo(String value) {
            addCriterion("node_ip <=", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpLike(String value) {
            addCriterion("node_ip like", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpNotLike(String value) {
            addCriterion("node_ip not like", value, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpIn(List<String> values) {
            addCriterion("node_ip in", values, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpNotIn(List<String> values) {
            addCriterion("node_ip not in", values, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpBetween(String value1, String value2) {
            addCriterion("node_ip between", value1, value2, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andNodeIpNotBetween(String value1, String value2) {
            addCriterion("node_ip not between", value1, value2, "nodeIp");
            return (Criteria) this;
        }

        public Criteria andTenantIsNull() {
            addCriterion("tenant is null");
            return (Criteria) this;
        }

        public Criteria andTenantIsNotNull() {
            addCriterion("tenant is not null");
            return (Criteria) this;
        }

        public Criteria andTenantEqualTo(String value) {
            addCriterion("tenant =", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotEqualTo(String value) {
            addCriterion("tenant <>", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantGreaterThan(String value) {
            addCriterion("tenant >", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantGreaterThanOrEqualTo(String value) {
            addCriterion("tenant >=", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantLessThan(String value) {
            addCriterion("tenant <", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantLessThanOrEqualTo(String value) {
            addCriterion("tenant <=", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantLike(String value) {
            addCriterion("tenant like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotLike(String value) {
            addCriterion("tenant not like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantIn(List<String> values) {
            addCriterion("tenant in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotIn(List<String> values) {
            addCriterion("tenant not in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantBetween(String value1, String value2) {
            addCriterion("tenant between", value1, value2, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotBetween(String value1, String value2) {
            addCriterion("tenant not between", value1, value2, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantCnIsNull() {
            addCriterion("tenant_cn is null");
            return (Criteria) this;
        }

        public Criteria andTenantCnIsNotNull() {
            addCriterion("tenant_cn is not null");
            return (Criteria) this;
        }

        public Criteria andTenantCnEqualTo(String value) {
            addCriterion("tenant_cn =", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnNotEqualTo(String value) {
            addCriterion("tenant_cn <>", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnGreaterThan(String value) {
            addCriterion("tenant_cn >", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnGreaterThanOrEqualTo(String value) {
            addCriterion("tenant_cn >=", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnLessThan(String value) {
            addCriterion("tenant_cn <", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnLessThanOrEqualTo(String value) {
            addCriterion("tenant_cn <=", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnLike(String value) {
            addCriterion("tenant_cn like", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnNotLike(String value) {
            addCriterion("tenant_cn not like", value, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnIn(List<String> values) {
            addCriterion("tenant_cn in", values, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnNotIn(List<String> values) {
            addCriterion("tenant_cn not in", values, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnBetween(String value1, String value2) {
            addCriterion("tenant_cn between", value1, value2, "tenantCn");
            return (Criteria) this;
        }

        public Criteria andTenantCnNotBetween(String value1, String value2) {
            addCriterion("tenant_cn not between", value1, value2, "tenantCn");
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