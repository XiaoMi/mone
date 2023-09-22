package com.xiaomi.hera.trace.etl.es.queue.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaomi.data.push.common.SnowFlake;
import com.xiaomi.hera.trace.etl.es.util.redis.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class TeSnowFlake extends SnowFlake implements InitializingBean {

    private static final long BATCH_CNT = 77L;
    private static final String SN_KEY_PREFIX = "te_sn";
    private static final String SN_KEY_STMP = SN_KEY_PREFIX + "_last_timestamp";

    public static final String FIRST_TIMESTAMP_REDIS_PREFIX = "first_new";
    public static final String SECOND_TIMESTAMP_REDIS_PREFIX = "second_new";

    private static AtomicLong STORE_CNT = new AtomicLong(0L);
    private static AtomicLong SAVED_LAST_STMP = new AtomicLong(-1L);
    public static String SN_KEY_WORK_ID;

    private String mId;
    @Autowired
    private RedisClientUtil redis;

    @Value("${gw.snowflake.datacenterId:0}")
    private long datacenterId;

    @PostConstruct
    public void init() {
        super.datacenterId = makeDatacenterId1(maxDatacenterId);
        super.workerId = makeWorkerId1(datacenterId, maxWorkerId);
        // Restore last time from redis
        afterPropertiesSet();
        // Get machine number from environment variables.
        String podName = System.getenv("MONE_CONTAINER_S_POD_NAME");
        if (StringUtils.isEmpty(podName)) {
            log.error("this pod con't get podName!");
            throw new RuntimeException("this pod con't get podName!");
        }
        mId = podName.substring(podName.lastIndexOf("-") + 1);
    }

    private long makeDatacenterId1(long maxDatacenterId) {
        long did = this.datacenterId % (maxDatacenterId + 1);
        log.warn("GwSnowFlake, datacenterId:{}, origin_datacenterId:{}, maxDatacenterId:{}", did, datacenterId, maxDatacenterId);
        return did;
    }

    /**
     * Using nginx for workId selection.
     *
     * @param datacenterId
     * @param maxWorkerId
     * @return
     */
    private long makeWorkerId1(long datacenterId, long maxWorkerId) {
        for (int i = 0; i < maxWorkerId; i++) {
            long workId = super.makeWorkerId(datacenterId, maxWorkerId);
            SN_KEY_WORK_ID = SN_KEY_PREFIX + "_workId" + workId;

            Long setNx = redis.setNx(SN_KEY_WORK_ID, "1");
            if (1L == setNx.longValue()) {
                log.warn("GwSnowFlake workId:{}", workId);
                return workId;
            }
        }

        long lastWorkId = super.makeWorkerId(datacenterId, maxWorkerId);
        log.error("GwSnowFlake have no workId available, maxWorkerId:{}, lastWorkId:{}", maxWorkerId, lastWorkId);
        return lastWorkId;
        //throw new RuntimeException("GwSnowFlake have no workId available, maxWorkerId:"+maxWorkerId);
    }


    public String recoverLastTimestamp(String keyPrefix) {
        try {
            String value = redis.get(keyPrefix + "_" + SN_KEY_STMP + "_" + mId);
            if (StringUtils.isEmpty(keyPrefix)) {
                return null;
            } else {
                return value;
            }
        } catch (Exception e) {
            log.error("recoverLastTimestamp exception:{}", e);
            return null;
        }
    }

    /**
     * extension method
     * Save the latest timestamp and save it to Redis, etc.
     */
    public void storeLastTimestamp(String keyPrefix, String lastRocksKey) {
        long cnt = STORE_CNT.addAndGet(1);
        long lastTimestamp = Long.parseLong(lastRocksKey.split("_")[0]);
        boolean needSave = false;
        // If the request has been made 77 times or the time since the last save exceeds 5 seconds, then execute the Redis save operation.
        if (cnt % BATCH_CNT == 0) {
            needSave = true;
        } else if (lastTimestamp - SAVED_LAST_STMP.get() > 5000) {
            needSave = true;
        }

        if (needSave) {
            redis.set(keyPrefix + "_" + SN_KEY_STMP + "_" + mId, lastRocksKey);
            SAVED_LAST_STMP.set(lastTimestamp);
        }
    }

    @Override
    public void afterPropertiesSet() {
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("refer", new ArrayList<>());
        System.out.println(json.toJSONString());
    }
}
