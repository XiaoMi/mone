package run.mone.local.docean.controller.test;

import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import run.mone.local.docean.po.AgentInfoPo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/26/24 5:22 PM
 */
public class DbTest {
    @Test
    public void testSqllite() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch (Exception e) {
            // do nothing
        }
        System.out.println("Open databse successfully");
    }

    @Test
    public void testAgentInfoStore() throws ClassNotFoundException {
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:test.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        Dao dao = new NutDao(dataSource);
        // 创建表
        dao.create(AgentInfoPo.class, false); // false的含义是,如果表已经存在,就不要删除重建了.
        dao.insert(AgentInfoPo.builder()
                        .token("testTokenAAA")
                        .avatarPath("testPath")
                .build());
        AgentInfoPo info = dao.fetch(AgentInfoPo.class, 1);
        List<AgentInfoPo> infos = dao.query(AgentInfoPo.class, null);
        System.out.println("First record:" + info);
        System.out.println("All: " + infos);
    }

    @Test
    public void testSqlEscape() {
        String originalString = "这是一个包含@的字符串，例如：email@example.com";
        String replacedString = originalString.replaceAll("@", "@@");

        System.out.println("原始字符串: " + originalString);
        System.out.println("替换后的字符串: " + replacedString);
    }
}
