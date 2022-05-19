package com.xiaomi.youpin.docean.plugin.aop;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.aop.anno.AopConfig;

import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
public class AopPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        classSet.stream().filter(it -> Ioc.filterClass(it, Lists.newArrayList(AopConfig.class))).forEach(it -> {
            AopConfig ac = it.getAnnotation(AopConfig.class);
            Aop.ins().getInterceptorMap().put(ac.clazz(), (EnhanceInterceptor) ReflectUtils.getInstance(it));
        });
    }

}
