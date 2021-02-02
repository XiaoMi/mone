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

package com.xiaomi.youpin.tesla.billing.dataobject;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * <p>
 * 资源操作表
 * 机器维度的,用来和云平台对账
 */
@Data
@Table("tesla_billing_resource_operating_record")
public class ResourceOperatingRecord {


    @Id
    private int id;

    @Column("resource_id")
    private int resourceId;


    @Column("resource_key")
    private String resourceKey;

    @Column("begin_time")
    private long beginTime;


    @Column("end_time")
    private long endTime;


    /**
     * 0 是开启  1 是关闭
     */
    @Column
    private int status;


}
