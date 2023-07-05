/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_search_save")
public class MilogLogSearchSaveDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long spaceId;

    private Long storeId;

    private Long tailId;

    private String queryText;

    /**
     * 1-保存了时间参数；0-没有保存
     */
    private Integer isFixTime;

    private Long startTime;

    private Long endTime;

    /**
     * 备注
     */
    private String common;

    private Integer sort;

    private Integer orderNum;

    private String creator;

    private String updater;

    private Long createTime;

    private Long updateTime;

}
