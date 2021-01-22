/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.common;

import lombok.extern.slf4j.Slf4j;

import java.time.*;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class TimeUtils {


    public static boolean moreThanOneHour(long time) {
        LocalDateTime ldt0 = millsToLocalDateTime(time);
        LocalDateTime ldt1 = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        log.debug(ldt0 + ":" + ldt1);
        return ldt1.isAfter(ldt0);
    }


    public static LocalDateTime millsToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }


}
