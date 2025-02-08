package run.mone.m78.test;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.mapper.M78TestMapper;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.bo.database.TableBO;
import run.mone.m78.service.common.HttpClient;
import run.mone.m78.service.database.TablePersist;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.PromptConstant.PROMPT_HTTP_PARSE;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 9:37 AM
 * 本地测试需要 docker run -itd --name mysql-test -p 3306:3306 -e MYSQL_ROOT_PASSWORD=legalhi597_789 -e TZ=Asia/Shanghai mysql:8.0.18
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class TestDatabase {


    @Resource
    private TablePersist tablePersist;

    @Resource
    private ChatgptService chatgptService;


    @Resource
    private M78TestMapper testMapper;


    @Test
    public void testSelect() {
        System.out.println(testMapper.selectAll());
    }



    @Test
    public void testCreateTable() {
        String sql = "create table if not exists mason.user_m78_test\n" +
                "(\n" +
                "    `id`                    bigint unsigned auto_increment primary key,\n" +
                "    `name`                  varchar(255)     default ''                    not null comment '配置名',\n" +
                "    `status`                bigint           default 0                     not null comment '行状态(0:有效 :删除)',\n" +
                "    `create_time`           datetime         default CURRENT_TIMESTAMP     not null comment '创建时间',\n" +
                "    `modify_time`           timestamp        default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '更新时间',\n" +
                "    unique key unq_test (name, status)\n" +
                ") charset = utf8mb4;";
        tablePersist.createTableBySql(sql, false);
    }

    @Test
    public void testInsertTable() {
        String sql = "insert into user_m78_test(name, status) values ('测试', 1);";
        tablePersist.insert2TableByInsertSql(sql);
    }

    @Test
    public void testQuery() {
        String sql = "select * from user_m78_test";
        TableBO query = tablePersist.query(sql);
        System.out.println(query.getData());
    }

    @Test
    public void testGetCreateTableStatement() {
        System.out.println(tablePersist.getCreateTableStatement("users"));
    }

    @Test
    public void testCheckTable() {
        System.out.println(tablePersist.checkExistTable("table_name"));
    }

    @Test
    public void testSplitCSV() {
        String csvline = "a,b,,c";
        String[] split = csvline.split(",");
        System.out.println(ArrayUtils.toString(split));
        System.out.println(split.length);
    }

    @Test
    public void testTime() {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }


}
