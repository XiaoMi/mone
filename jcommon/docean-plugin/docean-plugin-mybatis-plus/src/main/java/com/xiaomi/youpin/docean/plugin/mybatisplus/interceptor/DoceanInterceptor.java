package com.xiaomi.youpin.docean.plugin.mybatisplus.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 13:27
 *
 * 这里的sql未来会发往mesh服务器,哪里向mysql发送
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class DoceanInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler target = (StatementHandler) invocation.getTarget();
        BoundSql sql = target.getBoundSql();
        log.info("sql:{}  ->  {}",  sql.getSql(), sql.getParameterObject());
        Object res = invocation.proceed();
        return res;
    }

    @Override
    public Object plugin(Object target) {
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }

    @Override
    public void setProperties(Properties properties) {
        log.info("plugin setProperties:{}", properties);
    }
}
