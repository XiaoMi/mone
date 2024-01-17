package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class AgentInfo {
    private Integer id;

    private String serverName;

    private String ip;

    private Integer port;

    private Integer cpu;

    private Long mem;

    private Integer useCpu;

    private Long useMem;

    private String hostname;

    private String clientDesc;

    private Long ctime;

    private Long utime;

    private Boolean enable;

    private String nodeIp;

    private String tenant;

    private String tenantCn;

    private String domainConf;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName == null ? null : serverName.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Long getMem() {
        return mem;
    }

    public void setMem(Long mem) {
        this.mem = mem;
    }

    public Integer getUseCpu() {
        return useCpu;
    }

    public void setUseCpu(Integer useCpu) {
        this.useCpu = useCpu;
    }

    public Long getUseMem() {
        return useMem;
    }

    public void setUseMem(Long useMem) {
        this.useMem = useMem;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname == null ? null : hostname.trim();
    }

    public String getClientDesc() {
        return clientDesc;
    }

    public void setClientDesc(String clientDesc) {
        this.clientDesc = clientDesc == null ? null : clientDesc.trim();
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp == null ? null : nodeIp.trim();
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant == null ? null : tenant.trim();
    }

    public String getTenantCn() {
        return tenantCn;
    }

    public void setTenantCn(String tenantCn) {
        this.tenantCn = tenantCn == null ? null : tenantCn.trim();
    }

    public String getDomainConf() {
        return domainConf;
    }

    public void setDomainConf(String domainConf) {
        this.domainConf = domainConf == null ? null : domainConf.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        serverName("server_name", "serverName", "VARCHAR", false),
        ip("ip", "ip", "VARCHAR", false),
        port("port", "port", "INTEGER", false),
        cpu("cpu", "cpu", "INTEGER", false),
        mem("mem", "mem", "BIGINT", false),
        useCpu("use_cpu", "useCpu", "INTEGER", false),
        useMem("use_mem", "useMem", "BIGINT", false),
        hostname("hostname", "hostname", "VARCHAR", false),
        clientDesc("client_desc", "clientDesc", "VARCHAR", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        enable("enable", "enable", "BIT", false),
        nodeIp("node_ip", "nodeIp", "VARCHAR", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        tenantCn("tenant_cn", "tenantCn", "VARCHAR", false),
        domainConf("domain_conf", "domainConf", "LONGVARCHAR", false);

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