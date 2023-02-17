package com.xiaomi.mone.log.agent.channel.comparator;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/15 10:02
 */
public interface SimilarComparator<T> {

    boolean compare(T t);
}
