/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.hera.trace.etl.es;

import com.xiaomi.hera.trace.etl.common.HashUtil;
import com.xiaomi.hera.trace.etl.es.domain.LocalStorages;
import com.xiaomi.hera.trace.etl.es.queue.impl.RocksdbStoreServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.rocksdb.RocksDBException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author shanwb
 * @date 2021-09-18
 */
@Slf4j
public class RocksdbTest {

    private RocksdbStoreServiceImpl storeService = new RocksdbStoreServiceImpl("/tmp/rocksdb_test/firstdb", "first");

    @Test
    public void testTTl() throws InterruptedException, RocksDBException {
        storeService.put("key_ttl001", "ttl001".getBytes(StandardCharsets.UTF_8));
        log.info("key_ttl001, value:{}", new String(storeService.get("key_ttl001")));
        TimeUnit.SECONDS.sleep(15L);
        storeService.getRocksDB().compactRange("key_ttl001".getBytes(StandardCharsets.UTF_8), "key_ttl001".getBytes(StandardCharsets.UTF_8));
        log.info("key_ttl001, value:{}", new String(storeService.get("key_ttl001")));
    }

    @Test
    public void testInsert() {
        for (int i = 0; i < 17; i++) {
            int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    int j = 0;
                    for (; ; ) {
                        String key = System.currentTimeMillis() + "_" + LocalStorages.firstRocksKeySuffix.addAndGet(1);
                        log.info("write i:{}, j:{}, key:{}", finalI, j++, key);
                        storeService.put(key, "testMessage".getBytes(StandardCharsets.UTF_8));
//                        System.out.println("xxxx:" + new String(storeService.get("1635302409044_123680")));;
                        //                        storeService.put(key, "1".getBytes(StandardCharsets.UTF_8));
                        //storeService.delete(key);
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        }
    }

    @Test
    public void testDelete() {
        String key = "del_key";
        storeService.put(key, "0001".getBytes(StandardCharsets.UTF_8));

        log.info("del_key:{}, value:{}", key, new String(storeService.get(key)));
        storeService.delete(key);

        log.info("del_key1:{}, value is null:{}", key, null == storeService.get(key));
    }

    @Test
    public void testConsistentHash(){
        int i = HashUtil.consistentHash("2ce894abb64a5e7e0219875b1c7cd0c6", 100);
        System.out.println("result : "+i);
    }

}
