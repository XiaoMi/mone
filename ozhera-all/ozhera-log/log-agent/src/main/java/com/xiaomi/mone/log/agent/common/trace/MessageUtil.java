/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.common.trace;

public class MessageUtil {

    // Message body delimiter
    public static final String SPLIT = " ### ";
    // rocksdb message delimiter
    public static final String ROCKS_SPLIT = " #### ";
    public static final String ERROR_CODE = "ERROR";
    // The corner labels of each field are easy to modify
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
    // The total number of messages after splitting
    public static final int COUNT = 12;

    // Redis keys used to cache serviceName and operationName
    public static final String TRACE_SERVICE_REDIS_KEY = "trace_service_";
    // Cache the expiration time of the redis key of serviceName and operationName
    public static final int TRACE_SERVICE_REDIS_KEY_EXPIRE = 60 * 60 * 24;
}
