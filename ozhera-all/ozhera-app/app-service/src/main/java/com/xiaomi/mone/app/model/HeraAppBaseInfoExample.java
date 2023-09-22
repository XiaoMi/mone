package com.xiaomi.mone.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeraAppBaseInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset = 0;

    public HeraAppBaseInfoExample() {
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

        public Criteria andBindIdIsNull() {
            addCriterion("bind_id is null");
            return (Criteria) this;
        }

        public Criteria andBindIdIsNotNull() {
            addCriterion("bind_id is not null");
            return (Criteria) this;
        }

        public Criteria andBindIdEqualTo(String value) {
            addCriterion("bind_id =", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdNotEqualTo(String value) {
            addCriterion("bind_id <>", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdGreaterThan(String value) {
            addCriterion("bind_id >", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdGreaterThanOrEqualTo(String value) {
            addCriterion("bind_id >=", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdLessThan(String value) {
            addCriterion("bind_id <", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdLessThanOrEqualTo(String value) {
            addCriterion("bind_id <=", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdLike(String value) {
            addCriterion("bind_id like", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdNotLike(String value) {
            addCriterion("bind_id not like", value, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdIn(List<String> values) {
            addCriterion("bind_id in", values, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdNotIn(List<String> values) {
            addCriterion("bind_id not in", values, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdBetween(String value1, String value2) {
            addCriterion("bind_id between", value1, value2, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindIdNotBetween(String value1, String value2) {
            addCriterion("bind_id not between", value1, value2, "bindId");
            return (Criteria) this;
        }

        public Criteria andBindTypeIsNull() {
            addCriterion("bind_type is null");
            return (Criteria) this;
        }

        public Criteria andBindTypeIsNotNull() {
            addCriterion("bind_type is not null");
            return (Criteria) this;
        }

        public Criteria andBindTypeEqualTo(Integer value) {
            addCriterion("bind_type =", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeNotEqualTo(Integer value) {
            addCriterion("bind_type <>", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeGreaterThan(Integer value) {
            addCriterion("bind_type >", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("bind_type >=", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeLessThan(Integer value) {
            addCriterion("bind_type <", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeLessThanOrEqualTo(Integer value) {
            addCriterion("bind_type <=", value, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeIn(List<Integer> values) {
            addCriterion("bind_type in", values, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeNotIn(List<Integer> values) {
            addCriterion("bind_type not in", values, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeBetween(Integer value1, Integer value2) {
            addCriterion("bind_type between", value1, value2, "bindType");
            return (Criteria) this;
        }

        public Criteria andBindTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("bind_type not between", value1, value2, "bindType");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNull() {
            addCriterion("app_name is null");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNotNull() {
            addCriterion("app_name is not null");
            return (Criteria) this;
        }

        public Criteria andAppNameEqualTo(String value) {
            addCriterion("app_name =", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotEqualTo(String value) {
            addCriterion("app_name <>", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThan(String value) {
            addCriterion("app_name >", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("app_name >=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThan(String value) {
            addCriterion("app_name <", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThanOrEqualTo(String value) {
            addCriterion("app_name <=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLike(String value) {
            addCriterion("app_name like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotLike(String value) {
            addCriterion("app_name not like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameIn(List<String> values) {
            addCriterion("app_name in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotIn(List<String> values) {
            addCriterion("app_name not in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameBetween(String value1, String value2) {
            addCriterion("app_name between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotBetween(String value1, String value2) {
            addCriterion("app_name not between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppCnameIsNull() {
            addCriterion("app_cname is null");
            return (Criteria) this;
        }

        public Criteria andAppCnameIsNotNull() {
            addCriterion("app_cname is not null");
            return (Criteria) this;
        }

        public Criteria andAppCnameEqualTo(String value) {
            addCriterion("app_cname =", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameNotEqualTo(String value) {
            addCriterion("app_cname <>", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameGreaterThan(String value) {
            addCriterion("app_cname >", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameGreaterThanOrEqualTo(String value) {
            addCriterion("app_cname >=", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameLessThan(String value) {
            addCriterion("app_cname <", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameLessThanOrEqualTo(String value) {
            addCriterion("app_cname <=", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameLike(String value) {
            addCriterion("app_cname like", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameNotLike(String value) {
            addCriterion("app_cname not like", value, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameIn(List<String> values) {
            addCriterion("app_cname in", values, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameNotIn(List<String> values) {
            addCriterion("app_cname not in", values, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameBetween(String value1, String value2) {
            addCriterion("app_cname between", value1, value2, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppCnameNotBetween(String value1, String value2) {
            addCriterion("app_cname not between", value1, value2, "appCname");
            return (Criteria) this;
        }

        public Criteria andAppTypeIsNull() {
            addCriterion("app_type is null");
            return (Criteria) this;
        }

        public Criteria andAppTypeIsNotNull() {
            addCriterion("app_type is not null");
            return (Criteria) this;
        }

        public Criteria andAppTypeEqualTo(Integer value) {
            addCriterion("app_type =", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeNotEqualTo(Integer value) {
            addCriterion("app_type <>", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeGreaterThan(Integer value) {
            addCriterion("app_type >", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("app_type >=", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeLessThan(Integer value) {
            addCriterion("app_type <", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeLessThanOrEqualTo(Integer value) {
            addCriterion("app_type <=", value, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeIn(List<Integer> values) {
            addCriterion("app_type in", values, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeNotIn(List<Integer> values) {
            addCriterion("app_type not in", values, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeBetween(Integer value1, Integer value2) {
            addCriterion("app_type between", value1, value2, "appType");
            return (Criteria) this;
        }

        public Criteria andAppTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("app_type not between", value1, value2, "appType");
            return (Criteria) this;
        }

        public Criteria andAppLanguageIsNull() {
            addCriterion("app_language is null");
            return (Criteria) this;
        }

        public Criteria andAppLanguageIsNotNull() {
            addCriterion("app_language is not null");
            return (Criteria) this;
        }

        public Criteria andAppLanguageEqualTo(String value) {
            addCriterion("app_language =", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageNotEqualTo(String value) {
            addCriterion("app_language <>", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageGreaterThan(String value) {
            addCriterion("app_language >", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageGreaterThanOrEqualTo(String value) {
            addCriterion("app_language >=", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageLessThan(String value) {
            addCriterion("app_language <", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageLessThanOrEqualTo(String value) {
            addCriterion("app_language <=", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageLike(String value) {
            addCriterion("app_language like", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageNotLike(String value) {
            addCriterion("app_language not like", value, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageIn(List<String> values) {
            addCriterion("app_language in", values, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageNotIn(List<String> values) {
            addCriterion("app_language not in", values, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageBetween(String value1, String value2) {
            addCriterion("app_language between", value1, value2, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andAppLanguageNotBetween(String value1, String value2) {
            addCriterion("app_language not between", value1, value2, "appLanguage");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeIsNull() {
            addCriterion("platform_type is null");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeIsNotNull() {
            addCriterion("platform_type is not null");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeEqualTo(Integer value) {
            addCriterion("platform_type =", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeNotEqualTo(Integer value) {
            addCriterion("platform_type <>", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeGreaterThan(Integer value) {
            addCriterion("platform_type >", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("platform_type >=", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeLessThan(Integer value) {
            addCriterion("platform_type <", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeLessThanOrEqualTo(Integer value) {
            addCriterion("platform_type <=", value, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeIn(List<Integer> values) {
            addCriterion("platform_type in", values, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeNotIn(List<Integer> values) {
            addCriterion("platform_type not in", values, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeBetween(Integer value1, Integer value2) {
            addCriterion("platform_type between", value1, value2, "platformType");
            return (Criteria) this;
        }

        public Criteria andPlatformTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("platform_type not between", value1, value2, "platformType");
            return (Criteria) this;
        }

        public Criteria andAppSignIdIsNull() {
            addCriterion("app_sign_id is null");
            return (Criteria) this;
        }

        public Criteria andAppSignIdIsNotNull() {
            addCriterion("app_sign_id is not null");
            return (Criteria) this;
        }

        public Criteria andAppSignIdEqualTo(String value) {
            addCriterion("app_sign_id =", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdNotEqualTo(String value) {
            addCriterion("app_sign_id <>", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdGreaterThan(String value) {
            addCriterion("app_sign_id >", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdGreaterThanOrEqualTo(String value) {
            addCriterion("app_sign_id >=", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdLessThan(String value) {
            addCriterion("app_sign_id <", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdLessThanOrEqualTo(String value) {
            addCriterion("app_sign_id <=", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdLike(String value) {
            addCriterion("app_sign_id like", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdNotLike(String value) {
            addCriterion("app_sign_id not like", value, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdIn(List<String> values) {
            addCriterion("app_sign_id in", values, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdNotIn(List<String> values) {
            addCriterion("app_sign_id not in", values, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdBetween(String value1, String value2) {
            addCriterion("app_sign_id between", value1, value2, "appSignId");
            return (Criteria) this;
        }

        public Criteria andAppSignIdNotBetween(String value1, String value2) {
            addCriterion("app_sign_id not between", value1, value2, "appSignId");
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