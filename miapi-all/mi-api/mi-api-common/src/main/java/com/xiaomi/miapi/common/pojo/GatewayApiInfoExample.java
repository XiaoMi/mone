package com.xiaomi.miapi.common.pojo;

import java.util.ArrayList;
import java.util.List;

public class GatewayApiInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public GatewayApiInfoExample() {
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
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

        public Criteria andPathIsNull() {
            addCriterion("path is null");
            return (Criteria) this;
        }

        public Criteria andPathIsNotNull() {
            addCriterion("path is not null");
            return (Criteria) this;
        }

        public Criteria andPathEqualTo(String value) {
            addCriterion("path =", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathNotEqualTo(String value) {
            addCriterion("path <>", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathGreaterThan(String value) {
            addCriterion("path >", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathGreaterThanOrEqualTo(String value) {
            addCriterion("path >=", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathLessThan(String value) {
            addCriterion("path <", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathLessThanOrEqualTo(String value) {
            addCriterion("path <=", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathLike(String value) {
            addCriterion("path like", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathNotLike(String value) {
            addCriterion("path not like", value, "path");
            return (Criteria) this;
        }

        public Criteria andPathIn(List<String> values) {
            addCriterion("path in", values, "path");
            return (Criteria) this;
        }

        public Criteria andPathNotIn(List<String> values) {
            addCriterion("path not in", values, "path");
            return (Criteria) this;
        }

        public Criteria andPathBetween(String value1, String value2) {
            addCriterion("path between", value1, value2, "path");
            return (Criteria) this;
        }

        public Criteria andPathNotBetween(String value1, String value2) {
            addCriterion("path not between", value1, value2, "path");
            return (Criteria) this;
        }

        public Criteria andRouteTypeIsNull() {
            addCriterion("route_type is null");
            return (Criteria) this;
        }

        public Criteria andRouteTypeIsNotNull() {
            addCriterion("route_type is not null");
            return (Criteria) this;
        }

        public Criteria andRouteTypeEqualTo(Integer value) {
            addCriterion("route_type =", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeNotEqualTo(Integer value) {
            addCriterion("route_type <>", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeGreaterThan(Integer value) {
            addCriterion("route_type >", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("route_type >=", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeLessThan(Integer value) {
            addCriterion("route_type <", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeLessThanOrEqualTo(Integer value) {
            addCriterion("route_type <=", value, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeIn(List<Integer> values) {
            addCriterion("route_type in", values, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeNotIn(List<Integer> values) {
            addCriterion("route_type not in", values, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeBetween(Integer value1, Integer value2) {
            addCriterion("route_type between", value1, value2, "routeType");
            return (Criteria) this;
        }

        public Criteria andRouteTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("route_type not between", value1, value2, "routeType");
            return (Criteria) this;
        }

        public Criteria andApplicationIsNull() {
            addCriterion("application is null");
            return (Criteria) this;
        }

        public Criteria andApplicationIsNotNull() {
            addCriterion("application is not null");
            return (Criteria) this;
        }

        public Criteria andApplicationEqualTo(String value) {
            addCriterion("application =", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationNotEqualTo(String value) {
            addCriterion("application <>", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationGreaterThan(String value) {
            addCriterion("application >", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationGreaterThanOrEqualTo(String value) {
            addCriterion("application >=", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationLessThan(String value) {
            addCriterion("application <", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationLessThanOrEqualTo(String value) {
            addCriterion("application <=", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationLike(String value) {
            addCriterion("application like", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationNotLike(String value) {
            addCriterion("application not like", value, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationIn(List<String> values) {
            addCriterion("application in", values, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationNotIn(List<String> values) {
            addCriterion("application not in", values, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationBetween(String value1, String value2) {
            addCriterion("application between", value1, value2, "application");
            return (Criteria) this;
        }

        public Criteria andApplicationNotBetween(String value1, String value2) {
            addCriterion("application not between", value1, value2, "application");
            return (Criteria) this;
        }

        public Criteria andServiceNameIsNull() {
            addCriterion("service_name is null");
            return (Criteria) this;
        }

        public Criteria andServiceNameIsNotNull() {
            addCriterion("service_name is not null");
            return (Criteria) this;
        }

        public Criteria andServiceNameEqualTo(String value) {
            addCriterion("service_name =", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameNotEqualTo(String value) {
            addCriterion("service_name <>", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameGreaterThan(String value) {
            addCriterion("service_name >", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameGreaterThanOrEqualTo(String value) {
            addCriterion("service_name >=", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameLessThan(String value) {
            addCriterion("service_name <", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameLessThanOrEqualTo(String value) {
            addCriterion("service_name <=", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameLike(String value) {
            addCriterion("service_name like", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameNotLike(String value) {
            addCriterion("service_name not like", value, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameIn(List<String> values) {
            addCriterion("service_name in", values, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameNotIn(List<String> values) {
            addCriterion("service_name not in", values, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameBetween(String value1, String value2) {
            addCriterion("service_name between", value1, value2, "serviceName");
            return (Criteria) this;
        }

        public Criteria andServiceNameNotBetween(String value1, String value2) {
            addCriterion("service_name not between", value1, value2, "serviceName");
            return (Criteria) this;
        }

        public Criteria andMethodNameIsNull() {
            addCriterion("method_name is null");
            return (Criteria) this;
        }

        public Criteria andMethodNameIsNotNull() {
            addCriterion("method_name is not null");
            return (Criteria) this;
        }

        public Criteria andMethodNameEqualTo(String value) {
            addCriterion("method_name =", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameNotEqualTo(String value) {
            addCriterion("method_name <>", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameGreaterThan(String value) {
            addCriterion("method_name >", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameGreaterThanOrEqualTo(String value) {
            addCriterion("method_name >=", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameLessThan(String value) {
            addCriterion("method_name <", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameLessThanOrEqualTo(String value) {
            addCriterion("method_name <=", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameLike(String value) {
            addCriterion("method_name like", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameNotLike(String value) {
            addCriterion("method_name not like", value, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameIn(List<String> values) {
            addCriterion("method_name in", values, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameNotIn(List<String> values) {
            addCriterion("method_name not in", values, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameBetween(String value1, String value2) {
            addCriterion("method_name between", value1, value2, "methodName");
            return (Criteria) this;
        }

        public Criteria andMethodNameNotBetween(String value1, String value2) {
            addCriterion("method_name not between", value1, value2, "methodName");
            return (Criteria) this;
        }

        public Criteria andServiceGroupIsNull() {
            addCriterion("service_group is null");
            return (Criteria) this;
        }

        public Criteria andServiceGroupIsNotNull() {
            addCriterion("service_group is not null");
            return (Criteria) this;
        }

        public Criteria andServiceGroupEqualTo(String value) {
            addCriterion("service_group =", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupNotEqualTo(String value) {
            addCriterion("service_group <>", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupGreaterThan(String value) {
            addCriterion("service_group >", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupGreaterThanOrEqualTo(String value) {
            addCriterion("service_group >=", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupLessThan(String value) {
            addCriterion("service_group <", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupLessThanOrEqualTo(String value) {
            addCriterion("service_group <=", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupLike(String value) {
            addCriterion("service_group like", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupNotLike(String value) {
            addCriterion("service_group not like", value, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupIn(List<String> values) {
            addCriterion("service_group in", values, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupNotIn(List<String> values) {
            addCriterion("service_group not in", values, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupBetween(String value1, String value2) {
            addCriterion("service_group between", value1, value2, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceGroupNotBetween(String value1, String value2) {
            addCriterion("service_group not between", value1, value2, "serviceGroup");
            return (Criteria) this;
        }

        public Criteria andServiceVersionIsNull() {
            addCriterion("service_version is null");
            return (Criteria) this;
        }

        public Criteria andServiceVersionIsNotNull() {
            addCriterion("service_version is not null");
            return (Criteria) this;
        }

        public Criteria andServiceVersionEqualTo(String value) {
            addCriterion("service_version =", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionNotEqualTo(String value) {
            addCriterion("service_version <>", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionGreaterThan(String value) {
            addCriterion("service_version >", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionGreaterThanOrEqualTo(String value) {
            addCriterion("service_version >=", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionLessThan(String value) {
            addCriterion("service_version <", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionLessThanOrEqualTo(String value) {
            addCriterion("service_version <=", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionLike(String value) {
            addCriterion("service_version like", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionNotLike(String value) {
            addCriterion("service_version not like", value, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionIn(List<String> values) {
            addCriterion("service_version in", values, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionNotIn(List<String> values) {
            addCriterion("service_version not in", values, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionBetween(String value1, String value2) {
            addCriterion("service_version between", value1, value2, "serviceVersion");
            return (Criteria) this;
        }

        public Criteria andServiceVersionNotBetween(String value1, String value2) {
            addCriterion("service_version not between", value1, value2, "serviceVersion");
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

        public Criteria andInvokeLimitIsNull() {
            addCriterion("invoke_limit is null");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitIsNotNull() {
            addCriterion("invoke_limit is not null");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitEqualTo(Integer value) {
            addCriterion("invoke_limit =", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitNotEqualTo(Integer value) {
            addCriterion("invoke_limit <>", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitGreaterThan(Integer value) {
            addCriterion("invoke_limit >", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("invoke_limit >=", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitLessThan(Integer value) {
            addCriterion("invoke_limit <", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitLessThanOrEqualTo(Integer value) {
            addCriterion("invoke_limit <=", value, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitIn(List<Integer> values) {
            addCriterion("invoke_limit in", values, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitNotIn(List<Integer> values) {
            addCriterion("invoke_limit not in", values, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitBetween(Integer value1, Integer value2) {
            addCriterion("invoke_limit between", value1, value2, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andInvokeLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("invoke_limit not between", value1, value2, "invokeLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitIsNull() {
            addCriterion("qps_limit is null");
            return (Criteria) this;
        }

        public Criteria andQpsLimitIsNotNull() {
            addCriterion("qps_limit is not null");
            return (Criteria) this;
        }

        public Criteria andQpsLimitEqualTo(Integer value) {
            addCriterion("qps_limit =", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitNotEqualTo(Integer value) {
            addCriterion("qps_limit <>", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitGreaterThan(Integer value) {
            addCriterion("qps_limit >", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("qps_limit >=", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitLessThan(Integer value) {
            addCriterion("qps_limit <", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitLessThanOrEqualTo(Integer value) {
            addCriterion("qps_limit <=", value, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitIn(List<Integer> values) {
            addCriterion("qps_limit in", values, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitNotIn(List<Integer> values) {
            addCriterion("qps_limit not in", values, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitBetween(Integer value1, Integer value2) {
            addCriterion("qps_limit between", value1, value2, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andQpsLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("qps_limit not between", value1, value2, "qpsLimit");
            return (Criteria) this;
        }

        public Criteria andTimeoutIsNull() {
            addCriterion("timeout is null");
            return (Criteria) this;
        }

        public Criteria andTimeoutIsNotNull() {
            addCriterion("timeout is not null");
            return (Criteria) this;
        }

        public Criteria andTimeoutEqualTo(Integer value) {
            addCriterion("timeout =", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotEqualTo(Integer value) {
            addCriterion("timeout <>", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutGreaterThan(Integer value) {
            addCriterion("timeout >", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutGreaterThanOrEqualTo(Integer value) {
            addCriterion("timeout >=", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutLessThan(Integer value) {
            addCriterion("timeout <", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutLessThanOrEqualTo(Integer value) {
            addCriterion("timeout <=", value, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutIn(List<Integer> values) {
            addCriterion("timeout in", values, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotIn(List<Integer> values) {
            addCriterion("timeout not in", values, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutBetween(Integer value1, Integer value2) {
            addCriterion("timeout between", value1, value2, "timeout");
            return (Criteria) this;
        }

        public Criteria andTimeoutNotBetween(Integer value1, Integer value2) {
            addCriterion("timeout not between", value1, value2, "timeout");
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

        public Criteria andAllowMockIsNull() {
            addCriterion("allow_mock is null");
            return (Criteria) this;
        }

        public Criteria andAllowMockIsNotNull() {
            addCriterion("allow_mock is not null");
            return (Criteria) this;
        }

        public Criteria andAllowMockEqualTo(Boolean value) {
            addCriterion("allow_mock =", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockNotEqualTo(Boolean value) {
            addCriterion("allow_mock <>", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockGreaterThan(Boolean value) {
            addCriterion("allow_mock >", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockGreaterThanOrEqualTo(Boolean value) {
            addCriterion("allow_mock >=", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockLessThan(Boolean value) {
            addCriterion("allow_mock <", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockLessThanOrEqualTo(Boolean value) {
            addCriterion("allow_mock <=", value, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockIn(List<Boolean> values) {
            addCriterion("allow_mock in", values, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockNotIn(List<Boolean> values) {
            addCriterion("allow_mock not in", values, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_mock between", value1, value2, "allowMock");
            return (Criteria) this;
        }

        public Criteria andAllowMockNotBetween(Boolean value1, Boolean value2) {
            addCriterion("allow_mock not between", value1, value2, "allowMock");
            return (Criteria) this;
        }

        public Criteria andMockDataIsNull() {
            addCriterion("mock_data is null");
            return (Criteria) this;
        }

        public Criteria andMockDataIsNotNull() {
            addCriterion("mock_data is not null");
            return (Criteria) this;
        }

        public Criteria andMockDataEqualTo(String value) {
            addCriterion("mock_data =", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataNotEqualTo(String value) {
            addCriterion("mock_data <>", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataGreaterThan(String value) {
            addCriterion("mock_data >", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataGreaterThanOrEqualTo(String value) {
            addCriterion("mock_data >=", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataLessThan(String value) {
            addCriterion("mock_data <", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataLessThanOrEqualTo(String value) {
            addCriterion("mock_data <=", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataLike(String value) {
            addCriterion("mock_data like", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataNotLike(String value) {
            addCriterion("mock_data not like", value, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataIn(List<String> values) {
            addCriterion("mock_data in", values, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataNotIn(List<String> values) {
            addCriterion("mock_data not in", values, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataBetween(String value1, String value2) {
            addCriterion("mock_data between", value1, value2, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataNotBetween(String value1, String value2) {
            addCriterion("mock_data not between", value1, value2, "mockData");
            return (Criteria) this;
        }

        public Criteria andMockDataDescIsNull() {
            addCriterion("mock_data_desc is null");
            return (Criteria) this;
        }

        public Criteria andMockDataDescIsNotNull() {
            addCriterion("mock_data_desc is not null");
            return (Criteria) this;
        }

        public Criteria andMockDataDescEqualTo(String value) {
            addCriterion("mock_data_desc =", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescNotEqualTo(String value) {
            addCriterion("mock_data_desc <>", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescGreaterThan(String value) {
            addCriterion("mock_data_desc >", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescGreaterThanOrEqualTo(String value) {
            addCriterion("mock_data_desc >=", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescLessThan(String value) {
            addCriterion("mock_data_desc <", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescLessThanOrEqualTo(String value) {
            addCriterion("mock_data_desc <=", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescLike(String value) {
            addCriterion("mock_data_desc like", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescNotLike(String value) {
            addCriterion("mock_data_desc not like", value, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescIn(List<String> values) {
            addCriterion("mock_data_desc in", values, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescNotIn(List<String> values) {
            addCriterion("mock_data_desc not in", values, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescBetween(String value1, String value2) {
            addCriterion("mock_data_desc between", value1, value2, "mockDataDesc");
            return (Criteria) this;
        }

        public Criteria andMockDataDescNotBetween(String value1, String value2) {
            addCriterion("mock_data_desc not between", value1, value2, "mockDataDesc");
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