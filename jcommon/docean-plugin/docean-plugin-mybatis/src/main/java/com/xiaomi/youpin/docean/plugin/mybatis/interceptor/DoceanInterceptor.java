/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.mybatis.interceptor;

import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 13:27
 * <p>
 * 这里的sql未来会发往mesh服务器,哪里向mysql发送
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class DoceanInterceptor implements Interceptor {

    @Setter
    private String appName = "";

    public DoceanInterceptor(String appName) {
        this.appName = appName;
    }

    public DoceanInterceptor() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler target = (StatementHandler) invocation.getTarget();
        BoundSql sql = target.getBoundSql();
        if (StringUtils.isNotEmpty(this.appName)) {
            String newSql = sql.getSql().trim() + String.format("/*%s*/", appName);
            Field field = sql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(sql, newSql);
            log.debug("sql:{}  ->  {}", sql.getSql(), sql.getParameterObject());
        }
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
