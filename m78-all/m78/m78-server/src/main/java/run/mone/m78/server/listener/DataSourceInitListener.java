package run.mone.m78.server.listener;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 数据统计新增数据源，写入不同的数据库，目前只在线上是不同的数据源
 */
@Configuration
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${db_url_code_statistics}")
    private String dbUrlCodeStatistics;

    @Value("${db_user_name_code_statistics}")
    private String dbUserNameCodeStatistics;

    @Value("${db_pwd_code_statistics}")
    private String dbPwdCodeStatistics;

    @Value("${db_maximumPoolSize:10}")
    private int maximumPoolSize;

    @Value("${db_minimumIdle:5}")
    private int minimumIdle;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrlCodeStatistics);
        config.setUsername(dbUserNameCodeStatistics);
        config.setPassword(dbPwdCodeStatistics);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);

        // 连接超时时间(毫秒)
        config.setConnectionTimeout(30 * 1000);
        // 连接空闲超时时间(毫秒)
        config.setIdleTimeout(600 * 1000);
        // 每次执行SQL前是否自动检查连接有效性
        config.setConnectionTestQuery("SELECT 1");
        // 连接池名称,方便监控和日志记录
        config.setPoolName("M78CodeStatisticsDataSource");

        HikariDataSource hikariDataSource = new HikariDataSource(config);

        FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig()
                .getDataSource();

        dataSource.addDataSource("codeStatistics", hikariDataSource);
    }

}
