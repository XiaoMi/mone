package run.mone.m78.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.entity.M78Test;
import run.mone.m78.service.dao.entity.QueryContext;
import run.mone.m78.service.dao.entity.TableInfo;
import run.mone.m78.service.dao.mapper.M78TestMapper;
import run.mone.m78.service.database.DataSourceConfig;
import run.mone.m78.service.database.SqlExecutor;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.service.datasource.DatasourceService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/1/18 15:11
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class DbTest {


    @Resource
    private SqlExecutor sqlExecutor;

    @Resource
    private M78TestMapper m78TestMapper;

    @Resource
    private DataSourceConfig dataSourceConfig;

    @Resource
    private DatasourceService datasourceService;

    @Test
    public void executeAndPrintQueryResults() {
        List<Map<String, Object>> list = sqlExecutor.exec("select * from m78_test where id = ?", new QueryContext(dataSourceConfig.DEFAULT), 1000);
        System.out.println(list);
    }

    @SneakyThrows
    @Test
    public void executeAndCountSqlQuery() {
        String sql = "select * from website";
        String countSql = SqlParseUtil.transformSelectToCount(sql);
        System.out.println(countSql);

        List<Map<String, Object>> countResult = datasourceService.executeSqlQueryUsingConnectionId(25, countSql);
        int total = Integer.valueOf(countResult.get(0).get("COUNT(*)").toString());
        System.out.println(total);
        System.out.println(SqlParseUtil.addLimitToSelectSql(sql, 20, 40));
    }

    @Test
    public void testDefaultExecutor() {
//        Object obj = defaultExecutor.query("select * from m78_meta", rows -> {
//            if (CollectionUtils.isEmpty(rows)) {
//                return new ArrayList<>();
//            }
//            return rows.stream()
//                    .map(HashMap::new)
//                    .collect(Collectors.toList());
//        }, 1);
//        System.out.println(obj);
    }

    @Test
    public void testTableInfo() {
        List<TableInfo> res = sqlExecutor.getTableInfos(1, "aaa");
        res.forEach(System.out::println);
    }


    @Test
    public void testFetchCreateTableStatement() {
        String sql = sqlExecutor.fetchCreateTableStatement(1, "m78_connection_info");
        System.out.println(sql);
    }


    //删除数据
    @Test
    public void testDelete() {
        Db.deleteById("m78_chat_messages", "id", 9);
        System.out.println("delete finish");
    }


    @Test
    public void testUpdate() {
        Row row = new Row();
        row.set("id", 19);
        row.set("message", "2");
        row.setPrimaryKeys(Sets.newHashSet(RowKey.of("id")));
        Db.updateById("m78_chat_messages", row);
    }


    @Test
    public void testT() {
        m78TestMapper.insert(M78Test.builder().status(1).creator("").textAfter("").textBefore("").t(Lists.newArrayList("a", "b")).build());
    }

    @Test
    public void testSql() {
        List<Object> data = Db.selectObjectList("select json_extract(t,concat('$[',json_length(t)-1,']')) from m78_test mt ");
        System.out.println(data);
    }

    @Test
    public void testSql2() {
        List<Object> data = Db.selectObjectList("select t from m78_test mt where json_extract(t,concat('$[',json_length(t)-1,']'))='b' ");
        System.out.println(data);
    }

    @Test
    public void testSqlExe() {
        String sql = "ALTER TABLE nyx_test ADD COLUMN aaa bigint;";
        List<Row> rows = Db.selectListBySql(sql);
        System.out.println(rows);
    }
}
