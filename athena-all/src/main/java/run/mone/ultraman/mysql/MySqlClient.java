package run.mone.ultraman.mysql;

import lombok.SneakyThrows;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.sql.Sql;


/**
 * @author goodjava@qq.com
 * @date 2023/12/21 00:14
 */
public class MySqlClient {


    @SneakyThrows
    public static void createTable(String url, String userName, String password, String sqlStr) {
        NutDao dao = new NutDao();
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dao.setDataSource(dataSource);
        Sql sql = Sqls.create(sqlStr);
        dao.execute(sql);
    }

}
