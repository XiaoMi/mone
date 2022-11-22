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

package com.xiaomi.youpin.gateway;

/**
 * @author goodjava@qq.com
 */
public class TeslaConstants {


    public static final String FrontHeaderSourceName = "X-Yp-App-Source";


    public static final String FrontHeaderToken = "X-Yp-App-Token";

    public static final String FrontHeaderTag = "dubbo.tag";

    public static final String Tag = "tag";


    /**
     * 这次访问的唯一id
     */
    public static final String TraceId = "X-Trace-Id";

    public static final String TeslaVersion = "X-tesla-version";

    /**
     * uid
     */
    public static final String FrontUid = "X-Yp-Uid";

    /**
     * 协议版本号
     */
    public static final String ProtocolVersion = "X-Yp-Protocol-Version";


    public static final String WebSocketPath = "/ws";

    /**
     * 预发版本
     */
    public static final String Preview = "preview";

    public static final String Path = "Path";

    public static final String SwimLaneGroupFlag = "X-slg-flag";
}
