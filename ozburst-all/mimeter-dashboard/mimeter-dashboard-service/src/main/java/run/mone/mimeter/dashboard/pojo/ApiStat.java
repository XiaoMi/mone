package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ApiStat {
    private Long id;

    private Long sceneId;

    private String reportId;

    private Long apiId;

    private Integer reqSucc;

    private Integer reqFail;

    private Integer tps;

    private Integer tpsMax;

    private Integer rt;

    private Integer rtMax;

    private Integer rt99;

    private Integer rt90;

    private Integer rt70;

    private Integer rt50;

    private Integer connDuration;

    private Integer recvDuration;

    private Integer sendDuration;

    private Integer waitDuration;

    private Date createTime;

    private Date updateTime;

    private String apiUri;

    private String apiMethod;

    private Long serialId;

    private Long taskId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId == null ? null : reportId.trim();
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Integer getReqSucc() {
        return reqSucc;
    }

    public void setReqSucc(Integer reqSucc) {
        this.reqSucc = reqSucc;
    }

    public Integer getReqFail() {
        return reqFail;
    }

    public void setReqFail(Integer reqFail) {
        this.reqFail = reqFail;
    }

    public Integer getTps() {
        return tps;
    }

    public void setTps(Integer tps) {
        this.tps = tps;
    }

    public Integer getTpsMax() {
        return tpsMax;
    }

    public void setTpsMax(Integer tpsMax) {
        this.tpsMax = tpsMax;
    }

    public Integer getRt() {
        return rt;
    }

    public void setRt(Integer rt) {
        this.rt = rt;
    }

    public Integer getRtMax() {
        return rtMax;
    }

    public void setRtMax(Integer rtMax) {
        this.rtMax = rtMax;
    }

    public Integer getRt99() {
        return rt99;
    }

    public void setRt99(Integer rt99) {
        this.rt99 = rt99;
    }

    public Integer getRt90() {
        return rt90;
    }

    public void setRt90(Integer rt90) {
        this.rt90 = rt90;
    }

    public Integer getRt70() {
        return rt70;
    }

    public void setRt70(Integer rt70) {
        this.rt70 = rt70;
    }

    public Integer getRt50() {
        return rt50;
    }

    public void setRt50(Integer rt50) {
        this.rt50 = rt50;
    }

    public Integer getConnDuration() {
        return connDuration;
    }

    public void setConnDuration(Integer connDuration) {
        this.connDuration = connDuration;
    }

    public Integer getRecvDuration() {
        return recvDuration;
    }

    public void setRecvDuration(Integer recvDuration) {
        this.recvDuration = recvDuration;
    }

    public Integer getSendDuration() {
        return sendDuration;
    }

    public void setSendDuration(Integer sendDuration) {
        this.sendDuration = sendDuration;
    }

    public Integer getWaitDuration() {
        return waitDuration;
    }

    public void setWaitDuration(Integer waitDuration) {
        this.waitDuration = waitDuration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri == null ? null : apiUri.trim();
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod == null ? null : apiMethod.trim();
    }

    public Long getSerialId() {
        return serialId;
    }

    public void setSerialId(Long serialId) {
        this.serialId = serialId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public enum Column {
        id("id", "id", "BIGINT", false),
        sceneId("scene_id", "sceneId", "BIGINT", false),
        reportId("report_id", "reportId", "VARCHAR", false),
        apiId("api_id", "apiId", "BIGINT", false),
        reqSucc("req_succ", "reqSucc", "INTEGER", false),
        reqFail("req_fail", "reqFail", "INTEGER", false),
        tps("tps", "tps", "INTEGER", false),
        tpsMax("tps_max", "tpsMax", "INTEGER", false),
        rt("rt", "rt", "INTEGER", false),
        rtMax("rt_max", "rtMax", "INTEGER", false),
        rt99("rt99", "rt99", "INTEGER", false),
        rt90("rt90", "rt90", "INTEGER", false),
        rt70("rt70", "rt70", "INTEGER", false),
        rt50("rt50", "rt50", "INTEGER", false),
        connDuration("conn_duration", "connDuration", "INTEGER", false),
        recvDuration("recv_duration", "recvDuration", "INTEGER", false),
        sendDuration("send_duration", "sendDuration", "INTEGER", false),
        waitDuration("wait_duration", "waitDuration", "INTEGER", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        apiUri("api_uri", "apiUri", "VARCHAR", false),
        apiMethod("api_method", "apiMethod", "VARCHAR", false),
        serialId("serial_id", "serialId", "BIGINT", false),
        taskId("task_id", "taskId", "BIGINT", false);

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