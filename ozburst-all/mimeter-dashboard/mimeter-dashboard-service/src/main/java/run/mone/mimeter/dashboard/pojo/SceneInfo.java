package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class SceneInfo {
    private Integer id;

    private String name;

    private Integer sceneStatus;

    private String creator;

    private String updator;

    private Integer apinum;

    private String remark;

    private Integer sceneType;

    private Integer benchMode;

    private Integer incrementMode;

    private Integer increasePercent;

    private Integer benchTime;

    private Integer maxBenchQps;

    private Integer rpsRate;

    private Integer logRate;

    private Integer requestTimeout;

    private String successCode;

    private Long ctime;

    private Long utime;

    private Integer sceneGroupId;

    private String curReportId;

    private Integer sceneEnv;

    private String tenant;

    private Integer benchCount;

    private Integer sceneSource;

    private Long lastBenchTime;

    private String apiBenchInfos;

    private String sla;

    private String globalHeader;

    private String agentList;

    private String refDatasetIds;

    private String personInCharge;

    private String benchCalendar;

    private String globalTspAuth;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSceneStatus() {
        return sceneStatus;
    }

    public void setSceneStatus(Integer sceneStatus) {
        this.sceneStatus = sceneStatus;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator == null ? null : updator.trim();
    }

    public Integer getApinum() {
        return apinum;
    }

    public void setApinum(Integer apinum) {
        this.apinum = apinum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getSceneType() {
        return sceneType;
    }

    public void setSceneType(Integer sceneType) {
        this.sceneType = sceneType;
    }

    public Integer getBenchMode() {
        return benchMode;
    }

    public void setBenchMode(Integer benchMode) {
        this.benchMode = benchMode;
    }

    public Integer getIncrementMode() {
        return incrementMode;
    }

    public void setIncrementMode(Integer incrementMode) {
        this.incrementMode = incrementMode;
    }

    public Integer getIncreasePercent() {
        return increasePercent;
    }

    public void setIncreasePercent(Integer increasePercent) {
        this.increasePercent = increasePercent;
    }

    public Integer getBenchTime() {
        return benchTime;
    }

    public void setBenchTime(Integer benchTime) {
        this.benchTime = benchTime;
    }

    public Integer getMaxBenchQps() {
        return maxBenchQps;
    }

    public void setMaxBenchQps(Integer maxBenchQps) {
        this.maxBenchQps = maxBenchQps;
    }

    public Integer getRpsRate() {
        return rpsRate;
    }

    public void setRpsRate(Integer rpsRate) {
        this.rpsRate = rpsRate;
    }

    public Integer getLogRate() {
        return logRate;
    }

    public void setLogRate(Integer logRate) {
        this.logRate = logRate;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode == null ? null : successCode.trim();
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Integer getSceneGroupId() {
        return sceneGroupId;
    }

    public void setSceneGroupId(Integer sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }

    public String getCurReportId() {
        return curReportId;
    }

    public void setCurReportId(String curReportId) {
        this.curReportId = curReportId == null ? null : curReportId.trim();
    }

    public Integer getSceneEnv() {
        return sceneEnv;
    }

    public void setSceneEnv(Integer sceneEnv) {
        this.sceneEnv = sceneEnv;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant == null ? null : tenant.trim();
    }

    public Integer getBenchCount() {
        return benchCount;
    }

    public void setBenchCount(Integer benchCount) {
        this.benchCount = benchCount;
    }

    public Integer getSceneSource() {
        return sceneSource;
    }

    public void setSceneSource(Integer sceneSource) {
        this.sceneSource = sceneSource;
    }

    public Long getLastBenchTime() {
        return lastBenchTime;
    }

    public void setLastBenchTime(Long lastBenchTime) {
        this.lastBenchTime = lastBenchTime;
    }

    public String getApiBenchInfos() {
        return apiBenchInfos;
    }

    public void setApiBenchInfos(String apiBenchInfos) {
        this.apiBenchInfos = apiBenchInfos == null ? null : apiBenchInfos.trim();
    }

    public String getSla() {
        return sla;
    }

    public void setSla(String sla) {
        this.sla = sla == null ? null : sla.trim();
    }

    public String getGlobalHeader() {
        return globalHeader;
    }

    public void setGlobalHeader(String globalHeader) {
        this.globalHeader = globalHeader == null ? null : globalHeader.trim();
    }

    public String getAgentList() {
        return agentList;
    }

    public void setAgentList(String agentList) {
        this.agentList = agentList == null ? null : agentList.trim();
    }

    public String getRefDatasetIds() {
        return refDatasetIds;
    }

    public void setRefDatasetIds(String refDatasetIds) {
        this.refDatasetIds = refDatasetIds == null ? null : refDatasetIds.trim();
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge == null ? null : personInCharge.trim();
    }

    public String getBenchCalendar() {
        return benchCalendar;
    }

    public void setBenchCalendar(String benchCalendar) {
        this.benchCalendar = benchCalendar == null ? null : benchCalendar.trim();
    }

    public String getGlobalTspAuth() {
        return globalTspAuth;
    }

    public void setGlobalTspAuth(String globalTspAuth) {
        this.globalTspAuth = globalTspAuth == null ? null : globalTspAuth.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        name("name", "name", "VARCHAR", false),
        sceneStatus("scene_status", "sceneStatus", "INTEGER", false),
        creator("creator", "creator", "VARCHAR", false),
        updator("updator", "updator", "VARCHAR", false),
        apinum("apiNum", "apinum", "INTEGER", false),
        remark("remark", "remark", "VARCHAR", false),
        sceneType("scene_type", "sceneType", "INTEGER", false),
        benchMode("bench_mode", "benchMode", "INTEGER", false),
        incrementMode("Increment_mode", "incrementMode", "INTEGER", false),
        increasePercent("increase_percent", "increasePercent", "INTEGER", false),
        benchTime("bench_time", "benchTime", "INTEGER", false),
        maxBenchQps("max_bench_qps", "maxBenchQps", "INTEGER", false),
        rpsRate("rps_rate", "rpsRate", "INTEGER", false),
        logRate("log_rate", "logRate", "INTEGER", false),
        requestTimeout("request_timeout", "requestTimeout", "INTEGER", false),
        successCode("success_code", "successCode", "VARCHAR", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        sceneGroupId("scene_group_id", "sceneGroupId", "INTEGER", false),
        curReportId("cur_report_id", "curReportId", "VARCHAR", false),
        sceneEnv("scene_env", "sceneEnv", "INTEGER", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        benchCount("bench_count", "benchCount", "INTEGER", false),
        sceneSource("scene_source", "sceneSource", "INTEGER", false),
        lastBenchTime("last_bench_time", "lastBenchTime", "BIGINT", false),
        apiBenchInfos("api_bench_infos", "apiBenchInfos", "LONGVARCHAR", false),
        sla("sla", "sla", "LONGVARCHAR", false),
        globalHeader("global_header", "globalHeader", "LONGVARCHAR", false),
        agentList("agent_list", "agentList", "LONGVARCHAR", false),
        refDatasetIds("ref_dataset_ids", "refDatasetIds", "LONGVARCHAR", false),
        personInCharge("person_in_charge", "personInCharge", "LONGVARCHAR", false),
        benchCalendar("bench_calendar", "benchCalendar", "LONGVARCHAR", false),
        globalTspAuth("global_tsp_auth", "globalTspAuth", "LONGVARCHAR", false);

        private static final String BEGINNING_DELIMITER = "\"";

        private static final String ENDING_DELIMITER = "\"";

        private final String column;

        private final boolean isColumnNameDelimited;

        private final String javaProperty;

        private final String jdbcType;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String getJavaProperty() {
            return this.javaProperty;
        }

        public String getJdbcType() {
            return this.jdbcType;
        }

        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        public static Column[] all() {
            return Column.values();
        }

        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}