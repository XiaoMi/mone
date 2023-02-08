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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.*;
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
import java.lang.reflect.Method;
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

    private final List<Class<? extends Annotation>> scanAnno = Lists.newArrayList(Component.class, Service.class, Controller.class);

    private final List<Class<? extends Annotation>> resourceAnno = Lists.newArrayList(Resource.class);

    @Getter
    private String[] scanPackages;

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

    private String name = "";

    public Ioc name(String name) {
        this.name = name;
        return this;
    }

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

    public Ioc cleanAnnos() {
        scanAnno.clear();
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

    private static final String TYPE_METHOD_NAME = "type";

    private int getType(Class<?> clazz) {
        Optional<Annotation> optional = ReflectUtils.getAnno(clazz, scanAnno);
        if (optional.isPresent()) {
            Optional<Method> opt = ReflectUtils.getMethod(optional.get().getClass(), TYPE_METHOD_NAME);
            if (opt.isPresent()) {
                return Safe.callAndLog(() -> ReflectUtils.invokeMethod(optional.get(), TYPE_METHOD_NAME, new Object[]{}), -1);
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
            Plugin.ins().putBean(name, bean);
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


    private Annotation getResourceAnno(Field f) {
        Optional<Class<? extends Annotation>> optional = resourceAnno.stream().filter(it -> f.getAnnotation(it) != null).findAny();
        if (optional.isPresent()) {
            return f.getAnnotation(optional.get());
        }
        return null;
    }


    private String getResourceName(Annotation annotation, Field f) {
        if (annotation instanceof Resource) {
            Resource f2 = (Resource) annotation;
            String name = f2.name();
            if (name.equals("")) {
                name = f.getType().getName();
            }
            if (!f2.lookup().equals("")) {
                name = Joiner.on(":").join(name, f2.lookup());
            }
            return name;
        }
        return f.getType().getName();
    }


    /**
     * 完成依赖注入
     *
     * @param it
     */
    public void initIoc(Bean it) {
        Field[] fields = ReflectUtils.fields(it.getClazz());
        Arrays.stream(fields).forEach(f -> {
            Annotation[] ans = f.getAnnotations();
            String o = Plugin.ins().initIoc(this, f.getType(), ans, () -> {
                MutableObject res = new MutableObject();
                Optional.ofNullable(getResourceAnno(f)).ifPresent(f2 -> {
                    String name = getResourceName(f2, f);
                    res.setObj(name);
                });
                return Optional.ofNullable(res.getObj()).map(ob -> ob.toString()).orElse(null);
            });
            Optional.ofNullable(o).ifPresent(obj -> {
                initIoc0(o, it, f);
            });
        });
    }

    /**
     * 加入一个bean(动态的)
     * 放入依赖列表,为了避免二次分析(其实也有办法,再分析一遍,但为了性能先这样吧)
     */
    public void addBean(String name, Object obj, Map<String, Field> dependenceMap) {
        this.putBean(name, obj);
        Bean bean = this.getBeanInfo(name);
        //调用init函数
        callInit(bean);
        //完成依赖注入(我依赖的)
        initIoc(bean);
        //完成依赖注入(依赖我的)
        callDependenceIoc(bean, dependenceMap);
    }

    private void callDependenceIoc(Bean bean, Map<String, Field> dependenceMap) {
        dependenceMap.entrySet().stream().forEach(it -> {
            Bean b = this.getBeanInfo(it.getKey());
            ReflectUtils.setField(b.getObj(), it.getValue(), bean.getObj());
        });
    }

    /**
     * 移除某个bean(动态的)
     * 1.移除依赖这个bean的引用
     * 2.从ioc中删除这个bean
     *
     * @param name
     */
    public void removeBean(String name) {
        Bean bean = this.getBeanInfo(name);
        bean.getDependenceFieldMap().entrySet().forEach(it -> {
            Bean b = this.getBeanInfo(it.getKey());
            ReflectUtils.setField(b.getObj(), it.getValue(), null);
        });
        this.beans.remove(name);
        destoryBean(bean);
        this.publishEvent(new Event(EventType.removeBean, name, ImmutableMap.of("name", name)));
    }


    private void initIoc0(String name, Bean bean, Field field) {
        Bean b = this.beans.get(name);
        Optional.ofNullable(b).ifPresent(o -> {
            o.incrReferenceCnt();
            o.getDependenceList().add(bean.getName());
            o.getDependenceFieldMap().put(bean.getName(), field);
            ReflectUtils.setField(bean.getObj(), field, o.getObj());
        });
    }

    private void callInit(Bean it) {
        Stopwatch sw = Stopwatch.createStarted();
        String methodName = Plugin.ins().getInitMethodName(it.getObj(), it.getClazz());
        ReflectUtils.invokeMethod(it.getObj(), it.getClazz(), methodName, new Object[]{});
        this.publishEvent(new Event(EventType.initBean, it, ImmutableMap.of("useTime", sw.elapsed(TimeUnit.MILLISECONDS))));
    }

    public Ioc classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public Ioc init(String... scanPackages) {
        this.scanPackages = scanPackages;
        this.publishEvent(new Event(EventType.initBegin));
        Stopwatch sw = Stopwatch.createStarted();
        Set<String> classNameSet = getClassNameSet(this.scanPackages);
        Set<? extends Class<?>> classSet = classNameSet.stream().map(it -> classForName(it, this.classLoader)).filter(it -> Optional.ofNullable(it).isPresent()).collect(Collectors.toSet());
        //init plugin
        Plugin.ins().init(classSet, this);
        initAnnoList();
        //init bean
        classSet.stream().filter(it -> filterClass(this, it, scanAnno)).filter(this.classFilter).forEach(it -> initBean(it, true));
        //ioc
        this.beans.values().stream().forEach(it -> initIoc(it));
        //call init method
        this.beans.values().stream().forEach(it -> callInit(it));
        Plugin.ins().after(this);
        Plugin.ins().start(this);
        this.publishEvent(new Event(EventType.initFinish, sw.elapsed(TimeUnit.MILLISECONDS), ImmutableMap.of("name", this.name)));
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
    private void initAnnoList() {
        List<Class<? extends Annotation>> filterAnnotationList = Plugin.ins().filterAnnotationList();
        if (filterAnnotationList.size() > 0) {
            filterAnnotationList.addAll(scanAnno);
            scanAnno.clear();
            scanAnno.addAll(filterAnnotationList);
        }
        List<Class<? extends Annotation>> filterResourceAnnotationList = Plugin.ins().filterResourceAnnotationList();
        if (filterResourceAnnotationList.size() > 0) {
            filterResourceAnnotationList.addAll(resourceAnno);
            resourceAnno.clear();
            resourceAnno.addAll(filterResourceAnnotationList);
        }
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
        return getBean(name, null);
    }

    public <T> T getBean(String name, Object defalutValue) {
        Bean bean = beans.get(name);
        return (T) Optional.ofNullable(bean).map(it -> it.getObj()).orElse(defalutValue);
    }

    public <T> T getBean(Class clazz) {
        return getBean(clazz.getName(), null);
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

    public Ioc putBean(String name, Object obj, int type) {
        return putBean(name, name, obj, "", false, type);
    }

    public Ioc putBean(String name, String alias, Object obj, String lookup, boolean objMap) {
        return putBean(name, alias, obj, lookup, objMap, Bean.Type.component.ordinal());

    }

    public Ioc putBean(String name, String alias, Object obj, String lookup, boolean objMap, int type) {
        Bean bean = new Bean();
        bean.setObj(obj);
        bean.setAlias(alias);
        bean.setClazz(obj.getClass());
        bean.setName(obj.getClass().getName());
        bean.setLookup(lookup);
        bean.setType(type);
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

    /**
     * 根据类型获取bean 列表
     *
     * @param type
     * @return
     */
    public Set<Bean> getBeans(Bean.Type type) {
        return beans.values().stream().filter(it -> it.getType() == type.ordinal()).collect(Collectors.toSet());
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
                if (b.getObj() instanceof Ioc) {
                    return;
                }
                destoryBean(b);
            }
        });
        this.beans.clear();
    }


    /**
     * 调用这个bean中的销毁逻辑
     *
     * @param b
     */
    private void destoryBean(Bean b) {
        String destoryMethodName = Plugin.ins().getDestoryMethodName(b.getObj(), b.getObj().getClass());
        ReflectUtils.invokeMethod(b.getObj(), b.getObj().getClass(), destoryMethodName, new Object[]{});
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
        event.setIocName(this.name);
        this.iocListener.multicastEvent(event);
    }

}
