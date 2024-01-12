package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class SceneApiInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SceneApiInfoExample() {
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

        public Criteria andApiOrderIsNull() {
            addCriterion("api_order is null");
            return (Criteria) this;
        }

        public Criteria andApiOrderIsNotNull() {
            addCriterion("api_order is not null");
            return (Criteria) this;
        }

        public Criteria andApiOrderEqualTo(Integer value) {
            addCriterion("api_order =", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderNotEqualTo(Integer value) {
            addCriterion("api_order <>", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderGreaterThan(Integer value) {
            addCriterion("api_order >", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderGreaterThanOrEqualTo(Integer value) {
            addCriterion("api_order >=", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderLessThan(Integer value) {
            addCriterion("api_order <", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderLessThanOrEqualTo(Integer value) {
            addCriterion("api_order <=", value, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderIn(List<Integer> values) {
            addCriterion("api_order in", values, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderNotIn(List<Integer> values) {
            addCriterion("api_order not in", values, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderBetween(Integer value1, Integer value2) {
            addCriterion("api_order between", value1, value2, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiOrderNotBetween(Integer value1, Integer value2) {
            addCriterion("api_order not between", value1, value2, "apiOrder");
            return (Criteria) this;
        }

        public Criteria andApiNameIsNull() {
            addCriterion("api_name is null");
            return (Criteria) this;
        }

        public Criteria andApiNameIsNotNull() {
            addCriterion("api_name is not null");
            return (Criteria) this;
        }

        public Criteria andApiNameEqualTo(String value) {
            addCriterion("api_name =", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameNotEqualTo(String value) {
            addCriterion("api_name <>", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameGreaterThan(String value) {
            addCriterion("api_name >", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameGreaterThanOrEqualTo(String value) {
            addCriterion("api_name >=", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameLessThan(String value) {
            addCriterion("api_name <", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameLessThanOrEqualTo(String value) {
            addCriterion("api_name <=", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameLike(String value) {
            addCriterion("api_name like", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameNotLike(String value) {
            addCriterion("api_name not like", value, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameIn(List<String> values) {
            addCriterion("api_name in", values, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameNotIn(List<String> values) {
            addCriterion("api_name not in", values, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameBetween(String value1, String value2) {
            addCriterion("api_name between", value1, value2, "apiName");
            return (Criteria) this;
        }

        public Criteria andApiNameNotBetween(String value1, String value2) {
            addCriterion("api_name not between", value1, value2, "apiName");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNull() {
            addCriterion("source_type is null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNotNull() {
            addCriterion("source_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeEqualTo(Integer value) {
            addCriterion("source_type =", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotEqualTo(Integer value) {
            addCriterion("source_type <>", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThan(Integer value) {
            addCriterion("source_type >", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("source_type >=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThan(Integer value) {
            addCriterion("source_type <", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThanOrEqualTo(Integer value) {
            addCriterion("source_type <=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIn(List<Integer> values) {
            addCriterion("source_type in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotIn(List<Integer> values) {
            addCriterion("source_type not in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeBetween(Integer value1, Integer value2) {
            addCriterion("source_type between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("source_type not between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andApiTypeIsNull() {
            addCriterion("api_type is null");
            return (Criteria) this;
        }

        public Criteria andApiTypeIsNotNull() {
            addCriterion("api_type is not null");
            return (Criteria) this;
        }

        public Criteria andApiTypeEqualTo(Integer value) {
            addCriterion("api_type =", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeNotEqualTo(Integer value) {
            addCriterion("api_type <>", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeGreaterThan(Integer value) {
            addCriterion("api_type >", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("api_type >=", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeLessThan(Integer value) {
            addCriterion("api_type <", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeLessThanOrEqualTo(Integer value) {
            addCriterion("api_type <=", value, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeIn(List<Integer> values) {
            addCriterion("api_type in", values, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeNotIn(List<Integer> values) {
            addCriterion("api_type not in", values, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeBetween(Integer value1, Integer value2) {
            addCriterion("api_type between", value1, value2, "apiType");
            return (Criteria) this;
        }

        public Criteria andApiTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("api_type not between", value1, value2, "apiType");
            return (Criteria) this;
        }

        public Criteria andRequestMethodIsNull() {
            addCriterion("request_method is null");
            return (Criteria) this;
        }

        public Criteria andRequestMethodIsNotNull() {
            addCriterion("request_method is not null");
            return (Criteria) this;
        }

        public Criteria andRequestMethodEqualTo(Integer value) {
            addCriterion("request_method =", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodNotEqualTo(Integer value) {
            addCriterion("request_method <>", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodGreaterThan(Integer value) {
            addCriterion("request_method >", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodGreaterThanOrEqualTo(Integer value) {
            addCriterion("request_method >=", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodLessThan(Integer value) {
            addCriterion("request_method <", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodLessThanOrEqualTo(Integer value) {
            addCriterion("request_method <=", value, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodIn(List<Integer> values) {
            addCriterion("request_method in", values, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodNotIn(List<Integer> values) {
            addCriterion("request_method not in", values, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodBetween(Integer value1, Integer value2) {
            addCriterion("request_method between", value1, value2, "requestMethod");
            return (Criteria) this;
        }

        public Criteria andRequestMethodNotBetween(Integer value1, Integer value2) {
            addCriterion("request_method not between", value1, value2, "requestMethod");
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

        public Criteria andNeedLoginIsNull() {
            addCriterion("need_login is null");
            return (Criteria) this;
        }

        public Criteria andNeedLoginIsNotNull() {
            addCriterion("need_login is not null");
            return (Criteria) this;
        }

        public Criteria andNeedLoginEqualTo(Boolean value) {
            addCriterion("need_login =", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginNotEqualTo(Boolean value) {
            addCriterion("need_login <>", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginGreaterThan(Boolean value) {
            addCriterion("need_login >", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginGreaterThanOrEqualTo(Boolean value) {
            addCriterion("need_login >=", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginLessThan(Boolean value) {
            addCriterion("need_login <", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginLessThanOrEqualTo(Boolean value) {
            addCriterion("need_login <=", value, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginIn(List<Boolean> values) {
            addCriterion("need_login in", values, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginNotIn(List<Boolean> values) {
            addCriterion("need_login not in", values, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginBetween(Boolean value1, Boolean value2) {
            addCriterion("need_login between", value1, value2, "needLogin");
            return (Criteria) this;
        }

        public Criteria andNeedLoginNotBetween(Boolean value1, Boolean value2) {
            addCriterion("need_login not between", value1, value2, "needLogin");
            return (Criteria) this;
        }

        public Criteria andTokenTypeIsNull() {
            addCriterion("token_type is null");
            return (Criteria) this;
        }

        public Criteria andTokenTypeIsNotNull() {
            addCriterion("token_type is not null");
            return (Criteria) this;
        }

        public Criteria andTokenTypeEqualTo(Integer value) {
            addCriterion("token_type =", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeNotEqualTo(Integer value) {
            addCriterion("token_type <>", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeGreaterThan(Integer value) {
            addCriterion("token_type >", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("token_type >=", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeLessThan(Integer value) {
            addCriterion("token_type <", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeLessThanOrEqualTo(Integer value) {
            addCriterion("token_type <=", value, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeIn(List<Integer> values) {
            addCriterion("token_type in", values, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeNotIn(List<Integer> values) {
            addCriterion("token_type not in", values, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeBetween(Integer value1, Integer value2) {
            addCriterion("token_type between", value1, value2, "tokenType");
            return (Criteria) this;
        }

        public Criteria andTokenTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("token_type not between", value1, value2, "tokenType");
            return (Criteria) this;
        }

        public Criteria andContentTypeIsNull() {
            addCriterion("content_type is null");
            return (Criteria) this;
        }

        public Criteria andContentTypeIsNotNull() {
            addCriterion("content_type is not null");
            return (Criteria) this;
        }

        public Criteria andContentTypeEqualTo(String value) {
            addCriterion("content_type =", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeNotEqualTo(String value) {
            addCriterion("content_type <>", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeGreaterThan(String value) {
            addCriterion("content_type >", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeGreaterThanOrEqualTo(String value) {
            addCriterion("content_type >=", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeLessThan(String value) {
            addCriterion("content_type <", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeLessThanOrEqualTo(String value) {
            addCriterion("content_type <=", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeLike(String value) {
            addCriterion("content_type like", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeNotLike(String value) {
            addCriterion("content_type not like", value, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeIn(List<String> values) {
            addCriterion("content_type in", values, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeNotIn(List<String> values) {
            addCriterion("content_type not in", values, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeBetween(String value1, String value2) {
            addCriterion("content_type between", value1, value2, "contentType");
            return (Criteria) this;
        }

        public Criteria andContentTypeNotBetween(String value1, String value2) {
            addCriterion("content_type not between", value1, value2, "contentType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeIsNull() {
            addCriterion("nacos_type is null");
            return (Criteria) this;
        }

        public Criteria andNacosTypeIsNotNull() {
            addCriterion("nacos_type is not null");
            return (Criteria) this;
        }

        public Criteria andNacosTypeEqualTo(Integer value) {
            addCriterion("nacos_type =", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeNotEqualTo(Integer value) {
            addCriterion("nacos_type <>", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeGreaterThan(Integer value) {
            addCriterion("nacos_type >", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("nacos_type >=", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeLessThan(Integer value) {
            addCriterion("nacos_type <", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeLessThanOrEqualTo(Integer value) {
            addCriterion("nacos_type <=", value, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeIn(List<Integer> values) {
            addCriterion("nacos_type in", values, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeNotIn(List<Integer> values) {
            addCriterion("nacos_type not in", values, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeBetween(Integer value1, Integer value2) {
            addCriterion("nacos_type between", value1, value2, "nacosType");
            return (Criteria) this;
        }

        public Criteria andNacosTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("nacos_type not between", value1, value2, "nacosType");
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

        public Criteria andParamTypeListIsNull() {
            addCriterion("param_type_list is null");
            return (Criteria) this;
        }

        public Criteria andParamTypeListIsNotNull() {
            addCriterion("param_type_list is not null");
            return (Criteria) this;
        }

        public Criteria andParamTypeListEqualTo(String value) {
            addCriterion("param_type_list =", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListNotEqualTo(String value) {
            addCriterion("param_type_list <>", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListGreaterThan(String value) {
            addCriterion("param_type_list >", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListGreaterThanOrEqualTo(String value) {
            addCriterion("param_type_list >=", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListLessThan(String value) {
            addCriterion("param_type_list <", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListLessThanOrEqualTo(String value) {
            addCriterion("param_type_list <=", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListLike(String value) {
            addCriterion("param_type_list like", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListNotLike(String value) {
            addCriterion("param_type_list not like", value, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListIn(List<String> values) {
            addCriterion("param_type_list in", values, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListNotIn(List<String> values) {
            addCriterion("param_type_list not in", values, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListBetween(String value1, String value2) {
            addCriterion("param_type_list between", value1, value2, "paramTypeList");
            return (Criteria) this;
        }

        public Criteria andParamTypeListNotBetween(String value1, String value2) {
            addCriterion("param_type_list not between", value1, value2, "paramTypeList");
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