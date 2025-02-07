package run.mone.m78.service.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.m78.api.constant.TableConstant;
import run.mone.m78.service.dao.entity.ConnectionInfo;
import run.mone.m78.service.exceptions.InternalException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.net.URI;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/9/24 5:13 PM
 */
@Configuration
public class DataSourceConfig {

    @Value("${db_pwd}")
    private String db_pwd;

    @Value("${db_url}")
    private String db_url;

    @Value("${db_user_name}")
    private String db_user_name;

    @Value("${db_maximumPoolSize:10}")
    private int maximumPoolSize;

    @Value("${db_minimumIdle:5}")
    private int minimumIdle;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(db_url);
        config.setUsername(db_user_name);
        config.setPassword(db_pwd);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);

        // 连接超时时间(毫秒)
        config.setConnectionTimeout(30 * 1000);
        // 连接空闲超时时间(毫秒)
        config.setIdleTimeout(600 * 1000);
        // 每次执行SQL前是否自动检查连接有效性
        config.setConnectionTestQuery("SELECT 1");
        // 连接池名称,方便监控和日志记录
        config.setPoolName("M78DataSource");

        return new HikariDataSource(config);
    }

    public ConnectionInfo DEFAULT; // HINT: 保险起见在contextRefresh后才可获取

    @PostConstruct
    public void init() {
        try {
            URI uri = new URI(db_url);
            String host = uri.getHost();
            String userInfo = uri.getUserInfo();
            DEFAULT = ConnectionInfo.builder()
                    .host(uri.getHost())
                    .jdbcUrl(db_url)
                    .port(String.valueOf(uri.getPort()))
                    .type(1)
                    .database(TableConstant.META_SCHEMA)
                    .user(db_user_name)
                    .pwd(db_pwd)
                    .build();
        } catch (Exception e) {
            throw new InternalException("获取M78自身库配置信息错误,请检查数据库配置!");
        }
    }
}
