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
 * @description: 计费操作枚举
 * @author zhenghao
 *
 */
public enum BillingOperationTypeEnum {
    // 云平台包年包月 和 按分钟
    // idc 按分钟
    // 按分钟才有 升配 降配 启动服务 停止服务

    // 创建资源
    CREATE_RESOURCE(1),
    // 销毁资源
    DESTROY_RESOURCE(2),
    // 升配 针对按分钟计费
    RISE_RESOURCE(3),
    // 降配 针对按分钟计费
    DROP_RESOURCE(4),
    // 启动服务 针对按分钟计费
    START_RESOURCE(5),
    // 停止服务 针对按分钟计费
    STOP_RESOURCE(6),
    ;

    private int code;

    BillingOperationTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
