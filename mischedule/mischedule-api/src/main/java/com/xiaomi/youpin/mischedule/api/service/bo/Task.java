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


import lombok.Data;
import org.nutz.dao.entity.annotation.Id;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class Task implements Serializable {

    @Id
    private int id;

    private String bid;

    private String name;

    private String desc;

    private String cron;

    private String status;

    /**
     * 被回调执行的方式
     */
    private ExecuteType executeType;

    /**
     * 来源
     */
    private String source;


    /**
     * 参数(最好用json)
     */
    private String param;

}
