package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ReportStat {
    private Long id;

    private Long sceneId;

    private String reportId;

    private Integer bizSucc;

    private Integer bizFail;

    private Integer reqSucc;

    private Integer reqFail;

    private Integer tps;

    private Integer tpsMax;

    private Integer rt;

    private Integer rtMax;

    private Integer rt99;

    private Integer rt90;

    private Date createTime;

    private Date updateTime;

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

    public Integer getBizSucc() {
        return bizSucc;
    }

    public void setBizSucc(Integer bizSucc) {
        this.bizSucc = bizSucc;
    }

    public Integer getBizFail() {
        return bizFail;
    }

    public void setBizFail(Integer bizFail) {
        this.bizFail = bizFail;
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

    public enum Column {
        id("id", "id", "BIGINT", false),
        sceneId("scene_id", "sceneId", "BIGINT", false),
        reportId("report_id", "reportId", "VARCHAR", false),
        bizSucc("biz_succ", "bizSucc", "INTEGER", false),
        bizFail("biz_fail", "bizFail", "INTEGER", false),
        reqSucc("req_succ", "reqSucc", "INTEGER", false),
        reqFail("req_fail", "reqFail", "INTEGER", false),
        tps("tps", "tps", "INTEGER", false),
        tpsMax("tps_max", "tpsMax", "INTEGER", false),
        rt("rt", "rt", "INTEGER", false),
        rtMax("rt_max", "rtMax", "INTEGER", false),
        rt99("rt99", "rt99", "INTEGER", false),
        rt90("rt90", "rt90", "INTEGER", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false);

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