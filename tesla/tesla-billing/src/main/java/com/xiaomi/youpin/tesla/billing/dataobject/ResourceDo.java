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
 * @date 2020/8/4
 * 资源表
 */
@Table("tesla_billing_resource")
@Data
public class ResourceDo {

    @Id
    private int id;


    /**
     * 资源类型
     */
    @Column
    private int type;


    /**
     * 资源id
     * 比如机器就是ip
     */
    @Column("resource_key")
    @ColDefine(width = 100)
    private String resourceKey;


    /**
     * 资源开始时间
     */
    @Column("begin_time")
    private long beginTime;

    /**
     * 资源结束时间
     */
    @Column("end_time")
    private long endTime;


    /**
     * 资源状态
     */
    @Column
    private int status;

    /**
     * 产品id
     */
    @Column("product_id")
    private int productId;
}
