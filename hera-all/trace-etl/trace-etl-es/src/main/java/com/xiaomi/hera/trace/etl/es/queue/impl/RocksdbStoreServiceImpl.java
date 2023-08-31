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

package com.xiaomi.hera.trace.etl.es.queue.impl;

import com.xiaomi.hera.trace.etl.es.queue.DiskStoreService;
import com.xiaomi.hera.trace.etl.es.util.redis.RedisClientUtil;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.TtlDB;
import org.rocksdb.util.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author shanwb
 * @date 2021-09-14
 */
public class RocksdbStoreServiceImpl implements DiskStoreService {

    private static Logger log = LoggerFactory.getLogger(RocksdbStoreServiceImpl.class);

    private RocksDB rocksDB;

    private String keyPrefix;

    public static final String FIRST_ORDER = "first";
    public static final String SECOND_ORDER = "second";

    public RocksDB getRocksDB() {
        return rocksDB;
    }

    public RocksdbStoreServiceImpl(String path, String keyPrefix) {
        this.keyPrefix = keyPrefix;
        // Create a directory to solve the problem that only a single-level directory can be created on rocksDB
        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean mkdir = file.mkdirs();
                if (!mkdir) {
                    log.error("rocksdb mkdir failed, ptah : " + path);
                    throw new RuntimeException("rocksdb mkdir failed, ptah : " + path);
                }
            }
        } catch (Exception e) {
            log.error("rocksdb mkdir failed, ptah : " + path, e);
            throw new RuntimeException("rocksdb mkdir failed, ptah : " + path);
        }
        Options options = new Options();
        options.setCreateIfMissing(true)
                // Dynamic compression, automatic compression once an hour, delete expired files
                .setLevelCompactionDynamicLevelBytes(true)
                .setPeriodicCompactionSeconds(60 * 60)
                .setWriteBufferSize(8 * SizeUnit.MB)
                .setMaxWriteBufferNumber(3)
                .setDbWriteBufferSize(30 * SizeUnit.MB)
                .setMaxBackgroundJobs(10)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                .setNumLevels(5)
                // First layer 200Mb
                .setMaxBytesForLevelBase(200 * SizeUnit.MB)
                /**
                 * The size growth coefficient between each layer is 300%
                 * 200Mb -> 600Mb -> 1.8Gb -> 5.4Gb -> 16.2Gb
                 */
                .setMaxBytesForLevelMultiplier(3.0)
                .setCompactionStyle(CompactionStyle.LEVEL)
                .setWalTtlSeconds(60L);
        try {
            rocksDB = TtlDB.open(options, path, 60 , false);
        } catch (RocksDBException e) {
            log.error("RocksdbStoreService init error:{}", e);
            throw new RuntimeException("rocksdb init failed");
        }
    }

    @Override
    public void put(String key, byte[] value) {
        try {
            rocksDB.put(key.getBytes(StandardCharsets.UTF_8), value);
        } catch (RocksDBException e) {
            log.error("RocksdbStoreService put error : ", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            rocksDB.delete(key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            log.error("RocksdbStoreService delete error : ", e);
        }
    }

    @Override
    public byte[] get(String key) {
        try {
            return rocksDB.get(key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            log.error("RocksdbStoreService get error : ", e);
        }
        return null;
    }

    public void delayTake(final String begin, Long gapBetweenLatest, Consumer<byte[]> listener, TeSnowFlake snowFlake) {
        ReadOptions readOptions = new ReadOptions();
        readOptions.setVerifyChecksums(false);
        String[] beginArr = begin.split("_");
        String beginPrefix = beginArr[0];
        long lastkeySuffix = Long.valueOf(beginArr[1]);
        while (true) {
            try (RocksIterator iterator = rocksDB.newIterator()){
                long latestKeyTime = 0L;
                for (iterator.seek(beginPrefix.getBytes(StandardCharsets.UTF_8)); iterator.isValid(); iterator.next()) {
                    String key = new String(iterator.key(), StandardCharsets.UTF_8);
                    long latestTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - gapBetweenLatest;
                    if (key.indexOf("_") < 0) {
                        continue;
                    }
                    String[] keyArr = key.split("_");
                    latestKeyTime = Long.valueOf(keyArr[0]);
                    long suffix = Long.valueOf(keyArr[1]);
                    if (beginPrefix.equals(String.valueOf(latestKeyTime)) && lastkeySuffix >= suffix) {
                        // 已经处理了，break
                        continue;
                    }
                    if (latestKeyTime > latestTime) {
                        //本次数据已取完，break
                        lastkeySuffix = 0;
                        break;
                    } else {
                        listener.accept(iterator.value());
                        lastkeySuffix = suffix;
                        snowFlake.storeLastTimestamp(keyPrefix, key);
                    }
                }
                if (latestKeyTime > 0) {
                    beginPrefix = String.valueOf(latestKeyTime);
                }
            } catch (Throwable e) {
                log.error("delayTake error:{}", e);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("delayTake sleep error:{}", e);
            }
        }
    }

    public String getKey(long currSecond, long suffix) {
        String key = currSecond + "_" + suffix;
        return key;
    }
}
