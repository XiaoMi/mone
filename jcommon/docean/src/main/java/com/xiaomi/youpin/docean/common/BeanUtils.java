package com.xiaomi.youpin.docean.common;

import net.sf.cglib.beans.BeanMap;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 1/19/21
 */
public abstract class BeanUtils {

    public static <T> Map<String, Object> beanToMap(T bean) {
        return BeanMap.create(bean);
    }

    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }


}
