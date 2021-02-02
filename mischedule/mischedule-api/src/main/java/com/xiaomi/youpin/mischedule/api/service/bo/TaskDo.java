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
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.io.Serializable;

/**
 * @author zhangjunyi
 * created on 2020/8/18 2:41 下午
 */
@Data
@Table("task")
public class TaskDo implements Serializable {

    @Id
    private int id;

    @Column
    private String name;

    @Column
    private long created;

    @Column
    private long updated;

    @Column
    private String params;


    @Column("retry_num")
    private int retryNum;


    @Column("next_retry_time")
    private long nextRetryTime;

    @Column("error_retry_num")
    private int errorRetryNum;

    @Column
    private int status;

    @Column
    private String context;

    @Column
    private String type;

    @Column
    private  Integer gid;

}
