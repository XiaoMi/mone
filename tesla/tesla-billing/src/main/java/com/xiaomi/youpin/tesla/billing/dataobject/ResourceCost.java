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
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * 资源消费关联表
 */
@Table("tesla_billing_resource_cost")
@Data
public class ResourceCost {

    @Id
    private int id;

    @Column("resource_id")
    private int resourceId;

    /**
     * 资源的唯一id(机器是ip)
     */
    @Column("resource_key")
    @ColDefine(width = 100)
    private String resourceKey;

    @Column("cost_id")
    private int costId;

    /**
     * 可以认为是 projectId
     */
    @Column("biz_id")
    private long bizId;

    /**
     * 可以认为是 envId
     */
    @Column("sub_biz_id")
    private long subBizId;


    @Column
    private  int status;


}
