package run.mone.doris;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2024/1/8 15:09
 */
@Slf4j
public class DorisServiceTest {

    private DorisService dorisService;

    private String tableName = "doris_test_db";

    private List<String> columnList = Lists.newArrayList("id", "name", "message");

    private Gson gson = new Gson();

    @Before
    public void init() {
        String driver = "org.mariadb.jdbc.Driver";
        String url = "jdbc:mariadb://127.0.0.1:9030/test?rewriteBatchedStatements=true";
        String user = "root";
        String password = "";
        dorisService = new DorisService(driver, url, user, password);
    }

    @Test
    public void testCreateTable() {
        String sql = String.format("CREATE TABLE %s (`id` BIGINT,`name` VARCHAR(1024),`message` STRING) DISTRIBUTED BY HASH(`id`) \n" + "BUCKETS 1 \n" + "PROPERTIES (\"replication_num\" = \"1\");", "doris_test_db");
        dorisService.createTable(sql);
    }

    @Test
    public void testColumnList() {
        String tableName = "hera_log_doris_table_2_11";
        List<String> columnList = dorisService.getColumnList(tableName);
        log.info("columnList:{}", gson.toJson(columnList));
    }

    @Test
    public void testInsertData() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", i);
            data.put("name", "张三");
            data.put("message", "sfdsfdsfzhangsan发生士大夫但是发删掉发山东发sgdgdgdg");
            dorisService.send(tableName, columnList, data);
        }
        System.in.read();
    }

    @Test
    public void testQuery() throws SQLException {
        String sql = "select * from " + tableName + " where message like ";
        String searchTerm = "'%山东%'";
        List<Map<String, Object>> result = dorisService.query(sql + searchTerm);
        log.info("result:{}", result);
    }
}
