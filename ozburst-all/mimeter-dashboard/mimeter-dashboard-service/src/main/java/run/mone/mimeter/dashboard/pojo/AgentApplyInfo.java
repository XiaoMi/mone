package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class AgentApplyInfo {
    private Integer id;

    private String applyUser;

    private String applyOrgId;

    private String applyOrgName;

    private String agentIp;

    private String agentHostname;

    private Integer applyStatus;

    private Long ctime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser == null ? null : applyUser.trim();
    }

    public String getApplyOrgId() {
        return applyOrgId;
    }

    public void setApplyOrgId(String applyOrgId) {
        this.applyOrgId = applyOrgId == null ? null : applyOrgId.trim();
    }

    public String getApplyOrgName() {
        return applyOrgName;
    }

    public void setApplyOrgName(String applyOrgName) {
        this.applyOrgName = applyOrgName == null ? null : applyOrgName.trim();
    }

    public String getAgentIp() {
        return agentIp;
    }

    public void setAgentIp(String agentIp) {
        this.agentIp = agentIp == null ? null : agentIp.trim();
    }

    public String getAgentHostname() {
        return agentHostname;
    }

    public void setAgentHostname(String agentHostname) {
        this.agentHostname = agentHostname == null ? null : agentHostname.trim();
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        applyUser("apply_user", "applyUser", "VARCHAR", false),
        applyOrgId("apply_org_id", "applyOrgId", "VARCHAR", false),
        applyOrgName("apply_org_name", "applyOrgName", "VARCHAR", false),
        agentIp("agent_ip", "agentIp", "VARCHAR", false),
        agentHostname("agent_hostname", "agentHostname", "VARCHAR", false),
        applyStatus("apply_status", "applyStatus", "INTEGER", false),
        ctime("ctime", "ctime", "BIGINT", false);

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