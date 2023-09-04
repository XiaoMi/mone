package com.xiaomi.hera.trace.etl.es.queue;

/**
 * @author shanwb
 * @date 2021-09-14
 */
public interface DiskStoreService {

    void put(String key, byte[] value);

    byte[] get(String key);

    void delete(String key);

}
