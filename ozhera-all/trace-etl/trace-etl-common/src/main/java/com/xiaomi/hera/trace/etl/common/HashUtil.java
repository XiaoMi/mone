package com.xiaomi.hera.trace.etl.common;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/6 4:42 pm
 */
public class HashUtil {

    public static int consistentHash(String traceId, int buckets) {
        HashCode hasCode = Hashing.murmur3_32().hashString(traceId, StandardCharsets.UTF_8);
        return Hashing.consistentHash(hasCode, buckets);
    }
}
