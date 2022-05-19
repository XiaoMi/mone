package com.xiaomi.youpin.docean.plugin;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
public class Plugin {

    @Getter
    private List<IPlugin> plugins;

    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        this.plugins = classSet.stream()
                .filter(it -> ReflectUtils.getAnno(it, Lists.newArrayList(DOceanPlugin.class)).isPresent())
                .sorted((a, b) -> {
                    DOceanPlugin an = a.getAnnotation(DOceanPlugin.class);
                    DOceanPlugin bn = b.getAnnotation(DOceanPlugin.class);
                    return an.order() - bn.order();
                })
                .map(c -> (IPlugin) ReflectUtils.getInstance(c))
                .filter(it -> !it.disable(ioc))
                .peek(ins -> ins.init(classSet, ioc))
                .collect(Collectors.toList());

        this.plugins.stream().forEach(p -> ioc.putBean(p));
    }


    public void after(Ioc ioc) {
        this.plugins.stream().forEach(p -> {
            p.after(ioc);
        });
    }

    public void start(Ioc ioc) {
        this.plugins.stream().forEach(p -> p.start(ioc));
    }

    public void destory(Ioc ioc) {
        this.plugins.forEach(it -> it.destory(ioc));
    }

    private static final class LazyHolder {
        private static final Plugin ins = new Plugin();
    }

    public static final Plugin ins() {
        return LazyHolder.ins;
    }

    public void initBean(Ioc ioc, Bean bean) {
        plugins.stream().forEach(plugin -> {
            plugin.initBean(ioc, bean);
        });
    }

    public String initIoc(Ioc ioc, Annotation[] anns, Supplier<String> supplier) {
        return plugins.stream().map(plugin -> {
            Optional<String> obj = plugin.ioc(ioc, anns);
            return obj;
        }).filter(it -> it.isPresent()).map(it -> it.get()).findAny().orElse(supplier.get());
    }

    public List<Class<? extends Annotation>> filterAnnotationList() {
        return plugins.stream().map(it -> it.filterAnnotations()).flatMap(l -> l.stream()).collect(Collectors.toList());
    }


}
