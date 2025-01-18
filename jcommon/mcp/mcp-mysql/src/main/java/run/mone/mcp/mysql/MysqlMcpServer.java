package run.mone.mcp.mysql;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class MysqlMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public MysqlMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("mysql_mcp_server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

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

        // 注册execute_sql工具
        var sqlToolRegistration = new McpServer.ToolRegistration(
                new McpSchema.Tool("execute_sql", "Execute SQL query", sqlToolSchema),
                arguments -> {

                    Map<String, Object> args = arguments;
                    String query = (String) args.get("query");

                    if (query == null || query.trim().isEmpty()) {
                        throw new IllegalArgumentException("Query is required");
                    }

                    try (Connection conn = getConnection();
                         Statement stmt = conn.createStatement()) {

                        // 处理不同类型的查询
                        if (query.toUpperCase().startsWith("SHOW TABLES")) {
                            ResultSet rs = stmt.executeQuery(query);
                            List<String> tables = new ArrayList<>();
                            while (rs.next()) {
                                tables.add(rs.getString(1));
                            }
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

                            return new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent(result.toString())),
                                    false
                            );
                        } else {
                            // 执行其他类型的查询
                            int affected = stmt.executeUpdate(query);
                            return new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent("Affected rows: " + affected)),
                                    false
                            );
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException("SQL execution failed: " + e.getMessage());
                    }
                }
        );

        // 添加资源列表处理
        /*syncServer.setResourceLister(() -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                ResultSet rs = stmt.executeQuery("SHOW TABLES");
                List<McpSchema.Resource> resources = new ArrayList<>();

                while (rs.next()) {
                    String tableName = rs.getString(1);
                    resources.add(new McpSchema.Resource(
                            "mysql://" + tableName + "/data",
                            "Table: " + tableName,
                            "text/plain",
                            "Data in table: " + tableName
                    ));
                }

                return resources;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to list resources: " + e.getMessage());
            }
        });*/

        syncServer.addTool(sqlToolRegistration);

        return syncServer;
    }

    // 辅助方法：获取数据库连接
    private Connection getConnection() throws SQLException {
        String host = System.getenv().getOrDefault("MYSQL_HOST", "localhost");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");
        String database = System.getenv("MYSQL_DATABASE");

        if (user == null || password == null || database == null) {
            throw new IllegalStateException("Missing required database configuration");
        }

        return DriverManager.getConnection(
                "jdbc:mysql://" + host + "/" + database,
                user,
                password
        );
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            this.syncServer.closeGracefully();
        }
    }
}