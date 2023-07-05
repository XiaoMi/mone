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
@Comment("应用app与中间件配置关联表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilogAppMiddlewareRel extends BaseCommon implements Serializable {

    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "milog_app_id")
    @ColDefine(customType = "bigint")
    @Comment("milog app表主键")
    private Long milogAppId;

    @Column(value = "middleware_id")
    @ColDefine(customType = "bigint")
    @Comment("中间件配置表ID")
    private Long middlewareId;

    @Column(value = "tail_id")
    @ColDefine(customType = "bigint")
    @Comment("采集日志路径tailId")
    private Long tailId;

    @Column(value = "config")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("配置信息，json格式")
    @JsonField
    private Config config;

    @Data
    public static class Config implements Serializable {

        private String topic;

        private String consumerGroup;

        private String tag;

        private Integer partitionCnt;

        /**
         * es消费group，后续可以扩展其它group用于其它分析场景
         */
        private String esConsumerGroup;

        private Integer batchSendSize;

    }

}
