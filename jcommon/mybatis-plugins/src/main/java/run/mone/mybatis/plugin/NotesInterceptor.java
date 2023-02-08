package run.mone.mybatis.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

/**
 * SQL加注释appName，类，方法
 * @author zhangping17
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class NotesInterceptor implements Interceptor {

    private Properties properties;

    private final static String APP_NAEM = "appName";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        //获取MappedStatement对象
        MappedStatement ms = (MappedStatement) args[0];
        //获取传入sql语句的参数对象
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        //获取到拥有占位符的sql语句
        String sql = boundSql.getSql();
        if (properties != null && properties.getProperty(APP_NAEM) != null && properties.getProperty(APP_NAEM).trim().length() >0) {
            sql = sql.trim() + String.format("/*%s,%s*/", properties.getProperty(APP_NAEM), ms.getId());
            BoundSql bs = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), parameterObject);
            MappedStatement newMs = copyMappedStatement(ms, new BoundSqlSqlSource(bs));
            invocation.getArgs()[0] = newMs;
        }
        Object res = invocation.proceed();
        return res;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /***
     * 复制一个新的MappedStatement
     * @param ms
     * @param newSqlSource
     * @return
     */
    private MappedStatement copyMappedStatement (MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",",ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /***
     * MappedStatement构造器接受的是SqlSource
     * 实现SqlSource接口，将BoundSql封装进去
     */
    public static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}
