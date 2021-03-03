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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author dingpei
 */
public enum RecordingStrategyEnum {

    //按百分比录制
    PERCENTAGE(1, "按百分比录制"),

    //按uid录制
    UID(2, "按USER_ID录制"),

    //按header录制
    HEADER(3, "按header录制"),

    //按参数录制
    PARAM(4, "按参数录制");

    private int code;
    private String des;

    public static final int PERCENTAGE_CODE = 1;
    public static final int UID_CODE = 2;
    public static final int HEADER_CODE = 3;
    public static final int PARAM_CODE = 4;

    public static final List<Integer> RECODRING_STRATEGY_CODES = Arrays.asList(PERCENTAGE_CODE, UID_CODE, HEADER_CODE, PARAM_CODE);

    RecordingStrategyEnum(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public int getCode() {
        return code;
    }

}
