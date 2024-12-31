package io.opentelemetry.instrumentation.api.db;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("SystemOut")
public final class RedisCommandUtil {
    // don't perform the span export command
    public static final String REDIS_EXCLUDE_COMMAND = "PING|AUTH";

    // The operation quantity of these commands is too large, it needs to be skipped.
    private static final Set<String> SKIP_END_NAME = Stream.of("hget", "mget", "mset", "hmget", "hgetall", "get").collect(Collectors.toSet());

    // Retain spans that exceed this duration threshold, unit: ms.
    private static final int DURATION_THRESHOLD = 1000;

    // If it's in skipName and there are no errors, skip it directly.(issue #16)
    public static boolean skipEnd(String operationName, @Nullable Throwable error, long startTime) {
        return (null != operationName) && (SKIP_END_NAME.contains(operationName.toLowerCase()) && (null == error) && (redisDuration(startTime) < DURATION_THRESHOLD));
    }

    private static long redisDuration(long startTime){
        System.out.println("start time is : "+startTime);
        long l = System.currentTimeMillis() - startTime;
        System.out.println("duration is : "+l);
        return l;
    }
}
