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

package run.mone.mimeter.engine.agent.bo.task;

import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import lombok.Data;
import run.mone.mimeter.engine.agent.bo.data.DebugSceneApiInfoReq;
import run.mone.mimeter.engine.agent.bo.data.DemoData;
import run.mone.mimeter.engine.agent.bo.data.DubboData;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.data.NodeInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Data
public class Task implements Serializable {

    /**
     * 单接口任务id
     */
    private int id;

    /**
     * 用于取消任务的任务id列表
     */
    private List<Integer> ids;

    /**
     * 取消任务的类型 0:手动，1:由sla触发
     */
    private int cancelType;

    /**
     * 由sla触发结束的规则内容
     */
    private String cancelBySlaRule;

    /**
     * 场景定义id
     */
    private int sceneId;

    /**
     * 用于hera context 传参
     */
    private HeraContextInfo heraContextInfo;

    /**
     * 0:单接口调试
     * 1:场景调试
     * 2:场景压测
     */
    private int submitTaskType;

    /**
     * 用于调试的接口信息
     */
    private DebugSceneApiInfoReq apiInfo;

    private String reportId;

    private String opUser;

    //**********  以上字段由dashboard传，以下取场景数据构造//

    /**
     * 是否启用流量参数
     */
    private boolean enableTraffic;

    /**
     * 待拉取流量配置列表
     */
    private PullTrafficReqBase trafficToPullConfList;

    /**
     * 发压机数量
     */
    private int agentNum;

    /**
     * 该发压机器索引序号,从 0 开始
     */
    private int agentIndex;

    private int connectTaskNum;

    private int qps;

    private int logRate;

    /**
     * 持续多少秒
     */
    private int time;

    /**
     * 压力递增模式
     */
    private Integer incrementMode;

    /**
     * 本任务
     */
    DagTaskRps dagTaskRps;

    /**
     * 自定义成功状态码
     */
    private String successCode;

    /**
     * 接口发压信息
     */
    private String apiBenchInfos;

    /**
     * 超时时间(毫秒)
     */
    private int timeout = 1000;

    private TaskType type;

    private TaskType sceneType;

    private String addr;

    private HttpData httpData;

    private DubboData dubboData;

    private DemoData demoData;

    /**
     * 可控的场景发压比例
     */
    private Integer rpsRate;

    private ConcurrentHashMap<String, String> attachments = new ConcurrentHashMap<>();

    private TreeMap<String, List<String>> dataMap;

    /**
     * debug只会调用一次,并且需要拿到返回结果
     */
    private boolean debug;

    private GraphTaskContext<NodeInfo> dagInfo;

    /**
     * 提值表达式(需要从依赖的任务里提值)
     *
     * dag.0.map{data}{name}
     */
    private Map<String, String> exprMap;

    /**
     * 打点记录链路id
     */
    private Integer serialLinkID;

}
