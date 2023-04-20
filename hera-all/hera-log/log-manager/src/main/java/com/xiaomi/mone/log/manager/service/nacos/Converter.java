package com.xiaomi.mone.log.manager.service.nacos;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 15:24
 */
@FunctionalInterface
public interface Converter<S, T> {
    /**
     * 数据转化
     * @param source
     * @return
     */
    T convert(S source);
}
