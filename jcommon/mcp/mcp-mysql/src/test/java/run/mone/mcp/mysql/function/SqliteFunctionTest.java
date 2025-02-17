
package run.mone.mcp.mysql.function;

import org.junit.jupiter.api.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SqliteFunctionTest {

    private SqliteFunction sqliteFunction;
    private static final String TEST_DB_PATH = System.getProperty("user.home") + File.separator + "sqlite.db";

    @BeforeAll
    static void setUpTestDatabase() throws Exception {
        // Set up a test database
        System.setProperty("SQLITE_DB_PATH", TEST_DB_PATH);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + TEST_DB_PATH);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE test_table (id INTEGER PRIMARY KEY, name TEXT)");
            stmt.execute("INSERT INTO test_table (name) VALUES ('Test Name')");
        }
    }

    @AfterAll
    static void tearDownTestDatabase() {
        // Clean up the test database
        new File(TEST_DB_PATH).delete();
    }

    @BeforeEach
    void setUp() {
        sqliteFunction = new SqliteFunction();
    }

    @Test
    void testQueryExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "query");
        args.put("sql", "SELECT * FROM test_table");

        McpSchema.CallToolResult result = sqliteFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testUpdateExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "update");
        args.put("sql", "UPDATE test_table SET name = 'Updated Name' WHERE id = 1");

        McpSchema.CallToolResult result = sqliteFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testDDLExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "ddl");
        args.put("sql", "CREATE TABLE new_table (id INTEGER PRIMARY KEY, value TEXT)");

        McpSchema.CallToolResult result = sqliteFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testInvalidSQLExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "query");
        args.put("sql", "SELECT * FROM non_existent_table");

        assertThrows(RuntimeException.class, () -> sqliteFunction.apply(args));
    }

    @Test
    void testInvalidOperationType() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "invalid_type");
        args.put("sql", "SELECT * FROM test_table");

        assertThrows(IllegalArgumentException.class, () -> sqliteFunction.apply(args));
    }

    @Test
    void testEmptySQLExecution() {
        Map<String, Object> args = new HashMap<>();
        args.put("type", "query");
        args.put("sql", "");

        assertThrows(IllegalArgumentException.class, () -> sqliteFunction.apply(args));
    }
}
