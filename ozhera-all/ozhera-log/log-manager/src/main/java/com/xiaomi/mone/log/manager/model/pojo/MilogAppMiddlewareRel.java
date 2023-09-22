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

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;
import org.nutz.json.JsonField;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/17 16:55
 */
@Table("milog_app_middleware_rel")
@Comment("The application and middleware configuration association table")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilogAppMiddlewareRel extends BaseCommon implements Serializable {

    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "milog_app_id")
    @ColDefine(customType = "bigint")
    @Comment("milog app talble Primary key Id")
    private Long milogAppId;

    @Column(value = "middleware_id")
    @ColDefine(customType = "bigint")
    @Comment("The middleware configuration table ID")
    private Long middlewareId;

    @Column(value = "tail_id")
    @ColDefine(customType = "bigint")
    @Comment("Collect the trail ID of the log path")
    private Long tailId;

    @Column(value = "config")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("Configuration information, in JSON format")
    @JsonField
    private Config config;

    @Data
    public static class Config implements Serializable {

        private String topic;

        private String consumerGroup;

        private String tag;

        private Integer partitionCnt;

        /**
         * es consumption group, which can be extended to other groups for other analysis scenarios
         */
        private String esConsumerGroup;

        private Integer batchSendSize;

    }

}
