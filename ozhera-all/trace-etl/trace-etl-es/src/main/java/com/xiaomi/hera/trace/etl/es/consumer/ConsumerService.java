package com.xiaomi.hera.trace.etl.es.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.es.domain.FilterResult;
import com.xiaomi.hera.trace.etl.es.domain.LocalStorages;
import com.xiaomi.hera.trace.etl.es.queue.impl.RocksdbStoreServiceImpl;
import com.xiaomi.hera.trace.etl.es.queue.impl.TeSnowFlake;
import com.xiaomi.hera.trace.etl.es.util.bloomfilter.TraceIdRedisBloomUtil;
import com.xiaomi.hera.trace.etl.service.WriteEsService;
import com.xiaomi.hera.trace.etl.util.ExecutorUtil;
import com.xiaomi.hera.trace.etl.util.MessageUtil;
import com.xiaomi.hera.trace.etl.util.ThriftUtil;
import com.xiaomi.hera.tspandata.TAttributeKey;
import com.xiaomi.hera.tspandata.TAttributes;
import com.xiaomi.hera.tspandata.TSpanData;
import com.xiaomi.hera.tspandata.TValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author dingtao
 * @Description
 * @date 2021/9/29 2:47 下午
 */
@Service
@Slf4j
public class ConsumerService {

    @Value("${rocks.first.gap}")
    private long firstGap;
    @Value("${rocks.second.gap}")
    private long secondGap;
    @Value("${rocks.first.path}")
    private String firstRocksPath;
    @Value("${rocks.second.path}")
    private String secondRocksPath;

    @NacosValue(value = "${trace.es.filter.isopen}", autoRefreshed = true)
    private boolean filterIsOpen;

    @Autowired
    private TraceIdRedisBloomUtil traceIdRedisBloomUtil;
    @Autowired
    private FilterService filterService;
    @Autowired
    private WriteEsService writeEsService;
    @Autowired
    private TeSnowFlake snowFlake;

    private RocksdbStoreServiceImpl firstRocksdbStoreService;
    private RocksdbStoreServiceImpl secondRocksdbStoreService;
    private StringBuilder firstBatchRocksMessage = new StringBuilder();
    private StringBuilder secondBatchRocksMessage = new StringBuilder();

    /**
     * Control the number of rocksDB messages stored in each batch 
	 * to prevent memory overflow caused by too many single key messages
     */
    private int firstCount = 0;
    private int secondCount = 0;
    private static final int BATCH_ROCKSDB_COUNT = 20;
    /**
     * The first lock is isolated from the second lock
     */
    private static final Object FIRST_LOCK = new Object();
    private static final Object SECOND_LOCK = new Object();

    @PostConstruct
    public void init() {
        if (filterIsOpen) {
            firstRocksdbStoreService = new RocksdbStoreServiceImpl(firstRocksPath, TeSnowFlake.FIRST_TIMESTAMP_REDIS_PREFIX);
            secondRocksdbStoreService = new RocksdbStoreServiceImpl(secondRocksPath, TeSnowFlake.SECOND_TIMESTAMP_REDIS_PREFIX);
            // Initialize the rocksdb task for the first time
            initFirstRocksTask();
            // Initializes the second read rocksdb task
            initSecondRocksTask();
        }
    }

    private void dealMessage(String order, String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        // The body of the message stored in Rocksdb is: traceId ### serviceName ### spanName ### TSpanData(String) #### ......
        String[] messages = message.split(MessageUtil.ROCKS_SPLIT);
        for (String oneMessage : messages) {
            String[] split = oneMessage.split(MessageUtil.SPLIT);
            TSpanData tSpanData = deserializeFromString(split[3]);
            if (tSpanData != null) {
                if (traceIdRedisBloomUtil.isExistLocal(split[0])) {
                    // write into es
                    writeEsService.insertJaegerSpan(tSpanData, split[1], split[2]);
                } else if (RocksdbStoreServiceImpl.FIRST_ORDER.equals(order)) {
                    insertRocks(split[0], split[1], split[2], tSpanData, RocksdbStoreServiceImpl.SECOND_ORDER);
                }
            }
        }
    }

    public void consumer(TSpanData tSpanData) {
        try {
            if (tSpanData == null) {
                log.error("tSpanData is null");
                return;
            }
            String status = tSpanData.getStatus().name();
            String heraContext = "";
            TAttributes attributes = tSpanData.getAttributes();
            List<TAttributeKey> tagsKeys = attributes.getKeys();
            List<TValue> tagsValues = attributes.getValues();
            if (tagsKeys != null && tagsValues != null && tagsKeys.size() > 0 && tagsKeys.size() != tagsValues.size()) {
                for (int i = 0; i < tagsKeys.size(); i++) {
                    String key = tagsKeys.get(i).getValue();
                    String value = ThriftUtil.getStringValue(tagsValues.get(i), tagsKeys.get(i).getType());
                    if (filterIsOpen) {
                        if ("span.hera_context".equals(key)) {
                            heraContext = value;
                        }
                    }
                }
            }
            String serviceName = "unknow-service";
            if (tSpanData.getExtra() != null && StringUtils.isNotEmpty(tSpanData.getExtra().getServiceName())) {
                serviceName = tSpanData.getExtra().getServiceName();
            }
            // filter
            String traceId = tSpanData.getTraceId();
            String spanName = tSpanData.getName();
            Long duration = tSpanData.getEndEpochNanos() - tSpanData.getStartEpochNanos();
            FilterResult filter = filterService.filterBefore(status, traceId, spanName, heraContext, serviceName, duration, tSpanData);
            if(filter.isDiscard()){
                return;
            }
            if (filter.isResult()) {
                if (filter.isAddBloom()) {
                    // inert bloomfilter
                    traceIdRedisBloomUtil.addBatch(traceId);
                }
                // write into es
                writeEsService.insertJaegerSpan(tSpanData, serviceName, spanName);
            } else {
                insertRocks(traceId, serviceName, spanName, tSpanData, RocksdbStoreServiceImpl.FIRST_ORDER);
            }
        } catch (Throwable e) {
            log.error("message parse error, message : " + tSpanData.toString(), e);
            return;
        }
    }

    private void insertRocks(String traceId, String serviceName, String spanName, TSpanData tSpanData, String order) {
        if (filterIsOpen) {
            if (RocksdbStoreServiceImpl.FIRST_ORDER.equals(order)) {
                synchronized (FIRST_LOCK) {
                    internatInset(traceId, serviceName, spanName, tSpanData, order);
                }
            } else if (RocksdbStoreServiceImpl.SECOND_ORDER.equals(order)) {
                synchronized (SECOND_LOCK) {
                    internatInset(traceId, serviceName, spanName, tSpanData, order);
                }
            }
        }
    }

    private void internatInset(String traceId, String serviceName, String spanName, TSpanData tSpanData, String order) {
        buildRocksDBMessage(traceId, serviceName, spanName, tSpanData, order);
        // Check the second level match
        long currSeconds = System.currentTimeMillis() / 1000;
        if (RocksdbStoreServiceImpl.FIRST_ORDER.equals(order)) {
            if (LocalStorages.firstCurrentSeconds != currSeconds || firstCount >= BATCH_ROCKSDB_COUNT) {
                String key = firstRocksdbStoreService.getKey(currSeconds, LocalStorages.firstRocksKeySuffix.addAndGet(1));
                firstRocksdbStoreService.put(key, firstBatchRocksMessage.toString().getBytes(StandardCharsets.UTF_8));
                firstBatchRocksMessage = new StringBuilder();
                LocalStorages.firstCurrentSeconds = currSeconds;
                firstCount = 0;
            }
        } else if (RocksdbStoreServiceImpl.SECOND_ORDER.equals(order)) {
            if (LocalStorages.secondCurrentSeconds != currSeconds || secondCount >= BATCH_ROCKSDB_COUNT) {
                String key = secondRocksdbStoreService.getKey(currSeconds, LocalStorages.secondRocksKeySuffix.addAndGet(1));
                secondRocksdbStoreService.put(key, secondBatchRocksMessage.toString().getBytes(StandardCharsets.UTF_8));
                secondBatchRocksMessage = new StringBuilder();
                LocalStorages.secondCurrentSeconds = currSeconds;
                secondCount = 0;
            }
        }
    }

    private void buildRocksDBMessage(String traceId, String serviceName, String spanName, TSpanData tSpanData, String order) {
        String serialize = serializeToString(tSpanData);
        if (serialize != null) {
            if (RocksdbStoreServiceImpl.FIRST_ORDER.equals(order)) {
                firstBatchRocksMessage.append(traceId).append(MessageUtil.SPLIT)
                        .append(serviceName).append(MessageUtil.SPLIT)
                        .append(spanName).append(MessageUtil.SPLIT)
                        .append(serialize).append(MessageUtil.ROCKS_SPLIT);
                firstCount++;
            } else if (RocksdbStoreServiceImpl.SECOND_ORDER.equals(order)) {
                secondBatchRocksMessage.append(traceId).append(MessageUtil.SPLIT)
                        .append(serviceName).append(MessageUtil.SPLIT)
                        .append(spanName).append(MessageUtil.SPLIT)
                        .append(serialize).append(MessageUtil.ROCKS_SPLIT);
                secondCount++;
            }
        }
    }

    private void initFirstRocksTask() {
        // Gets the timestamp of the last message read
        String firstKey = snowFlake.recoverLastTimestamp(TeSnowFlake.FIRST_TIMESTAMP_REDIS_PREFIX);
        final String firstLastRocksKey = firstKey == null ?
                System.currentTimeMillis() + "_" + LocalStorages.firstRocksKeySuffix.get() : firstKey;
        // The local message thread is read for the first time
        ExecutorUtil.submitRocksDBRead(() -> {
            try {
                firstRocksdbStoreService.delayTake(firstLastRocksKey, firstGap, new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) {
                        ExecutorUtil.submitDelayMessage(() -> {
                            try {
                                String firstRocksMes = new String(bytes);
                                dealMessage(RocksdbStoreServiceImpl.FIRST_ORDER, firstRocksMes);
                            } catch (Throwable t) {
                                log.error("deal first rocksdb message error : ", t);
                            }
                        });
                    }
                }, snowFlake);
            } catch (Throwable e) {
                log.error("first get Rocks message error : ", e);
            }
        });
    }

    private void initSecondRocksTask() {
        // Gets the timestamp of the last message read
        String secondKey = snowFlake.recoverLastTimestamp(TeSnowFlake.SECOND_TIMESTAMP_REDIS_PREFIX);
        final String secondLastRocksKey = secondKey == null ?
                System.currentTimeMillis() + "_" + LocalStorages.secondRocksKeySuffix.get() : secondKey;
        // The local message thread is read for the sencond time
        ExecutorUtil.submitRocksDBRead(() -> {
            try {
                secondRocksdbStoreService.delayTake(secondLastRocksKey, secondGap, new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) {
                        ExecutorUtil.submitDelayMessage(() -> {
                            try {
                                String firstRocksMes = new String(bytes);
                                dealMessage(RocksdbStoreServiceImpl.SECOND_ORDER, firstRocksMes);
                            } catch (Throwable t) {
                                log.error("deal second rocksdb message error : ", t);
                            }
                        });
                    }
                }, snowFlake);
            } catch (Throwable e) {
                log.error("second get Rocks message error : ", e);
            }
        });
    }


    private String serializeToString(TSpanData tSpanData) {
        try {
            byte[] serialize = new TSerializer(ThriftUtil.PROTOCOL_FACTORY).serialize(tSpanData);
            return new String(serialize, StandardCharsets.ISO_8859_1);
        } catch (Throwable e) {
            log.error("rocksDB serializer serialize error");
        }
        return null;
    }

    private TSpanData deserializeFromString(String decode) {
        try {
            TSpanData tSpanData = new TSpanData();
            // The ISO-8859-1 encoding prevents byte[] inconsistency caused by extra character set processing when byte[] is converted to String, resulting in missing thrift deserialization fields
            new TDeserializer(ThriftUtil.PROTOCOL_FACTORY).deserialize(tSpanData, decode.getBytes(StandardCharsets.ISO_8859_1));
            return tSpanData;
        } catch (Throwable e) {
            log.error("rocksDB deserializer deserialize error");
        }
        return null;
    }
}
