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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.*;
import com.xiaomi.youpin.docean.ioc.BeanAnnoProcessor;
import com.xiaomi.youpin.docean.listener.IocListener;
import com.xiaomi.youpin.docean.listener.Listener;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.notify.DoceanNotify;
import com.xiaomi.youpin.docean.plugin.Plugin;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Resource;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class Ioc {

    private ConcurrentHashMap<String, Bean> beans = new ConcurrentHashMap<>();

    private static final List<Class<? extends Annotation>> scanAnno = Lists.newArrayList(Component.class, Service.class, Controller.class);

    /**
     * 和spring这样的容器交互的时候需要用到
     */
    private Function<String, Object> contextFunction = new Function<String, Object>() {
        @Override
        public @Nullable Object apply(@Nullable String s) {
            return new Object();
        }
    };

    private IocListener iocListener = new IocListener();

    private Predicate<Class> classFilter = o -> true;

    @Getter
    private ClassLoader classLoader;

    public Ioc setContextFunction(Function<String, Object> function) {
        this.contextFunction = function;
        return this;
    }

    public Ioc classFilter(Predicate<Class> classFilter) {
        this.classFilter = classFilter;
        return this;
    }

    public Ioc setAnnos(Class<? extends Annotation>... annotations) {
        scanAnno.addAll(Arrays.asList(annotations));
        return this;
    }

    public Function<String, Object> contextFunction() {
        return this.contextFunction;
    }

    private Ioc() {
        this.iocListener.regListener(new DoceanNotify());
    }

    private Ioc(ClassLoader classLoader) {
        this();
        this.classLoader = classLoader;
    }

    private static boolean filterConfigrationClass(Class<?> it) {
        return Optional.ofNullable(it).map(it2 -> it2.getAnnotation(Configuration.class)).isPresent();
    }

    public static boolean filterClass(Ioc ioc, Class<?> it, List<Class<? extends Annotation>> scanAnnoList) {
        return Optional.ofNullable(it).map(it2 -> {
                    Optional<Annotation> optional = ReflectUtils.getAnno(it2, scanAnnoList);
                    if (optional.isPresent()) {
                        ioc.publishEvent(new Event(EventType.addBean, it.getName()));
                        return it2;
                    }
                    return null;
                }
        ).isPresent();
    }

    private static Class<?> classForName(String name, ClassLoader classLoader) {
        Class<?> clazz = ReflectUtils.classForName(name, classLoader);
        return clazz;
    }


    private int getType(Class<?> clazz) {
        Optional<Annotation> optional = ReflectUtils.getAnno(clazz, scanAnno);
        if (optional.isPresent()) {
            try {
                return (int) ReflectUtils.invokeMethod(optional.get(), "type", new Object[]{});
            } catch (Throwable ignore) {

            }
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
        Plugin.ins().initBean(this, bean);
        return bean;
    }


    private String getName(Class<?> clazz) {
        String name = getName0(clazz);
        if (StringUtils.isEmpty(name)) {
            return clazz.getName();
        }
        return name;
    }

    private String getName0(Class<?> clazz) {
        List<Class<? extends Annotation>> annoList = scanAnno;
        return annoList.stream().map(it -> {
            Object obj = clazz.getAnnotation(it);
            if (Optional.ofNullable(obj).isPresent()) {
                Optional<String> optional = Lists.newArrayList("name", "value").stream().map(v -> getAnnoValue(obj, v)).filter(v -> null != v).findAny();
                return optional.orElse("");
            }
            return "";
        }).filter(it -> StringUtils.isNotEmpty(it)).findFirst().orElse("");
    }

    /**
     * 获取注解中的值
     *
     * @param obj
     * @param method
     * @return
     */
    private String getAnnoValue(Object obj, String method) {
        try {
            String value = obj.getClass().getMethod(method).invoke(obj).toString();
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        } catch (Throwable ignore) {
        }
        return null;
    }


    /**
     * 完成依赖注入
     *
     * @param it
     */
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
                    if (!f2.lookup().equals("")) {
                        name = Joiner.on(":").join(name, f2.lookup());
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
            o.getDependenceList().add(name);
            ReflectUtils.setField(obj, field, o.getObj());
        });
    }

    private static void callInit(Bean it) {
        ReflectUtils.invokeMethod(it.getObj(), it.getClazz(), Cons.INIT, new Object[]{});
    }

    public Ioc init(String... scanPackages) {
        this.publishEvent(new Event(EventType.initBegin));
        Stopwatch sw = Stopwatch.createStarted();
        Set<String> classNameSet = getClassNameSet(scanPackages);
        Set<? extends Class<?>> classSet = classNameSet.stream().map(it -> classForName(it, this.classLoader)).filter(it -> Optional.ofNullable(it).isPresent()).collect(Collectors.toSet());
        //init plugin
        Plugin.ins().init(classSet, this);
        //init configuration bean
        classSet.stream().filter(Ioc::filterConfigrationClass).forEach(it -> BeanAnnoProcessor.process(it, this));
        //init bean
        annoList();
        classSet.stream().filter(it -> filterClass(this, it, scanAnno)).filter(this.classFilter).forEach(it -> initBean(it, true));
        //ioc
        this.beans.values().stream().forEach(it -> initIoc(it));
        //call init method
        this.beans.values().stream().forEach(Ioc::callInit);
        Plugin.ins().after(this);
        Plugin.ins().start(this);
        this.publishEvent(new Event(EventType.initFinish, sw.elapsed(TimeUnit.MILLISECONDS)));
        return this;
    }

    private Set<String> getClassNameSet(String[] scanPackages) {
        return Arrays.stream(scanPackages).map(scanPackage -> {
            Set<String> set = new ClassFinder().findClassSet(scanPackage, this.classLoader);
            return set;
        }).flatMap(it -> it.stream()).collect(Collectors.toSet());
    }

    /**
     * 把插件也需要扫描的加载进来
     *
     * @return
     */
    private List<Class<? extends Annotation>> annoList() {
        List<Class<? extends Annotation>> filterAnnotationList = Plugin.ins().filterAnnotationList();
        if (filterAnnotationList.size() > 0) {
            filterAnnotationList.addAll(scanAnno);
            scanAnno.clear();
            scanAnno.addAll(filterAnnotationList);
        }
        return scanAnno;
    }


    private static class LazyHolder {
        private static final Ioc ins = new Ioc();
    }

    public static final Ioc ins() {
        return LazyHolder.ins;
    }

    /**
     * 创建一个全新的Ioc容器(server less 需要)
     *
     * @param classLoader
     * @return
     */
    public static Ioc create(ClassLoader classLoader) {
        return new Ioc(classLoader);
    }


    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return beans.entrySet().stream().filter(entry -> entry.getValue().getClazz().isAnnotationPresent(annotationType)).collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getObj()));
    }


    public <T> T getBean(String name) {
        return (T) beans.get(name).getObj();
    }

    public <T> T getBean(String name, Object defalutValue) {
        Bean bean = beans.get(name);
        return (T) Optional.ofNullable(bean).map(it -> it.getObj()).orElse(defalutValue);
    }

    public <T> T getBean(Class clazz) {
        return (T) beans.get(clazz.getName()).getObj();
    }

    public Bean getBeanInfo(String name) {
        return beans.get(name);
    }

    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    public Ioc putBean(Object obj) {
        return this.putBean(obj.getClass().getName(), obj);
    }


    public Ioc regListener(Listener listener) {
        this.iocListener.regListener(listener);
        return this;
    }


    public Ioc putBeanInfo(Bean bean) {
        this.beans.put(bean.getName(), bean);
        return this;
    }


    public Ioc putBean(String name, Object obj) {
        return putBean(name, name, obj, "", false);
    }

    public Ioc putBean(String name, String alias, Object obj, String lookup, boolean objMap) {
        Bean bean = new Bean();
        bean.setObj(obj);
        bean.setAlias(alias);
        bean.setClazz(obj.getClass());
        bean.setName(obj.getClass().getName());
        bean.setLookup(lookup);
        if (StringUtils.isNotEmpty(lookup)) {
            name = Joiner.on(":").join(name, lookup);
        }
        if (objMap) {
            beans.put(obj.toString(), bean);
        }
        beans.put(name, bean);
        this.publishEvent(new Event(EventType.putBean, bean));
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


    public Set<Object> getBeans() {
        return new HashSet<>(beans.values());
    }


    public ConcurrentHashMap<String, Bean> getBeanInfos() {
        return beans;
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

    @SneakyThrows
    public void saveSnapshot() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassDeserializer())
                .registerTypeAdapter(Class.class, new ClassSerializer()).create();

        List<Bean> list = this.beans.entrySet().stream().map(it -> it.getValue()).collect(Collectors.toList());
        Files.write(Paths.get(savePath()), gson.toJson(list).getBytes());
    }

    private String savePath() {
        return FileUtils.home() + File.separator + "docean.cache";
    }

    @SneakyThrows
    public void loadSnapshot() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassDeserializer())
                .registerTypeAdapter(Class.class, new ClassSerializer()).create();
        byte[] data = Files.readAllBytes(Paths.get(savePath()));
        Type typeOfT = new TypeToken<List<Bean>>() {
        }.getType();
        List<Bean> list = gson.fromJson(new String(data), typeOfT);
        list.stream().forEach(it -> {
            log.info("{} {}", it, it.getClazz());
            Object obj = Aop.ins().enhance(it.getClazz());
            it.setObj(obj);
            this.putBeanInfo(it);
        });

        this.beans.values().stream().forEach(it -> initIoc(it));
    }

    public void publishEvent(Event event) {
        this.iocListener.multicastEvent(event);
    }

}
