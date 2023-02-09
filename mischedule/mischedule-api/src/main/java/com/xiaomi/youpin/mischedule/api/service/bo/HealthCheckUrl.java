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

package com.xiaomi.youpin.mischedule.api.service.bo;

import com.xiaomi.youpin.mischedule.MethodInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class HealthCheckUrl implements Serializable {

    /**
     * http dubbo
     */
    private String type;

    /**
     * for http
     */
    private String url;

    /**
     * for dubbo
     */
    private MethodInfo methodInfo;


    private String ip;


    public static final String HTTP = "http";
    public static final String DUBBO = "dubbo";


}
