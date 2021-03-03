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

package com.xiaomi.youpin.docean;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.ClassFinder;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.ioc.BeanAnnoProcessor;
import com.xiaomi.youpin.docean.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class Ioc {

    private ConcurrentHashMap<String, Bean> beans = new ConcurrentHashMap<>();

    private static final List<Class<? extends Annotation>> scanAnno = Lists.newArrayList(Component.class, Service.class, Controller.class);

    private Ioc() {
    }

    private static boolean filterConfigrationClass(Class<?> it) {
        return Optional.ofNullable(it).map(it2 -> it2.getAnnotation(Configuration.class)).isPresent();
    }

    public static boolean filterClass(Class<?> it, List<Class<? extends Annotation>> scanAnnoList) {
        return Optional.ofNullable(it).map(it2 -> {
                    Optional<Annotation> optional = ReflectUtils.getAnno(it2, scanAnnoList);
                    if (optional.isPresent()) {
                        log.info("ioc add:{}", it);
                        return it2;
                    }
                    return null;
                }
        ).isPresent();
    }

    private static Class<?> classForName(String name) {
        Class<?> clazz = ReflectUtils.classForName(name);
        return clazz;
    }


    private int getType(Class<?> clazz) {
        Optional<Annotation> optional = ReflectUtils.getAnno(clazz, scanAnno);
        if (optional.isPresent()) {
            return (int) ReflectUtils.invokeMethod(optional.get(), "type", new Object[]{});
        }
        return -1;
    }


    private Bean initBean(Class<?> it, boolean beans) {
        String name = getName(it);
        Bean bean = new Bean();
        bean.setName(name);
        bean.setClazz(it);
        bean.setType(getType(it));
        Object obj = Aop.ins().enhance(it);
        bean.setObj(obj);
        if (beans) {
            this.beans.put(name, bean);
        }
        //插件增强
        Plugin.ins().initBean(this, bean);
        return bean;
    }


    private String getName(Class<?> clazz) {
        Service service = clazz.getAnnotation(Service.class);
        return Optional.ofNullable(service).map(it -> {
            String name = it.name();
            if (!name.equals("")) {
                return name;
            }
            return clazz.getName();
        }).orElse(clazz.getName());
    }


    private void initIoc(Bean it) {
        Field[] fields = ReflectUtils.fields(it.getClazz());
        Arrays.stream(fields).forEach(f -> {
            Annotation[] ans = f.getAnnotations();
            String o = Plugin.ins().initIoc(this, ans, () -> {
                MutableObject res = new MutableObject();
                Optional.ofNullable(f.getAnnotation(Resource.class)).ifPresent(f2 -> {
                    String name = f2.name();
                    if (name.equals("")) {
                        name = f.getType().getName();
                    }
                    res.setObj(name);
                });
                return Optional.ofNullable(res.getObj()).map(ob -> ob.toString()).orElse(null);
            });
            Optional.ofNullable(o).ifPresent(obj -> {
                initIoc0(o, it.getObj(), f);
            });
        });
    }

    private void initIoc0(String name, Object obj, Field field) {
        Bean b = this.beans.get(name);
        Optional.ofNullable(b).ifPresent(o -> {
            o.incrReferenceCnt();
            ReflectUtils.setField(obj, field, o.getObj());
        });
    }

    private static void callInit(Bean it) {
        ReflectUtils.invokeMethod(it.getObj(), it.getClazz(), Cons.INIT, new Object[]{});
    }

    public Ioc init(String... scanPackages) {
        Stopwatch sw = Stopwatch.createStarted();
        Set<String> classNameSet = getClassNameSet(scanPackages);
        Set<? extends Class<?>> classSet = classNameSet.stream().map(Ioc::classForName).filter(it -> Optional.ofNullable(it).isPresent()).collect(Collectors.toSet());
        //init plugin
        Plugin.ins().init(classSet, this);
        //init configuration bean
        classSet.stream().filter(Ioc::filterConfigrationClass).forEach(it -> BeanAnnoProcessor.process(it, this));
        //init bean
        classSet.stream().filter(it -> filterClass(it, annoList())).forEach(it -> initBean(it, true));
        //ioc
        this.beans.values().stream().forEach(it -> initIoc(it));
        //call init method
        this.beans.values().stream().forEach(Ioc::callInit);
        Plugin.ins().after(this);
        Plugin.ins().start(this);
        log.info("Docean init use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        return this;
    }

    private Set<String> getClassNameSet(String[] scanPackages) {
        return Arrays.stream(scanPackages).map(scanPackage -> {
            Set<String> set = new ClassFinder().findClassSet(scanPackage);
            return set;
        }).flatMap(it -> it.stream()).collect(Collectors.toSet());
    }

    private List<Class<? extends Annotation>> annoList() {
        List<Class<? extends Annotation>> filterAnnotationList = Plugin.ins().filterAnnotationList();
        filterAnnotationList.addAll(scanAnno);
        return filterAnnotationList;
    }


    private static class LazyHolder {
        private static final Ioc ins = new Ioc();
    }

    public static final Ioc ins() {
        return LazyHolder.ins;
    }


    public <T> T getBean(String name) {
        return (T) beans.get(name).getObj();
    }

    public <T> T getBean(Class clazz) {
        return (T) beans.get(clazz.getName()).getObj();
    }

    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    public Ioc putBean(String name, Object obj) {
        return putBean(name, name, obj);
    }

    public Ioc putBean(String name, String alias, Object obj) {
        Bean bean = new Bean();
        bean.setObj(obj);
        bean.setAlias(alias);
        bean.setClazz(obj.getClass());
        bean.setName(obj.getClass().getName());
        beans.put(name, bean);
        return this;
    }

    public Ioc remove(String name) {
        beans.remove(name);
        return this;
    }


    public <T> T createBean(Class<T> clazz) {
        log.info("create bean:{}", clazz);
        Bean bean = initBean(clazz, false);
        initIoc(bean);
        return (T) bean.getObj();
    }


    public Ioc putBean(Object obj) {
        Bean bean = new Bean();
        bean.setObj(obj);
        bean.setClazz(obj.getClass());
        beans.put(obj.getClass().getName(), bean);
        return this;
    }


    public Set<Object> getBeans() {
        return new HashSet<>(beans.values());
    }

    public <T> Set<T> getBeans(Class<T> clazz) {
        return beans.values().stream().filter(it -> (clazz.isAssignableFrom(it.getClazz()))).map(it -> (T) it.getObj()).collect(Collectors.toSet());
    }

    public Map<String, Bean> beans() {
        return this.beans;
    }

    /**
     * destory
     * 根据层级和引用数量 (controller->service->component) (理论上被引用最少的,最先销毁负面影响最少) call destory method
     */
    public void destory() {
        log.info("ioc destory");
        Plugin.ins().destory(this);
        this.beans.values().stream().sorted(Bean::compareTo).forEach(b -> {
            if (Optional.ofNullable(b.getObj()).isPresent()) {
                ReflectUtils.invokeMethod(b.getObj(), b.getObj().getClass(), Cons.DESTORY, new Object[]{});
            }
        });
        this.beans.clear();
    }

}
