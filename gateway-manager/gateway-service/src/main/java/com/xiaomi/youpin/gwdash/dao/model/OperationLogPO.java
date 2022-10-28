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

package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;


@Data
@Table("operation_log")
public class OperationLogPO {

    @Id
    private Long id;

    @Column("app_name")
    private String appName;

    @Column("user_name")
    private String userName;

    @Column("data_id")
    private String dataId;

    @Column("data_before")
    private String dataBefore;

    @Column("data_after")
    private String dataAfter;

    @Column("create_time")
    private long createTime;

    @Column("type")
    private int type;

    @Column("remark")
    private String remark;

}
