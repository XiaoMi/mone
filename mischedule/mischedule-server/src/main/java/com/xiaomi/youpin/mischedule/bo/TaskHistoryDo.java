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

package com.xiaomi.youpin.mischedule.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
@Data
@Table("task_history")
public class TaskHistoryDo {

    @Column("task_Iid")
    private long taskId;

    @Column("task_content")
    private String taskContent;

    @Column("ctime")
    private long ctime;

    @Column("status")
    private int status;

    @Column("uid")
    private String uid;
}
