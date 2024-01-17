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

package run.mone.mimeter.agent.manager.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * @date 2022/5/23
 * 实际执行的压测任务
 */
@Table("mibench_task")
@Data
public class MibenchTask {

    @Id
    private int id;

    @Column(value = "scene_id")
    private int sceneId;

    @Column(value = "serial_link_id")
    private int serialLinkId;
    @Column(value = "scene_api_id")
    private int sceneApiId;

    /**
     * 1 http 接口任务
     * 2 dubbo 接口任务
     * 3 dag 图任务
     */
    @Column(value = "task_type")
    private int taskType;

    @Column
    private int qps;

    @Column(value = "origin_qps")
    private int originQps;

    @Column(value = "max_qps")
    private int maxQps;

    @Column
    private int time;

    @Column(value = "agent_num")
    private int agentNum;

    @Column(value = "finish_agent_num")
    private int finishAgentNum;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int state;

    @Column(value = "version", version = true)
    private int version;

    @Column(value = "success_num")
    private long successNum;

    @Column(value = "failure_num")
    private long failureNum;

    @Column(value = "parent_task_id")
    private int parentTaskId;

    @Column(value = "debug_result")
    private String debugResult;

    @Column(value = "debug_url")
    private String debugUrl;

    @Column(value = "debug_result_header")
    private String debugResultHeader;

    @Column(value = "debug_req_headers")
    private String debugReqHeaders;

    @Column(value = "debug_trigger_cp")
    private String debugTriggerCp;

    @Column(value = "debug_trigger_filter_condition")
    private String debugTriggerFilterCondition;

    @Column(value = "report_id")
    private String reportId;

    @Column(value = "request_params")
    private String requestParams;

    @Column(value = "req_param_type")
    private int reqParamType;

    @Column(value = "ok")
    private boolean ok;

    @Column(value = "connect_task_num")
    private int connectTaskNum;

    @Column(value = "debug_rt")
    private int debugRt;

    @Column(value = "debug_size")
    private int debugSize;

    @Column(value = "bench_mode")
    private int benchMode;

    @Column(value = "increase_mode")
    private int increaseMode;

    @Column(value = "increase_percent")
    private int increasePercent;

}
