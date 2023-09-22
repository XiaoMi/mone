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
package com.xiaomi.mone.log.manager.common.context;

import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/14 10:58
 */
@Slf4j
public class MilogUserUtil {

    /**
     * @return
     */
    public static boolean isOne() {
        try {
            return StringUtils.equalsIgnoreCase(MoneUserContext.getCurrentUser().getZone(), ProjectSourceEnum.ONE_SOURCE.getSource());
        } catch (Exception e) {
            log.error("query user zone error,is one", e);
        }
        return false;
    }

    /**
     * @return
     */
    public static boolean isTwo() {
        try {
            return StringUtils.equalsIgnoreCase(MoneUserContext.getCurrentUser().getZone(), ProjectSourceEnum.TWO_SOURCE.getSource());
        } catch (Exception e) {
            log.error("query user zone error, is two", e);
        }
        return false;
    }
}
