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

package com.xiaomi.data.push.rpc;

/**
 * Created by zhangzhiyong on 05/06/2018.
 */
public abstract class RpcCmd {

    public static final int sfileReq = 0;
    public static final int sfileRes = 1;


    public static final int sfileReq2 = 10;
    public static final int sfileRes2 = 11;

    /**
     * ping
     */
    public static final int pingReq = 1001;
    public static final int pingRes = 1002;

    public static final int mpPingReq = 1003;
    public static final int mpPingRes = 1004;


    /**
     * 获取客户端信息
     */
    public static final int getInfoReq = 2000;
    public static final int getInfoRes = 2001;


}
