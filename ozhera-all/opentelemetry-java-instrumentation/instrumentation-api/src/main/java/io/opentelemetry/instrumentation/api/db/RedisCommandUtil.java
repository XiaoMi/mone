package io.opentelemetry.instrumentation.api.db;

public class RedisCommandUtil {
    // 不进行span导出的命令
    public static final String REDIS_EXCLUDE_COMMAND = "PING|AUTH";
}
