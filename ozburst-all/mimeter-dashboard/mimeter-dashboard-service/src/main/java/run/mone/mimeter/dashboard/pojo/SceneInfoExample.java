package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.List;

public class SceneInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SceneInfoExample() {
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

        public Criteria andSceneStatusIsNull() {
            addCriterion("scene_status is null");
            return (Criteria) this;
        }

        public Criteria andSceneStatusIsNotNull() {
            addCriterion("scene_status is not null");
            return (Criteria) this;
        }

        public Criteria andSceneStatusEqualTo(Integer value) {
            addCriterion("scene_status =", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusNotEqualTo(Integer value) {
            addCriterion("scene_status <>", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusGreaterThan(Integer value) {
            addCriterion("scene_status >", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_status >=", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusLessThan(Integer value) {
            addCriterion("scene_status <", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusLessThanOrEqualTo(Integer value) {
            addCriterion("scene_status <=", value, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusIn(List<Integer> values) {
            addCriterion("scene_status in", values, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusNotIn(List<Integer> values) {
            addCriterion("scene_status not in", values, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusBetween(Integer value1, Integer value2) {
            addCriterion("scene_status between", value1, value2, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andSceneStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_status not between", value1, value2, "sceneStatus");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNull() {
            addCriterion("creator is null");
            return (Criteria) this;
        }

        public Criteria andCreatorIsNotNull() {
            addCriterion("creator is not null");
            return (Criteria) this;
        }

        public Criteria andCreatorEqualTo(String value) {
            addCriterion("creator =", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotEqualTo(String value) {
            addCriterion("creator <>", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThan(String value) {
            addCriterion("creator >", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorGreaterThanOrEqualTo(String value) {
            addCriterion("creator >=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThan(String value) {
            addCriterion("creator <", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLessThanOrEqualTo(String value) {
            addCriterion("creator <=", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorLike(String value) {
            addCriterion("creator like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotLike(String value) {
            addCriterion("creator not like", value, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorIn(List<String> values) {
            addCriterion("creator in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotIn(List<String> values) {
            addCriterion("creator not in", values, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorBetween(String value1, String value2) {
            addCriterion("creator between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andCreatorNotBetween(String value1, String value2) {
            addCriterion("creator not between", value1, value2, "creator");
            return (Criteria) this;
        }

        public Criteria andUpdatorIsNull() {
            addCriterion("updator is null");
            return (Criteria) this;
        }

        public Criteria andUpdatorIsNotNull() {
            addCriterion("updator is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatorEqualTo(String value) {
            addCriterion("updator =", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorNotEqualTo(String value) {
            addCriterion("updator <>", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorGreaterThan(String value) {
            addCriterion("updator >", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorGreaterThanOrEqualTo(String value) {
            addCriterion("updator >=", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorLessThan(String value) {
            addCriterion("updator <", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorLessThanOrEqualTo(String value) {
            addCriterion("updator <=", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorLike(String value) {
            addCriterion("updator like", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorNotLike(String value) {
            addCriterion("updator not like", value, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorIn(List<String> values) {
            addCriterion("updator in", values, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorNotIn(List<String> values) {
            addCriterion("updator not in", values, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorBetween(String value1, String value2) {
            addCriterion("updator between", value1, value2, "updator");
            return (Criteria) this;
        }

        public Criteria andUpdatorNotBetween(String value1, String value2) {
            addCriterion("updator not between", value1, value2, "updator");
            return (Criteria) this;
        }

        public Criteria andApinumIsNull() {
            addCriterion("apiNum is null");
            return (Criteria) this;
        }

        public Criteria andApinumIsNotNull() {
            addCriterion("apiNum is not null");
            return (Criteria) this;
        }

        public Criteria andApinumEqualTo(Integer value) {
            addCriterion("apiNum =", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumNotEqualTo(Integer value) {
            addCriterion("apiNum <>", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumGreaterThan(Integer value) {
            addCriterion("apiNum >", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumGreaterThanOrEqualTo(Integer value) {
            addCriterion("apiNum >=", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumLessThan(Integer value) {
            addCriterion("apiNum <", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumLessThanOrEqualTo(Integer value) {
            addCriterion("apiNum <=", value, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumIn(List<Integer> values) {
            addCriterion("apiNum in", values, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumNotIn(List<Integer> values) {
            addCriterion("apiNum not in", values, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumBetween(Integer value1, Integer value2) {
            addCriterion("apiNum between", value1, value2, "apinum");
            return (Criteria) this;
        }

        public Criteria andApinumNotBetween(Integer value1, Integer value2) {
            addCriterion("apiNum not between", value1, value2, "apinum");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIsNull() {
            addCriterion("scene_type is null");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIsNotNull() {
            addCriterion("scene_type is not null");
            return (Criteria) this;
        }

        public Criteria andSceneTypeEqualTo(Integer value) {
            addCriterion("scene_type =", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotEqualTo(Integer value) {
            addCriterion("scene_type <>", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeGreaterThan(Integer value) {
            addCriterion("scene_type >", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_type >=", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeLessThan(Integer value) {
            addCriterion("scene_type <", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeLessThanOrEqualTo(Integer value) {
            addCriterion("scene_type <=", value, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeIn(List<Integer> values) {
            addCriterion("scene_type in", values, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotIn(List<Integer> values) {
            addCriterion("scene_type not in", values, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeBetween(Integer value1, Integer value2) {
            addCriterion("scene_type between", value1, value2, "sceneType");
            return (Criteria) this;
        }

        public Criteria andSceneTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_type not between", value1, value2, "sceneType");
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

        public Criteria andIncrementModeIsNull() {
            addCriterion("Increment_mode is null");
            return (Criteria) this;
        }

        public Criteria andIncrementModeIsNotNull() {
            addCriterion("Increment_mode is not null");
            return (Criteria) this;
        }

        public Criteria andIncrementModeEqualTo(Integer value) {
            addCriterion("Increment_mode =", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeNotEqualTo(Integer value) {
            addCriterion("Increment_mode <>", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeGreaterThan(Integer value) {
            addCriterion("Increment_mode >", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeGreaterThanOrEqualTo(Integer value) {
            addCriterion("Increment_mode >=", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeLessThan(Integer value) {
            addCriterion("Increment_mode <", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeLessThanOrEqualTo(Integer value) {
            addCriterion("Increment_mode <=", value, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeIn(List<Integer> values) {
            addCriterion("Increment_mode in", values, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeNotIn(List<Integer> values) {
            addCriterion("Increment_mode not in", values, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeBetween(Integer value1, Integer value2) {
            addCriterion("Increment_mode between", value1, value2, "incrementMode");
            return (Criteria) this;
        }

        public Criteria andIncrementModeNotBetween(Integer value1, Integer value2) {
            addCriterion("Increment_mode not between", value1, value2, "incrementMode");
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

        public Criteria andBenchTimeIsNull() {
            addCriterion("bench_time is null");
            return (Criteria) this;
        }

        public Criteria andBenchTimeIsNotNull() {
            addCriterion("bench_time is not null");
            return (Criteria) this;
        }

        public Criteria andBenchTimeEqualTo(Integer value) {
            addCriterion("bench_time =", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeNotEqualTo(Integer value) {
            addCriterion("bench_time <>", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeGreaterThan(Integer value) {
            addCriterion("bench_time >", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("bench_time >=", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeLessThan(Integer value) {
            addCriterion("bench_time <", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeLessThanOrEqualTo(Integer value) {
            addCriterion("bench_time <=", value, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeIn(List<Integer> values) {
            addCriterion("bench_time in", values, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeNotIn(List<Integer> values) {
            addCriterion("bench_time not in", values, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeBetween(Integer value1, Integer value2) {
            addCriterion("bench_time between", value1, value2, "benchTime");
            return (Criteria) this;
        }

        public Criteria andBenchTimeNotBetween(Integer value1, Integer value2) {
            addCriterion("bench_time not between", value1, value2, "benchTime");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsIsNull() {
            addCriterion("max_bench_qps is null");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsIsNotNull() {
            addCriterion("max_bench_qps is not null");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsEqualTo(Integer value) {
            addCriterion("max_bench_qps =", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsNotEqualTo(Integer value) {
            addCriterion("max_bench_qps <>", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsGreaterThan(Integer value) {
            addCriterion("max_bench_qps >", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsGreaterThanOrEqualTo(Integer value) {
            addCriterion("max_bench_qps >=", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsLessThan(Integer value) {
            addCriterion("max_bench_qps <", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsLessThanOrEqualTo(Integer value) {
            addCriterion("max_bench_qps <=", value, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsIn(List<Integer> values) {
            addCriterion("max_bench_qps in", values, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsNotIn(List<Integer> values) {
            addCriterion("max_bench_qps not in", values, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsBetween(Integer value1, Integer value2) {
            addCriterion("max_bench_qps between", value1, value2, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andMaxBenchQpsNotBetween(Integer value1, Integer value2) {
            addCriterion("max_bench_qps not between", value1, value2, "maxBenchQps");
            return (Criteria) this;
        }

        public Criteria andRpsRateIsNull() {
            addCriterion("rps_rate is null");
            return (Criteria) this;
        }

        public Criteria andRpsRateIsNotNull() {
            addCriterion("rps_rate is not null");
            return (Criteria) this;
        }

        public Criteria andRpsRateEqualTo(Integer value) {
            addCriterion("rps_rate =", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateNotEqualTo(Integer value) {
            addCriterion("rps_rate <>", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateGreaterThan(Integer value) {
            addCriterion("rps_rate >", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateGreaterThanOrEqualTo(Integer value) {
            addCriterion("rps_rate >=", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateLessThan(Integer value) {
            addCriterion("rps_rate <", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateLessThanOrEqualTo(Integer value) {
            addCriterion("rps_rate <=", value, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateIn(List<Integer> values) {
            addCriterion("rps_rate in", values, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateNotIn(List<Integer> values) {
            addCriterion("rps_rate not in", values, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateBetween(Integer value1, Integer value2) {
            addCriterion("rps_rate between", value1, value2, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andRpsRateNotBetween(Integer value1, Integer value2) {
            addCriterion("rps_rate not between", value1, value2, "rpsRate");
            return (Criteria) this;
        }

        public Criteria andLogRateIsNull() {
            addCriterion("log_rate is null");
            return (Criteria) this;
        }

        public Criteria andLogRateIsNotNull() {
            addCriterion("log_rate is not null");
            return (Criteria) this;
        }

        public Criteria andLogRateEqualTo(Integer value) {
            addCriterion("log_rate =", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateNotEqualTo(Integer value) {
            addCriterion("log_rate <>", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateGreaterThan(Integer value) {
            addCriterion("log_rate >", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateGreaterThanOrEqualTo(Integer value) {
            addCriterion("log_rate >=", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateLessThan(Integer value) {
            addCriterion("log_rate <", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateLessThanOrEqualTo(Integer value) {
            addCriterion("log_rate <=", value, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateIn(List<Integer> values) {
            addCriterion("log_rate in", values, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateNotIn(List<Integer> values) {
            addCriterion("log_rate not in", values, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateBetween(Integer value1, Integer value2) {
            addCriterion("log_rate between", value1, value2, "logRate");
            return (Criteria) this;
        }

        public Criteria andLogRateNotBetween(Integer value1, Integer value2) {
            addCriterion("log_rate not between", value1, value2, "logRate");
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

        public Criteria andSuccessCodeIsNull() {
            addCriterion("success_code is null");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeIsNotNull() {
            addCriterion("success_code is not null");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeEqualTo(String value) {
            addCriterion("success_code =", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeNotEqualTo(String value) {
            addCriterion("success_code <>", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeGreaterThan(String value) {
            addCriterion("success_code >", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeGreaterThanOrEqualTo(String value) {
            addCriterion("success_code >=", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeLessThan(String value) {
            addCriterion("success_code <", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeLessThanOrEqualTo(String value) {
            addCriterion("success_code <=", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeLike(String value) {
            addCriterion("success_code like", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeNotLike(String value) {
            addCriterion("success_code not like", value, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeIn(List<String> values) {
            addCriterion("success_code in", values, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeNotIn(List<String> values) {
            addCriterion("success_code not in", values, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeBetween(String value1, String value2) {
            addCriterion("success_code between", value1, value2, "successCode");
            return (Criteria) this;
        }

        public Criteria andSuccessCodeNotBetween(String value1, String value2) {
            addCriterion("success_code not between", value1, value2, "successCode");
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

        public Criteria andSceneGroupIdIsNull() {
            addCriterion("scene_group_id is null");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdIsNotNull() {
            addCriterion("scene_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdEqualTo(Integer value) {
            addCriterion("scene_group_id =", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdNotEqualTo(Integer value) {
            addCriterion("scene_group_id <>", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdGreaterThan(Integer value) {
            addCriterion("scene_group_id >", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_group_id >=", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdLessThan(Integer value) {
            addCriterion("scene_group_id <", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdLessThanOrEqualTo(Integer value) {
            addCriterion("scene_group_id <=", value, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdIn(List<Integer> values) {
            addCriterion("scene_group_id in", values, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdNotIn(List<Integer> values) {
            addCriterion("scene_group_id not in", values, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdBetween(Integer value1, Integer value2) {
            addCriterion("scene_group_id between", value1, value2, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andSceneGroupIdNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_group_id not between", value1, value2, "sceneGroupId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdIsNull() {
            addCriterion("cur_report_id is null");
            return (Criteria) this;
        }

        public Criteria andCurReportIdIsNotNull() {
            addCriterion("cur_report_id is not null");
            return (Criteria) this;
        }

        public Criteria andCurReportIdEqualTo(String value) {
            addCriterion("cur_report_id =", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdNotEqualTo(String value) {
            addCriterion("cur_report_id <>", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdGreaterThan(String value) {
            addCriterion("cur_report_id >", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdGreaterThanOrEqualTo(String value) {
            addCriterion("cur_report_id >=", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdLessThan(String value) {
            addCriterion("cur_report_id <", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdLessThanOrEqualTo(String value) {
            addCriterion("cur_report_id <=", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdLike(String value) {
            addCriterion("cur_report_id like", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdNotLike(String value) {
            addCriterion("cur_report_id not like", value, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdIn(List<String> values) {
            addCriterion("cur_report_id in", values, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdNotIn(List<String> values) {
            addCriterion("cur_report_id not in", values, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdBetween(String value1, String value2) {
            addCriterion("cur_report_id between", value1, value2, "curReportId");
            return (Criteria) this;
        }

        public Criteria andCurReportIdNotBetween(String value1, String value2) {
            addCriterion("cur_report_id not between", value1, value2, "curReportId");
            return (Criteria) this;
        }

        public Criteria andSceneEnvIsNull() {
            addCriterion("scene_env is null");
            return (Criteria) this;
        }

        public Criteria andSceneEnvIsNotNull() {
            addCriterion("scene_env is not null");
            return (Criteria) this;
        }

        public Criteria andSceneEnvEqualTo(Integer value) {
            addCriterion("scene_env =", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvNotEqualTo(Integer value) {
            addCriterion("scene_env <>", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvGreaterThan(Integer value) {
            addCriterion("scene_env >", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_env >=", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvLessThan(Integer value) {
            addCriterion("scene_env <", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvLessThanOrEqualTo(Integer value) {
            addCriterion("scene_env <=", value, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvIn(List<Integer> values) {
            addCriterion("scene_env in", values, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvNotIn(List<Integer> values) {
            addCriterion("scene_env not in", values, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvBetween(Integer value1, Integer value2) {
            addCriterion("scene_env between", value1, value2, "sceneEnv");
            return (Criteria) this;
        }

        public Criteria andSceneEnvNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_env not between", value1, value2, "sceneEnv");
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

        public Criteria andBenchCountIsNull() {
            addCriterion("bench_count is null");
            return (Criteria) this;
        }

        public Criteria andBenchCountIsNotNull() {
            addCriterion("bench_count is not null");
            return (Criteria) this;
        }

        public Criteria andBenchCountEqualTo(Integer value) {
            addCriterion("bench_count =", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountNotEqualTo(Integer value) {
            addCriterion("bench_count <>", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountGreaterThan(Integer value) {
            addCriterion("bench_count >", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("bench_count >=", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountLessThan(Integer value) {
            addCriterion("bench_count <", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountLessThanOrEqualTo(Integer value) {
            addCriterion("bench_count <=", value, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountIn(List<Integer> values) {
            addCriterion("bench_count in", values, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountNotIn(List<Integer> values) {
            addCriterion("bench_count not in", values, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountBetween(Integer value1, Integer value2) {
            addCriterion("bench_count between", value1, value2, "benchCount");
            return (Criteria) this;
        }

        public Criteria andBenchCountNotBetween(Integer value1, Integer value2) {
            addCriterion("bench_count not between", value1, value2, "benchCount");
            return (Criteria) this;
        }

        public Criteria andSceneSourceIsNull() {
            addCriterion("scene_source is null");
            return (Criteria) this;
        }

        public Criteria andSceneSourceIsNotNull() {
            addCriterion("scene_source is not null");
            return (Criteria) this;
        }

        public Criteria andSceneSourceEqualTo(Integer value) {
            addCriterion("scene_source =", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceNotEqualTo(Integer value) {
            addCriterion("scene_source <>", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceGreaterThan(Integer value) {
            addCriterion("scene_source >", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceGreaterThanOrEqualTo(Integer value) {
            addCriterion("scene_source >=", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceLessThan(Integer value) {
            addCriterion("scene_source <", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceLessThanOrEqualTo(Integer value) {
            addCriterion("scene_source <=", value, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceIn(List<Integer> values) {
            addCriterion("scene_source in", values, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceNotIn(List<Integer> values) {
            addCriterion("scene_source not in", values, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceBetween(Integer value1, Integer value2) {
            addCriterion("scene_source between", value1, value2, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andSceneSourceNotBetween(Integer value1, Integer value2) {
            addCriterion("scene_source not between", value1, value2, "sceneSource");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeIsNull() {
            addCriterion("last_bench_time is null");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeIsNotNull() {
            addCriterion("last_bench_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeEqualTo(Long value) {
            addCriterion("last_bench_time =", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeNotEqualTo(Long value) {
            addCriterion("last_bench_time <>", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeGreaterThan(Long value) {
            addCriterion("last_bench_time >", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("last_bench_time >=", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeLessThan(Long value) {
            addCriterion("last_bench_time <", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeLessThanOrEqualTo(Long value) {
            addCriterion("last_bench_time <=", value, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeIn(List<Long> values) {
            addCriterion("last_bench_time in", values, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeNotIn(List<Long> values) {
            addCriterion("last_bench_time not in", values, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeBetween(Long value1, Long value2) {
            addCriterion("last_bench_time between", value1, value2, "lastBenchTime");
            return (Criteria) this;
        }

        public Criteria andLastBenchTimeNotBetween(Long value1, Long value2) {
            addCriterion("last_bench_time not between", value1, value2, "lastBenchTime");
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