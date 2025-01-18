package run.mone.mcp.mysql.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class MysqlFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "execute_sql";

    private String desc = "Execute SQL query";

    // 定义execute_sql工具的schema
    String sqlToolSchema = """
        {
            "type": "object",
            "properties": {
                "query": {
                    "type": "string",
                    "description": "SQL query to execute"
                }
            },
            "required": ["query"]
        }
        """;

    private Connection connection;

    public MysqlFunction() {
        log.info("Initializing MysqlFunction...");
        String host = System.getenv().getOrDefault("MYSQL_HOST", "localhost");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");
        String database = System.getenv("MYSQL_DATABASE");

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

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String query = (String) args.get("query");

        if (query == null || query.trim().isEmpty()) {
            log.error("Empty query provided");
            throw new IllegalArgumentException("Query is required");
        }

        log.info("Executing query: {}", query);
        try (Statement stmt = connection.createStatement()) {
            // 处理不同类型的查询
            if (query.toUpperCase().startsWith("SHOW TABLES")) {
                ResultSet rs = stmt.executeQuery(query);
                List<String> tables = new ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
                log.info("Successfully executed SHOW TABLES query");
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(String.join("\n", tables))),
                        false
                );
            } else if (query.toUpperCase().startsWith("SELECT")) {
                // 限制SELECT查询结果
                stmt.setMaxRows(100);
                ResultSet rs = stmt.executeQuery(query);
                StringBuilder result = new StringBuilder();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // 添加列名
                for (int i = 1; i <= columnCount; i++) {
                    result.append(metaData.getColumnName(i)).append("\t");
                }
                result.append("\n");

                // 添加数据行
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(rs.getString(i)).append("\t");
                    }
                    result.append("\n");
                }

                log.info("Successfully executed SELECT query");
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(result.toString())),
                        false
                );
            } else {
                // 执行其他类型的查询
                int affected = stmt.executeUpdate(query);
                log.info("Successfully executed UPDATE query. Affected rows: {}", affected);
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Affected rows: " + affected)),
                        false
                );
            }
        } catch (SQLException e) {
            log.error("SQL execution failed", e);
            throw new RuntimeException("SQL execution failed: " + e.getMessage());
        }
    }
}
