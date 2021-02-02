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

package com.xiaomi.youpin.tesla.traffic.recording.daoobj;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Map;

@Data
@Table("tesla_tr_recording_config")
public class RecordingConfigDao {

    /**
     * 录制配置id
     */
    @Id
    private int id;

    /**
     * 配置名
     */
    @Column
    private String name;

    /**
     * 录制流量来源
     * 网关、dubbo
     */
    @Column("source_type")
    private int sourceType;

    //网关
    /**
     * 网关环境
     */
    @Column("env_type")
    private int envType;

    /**
     * 录制的网关的url
     */
    @Column
    private String url;

    @Column("service_name")
    private String serviceName;

    @Column("service_group")
    private String group;

    @Column
    private String methods;

    @Column
    private String version;

    /**
     * 录制策略
     * 按百分比录制、按header录制、按uid录制、按参数录制
     */
    @Column("recording_strategy")
    private int recordingStrategy;

    @Column
    private int percentage;

    @Column
    private long uid;

    @Column("headers")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> headers;

    /**
     * 状态：待录制 = 0、录制中 = 1
     */
    @Column
    private int status;

    /**
     * 流量保存时间
     */
    @Column("save_days")
    private int saveDays;

    @Column("create_time")
    private long createTime;

    @Column("update_time")
    private long updateTime;

    @Column("creator")
    private String creator;

    @Column("updater")
    private String updater;
}
