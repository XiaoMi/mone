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

import com.alibaba.fastjson.annotation.JSONField;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import com.xiaomi.mone.log.manager.model.dto.MotorRoomDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.json.JsonField;

import java.util.List;

/**
 * @author shanwb
 * @date 2021-06-28
 */

@Table("milog_logstail")
@Comment("Milog log collection")
@Data
@EqualsAndHashCode
public class MilogLogTailDo extends BaseCommon {
    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "tail")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("App aliases")
    private String tail;

    @Column(value = "space_id")
    @ColDefine(customType = "bigint")
    @Comment("spaceId")
    private Long spaceId;

    @Column(value = "store_id")
    @ColDefine(customType = "bigint")
    @Comment("storeId")
    private Long storeId;

    @Column(value = "milog_app_id")
    @ColDefine(type = ColType.INT)
    @Comment("Application table ID")
    private Long milogAppId;

    @Column(value = "app_id")
    @ColDefine(type = ColType.INT)
    @Comment("App ID")
    private Long appId;

    @Column(value = "app_name")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("App name")
    private String appName;

    @Column(value = "app_type")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("0.mione project")
    private Integer appType;

    @Column(value = "env_id")
    @ColDefine(type = ColType.INT)
    @Comment("environment Id")
    private Long envId;

    @Column(value = "env_name")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("environment name")
    private String envName;

    @Column(value = "machine_type")
    @ColDefine(type = ColType.INT)
    @Comment("Machine Type 0. Container 1. Physical machine")
    private Integer machineType;

    @Column(value = "ips")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("tail Collect IP list (appId + envId corresponding to IP list)")
    @JsonField
    private List<String> ips;

    @Column(value = "motor_rooms")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("Apply the information of the computer room")
    @JSONField
    private List<MotorRoomDTO> motorRooms;

    @Column(value = "parse_type")
    @ColDefine(type = ColType.INT)
    @Comment("Log parsing type: 1: service application log, 2. delimiter, 3: single line, 4: multiple line, 5: custom")
    private Integer parseType;

    @Column(value = "parse_script")
    @ColDefine(type = ColType.TEXT)
    @Comment("For delimiter, the field specifies the delimiter; For customization, this field specifies the log read script")
    private String parseScript;

    @Column(value = "log_path")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("Comma split, multiple log file paths,e.g.:/home/work/log/xxx/server.log")
    private String logPath;

    @Column(value = "log_split_express")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("Log slicing expression")
    private String logSplitExpress;

    @Column(value = "value_list")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("A list of values, multiple separated by commas")
    private String valueList;

    @Column(value = "filter")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("filterDefine")
    @JSONField
    private List<FilterDefine> filter;

    @Column(value = "deploy_way")
    @ColDefine(type = ColType.INT)
    @Comment("Deployment method：1-mione; 2-miline;")
    private Integer deployWay;

    @Column(value = "deploy_space")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("The deployment space corresponding to the service")
    private String deploySpace;

    @Column(value = "first_line_reg")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("A regular match expression at the beginning of a row")
    private String firstLineReg;

    public MilogLogTailDo() {
    }

    public MilogLogTailDo(Long spaceId, Long storeId, Long appId, Integer parseType, String parseScript, String logPath, String valueList) {
        this.spaceId = spaceId;
        this.storeId = storeId;
        this.appId = appId;
        this.parseType = parseType;
        this.parseScript = parseScript;
        this.logPath = logPath;
        this.valueList = valueList;
    }

}
