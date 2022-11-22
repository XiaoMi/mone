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

package com.youpin.xiaomi.tesla.plugin.bo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author goodjava@qq.com
 */
public class Request extends HashMap<String, Object> implements Serializable {

    public String getCmd() {
        return this.get("cmd").toString();
    }


    public long getBeginTime() {
        return Long.valueOf(this.get("beginTime").toString());
    }


    public String getTraceId() {
        return this.get("traceId").toString();
    }


    public String getUid() {
        return this.get("uid").toString();
    }

}
