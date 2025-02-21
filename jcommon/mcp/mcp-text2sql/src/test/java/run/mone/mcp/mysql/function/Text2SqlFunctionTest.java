
package run.mone.mcp.mysql.function;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.text2sql.function.Text2SqlFunction;

class Text2SqlFunctionTest {

    private Text2SqlFunction text2SqlFunction;

    private static final String TEST_DB_PATH = "D:/product-info.db";

    @BeforeAll
    static void setUpTestDatabase() throws Exception {
        // Set up a test database
        System.setProperty("SQLITE_DB_PATH", TEST_DB_PATH);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + TEST_DB_PATH);
            Statement stmt = conn.createStatement()) {
            stmt.execute("select * from test_table");
        }
    }

    @BeforeEach
    void setUp() {
        text2SqlFunction = new Text2SqlFunction();
    }

    @Test
    void testQueryExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "search_table_schema_info");
        args.put("database_url",
            "jdbc:mysql://staging.bms.info.tidb.srv:4100/m78?characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&allowMultiQueries=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
        args.put("tables", List.of("m78_code_generation_info"));

//        setEnv("m78_wr", "BKJ8WwMrj2VHHVdIL8l8DCpK");

        McpSchema.CallToolResult result = text2SqlFunction.apply(args);

        assertFalse(result.isError());
    }

    private void setEnv(String user, String password) {
        try {
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironment = processEnvironment.getDeclaredField("theEnvironment");
            theEnvironment.setAccessible(true);
            Map<String, String> env = (Map<String, String>)theEnvironment.get(null);
            env.put("DB_USER", user);
            env.put("DB_PW", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
