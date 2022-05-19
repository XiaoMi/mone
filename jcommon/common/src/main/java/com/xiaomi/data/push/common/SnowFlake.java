package com.xiaomi.data.push.common;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Twitter的分布式自增ID雪花算法snowflake
 *
 * @author shanwenbang@xiaomi.com
 */
public class SnowFlake {

    //下面两个每个5位，加起来就是10位的工作机器id
    protected long workerId;       //机器id
    protected long datacenterId;   //数据中心id
    //12位的序列号
    protected long sequence;

    //初始时间戳(取一个离当前时间最近的[2021-04-01 00:00:00],从此时间戳开始够用69年)
    private static final long START_STMP = 1617206400000L;

    //长度为7位
    private long workerIdBits = 8L;
    private long datacenterIdBits = 2L;

    //最大值 255 [0,255]
    protected long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //最大值 3   [0,3]
    protected long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    //序列号id长度
    private long sequenceBits = 12L;
    //序列号最大值
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    //工作id需要左移的位数，12位
    private long workerIdShift = sequenceBits;
    //数据id需要左移位数 12+8=20位
    private long datacenterIdShift = sequenceBits + workerIdBits;
    //时间戳需要左移位数 12+8+2=22位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    //上次时间戳，初始值为负数
    protected long lastTimestamp = -1L;

    private final byte[] lock = new byte[0];

    public SnowFlake() {
        this.datacenterId = makeDatacenterId(maxDatacenterId);
        this.workerId = makeWorkerId(datacenterId, maxWorkerId);
        this.sequence = 0L;
    }

    public SnowFlake(long datacenterId, long workerId, long sequence) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0\r\n", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0\r\n", maxDatacenterId));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

    //下一个ID生成算法
    public long nextId() {
        synchronized (lock) {
            long timestamp = timeGen();

            //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
            if (timestamp < lastTimestamp) {
                System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        lastTimestamp - timestamp));
            }

            //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = getNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0;
            }

            //将上次时间戳值刷新
            updateLastTimestamp(timestamp);

            /**
             * 返回结果：
             * (timestamp - START_STMP) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
             * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
             * (workerId << workerIdShift) 表示将工作id左移相应位数
             * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
             * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
             */
            return ((timestamp - START_STMP) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        }
    }

    protected long makeWorkerId(long datacenterId, long maxWorkerId) {
        long workId = randomLong(maxDatacenterId);
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(datacenterId);

            String localIp = Utils.getIp();
            sb.append(localIp);

            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (null != name && !name.isEmpty()) {
                /*
                 * GET jvmPid
                 */
                sb.append(name.split("@")[0]);
            }
            // 随机数
            sb.append(randomLong(maxDatacenterId));
            /*
             * IP + PID + 随机数 的 hashcode 获取16个低位
             */
            workId = (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
        } catch (Exception e) {
            System.out.println("makeWorkerId Exception:" + e.getMessage());
        }

        return workId;
    }

    /**
     * 扩展方法
     * 从远程redis/mysql等 恢复最新时间戳
     */
    protected long recoverLastTimestamp() {
        return -1L;
    }

    private void updateLastTimestamp(long newTime) {
        this.lastTimestamp = newTime;
        storeLastTimestamp(lastTimestamp);
    }

    private static AtomicLong STORE_CNT = new AtomicLong();

    /**
     * 扩展方法
     * 保存最新时间戳，保存至redis/mysql等
     */
    protected void storeLastTimestamp(long lastTimestamp) {
        STORE_CNT.addAndGet(1);
        return;
    }

    private long randomLong(long maxId) {
        return ThreadLocalRandom.current().nextLong(0, maxId);
    }

    protected long makeDatacenterId(long maxDatacenterId) {
        long datacenterId = 0L;

        return datacenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    //获取时间戳，并与上次时间戳比较
    private long getNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
            // 如果出现时钟回拨>3s，直接抛异常
            if (lastTimestamp - timestamp > 3 * 1000) {
                throw new RuntimeException(String.format("clock is moving backwards.  Rejecting requests until %s.", lastTimestamp));
            }
        }
        return timestamp;
    }

    //获取系统时间戳
    private long timeGen() {
        return System.currentTimeMillis();
    }

}