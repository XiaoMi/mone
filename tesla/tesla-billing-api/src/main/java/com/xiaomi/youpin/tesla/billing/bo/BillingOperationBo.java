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

package com.xiaomi.youpin.tesla.billing.bo;

import com.xiaomi.youpin.tesla.billing.dataobject.BillingNorms;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhenghao
 * @description: TODO
 */
@Data
public class BillingOperationBo implements Serializable {

    /**
     * 保证唯一建议使用uuid
     */
    private String sendId;

    /**
     * 资源ids
     */
    private List<String> resourceIds;

    /**
     * 计费类型 1包年包月 2按分钟
     */
    private int billingType;

    /**
     * 0 服务  1 机器
     */
    private int subType;

    /**
     * 计费平台 1idc机房 2云平台
     */
    private int billingPlatform;

    /**
     * 计费操作 1创建资源 2销毁资源 3升配 针对按分钟计费 4降配 针对按分钟计费 5启动服务 针对按分钟计费 6停止服务 针对按分钟计费
     */
    private int billingOperation;

    /**
     * 环境 c3 c4 st等
     */
    private long environment;

    /**
     * 项目id
     */
    private long accountId;

    /**
     * 规格 json cpu memory disk 使用规格
     */
    private BillingNorms useNorms;

    /**
     * 规格 json cpu memory disk 总规格
     */
    private BillingNorms allNorms;

    /**
     * 操作时间 时间戳
     */
    private long operationTime;

    /**
     * 发送时间 时间戳
     */
    private long sendTime;


    private long bizId;

    private long subBizId;


    public enum SubType {
        service,
        machine
    }

}
