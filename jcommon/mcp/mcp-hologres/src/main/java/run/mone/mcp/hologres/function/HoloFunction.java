package run.mone.mcp.hologres.function;

import com.blinkfox.zealot.bean.SqlInfo;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class HoloFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "hologres_executor";

    private String desc = "Execute Hologres query operations";

    private String sqlToolSchema = """
            {
                "type": "object",
                "properties": {
                    "tableName": {
                        "type": "string",
                        "enum": ["dim_org"],
                        "description": "the name of execute table"
                     },
                     "startTime": {
                         "type": "string",
                         "description": "the start time of where condition"
                    },
                     "endTime": {
                         "type": "string",
                         "description": "the start time of where condition"
                    },
                     "count": {
                         "type": "Integer",
                         "description": "the number of query row"
                    }
                },
                "required": ["tableName", "startTime", "endTime"]
            }
            """;
    private DataSource dataSource;


    public HoloFunction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String tableName = (String) args.get("tableName");
        String startTime = (String) args.get("startTime");
        Integer count = (Integer) args.get("count");
        String endTime = (String) args.get("endTime");
        if (tableName == null || tableName.trim().isEmpty()) {
            log.error("没有指明表明");
            throw new IllegalArgumentException("tableName is required");
        }
        log.info("tableName: {}, startTime: {}, endTime: {}", tableName, startTime, endTime);

        // 获取连接
        Connection conn = dataSource.getConnection();

        try {
            SqlInfo sql = SQLGenerator.generateSQLQuery(tableName, startTime, endTime, count);
            log.info("sql : {}", sql);
            return executeQuery(conn, sql);
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    private McpSchema.CallToolResult executeQuery(Connection conn, SqlInfo sqlInfo) throws SQLException {

        // 创建 PreparedStatement 执行 SQL 语句
        try (PreparedStatement ps = conn.prepareStatement(sqlInfo.getSql())) {
            if (sqlInfo.getParams() != null && sqlInfo.getParams().size() > 0) {
                for (int i = 0; i < sqlInfo.getParams().size(); i++) {
                    ps.setObject(i + 1, sqlInfo.getParams().get(i));
                }

            }

            ResultSet rs = ps.executeQuery();

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
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.toString())),
                    false
            );
        }

    }

}