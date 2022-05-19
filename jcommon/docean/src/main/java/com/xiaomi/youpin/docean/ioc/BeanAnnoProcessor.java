package com.xiaomi.youpin.docean.ioc;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.ReflectUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/6/24
 */
public class BeanAnnoProcessor {

    public static void process(Class<?> configuration, Ioc ioc) {
            Arrays.stream(configuration.getMethods()).filter(m -> Optional.ofNullable(m.getAnnotation(Bean.class)).isPresent())
                    .map(m -> Pair.of(m.getReturnType().getName(),ReflectUtils.invokeMethod(ReflectUtils.getInstance(configuration), m, new Object[]{}))).forEach(pair->{
                        ioc.putBean(pair.getKey(),pair.getValue());
            });
    }


}
