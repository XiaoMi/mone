package run.mone.m78.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import run.mone.m78.service.common.*;
import run.mone.m78.service.database.MySQLTablePersist;
import run.mone.m78.service.database.SqlParseUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author goodjava@qq.com
 * @date 2024/1/15 15:52
 */
public class CommonUtilsTest {


    //模拟错误,用来测试Ahtena 快速修复
    @Test
    public void testList() {
        //获取list 最后一个元素
        int a = 1/0;
        System.out.println(a);
        List<String>list = Lists.newArrayList("1");
        System.out.println(list.get(2));
    }

    @Test
    public void testHttpGet() {
        String res = HttpClient.get("https://test.com");
        System.out.println(res);
    }


    @Test
    public void testParseHtml() {
        String html = HttpClient.get("https://test.com");
        Document doc = Jsoup.parse(html);
        // 选择所有的div元素
        Elements divs = doc.selectXpath("//*[@id=\"mainScreen\"]/div[2]/div/div[1]/div[2]/div[1]/div/div[1]/div/div[2]/div[1]/div[1]/div");
        List<String> list = new ArrayList<>();
        // 遍历所有的div元素并打印其内容
        for (Element div : divs) {
            System.out.println(div.text());
            list.add(div.text());
        }
        list = list.stream().sorted((a, b) -> b.length() - a.length()).collect(Collectors.toList());
        System.out.println(list.get(0));
    }


    @Test
    public void test2() {
        System.out.println(HttpParse.parseOsChina("https://test.com"));
    }


    @Test
    public void test3() {
        System.out.println(HttpParse.parseFreeBuf("https://test.com"));
    }

    @Test
    public void test5() {
        System.out.println(HttpParse.parseWithCssQuery("https://test.com", "article"));
    }


    @Test
    public void insertDataIntoTable() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id1", "1");
        m.put("id2", null);
        m.put("id3", "3");
        new MySQLTablePersist().insert2Table("aaa", m);
    }


    @Test
    public void extractAndPrintArticleContent() {
        // 示例HTML字符串，包含<article>标签
        String html = "<html><head><title>Sample Article</title></head>"
                + "<body>"
                + "<article>"
                + "<h1>Article Title</h1>"
                + "<p>This is a paragraph in the article content.</p>"
                + "</article>"
                + "</body></html>";

        // 使用Jsoup解析HTML字符串
        Document doc = Jsoup.parse(html);

        // 选择所有<article>标签
        Elements articles = doc.select("article");

        // 遍历所有<article>元素并打印其内容
        for (Element article : articles) {
            System.out.println("Article title: " + article.select("h1").text());
            System.out.println("Article content: " + article.select("p").text());
        }
    }

    @Test
    public void fetchGitHubReadmeContent() {
        System.setProperty("webdriver.chrome.driver", "/Users/zhangzhiyong/program/chrome_driver/chromedriver-mac-arm64/chromedriver");
        // 初始化 ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 无头模式，不显示浏览器界面
        WebDriver driver = new ChromeDriver(options);
        try {
            // 访问页面
            driver.get("https://test.com");
            Thread.sleep(2000);
            String html = driver.getPageSource();
//            System.out.println(html);
            Document doc = Jsoup.parse(html);
            // 选择所有<article>标签
            Elements articles = doc.select("article");
            System.out.println(articles.text());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


    @SneakyThrows
    @Test
    public void testUpdateSql() {
        String sqlStr = "select * from dual where sex='a' and name='b' and age=18";

        Select selectStatement = (Select) CCJSqlParserUtil.parse(sqlStr);
        PlainSelect selectBody = (PlainSelect) selectStatement.getSelectBody();
        Expression where = selectBody.getWhere();

        // Create a map with the new values
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("sex", "b");
        newValues.put("name", "zzy");
        newValues.put("age", 23L); // Use Long for numeric values

        // Use ExpressionDeParser to parse the WHERE clause and update column values
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(EqualsTo equalsTo) {
                // Get the left expression as a column
                String columnName = equalsTo.getLeftExpression().toString();
                // Check if the column is in the map of new values
                if (newValues.containsKey(columnName)) {
                    // Update the right expression with the new value
                    Object newValue = newValues.get(columnName);
                    if (newValue instanceof String) {
                        equalsTo.setRightExpression(new StringValue("'" + newValue + "'"));
                    } else if (newValue instanceof Long) {
                        equalsTo.setRightExpression(new LongValue(newValue.toString()));
                    }
                }
                super.visit(equalsTo);
            }
        };

        // Use SelectDeParser to rebuild the select statement with the updated WHERE clause
        SelectDeParser selectDeParser = new SelectDeParser(expressionDeParser, new StringBuilder());
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(selectDeParser.getBuffer());
        where.accept(expressionDeParser);

        // Get the updated SQL query
        String updatedSqlStr = selectDeParser.getBuffer().toString();
        System.out.println(updatedSqlStr);

        System.out.println(selectBody);


    }


    @SneakyThrows
    @Test
    public void testParseAndPrintTableSchema() {
        String sql = "CREATE TABLE `m78_meta` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  `uuid` varchar(255) NOT NULL DEFAULT '' COMMENT 'UUID',\n" +
                "  `table_name` varchar(1024) NOT NULL DEFAULT '',\n" +
                "  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  `status` int(11) NOT NULL DEFAULT '0' COMMENT '预留, 行状态标记',\n" +
                "  `user_name` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',\n" +
                "  `favorite` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为收藏, 0: 非收藏, 1：收藏',\n" +
                "  `type` int(11) NOT NULL DEFAULT '0' COMMENT '类型标记, 0：excel文档, 1：翻译文本的输入, 2: 翻译文本的输出',\n" +
                "  `original_file_name` varchar(1024) NOT NULL DEFAULT '' COMMENT '上传文件原始名',\n" +
                "  `custom_knowledge` text DEFAULT NULL COMMENT 'meta信息的文本',\n" +
                "  PRIMARY KEY (`id`) /*T![clustered_index] CLUSTERED */,\n" +
                "  UNIQUE KEY `unq_uuid` (`uuid`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001";

        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof CreateTable) {
                CreateTable createTable = (CreateTable) statement;
                List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
                for (ColumnDefinition columnDef : columnDefinitions) {
                    String columnName = columnDef.getColumnName();
                    String columnType = columnDef.getColDataType().toString();

                    String comment = "";
                    List<String> columnSpecStrings = columnDef.getColumnSpecs();
                    if (columnSpecStrings != null) {
                        int commentIndex = columnSpecStrings.indexOf("COMMENT");
                        if (commentIndex != -1 && commentIndex < columnSpecStrings.size() - 1) {
                            comment = columnSpecStrings.get(commentIndex + 1);
                        }
                    }

                    System.out.println("Column Name: " + columnName + " - Column Type: " + columnType + " COMMENT:" + comment);
                }
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    @Test
    public void testSqlCreateTable() {
        String sql = "create table test(id int primary key auto_increment,name varchar(50))";
        CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sql);
        System.out.println(createTable);

        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        for (Iterator<ColumnDefinition> it = columnDefinitions.iterator(); it.hasNext(); ) {
            ColumnDefinition columnDef = it.next();
            if (columnDef.getColumnName().equalsIgnoreCase("id")) {
                // Remove auto_increment and primary key attributes
//                columnDef.getColumnSpecStrings().clear();
                columnDef.getColumnSpecs().clear();
            }
        }
        // Add a new column 'm78_id' as primary key with auto_increment
        ColumnDefinition newColumn = new ColumnDefinition();
        newColumn.setColumnName("m78_id");
        ColDataType newColumnType = new ColDataType();
        newColumnType.setDataType("int");
        newColumn.setColDataType(newColumnType);
        newColumn.setColumnSpecs(Lists.newArrayList());
        newColumn.getColumnSpecs().add("auto_increment");
        newColumn.getColumnSpecs().add("primary key");
        createTable.getColumnDefinitions().add(0, newColumn); // Add as the first column

        System.out.println(createTable);

    }


    @SneakyThrows
    @Test
    public void testSqlParse() {
        String sqlStr = "select * from dual where sex='a' and name='b' and age=18";

        Select s = (Select) CCJSqlParserUtil.parse(sqlStr);

        PlainSelect select = (PlainSelect) s.getSelectBody();

        SelectItem selectItem =
                select.getSelectItems().get(0);
        Table table = (Table) select.getFromItem();
        assertEquals("dual", table.getName());

        Expression where = select.getWhere();

        // List to hold the column names
        List<Pair<String, String>> columnNames = new ArrayList<>();

        // Use ExpressionDeParser to parse the WHERE clause and extract column names
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(Column column) {

                super.visit(column);
            }

            @Override
            public void visit(EqualsTo equalsTo) {
                Expression rightExp = equalsTo.getRightExpression();
                String value = "";
                if (rightExp instanceof StringValue) {
                    StringValue sv = (StringValue) rightExp;
                    value = sv.getValue();
                } else if (rightExp instanceof LongValue) {
                    LongValue lv = (LongValue) rightExp;
                    value = lv.getStringValue();
                } else {
                    value = rightExp.toString();
                }
                columnNames.add(Pair.of(equalsTo.getLeftExpression().toString(), value));
                super.visit(equalsTo);
            }
        };
        where.accept(expressionDeParser);
        System.out.println(columnNames);
    }


    @Test
    public void testExcelUtil() {
        //System.out.println(CsvExcelUtils.convertExcelToCsvMap("/Users/wmin/Downloads/wzzj.xlsx").get("Sheet1"));
    }




    @Test
    public void testIsSql() {
        System.out.println(SqlParseUtil.isSqlStatement("select a from b"));
    }


    @SneakyThrows
    @Test
    public void extractColumnNamesFromSqlQuery() {
        List<Map<String, Object>> map = SqlParseUtil.getColumnNames("select a from b where id=1 and name = ?");
        System.out.println(map);
    }

    @Test
    public void testTransformSelectToCount() throws JSQLParserException {
        String originalSql = "SELECT id, name, age FROM users WHERE age > 30";
        String expectedSql = "SELECT COUNT(*) FROM users WHERE age > 30";
        String actualSql = SqlParseUtil.transformSelectToCount(originalSql);
        assertEquals(expectedSql, actualSql);
    }


    @Test
    public void testAddLimitToSelectSql() throws JSQLParserException {
        String originalSql = "SELECT id, name, age FROM users WHERE age > 30";
        int lowerBound = 0;
        int upperBound = 30;
        String expectedSql = "SELECT id, name, age FROM users WHERE age > 30 LIMIT 0, 30";
        String actualSql = SqlParseUtil.addLimitToSelectSql(originalSql, lowerBound, upperBound);
        assertEquals(expectedSql, actualSql);
    }


    @Test
    public void testParseTableNameAndColumns() throws JSQLParserException {
        String sql = "SELECT u.name, u.email FROM users u WHERE u.id = 1";
        Pair<String, List<String>> result = SqlParseUtil.parseTableNameAndColumns(sql);
        String expectedTableName = "users";
        List<String> expectedColumnNames = Arrays.asList("name", "email");

        assertEquals(expectedTableName, result.getLeft());
        assertTrue(result.getRight().containsAll(expectedColumnNames) && expectedColumnNames.containsAll(result.getRight()));
    }

    @Test
    public void testIsJoinQuery() throws JSQLParserException {
        // Test case with JOIN query
        String joinQuery = "SELECT * FROM table1 JOIN table2 ON table1.id = table2.id";
        assertTrue(SqlParseUtil.isJoinQuery(joinQuery));

        // Test case without JOIN query
        String simpleQuery = "SELECT * FROM table1 WHERE table1.name = 'test'";
        assertFalse(SqlParseUtil.isJoinQuery(simpleQuery));
    }

    @Test
    public void testGetSqlOperationTypeSelect() throws JSQLParserException {
        String sql = "SELECT * FROM users";
        SqlParseUtil.SqlOperationType operationType = SqlParseUtil.getSqlOperationType(sql);
        assertEquals(SqlParseUtil.SqlOperationType.SELECT, operationType);
    }

    @Test
    public void testGetSqlOperationTypeInsert() throws JSQLParserException {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'John Doe')";
        SqlParseUtil.SqlOperationType operationType = SqlParseUtil.getSqlOperationType(sql);
        assertEquals(SqlParseUtil.SqlOperationType.INSERT, operationType);
    }

    @Test
    public void testGetSqlOperationTypeUpdate() throws JSQLParserException {
        String sql = "UPDATE users SET name = 'Jane Doe' WHERE id = 1";
        SqlParseUtil.SqlOperationType operationType = SqlParseUtil.getSqlOperationType(sql);
        assertEquals(SqlParseUtil.SqlOperationType.UPDATE, operationType);
    }

    @Test
    public void testGetSqlOperationTypeDelete() throws JSQLParserException {
        String sql = "DELETE FROM users WHERE id = 1";
        SqlParseUtil.SqlOperationType operationType = SqlParseUtil.getSqlOperationType(sql);
        assertEquals(SqlParseUtil.SqlOperationType.DELETE, operationType);
    }

    @Test
    public void testGetSqlOperationTypeUnknown() throws JSQLParserException {
        String sql = "CREATE TABLE users (id INT, name VARCHAR(100))";
        SqlParseUtil.SqlOperationType operationType = SqlParseUtil.getSqlOperationType(sql);
        assertEquals(SqlParseUtil.SqlOperationType.UNKNOWN, operationType);
    }

    @Test
    public void testSum() {
        System.out.println(Integer.valueOf("33"));

        JsonElement je =GsonUtils.gson.fromJson("[1,'b']", JsonElement.class);
        System.out.println(je.getAsJsonArray().get(0).isJsonPrimitive());
    }

    @Test
    public void testStrip() {
        String s1 = "'hello'";
        String s2 = "'hello";
        String s3 = "hello'";
        String s4 = "hello";
        System.out.println(M78StringUtils.stripStr(s1, "'"));
        System.out.println(M78StringUtils.stripStr(s2, "'"));
        System.out.println(M78StringUtils.stripStr(s3, "'"));
        System.out.println(M78StringUtils.stripStr(s4, "'"));
    }


}
