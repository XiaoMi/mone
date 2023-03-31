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
@Comment("milog日志收集")
@Data
@EqualsAndHashCode
public class MilogLogTailDo extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "tail")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("应用别名")
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
    @Comment("应用表id")
    private Long milogAppId;

    @Column(value = "app_id")
    @ColDefine(type = ColType.INT)
    @Comment("应用id")
    private Long appId;

    @Column(value = "app_name")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("应用名")
    private String appName;

    @Column(value = "app_type")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("0.mione项目 1.mis项目")
    private Integer appType;

    @Column(value = "env_id")
    @ColDefine(type = ColType.INT)
    @Comment("环境Id")
    private Long envId;

    @Column(value = "env_name")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("环境名称")
    private String envName;

    @Column(value = "machine_type")
    @ColDefine(type = ColType.INT)
    @Comment("mis应用 机器类型 0.容器 1.物理机")
    private Integer machineType;

    @Column(value = "ips")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("tail 采集ip列表(appId+envId所对应的ip列表)")
    @JsonField
    private List<String> ips;

    @Column(value = "motor_rooms")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("mis 应用机房信息")
    @JSONField
    private List<MotorRoomDTO> motorRooms;

    @Column(value = "parse_type")
    @ColDefine(type = ColType.INT)
    @Comment("日志解析类型：1:服务应用日志，2.分隔符，3：单行，4：多行，5：自定义")
    private Integer parseType;

    @Column(value = "parse_script")
    @ColDefine(type = ColType.TEXT)
    @Comment("对于分隔符，该字段指定分隔符；对于自定义，该字段指定日志读取脚本")
    private String parseScript;

    @Column(value = "log_path")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("逗号分割，多个日志文件路径,e.g.:/home/work/log/xxx/server.log")
    private String logPath;

    @Column(value = "log_split_express")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("日志切分表达式")
    private String logSplitExpress;

    @Column(value = "value_list")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("value列表，多个用逗号分隔")
    private String valueList;

    @Column(value = "filter")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("filterDefine")
    @JSONField
    private List<FilterDefine> filter;

    @Column(value = "deploy_way")
    @ColDefine(type = ColType.INT)
    @Comment("部署方式：1-mione; 2-miline;")
    private Integer deployWay;

    @Column(value = "first_line_reg")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("行首正则匹配表达式")
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
