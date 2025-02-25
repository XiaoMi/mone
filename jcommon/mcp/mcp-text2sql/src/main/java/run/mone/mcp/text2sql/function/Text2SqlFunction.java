package run.mone.mcp.text2sql.function;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.text2sql.model.ColumnInfo;
import run.mone.mcp.text2sql.model.ForeignKey;
import run.mone.mcp.text2sql.model.IndexInfo;
import run.mone.mcp.text2sql.model.TableSchema;

@Data
@Slf4j
public class Text2SqlFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private static final Gson gson = new Gson();

    private Connection connection;

    private String name = "db_executor";

    private String desc = "Querying the schema information of a table";;

    private String defaultGroupName = "your-default-GroupName";

    private static final Pattern JDBC_PATTERN =
        Pattern.compile("jdbc:(?<dbtype>\\w+)://(?<host>[^:/]+)(:(?<port>\\d+))?/(?<dbname>[^?]+)");

    private String toolSchema = """
        {
            "type": "object",
            "properties": {
                "type": {
                    "type": "string",
                    "enum": ["search_table_schema_info"],
                    "description": "search table's echema info"
                 },
                "database_url": {
                     "type": "string",
                     "description": "database's url"
                },
                "tables": {
                    "type": "array",
                    "items": {
                        "type": "string",
                        "description": "tableName"
                    }
                }
            },
            "required": ["type", "database_url", "tables"]
        }
        """;

    public Text2SqlFunction() {}

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String)args.get("type");
        String databaseUrl = (String)args.get("database_url");
        if (databaseUrl == null || databaseUrl.trim().isEmpty()) {
            log.error("Empty databaseUrl provided");
            throw new IllegalArgumentException("databaseUrl is required");
        }
        log.info("Executing {} operation: {}", type, databaseUrl);
        try {
            switch (type.toLowerCase()) {
                case "search_table_schema_info":
                    return executeSearchSchema(args, databaseUrl);
                default:
                    throw new IllegalArgumentException("Unsupported operation type: " + type);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private McpSchema.CallToolResult executeSearchSchema(Map<String, Object> args, String databaseUrl)
        throws SQLException {
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PW", "123456");
        List<TableSchema> schema = new ArrayList<>();
        List<String> tableNames = (List<String>)args.get("tables");
        try (Connection connection = DriverManager.getConnection(databaseUrl, user, password)) {
            DatabaseMetaData meta = connection.getMetaData();
            String tabelNamePattern = "";
            if (tableNames != null && tableNames.size() > 0) {
                for (String table : tableNames) {
                    schema.addAll(getTable(meta, table));
                }
            } else {
                schema.addAll(getTable(meta, "%"));;
            }
            log.info("Successfully executed query");
        }
        String toolResponse = formatAsJson(schema);
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(toolResponse)), false);
    }

    private List<TableSchema> getTable(DatabaseMetaData meta, String tabelNamePattern) throws SQLException {
        // 获取所有表
        List<TableSchema> schema = new ArrayList<>();
        try (ResultSet tables = meta.getTables(null, null, tabelNamePattern, null)) {
            while (tables.next()) {
                TableSchema table = new TableSchema();
                table.setTableName(tables.getString("TABLE_NAME"));
                table.setTableComment(tables.getString("REMARKS"));
                // 获取列信息
                extractColumns(meta, table);
                // 获取主键
                extractPrimaryKeys(meta, table);
                // 获取外键
                extractForeignKeys(meta, table);
                // 获取索引
                extractIndexes(meta, table);
                //
                schema.add(table);
            }
        }
        return schema;
    }

    private void extractColumns(DatabaseMetaData meta, TableSchema table) throws SQLException {
        try (ResultSet columns = meta.getColumns(null, null, table.getTableName(), null)) {
            while (columns.next()) {
                ColumnInfo col = new ColumnInfo();
                col.setColumnName(columns.getString("COLUMN_NAME"));
                col.setDataType(columns.getString("TYPE_NAME"));;
                col.setColumnSize(columns.getInt("COLUMN_SIZE"));
                col.setNullable("YES".equals(columns.getString("IS_NULLABLE")));
                col.setColumnComment(columns.getString("REMARKS"));
                table.getColumns().add(col);
            }
        }
    }

    private static void extractPrimaryKeys(DatabaseMetaData meta, TableSchema table) throws SQLException {
        try (ResultSet pks = meta.getPrimaryKeys(null, null, table.getTableName())) {
            while (pks.next()) {
                table.getPrimaryKeys().add(pks.getString("COLUMN_NAME"));
            }
        }
    }

    private static void extractForeignKeys(DatabaseMetaData meta, TableSchema table) throws SQLException {
        try (ResultSet fks = meta.getImportedKeys(null, null, table.getTableName())) {
            while (fks.next()) {
                ForeignKey fk = new ForeignKey();
                fk.setConstraintName(fks.getString("FK_NAME"));
                fk.setColumnName(fks.getString("FKCOLUMN_NAME"));
                fk.setForeignColumn(fks.getString("PKCOLUMN_NAME"));
                fk.setForeignTable(fks.getString("PKTABLE_NAME"));
                table.getForeignKeys().add(fk);
            }
        }
    }

    private static void extractIndexes(DatabaseMetaData meta, TableSchema table) throws SQLException {
        try (ResultSet indexes = meta.getIndexInfo(null, null, table.getTableName(), false, true)) {
            String lastIndex = null;
            IndexInfo current = null;

            while (indexes.next()) {
                String indexName = indexes.getString("INDEX_NAME");
                if (indexName == null)
                    continue;

                if (!indexName.equals(lastIndex)) {
                    current = new IndexInfo();
                    current.setIndexName(indexName);
                    current.setUnique(!indexes.getBoolean("NON_UNIQUE"));
                    table.getIndexes().add(current);
                    lastIndex = indexName;
                }
                current.getColumns().add(indexes.getString("COLUMN_NAME"));
            }
        }
    }

    private static String formatAsJson(List<TableSchema> schema) {
        return gson.toJson(schema);
    }
}
