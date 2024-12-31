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

package com.xiaomi.youpin.docean.plugin.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.ClassPathResource;
import com.xiaomi.youpin.docean.common.Resource;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.mybatisplus.interceptor.InterceptorForQryAndUpdate;
import com.xiaomi.youpin.docean.plugin.mybatisplus.interceptor.InterceptorFunction;
import com.xiaomi.youpin.docean.plugin.mybatisplus.interceptor.TransactionalInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin(order = Integer.MAX_VALUE - 1)
@Slf4j
public class MybatisPlugin implements IPlugin {

    public static final String MY_BATIS_MESH_INTERCEPTORNAME = "mybatisMeshInterceptor";

    public static final String MYBATIS_MAPPER_LOCATION = "mybatis_mapper_location";

    @SneakyThrows
    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("mybatis plugin init");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        Config config = ioc.getBean(Config.class);
        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        boolean one = dbNames.size() == 1;
        dbNames.stream().forEach(it -> addDAO(ioc, it, config.get(MYBATIS_MAPPER_LOCATION, ""), one));
    }

    @SneakyThrows
    private void addDAO(Ioc ioc, String beanName, String mapperLocation, boolean one) {
        Object dataSource = ioc.getBean(beanName);
        if (dataSource instanceof DataSource) {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource((DataSource) dataSource);
            String[] mapperList = mapperLocation.split(",");
            log.info("mapperLocation is [{}]", mapperList);
            if (mapperList == null || mapperList.length == 0) {
                log.info("mapper path is empty");
                return;
            }
            Resource[] array = new Resource[mapperList.length];
            for (int i = 0; i < mapperList.length; i++) {
                array[i] = new ClassPathResource(mapperList[i].trim());
                log.info("mapper local is [{}]", mapperList[i].trim());
            }
            bean.setMapperLocations(array);

            DatasourceConfig config = ioc.getBean(beanName + "_config");
            if (ioc.containsBean(MY_BATIS_MESH_INTERCEPTORNAME)) {
                InterceptorFunction function = ioc.getBean(MY_BATIS_MESH_INTERCEPTORNAME);
                InterceptorForQryAndUpdate interceptor = new InterceptorForQryAndUpdate(function);
                interceptor.setDatasourceConfig(config);
                bean.setPlugins(new Interceptor[]{interceptor});
            }

            Set<MybatisPlusInterceptor> plusInterceptors = ioc.getBeans(MybatisPlusInterceptor.class);
            if (!CollectionUtils.isEmpty(plusInterceptors)) {
                bean.setPlugins(plusInterceptors.toArray(new Interceptor[]{}));
            }

            SqlSessionFactory factory = bean.buildSqlSessionFactory();

            ioc.putBean("mybatis_" + beanName + config.getName(), factory);

            Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();
            mappers.forEach(it -> {
                Object proxy = factory.getConfiguration().getMapperRegistry().getMapper(it, (SqlSession) newProxyInstance(
                        SqlSessionFactory.class.getClassLoader(),
                        new Class[]{SqlSession.class},
                        new SqlSessionInterceptor(factory)));

                String name = it.getName();
                if (!one) {
                    name = it.getSimpleName() + ":" + config.getName();
                }
                log.info("mybatis dao name:{} add", name);
                ioc.putBean(name, proxy);
            });

        }
    }

    @Override
    public String version() {
        return "0.0.1:2021-03-03";
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
                if (TransactionalContext.getTransactionActive() != null && TransactionalContext.getTransactionActive()) {
                    if (TransactionalContext.getSqlSession() != null) {
                        sqlSession = TransactionalContext.getSqlSession();
                        Object result = method.invoke(sqlSession, args);
                        return result;
                    } else {
                        sqlSession = sqlSessionFactory.openSession(false);
                        Object result = method.invoke(sqlSession, args);
                        TransactionalContext.setSqlSession(sqlSession);
                        return result;
                    }
                } else {
                    sqlSession = sqlSessionFactory.openSession(true);
                    Object result = method.invoke(sqlSession, args);
                    sqlSession.commit(true);
                    return result;
                }

            } catch (Throwable t) {
                Throwable unwrapped = unwrapThrowable(t);
                throw unwrapped;
            } finally {
                if ((TransactionalContext.getTransactionActive() == null || !TransactionalContext.getTransactionActive()) && sqlSession != null) {
                    sqlSession.close();
                }
            }
        }
    }
}
