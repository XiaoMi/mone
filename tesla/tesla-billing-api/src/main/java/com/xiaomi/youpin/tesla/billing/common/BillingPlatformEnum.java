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

package com.xiaomi.youpin.tesla.billing.common;

/**
 * @description: 计费平台枚举
 * @author zhenghao
 *
 */
public enum BillingPlatformEnum {
    // idc机房
    IDC_COMPUTER_ROOM(1),

    // 云平台
    CLOUD_COMPUTER_ROOM(2),
    ;

    private int code;

    BillingPlatformEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
