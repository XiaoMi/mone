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
package run.mone.m78.service.service.invokeHistory;

import com.xiaomi.data.push.redis.Redis;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
public class RedisUtils {

    private static final String ProbotHotKey = "M78ProbotHotKey";

    public static final Integer ONE_DAY_SECONDS = 24 * 60 * 60;

    public static boolean acquireLock(Redis redis, String key0) {
        long daysAgo = getCurrentDateTimestamp();
        String key = key0 + "-" + daysAgo;
        long value = redis.incr(key);
        if (value == 1L) {
            redis.expire(key, 3600);
            return true;
        }
        return false;
    }

    public static long getCurrentDateTimestamp() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String getProbotHotKey(Long botId) {
        return ProbotHotKey + "-" +  getCurrentDateTimestamp() + "-" + botId;
    }
}
