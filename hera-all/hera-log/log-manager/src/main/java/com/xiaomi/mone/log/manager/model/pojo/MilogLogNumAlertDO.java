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

@Data
@TableName("milog_log_num_alert")
public class MilogLogNumAlertDO {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String day;

    private Long number;

    private Long appId;

    private String appName;

    private String alertUser;

    private Long ctime;

    public MilogLogNumAlertDO(String day, Long number, Long appId, String appName, String alertUser, Long ctime) {
        this.day = day;
        this.number = number;
        this.appId = appId;
        this.appName = appName;
        this.alertUser = alertUser;
        this.ctime = ctime;
    }
}
