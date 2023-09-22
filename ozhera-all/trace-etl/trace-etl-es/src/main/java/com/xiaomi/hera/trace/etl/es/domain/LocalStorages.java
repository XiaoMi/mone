package com.xiaomi.hera.trace.etl.es.domain;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/11/10 10:00 am
 */
public class LocalStorages {
    public static long firstCurrentSeconds = System.currentTimeMillis() / 1000;
    public static long secondCurrentSeconds = System.currentTimeMillis() / 1000;
    public static AtomicLong firstRocksKeySuffix = new AtomicLong(0L);
    public static AtomicLong secondRocksKeySuffix = new AtomicLong(0L);

    public static volatile boolean talosIsShutDown = false;
}
