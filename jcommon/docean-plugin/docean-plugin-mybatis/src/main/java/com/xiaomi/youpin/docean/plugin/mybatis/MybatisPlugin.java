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

package com.xiaomi.youpin.docean.plugin.mybatis;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.ClassPathResource;
import com.xiaomi.youpin.docean.common.Resource;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.InterceptorForQryAndUpdate;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.InterceptorFunction;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.TransactionalInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
@Slf4j
public class MybatisPlugin implements IPlugin {

    public static final String MY_BATIS_MESH_INTERCEPTORNAME = "mybatisMeshInterceptor";

    public static final String MYBATIS_MAPPER_NAME_LIST = "mybatisMapperNameList";

    private boolean serverLess = false;

    @SneakyThrows
    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("mybatis plugin init");
        Stopwatch sw = Stopwatch.createStarted();
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        Config config = ioc.getBean(Config.class);
        serverLess = Boolean.valueOf(config.get("serverless", "false"));
        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        boolean one = dbNames.size() == 1;
        dbNames.stream().forEach(it -> addDAO(ioc, it, config.get("mybatis_mapper_location", ""), one));
        log.info("mybatis plugin init use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @SneakyThrows
    private void addDAO(Ioc ioc, String beanName, String mapperLocation, boolean one) {
        Object dataSource = ioc.getBean(beanName);
        if (dataSource instanceof DataSource) {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource((DataSource) dataSource);

            Resource[] array = StreamSupport.stream(Splitter.on(",").split(mapperLocation).spliterator(), false).map(it -> new ClassPathResource(it, ioc.getClassLoader())).toArray(Resource[]::new);
            bean.setMapperLocations(array);

            DatasourceConfig config = ioc.getBean(beanName + "_config");
            if (ioc.containsBean(MY_BATIS_MESH_INTERCEPTORNAME)) {
                InterceptorFunction function = ioc.getBean(MY_BATIS_MESH_INTERCEPTORNAME);
                InterceptorForQryAndUpdate interceptor = new InterceptorForQryAndUpdate(function);
                interceptor.setDatasourceConfig(config);
                bean.setPlugins(new Interceptor[]{interceptor});
            }

            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                if (serverLess) {
                    Thread.currentThread().setContextClassLoader(ioc.getClassLoader());
                }
                SqlSessionFactory factory = bean.buildSqlSessionFactory();
                ioc.putBean("mybatis_" + beanName + config.getName(), factory);
                Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();

                List<String> mapperNameList = mappers.stream().map(it -> it.getName()).collect(Collectors.toList());
                ioc.putBean(MYBATIS_MAPPER_NAME_LIST, mapperNameList);

                mappers.forEach(it -> {
                    Object proxy = factory.getConfiguration().getMapperRegistry().getMapper(it, (SqlSession) newProxyInstance(
                            SqlSessionFactory.class.getClassLoader(),
                            new Class[]{SqlSession.class},
                            new SqlSessionInterceptor(factory)));
                    if (one) {
                        ioc.putBean(it.getName(), proxy);
                    } else {
                        String name = it.getSimpleName() + ":" + config.getName();
                        log.info("mybatis dao name:{} add", name);
                        ioc.putBean(name, proxy);
                    }
                });
            } finally {
                if (serverLess) {
                    Thread.currentThread().setContextClassLoader(oldCl);
                }
            }

        }
    }

    @Override
    public String version() {
        return "0.0.1:2022-03-29:goodjava@qq.com";
    }

    private class SqlSessionInterceptor implements InvocationHandler {

        private SqlSessionFactory sqlSessionFactory;

        private SqlSessionInterceptor(SqlSessionFactory sqlSessionFactory) {
            this.sqlSessionFactory = sqlSessionFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession sqlSession = null;
            try {
                sqlSession = sqlSessionFactory.openSession(true);
                return method.invoke(sqlSession, args);
            } catch (Throwable t) {
                Throwable unwrapped = unwrapThrowable(t);
                throw unwrapped;
            } finally {
                if (SidecarTransactionContext.getXidLocal() == null && TransactionalContext.getContext().get() == null && sqlSession != null) {
                    sqlSession.close();
                }
            }
        }
    }
}