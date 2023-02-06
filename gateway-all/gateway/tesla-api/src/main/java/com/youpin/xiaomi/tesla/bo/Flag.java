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

package com.youpin.xiaomi.tesla.bo;

/**
 * @author goodjava@qq.com
 */
public class Flag {

    /**
     * 是否直接返回mock数据
     */
    public static final int ALLOW_MOCK = 1 << 0;

    /**
     * 是否允许cache
     */
    public static final int ALLOW_CACHE = 1 << 1;

    /**
     * 是否允许日志
     */
    public static final int ALLOW_LOG = 1 << 2;

    /**
     * 是否允许使用脚本
     */
    public static final int ALLOW_SCRIPT = 1 << 3;

    /**
     * 是否允许跨域
     */
    public static final int ALLOW_CORS = 1 << 4;

    /**
     * 是否允许使用token验证
     */
    public static final int ALLOW_TOKEN = 1 << 5;

    /**
     * 服务是否下线(true 是下线 默认是false)
     */
    public static final int OFF_LINE = 1 << 6;

    /**
     * 允许验权
     */
    public static final int ALLOW_AUTH = 1 << 7;

    /**
     * 使用QPS限制
     */
    public static final int USE_QPS_LIMIT = 1 << 8;

    /**
     * 是否基于ip防刷
     */
    public static final int IP_ANTI_BRUSH = 1 << 9;

    /**
     * 是否基于uid防刷
     */
    public static final int UID_ANTI_BRUSH = 1 << 10;

    /**
     * 是否允许解析返回结果错误码解析
     */
    public static final int ALLOW_RESP_CODE_PARSE = 1 << 11;


    /**
     * 是否区分预发(内网直接转到预发环境)
     */
    public  static final int ALLOW_PREVIEW = 1 << 12;

    /**
     * 是否允许使用header
     */
    public static final int ALLOW_HEADER = 1 << 13;
}
