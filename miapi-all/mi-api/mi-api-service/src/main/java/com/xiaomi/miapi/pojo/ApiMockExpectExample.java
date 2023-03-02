package com.xiaomi.miapi.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiMockExpectExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ApiMockExpectExample() {
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

        public Criteria andMockExpNameIsNull() {
            addCriterion("mock_exp_name is null");
            return (Criteria) this;
        }

        public Criteria andMockExpNameIsNotNull() {
            addCriterion("mock_exp_name is not null");
            return (Criteria) this;
        }

        public Criteria andMockExpNameEqualTo(String value) {
            addCriterion("mock_exp_name =", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameNotEqualTo(String value) {
            addCriterion("mock_exp_name <>", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameGreaterThan(String value) {
            addCriterion("mock_exp_name >", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameGreaterThanOrEqualTo(String value) {
            addCriterion("mock_exp_name >=", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameLessThan(String value) {
            addCriterion("mock_exp_name <", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameLessThanOrEqualTo(String value) {
            addCriterion("mock_exp_name <=", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameLike(String value) {
            addCriterion("mock_exp_name like", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameNotLike(String value) {
            addCriterion("mock_exp_name not like", value, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameIn(List<String> values) {
            addCriterion("mock_exp_name in", values, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameNotIn(List<String> values) {
            addCriterion("mock_exp_name not in", values, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameBetween(String value1, String value2) {
            addCriterion("mock_exp_name between", value1, value2, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockExpNameNotBetween(String value1, String value2) {
            addCriterion("mock_exp_name not between", value1, value2, "mockExpName");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeIsNull() {
            addCriterion("mock_data_type is null");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeIsNotNull() {
            addCriterion("mock_data_type is not null");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeEqualTo(Integer value) {
            addCriterion("mock_data_type =", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeNotEqualTo(Integer value) {
            addCriterion("mock_data_type <>", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeGreaterThan(Integer value) {
            addCriterion("mock_data_type >", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("mock_data_type >=", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeLessThan(Integer value) {
            addCriterion("mock_data_type <", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeLessThanOrEqualTo(Integer value) {
            addCriterion("mock_data_type <=", value, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeIn(List<Integer> values) {
            addCriterion("mock_data_type in", values, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeNotIn(List<Integer> values) {
            addCriterion("mock_data_type not in", values, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeBetween(Integer value1, Integer value2) {
            addCriterion("mock_data_type between", value1, value2, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andMockDataTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("mock_data_type not between", value1, value2, "mockDataType");
            return (Criteria) this;
        }

        public Criteria andParamsMd5IsNull() {
            addCriterion("params_md5 is null");
            return (Criteria) this;
        }

        public Criteria andParamsMd5IsNotNull() {
            addCriterion("params_md5 is not null");
            return (Criteria) this;
        }

        public Criteria andParamsMd5EqualTo(String value) {
            addCriterion("params_md5 =", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5NotEqualTo(String value) {
            addCriterion("params_md5 <>", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5GreaterThan(String value) {
            addCriterion("params_md5 >", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5GreaterThanOrEqualTo(String value) {
            addCriterion("params_md5 >=", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5LessThan(String value) {
            addCriterion("params_md5 <", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5LessThanOrEqualTo(String value) {
            addCriterion("params_md5 <=", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5Like(String value) {
            addCriterion("params_md5 like", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5NotLike(String value) {
            addCriterion("params_md5 not like", value, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5In(List<String> values) {
            addCriterion("params_md5 in", values, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5NotIn(List<String> values) {
            addCriterion("params_md5 not in", values, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5Between(String value1, String value2) {
            addCriterion("params_md5 between", value1, value2, "paramsMd5");
            return (Criteria) this;
        }

        public Criteria andParamsMd5NotBetween(String value1, String value2) {
            addCriterion("params_md5 not between", value1, value2, "paramsMd5");
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

        public Criteria andIsDefaultIsNull() {
            addCriterion("is_default is null");
            return (Criteria) this;
        }

        public Criteria andIsDefaultIsNotNull() {
            addCriterion("is_default is not null");
            return (Criteria) this;
        }

        public Criteria andIsDefaultEqualTo(Boolean value) {
            addCriterion("is_default =", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultNotEqualTo(Boolean value) {
            addCriterion("is_default <>", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultGreaterThan(Boolean value) {
            addCriterion("is_default >", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_default >=", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultLessThan(Boolean value) {
            addCriterion("is_default <", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultLessThanOrEqualTo(Boolean value) {
            addCriterion("is_default <=", value, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultIn(List<Boolean> values) {
            addCriterion("is_default in", values, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultNotIn(List<Boolean> values) {
            addCriterion("is_default not in", values, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultBetween(Boolean value1, Boolean value2) {
            addCriterion("is_default between", value1, value2, "isDefault");
            return (Criteria) this;
        }

        public Criteria andIsDefaultNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_default not between", value1, value2, "isDefault");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNull() {
            addCriterion("update_user is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("update_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(String value) {
            addCriterion("update_user =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(String value) {
            addCriterion("update_user <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(String value) {
            addCriterion("update_user >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(String value) {
            addCriterion("update_user >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(String value) {
            addCriterion("update_user <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(String value) {
            addCriterion("update_user <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLike(String value) {
            addCriterion("update_user like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotLike(String value) {
            addCriterion("update_user not like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<String> values) {
            addCriterion("update_user in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<String> values) {
            addCriterion("update_user not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(String value1, String value2) {
            addCriterion("update_user between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(String value1, String value2) {
            addCriterion("update_user not between", value1, value2, "updateUser");
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

        public Criteria andMockRequestParamTypeIsNull() {
            addCriterion("mock_request_param_type is null");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeIsNotNull() {
            addCriterion("mock_request_param_type is not null");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeEqualTo(Integer value) {
            addCriterion("mock_request_param_type =", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeNotEqualTo(Integer value) {
            addCriterion("mock_request_param_type <>", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeGreaterThan(Integer value) {
            addCriterion("mock_request_param_type >", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("mock_request_param_type >=", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeLessThan(Integer value) {
            addCriterion("mock_request_param_type <", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeLessThanOrEqualTo(Integer value) {
            addCriterion("mock_request_param_type <=", value, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeIn(List<Integer> values) {
            addCriterion("mock_request_param_type in", values, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeNotIn(List<Integer> values) {
            addCriterion("mock_request_param_type not in", values, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeBetween(Integer value1, Integer value2) {
            addCriterion("mock_request_param_type between", value1, value2, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andMockRequestParamTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("mock_request_param_type not between", value1, value2, "mockRequestParamType");
            return (Criteria) this;
        }

        public Criteria andProxyUrlIsNull() {
            addCriterion("proxy_url is null");
            return (Criteria) this;
        }

        public Criteria andProxyUrlIsNotNull() {
            addCriterion("proxy_url is not null");
            return (Criteria) this;
        }

        public Criteria andProxyUrlEqualTo(String value) {
            addCriterion("proxy_url =", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlNotEqualTo(String value) {
            addCriterion("proxy_url <>", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlGreaterThan(String value) {
            addCriterion("proxy_url >", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlGreaterThanOrEqualTo(String value) {
            addCriterion("proxy_url >=", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlLessThan(String value) {
            addCriterion("proxy_url <", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlLessThanOrEqualTo(String value) {
            addCriterion("proxy_url <=", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlLike(String value) {
            addCriterion("proxy_url like", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlNotLike(String value) {
            addCriterion("proxy_url not like", value, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlIn(List<String> values) {
            addCriterion("proxy_url in", values, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlNotIn(List<String> values) {
            addCriterion("proxy_url not in", values, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlBetween(String value1, String value2) {
            addCriterion("proxy_url between", value1, value2, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andProxyUrlNotBetween(String value1, String value2) {
            addCriterion("proxy_url not between", value1, value2, "proxyUrl");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptIsNull() {
            addCriterion("use_mock_script is null");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptIsNotNull() {
            addCriterion("use_mock_script is not null");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptEqualTo(Boolean value) {
            addCriterion("use_mock_script =", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptNotEqualTo(Boolean value) {
            addCriterion("use_mock_script <>", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptGreaterThan(Boolean value) {
            addCriterion("use_mock_script >", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptGreaterThanOrEqualTo(Boolean value) {
            addCriterion("use_mock_script >=", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptLessThan(Boolean value) {
            addCriterion("use_mock_script <", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptLessThanOrEqualTo(Boolean value) {
            addCriterion("use_mock_script <=", value, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptIn(List<Boolean> values) {
            addCriterion("use_mock_script in", values, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptNotIn(List<Boolean> values) {
            addCriterion("use_mock_script not in", values, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptBetween(Boolean value1, Boolean value2) {
            addCriterion("use_mock_script between", value1, value2, "useMockScript");
            return (Criteria) this;
        }

        public Criteria andUseMockScriptNotBetween(Boolean value1, Boolean value2) {
            addCriterion("use_mock_script not between", value1, value2, "useMockScript");
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