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

package com.xiaomi.youpin.quota.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * @author goodjava@qq.com
 * 配额的申请
 * <p>
 * 一个envId 对应一个配额申请
 */
@Table("quota_request")
@Data
public class QuotaRequest {

    @Id
    private int id;

    /**
     * 业务id(要保障唯一)
     */
    @Column("biz_id")
    private long bizId;

    /**
     * owner
     */
    @Column("owner")
    private String owner;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 申请的实例数
     */
    @Column
    private int num;

    @Column
    private int cpu;

    @Column
    private long mem;

    @Column("ports")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> ports;

    /**
     * 申请的配额
     */
    @Column
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> quotas;


}
