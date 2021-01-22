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
 * 产品表
 */
@Data
@Table("tesla_billing_product")
public class Product {

    @Id
    private int id;

    @Column
    private String name;

    /**
     * 分为单位
     */
    @Column
    private long price;

    /**
     * 产品类型
     * 0 包月  1 按分钟
     */
    @Column
    private int type;


    public enum ProductType {
        //包月
        month,
        //按分钟计费
        minute,
    }


}
