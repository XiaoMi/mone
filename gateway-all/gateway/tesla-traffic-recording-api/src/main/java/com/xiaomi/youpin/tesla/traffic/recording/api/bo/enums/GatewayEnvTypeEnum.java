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


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author dingpei
 */
public enum GatewayEnvTypeEnum implements Serializable {

    //外网网关
    ONLINE(1, "", "外网网关"),

    //内网网关
    INTRANET(2, "intranet", "内网网关"),

    //测试环境网关
    STAGING(3, "staging", "测试环境网关");


    private int code;
    private String env;

    private String desc;

    public static final int ONLINE_CODE = 1;
    public static final int INTRANET_CODE = 2;
    public static final int STAGING_CODE = 3;

    public static final List<Integer> GATEWAY_ENV_TYPE_CODES = Arrays.asList(ONLINE_CODE, INTRANET_CODE, STAGING_CODE);

    GatewayEnvTypeEnum(int code, String env, String desc) {
        this.code = code;
        this.env = env;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
