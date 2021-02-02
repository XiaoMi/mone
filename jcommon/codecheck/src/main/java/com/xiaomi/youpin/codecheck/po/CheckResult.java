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

package com.xiaomi.youpin.codecheck.po;

import lombok.Data;

@Data
public class CheckResult {
    String level;
    String name;
    String detailDesc;
    String chineseDesc;

    public CheckResult(String level, String name, String detailDesc, String chineseDesc) {
        this.level = level;
        this.name = name;
        this.detailDesc = detailDesc;
        this.chineseDesc = chineseDesc;
    }

    public static CheckResult getErrorRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_ERROR, name, detailDesc, chineseDesc);
    }

    public static CheckResult getWarnRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_WARN, name, detailDesc, chineseDesc);
    }

    public static CheckResult getInfoRes(String name, String detailDesc, String chineseDesc) {
        return new CheckResult(LEVEL_INFO, name, detailDesc, chineseDesc);
    }

    public static final String LEVEL_INFO = "[INFO]";
    public static final String LEVEL_WARN = "[WARN]";
    public static final String LEVEL_ERROR = "[ERROR]";

    public static final Integer INFO = 0;
    public static final Integer WARN = 10;
    public static final Integer ERROR = 20;

    public static Integer getIntLevel(String level) {
        if (level == null) {
            return INFO;
        }
        switch (level) {
            case LEVEL_INFO: return INFO;
            case LEVEL_WARN: return WARN;
            case LEVEL_ERROR: return ERROR;
            default: return INFO;
        }
    }

}
