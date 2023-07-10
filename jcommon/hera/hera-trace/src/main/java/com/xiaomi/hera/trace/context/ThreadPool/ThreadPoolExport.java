package com.xiaomi.hera.trace.context.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zhangping17
 * @Date 2023/5/8 5:24 下午
 */
public class ThreadPoolExport {

    private static List<CallBack> callBacks = new ArrayList<>();
    private static ConcurrentHashMap<String, ThreadpoolConfig> hashMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ThreadpoolConfig> getThreadPoolConfigs() {
        for (CallBack callBack : callBacks) {
            hashMap.putAll(callBack.getThreadPoolConfigs());
        }
        return hashMap;
    }

    public static void addCallBack(CallBack callBack) {
        callBacks.add(callBack);
    }
}
