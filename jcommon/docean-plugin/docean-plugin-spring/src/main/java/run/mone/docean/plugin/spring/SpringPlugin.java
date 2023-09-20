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

package run.mone.docean.plugin.spring;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Author goodjava@qq.com
 * @Date 2022-07-12
 * Make Docean feel like Spring.
 * <p>
 * Annotations compatible with Spring(Service Repository Component Autowired PreDestroy PostConstruct)
 */
@DOceanPlugin
@Slf4j
public class SpringPlugin implements IPlugin {

    private Map<String, String> serviceMap = new HashMap<>();

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init spring plugin");
    }

    @Override
    public List<Class<? extends Annotation>> filterAnnotations() {
        return Lists.newArrayList(Service.class, Repository.class, Component.class);
    }

    @Override
    public List<Class<? extends Annotation>> filterResourceAnnotations() {
        return Lists.newArrayList(Autowired.class);
    }

    @Override
    public void putBean(String name, Bean bean) {
        if (bean.getType() == Bean.Type.service.ordinal() || bean.getType() == Bean.Type.component.ordinal()) {
            Object service = bean.getObj();
            Class<?>[] interfaceArray = service.getClass().getInterfaces();
            if (interfaceArray.length > 0) {
                serviceMap.put(interfaceArray[0].getName(), name);
            }
        }
    }

    @Override
    public String getInitMethodName(Object obj, Class clazz) {
        return Arrays.stream(clazz.getMethods())
                .map(it -> Arrays.stream(it.getAnnotations()).filter(anno -> anno instanceof PostConstruct)
                        .findAny().map(it2 -> it.getName()).orElse(Cons.INIT))
                .filter(name -> !Cons.INIT.equals(name)).findAny().orElse(Cons.INIT);
    }

    @Override
    public String getDestoryMethodName(Object obj, Class clazz) {
        return Arrays.stream(clazz.getMethods())
                .map(it -> Arrays.stream(it.getAnnotations()).filter(anno -> anno instanceof PreDestroy)
                        .findAny().map(it2 -> it.getName()).orElse(Cons.DESTORY))
                .filter(name -> !Cons.DESTORY.equals(name)).findAny().orElse(Cons.DESTORY);
    }

    @Override
    public Optional<String> ioc(Ioc ioc, Class type, Annotation[] annotations) {
        Optional<Annotation> optional = getAnno(annotations, Resource.class);
        if (optional.isPresent()) {
            Resource resource = (Resource) optional.get();
            if (StringUtils.isEmpty(resource.name()) && type.isInterface()) {
                if (serviceMap.containsKey(type.getName())) {
                    return Optional.of(serviceMap.get(type.getName()));
                }
            }
        }
        //适配spring 的value注解
        Optional<Annotation> optionalValue = getAnno(annotations, Value.class);
        if (optionalValue.isPresent()) {
            Value value = (Value) optionalValue.get();
            String str = value.value();
            String defaultValue = "";
            String key = str;
            if (str.contains(":")) {
                str = str.substring(2, str.length() - 1);
                String[] array = str.split(":");
                if (array.length > 1) {
                    defaultValue = array[1];
                } else {
                    defaultValue = "";
                }
                key = Joiner.on("").join("${", array[0], "}");
            }
            //支持默认值
            if (ioc.getBean(key, "").equals("")) {
                ioc.putBean(key, defaultValue);
            }

            if (type == int.class) {
                ioc.putBean(key, Integer.valueOf(ioc.getBean(key)));
            }
            if (type == boolean.class) {
                ioc.putBean(key, Boolean.valueOf(ioc.getBean(key)));
            }
            return Optional.of(key);
        }
        return Optional.empty();
    }
}
