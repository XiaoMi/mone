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

package com.xiaomi.youpin.gateway.netty.filter;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.Keys;
import com.xiaomi.youpin.gateway.common.ZipUtils;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.filter.FilterDef;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.qps.QpsAop;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.nutz.lang.stream.StringInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service
public class FilterManager {

    @Autowired
    private ApplicationContext ac;

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Autowired
    private ConfigService configService;

    @Autowired
    private Redis redis;

    @Autowired
    protected DefaultMQProducer defaultMQProducer;

    @Autowired
    private QpsAop qpsAop;

    private ConcurrentHashMap<String, URLClassLoader> classLoaderMap = new ConcurrentHashMap<>();


    /**
     * @param pathNameList
     * @return
     */
    public List<RequestFilter> loadRequestFilter(List<String> pathNameList) {
        if (pathNameList.size() == 0) {
            return Lists.newArrayList();
        }
        try {
            URL[] urls = pathNameList.stream().map(p -> {
                try {
                    return new URL("file:" + p);
                } catch (MalformedURLException e) {
                    log.error(e.getMessage());
                }
                return null;
            }).filter(it -> null != it).toArray(URL[]::new);
            return Arrays.stream(urls).map(url -> {
                try {
                    log.info("load request filter url:{}", url);
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
                    return loadFilter(url.getFile(), classLoader);
                } catch (Throwable e) {
                    log.error("load filter error, url: {}, msg: {}", url, e.getMessage(), e);
                }
                return null;
            }).filter(it -> null != it).collect(Collectors.toList());

        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return Lists.newArrayList();
    }


    public void deleteOldFilter(String type, List<String> names) {
        try {
            File file = new File(configService.getSystemFilterPath());
            //如果是添加,则不再删除所有,而是直删除需要更新的
            if (type.equals("add") || type.equals("remove")) {
                String name = names.get(0);
                String delFile = configService.getSystemFilterPath() + name;
                new File(delFile).delete();
            } else {
                Arrays.stream(file.listFiles()).forEach(f -> f.delete());
            }
        } catch (Throwable ex) {
            //ignore
        }
    }


    public RequestFilter loadFilter(String url, URLClassLoader classLoader) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String content = ZipUtils.readFile(url, "FilterDef");
        Properties properties = new Properties();
        properties.load(new StringInputStream(content));
        String filterClass = properties.getProperty("filter");
        Class<?> clazz = classLoader.loadClass(filterClass);
        RequestFilter ins = (RequestFilter) clazz.newInstance();
        String name = properties.getProperty("name");
        String author = properties.getProperty("author");
        String groups = properties.getProperty("groups");
        log.info("loadFilter, name:{}, author:{}, groups:{} ", name, author, groups);
        classLoaderMap.put(name, classLoader);
        ins.setDef(new FilterDef(0, name, author, groups));
        ins.setGetBeanFunction(getBean());
        return ins;
    }

    /**
     * 需要限制(目前只透出 redis 和 dubbo 和 nacos 和 RocketMq 和 qps)
     *
     * @return
     */
    private Function<Class, Object> getBean() {
        return (bean) -> {
            if (bean.equals(Redis.class) || bean.equals(Dubbo.class) || bean.equals(Nacos.class) || bean.equals(DefaultMQProducer.class) || bean.equals(QpsAop.class)) {
                return ac.getBean(bean);
            } else {
                return null;
            }
        };
    }


    public void register(String name, Function<ApplicationContext, Object> function) {
        beanFactory.registerSingleton(name, function.apply(ac));
    }


    public void destroyBean(String name) {
        beanFactory.destroyScopedBean(name);
    }

    /**
     * 释放classloader
     *
     * @param name
     */
    public void releaseClassloader(String name) {
        try {
            URLClassLoader classLoader = classLoaderMap.get(name);
            if (null == classLoader) {
                return;
            }
            classLoaderMap.remove(name);

            Object ucpObj = null;
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);
            ucpObj = ucpField.get(classLoader);
            URL[] list = classLoader.getURLs();
            for (int i = 0; i < list.length; i++) {
                Method m = ucpObj.getClass().getDeclaredMethod("getLoader", int.class);
                m.setAccessible(true);
                Object jarLoader = m.invoke(ucpObj, i);
                String clsName = jarLoader.getClass().getName();
                if (clsName.indexOf("JarLoader") != -1) {
                    m = jarLoader.getClass().getDeclaredMethod("ensureOpen");
                    m.setAccessible(true);
                    m.invoke(jarLoader);
                    m = jarLoader.getClass().getDeclaredMethod("getJarFile");
                    m.setAccessible(true);
                    JarFile jf = (JarFile) m.invoke(jarLoader);
                    // 释放jarLoader中的jar文件
                    jf.close();
                    log.info("release jar: " + jf.getName());
                }
            }
        } catch (Throwable ex) {
            log.error("releaseClassloader error:{}", ex.getMessage());
        }

    }


    public List<RequestFilter> getUserFilterList(String type, List<String> names) {
        try {
            if (!configService.isAllowUserFilter()) {
                log.info("skip user filter");
                return Lists.newArrayList();
            }
            deleteOldFilter(type, names);
            downloadFilter(type, names);
            List<String> jarList = getJarPathList();
            log.info("jarList:{}", jarList);
            return loadRequestFilter(jarList);
        } catch (Throwable ex) {
            log.error("getUserFilterList ex:{}", ex.getMessage());
            return Lists.newArrayList();
        }
    }

    private List<String> getJarPathList() {
        try {
            return Files.find(Paths.get(configService.getSystemFilterPath()), 5, (path, attr) -> !attr.isDirectory()).map(it -> it.toString()).collect(Collectors.toList());
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return Lists.newArrayList();
    }

    private void downloadFilter(String type, List<String> names) {
        try {
            //如果type是remove，则不需要下载任何包
            if ("remove".equals(type)) {
                log.info("downloadFilter, type is remove");
                return;
            }
            createFilterDirectorie();
            Set<String> set = redis.smembers(Keys.systemFilterSetKey());
            log.info("filter num:{} type:{} names:{}", set.size(), type, names);
            if (null != set && set.size() > 0) {
                set.stream().forEach(it -> {
                    try {
                        //只更新需要添加的
                        if (type.equals("add") && !names.get(0).equals(it)) {
                            return;
                        }

                        downloadFilterFromRedis(it);
                    } catch (Throwable e) {
                        log.error("downloadFilter:{} error:{}", it, e.getMessage());
                    }
                });
            }
        } catch (Throwable ex) {
            log.error("downloadFilter:{}", ex.getMessage());
        }
    }

    private void downloadFilterFromRedis(String name) throws IOException {
        for (int i = 0; i < 3; i++) {
            byte[] bytes = redis.getBytes(Keys.systemFilterKey(name));
            if (null == bytes || bytes.length == 0) {
                log.warn("downloadFilter:{} fail, retry times:{}", name, i);
                continue;
            }
            if (Optional.ofNullable(bytes).isPresent()) {
                log.info("downloadFilter:{} length:{}", name, bytes.length);
                Files.write(Paths.get(configService.getSystemFilterPath() + name), bytes);
                break;
            }
        }
        return;
    }


    private void createFilterDirectorie() {
        try {
            Files.createDirectories(Paths.get(configService.getSystemFilterPath()));
        } catch (IOException e) {
        }
    }

}
