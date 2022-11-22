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

package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.data.push.micloud.bo.response.CatalystResponse;
import com.xiaomi.data.push.micloud.bo.response.OrderDetail;
import org.nutz.dao.entity.annotation.*;
import lombok.Data;

/**
 * @author zhangjunyi
 * created on 2020/6/24 3:14 下午
 */
@Data
@Table(value = "apply_machine")
public class ApplyMachineBo {

    @Id
    private long id;

    @Column("order_id")
    private long orderId;

    @Column("order_res")
    private String orderRes;

    @Column
    private String creator;

    @Column("suit_id")
    private String suitId;

    @Column("site_id")
    private String siteId;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("order_detail")
    private OrderDetail orderDetail;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("catalyst_res")
    private CatalystResponse catalystResponse;

    @Column("init_seq")
    private String initSequence;

    @Column("env")
    private String env;

    @Column
    private int status;

    @Column
    private long ctime;

    @Column
    private long utime;
}