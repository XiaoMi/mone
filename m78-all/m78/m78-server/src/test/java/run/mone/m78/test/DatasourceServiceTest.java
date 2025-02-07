package run.mone.m78.test;

import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.datasource.DatasourceSqlParam;
import run.mone.m78.api.bo.datasource.SqlQueryRes;
import run.mone.m78.service.service.datasource.DatasourceService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author goodjava@qq.com
 * @date 2024/2/7 16:21
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class DatasourceServiceTest {


    @Resource
    private DatasourceService datasourceService;


    @Test
    public void testQueryTableDataWithPaging() {
        int datasourceId = 33; // 假设的数据源ID
        String tableName = "m78_meta"; // 假设的表名
        String username = "name"; // 假设的用户名
        int low = 0; // 测试的页码
        int upper = 10; // 测试的每页数据量

        // 构建预期的结果，这里需要根据实际情况来模拟预期数据
        List<Map<String, Object>> expectedData = new ArrayList<>();

        // 调用测试方法
        Result<SqlQueryRes> result = datasourceService.queryTableDataWithPaging(datasourceId, tableName, username, low, upper);

        System.out.println(result);
    }

    @Test
    public void testDeleteTable() {
        int datasourceId = 33; // 假设的数据源ID
        String tableName = "user_m78_unknown_testuuid2"; // 假设的表名
        String username = "name"; // 假设有权限的用户名
        Result<Boolean> result = datasourceService.deleteTable(datasourceId, tableName, username);
        System.out.println(result);
    }

    @Test
    public void testQueryTableStructure() {
        int datasourceId = 33; // 假设的数据源ID
        String tableName = "m78_meta"; // 假设的表名
        String username = "name"; // 假设的用户名

        // 调用测试方法
        Result<List<Map<String, String>>> result = datasourceService.queryTableStructure(datasourceId, tableName, username);

        // 验证返回的表结构是否符合预期
        List<Map<String, String>> actualStructure = result.getData();
        assertNotNull(actualStructure);
        assertEquals(11, actualStructure.size());
    }

    @Test
    public void testAlterTableColumns() {
        int datasourceId = 33; // 假设的数据源ID
        String tableName = "m78_test"; // 假设的表名
        String username = "name"; // 假设的用户名
        List<Map<String, String>> columnOperations = new ArrayList<>();

        // 构建模拟的列操作
        Map<String, String> addColumnOperation = new HashMap<>();
        addColumnOperation.put("operationType", "ADD");
        addColumnOperation.put("columnName", "new_column");
        addColumnOperation.put("columnType", "VARCHAR(255)");
        addColumnOperation.put("nullable", "NO");
        addColumnOperation.put("defaultValue", "default_value");
        addColumnOperation.put("comment", "Newly added column");
        columnOperations.add(addColumnOperation);

        Map<String, String> modifyColumnOperation = new HashMap<>();
        modifyColumnOperation.put("operationType", "MODIFY");
        modifyColumnOperation.put("columnName", "new_column1");
        modifyColumnOperation.put("columnType", "INT");
        modifyColumnOperation.put("nullable", "YES");
        modifyColumnOperation.put("defaultValue", null);
        modifyColumnOperation.put("comment", "Modified column type");
        columnOperations.add(modifyColumnOperation);

        Map<String, String> dropColumnOperation = new HashMap<>();
        dropColumnOperation.put("operationType", "DROP");
        dropColumnOperation.put("columnName", "new_column2");
        columnOperations.add(dropColumnOperation);

        // 调用测试方法
        Result<Boolean> result = datasourceService.alterTableColumns(datasourceId, tableName, columnOperations, username);
        System.out.println(result);

    }

    @Test
    public void testExecuteSql() {
        // 构建假设的输入参数
        DatasourceSqlParam datasourceSqlParam = new DatasourceSqlParam();
        datasourceSqlParam.setConnectionId(33); // 假设的连接ID
//        datasourceSqlParam.setComment("计算两个数的和");
        datasourceSqlParam.setComment("给你一组int 数组,你随机返回一半的数据");
        String userName = "name"; // 假设的用户名

        // 构建预期的结果，这里需要根据实际情况来模拟预期数据
        List<Map<String, Object>> expectedData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("column1", "value1");
        row1.put("column2", "value2");
        expectedData.add(row1);

        // 调用测试方法
        Result<SqlQueryRes> result = datasourceService.executeSql(datasourceSqlParam, userName);
        System.out.println(result);

    }


}
