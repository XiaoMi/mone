package com.xiaomi.youpin.gateway.function.imp;

import com.xiaomi.data.push.common.SnowFlake;
import com.xiaomi.youpin.gateway.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class GwSnowFlake extends SnowFlake implements InitializingBean {

    private static final long BATCH_CNT = 77L;
    private static final String SN_KEY_PREFIX = "gw_sn";
    private static final String SN_KEY_STMP = SN_KEY_PREFIX + "_last_timestamp";

    private static AtomicLong STORE_CNT = new AtomicLong(0L);
    private static AtomicLong SAVED_LAST_STMP = new AtomicLong(-1L);
    private static String SN_KEY_WORK_ID;

    private Redis redis;

    //数据中心ID
    @Value("${gw.snowflake.datacenterId:0}")
    private long datacenterId;

    public GwSnowFlake(@Autowired Redis redisImp) {
        this.redis = redisImp;
        super.datacenterId = makeDatacenterId1(maxDatacenterId);
        super.workerId = makeWorkerId1(datacenterId, maxWorkerId);
        //从redis恢复上次时间
        super.lastTimestamp = recoverLastTimestamp();
    }

    private long makeDatacenterId1(long maxDatacenterId) {
        long did = this.datacenterId % (maxDatacenterId + 1);
        log.warn("GwSnowFlake, datacenterId:{}, origin_datacenterId:{}, maxDatacenterId:{}", did, datacenterId, maxDatacenterId);
        return did;
    }

    /**
     * 利用nginx进行workId选择
     * @param datacenterId
     * @param maxWorkerId
     * @return
     */
    private long makeWorkerId1(long datacenterId, long maxWorkerId) {
        for(int i=0; i<maxWorkerId; i++) {
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


    @Override
    public long recoverLastTimestamp() {
        try {
            String value = redis.get(SN_KEY_STMP);
            if (null == value || value.trim().length() == 0) {
                return -1L;
            } else {
                return Long.valueOf(value);
            }
        } catch (Exception e) {
            log.error("recoverLastTimestamp exception:{}", e);
            return -1;
        }
    }

    /**
     * 扩展方法
     * 保存最新时间戳，保存至redis等
     */
    @Override
    public void storeLastTimestamp(long lastTimestamp) {
        long cnt = STORE_CNT.addAndGet(1);

        boolean needSave = false;
        // 请求77次或者距离上次保存超过5秒时间 则执行redis保存
        if (cnt % BATCH_CNT == 0) {
            needSave = true;
        } else if (lastTimestamp - SAVED_LAST_STMP.get() > 5000) {
            needSave = true;
        }

        if (needSave) {
            String value = String.valueOf(lastTimestamp);
            redis.set(SN_KEY_STMP, value);
            SAVED_LAST_STMP.set(lastTimestamp);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            redis.del(SN_KEY_WORK_ID);
            log.warn("GwSnowFlake delete key:{} success", SN_KEY_WORK_ID);
        }));
    }
}
