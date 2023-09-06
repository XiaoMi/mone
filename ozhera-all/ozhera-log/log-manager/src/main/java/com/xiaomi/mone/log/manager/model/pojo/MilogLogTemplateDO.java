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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * milog Log templates
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_log_template")
public class MilogLogTemplateDO implements Serializable {

    /**
     * Primary key Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * create time
     */
    private Long ctime;

    /**
     * update time
     */
    private Long utime;

    /**
     * Log template name
     */
    private String templateName;

    /**
     * Log template type 0 - Custom log; 1-app; 2-nginx ...
     */
    private Integer type;

    private String supportArea;

    /**
     * sort
     */
    private Integer orderCol;
    /**
     * Whether self-sustaining consumption, 1. Support
     */
    private Integer supportedConsume;

}
