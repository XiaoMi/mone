package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class MibenchTask {
    private Integer id;

    private Integer qps;

    private Integer originQps;

    private Integer maxQps;

    private Integer sceneId;

    private Integer serialLinkId;

    private Integer sceneApiId;

    private Integer time;

    private Integer agentNum;

    private Integer finishAgentNum;

    private Long ctime;

    private Long utime;

    private Integer state;

    private Integer version;

    private Long successNum;

    private Long failureNum;

    private Integer taskType;

    private Integer parentTaskId;

    private String reportId;

    private Integer reqParamType;

    private Boolean ok;

    private Integer connectTaskNum;

    private Integer debugRt;

    private Integer debugSize;

    private Integer benchMode;

    private Integer increaseMode;

    private Integer increasePercent;

    private String debugResult;

    private String debugResultHeader;

    private String requestParams;

    private String debugTriggerCp;

    private String debugTriggerFilterCondition;

    private String debugReqHeaders;

    private String debugUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQps() {
        return qps;
    }

    public void setQps(Integer qps) {
        this.qps = qps;
    }

    public Integer getOriginQps() {
        return originQps;
    }

    public void setOriginQps(Integer originQps) {
        this.originQps = originQps;
    }

    public Integer getMaxQps() {
        return maxQps;
    }

    public void setMaxQps(Integer maxQps) {
        this.maxQps = maxQps;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getSerialLinkId() {
        return serialLinkId;
    }

    public void setSerialLinkId(Integer serialLinkId) {
        this.serialLinkId = serialLinkId;
    }

    public Integer getSceneApiId() {
        return sceneApiId;
    }

    public void setSceneApiId(Integer sceneApiId) {
        this.sceneApiId = sceneApiId;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(Integer agentNum) {
        this.agentNum = agentNum;
    }

    public Integer getFinishAgentNum() {
        return finishAgentNum;
    }

    public void setFinishAgentNum(Integer finishAgentNum) {
        this.finishAgentNum = finishAgentNum;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Long successNum) {
        this.successNum = successNum;
    }

    public Long getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(Long failureNum) {
        this.failureNum = failureNum;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId == null ? null : reportId.trim();
    }

    public Integer getReqParamType() {
        return reqParamType;
    }

    public void setReqParamType(Integer reqParamType) {
        this.reqParamType = reqParamType;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Integer getConnectTaskNum() {
        return connectTaskNum;
    }

    public void setConnectTaskNum(Integer connectTaskNum) {
        this.connectTaskNum = connectTaskNum;
    }

    public Integer getDebugRt() {
        return debugRt;
    }

    public void setDebugRt(Integer debugRt) {
        this.debugRt = debugRt;
    }

    public Integer getDebugSize() {
        return debugSize;
    }

    public void setDebugSize(Integer debugSize) {
        this.debugSize = debugSize;
    }

    public Integer getBenchMode() {
        return benchMode;
    }

    public void setBenchMode(Integer benchMode) {
        this.benchMode = benchMode;
    }

    public Integer getIncreaseMode() {
        return increaseMode;
    }

    public void setIncreaseMode(Integer increaseMode) {
        this.increaseMode = increaseMode;
    }

    public Integer getIncreasePercent() {
        return increasePercent;
    }

    public void setIncreasePercent(Integer increasePercent) {
        this.increasePercent = increasePercent;
    }

    public String getDebugResult() {
        return debugResult;
    }

    public void setDebugResult(String debugResult) {
        this.debugResult = debugResult == null ? null : debugResult.trim();
    }

    public String getDebugResultHeader() {
        return debugResultHeader;
    }

    public void setDebugResultHeader(String debugResultHeader) {
        this.debugResultHeader = debugResultHeader == null ? null : debugResultHeader.trim();
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams == null ? null : requestParams.trim();
    }

    public String getDebugTriggerCp() {
        return debugTriggerCp;
    }

    public void setDebugTriggerCp(String debugTriggerCp) {
        this.debugTriggerCp = debugTriggerCp == null ? null : debugTriggerCp.trim();
    }

    public String getDebugTriggerFilterCondition() {
        return debugTriggerFilterCondition;
    }

    public void setDebugTriggerFilterCondition(String debugTriggerFilterCondition) {
        this.debugTriggerFilterCondition = debugTriggerFilterCondition == null ? null : debugTriggerFilterCondition.trim();
    }

    public String getDebugReqHeaders() {
        return debugReqHeaders;
    }

    public void setDebugReqHeaders(String debugReqHeaders) {
        this.debugReqHeaders = debugReqHeaders == null ? null : debugReqHeaders.trim();
    }

    public String getDebugUrl() {
        return debugUrl;
    }

    public void setDebugUrl(String debugUrl) {
        this.debugUrl = debugUrl == null ? null : debugUrl.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        qps("qps", "qps", "INTEGER", false),
        originQps("origin_qps", "originQps", "INTEGER", false),
        maxQps("max_qps", "maxQps", "INTEGER", false),
        sceneId("scene_id", "sceneId", "INTEGER", false),
        serialLinkId("serial_link_id", "serialLinkId", "INTEGER", false),
        sceneApiId("scene_api_id", "sceneApiId", "INTEGER", false),
        time("time", "time", "INTEGER", false),
        agentNum("agent_num", "agentNum", "INTEGER", false),
        finishAgentNum("finish_agent_num", "finishAgentNum", "INTEGER", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        state("state", "state", "INTEGER", false),
        version("version", "version", "INTEGER", false),
        successNum("success_num", "successNum", "BIGINT", false),
        failureNum("failure_num", "failureNum", "BIGINT", false),
        taskType("task_type", "taskType", "INTEGER", false),
        parentTaskId("parent_task_id", "parentTaskId", "INTEGER", false),
        reportId("report_id", "reportId", "VARCHAR", false),
        reqParamType("req_param_type", "reqParamType", "INTEGER", false),
        ok("ok", "ok", "BIT", false),
        connectTaskNum("connect_task_num", "connectTaskNum", "INTEGER", false),
        debugRt("debug_rt", "debugRt", "INTEGER", false),
        debugSize("debug_size", "debugSize", "INTEGER", false),
        benchMode("bench_mode", "benchMode", "INTEGER", false),
        increaseMode("increase_mode", "increaseMode", "INTEGER", false),
        increasePercent("increase_percent", "increasePercent", "INTEGER", false),
        debugResult("debug_result", "debugResult", "LONGVARCHAR", false),
        debugResultHeader("debug_result_header", "debugResultHeader", "LONGVARCHAR", false),
        requestParams("request_params", "requestParams", "LONGVARCHAR", false),
        debugTriggerCp("debug_trigger_cp", "debugTriggerCp", "LONGVARCHAR", false),
        debugTriggerFilterCondition("debug_trigger_filter_condition", "debugTriggerFilterCondition", "LONGVARCHAR", false),
        debugReqHeaders("debug_req_headers", "debugReqHeaders", "LONGVARCHAR", false),
        debugUrl("debug_url", "debugUrl", "LONGVARCHAR", false);

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