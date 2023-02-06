package comxiaomi.data.push.test;

import com.google.gson.Gson;
import com.xiaomi.data.push.antlr.sql.PrestoSqlParse;
import com.xiaomi.data.push.antlr.sql.exceptions.SqlParseException;
import com.xiaomi.data.push.antlr.sql.model.SqlElement;
import org.junit.Test;

/**
 * @author shanwenbang@xiaomi.com
 * @date 2021/4/16
 */
public class  SqlTest {

    @Test
    public void testSingleSql() throws SqlParseException {
        PrestoSqlParse prestoSqlParse = new PrestoSqlParse();

        SqlElement sqlElement = prestoSqlParse.parse("select a,b,c from table0");
        System.out.println("result: " + new Gson().toJson(sqlElement));
    }

    @Test
    public void testSingleSql1() throws SqlParseException {
        PrestoSqlParse prestoSqlParse = new PrestoSqlParse();

        SqlElement sqlElement = prestoSqlParse.parse("select * from table0");
        System.out.println("result: " + new Gson().toJson(sqlElement));
    }

    @Test
    public void testJoin() throws SqlParseException {
        PrestoSqlParse prestoSqlParse = new PrestoSqlParse();

        SqlElement sqlElement2 = prestoSqlParse.parse("select t0.id, t1.name, t1.desc " +
                "from table0 t0, table1 t1 " +
                "where t0.id = t1.id");
        System.out.println("result: " + new Gson().toJson(sqlElement2));
    }

    @Test
    public void testJoin2() throws SqlParseException {
        PrestoSqlParse prestoSqlParse = new PrestoSqlParse();

        SqlElement sqlElement2 = prestoSqlParse.parse("select t0.id, t0.age, t1.name, t1.desc " +
                "from table0 t0 join table1 t1 " +
                "on t0.id = t1.id");
        System.out.println("result: " + new Gson().toJson(sqlElement2));
    }

    @Test
    public void testCount() throws SqlParseException {
        PrestoSqlParse prestoSqlParse = new PrestoSqlParse();

//        SqlElement sqlElement = prestoSqlParse.parse("select count(*) from table0");
//        System.out.println("result1: " + new Gson().toJson(sqlElement));

//        SqlElement sqlElement2 = prestoSqlParse.parse("select count(1) from table0");
//        System.out.println("result2: " + new Gson().toJson(sqlElement2));
//
        SqlElement sqlElement3 = prestoSqlParse.parse("select sum(id) from table0");
        System.out.println("result3: " + new Gson().toJson(sqlElement3));
    }
}
