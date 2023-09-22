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
import lombok.*;
import org.nutz.dao.entity.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author shanwb
 * @date 2021-06-28
 */
@Table("milog_app_topic_rel")
@Comment("The application association table with MQ")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MilogAppTopicRelDO extends BaseCommon {
    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "tenant_id")
    @ColDefine(customType = "bigint")
    @Comment("Tenant ID")
    private Long tenantId;

    @Column(value = "iam_tree_id")
    @ColDefine(customType = "bigint")
    @Comment("IAM tree ID (unique to the mione app)")
    private Long iamTreeId;

    @Column(value = "app_id")
    @ColDefine(customType = "bigint")
    @Comment("app id")
    private Long appId;

    @Column(value = "app_name")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("app name")
    private String appName;

    @Column(value = "tree_ids")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("The IDS of the mounted tree of the project")
    private List<Long> treeIds;

    @Column(value = "node_ips")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("The IPS of the physical machine where the application resides")
    private LinkedHashMap<String, List<String>> nodeIPs;

    @Column(value = "operator")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("operator")
    private String operator;

    @Column(value = "source")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    @Comment("Service source")
    private String source;

    @Column(value = "type")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("0.mione project")
    private Integer type;


}
