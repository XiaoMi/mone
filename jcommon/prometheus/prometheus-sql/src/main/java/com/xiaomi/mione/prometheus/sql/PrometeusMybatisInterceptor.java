package com.xiaomi.mione.prometheus.sql;

import com.xiaomi.youpin.prometheus.client.Metrics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),

        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})

})
@Slf4j
public class PrometeusMybatisInterceptor implements Interceptor {

    //超时时间
    public static final int TIMEOUT_OPERATION = 300;

    public static final String SQL_TOTAL_COUNT = "sqlTotalCount";

    public static final String SQL_TIME_OUT_COUNT = "sqlTimeOutCount";

    public static final String SQL_SUCCESS_COUNT = "sqlSuccessCount";

    public static final String SQL_ERROR_COUNT = "sqlErrorCount";

    public static final String SQL_TOTAL_TIMER = "sqlTotalTimer";

    @Getter
    private Properties properties;

    private String datasourceUrl;

    public PrometeusMybatisInterceptor(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //begin cat transaction
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String methodName = this.getMethodName(mappedStatement);
        String sqlMethod = mappedStatement.getSqlCommandType().name().toLowerCase();
        String sql = getSql(invocation, mappedStatement);
        //记录访问次数
        recordCounter(SQL_TOTAL_COUNT,new String[]{"methodName","dataSource","sqlMethod","sql"},methodName,datasourceUrl,sqlMethod,sql);
        //记录sqlMethod
        log.info("SQL.Method:{} {} {}", sqlMethod, "SUCCESS", sql);
        long begin = System.currentTimeMillis();
        try {
            Object returnValue = invocation.proceed();
            if (System.currentTimeMillis() - begin > TIMEOUT_OPERATION) {
                //记录慢sql
                recordCounter(SQL_TIME_OUT_COUNT,new String[]{"methodName","dataSource","sqlMethod","sql"},methodName,datasourceUrl,sqlMethod,sql);
            }
            //记录成功次数
            log.debug("SUCCESS");
            recordCounter(SQL_SUCCESS_COUNT,new String[]{"methodName","dataSource","sqlMethod","sql"},methodName,datasourceUrl,sqlMethod,sql);
            return returnValue;
        } catch (Throwable e) {
            log.error(e.getMessage());
            //记录失败次数
            recordCounter(SQL_ERROR_COUNT,new String[]{"methodName","dataSource","sqlMethod","sql"},methodName,datasourceUrl,sqlMethod,sql);
            throw e;
        } finally {
            //记录耗时
            log.debug("sql 总耗时为：{}",System.currentTimeMillis() - begin);
            recordTimer(SQL_TOTAL_TIMER,new String[]{"methodName","dataSource","sqlMethod","sql"},System.currentTimeMillis() - begin,methodName,datasourceUrl,sqlMethod,sql);
        }
    }

    private String getMethodName(MappedStatement mappedStatement) {
        String[] strArr = mappedStatement.getId().split("\\.");
        String methodName = strArr[strArr.length - 2] + "." + strArr[strArr.length - 1];
        return methodName;
    }

    private String getSql(Invocation invocation, MappedStatement mappedStatement) {
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        return showSql(configuration, boundSql);
    }

    private static String getParameterValue(Object obj) {
        StringBuilder retStringBuilder = new StringBuilder();
        if (obj instanceof String) {
            retStringBuilder.append("'").append(obj).append("'");
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            retStringBuilder.append("'").append(formatter.format(new Date())).append("'");
        } else {
            retStringBuilder.append("'").append(obj == null ? "" : obj).append("'");
        }
        return retStringBuilder.toString();
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        StringBuilder sqlBuilder = new StringBuilder(sql);
        if (parameterMappings.size() > 0 && parameterObject != null) {
            int start = sqlBuilder.indexOf("?");
            int end = start + 1;

            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sqlBuilder.replace(start, end, getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sqlBuilder.replace(start, end, getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sqlBuilder.replace(start, end, getParameterValue(obj));
                    }

                    start = sqlBuilder.indexOf("?");
                    end = start + 1;
                }
            }
        }
        return sqlBuilder.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    private void recordCounter(String metricName, String[] labelsName,String... labelsValue) {
        try {
            //用来计算qps等信息
            Metrics.getInstance().newCounter(metricName, labelsName).with(labelsValue).add(1);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void recordGauge(String metricName,String uri) {

    }

    private void recordTimer(String metricName, String[] labelsName,double value,String... labelsValue) {
        double[] sqlBuckets =  new double[]{1, 5, 10, 50,75, 100,150, 200,300, 400, 600,800, 1000, 2000, 5000};
        try {
            //用来计算p99等数据
            Metrics.getInstance().newHistogram(metricName, sqlBuckets,labelsName).with(labelsValue).observe(value);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}