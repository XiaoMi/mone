package com.xiaomi.hera.trace.etl.util;

public class MessageUtil {

    // Message body separator
    public static final String SPLIT = " ### ";
    // rocksdb message separator
    public static final String ROCKS_SPLIT = " #### ";
    public static final String ERROR_CODE = "ERROR";
    // Corner mark of each field for easy modification
    public static final int START_TIME = 0;
    public static final int DURATION = 1;
    public static final int IP = 2;
    public static final int APPLICATION_NAME = 3;
    public static final int SPAN_NAME = 4;
    public static final int STATUS_CODE = 5;
    public static final int TRACE_ID = 6;
    public static final int SPAN_ID = 7;
    public static final int TAGS = 8;
    public static final int EVENTS = 9;
    public static final int REOUSCES = 10;
    public static final int REFERERNCES = 11;
    // Messages by the total number after splitting
    public static final int COUNT = 12;

    // Cache the redis key for serviceName and operationName
    public static final String TRACE_SERVICE_REDIS_KEY = "trace_service_";
    // Cache the expiration time of the redis key of serviceName and operationName
    public static final int TRACE_SERVICE_REDIS_KEY_EXPIRE = 60 * 60 * 24;
}
