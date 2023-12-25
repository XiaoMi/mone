package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ReportInfo {
    private Long id;

    private Long sceneId;

    private String snapshotId;

    private String reportId;

    private String reportName;

    private Integer duration;

    private Integer concurrency;

    private Integer concurrencyMax;

    private String createBy;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private Long taskId;

    private String filePath;

    private Long finishTime;

    private String tenant;

    private String agents;

    private String extra;

    private String slaEventList;

    private String totalStatAnalysisEventList;

    private String linkToDagId;

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

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId == null ? null : snapshotId.trim();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId == null ? null : reportId.trim();
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName == null ? null : reportName.trim();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public Integer getConcurrencyMax() {
        return concurrencyMax;
    }

    public void setConcurrencyMax(Integer concurrencyMax) {
        this.concurrencyMax = concurrencyMax;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant == null ? null : tenant.trim();
    }

    public String getAgents() {
        return agents;
    }

    public void setAgents(String agents) {
        this.agents = agents == null ? null : agents.trim();
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra == null ? null : extra.trim();
    }

    public String getSlaEventList() {
        return slaEventList;
    }

    public void setSlaEventList(String slaEventList) {
        this.slaEventList = slaEventList == null ? null : slaEventList.trim();
    }

    public String getTotalStatAnalysisEventList() {
        return totalStatAnalysisEventList;
    }

    public void setTotalStatAnalysisEventList(String totalStatAnalysisEventList) {
        this.totalStatAnalysisEventList = totalStatAnalysisEventList == null ? null : totalStatAnalysisEventList.trim();
    }

    public String getLinkToDagId() {
        return linkToDagId;
    }

    public void setLinkToDagId(String linkToDagId) {
        this.linkToDagId = linkToDagId == null ? null : linkToDagId.trim();
    }

    public enum Column {
        id("id", "id", "BIGINT", false),
        sceneId("scene_id", "sceneId", "BIGINT", false),
        snapshotId("snapshot_id", "snapshotId", "VARCHAR", false),
        reportId("report_id", "reportId", "VARCHAR", false),
        reportName("report_name", "reportName", "VARCHAR", false),
        duration("duration", "duration", "INTEGER", false),
        concurrency("concurrency", "concurrency", "INTEGER", false),
        concurrencyMax("concurrency_max", "concurrencyMax", "INTEGER", false),
        createBy("create_by", "createBy", "VARCHAR", false),
        createTime("create_time", "createTime", "TIMESTAMP", false),
        updateTime("update_time", "updateTime", "TIMESTAMP", false),
        status("status", "status", "INTEGER", false),
        taskId("task_id", "taskId", "BIGINT", false),
        filePath("file_path", "filePath", "VARCHAR", false),
        finishTime("finish_time", "finishTime", "BIGINT", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        agents("agents", "agents", "LONGVARCHAR", false),
        extra("extra", "extra", "LONGVARCHAR", false),
        slaEventList("sla_event_list", "slaEventList", "LONGVARCHAR", false),
        totalStatAnalysisEventList("total_stat_analysis_event_list", "totalStatAnalysisEventList", "LONGVARCHAR", false),
        linkToDagId("link_to_dag_id", "linkToDagId", "LONGVARCHAR", false);

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