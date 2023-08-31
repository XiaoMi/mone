package run.mone.mybatis.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * SQL加注释appName
 * @author zhangping17
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class AppNameInterceptor implements Interceptor {

    private Properties properties;

    private final String APP_NAEM = "appName";
    private Field sqlFiled = null;

    AppNameInterceptor() {
        try {
            //减少每次反射性能损耗
            sqlFiled = BoundSql.class.getDeclaredField("sql");
            sqlFiled.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler target = (StatementHandler) invocation.getTarget();
        BoundSql sql = target.getBoundSql();
        if (properties != null && properties.getProperty(APP_NAEM) != null && properties.getProperty(APP_NAEM).trim().length() >0) {
            String newSql = sql.getSql().trim() + String.format("/*%s*/", properties.getProperty(APP_NAEM));
            sqlFiled.set(sql, newSql);
        }
        Object res = invocation.proceed();
        return res;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
