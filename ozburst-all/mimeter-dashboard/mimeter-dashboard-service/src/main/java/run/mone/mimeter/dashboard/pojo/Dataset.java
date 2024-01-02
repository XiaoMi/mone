package run.mone.mimeter.dashboard.pojo;

import java.util.ArrayList;
import java.util.Arrays;

public class Dataset {
    private Integer id;

    private String name;

    private String note;

    private Integer type;

    private String defaultParamName;

    private Integer ignoreFirstRow;

    private String fileName;

    private String fileUrl;

    private String fileKsKey;

    private Long fileRows;

    private Long fileSize;

    private String interfaceUrl;

    private Integer trafficRecordId;

    private Long ctime;

    private Long utime;

    private String creator;

    private String updater;

    private String tenant;

    private String previewFileRows;

    private String header;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDefaultParamName() {
        return defaultParamName;
    }

    public void setDefaultParamName(String defaultParamName) {
        this.defaultParamName = defaultParamName == null ? null : defaultParamName.trim();
    }

    public Integer getIgnoreFirstRow() {
        return ignoreFirstRow;
    }

    public void setIgnoreFirstRow(Integer ignoreFirstRow) {
        this.ignoreFirstRow = ignoreFirstRow;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
    }

    public String getFileKsKey() {
        return fileKsKey;
    }

    public void setFileKsKey(String fileKsKey) {
        this.fileKsKey = fileKsKey == null ? null : fileKsKey.trim();
    }

    public Long getFileRows() {
        return fileRows;
    }

    public void setFileRows(Long fileRows) {
        this.fileRows = fileRows;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl == null ? null : interfaceUrl.trim();
    }

    public Integer getTrafficRecordId() {
        return trafficRecordId;
    }

    public void setTrafficRecordId(Integer trafficRecordId) {
        this.trafficRecordId = trafficRecordId;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater == null ? null : updater.trim();
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant == null ? null : tenant.trim();
    }

    public String getPreviewFileRows() {
        return previewFileRows;
    }

    public void setPreviewFileRows(String previewFileRows) {
        this.previewFileRows = previewFileRows == null ? null : previewFileRows.trim();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header == null ? null : header.trim();
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        name("name", "name", "VARCHAR", false),
        note("note", "note", "VARCHAR", false),
        type("type", "type", "INTEGER", false),
        defaultParamName("default_param_name", "defaultParamName", "VARCHAR", false),
        ignoreFirstRow("ignore_first_row", "ignoreFirstRow", "INTEGER", false),
        fileName("file_name", "fileName", "VARCHAR", false),
        fileUrl("file_url", "fileUrl", "VARCHAR", false),
        fileKsKey("file_ks_key", "fileKsKey", "VARCHAR", false),
        fileRows("file_rows", "fileRows", "BIGINT", false),
        fileSize("file_size", "fileSize", "BIGINT", false),
        interfaceUrl("interface_url", "interfaceUrl", "VARCHAR", false),
        trafficRecordId("traffic_record_id", "trafficRecordId", "INTEGER", false),
        ctime("ctime", "ctime", "BIGINT", false),
        utime("utime", "utime", "BIGINT", false),
        creator("creator", "creator", "VARCHAR", false),
        updater("updater", "updater", "VARCHAR", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        previewFileRows("preview_file_rows", "previewFileRows", "LONGVARCHAR", false),
        header("header", "header", "LONGVARCHAR", false);

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