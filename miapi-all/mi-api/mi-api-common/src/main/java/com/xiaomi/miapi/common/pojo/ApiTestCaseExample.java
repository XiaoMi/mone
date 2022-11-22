package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class ApiTestCaseExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ApiTestCaseExample() {
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

        public Criteria andAccountIdIsNull() {
            addCriterion("account_id is null");
            return (Criteria) this;
        }

        public Criteria andAccountIdIsNotNull() {
            addCriterion("account_id is not null");
            return (Criteria) this;
        }

        public Criteria andAccountIdEqualTo(Integer value) {
            addCriterion("account_id =", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdNotEqualTo(Integer value) {
            addCriterion("account_id <>", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdGreaterThan(Integer value) {
            addCriterion("account_id >", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("account_id >=", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdLessThan(Integer value) {
            addCriterion("account_id <", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdLessThanOrEqualTo(Integer value) {
            addCriterion("account_id <=", value, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdIn(List<Integer> values) {
            addCriterion("account_id in", values, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdNotIn(List<Integer> values) {
            addCriterion("account_id not in", values, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdBetween(Integer value1, Integer value2) {
            addCriterion("account_id between", value1, value2, "accountId");
            return (Criteria) this;
        }

        public Criteria andAccountIdNotBetween(Integer value1, Integer value2) {
            addCriterion("account_id not between", value1, value2, "accountId");
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

        public Criteria andApiIdEqualTo(Integer value) {
            addCriterion("api_id =", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotEqualTo(Integer value) {
            addCriterion("api_id <>", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdGreaterThan(Integer value) {
            addCriterion("api_id >", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("api_id >=", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdLessThan(Integer value) {
            addCriterion("api_id <", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdLessThanOrEqualTo(Integer value) {
            addCriterion("api_id <=", value, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdIn(List<Integer> values) {
            addCriterion("api_id in", values, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotIn(List<Integer> values) {
            addCriterion("api_id not in", values, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdBetween(Integer value1, Integer value2) {
            addCriterion("api_id between", value1, value2, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiIdNotBetween(Integer value1, Integer value2) {
            addCriterion("api_id not between", value1, value2, "apiId");
            return (Criteria) this;
        }

        public Criteria andApiProtocalIsNull() {
            addCriterion("api_protocal is null");
            return (Criteria) this;
        }

        public Criteria andApiProtocalIsNotNull() {
            addCriterion("api_protocal is not null");
            return (Criteria) this;
        }

        public Criteria andApiProtocalEqualTo(Integer value) {
            addCriterion("api_protocal =", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalNotEqualTo(Integer value) {
            addCriterion("api_protocal <>", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalGreaterThan(Integer value) {
            addCriterion("api_protocal >", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalGreaterThanOrEqualTo(Integer value) {
            addCriterion("api_protocal >=", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalLessThan(Integer value) {
            addCriterion("api_protocal <", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalLessThanOrEqualTo(Integer value) {
            addCriterion("api_protocal <=", value, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalIn(List<Integer> values) {
            addCriterion("api_protocal in", values, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalNotIn(List<Integer> values) {
            addCriterion("api_protocal not in", values, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalBetween(Integer value1, Integer value2) {
            addCriterion("api_protocal between", value1, value2, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andApiProtocalNotBetween(Integer value1, Integer value2) {
            addCriterion("api_protocal not between", value1, value2, "apiProtocal");
            return (Criteria) this;
        }

        public Criteria andHttpMethodIsNull() {
            addCriterion("http_method is null");
            return (Criteria) this;
        }

        public Criteria andHttpMethodIsNotNull() {
            addCriterion("http_method is not null");
            return (Criteria) this;
        }

        public Criteria andHttpMethodEqualTo(String value) {
            addCriterion("http_method =", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodNotEqualTo(String value) {
            addCriterion("http_method <>", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodGreaterThan(String value) {
            addCriterion("http_method >", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodGreaterThanOrEqualTo(String value) {
            addCriterion("http_method >=", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodLessThan(String value) {
            addCriterion("http_method <", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodLessThanOrEqualTo(String value) {
            addCriterion("http_method <=", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodLike(String value) {
            addCriterion("http_method like", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodNotLike(String value) {
            addCriterion("http_method not like", value, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodIn(List<String> values) {
            addCriterion("http_method in", values, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodNotIn(List<String> values) {
            addCriterion("http_method not in", values, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodBetween(String value1, String value2) {
            addCriterion("http_method between", value1, value2, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andHttpMethodNotBetween(String value1, String value2) {
            addCriterion("http_method not between", value1, value2, "httpMethod");
            return (Criteria) this;
        }

        public Criteria andUrlIsNull() {
            addCriterion("url is null");
            return (Criteria) this;
        }

        public Criteria andUrlIsNotNull() {
            addCriterion("url is not null");
            return (Criteria) this;
        }

        public Criteria andUrlEqualTo(String value) {
            addCriterion("url =", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlNotEqualTo(String value) {
            addCriterion("url <>", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlGreaterThan(String value) {
            addCriterion("url >", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlGreaterThanOrEqualTo(String value) {
            addCriterion("url >=", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlLessThan(String value) {
            addCriterion("url <", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlLessThanOrEqualTo(String value) {
            addCriterion("url <=", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlLike(String value) {
            addCriterion("url like", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlNotLike(String value) {
            addCriterion("url not like", value, "url");
            return (Criteria) this;
        }

        public Criteria andUrlIn(List<String> values) {
            addCriterion("url in", values, "url");
            return (Criteria) this;
        }

        public Criteria andUrlNotIn(List<String> values) {
            addCriterion("url not in", values, "url");
            return (Criteria) this;
        }

        public Criteria andUrlBetween(String value1, String value2) {
            addCriterion("url between", value1, value2, "url");
            return (Criteria) this;
        }

        public Criteria andUrlNotBetween(String value1, String value2) {
            addCriterion("url not between", value1, value2, "url");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutIsNull() {
            addCriterion("request_timeout is null");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutIsNotNull() {
            addCriterion("request_timeout is not null");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutEqualTo(Integer value) {
            addCriterion("request_timeout =", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutNotEqualTo(Integer value) {
            addCriterion("request_timeout <>", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutGreaterThan(Integer value) {
            addCriterion("request_timeout >", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutGreaterThanOrEqualTo(Integer value) {
            addCriterion("request_timeout >=", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutLessThan(Integer value) {
            addCriterion("request_timeout <", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutLessThanOrEqualTo(Integer value) {
            addCriterion("request_timeout <=", value, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutIn(List<Integer> values) {
            addCriterion("request_timeout in", values, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutNotIn(List<Integer> values) {
            addCriterion("request_timeout not in", values, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutBetween(Integer value1, Integer value2) {
            addCriterion("request_timeout between", value1, value2, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andRequestTimeoutNotBetween(Integer value1, Integer value2) {
            addCriterion("request_timeout not between", value1, value2, "requestTimeout");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersIsNull() {
            addCriterion("http_headers is null");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersIsNotNull() {
            addCriterion("http_headers is not null");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersEqualTo(String value) {
            addCriterion("http_headers =", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersNotEqualTo(String value) {
            addCriterion("http_headers <>", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersGreaterThan(String value) {
            addCriterion("http_headers >", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersGreaterThanOrEqualTo(String value) {
            addCriterion("http_headers >=", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersLessThan(String value) {
            addCriterion("http_headers <", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersLessThanOrEqualTo(String value) {
            addCriterion("http_headers <=", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersLike(String value) {
            addCriterion("http_headers like", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersNotLike(String value) {
            addCriterion("http_headers not like", value, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersIn(List<String> values) {
            addCriterion("http_headers in", values, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersNotIn(List<String> values) {
            addCriterion("http_headers not in", values, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersBetween(String value1, String value2) {
            addCriterion("http_headers between", value1, value2, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andHttpHeadersNotBetween(String value1, String value2) {
            addCriterion("http_headers not between", value1, value2, "httpHeaders");
            return (Criteria) this;
        }

        public Criteria andCaseNameIsNull() {
            addCriterion("case_name is null");
            return (Criteria) this;
        }

        public Criteria andCaseNameIsNotNull() {
            addCriterion("case_name is not null");
            return (Criteria) this;
        }

        public Criteria andCaseNameEqualTo(String value) {
            addCriterion("case_name =", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameNotEqualTo(String value) {
            addCriterion("case_name <>", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameGreaterThan(String value) {
            addCriterion("case_name >", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameGreaterThanOrEqualTo(String value) {
            addCriterion("case_name >=", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameLessThan(String value) {
            addCriterion("case_name <", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameLessThanOrEqualTo(String value) {
            addCriterion("case_name <=", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameLike(String value) {
            addCriterion("case_name like", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameNotLike(String value) {
            addCriterion("case_name not like", value, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameIn(List<String> values) {
            addCriterion("case_name in", values, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameNotIn(List<String> values) {
            addCriterion("case_name not in", values, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameBetween(String value1, String value2) {
            addCriterion("case_name between", value1, value2, "caseName");
            return (Criteria) this;
        }

        public Criteria andCaseNameNotBetween(String value1, String value2) {
            addCriterion("case_name not between", value1, value2, "caseName");
            return (Criteria) this;
        }

        public Criteria andHttpDomianIsNull() {
            addCriterion("http_domian is null");
            return (Criteria) this;
        }

        public Criteria andHttpDomianIsNotNull() {
            addCriterion("http_domian is not null");
            return (Criteria) this;
        }

        public Criteria andHttpDomianEqualTo(String value) {
            addCriterion("http_domian =", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianNotEqualTo(String value) {
            addCriterion("http_domian <>", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianGreaterThan(String value) {
            addCriterion("http_domian >", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianGreaterThanOrEqualTo(String value) {
            addCriterion("http_domian >=", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianLessThan(String value) {
            addCriterion("http_domian <", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianLessThanOrEqualTo(String value) {
            addCriterion("http_domian <=", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianLike(String value) {
            addCriterion("http_domian like", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianNotLike(String value) {
            addCriterion("http_domian not like", value, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianIn(List<String> values) {
            addCriterion("http_domian in", values, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianNotIn(List<String> values) {
            addCriterion("http_domian not in", values, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianBetween(String value1, String value2) {
            addCriterion("http_domian between", value1, value2, "httpDomian");
            return (Criteria) this;
        }

        public Criteria andHttpDomianNotBetween(String value1, String value2) {
            addCriterion("http_domian not between", value1, value2, "httpDomian");
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

        public Criteria andHttpReqBodyTypeIsNull() {
            addCriterion("http_req_body_type is null");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeIsNotNull() {
            addCriterion("http_req_body_type is not null");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeEqualTo(Integer value) {
            addCriterion("http_req_body_type =", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeNotEqualTo(Integer value) {
            addCriterion("http_req_body_type <>", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeGreaterThan(Integer value) {
            addCriterion("http_req_body_type >", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("http_req_body_type >=", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeLessThan(Integer value) {
            addCriterion("http_req_body_type <", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeLessThanOrEqualTo(Integer value) {
            addCriterion("http_req_body_type <=", value, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeIn(List<Integer> values) {
            addCriterion("http_req_body_type in", values, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeNotIn(List<Integer> values) {
            addCriterion("http_req_body_type not in", values, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeBetween(Integer value1, Integer value2) {
            addCriterion("http_req_body_type between", value1, value2, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andHttpReqBodyTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("http_req_body_type not between", value1, value2, "httpReqBodyType");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceIsNull() {
            addCriterion("dubbo_interface is null");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceIsNotNull() {
            addCriterion("dubbo_interface is not null");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceEqualTo(String value) {
            addCriterion("dubbo_interface =", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceNotEqualTo(String value) {
            addCriterion("dubbo_interface <>", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceGreaterThan(String value) {
            addCriterion("dubbo_interface >", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_interface >=", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceLessThan(String value) {
            addCriterion("dubbo_interface <", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceLessThanOrEqualTo(String value) {
            addCriterion("dubbo_interface <=", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceLike(String value) {
            addCriterion("dubbo_interface like", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceNotLike(String value) {
            addCriterion("dubbo_interface not like", value, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceIn(List<String> values) {
            addCriterion("dubbo_interface in", values, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceNotIn(List<String> values) {
            addCriterion("dubbo_interface not in", values, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceBetween(String value1, String value2) {
            addCriterion("dubbo_interface between", value1, value2, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboInterfaceNotBetween(String value1, String value2) {
            addCriterion("dubbo_interface not between", value1, value2, "dubboInterface");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameIsNull() {
            addCriterion("dubbo_method_name is null");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameIsNotNull() {
            addCriterion("dubbo_method_name is not null");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameEqualTo(String value) {
            addCriterion("dubbo_method_name =", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameNotEqualTo(String value) {
            addCriterion("dubbo_method_name <>", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameGreaterThan(String value) {
            addCriterion("dubbo_method_name >", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_method_name >=", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameLessThan(String value) {
            addCriterion("dubbo_method_name <", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameLessThanOrEqualTo(String value) {
            addCriterion("dubbo_method_name <=", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameLike(String value) {
            addCriterion("dubbo_method_name like", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameNotLike(String value) {
            addCriterion("dubbo_method_name not like", value, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameIn(List<String> values) {
            addCriterion("dubbo_method_name in", values, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameNotIn(List<String> values) {
            addCriterion("dubbo_method_name not in", values, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameBetween(String value1, String value2) {
            addCriterion("dubbo_method_name between", value1, value2, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboMethodNameNotBetween(String value1, String value2) {
            addCriterion("dubbo_method_name not between", value1, value2, "dubboMethodName");
            return (Criteria) this;
        }

        public Criteria andDubboGroupIsNull() {
            addCriterion("dubbo_group is null");
            return (Criteria) this;
        }

        public Criteria andDubboGroupIsNotNull() {
            addCriterion("dubbo_group is not null");
            return (Criteria) this;
        }

        public Criteria andDubboGroupEqualTo(String value) {
            addCriterion("dubbo_group =", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupNotEqualTo(String value) {
            addCriterion("dubbo_group <>", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupGreaterThan(String value) {
            addCriterion("dubbo_group >", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_group >=", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupLessThan(String value) {
            addCriterion("dubbo_group <", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupLessThanOrEqualTo(String value) {
            addCriterion("dubbo_group <=", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupLike(String value) {
            addCriterion("dubbo_group like", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupNotLike(String value) {
            addCriterion("dubbo_group not like", value, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupIn(List<String> values) {
            addCriterion("dubbo_group in", values, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupNotIn(List<String> values) {
            addCriterion("dubbo_group not in", values, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupBetween(String value1, String value2) {
            addCriterion("dubbo_group between", value1, value2, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboGroupNotBetween(String value1, String value2) {
            addCriterion("dubbo_group not between", value1, value2, "dubboGroup");
            return (Criteria) this;
        }

        public Criteria andDubboVersionIsNull() {
            addCriterion("dubbo_version is null");
            return (Criteria) this;
        }

        public Criteria andDubboVersionIsNotNull() {
            addCriterion("dubbo_version is not null");
            return (Criteria) this;
        }

        public Criteria andDubboVersionEqualTo(String value) {
            addCriterion("dubbo_version =", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionNotEqualTo(String value) {
            addCriterion("dubbo_version <>", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionGreaterThan(String value) {
            addCriterion("dubbo_version >", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_version >=", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionLessThan(String value) {
            addCriterion("dubbo_version <", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionLessThanOrEqualTo(String value) {
            addCriterion("dubbo_version <=", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionLike(String value) {
            addCriterion("dubbo_version like", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionNotLike(String value) {
            addCriterion("dubbo_version not like", value, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionIn(List<String> values) {
            addCriterion("dubbo_version in", values, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionNotIn(List<String> values) {
            addCriterion("dubbo_version not in", values, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionBetween(String value1, String value2) {
            addCriterion("dubbo_version between", value1, value2, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboVersionNotBetween(String value1, String value2) {
            addCriterion("dubbo_version not between", value1, value2, "dubboVersion");
            return (Criteria) this;
        }

        public Criteria andDubboAddrIsNull() {
            addCriterion("dubbo_addr is null");
            return (Criteria) this;
        }

        public Criteria andDubboAddrIsNotNull() {
            addCriterion("dubbo_addr is not null");
            return (Criteria) this;
        }

        public Criteria andDubboAddrEqualTo(String value) {
            addCriterion("dubbo_addr =", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrNotEqualTo(String value) {
            addCriterion("dubbo_addr <>", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrGreaterThan(String value) {
            addCriterion("dubbo_addr >", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_addr >=", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrLessThan(String value) {
            addCriterion("dubbo_addr <", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrLessThanOrEqualTo(String value) {
            addCriterion("dubbo_addr <=", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrLike(String value) {
            addCriterion("dubbo_addr like", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrNotLike(String value) {
            addCriterion("dubbo_addr not like", value, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrIn(List<String> values) {
            addCriterion("dubbo_addr in", values, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrNotIn(List<String> values) {
            addCriterion("dubbo_addr not in", values, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrBetween(String value1, String value2) {
            addCriterion("dubbo_addr between", value1, value2, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboAddrNotBetween(String value1, String value2) {
            addCriterion("dubbo_addr not between", value1, value2, "dubboAddr");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeIsNull() {
            addCriterion("dubbo_param_type is null");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeIsNotNull() {
            addCriterion("dubbo_param_type is not null");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeEqualTo(String value) {
            addCriterion("dubbo_param_type =", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeNotEqualTo(String value) {
            addCriterion("dubbo_param_type <>", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeGreaterThan(String value) {
            addCriterion("dubbo_param_type >", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_param_type >=", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeLessThan(String value) {
            addCriterion("dubbo_param_type <", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeLessThanOrEqualTo(String value) {
            addCriterion("dubbo_param_type <=", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeLike(String value) {
            addCriterion("dubbo_param_type like", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeNotLike(String value) {
            addCriterion("dubbo_param_type not like", value, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeIn(List<String> values) {
            addCriterion("dubbo_param_type in", values, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeNotIn(List<String> values) {
            addCriterion("dubbo_param_type not in", values, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeBetween(String value1, String value2) {
            addCriterion("dubbo_param_type between", value1, value2, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboParamTypeNotBetween(String value1, String value2) {
            addCriterion("dubbo_param_type not between", value1, value2, "dubboParamType");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericIsNull() {
            addCriterion("dubbo_is_generic is null");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericIsNotNull() {
            addCriterion("dubbo_is_generic is not null");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericEqualTo(Boolean value) {
            addCriterion("dubbo_is_generic =", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericNotEqualTo(Boolean value) {
            addCriterion("dubbo_is_generic <>", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericGreaterThan(Boolean value) {
            addCriterion("dubbo_is_generic >", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericGreaterThanOrEqualTo(Boolean value) {
            addCriterion("dubbo_is_generic >=", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericLessThan(Boolean value) {
            addCriterion("dubbo_is_generic <", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericLessThanOrEqualTo(Boolean value) {
            addCriterion("dubbo_is_generic <=", value, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericIn(List<Boolean> values) {
            addCriterion("dubbo_is_generic in", values, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericNotIn(List<Boolean> values) {
            addCriterion("dubbo_is_generic not in", values, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericBetween(Boolean value1, Boolean value2) {
            addCriterion("dubbo_is_generic between", value1, value2, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboIsGenericNotBetween(Boolean value1, Boolean value2) {
            addCriterion("dubbo_is_generic not between", value1, value2, "dubboIsGeneric");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeIsNull() {
            addCriterion("dubbo_retry_time is null");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeIsNotNull() {
            addCriterion("dubbo_retry_time is not null");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeEqualTo(Integer value) {
            addCriterion("dubbo_retry_time =", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeNotEqualTo(Integer value) {
            addCriterion("dubbo_retry_time <>", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeGreaterThan(Integer value) {
            addCriterion("dubbo_retry_time >", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("dubbo_retry_time >=", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeLessThan(Integer value) {
            addCriterion("dubbo_retry_time <", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeLessThanOrEqualTo(Integer value) {
            addCriterion("dubbo_retry_time <=", value, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeIn(List<Integer> values) {
            addCriterion("dubbo_retry_time in", values, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeNotIn(List<Integer> values) {
            addCriterion("dubbo_retry_time not in", values, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeBetween(Integer value1, Integer value2) {
            addCriterion("dubbo_retry_time between", value1, value2, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboRetryTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("dubbo_retry_time not between", value1, value2, "dubboRetryTime");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentIsNull() {
            addCriterion("dubbo_use_attachment is null");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentIsNotNull() {
            addCriterion("dubbo_use_attachment is not null");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentEqualTo(Boolean value) {
            addCriterion("dubbo_use_attachment =", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentNotEqualTo(Boolean value) {
            addCriterion("dubbo_use_attachment <>", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentGreaterThan(Boolean value) {
            addCriterion("dubbo_use_attachment >", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentGreaterThanOrEqualTo(Boolean value) {
            addCriterion("dubbo_use_attachment >=", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentLessThan(Boolean value) {
            addCriterion("dubbo_use_attachment <", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentLessThanOrEqualTo(Boolean value) {
            addCriterion("dubbo_use_attachment <=", value, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentIn(List<Boolean> values) {
            addCriterion("dubbo_use_attachment in", values, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentNotIn(List<Boolean> values) {
            addCriterion("dubbo_use_attachment not in", values, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentBetween(Boolean value1, Boolean value2) {
            addCriterion("dubbo_use_attachment between", value1, value2, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboUseAttachmentNotBetween(Boolean value1, Boolean value2) {
            addCriterion("dubbo_use_attachment not between", value1, value2, "dubboUseAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentIsNull() {
            addCriterion("dubbo_attachment is null");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentIsNotNull() {
            addCriterion("dubbo_attachment is not null");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentEqualTo(String value) {
            addCriterion("dubbo_attachment =", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentNotEqualTo(String value) {
            addCriterion("dubbo_attachment <>", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentGreaterThan(String value) {
            addCriterion("dubbo_attachment >", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_attachment >=", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentLessThan(String value) {
            addCriterion("dubbo_attachment <", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentLessThanOrEqualTo(String value) {
            addCriterion("dubbo_attachment <=", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentLike(String value) {
            addCriterion("dubbo_attachment like", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentNotLike(String value) {
            addCriterion("dubbo_attachment not like", value, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentIn(List<String> values) {
            addCriterion("dubbo_attachment in", values, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentNotIn(List<String> values) {
            addCriterion("dubbo_attachment not in", values, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentBetween(String value1, String value2) {
            addCriterion("dubbo_attachment between", value1, value2, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboAttachmentNotBetween(String value1, String value2) {
            addCriterion("dubbo_attachment not between", value1, value2, "dubboAttachment");
            return (Criteria) this;
        }

        public Criteria andDubboEnvIsNull() {
            addCriterion("dubbo_env is null");
            return (Criteria) this;
        }

        public Criteria andDubboEnvIsNotNull() {
            addCriterion("dubbo_env is not null");
            return (Criteria) this;
        }

        public Criteria andDubboEnvEqualTo(String value) {
            addCriterion("dubbo_env =", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvNotEqualTo(String value) {
            addCriterion("dubbo_env <>", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvGreaterThan(String value) {
            addCriterion("dubbo_env >", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvGreaterThanOrEqualTo(String value) {
            addCriterion("dubbo_env >=", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvLessThan(String value) {
            addCriterion("dubbo_env <", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvLessThanOrEqualTo(String value) {
            addCriterion("dubbo_env <=", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvLike(String value) {
            addCriterion("dubbo_env like", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvNotLike(String value) {
            addCriterion("dubbo_env not like", value, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvIn(List<String> values) {
            addCriterion("dubbo_env in", values, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvNotIn(List<String> values) {
            addCriterion("dubbo_env not in", values, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvBetween(String value1, String value2) {
            addCriterion("dubbo_env between", value1, value2, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andDubboEnvNotBetween(String value1, String value2) {
            addCriterion("dubbo_env not between", value1, value2, "dubboEnv");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterIsNull() {
            addCriterion("use_x5_filter is null");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterIsNotNull() {
            addCriterion("use_x5_filter is not null");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterEqualTo(Boolean value) {
            addCriterion("use_x5_filter =", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterNotEqualTo(Boolean value) {
            addCriterion("use_x5_filter <>", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterGreaterThan(Boolean value) {
            addCriterion("use_x5_filter >", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterGreaterThanOrEqualTo(Boolean value) {
            addCriterion("use_x5_filter >=", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterLessThan(Boolean value) {
            addCriterion("use_x5_filter <", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterLessThanOrEqualTo(Boolean value) {
            addCriterion("use_x5_filter <=", value, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterIn(List<Boolean> values) {
            addCriterion("use_x5_filter in", values, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterNotIn(List<Boolean> values) {
            addCriterion("use_x5_filter not in", values, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterBetween(Boolean value1, Boolean value2) {
            addCriterion("use_x5_filter between", value1, value2, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andUseX5FilterNotBetween(Boolean value1, Boolean value2) {
            addCriterion("use_x5_filter not between", value1, value2, "useX5Filter");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyIsNull() {
            addCriterion("x5_app_key is null");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyIsNotNull() {
            addCriterion("x5_app_key is not null");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyEqualTo(String value) {
            addCriterion("x5_app_key =", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyNotEqualTo(String value) {
            addCriterion("x5_app_key <>", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyGreaterThan(String value) {
            addCriterion("x5_app_key >", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyGreaterThanOrEqualTo(String value) {
            addCriterion("x5_app_key >=", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyLessThan(String value) {
            addCriterion("x5_app_key <", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyLessThanOrEqualTo(String value) {
            addCriterion("x5_app_key <=", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyLike(String value) {
            addCriterion("x5_app_key like", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyNotLike(String value) {
            addCriterion("x5_app_key not like", value, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyIn(List<String> values) {
            addCriterion("x5_app_key in", values, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyNotIn(List<String> values) {
            addCriterion("x5_app_key not in", values, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyBetween(String value1, String value2) {
            addCriterion("x5_app_key between", value1, value2, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppKeyNotBetween(String value1, String value2) {
            addCriterion("x5_app_key not between", value1, value2, "x5AppKey");
            return (Criteria) this;
        }

        public Criteria andX5AppIdIsNull() {
            addCriterion("x5_app_id is null");
            return (Criteria) this;
        }

        public Criteria andX5AppIdIsNotNull() {
            addCriterion("x5_app_id is not null");
            return (Criteria) this;
        }

        public Criteria andX5AppIdEqualTo(Integer value) {
            addCriterion("x5_app_id =", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdNotEqualTo(Integer value) {
            addCriterion("x5_app_id <>", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdGreaterThan(Integer value) {
            addCriterion("x5_app_id >", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("x5_app_id >=", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdLessThan(Integer value) {
            addCriterion("x5_app_id <", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdLessThanOrEqualTo(Integer value) {
            addCriterion("x5_app_id <=", value, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdIn(List<Integer> values) {
            addCriterion("x5_app_id in", values, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdNotIn(List<Integer> values) {
            addCriterion("x5_app_id not in", values, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdBetween(Integer value1, Integer value2) {
            addCriterion("x5_app_id between", value1, value2, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andX5AppIdNotBetween(Integer value1, Integer value2) {
            addCriterion("x5_app_id not between", value1, value2, "x5AppId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdIsNull() {
            addCriterion("case_group_id is null");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdIsNotNull() {
            addCriterion("case_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdEqualTo(Integer value) {
            addCriterion("case_group_id =", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdNotEqualTo(Integer value) {
            addCriterion("case_group_id <>", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdGreaterThan(Integer value) {
            addCriterion("case_group_id >", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("case_group_id >=", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdLessThan(Integer value) {
            addCriterion("case_group_id <", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdLessThanOrEqualTo(Integer value) {
            addCriterion("case_group_id <=", value, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdIn(List<Integer> values) {
            addCriterion("case_group_id in", values, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdNotIn(List<Integer> values) {
            addCriterion("case_group_id not in", values, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdBetween(Integer value1, Integer value2) {
            addCriterion("case_group_id between", value1, value2, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andCaseGroupIdNotBetween(Integer value1, Integer value2) {
            addCriterion("case_group_id not between", value1, value2, "caseGroupId");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameIsNull() {
            addCriterion("grpc_package_name is null");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameIsNotNull() {
            addCriterion("grpc_package_name is not null");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameEqualTo(String value) {
            addCriterion("grpc_package_name =", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameNotEqualTo(String value) {
            addCriterion("grpc_package_name <>", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameGreaterThan(String value) {
            addCriterion("grpc_package_name >", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameGreaterThanOrEqualTo(String value) {
            addCriterion("grpc_package_name >=", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameLessThan(String value) {
            addCriterion("grpc_package_name <", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameLessThanOrEqualTo(String value) {
            addCriterion("grpc_package_name <=", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameLike(String value) {
            addCriterion("grpc_package_name like", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameNotLike(String value) {
            addCriterion("grpc_package_name not like", value, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameIn(List<String> values) {
            addCriterion("grpc_package_name in", values, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameNotIn(List<String> values) {
            addCriterion("grpc_package_name not in", values, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameBetween(String value1, String value2) {
            addCriterion("grpc_package_name between", value1, value2, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcPackageNameNotBetween(String value1, String value2) {
            addCriterion("grpc_package_name not between", value1, value2, "grpcPackageName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameIsNull() {
            addCriterion("grpc_interface_name is null");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameIsNotNull() {
            addCriterion("grpc_interface_name is not null");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameEqualTo(String value) {
            addCriterion("grpc_interface_name =", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameNotEqualTo(String value) {
            addCriterion("grpc_interface_name <>", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameGreaterThan(String value) {
            addCriterion("grpc_interface_name >", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameGreaterThanOrEqualTo(String value) {
            addCriterion("grpc_interface_name >=", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameLessThan(String value) {
            addCriterion("grpc_interface_name <", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameLessThanOrEqualTo(String value) {
            addCriterion("grpc_interface_name <=", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameLike(String value) {
            addCriterion("grpc_interface_name like", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameNotLike(String value) {
            addCriterion("grpc_interface_name not like", value, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameIn(List<String> values) {
            addCriterion("grpc_interface_name in", values, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameNotIn(List<String> values) {
            addCriterion("grpc_interface_name not in", values, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameBetween(String value1, String value2) {
            addCriterion("grpc_interface_name between", value1, value2, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcInterfaceNameNotBetween(String value1, String value2) {
            addCriterion("grpc_interface_name not between", value1, value2, "grpcInterfaceName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameIsNull() {
            addCriterion("grpc_method_name is null");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameIsNotNull() {
            addCriterion("grpc_method_name is not null");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameEqualTo(String value) {
            addCriterion("grpc_method_name =", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameNotEqualTo(String value) {
            addCriterion("grpc_method_name <>", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameGreaterThan(String value) {
            addCriterion("grpc_method_name >", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameGreaterThanOrEqualTo(String value) {
            addCriterion("grpc_method_name >=", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameLessThan(String value) {
            addCriterion("grpc_method_name <", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameLessThanOrEqualTo(String value) {
            addCriterion("grpc_method_name <=", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameLike(String value) {
            addCriterion("grpc_method_name like", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameNotLike(String value) {
            addCriterion("grpc_method_name not like", value, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameIn(List<String> values) {
            addCriterion("grpc_method_name in", values, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameNotIn(List<String> values) {
            addCriterion("grpc_method_name not in", values, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameBetween(String value1, String value2) {
            addCriterion("grpc_method_name between", value1, value2, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcMethodNameNotBetween(String value1, String value2) {
            addCriterion("grpc_method_name not between", value1, value2, "grpcMethodName");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrIsNull() {
            addCriterion("grpc_server_addr is null");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrIsNotNull() {
            addCriterion("grpc_server_addr is not null");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrEqualTo(String value) {
            addCriterion("grpc_server_addr =", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrNotEqualTo(String value) {
            addCriterion("grpc_server_addr <>", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrGreaterThan(String value) {
            addCriterion("grpc_server_addr >", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrGreaterThanOrEqualTo(String value) {
            addCriterion("grpc_server_addr >=", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrLessThan(String value) {
            addCriterion("grpc_server_addr <", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrLessThanOrEqualTo(String value) {
            addCriterion("grpc_server_addr <=", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrLike(String value) {
            addCriterion("grpc_server_addr like", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrNotLike(String value) {
            addCriterion("grpc_server_addr not like", value, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrIn(List<String> values) {
            addCriterion("grpc_server_addr in", values, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrNotIn(List<String> values) {
            addCriterion("grpc_server_addr not in", values, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrBetween(String value1, String value2) {
            addCriterion("grpc_server_addr between", value1, value2, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcServerAddrNotBetween(String value1, String value2) {
            addCriterion("grpc_server_addr not between", value1, value2, "grpcServerAddr");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameIsNull() {
            addCriterion("grpc_app_name is null");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameIsNotNull() {
            addCriterion("grpc_app_name is not null");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameEqualTo(String value) {
            addCriterion("grpc_app_name =", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameNotEqualTo(String value) {
            addCriterion("grpc_app_name <>", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameGreaterThan(String value) {
            addCriterion("grpc_app_name >", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("grpc_app_name >=", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameLessThan(String value) {
            addCriterion("grpc_app_name <", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameLessThanOrEqualTo(String value) {
            addCriterion("grpc_app_name <=", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameLike(String value) {
            addCriterion("grpc_app_name like", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameNotLike(String value) {
            addCriterion("grpc_app_name not like", value, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameIn(List<String> values) {
            addCriterion("grpc_app_name in", values, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameNotIn(List<String> values) {
            addCriterion("grpc_app_name not in", values, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameBetween(String value1, String value2) {
            addCriterion("grpc_app_name between", value1, value2, "grpcAppName");
            return (Criteria) this;
        }

        public Criteria andGrpcAppNameNotBetween(String value1, String value2) {
            addCriterion("grpc_app_name not between", value1, value2, "grpcAppName");
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