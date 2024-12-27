package run.mone.local.docean.bootstrap;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.DaoUp;
import run.mone.local.docean.po.AgentInfoPo;
import run.mone.local.docean.po.Message;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/27/24 17:00
 */
@Slf4j
@Service
public class DbInit {

    @Resource
    private NutDao dao;

    @Value("${db_uname_cus}")
    private String userUname;

    @Value("${db_pwd_cus}")
    private String userPwd;

    @Value("${db_url_cus}")
    private String userUrl;

    @PostConstruct
    public void init() {
        log.info("init local data source...");
        dao.create(AgentInfoPo.class, false); // false的含义是,如果表已经存在,就不要删除重建了.
        dao.create(Message.class, false);
        try {
            Properties props = new Properties();
            props.put("jdbcUrl", userUrl);
            props.put("password", userPwd);
            props.put("username", userUname);
            DaoUp.me().init(props);
        } catch (Exception e) {
            log.info("initializing user table dao error, nested exception is:", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("db resource destroying...");
        DaoUp.me().close();
    }

}
