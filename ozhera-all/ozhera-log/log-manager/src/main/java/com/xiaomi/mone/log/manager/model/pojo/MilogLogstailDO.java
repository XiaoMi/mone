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
 *
 * </p>
 *
 * @author wanghaoyang
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("milog_logstail")
public class MilogLogstailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Creation time
     */
    private Long ctime;

    /**
     * Update time
     */
    private Long utime;

    /**
     * creator
     */
    private String creator;

    /**
     * updater
     */
    private String updater;

    /**
     * spaceId
     */
    private Long spaceId;

    /**
     * storeId
     */
    private Long storeId;

    /**
     * Milog table primary key
     */
    private Long milogAppId;

    /**
     * App ID
     */
    private Long appId;

    /**
     * App name
     */
    private String appName;

    /**
     * 0.mione project
     */
    private Integer appType;

    /**
     * machine type 0. Container 1. Physical machine
     */
    private Integer machineType;

    /**
     * environment Id
     */
    private Integer envId;

    /**
     * environment name
     */
    private String envName;

    /**
     * Log parsing type: 1: service application log, 2. delimiter, 3: single line, 4: multiple line, 5: custom
     */
    private Integer parseType;

    /**
     * For delimiter, the field specifies the delimiter; For customization, this field specifies the log read script
     */
    private String parseScript;

    /**
     * Comma split, multiple log file paths,e.g.:/home/work/log/xxx/server.log
     */
    private String logPath;

    /**
     * A list of values, multiple separated by commas
     */
    private String valueList;

    /**
     * ip list
     */
    private String ips;

    /**
     *  Apply the information of the computer room
     */
    private String motorRooms;

    /**
     * App aliases
     */
    private String tail;

    /**
     * Filter configuration
     */
    private String filter;

    /**
     * applies the index configuration
     */
    private String enEsIndex;


}
