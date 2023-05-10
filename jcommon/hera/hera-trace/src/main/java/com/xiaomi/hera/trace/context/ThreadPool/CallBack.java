package com.xiaomi.hera.trace.context.ThreadPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhangping17
 * @Date 2023/5/8 5:24 下午
 */
public interface CallBack {

    ConcurrentHashMap<String, ThreadpoolConfig> getThreadPoolConfigs();
}
