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

package run.mone.mimeter.agent.manager;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.data.push.schedule.task.graph.TaskEdgeData;
import com.xiaomi.data.push.schedule.task.graph.TaskVertexData;
import common.Const;
import common.Util;
import org.apache.dubbo.annotation.DubboReference;
import org.nutz.trans.Trans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import com.xiaomi.youpin.docean.anno.Service;
import org.nutz.dao.impl.NutDao;
import run.mone.mimeter.agent.manager.bo.MibenchTask;
import run.mone.mimeter.dashboard.bo.agent.*;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLineNum;
import run.mone.mimeter.dashboard.bo.scene.*;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiTrafficInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiX5Info;
import run.mone.mimeter.dashboard.bo.sceneapi.FormParamValue;
import run.mone.mimeter.dashboard.bo.task.BenchIncreaseModeEnum;
import run.mone.mimeter.dashboard.bo.task.BenchModeEnum;
import run.mone.mimeter.dashboard.service.DatasetInfoSercice;
import run.mone.mimeter.dashboard.service.SceneInfoService;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;
import run.mone.mimeter.engine.agent.bo.data.*;
import run.mone.mimeter.engine.agent.bo.task.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static common.Const.*;
import static run.mone.mimeter.dashboard.bo.common.Constants.CONTENT_TYPE_APP_FORM2;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/19
 */
@Service
public class ManagerService {

    @Resource(name = "rpcServer")
    private RpcServer rpcServer;

    @Resource(name = "$daoName:mibench_st_db", description = "mysql")
    private NutDao dao;

    @Resource
    private SlaService slaService;

    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    private static final Logger log = LoggerFactory.getLogger(ManagerService.class);

    private static final int DEFAULT_SCENE_MAX_QPS = 500;

    private static final Gson gson = Util.getGson();

    private static final Pattern EL_PATTERN_MUTI = Pattern.compile("\\$\\{([^}]*)\\}");

    private final Random random = new Random(System.currentTimeMillis());

    private final ConcurrentHashMap<Integer, Boolean> rateUpWorkingTaskCache = new ConcurrentHashMap<>();

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = SceneInfoService.class, timeout = 3000)
    private SceneInfoService sceneInfoService;

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = DatasetInfoSercice.class, timeout = 10000)
    private DatasetInfoSercice datasetInfoSercice;

    public List<AgentChannel> agents() {
        return new ArrayList<>(AgentContext.ins().map.values());
    }

    public HttpResult submitTask(Task task) throws Exception {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        //构建插入任务
        MibenchTask mibenchTask = buildMibenchTask(task);

        //本场景同一次任务标记
        if (task.getReportId() == null) {
            task.setReportId("");
        }
        //默认不开启录制流量使用
        task.setEnableTraffic(false);
        mibenchTask.setReportId(task.getReportId());

        switch (task.getSubmitTaskType()) {
            case Const.SINGLE_API_DEBUG -> {
                return singleApiDebug(task, mibenchTask, agentList);
            }
            case Const.SCENE_DEBUG -> {
                return sceneDebug(task, mibenchTask, agentList);
            }
            case Const.SINGLE_BENCH -> {
                return sceneBench(task, mibenchTask, agentList);
            }
            default -> {
                return HttpResult.fail(500, "error param task type", "错误的任务类型");
            }
        }
    }

    /**
     * 单接口调试
     */
    private HttpResult singleApiDebug(Task task, MibenchTask mibenchTask, List<AgentChannel> agentList) throws Exception {
        //单接口调试
        task.setDebug(true);

        DebugSceneApiInfoReq apiInfo = task.getApiInfo();
        if (apiInfo.getApiType() == Const.API_TYPE_HTTP) {
            //处理http调试参数
            try {
                httpDebug(apiInfo, task, mibenchTask);
            } catch (Exception e) {
                return HttpResult.fail(500, "fail", "invalid http param");
            }
        } else if (apiInfo.getApiType() == Const.API_TYPE_DUBBO) {
            //处理dubbo调试参数
            try {
                dubboDebug(apiInfo, task, mibenchTask);
            } catch (Exception e) {
                log.error("singleApiDebug dubbo error:{}",e);
                return HttpResult.fail(500, "fail", "invalid dubbo param");
            }
        }
        int index = random.nextInt(agentList.size());
        AgentChannel c = agentList.get(index);
        agentList.clear();
        agentList.add(c);

        //记录单接口测试任务
        mibenchTask.setAgentNum(agentList.size());
        mibenchTask.setQps(1);
        SubmitTaskRes res = null;
        try {
            Trans.begin();
            dao.insert(mibenchTask);
            task.setTime(1);
            task.setQps(1);
            task.setId(mibenchTask.getId());
            task.setAgentNum(agentList.size());

            AgentReq agentReq = new AgentReq();
            agentReq.setCmd(AgentReq.SUBMIT_TASK_CMD);
            agentReq.setTask(task);

            //发送单接口测试任务
            List<String> agentIps = new ArrayList<>(agentList.stream().map(AgentChannel::getRemoteAddr).toList());
            TaskResult tr = syncCallAgent(agentList.get(0), agentReq);
            res = new SubmitTaskRes(task.getReportId(), tr, new HashMap<>(), agentIps);
            Trans.commit();
        } catch (Exception e) {
            Trans.rollback();
            log.error("single debug error,e:{}", e.getMessage());
        } finally {
            Trans.close();
        }
        return HttpResult.success(gson.toJson(res));
    }


    /**
     * 场景调试
     */
    private HttpResult sceneDebug(Task task, MibenchTask mibenchTask, List<AgentChannel> agentList) throws Exception {
        //场景调试及场景压测，都需要先基于场景数据构建任务图
        //场景数据
        SceneDTO sceneInfo = sceneInfoService.getSceneByID(task.getSceneId()).getData();
        if (sceneInfo == null) {
            log.error("can not get scene info,scene id:{}", task.getSceneId());
            return HttpResult.fail(500, "faild", "can not get scene info");
        }
        List<AgentDTO> agentDTOList = sceneInfo.getAgentDTOList();
        if (agentDTOList == null) {
            agentDTOList = new ArrayList<>();
        }
        //过滤不可用机器
        agentDTOList = agentDTOList.stream().filter(AgentDTO::getEnable).collect(Collectors.toList());

        //绑定的机器ip
        List<String> bindIps = agentDTOList.stream().filter(AgentDTO::getEnable).map(AgentDTO::getIp).toList();

        if (bindIps.size() == 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        //过滤未启用链路
        filterLink(sceneInfo);

        //链路id与生成的dag 任务id映射
        Map<Integer, DagTaskRps> linkTaskIdMap = new HashMap<>(sceneInfo.getSerialLinkDTOs().size());

        //场景调试
        task.setDebug(true);
        task.setType(TaskType.dag);
        //施压时间
        task.setTime(sceneInfo.getBenchTime());
        //场景任务整体超时时间
        task.setTimeout(sceneInfo.getRequestTimeout());
        task.setQps(1);

        //自定义成功状态码
        task.setSuccessCode(sceneInfo.getSuccessCode());
        task.setConnectTaskNum(sceneInfo.getSerialLinkDTOs().size());
        List<AgentChannel> tmpAgentList;

        //有绑定机器
        tmpAgentList = agentList.stream().filter(channel -> {
            //过滤在绑定机器内的ip
            return bindIps.contains(channel.getRemoteAddr().substring(0, channel.getRemoteAddr().indexOf(":")));
        }).toList();

        if (tmpAgentList.size() == 0) {
            return HttpResult.fail(500, "agent num <= 0", "failed");
        }
        int index = random.nextInt(tmpAgentList.size());

        AgentChannel c = tmpAgentList.get(index);
        agentList.clear();
        agentList.add(c);

        //图任务
        mibenchTask.setTaskType(TaskType.dag.code);
        mibenchTask.setAgentNum(agentList.size());
        mibenchTask.setQps(1);
        mibenchTask.setTime(sceneInfo.getBenchTime());
        mibenchTask.setConnectTaskNum(sceneInfo.getSerialLinkDTOs().size());
        try {
            //记录场景调试任务
            Trans.begin();
            //build && 发送场景调试请求
            buildAgentReqs(sceneInfo, task, agentList.size(), mibenchTask, linkTaskIdMap).forEach(agentReq -> {
                try {
                    callTaskAgents(agentList, agentReq);
                } catch (Exception e) {
                    log.error("call agent error:{}", e.getMessage());
                }
            });
            Trans.commit();
        } catch (Exception e) {
            Trans.rollback();
            log.error("scene debug error,e:{}", e.getMessage());
        } finally {
            Trans.close();
        }
        List<String> agentIps = new ArrayList<>(agentList.stream().map(AgentChannel::getRemoteAddr).toList());
        return HttpResult.success(gson.toJson(new SubmitTaskRes(task.getReportId(), new TaskResult(), new HashMap<>(), agentIps)));
    }

    /**
     * 场景压测
     */
    private HttpResult sceneBench(Task task, MibenchTask mibenchTask, List<AgentChannel> agentList) throws Exception {
        //场景调试及场景压测，都需要先基于场景数据构建任务图
        //场景数据
        task.setType(TaskType.dag);
        SceneDTO sceneInfo = sceneInfoService.getSceneByID(task.getSceneId()).getData();
        if (sceneInfo == null) {
            log.error("can not get scene info,scene id:{}", task.getSceneId());
            return HttpResult.fail(500, "faild", "can not get scene info");
        }
        if (sceneInfo.getSceneType() == SCENE_TYPE_HTTP) {
            task.setSceneType(TaskType.http);
        } else if (sceneInfo.getSceneType() == SCENE_TYPE_DUBBO) {
            task.setSceneType(TaskType.dubbo);
        }
        //过滤链路
        filterLink(sceneInfo);

        //链路id与生成的dag 任务id映射
        Map<Integer, DagTaskRps> linkTaskIdMap = new HashMap<>(sceneInfo.getSerialLinkDTOs().size());

        task.setSceneId(sceneInfo.getId());
        List<AgentDTO> agentDTOList = sceneInfo.getAgentDTOList();
        if (agentDTOList == null) {
            agentDTOList = new ArrayList<>();
        }
        //过滤不可用机器
        agentDTOList = agentDTOList.stream().filter(AgentDTO::getEnable).collect(Collectors.toList());

        //绑定的机器ip
        List<String> bindIps = agentDTOList.stream().filter(AgentDTO::getEnable).map(AgentDTO::getIp).toList();

        if (bindIps.size() == 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }

        List<AgentChannel> tmpAgentList;

        //有绑定机器
        tmpAgentList = agentList.stream().filter(channel -> {
            //过滤在绑定机器内的ip
            return bindIps.contains(channel.getRemoteAddr().substring(0, channel.getRemoteAddr().indexOf(":")));
        }).toList();

        if (tmpAgentList.size() == 0) {
            return HttpResult.fail(500, "agent num <= 0", "请绑定至少一台压测机");
        }

        if (sceneInfo.getMaxBenchQps() < 50) {
            int index = random.nextInt(tmpAgentList.size());

            AgentChannel c = tmpAgentList.get(index);
            agentList.clear();
            agentList.add(c);
        } else {
            agentList = tmpAgentList;
        }
        int benchAgentNum = agentList.size();
        //校验本场景所使用的数据源行数是否大于压测机数量
        AtomicBoolean ok = new AtomicBoolean(true);
        List<DatasetLineNum> datasetLineNums = datasetInfoSercice.getLineNumBySceneId(task.getSceneId()).getData();
        datasetLineNums.forEach(datasetLineNum -> {
            if (datasetLineNum.getFileRaw() < benchAgentNum){
                ok.set(false);
            }
        });
        if (!ok.get()){
            return HttpResult.fail(500, "dataset line num < agent num", "数据源文件行数必须大于等于所使用的发压机数量");
        }

        //为图任务
        mibenchTask.setTaskType(TaskType.dag.code);
        mibenchTask.setAgentNum(agentList.size());
        mibenchTask.setConnectTaskNum(sceneInfo.getSerialLinkDTOs().size());

        //总qps,即
        int sceneMaxQps;
        if (sceneInfo.getMaxBenchQps() == null) {
            sceneMaxQps = DEFAULT_SCENE_MAX_QPS;
        } else {
            sceneMaxQps = sceneInfo.getMaxBenchQps();
        }
        mibenchTask.setQps(sceneMaxQps);

        task.setTime(sceneInfo.getBenchTime());
        //场景任务整体超时时间
        task.setTimeout(sceneInfo.getRequestTimeout());
        task.setLogRate(sceneInfo.getLogRate());
        task.setConnectTaskNum(sceneInfo.getSerialLinkDTOs().size());
        task.setAgentNum(agentList.size());
        //自定义成功状态码
        task.setSuccessCode(sceneInfo.getSuccessCode());

        try {
            //记录场景压测任务
            Trans.begin();
            //构建任务图请求
            List<AgentReq> reqList = buildAgentReqs(sceneInfo, task, agentList.size(), mibenchTask, linkTaskIdMap);

            //分发链路任务
            List<AgentChannel> finalTmpAgentList = agentList;
            log.info("use agent num:{},agent list:{}", finalTmpAgentList.size(), finalTmpAgentList);
            reqList.forEach(agentReq -> callTaskAgents(finalTmpAgentList, agentReq));
            Trans.commit();

            //更新场景状态为运行中
            sceneInfoService.updateSceneStatus(task.getSceneId(), TaskStatus.Running.code);

            //开启SLA实时通知
            try {
                slaService.processSlaNotifyTask(sceneInfo, task.getReportId());
            } catch (Exception e) {
                log.warn("slaService start failed,cause by:{}", e.getMessage());
            }
        } catch (Exception e) {
            Trans.rollback();
            log.error("scene bench error,e:{}", e.getMessage());
        } finally {
            Trans.close();
        }
        List<String> agentIps = new ArrayList<>(agentList.stream().map(AgentChannel::getRemoteAddr).toList());
        return HttpResult.success(gson.toJson(new SubmitTaskRes(task.getReportId(), new TaskResult(), linkTaskIdMap, agentIps)));
    }

    /**
     * 取消任务
     */
    public HttpResult cancelTask(Task task) {
        AgentReq ar = new AgentReq();
        ar.setCmd(AgentReq.CANCEL_TASK_CMD);
        ar.setTask(task);
        RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, ar);
        if (AgentContext.ins().list().size() != 0) {
            AgentContext.ins().list().forEach(c -> rpcServer.tell(c.getChannel(), req));
        } else {
            //没有agent机器，直接更新场景及任务状态
            directStopTask(task.getIds());
        }
        return HttpResult.success("ok");
    }

    private void directStopTask(List<Integer> taskIds) {
        taskIds.forEach(taskId -> {
            MibenchTask task = dao.fetch(MibenchTask.class, taskId);
            if (task != null) {
                task.setState(TaskStatus.Stopped.code);
                task.setUtime(System.currentTimeMillis());
                dao.update(task);
            }
        });
    }

    /**
     * 图压测任务开始执行后的回调通知
     * 百分比递增模式下，周期性执行rps递增
     */
    public void taskRunningNotify(MibenchTask dagTask) {
        if (rateUpWorkingTaskCache.containsKey(dagTask.getId())) {
            return;
        }
        //同一个链路任务只接收一次通知，仅用一个协程进行调度
        rateUpWorkingTaskCache.put(dagTask.getId(), true);

        log.info("manualUpdateQps in bench mode:{},increase mode :{},dagTask increasePercent:{}", dagTask.getBenchMode(), dagTask.getIncreaseMode(), dagTask.getIncreasePercent());
        if (dagTask.getBenchMode() == BenchModeEnum.RPS.code && dagTask.getIncreaseMode() == BenchIncreaseModeEnum.PERCENT_INCREASE.code) {
            //百分比递增默认比例
            List<Integer> increasePercentList = Lists.newArrayList(5, 10, 20, 25, 50);
            // RPS 百分比递增模式
            pool.submit(() -> {
                long start = System.currentTimeMillis();
                int benchTime = dagTask.getTime();
                int percentNum = dagTask.getIncreasePercent();
                if (benchTime >= 60 && increasePercentList.contains(percentNum)) {
                    int curRps = dagTask.getOriginQps();
                    double percent = percentNum / 100d;
                    //调度周期 ms
                    long schedule = (long) (benchTime * percent * 1000);
                    //最大、最小RPS差值
                    int distance = dagTask.getMaxQps() - curRps;
                    //差值100以内不处理
                    if (distance >= 100) {
                        //步长
                        int step = (int) Math.ceil(distance * percent);
                        log.info("manualUpdateQps step:{},schedule:{}", step, schedule);
                        try {
                            //起始rps需要先跑一个周期
                            Thread.sleep(schedule);
                        } catch (InterruptedException e) {
                            log.error("taskRunningNotify thread sleep failed,cause by:{}", e.getMessage());
                        }
                        while (System.currentTimeMillis() - start < benchTime * 1000L) {
                            //在压测时间内
                            curRps += step;
                            if (curRps > dagTask.getMaxQps()) {
                                curRps = dagTask.getMaxQps();
                            }
                            if (curRps <= dagTask.getMaxQps()) {
                                DagTaskRps dagTaskRps = new DagTaskRps(dagTask.getReportId(), dagTask.getSerialLinkId(), dagTask.getId(), curRps);
                                List<DagTaskRps> dagTaskRpsList = Lists.newArrayList(dagTaskRps);
                                ChangeQpsReq req = new ChangeQpsReq(dagTaskRpsList);
                                //发送调速命令
                                log.info("manualUpdateQps cur rps:{}", curRps);
                                this.manualUpdateQps(req);
                                try {
                                    Thread.sleep(schedule);
                                } catch (InterruptedException e) {
                                    log.error("taskRunningNotify thread sleep failed,cause by:{}", e.getMessage());
                                }
                                if (curRps == dagTask.getMaxQps()) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        //最多维护到压测时间截止
                        rateUpWorkingTaskCache.remove(dagTask.getId());
                    }
                }
            });
        }
    }

    public HttpResult manualUpdateQps(ChangeQpsReq qpsReq) {
        AgentReq ar = new AgentReq();
        ar.setCmd(AgentReq.CHANGE_TASK_QPS);
        ar.setChangeQpsReq(qpsReq);
        RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, ar);
        AgentContext.ins().list().forEach(c -> rpcServer.tell(c.getChannel(), req));
        return HttpResult.success("ok");
    }

    private void httpDebug(DebugSceneApiInfoReq apiInfo, Task task, MibenchTask mibenchTask) throws Exception {
        //http单接口测试任务
        HttpData httpData = new HttpData();
        httpData.setUrl(apiInfo.getApiUrl());

        httpData.setTspAuthInfoDTO(apiInfo.getApiTspAuth());

        if (apiInfo.getOutputParamInfosStr() != null) {
            List<OutputParam> outputParams = new ArrayList<>();
            List<OutputParam> outputParamList = gson.fromJson(apiInfo.getOutputParamInfosStr(), new TypeToken<List<OutputParam>>() {
            }.getType());
            // key 表达式，需要取上游接口的出参定义合成
            outputParamList.forEach(oParam -> outputParams.add(new OutputParam(oParam.getOrigin(), oParam.getParamName(), oParam.getParseExpr())));

            httpData.setOutputParams(new CopyOnWriteArrayList<>(outputParams));
        }
        if (apiInfo.getCheckPointInfoListStr() != null) {
            List<CheckPointInfo> checkPointInfos = gson.fromJson(apiInfo.getCheckPointInfoListStr(), new TypeToken<List<CheckPointInfo>>() {
            }.getType());
            httpData.setCheckPointInfoList(new CopyOnWriteArrayList<>(checkPointInfos));
        }
        List<ParamType> types = new ArrayList<>();
        ConcurrentHashMap<String, String> headerMap;
        //请求头
        try {
            headerMap = gson.fromJson(apiInfo.getApiHeader(), new TypeToken<ConcurrentHashMap<String, String>>() {
            }.getType());

            if (headerMap == null) {
                headerMap = new ConcurrentHashMap<>();
            }
            headerMap.put("User-Agent", Const.MIMETER_UA_KEY);
            if (apiInfo.getRequestMethod() == Const.HTTP_REQ_GET || apiInfo.getContentType().equals(Constants.CONTENT_TYPE_APP_FORM) || apiInfo.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                List<FormParamValue> paramValues;
                //get
                if (apiInfo.getRequestMethod() == Const.HTTP_REQ_GET) {
                    httpData.setMethod(Const.HTTP_GET);
                } else {
                    if (apiInfo.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                        headerMap.put("Content-type", CONTENT_TYPE_APP_FORM);
                    } else {
                        headerMap.put("Content-type", apiInfo.getContentType());
                    }
                    httpData.setMethod(Const.HTTP_POST);
                }
                //表单/get 类型参数
                //例：[{"paramKey": "v1", "paramValue": "v2"}]
                CopyOnWriteArrayList<Object> tmpValues = new CopyOnWriteArrayList<>();
                if (null != apiInfo.getRequestBody()) {
                    paramValues = gson.fromJson(apiInfo.getRequestBody(), new TypeToken<List<FormParamValue>>() {
                    }.getType());
                    paramValues.forEach(paramValue -> {
                        ParamType paramType = new ParamType(ParamTypeEnum.primary, paramValue.getParamKey());
                        types.add(paramType);
                        tmpValues.add(paramValue.getParamValue());
                    });
                }
                httpData.initTypeList(types);
                //["a","b"...]
                httpData.getParams().addAll(new CopyOnWriteArrayList(tmpValues));
            } else {
                headerMap.put("Content-type", apiInfo.getContentType());
                httpData.setMethod(Const.HTTP_POST);
                List<Object> objectList;
                // 处理参数
                if (null != apiInfo.getRequestBody()) {
                    objectList = gson.fromJson(apiInfo.getRequestBody(), new TypeToken<List<Object>>() {
                    }.getType());
                    if (objectList.size() == 0) {
                        httpData.setPostParamJson("");
                        return;
                    }
                    if (isStringType(objectList.get(0).getClass())) {
                        //字符串
                        httpData.getJsonParam().set(objectList.get(0).toString());
                        httpData.setPostParamJson(objectList.get(0).toString());
                    } else {
                        String jsonParam = gson.toJson(objectList.get(0));
                        httpData.getJsonParam().set(jsonParam);
                        httpData.setPostParamJson(jsonParam);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            log.error("parse http param error,url:" + apiInfo.getApiUrl() + ",error:{}", e);
            throw new Exception(e);
        }
        httpData.setHeaders(headerMap);
        httpData.setContentType(apiInfo.getContentType());
        httpData.setTimeout(apiInfo.getRequestTimeout());

        task.setHttpData(httpData);
        task.setType(TaskType.http);

        //http任务
        mibenchTask.setTaskType(TaskType.http.code);
    }

    private void dubboDebug(DebugSceneApiInfoReq apiInfo, Task task, MibenchTask mibenchTask) {
        //dubbo单接口测试任务
        DubboData dubboData = new DubboData();
        dubboData.setServiceName(apiInfo.getServiceName());
        dubboData.setMethodName(apiInfo.getMethodName());
        dubboData.setGroup(apiInfo.getDubboGroup());
        dubboData.setVersion(apiInfo.getDubboVersion());
        dubboData.setMavenVersion(apiInfo.getDubboMavenVersion());

        if (apiInfo.getOutputParamInfosStr() != null) {

            List<OutputParam> outputParams = new ArrayList<>();
            List<OutputParam> outputParamList = gson.fromJson(apiInfo.getOutputParamInfosStr(), new TypeToken<List<OutputParam>>() {
            }.getType());
            // key 表达式，需要取上游接口的出参定义合成
            outputParamList.forEach(oParam -> outputParams.add(new OutputParam(oParam.getOrigin(), oParam.getParamName(), oParam.getParseExpr())));

            dubboData.setOutputParams(new CopyOnWriteArrayList<>(outputParams));
        }
        if (apiInfo.getAttachments() != null && !apiInfo.getAttachments().isEmpty()) {
            List<Attachment> attachmentList = gson.fromJson(apiInfo.getAttachments(), new TypeToken<List<Attachment>>() {
            }.getType());
            ConcurrentHashMap<String, String> attachmentMap = new ConcurrentHashMap<>(attachmentList.size());

            //可以覆盖
            attachmentList.forEach(headerInfo -> attachmentMap.put(headerInfo.getParamKey(), headerInfo.getParamValue()));
            //mimeter标记
            attachmentMap.put("User-Agent", Const.MIMETER_UA_KEY);
            dubboData.setAttachments(attachmentMap);
        }
        //检查点
        if (apiInfo.getCheckPointInfoListStr() != null) {
            List<CheckPointInfo> checkPointInfos = gson.fromJson(apiInfo.getCheckPointInfoListStr(), new TypeToken<List<CheckPointInfo>>() {
            }.getType());
            dubboData.setCheckPointInfoList(new CopyOnWriteArrayList<>(checkPointInfos));
        }
        //参数类型列表
        List<String> paramTypeList = gson.fromJson(apiInfo.getParamTypeList(), new TypeToken<List<String>>() {
        }.getType());
        dubboData.setRequestParamTypeList(paramTypeList);
        dubboData.setRequestBody(apiInfo.getDubboParamJson());
        dubboData.setRequestTimeout(apiInfo.getRequestTimeout());
        task.setDubboData(dubboData);
        task.setType(TaskType.dubbo);

        //dubbo任务
        mibenchTask.setTaskType(TaskType.dubbo.code);
    }

    private List<AgentReq> buildAgentReqs(SceneDTO sceneInfo, Task task, int agentNum, MibenchTask dagTask, Map<Integer, DagTaskRps> linkTaskIdMap) {
        List<AgentReq> reqList = new ArrayList<>();
        if (sceneInfo.getSceneType() == SCENE_TYPE_HTTP) {

            //对链路中接口排序
            for (SerialLinkDTO serialLink : sceneInfo.getSerialLinkDTOs()) {
                serialLink.setHttpApiInfoDTOList(serialLink.getHttpApiInfoDTOList().stream().sorted().collect(Collectors.toList()));
            }
            //构建 http接口 任务图
            if (sceneInfo.getSerialLinkDTOs().size() == 1) {
                //构建taskContext前的初始化工作
                SerialLinkDTO serialLink = sceneInfo.getSerialLinkDTOs().get(0);
                buildDagBefore(sceneInfo, serialLink, dagTask, task, agentNum, linkTaskIdMap);
                //http 单链路场景
                GraphTaskContext<NodeInfo> taskContext = buildHttpDagContext(sceneInfo.getId(), serialLink, task, dagTask, sceneInfo.getApiBenchInfos(), sceneInfo.getGlobalHeaderList(), sceneInfo.getGlobalTspAuth());
                task.setDagInfo(taskContext);

                AgentReq agentReq = new AgentReq();
                agentReq.setCmd(AgentReq.SUBMIT_TASK_CMD);
                agentReq.setTask(task);
                reqList.add(agentReq);
            } else {
                //多链路并发场景
                for (SerialLinkDTO serialLink :
                        sceneInfo.getSerialLinkDTOs()) {
                    processMutiLinkTask(SCENE_TYPE_HTTP, sceneInfo, serialLink, dagTask, task, agentNum, reqList, linkTaskIdMap);
                }
            }
        } else if (sceneInfo.getSceneType() == Const.SCENE_TYPE_DUBBO) {
            //对链路中接口排序
            for (SerialLinkDTO serialLink : sceneInfo.getSerialLinkDTOs()) {
                serialLink.setDubboApiInfoDTOList(serialLink.getDubboApiInfoDTOList().stream().sorted().collect(Collectors.toList()));
            }
            if (sceneInfo.getSerialLinkDTOs().size() == 1) {
                //单链路场景
                SerialLinkDTO serialLink = sceneInfo.getSerialLinkDTOs().get(0);
                buildDagBefore(sceneInfo, serialLink, dagTask, task, agentNum, linkTaskIdMap);
                //构建 dubbo接口 任务图
                GraphTaskContext<NodeInfo> taskContext = buildDubboDagContext(sceneInfo.getId(), serialLink, task, dagTask, sceneInfo.getApiBenchInfos(), sceneInfo.getGlobalHeaderList());
                task.setDagInfo(taskContext);

                AgentReq agentReq = new AgentReq();
                agentReq.setCmd(AgentReq.SUBMIT_TASK_CMD);
                agentReq.setTask(task);
                reqList.add(agentReq);

            } else {
                //多链路并发场景
                for (SerialLinkDTO serialLink :
                        sceneInfo.getSerialLinkDTOs()) {
                    processMutiLinkTask(Const.SCENE_TYPE_DUBBO, sceneInfo, serialLink, dagTask, task, agentNum, reqList, linkTaskIdMap);
                }
            }
        } else {
            log.error("un support scene type");
        }
        return reqList;
    }

    private void processMutiLinkTask(int sceneType, SceneDTO sceneInfo, SerialLinkDTO serialLink, MibenchTask dagTask, Task task, int agentNum, List<AgentReq> reqList, Map<Integer, DagTaskRps> linkTaskIdMap) {
        //构建taskContext前的初始化工作
        buildDagBefore(sceneInfo, serialLink, dagTask, task, agentNum, linkTaskIdMap);

        //多次构建单链路任务
        AgentReq agentReq = new AgentReq();
        agentReq.setCmd(AgentReq.SUBMIT_TASK_CMD);

        Task tempTask = new Task();
        BeanUtils.copyProperties(task, tempTask);

        //该链路下的接口发压信息
        List<ApiBenchInfo> apiBenchInfos = sceneInfo.getApiBenchInfos().stream().filter(apiBenchInfo -> apiBenchInfo.getSerialName().equals(serialLink.getSerialLinkName())).collect(Collectors.toList());
        //排序
        apiBenchInfos = apiBenchInfos.stream().sorted().collect(Collectors.toList());
        GraphTaskContext<NodeInfo> taskContext = null;
        if (sceneType == SCENE_TYPE_HTTP) {
            taskContext = buildHttpDagContext(sceneInfo.getId(), serialLink, task, dagTask, apiBenchInfos, sceneInfo.getGlobalHeaderList(), sceneInfo.getGlobalTspAuth());
        } else if (sceneType == Const.SCENE_TYPE_DUBBO) {
            taskContext = buildDubboDagContext(sceneInfo.getId(), serialLink, task, dagTask, apiBenchInfos, sceneInfo.getGlobalHeaderList());
        }
        tempTask.setDagInfo(taskContext);

        agentReq.setTask(tempTask);
        reqList.add(agentReq);
    }

    private void buildDagBefore(SceneDTO sceneInfo, SerialLinkDTO serialLink, MibenchTask dagTask, Task task, int agentNum, Map<Integer, DagTaskRps> linkTaskIdMap) {
        dagTask.setSerialLinkId(serialLink.getSerialLinkID());
        dagTask.setState(TaskStatus.Init.code);
        //链路任务插库
        List<ApiBenchInfo> apiBenchInfos = sceneInfo.getApiBenchInfos().stream().filter(apiBenchInfo -> apiBenchInfo.getSerialName().equals(serialLink.getSerialLinkName())).toList();
        //固定rps
        int finalRps = 0;
        int linkBenchTime = 60;
        if (sceneInfo.getBenchMode() == BenchModeEnum.RPS.code) {
            if (apiBenchInfos.size() != 0) {
                //手动和百分比递增都以其实为基准
                if (sceneInfo.getIncrementMode() != BenchIncreaseModeEnum.STABLE.code) {
                    finalRps = apiBenchInfos.get(0).getOriginRps();
                } else {
                    finalRps = apiBenchInfos.get(0).getLinkTps();
                }
                dagTask.setQps(finalRps);
                if (apiBenchInfos.get(0).getLinkBenchTime() != null) {
                    linkBenchTime = apiBenchInfos.get(0).getLinkBenchTime();
                }
                dagTask.setTime(linkBenchTime);

                dagTask.setOriginQps(apiBenchInfos.get(0).getOriginRps());
                dagTask.setMaxQps(apiBenchInfos.get(0).getMaxRps());
            }
        }

        task.setQps(Util.calculateAgentRps(finalRps, agentNum));

        //发压比例
        task.setRpsRate(sceneInfo.getRpsRate());
        //链路施压时间
        task.setTime(linkBenchTime);
        //压力模式
        dagTask.setBenchMode(sceneInfo.getBenchMode());
        //rps 递增模式
        if (sceneInfo.getIncrementMode() != null) {
            dagTask.setIncreaseMode(sceneInfo.getIncrementMode());
        }
        //发压比例
        if (sceneInfo.getIncreasePercent() != null) {
            dagTask.setIncreasePercent(sceneInfo.getIncreasePercent());
        }
        dao.insert(dagTask);
        task.setId(dagTask.getId());
        task.setSerialLinkID(dagTask.getSerialLinkId());

        //记录dag 任务id与链路的映射关系
        DagTaskRps dagTaskRps = new DagTaskRps(task.getReportId(), dagTask.getSerialLinkId(), dagTask.getId(), finalRps);
        task.setDagTaskRps(dagTaskRps);
        linkTaskIdMap.putIfAbsent(serialLink.getSerialLinkID(), dagTaskRps);

        HeraContextInfo heraContextInfo = new HeraContextInfo(sceneInfo.getId(), serialLink.getSerialLinkID(), 0, dagTask.getReportId());

        task.setHeraContextInfo(heraContextInfo);
        //单链路，直接对接口对压测qps信息排序
        sceneInfo.setApiBenchInfos(sceneInfo.getApiBenchInfos().stream().sorted().collect(Collectors.toList()));

        task.setAgentNum(agentNum);
    }

    /**
     * 基于单个链路接口信息构建http任务图
     */
    public GraphTaskContext<NodeInfo> buildHttpDagContext(int sceneId, SerialLinkDTO serialLinkDTO, Task dagTask, MibenchTask mibenchTask, List<ApiBenchInfo> apiBenchInfos, List<GlobalHeader> globalHeaders, TspAuthInfo globalTspAuthInfo) {
        GraphTaskContext<NodeInfo> taskContext = new GraphTaskContext<>();

        //任务图顶点集
        List<TaskVertexData<NodeInfo>> taskList = new ArrayList<>();
        //任务图 边集
        List<TaskEdgeData> dependList = new ArrayList<>();

        int benchInfoIndex = 0;

        //用来临时记录每个接口的出参定义，用于依赖连接
        Map<String, Integer> outputParamsMap = new HashMap<>();

        //该链路拉取流量参数的配置信息
        PullTrafficReqBase pullTrafficReqBase = new PullTrafficReqBase();
        dagTask.setTrafficToPullConfList(pullTrafficReqBase);
        List<PullApiTrafficReq> apiTrafficReqList = new ArrayList<>();
        pullTrafficReqBase.setApiTrafficReqList(apiTrafficReqList);
        //接口在链路中的顺序
        int index = 0;
        for (HttpApiInfoDTO apiInfo : serialLinkDTO.getHttpApiInfoDTOList()) {
            ApiTrafficInfo apiTrafficInfo = apiInfo.getApiTrafficInfo();
            if (apiTrafficInfo != null && apiTrafficInfo.isEnableTraffic()) {
                //顺带校验该链路中是否有启用录制流量参数的api，有的话再开启开关
                dagTask.setEnableTraffic(true);
                apiTrafficReqList.add(new PullApiTrafficReq(apiTrafficInfo.getRecordingConfigId(), apiTrafficInfo.getUrl(), apiTrafficInfo.getFromTime(), apiTrafficInfo.getToTime()));
            }

            TaskVertexData<NodeInfo> httpCalVertex = buildVertexForTask(sceneId, serialLinkDTO, mibenchTask, dagTask, index, TaskType.http.code, benchInfoIndex, apiBenchInfos, apiInfo, null, outputParamsMap, globalHeaders, globalTspAuthInfo);
            taskList.add(httpCalVertex);
            index++;
            benchInfoIndex++;
        }
        //多接口，按顺序构建顶点依赖关系
        if (serialLinkDTO.getHttpApiInfoDTOList().size() > 1) {
            List<Integer> vertexIdList = taskList.stream().map(TaskVertexData::getIndex).toList();
            for (int i = 0; i < vertexIdList.size() - 1; i++) {
                //连结当前顶点与下一个顶点
                TaskEdgeData edge = new TaskEdgeData(vertexIdList.get(i), vertexIdList.get(i + 1));
                dependList.add(edge);
            }
        }
        //连接具有结果依赖的结点
        for (TaskVertexData<NodeInfo> taskVertexData : taskList) {
            HttpData httpData = taskVertexData.getData().getTask().getHttpData();
            if (httpData.getMethod().equals("get")) {
                //处理get请求的 http任务,更新keys表达式列表及连接依赖的边
                parseHttpGetTaskDep(taskVertexData, httpData.getUrl(), outputParamsMap, taskList, dependList);
            } else {
                //处理post请求的 http任务
                parseHttpPostTaskDep(taskVertexData, outputParamsMap, taskList, dependList);
            }
        }

        //解析结点条件过滤关系
        for (TaskVertexData<NodeInfo> taskVertexData : taskList) {
            parseTaskFilterConditionDep(taskVertexData, taskVertexData.getData().getTask().getHttpData().getFilterCondition(), outputParamsMap, taskList, dependList, TaskType.http.code);
        }
        taskContext.setTaskList(taskList);
        //任务是有依赖关系的
        taskContext.setDependList(dependList);
        return taskContext;
    }

    /**
     * 构建dubbo任务图
     *
     * @return
     */
    public GraphTaskContext<NodeInfo> buildDubboDagContext(int sceneId, SerialLinkDTO serialLinkDTO, Task dagTask, MibenchTask mibenchTask, List<ApiBenchInfo> apiBenchInfos, List<GlobalHeader> globalAttachments) {
        GraphTaskContext<NodeInfo> taskContext = new GraphTaskContext<>();

        //任务图顶点集
        List<TaskVertexData<NodeInfo>> taskList = new ArrayList<>();
        //任务图 边集
        List<TaskEdgeData> dependList = new ArrayList<>();

        int benchInfoIndex = 0;

        //用来临时记录每个接口的出参定义，用于依赖连接
        Map<String, Integer> outputParamsMap = new HashMap<>();
        int index = 0;
        for (DubboApiInfoDTO apiInfo : serialLinkDTO.getDubboApiInfoDTOList()) {
            TaskVertexData<NodeInfo> dubboCalVertex = buildVertexForTask(sceneId, serialLinkDTO, mibenchTask,
                    dagTask, index, TaskType.dubbo.code, benchInfoIndex, apiBenchInfos, null, apiInfo, outputParamsMap, globalAttachments, null);
            taskList.add(dubboCalVertex);
            index++;
            benchInfoIndex++;
        }

        //多接口，按顺序构建顶点依赖关系
        if (serialLinkDTO.getDubboApiInfoDTOList().size() > 1) {
            List<Integer> vertexIdList = taskList.stream().map(TaskVertexData::getIndex).toList();
            for (int i = 0; i < vertexIdList.size() - 1; i++) {
                //连结当前顶点与下一个顶点
                TaskEdgeData edge = new TaskEdgeData(vertexIdList.get(i), vertexIdList.get(i + 1));
                dependList.add(edge);
            }
        }

        //连接具有结果依赖的结点
        for (TaskVertexData<NodeInfo> taskVertexData : taskList) {
            parseDubboTaskDep(taskVertexData, outputParamsMap, taskList, dependList);
        }

        //连接具有结果条件依赖的结点
        for (TaskVertexData<NodeInfo> taskVertexData : taskList) {
            parseTaskFilterConditionDep(taskVertexData, taskVertexData.getData().getTask().getDubboData().getFilterCondition(), outputParamsMap, taskList, dependList, TaskType.dubbo.code);
        }
        taskContext.setTaskList(taskList);
        //任务是有依赖关系的
        taskContext.setDependList(dependList);
        return taskContext;
    }

    private TaskVertexData<NodeInfo> buildVertexForTask(int sceneId, SerialLinkDTO serialLinkDTO, MibenchTask mibenchTask,
                                                        Task dagTask, int apiIndex, int taskType, int benchInfoIndex,
                                                        List<ApiBenchInfo> apiBenchInfos, HttpApiInfoDTO httpApiInfo, DubboApiInfoDTO dubboApiInfo, Map<String, Integer> outputParamsMap,
                                                        List<GlobalHeader> globalAttachments, TspAuthInfo globalTspAuthInfo) {
        MibenchTask apiTask = buildMibenchTask(dagTask);
        apiTask.setParentTaskId(mibenchTask.getId());
        if (taskType == TaskType.http.code) {
            apiTask.setSceneApiId(httpApiInfo.getApiID());
        } else if (taskType == TaskType.dubbo.code) {
            apiTask.setSceneApiId(dubboApiInfo.getApiID());
        }
        apiTask.setTaskType(taskType);
        apiTask.setAgentNum(mibenchTask.getAgentNum());
        //每个接口需要使用各自的qps
        ApiBenchInfo apiBenchInfo = apiBenchInfos.get(benchInfoIndex);
        int agentNum = mibenchTask.getAgentNum();
        int apiOneAgentOriTps = apiBenchInfo.getOriginRps() / agentNum;
        int apiOneAgentMaxTps = apiBenchInfo.getMaxRps() / agentNum;

        apiTask.setQps(mibenchTask.getQps());
        //起始qps
        apiTask.setOriginQps(apiOneAgentOriTps);
        //最大qps
        apiTask.setMaxQps(apiOneAgentMaxTps);
        apiTask.setReportId(mibenchTask.getReportId());
        apiTask.setState(TaskStatus.Init.code);
        //每个接口独立任务插库
        dao.insert(apiTask);

        //用于hera传参的信息,任务id使用dagTask的
        HeraContextInfo heraContextInfo;
        TaskVertexData<NodeInfo> calVertex = new TaskVertexData<>();
        //分类型创建任务顶点
        if (taskType == TaskType.http.code) {
            heraContextInfo = new HeraContextInfo(sceneId, serialLinkDTO.getSerialLinkID(), httpApiInfo.getApiID(), mibenchTask.getReportId());
            calVertex = createHttpCalVertex(heraContextInfo, apiIndex, apiTask.getId(), httpApiInfo, globalAttachments, globalTspAuthInfo, apiOneAgentOriTps, dagTask, outputParamsMap);
        } else if (taskType == TaskType.dubbo.code) {
            heraContextInfo = new HeraContextInfo(sceneId, serialLinkDTO.getSerialLinkID(), dubboApiInfo.getApiID(), mibenchTask.getReportId());
            calVertex = createDubboCalVertex(heraContextInfo, apiIndex, apiTask.getId(), dubboApiInfo, globalAttachments, apiOneAgentOriTps, dagTask, outputParamsMap);
        }
        return calVertex;
    }

    /**
     * 解析构建具有条件依赖的顶点关系
     */
    private void parseTaskFilterConditionDep(TaskVertexData<NodeInfo> currentNode,
                                             CopyOnWriteArrayList<CheckPointInfo> filterConditions,
                                             Map<String, Integer> outputParamsMap,
                                             List<TaskVertexData<NodeInfo>> taskList,
                                             List<TaskEdgeData> dependList, int taskType) {
        if (filterConditions != null && filterConditions.size() != 0) {
            List<ExprKey> keys = new ArrayList<>();

            for (CheckPointInfo filterCondition : filterConditions) {
                //条件 key 在出参中，即具有依赖关系
                String paramValue = filterCondition.getCheckObj();
                if (outputParamsMap.containsKey(paramValue)) {
                    int parentTaskVertexId = outputParamsMap.get(paramValue);

                    OutputParam outputParam;
                    Optional<TaskVertexData<NodeInfo>> optional = taskList.stream().filter(vertexData -> vertexData.getIndex() == parentTaskVertexId).findFirst();

                    TaskVertexData<NodeInfo> parentVertexData = optional.get();
                    if (taskType == TaskType.http.code) {
                        outputParam = parentVertexData.getData().getTask().getHttpData().getOutputParams().stream().filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();
                    } else if (taskType == TaskType.dubbo.code) {
                        outputParam = parentVertexData.getData().getTask().getDubboData().getOutputParams().stream().filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();
                    } else {
                        return;
                    }
                    ExprKey key = new ExprKey(parentTaskVertexId, outputParam.getOrigin(), outputParam.getParamName(), outputParam.getParseExpr(), new ArrayList<>());
                    keys.add(key);
                    //当前顶点依赖该上游顶点
                    TaskEdgeData edge = new TaskEdgeData(parentTaskVertexId, currentNode.getIndex());
                    if (!dependList.contains(edge)) {
                        dependList.add(edge);
                    }
                }
            }
            keys.forEach(key -> currentNode.getData().getExprMap().put(key, ""));
        }
    }

    /**
     * 解析构建 dubbo 请求类型的顶点的依赖关系
     */
    private void parseDubboTaskDep(TaskVertexData<NodeInfo> currentNode, Map<String, Integer> outputParamsMap, List<TaskVertexData<NodeInfo>> taskList, List<TaskEdgeData> dependList) {
        List<ExprKey> keys = new ArrayList<>();

        DubboData dubboData = currentNode.getData().getTask().getDubboData();

        // json串
        String jsonBody = dubboData.getRequestBody();

        Matcher m = EL_PATTERN_MUTI.matcher(jsonBody);

        while (m.find()) {
            String paramValue = m.group(1);
            //该 ${} 参数为上游链路参数,更新结点 key表达式列表
            if (outputParamsMap.containsKey(paramValue)) {
                int parentTaskVertexId = outputParamsMap.get(paramValue);
                Optional<TaskVertexData<NodeInfo>> optional = taskList.stream().filter(vertexData -> vertexData.getIndex() == parentTaskVertexId).findFirst();
                if (optional.isPresent()) {
                    TaskVertexData<NodeInfo> parentVertexData = optional.get();

                    OutputParam outputParam = parentVertexData.getData().getTask().getDubboData().getOutputParams().stream()
                            .filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();

                    List<String> putValueExpr = new ArrayList<>();
                    //用于指定更新http的数据
                    putValueExpr.add("dubboData");
                    //入参的位置index
                    putValueExpr.add(String.valueOf(0));
                    // 要替换的参数名,大json直接用 例：${name} 内容替代
                    putValueExpr.add(m.group(0));

                    ExprKey key = new ExprKey(parentTaskVertexId, outputParam.getOrigin(), outputParam.getParamName(), outputParam.getParseExpr(), putValueExpr);
                    keys.add(key);
                    //当前顶点依赖该上游顶点
                    TaskEdgeData edge = new TaskEdgeData(parentTaskVertexId, currentNode.getIndex());
                    if (!dependList.contains(edge)) {
                        dependList.add(edge);
                    }
                }
                keys.forEach(key -> currentNode.getData().getExprMap().put(key, ""));
            }
        }
    }

    /**
     * 解析构建http post请求类型的顶点的依赖关系
     */
    private void parseHttpPostTaskDep(TaskVertexData<NodeInfo> currentNode, Map<String, Integer> outputParamsMap, List<TaskVertexData<NodeInfo>> taskList, List<TaskEdgeData> dependList) {
        //post 参数
        //例：http://www.baidu.com"

        List<ExprKey> keys = new ArrayList<>();

        HttpData httpData = currentNode.getData().getTask().getHttpData();

        List<Object> postParam = httpData.getParams();
        if (httpData.getContentType().equals(Constants.CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
            //表单
            for (int index = 0; index < postParam.size(); index++) {
                if (postParam.get(index).toString().startsWith("${")) {
                    String paramValue = Util.getElKey(postParam.get(index).toString()).getKey();
                    //该 ${} 参数为上游链路参数,更新结点 key表达式列表
                    if (outputParamsMap.containsKey(paramValue)) {
                        int parentTaskVertexId = outputParamsMap.get(paramValue);
                        Optional<TaskVertexData<NodeInfo>> optional = taskList.stream().filter(vertexData -> vertexData.getIndex() == parentTaskVertexId).findFirst();
                        if (optional.isPresent()) {
                            TaskVertexData<NodeInfo> parentVertexData = optional.get();

                            OutputParam outputParam = parentVertexData.getData().getTask().getHttpData().getOutputParams().stream().filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();

                            List<String> putValueExpr = new ArrayList<>();
                            //用于指定更新http的数据
                            putValueExpr.add("httpData");
                            //入参的位置index
                            putValueExpr.add(String.valueOf(index));
                            //要替换的参数名
                            putValueExpr.add(httpData.getTypes().get(index).getName());

                            ExprKey key = new ExprKey(parentTaskVertexId, outputParam.getOrigin(), outputParam.getParamName(), outputParam.getParseExpr(), putValueExpr);
                            keys.add(key);
                            //当前顶点依赖该上游顶点
                            TaskEdgeData edge = new TaskEdgeData(parentTaskVertexId, currentNode.getIndex());
                            if (!dependList.contains(edge)) {
                                dependList.add(edge);
                            }
                        }
                        keys.forEach(key -> currentNode.getData().getExprMap().put(key, ""));
                    }
                }
            }
        } else if (httpData.getContentType().equals(Constants.CONTENT_TYPE_APP_JSON)) {
            // json串
            String jsonBody = httpData.getJsonParam().get();
            Matcher m = EL_PATTERN_MUTI.matcher(jsonBody);

            while (m.find()) {
                String paramValue = m.group(1);
                //该 ${} 参数为上游链路参数,更新结点 key表达式列表
                if (outputParamsMap.containsKey(paramValue)) {
                    int parentTaskVertexId = outputParamsMap.get(paramValue);
                    Optional<TaskVertexData<NodeInfo>> optional = taskList.stream().filter(vertexData -> vertexData.getIndex() == parentTaskVertexId).findFirst();
                    if (optional.isPresent()) {
                        TaskVertexData<NodeInfo> parentVertexData = optional.get();

                        OutputParam outputParam = parentVertexData.getData().getTask().getHttpData().getOutputParams().stream().filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();

                        List<String> putValueExpr = new ArrayList<>();
                        //用于指定更新http的数据
                        putValueExpr.add("httpData");
                        //入参的位置index
                        putValueExpr.add(String.valueOf(0));
                        // 要替换的参数名,大json直接用 例：${name} 内容替代
                        putValueExpr.add(m.group(0));

                        ExprKey key = new ExprKey(parentTaskVertexId, outputParam.getOrigin(), outputParam.getParamName(), outputParam.getParseExpr(), putValueExpr);
                        keys.add(key);
                        //当前顶点依赖该上游顶点
                        TaskEdgeData edge = new TaskEdgeData(parentTaskVertexId, currentNode.getIndex());
                        if (!dependList.contains(edge)) {
                            dependList.add(edge);
                        }
                    }
                    keys.forEach(key -> currentNode.getData().getExprMap().put(key, ""));
                }
            }
        }
    }

    private void parseHttpGetTaskDep(TaskVertexData<NodeInfo> currentNode, String url, Map<String, Integer> outputParamsMap, List<TaskVertexData<NodeInfo>> taskList, List<TaskEdgeData> dependList) {
        if (url.contains("?")) {
            //带参数
            //例：http://www.baidu.com?name=${name}&age=${rAge}&k=v"
            //paramStr： name=${name}&age=${rAge}&k=v
            String paramStr = url.split("\\?", 2)[1];
            String[] paramPairStrs = paramStr.split("&");

            List<ExprKey> keys = new ArrayList<>();

            for (int index = 0; index < paramPairStrs.length; index++) {
                String[] kV = paramPairStrs[index].split("=", 2);
                if (kV[1].startsWith("${")) {
                    String paramValue = Util.getElKey(kV[1]).getKey();
                    //该 ${} 参数为上游链路参数,更新结点 key表达式列表
                    if (outputParamsMap.containsKey(paramValue)) {
                        int parentTaskVertexId = outputParamsMap.get(paramValue);
                        Optional<TaskVertexData<NodeInfo>> optional = taskList.stream().filter(vertexData -> vertexData.getIndex() == parentTaskVertexId).findFirst();
                        if (optional.isPresent()) {
                            TaskVertexData<NodeInfo> parentVertexData = optional.get();

                            OutputParam outputParam = parentVertexData.getData().getTask().getHttpData().getOutputParams().stream().filter(tmpOutputParam -> tmpOutputParam.getParamName().equals(paramValue)).findFirst().get();

                            List<String> putValueExpr = new ArrayList<>();
                            //用于指定更新http的数据
                            putValueExpr.add("httpData");
                            //入参的位置index
                            putValueExpr.add(String.valueOf(index));
                            //参数名
                            putValueExpr.add(kV[0]);

                            ExprKey key = new ExprKey(parentTaskVertexId, outputParam.getOrigin(), outputParam.getParamName(), outputParam.getParseExpr(), putValueExpr);
                            keys.add(key);
                            //当前顶点依赖该上游顶点
                            TaskEdgeData edge = new TaskEdgeData(parentTaskVertexId, currentNode.getIndex());
                            if (!dependList.contains(edge)) {
                                dependList.add(edge);
                            }
                        }
                        keys.forEach(key -> currentNode.getData().getExprMap().put(key, ""));
                    }
                }
            }
            currentNode.getData().getTask().getHttpData().setUrl(url.split("\\?")[0]);
        }
    }

    /**
     * 构建 http 任务顶点
     */
    private TaskVertexData<NodeInfo> createHttpCalVertex(HeraContextInfo heraContextInfo, int apiIndex, int taskId, HttpApiInfoDTO apiInfo, List<GlobalHeader> globalHeaders, TspAuthInfo globalTspAuthInfo, int oriTps, Task dagTask, Map<String, Integer> outputParamsMap) {
        //任务顶点
        TaskVertexData<NodeInfo> data = new TaskVertexData<>();
        //结点信息
        NodeInfo nodeInfo = new NodeInfo();
        //任务id
        nodeInfo.setId(taskId);
        //该结点返回结果名，非必须
        nodeInfo.setResultName("result");

        //发送给agent的 http 任务体
        Task task = new Task();
        task.setHeraContextInfo(heraContextInfo);

        task.setId(taskId);
        task.setDebug(dagTask.isDebug());
        task.setType(TaskType.http);
        task.setTimeout(apiInfo.getRequestTimeout());
        task.setQps(oriTps);

        task.setSuccessCode(dagTask.getSuccessCode());

        HttpData httpData = new HttpData();
        //启用流量数据
        ApiTrafficInfo apiTrafficInfo = apiInfo.getApiTrafficInfo();
        if (apiTrafficInfo != null && apiTrafficInfo.isEnableTraffic()) {
            httpData.setEnableTraffic(true);
            httpData.setTrafficConfId(apiTrafficInfo.getRecordingConfigId());
        }
        //启用x5
        ApiX5Info apiX5Info = apiInfo.getApiX5Info();
        if (apiX5Info != null && apiX5Info.isEnableX5()) {
            ApiX5InfoDTO x5InfoDTO = new ApiX5InfoDTO();
            BeanUtils.copyProperties(apiX5Info, x5InfoDTO);
            httpData.setApiX5InfoDTO(x5InfoDTO);
        }

        httpData.setUrl(apiInfo.getApiUrl());
        httpData.setTimeout(apiInfo.getRequestTimeout());

        //检查点
        if (apiInfo.getCheckPointInfoList() != null) {
            List<CheckPointInfo> checkPointInfoList = new ArrayList<>();
            apiInfo.getCheckPointInfoList().forEach(checkPointInfoDTO -> {
                CheckPointInfo checkPointInfo = new CheckPointInfo();
                BeanUtils.copyProperties(checkPointInfoDTO, checkPointInfo);
                checkPointInfoList.add(checkPointInfo);
            });
            httpData.setCheckPointInfoList(new CopyOnWriteArrayList<>(checkPointInfoList));
        }

        //过滤条件
        if (apiInfo.getFilterCondition() != null) {
            List<CheckPointInfo> filterConditionList = new ArrayList<>();
            apiInfo.getFilterCondition().forEach(filterCondition -> {
                CheckPointInfo checkPointInfo = new CheckPointInfo();
                BeanUtils.copyProperties(filterCondition, checkPointInfo);
                filterConditionList.add(checkPointInfo);
            });
            httpData.setFilterCondition(new CopyOnWriteArrayList<>(filterConditionList));
        }

        String headerStr = apiInfo.getHeaderInfo();
        List<HeaderInfo> headerInfoList = gson.fromJson(headerStr, new TypeToken<List<HeaderInfo>>() {
        }.getType());
        ConcurrentHashMap<String, String> headerMap = new ConcurrentHashMap<>(headerInfoList.size());

        //全局请求头
        if (globalHeaders != null && globalHeaders.size() != 0) {
            globalHeaders.forEach(globalHeader -> headerMap.putIfAbsent(globalHeader.getHeaderName(), globalHeader.getHeaderValue()));
        }
        //可以覆盖
        headerInfoList.forEach(headerInfo -> headerMap.put(headerInfo.getHeaderName(), headerInfo.getHeaderValue()));
        //mimeter标记
        headerMap.put("User-Agent", Const.MIMETER_UA_KEY);
        if (apiInfo.getApiRequestType() == Const.HTTP_REQ_GET) {
            httpData.setMethod("get");
        } else {
            httpData.setMethod("post");
            if (apiInfo.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                headerMap.put("Content-type", CONTENT_TYPE_APP_FORM);
                httpData.setContentType(CONTENT_TYPE_APP_FORM);
            } else {
                httpData.setContentType(apiInfo.getContentType());
                headerMap.putIfAbsent("Content-Type", apiInfo.getContentType());
            }
        }
        httpData.setHeaders(headerMap);

        TspAuthInfoDTO tspAuthInfoDTO = new TspAuthInfoDTO(false, "", "");
        //全局 tsp auth
        if (globalTspAuthInfo != null && globalTspAuthInfo.isEnableAuth()) {
            //全局启用
            tspAuthInfoDTO.setEnableAuth(true);
            tspAuthInfoDTO.setAccessKey(globalTspAuthInfo.getAccessKey());
            tspAuthInfoDTO.setSecretKey(globalTspAuthInfo.getSecretKey());
        }
        //接口层 tsp auth
        TspAuthInfo apiTspAuth = apiInfo.getApiTspAuth();
        if (apiTspAuth != null && apiTspAuth.isEnableAuth() && !apiTspAuth.getAccessKey().isEmpty() && !apiTspAuth.getSecretKey().isEmpty()) {
            //接口层级开启，并有自身配置数据
            tspAuthInfoDTO.setEnableAuth(true);
            tspAuthInfoDTO.setAccessKey(apiTspAuth.getAccessKey());
            tspAuthInfoDTO.setSecretKey(apiTspAuth.getSecretKey());
        } else if (apiTspAuth != null && !apiTspAuth.isEnableAuth()) {
            tspAuthInfoDTO.setEnableAuth(false);
        }
        //最终 tsp auth info
        httpData.setTspAuthInfoDTO(tspAuthInfoDTO);

        List<ParamType> types = new ArrayList<>();
        List<FormParamValue> paramValues;
        try {
            if (apiInfo.getApiRequestType() == Const.HTTP_REQ_GET || (apiInfo.getContentType() != null && (apiInfo.getContentType().equals(Constants.CONTENT_TYPE_APP_FORM) || apiInfo.getContentType().equals(CONTENT_TYPE_APP_FORM2)))) {
                //表单类型参数
                //例：[{"paramKey": "v1", "paramValue": "v2"}]
                List<Object> tmpValues = new ArrayList<>();
                if (null != apiInfo.getRequestInfo()) {
                    paramValues = gson.fromJson(apiInfo.getRequestInfo(), new TypeToken<List<FormParamValue>>() {
                    }.getType());
                    paramValues.forEach(paramValue -> {
                        ParamType paramType = new ParamType(ParamTypeEnum.primary, paramValue.getParamKey());
                        types.add(paramType);
                        tmpValues.add(paramValue.getParamValue());
                    });
                }
                httpData.initTypeList(types);

                //["a","b"...]
                httpData.getParams().addAll(new CopyOnWriteArrayList<>(tmpValues));

            } else if (apiInfo.getContentType().equals(Constants.CONTENT_TYPE_APP_JSON)) {
                //对象参数 例："[{\"name\": \"dzx\", \"age\": 19},{\"sex\": \"man\"}]"

                List<Object> objectList;


                if (null != apiInfo.getRequestInfoRaw()) {
                    objectList = gson.fromJson(apiInfo.getRequestInfoRaw(), new TypeToken<List<Object>>() {
                    }.getType());
                    if (objectList.size() == 0) {
                        httpData.setOriginJsonParam("");
                        httpData.getJsonParam().set("");
                        httpData.setPostParamJson("");
                    }
                    String val;
                    if (isStringType(objectList.get(0).getClass())) {
                        //字符串
                        val = objectList.get(0).toString();
                        httpData.setOriginJsonParam(val);
                        httpData.getJsonParam().set(val);
                        httpData.setPostParamJson(val);
                    } else {
                        val = gson.toJson(objectList.get(0));
                        httpData.setOriginJsonParam(val);
                        httpData.getJsonParam().set(val);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            log.error("error param type,api id:{},param:{},{}", apiInfo.getApiID(), apiInfo.getRequestInfo(), apiInfo.getRequestInfoRaw());
            throw new RuntimeException(e);
        }

        List<OutputParam> outputParams = new ArrayList<>();

        // key 表达式，需要取上游接口的出参定义合成
        apiInfo.getOutputParamInfos().forEach(oParam -> {
            outputParamsMap.putIfAbsent(oParam.getParamName(), apiIndex);
            outputParams.add(new OutputParam(oParam.getOrigin(), oParam.getParamName(), oParam.getParseExpr()));
        });

        httpData.setOutputParams(new CopyOnWriteArrayList<>(outputParams));

        task.setHttpData(httpData);
        nodeInfo.setTask(task);

        data.setData(nodeInfo);
        data.setIndex(apiIndex);
        return data;
    }

    /**
     * 构建dubbo任务顶点
     *
     * @return
     */
    private TaskVertexData<NodeInfo> createDubboCalVertex(HeraContextInfo heraContextInfo, int apiIndex, int taskId, DubboApiInfoDTO apiInfo, List<GlobalHeader> globalHeaders, int qps, Task dagTask, Map<String, Integer> outputParamsMap) {
        //任务顶点
        TaskVertexData<NodeInfo> data = new TaskVertexData<>();
        //结点信息
        NodeInfo nodeInfo = new NodeInfo();
        //任务id
        nodeInfo.setId(taskId);
        //该结点返回结果名，非必须
        nodeInfo.setResultName("result");

        //发送给agent的 dubbo 任务体
        Task task = new Task();
        task.setHeraContextInfo(heraContextInfo);

        task.setId(taskId);
        task.setDebug(dagTask.isDebug());
        task.setType(TaskType.dubbo);
        task.setTimeout(apiInfo.getRequestTimeout());
        task.setQps(qps);

        DubboData dubboData = new DubboData();
        dubboData.setServiceName(apiInfo.getServiceName());
        dubboData.setMethodName(apiInfo.getMethodName());
        if (apiInfo.getGroup() != null) {
            dubboData.setGroup(apiInfo.getGroup());
        }
        if (apiInfo.getVersion() != null) {
            dubboData.setVersion(apiInfo.getVersion());
        }
        if (apiInfo.getDubboMavenVersion() != null) {
            dubboData.setMavenVersion(apiInfo.getDubboMavenVersion());
        }

        //检查点
        if (apiInfo.getCheckPointInfoList() != null) {
            List<CheckPointInfo> checkPointInfoList = new ArrayList<>();
            apiInfo.getCheckPointInfoList().forEach(checkPointInfoDTO -> {
                CheckPointInfo checkPointInfo = new CheckPointInfo();
                BeanUtils.copyProperties(checkPointInfoDTO, checkPointInfo);
                checkPointInfoList.add(checkPointInfo);
            });
            dubboData.setCheckPointInfoList(new CopyOnWriteArrayList<>(checkPointInfoList));
        }
        //过滤条件
        if (apiInfo.getFilterCondition() != null) {
            List<CheckPointInfo> filterConditionList = new ArrayList<>();
            apiInfo.getFilterCondition().forEach(filterCondition -> {
                CheckPointInfo checkPointInfo = new CheckPointInfo();
                BeanUtils.copyProperties(filterCondition, checkPointInfo);
                filterConditionList.add(checkPointInfo);
            });
            dubboData.setFilterCondition(new CopyOnWriteArrayList<>(filterConditionList));
        }

        //注册中心环境
        dubboData.setDubboEnv(apiInfo.getDubboEnv());
        dubboData.setRequestTimeout(apiInfo.getRequestTimeout());

        String attachmentsStr = apiInfo.getAttachments();
        ConcurrentHashMap<String, String> attachmentMap = new ConcurrentHashMap<>(8);

        //全局请求头
        if (globalHeaders != null && globalHeaders.size() != 0) {
            globalHeaders.forEach(globalHeader -> attachmentMap.putIfAbsent(globalHeader.getHeaderName(), globalHeader.getHeaderValue()));
        }

        if (attachmentsStr != null && !attachmentsStr.isEmpty()) {
            List<Attachment> attachmentList = gson.fromJson(attachmentsStr, new TypeToken<List<Attachment>>() {
            }.getType());
            //可以覆盖
            attachmentList.forEach(headerInfo -> attachmentMap.put(headerInfo.getParamKey(), headerInfo.getParamValue()));
        }

        //mimeter标记
        attachmentMap.put("User-Agent", Const.MIMETER_UA_KEY);
        dubboData.setAttachments(attachmentMap);

        dubboData.setRequestParamTypeList(apiInfo.getRequestParamTypeList());
        if (apiInfo.getRequestBody() == null) {
            dubboData.setRequestBody("[]");
            dubboData.setOriginJsonParam("[]");
            dubboData.getJsonParam().set("[]");
        } else {
            dubboData.setRequestBody(apiInfo.getRequestBody());
            dubboData.setOriginJsonParam(apiInfo.getRequestBody());
            dubboData.getJsonParam().set(apiInfo.getRequestBody());
        }

        List<OutputParam> outputParams = new ArrayList<>();

        // key 表达式，需要取上游接口的出参定义合成
        apiInfo.getOutputParamInfos().forEach(oParam -> {
            outputParamsMap.putIfAbsent(oParam.getParamName(), apiIndex);
            outputParams.add(new OutputParam(oParam.getOrigin(), oParam.getParamName(), oParam.getParseExpr()));
        });

        dubboData.setOutputParams(new CopyOnWriteArrayList<>(outputParams));
        task.setDubboData(dubboData);
        nodeInfo.setTask(task);
        data.setData(nodeInfo);
        data.setIndex(apiIndex);
        return data;
    }

    /**
     * 过滤链路配置
     */
    private void filterLink(SceneDTO sceneInfo) {
        //过滤未启用的链路
        sceneInfo.setSerialLinkDTOs(sceneInfo.getSerialLinkDTOs().stream().filter(SerialLinkDTO::getEnable).collect(Collectors.toList()));
        List<String> enableLinkNames = sceneInfo.getSerialLinkDTOs().stream().map(SerialLinkDTO::getSerialLinkName).toList();
        sceneInfo.setApiBenchInfos(sceneInfo.getApiBenchInfos().stream().filter(apiBenchInfo -> enableLinkNames.contains(apiBenchInfo.getSerialName())).collect(Collectors.toList()));

        //默认100%发压比例
        int rpsRate;
        if (sceneInfo.getRpsRate() == null) {
            rpsRate = 100;
        } else {
            rpsRate = sceneInfo.getRpsRate();
        }
        //配比
        sceneInfo.getApiBenchInfos().forEach(apiBenchInfo -> {
            apiBenchInfo.setLinkTps((int) Math.ceil(apiBenchInfo.getLinkTps() * (rpsRate / 100d)));
            apiBenchInfo.setOriginRps((int) Math.ceil(apiBenchInfo.getOriginRps() * (rpsRate / 100d)));
            apiBenchInfo.setMaxRps((int) Math.ceil(apiBenchInfo.getMaxRps() * (rpsRate / 100d)));
        });
    }

    public static boolean isStringType(Class<?> clazz) {
        return clazz.getTypeName().startsWith("java.lang.String");
    }

    private MibenchTask buildMibenchTask(Task task) {
        MibenchTask mibenchTask = new MibenchTask();
        mibenchTask.setSceneId(task.getSceneId());
        mibenchTask.setCtime(System.currentTimeMillis());
        mibenchTask.setUtime(System.currentTimeMillis());
        if (task.getTime() != 0) {
            mibenchTask.setTime(task.getTime());
        }
        mibenchTask.setState(TaskStatus.Init.code);
        return mibenchTask;
    }

    /**
     * 获取某台机器host文件内容
     */
    public HttpResult loadHostsFile(LoadHostsFileReq loadHostsFileReq) {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        if (loadHostsFileReq.getAgentIp() == null) {
            return HttpResult.success("ok");
        }

        AgentReq req = new AgentReq();
        req.setCmd(AgentReq.LOAD_HOST_CMD);

        int i = 0;
        boolean find = false;
        for (int index = 0; index < agentList.size(); index++) {
            String[] ipAndPort = agentList.get(index).getRemoteAddr().split(":", 2);
            if (ipAndPort[0].equals(loadHostsFileReq.getAgentIp())) {
                i = index;
                find = true;
                break;
            }
        }
        if (!find) {
            return HttpResult.fail(500, "call agent error", "发压机不存在");
        }
        HostsFileResult tr;
        try {
            tr = syncLoadHostsFromAgent(agentList.get(i), req);
            return HttpResult.success(tr.getHostsFile());
        } catch (Exception e) {
            log.error("call load host interface error");
            return HttpResult.fail(500, "call agent error", e.getMessage());
        }
    }

    /**
     * 手动修改单台机器host文件
     */
    public HttpResult manualEditHosts(HostForAgentReq hostForAgentReq) {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        if (hostForAgentReq.getAgentIp() == null) {
            return HttpResult.success("ok");
        }

        AgentReq req = new AgentReq();
        req.setCmd(AgentReq.EDIT_HOST_CMD);

        AgentHostReq hostReq = new AgentHostReq();
        hostReq.setDomain(hostForAgentReq.getDomain());
        hostReq.setIp(hostForAgentReq.getIp());
        req.setAgentHostReqList(Collections.singletonList(hostReq));

        int i = 0;
        boolean find = false;
        for (int index = 0; index < agentList.size(); index++) {
            String[] ipAndPort = agentList.get(index).getRemoteAddr().split(":", 2);
            if (ipAndPort[0].equals(hostForAgentReq.getAgentIp())) {
                i = index;
                find = true;
                break;
            }
        }
        if (!find) {
            return HttpResult.fail(500, "call agent error", "发压机不存在");
        }
        try {
            syncCallAgent(agentList.get(i), req);
        } catch (Exception e) {
            log.error("call edit host interface error");
            return HttpResult.fail(500, "call agent error", e.getMessage());
        }
        return HttpResult.success("ok");
    }

    /**
     * 修改host文件
     */
    public HttpResult editHosts(DomainApplyReq domainApplyReq) {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        List<AgentChannel> tmpAgentList = new ArrayList<>();
        if (domainApplyReq.getAgentIPs() == null || domainApplyReq.getAgentIPs().size() == 0) {
            return HttpResult.success("ok");
        }
        AgentReq req = new AgentReq();
        req.setCmd(AgentReq.EDIT_HOST_CMD);

        AgentHostReq hostReq = new AgentHostReq();
        hostReq.setDomain(domainApplyReq.getDomain());
        hostReq.setIp(domainApplyReq.getIp());
        req.setAgentHostReqList(Collections.singletonList(hostReq));

        agentList.forEach(channel -> {
            String[] ipAndPort = channel.getRemoteAddr().split(":", 2);
            if (domainApplyReq.getAgentIPs().contains(ipAndPort[0])) {
                tmpAgentList.add(channel);
            }
        });
        try {
            callHostsAgents(tmpAgentList, req);
        } catch (Exception e) {
            log.error("call edit host interface error");
            return HttpResult.fail(500, "call agent error", e.getMessage());
        }
        return HttpResult.success("ok");
    }

    /**
     * 同步发压机hosts配置
     */
    public HttpResult syncHosts(SyncHostsReq syncHostsReq) {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        if (syncHostsReq.getAgentHostsConfList() == null || syncHostsReq.getAgentHostsConfList().size() == 0) {
            return HttpResult.success("ok");
        }

        AgentReq req = new AgentReq();
        req.setCmd(AgentReq.EDIT_HOST_CMD);

        syncHostsReq.getAgentHostsConfList().forEach(agentHostsConf -> {
            List<AgentHostReq> hostReqList = new ArrayList<>();

            agentHostsConf.getDomainConfs().forEach(hostConf -> {
                AgentHostReq hostReq = new AgentHostReq();
                hostReq.setDomain(hostConf.getDomain());
                hostReq.setIp(hostConf.getIp());
                hostReqList.add(hostReq);
            });
            req.setAgentHostReqList(hostReqList);

            int i = 0;
            boolean find = false;
            for (int index = 0; index < agentList.size(); index++) {
                String[] ipAndPort = agentList.get(index).getRemoteAddr().split(":", 2);
                if (ipAndPort[0].equals(agentHostsConf.getAgentIp())) {
                    i = index;
                    find = true;
                    break;
                }
            }
            if (find) {
                try {
                    syncCallAgent(agentList.get(i), req);
                } catch (Exception e) {
                    log.error("call edit host interface error");
                }
            }
        });
        return HttpResult.success("ok");
    }

    /**
     * 删除某项域名绑定配置
     */
    public HttpResult delHosts(DelHostForAgentsReq req) {
        int num = AgentContext.ins().list().size();
        if (num <= 0) {
            return HttpResult.fail(500, "agent num <= 0", "暂无可用的发压机");
        }
        List<AgentChannel> agentList = new ArrayList<>(AgentContext.ins().map.values());
        List<AgentChannel> tmpGgentList = new ArrayList<>();
        if (req.getAgentIps() == null || req.getAgentIps().size() == 0) {
            return HttpResult.success("ok");
        }
        AgentReq agentReq = new AgentReq();
        agentReq.setCmd(AgentReq.DEL_HOST_CMD);

        List<AgentHostReq> hostReqList = new ArrayList<>();
        AgentHostReq hostReq = new AgentHostReq();
        hostReq.setDomain(req.getDomain());
        hostReqList.add(hostReq);
        agentReq.setAgentHostReqList(hostReqList);

        agentList.forEach(channel -> {
            String[] ipAndPort = channel.getRemoteAddr().split(":", 2);
            if (req.getAgentIps().contains(ipAndPort[0])) {
                tmpGgentList.add(channel);
            }
        });
        try {
            callHostsAgents(tmpGgentList, agentReq);
        } catch (Exception e) {
            log.error("call edit host interface error");
            return HttpResult.fail(500, "call agent error", e.getMessage());
        }
        return HttpResult.success("ok");

    }

    private void callTaskAgents(List<AgentChannel> agentList, AgentReq agentReq) {
        for (int agentIndex = 0; agentIndex < agentList.size(); agentIndex++) {
            //该机器在所使用发压机集群中的序号索引
            agentReq.getTask().setAgentIndex(agentIndex);
            agentReq.setAddr(agentList.get(agentIndex).getRemoteAddr());
            RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, agentReq, gson);
            rpcServer.tell(agentList.get(agentIndex).getChannel(), req);
        }
    }

    /**
     * 修改host文件
     */
    private void callHostsAgents(List<AgentChannel> agentList, AgentReq agentReq) {
        agentList.forEach(ch -> {
            agentReq.setAddr(ch.getRemoteAddr());
            RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, agentReq, gson);
            rpcServer.tell(ch.getChannel(), req);
        });
    }

    private TaskResult syncCallAgent(AgentChannel channel, AgentReq agentReq) {
        agentReq.setAddr(channel.getRemoteAddr());
        RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, agentReq, gson);
        RemotingCommand response = rpcServer.sendMessage(channel, req, 10000);
        return gson.fromJson(new String(response.getBody()), TaskResult.class);
    }

    private HostsFileResult syncLoadHostsFromAgent(AgentChannel channel, AgentReq agentReq) {
        agentReq.setAddr(channel.getRemoteAddr());
        RemotingCommand req = RemotingCommand.createGsonRequestCommand(MibenchCmd.TASK, agentReq, gson);
        RemotingCommand response = rpcServer.sendMessage(channel, req, 10000);
        return gson.fromJson(new String(response.getBody()), HostsFileResult.class);
    }

}
