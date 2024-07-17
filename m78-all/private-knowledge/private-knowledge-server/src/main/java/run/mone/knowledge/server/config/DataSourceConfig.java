package run.mone.knowledge.server.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
    public DataSource getDataSource() {
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
        config.setPoolName("M78KnowledgeDataSource");

        return new HikariDataSource(config);
    }
}
