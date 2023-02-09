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

package com.xiaomi.youpin.tesla.bug.domain;

import lombok.Data;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * @date 2020/9/5
 */
@Table("bug_record")
@Data
public class Record {

    @Id
    private int id;

    private String request;

    private String response;

    private long ctime;

    private long utime;

    private int status;

    private int version;

    private int bizId;

    /**
     * 批次
     */
    private String batch;

    /**
     * http dubbo
     */
    private int type;

    private String requestMeta;

}
