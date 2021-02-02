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

package com.xiaomi.youpin.tesla.agent.cmd;


/**
 * @author goodjava@qq.com
 */
public class AgentCmd {


    public static final int dockerReq = 3000;
    public static final int dockerRes = 3001;


    public static final int fileReq = 4000;
    public static final int fileRes = 4001;


    public static final int shellReq = 5000;
    public static final int shellRes = 5001;


    public static final int debugReq = 6000;
    public static final int debugRes = 6001;

    public static final int serviceReq = 7000;
    public static final int serviceRes = 7001;


    public static final int godReq = 8000;
    public static final int godRes = 8001;

    public static final int notifyMsgReq = 9000;
    public static final int notifyMsgRes = 9001;


    public static final int nginxReq = 9010;
    public static final int nginxRes = 9011;

    public static final int managerReq = 9020;
    public static final int managerRes = 9021;


    public static final int logReq = 9030;
    public static final int logRes = 9031;
}
