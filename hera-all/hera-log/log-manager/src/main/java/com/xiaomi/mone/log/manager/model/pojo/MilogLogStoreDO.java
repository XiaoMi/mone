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

package com.xiaomi.mone.log.manager.model.pojo;

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author shanwb
 * @date 2021-06-28
 */

@Table("milog_logstore")
@Comment("milog日志存储")
@Data
public class MilogLogStoreDO extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "space_id")
    @ColDefine(customType = "bigint")
    @Comment("spaceId")
    private Long spaceId;

    @Column(value = "mq_resource_id")
    @ColDefine(customType = "bigint")
    @Comment("mq资源的主键Id")
    private Long mqResourceId;


    @Column(value = "logstoreName")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("日志存储名称")
    private String logstoreName;


    @Column(value = "store_period")
    @ColDefine(type = ColType.INT)
    @Comment("存储周期:1-3-5-7")
    private Integer storePeriod;


    @Column(value = "shard_cnt")
    @ColDefine(type = ColType.INT)
    @Comment("存储分片数")
    private Integer shardCnt;

    @Column(value = "key_list")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("key列表，多个用逗号分隔")
    private String keyList;

    @Column(value = "column_type_list")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("字段类型，多个用逗号分隔")
    private String columnTypeList;

    @Column(value = "log_type")
    @ColDefine(type = ColType.INT)
    @Comment("1:app,2:ngx..")
    private Integer logType;

    @Column(value = "is_matrix_app")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("false")
    private Boolean isMatrixApp;

    @Column(value = "es_index")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("es index:milog_logstoreName")
    private String esIndex;

    @Column(value = "es_cluster_id")
    @ColDefine(customType = "bigint")
    @Comment("logstore 对应es集群id")
    private Long esClusterId;

    @Column(value = "machine_room")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("机房信息")
    private String machineRoom;

    public MilogLogStoreDO() {
    }

    public MilogLogStoreDO(Long spaceId, String logstoreName, Integer storePeriod, Integer shardCnt, String keyList, Integer logType, Boolean isMatrixApp) {
        this.logstoreName = logstoreName;
        this.spaceId = spaceId;
        this.storePeriod = storePeriod;
        this.shardCnt = shardCnt;
        this.keyList = keyList;
        this.logType = logType;
        this.isMatrixApp = isMatrixApp;
    }

    public boolean isMatrixAppStore() {
        return this.isMatrixApp == null ? false : this.isMatrixApp;
    }

}
