package run.mone.m78.server.config;

import com.mybatisflex.core.audit.AuditManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 3:40 PM
 */
@Configuration
@Slf4j
public class MybatisFlexConfiguration {

    public MybatisFlexConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                log.info("sql audit :{}, cost: {}ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );
    }
}
