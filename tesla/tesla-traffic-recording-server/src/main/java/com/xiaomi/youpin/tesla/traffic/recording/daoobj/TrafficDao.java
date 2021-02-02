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

import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.DubboTraffic;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.HttpTraffic;
import lombok.Cleanup;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Map;

@Data
@Table("tesla_tr_traffic")
public class TrafficDao implements Serializable {

    /**
     * 自增id
     */
    @Id
    private int id;

    /**
     * 录制配置id
     */
    @Column("recording_config_id")
    private int recordingConfigId;

    /**
     * 流量来源
     */
    @Column("source_type")
    private int sourceType;

    /**
     * 原始header
     */
    @Column("origin_headers")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> originHeaders;


    /**
     * 修改后的header
     */
    @Column("modify_headers")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> modifyHeaders;


    /**
     * GET、POST
     */
    @Column("http_method")
    private String httpMethod;

    @Column("host")
    private String host;

    /**
     * 调用地址
     */
    @Column("url")
    private String url;

    @Column("origin_query_string")
    private String originQueryString;

    @Column("origin_body")
    private String orginBody;




    @Column("modify_body")
    private String modifyBody;

    /**
     * dubbo流量
     */
    private DubboTraffic dubboTraffic;

    /**
     * 调用结果
     */
    @Column("response")
    private String response;

    /**
     * 调用开始时间
     */
    @Column("invoke_begin_time")
    private long invokeBeginTime;

    /**
     * 调用结束时间
     */
    @Column("invoke_end_time")
    private long invokeEndTime;

    @Column("trace_id")
    private String traceId;

    @Column("uid")
    private long uid;

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
