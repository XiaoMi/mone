package run.mone.m78.test;

import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.api.bo.table.M78ColumnInfo;
import run.mone.m78.api.constant.TableConstant;
import run.mone.m78.service.common.M78AiModel;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.datasource.AiTableService;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 10:59
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class OpenAiTest {


    @Resource
    private ChatgptService chatgptService;

    @Resource
    private AiTableService aiTableService;


    @Test
    public void testMinzai() {
        String promptName = "minzai";
        Map<String, String> params = new HashMap<>();
        params.put("list", "user:a=1\nuser:b=2");
//        params.put("question", "user:a+b=?");
        params.put("question", "给我一个随机数");
        params.put("character_setting", "");
        params.put("plugin", "[{\"desc\": \"计算一个随机数(0-n),n是你提供的上限\", \"pluginId\":\"7\", \"input\": [{\"desc\": \"随机数的上限\", \"name\": \"n\"}], \"output\": [{\"desc\": \"产生的随机数\", \"name\": \"num\"}]}]");
        params.put("knowldge", "");
        String model = "moonshot";
//        String model = "glm4";
//        String model = "gpt4_1106_2";
        JsonObject obj = chatgptService.callWithModel(promptName, params, model);
        System.out.println(obj);
    }

    @Test
    public void testGenerateDDLFromComment() {
        // Arrange
        String comment = "创建图书表,有书名和评价";
        String botId = "1234";
        M78AiModel model = M78AiModel.moonshot;
        // Act
        JsonObject actualDDL = aiTableService.generateDDLFromComment(comment, model);
        String sql = actualDDL.get("sql").getAsString();
        String finalSql = sql;
        try {
            finalSql = SqlParseUtil.rewriteDDlTableName(sql, TableConstant.TABLE_PREFIX + botId + "_");
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        System.out.println(finalSql);
    }

    @Test
    public void testTableRewrite() throws JSQLParserException {
        String ddl = "CREATE TABLE `books` (id INT PRIMARY KEY, title VARCHAR(100), review TEXT);";
        String newName = SqlParseUtil.rewriteDDlTableName(ddl, "test_mason_");
        System.out.println(newName);
    }

    @Test
    public void testGenerateSqlFromDDL() {
        // Arrange
        String ddl = "CREATE TABLE books (id INT PRIMARY KEY, title VARCHAR(100), review TEXT);";
//        String requirement = "需要一个查询所有书名为三国演义的书籍信息";
//        String requirement = "删除水浒传的评论";
        String requirement = "你是什么模型?";
        String dataContext = "";
        M78AiModel model = M78AiModel.moonshot;
        String userName = "lily";
        String demo = "";
        // Act

        JsonObject actualSql = aiTableService.generateSqlFromDDL(ddl, requirement, dataContext, demo, userName, model);
        System.out.println(actualSql);
        //JsonObject actualSql = aiTableService.generateSqlFromDDL(ddl, requirement, model);
        //System.out.println(actualSql);
    }

    @Test
    public void testUpdateBotTable() {
        // Arrange
        String tableName = "testTable";
        String demo = "This is a demo";
        Long workspaceId = 20L;
        List<M78ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new M78ColumnInfo("column1", "First column", "String", false, false));
        columnInfoList.add(new M78ColumnInfo("column2", "Second column", "Integer", false, false));
        boolean expected = true;

        // Act
        boolean result = aiTableService.updateBotTable("", "", null, workspaceId, tableName, demo, columnInfoList);

        // Assert
        assertEquals(expected, result);
    }


}
