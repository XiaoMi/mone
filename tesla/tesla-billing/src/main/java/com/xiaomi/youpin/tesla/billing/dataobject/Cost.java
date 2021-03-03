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
 * @date 2020/8/4
 * 消费表(service 服务维度)
 */
@Table("tesla_billing_cost")
@Data
public class Cost {

    @Id
    private int id;

    /**
     * 账户
     */
    @Column("account_id")
    private int accountId;

    /**
     * 产品
     */
    @Column("product_id")
    private int productId;

    /**
     * 资源
     */
    @Column("resource_id")
    private int resourceId;

    /**
     * 0 正常
     * 1 关闭
     */
    @Column
    private int status;

    @Column
    private int version;

    @Column
    private long ctime;

    @Column
    private long utime;


    @Column("begin_time")
    private long beginTime;

    @Column("end_time")
    private long endTime;


    /**
     * 使用的cpu数量
     */
    @Column
    private int useCpuNum;


    /**
     * 总的cpu数量
     */
    @Column
    private int cpuNum;


    @Column("biz_id")
    private long bizId;


    @Column("sub_biz_id")
    private long subBizId;


}
