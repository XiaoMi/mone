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

package com.xiaomi.youpin.tesla.billing.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * <p>
 * 资源使用记录
 */
@Data
public class ResourceRecord implements Serializable {


    /**
     * 价格
     */
    private long price;


    /**
     * 资源的唯一key
     * 机器就是ip
     */
    private String resourceKey;


}
