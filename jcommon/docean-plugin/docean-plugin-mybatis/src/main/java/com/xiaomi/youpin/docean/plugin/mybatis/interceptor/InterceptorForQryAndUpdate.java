package com.xiaomi.youpin.docean.plugin.mybatis.interceptor;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryParam;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryResult;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.UpdateParam;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.List;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/25 17:04
 *
 * mybatis 的拦截器
 * 最后实际请求会发给mesh服务器
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class InterceptorForQryAndUpdate implements Interceptor {


    private InterceptorFunction function;

    @Setter
    private DatasourceConfig datasourceConfig;


    public InterceptorForQryAndUpdate(InterceptorFunction function) {
        this.function = function;
    }

    private List<Object> params(BoundSql boundSql, MappedStatement mappedStatement, Object parameterObject) {
        TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        Configuration configuration = mappedStatement.getConfiguration();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<Object> list = Lists.newArrayList();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value = null;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    list.add(value);
                }
            }
        }
        return list;
    }


    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement target = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType t = target.getSqlCommandType();
        SqlSource sqlSource = target.getSqlSource();
        BoundSql bsql = sqlSource.getBoundSql(invocation.getArgs()[1]);
        List<Object> params = params(bsql, target, invocation.getArgs()[1]);
        log.info("type:{} sql:{}  ->  {} {}", t, bsql.getSql(), bsql.getParameterObject(), params);


        if (t.equals(SqlCommandType.INSERT) || t.equals(SqlCommandType.DELETE) || t.equals(SqlCommandType.UPDATE)) {
            int n = function.update(UpdateParam.builder()
                    .sql(bsql.getSql())
                    .dsName(this.datasourceConfig.getName())
                    .params(params.stream().map(it -> {
                        if (null == it) {
                            return null;
                        }
                        return it.toString();
                    }).toArray(String[]::new))
                    .build());
            return n;
        }

        if (t.equals(SqlCommandType.SELECT)) {
            List<ResultMap> maps = target.getResultMaps();
            List<ResultMapping> mapping = maps.get(0).getResultMappings();
            Class<?> type = maps.get(0).getType();
            QueryResult res = function.query(QueryParam
                    .builder()
                    .dsName(this.datasourceConfig.getName())
                    .type(type)
                    .mappings(mapping)
                    .sql(bsql.getSql())
                    .params(params.stream().map(it -> it.toString()).toArray(String[]::new))
                    .build());

            return res.getList();
        }

        return 1;
    }



    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


}
