package run.mone.mcp.mysql.function;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class MysqlFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "stream_mysql_executor";

    private String desc = "Execute MySQL operations (query, update, DDL)";

    private String database;

    private String password;

    private String sqlToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["query", "update", "ddl"],
                        "description": "Type of SQL operation to execute"
                     },
                     "sql": {
                         "type": "string",
                         "description": "SQL statement to execute"
                    }
                },
                "required": ["type", "sql"]
            }
            """;
    private Connection connection;


    public MysqlFunction(String db,String password) {
        log.info("Initializing MysqlFunction...");
        this.database = db;
        this.password = password;
    }

    private synchronized void ensureConnection() {
        if (connection != null) {
            try {
                if (connection.isValid(1)) {
                    return;
                }
            } catch (SQLException e) {
                log.warn("Connection validation failed", e);
            }
        }

        String host = System.getenv().getOrDefault("MYSQL_HOST", "127.0.0.1");
        String user = System.getenv().getOrDefault("MYSQL_USER", "root");

        if (user == null || password == null || database == null) {
            log.error("Missing required database configuration");
            throw new IllegalStateException("Missing required database configuration");
        }

        try {
            log.info("Attempting to connect to database at {}...", host);
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database,
                    user,
                    password
            );
            log.info("Successfully connected to database");
        } catch (SQLException e) {
            log.error("Failed to connect to database", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        String type = (String) args.get("type");
        String sql = (String) args.get("sql");
        if (sql == null || sql.trim().isEmpty()) {
            log.error("Empty SQL provided");
            throw new IllegalArgumentException("SQL is required");
        }
        log.info("Executing {} operation: {}", type, sql);
        ensureConnection();

        try {
            switch (type.toLowerCase()) {
                case "query":
                    return executeQuery(sql);
                case "update":
                    return executeUpdate(sql);
                case "ddl":
                    return executeDDL(sql);
                default:
                    throw new IllegalArgumentException("Unsupported operation type: " + type);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    private Flux<McpSchema.CallToolResult> executeQuery(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.setMaxRows(100);
            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder result = new StringBuilder();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names
            for (int i = 1; i <= columnCount; i++) {
                result.append(metaData.getColumnName(i)).append("\t");
            }
            result.append("\n");

            // Add data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getString(i)).append("\t");
                }
                result.append("\n");
            }

            log.info("Successfully executed query");

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.toString())),
                    false
            ));
        }
    }

    private Flux<McpSchema.CallToolResult> executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            int affected = stmt.executeUpdate(sql);
            log.info("Successfully executed update. Affected rows: {}", affected);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Affected rows: " + affected)),
                    false
            ));
        }
    }

    private Flux<McpSchema.CallToolResult> executeDDL(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            boolean result = stmt.execute(sql);
            if (result) {
                // DDL操作返回了结果集（不太常见）
                ResultSet rs = stmt.getResultSet();
                StringBuilder stringBuilder = new StringBuilder();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        stringBuilder.append(rs.getString(i)).append("\t");
                    }
                    stringBuilder.append("\n");
                }
                log.info("Successfully executed DDL with result set");
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(stringBuilder.toString())),
                        false
                ));
            } else {
                int affected = stmt.getUpdateCount();
                log.info("Successfully executed UPDATE query. Affected rows: {}", affected);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Affected rows: " + affected)),
                        false
                ));
            }
        }
    }

}