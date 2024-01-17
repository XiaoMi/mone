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

package run.mone.mimeter.engine.service;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.TrafficDubboService;
import common.Const;
import common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.event.Event;
import run.mone.mimeter.engine.agent.bo.data.*;
import run.mone.mimeter.engine.agent.bo.hosts.HostBo;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.client.base.IClient;
import run.mone.mimeter.engine.event.EventProcessor;
import run.mone.mimeter.engine.agent.bo.task.*;
import run.mone.mimeter.engine.filter.preFilter.filters.TrafficFilter;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.Const.*;
import static common.Util.*;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Service
public class BenchEngineService {

    private static final Logger log = LoggerFactory.getLogger(BenchEngineService.class);

    private static final String logPrefix = "[BenchEngineService]";

    private static final int LOG_SAMPLING_PERCENTAGE = 5;

    private static final int MIN_TASK_EXECUTION_TIME = 10;

    private static final int TIME_ONE_SECOND = 1000;

    private final Gson gson = Util.getGson();

    @Resource
    private Ioc ioc;

    @Resource
    private EventProcessor eventProcessor;

    @Resource
    private DataCountService dataCountService;

    @Resource
    private DatasetService datasetService;

    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    private static final ConcurrentMap<Integer, TaskResult> contextMap = new ConcurrentHashMap<>();

    public void init() {
        Event.ins().register(eventProcessor);
    }

    public TaskResult submitTask(Context context, Task task) throws InterruptedException {

        Stopwatch dsw = Stopwatch.createStarted();

        String logMsg = logPrefix + "submitTask ";
        String logSuffix = ", task id: " + task.getId() + ", " + "scene id: " + task.getSceneId() +
                ", submit type: " + task.getSubmitTaskType() + ", task type: " + task.getType().code + " ";

        TaskResult tr = initTaskResult(task, context);

        IClient client = getClient(task.getType());
        final int taskId = task.getId();

        //分发任务前，加载，初始化参数、header数据源
        if (task.getType() == TaskType.dag) {
            //参数数据源
            TreeMap<String, List<String>> dataMap = new TreeMap<>();
            try {
                dataMap = datasetService.loadAndInitParamDataset(task);
                log.debug("loadAndInitParamDataset data map :{}",gson.toJson(dataMap));
            } catch (Exception e) {
                log.warn("load dataset error,e:{}", e.getMessage());
            } finally {
                log.debug("task {} load dataset use time:{}", task.getId(), dsw.elapsed(TimeUnit.MILLISECONDS));
            }
            task.setDataMap(dataMap);

            //流量参数数据源
            if (task.isEnableTraffic()){
                //若该链路开启流量参数使用
                datasetService.loadTrafficData(task);
            }
        }

        if (task.isDebug()) {
            //单接口调试或场景调试
            log.info("debug task:{} begin", task.getId());
            Result res;
            try {
                res = singleDebugOnce(task, client);
                tr.setCode(res.getCode());
                if (res.getData() != null) {
                    tr.setResult(res.getData().toString());
                } else {
                    tr.setResult("500 error");
                }
                tr.setOk(res.isOk());
                if (res.getRespHeaders() != null) {
                    tr.setRespHeaders(res.getRespHeaders());
                }
                if (!res.isOk() && res.getTriggerCp() != null) {
                    tr.setTriggerCpInfo(res.getTriggerCp());
                }
                tr.setRt(res.getRt());
                tr.setSize(res.getSize());
                tr.getSuccess().incrementAndGet();

            } catch (Throwable ex) {
                tr.getFailure().incrementAndGet();
                tr.setCode(500);
                tr.setOk(false);
                tr.setResult(ex.getMessage());
                log.error(logMsg + "single debug error" + logSuffix, ex);
            } finally {
                //每次调试完需要清理数据
                pool.submit(() ->{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error(logMsg + "single debug error" + logSuffix, e);
                    }
                    this.cleanLoadedDataCache(task);
                });
                log.info("task:{} debug one finish use time:{}", taskId, dsw.elapsed(TimeUnit.SECONDS));
            }
        } else {
            //执行场景压测
            //更新任务状态
            notifyTaskRunning(taskId);
            contextMap.put(task.getId(), tr);
            if (task.getTime() == 0) {
                task.setTime(MIN_TASK_EXECUTION_TIME);
            }

            //总协程计数器
            LongAdder counter = new LongAdder();

            //dag任务前缀标识
            AtomicInteger dagIndex = new AtomicInteger();

            int qps = task.getQps();
            RateLimiter limiter = RateLimiter.create(qps);
            int finalLogRate = (task.getLogRate() > 0 && task.getLogRate() <= 1000) ? task.getLogRate() : LOG_SAMPLING_PERCENTAGE;
            Set<Integer> logIndices = new HashSet<>(sampleByTimeAndQps(task.getTime(), qps, finalLogRate));

            //数据记录统计初始化
            SceneTotalCountContext totalCountContext = initCountCtx(task);

            log.info(logMsg + "task begin qps:{} task time:{}" + logSuffix, task.getQps(), task.getTime());
            //每次j循环内都是1s
            for (int j = 1; j <= task.getTime(); j++) {
                Stopwatch sw = Stopwatch.createStarted();

                int finalJ = j;
                int curQps = qps;
                // 手动命令控制 qps
                if (context.getTaskQps() != 0){
                    curQps = Util.calculateAgentRps(context.getTaskQps(), task.getAgentNum());
                }
                if (curQps != 0) {
                    if (curQps != qps) {
                        logIndices.clear();
                        logIndices.addAll(sampleByTimeAndQps(task.getTime(), curQps, finalLogRate));
                        qps = curQps;
                    }
                    limiter.setRate(qps);
                }
                int finalQps = qps;
                //当前该链路的实际rps
                if (context.getTaskQps() != 0){
                    totalCountContext.getDagTaskRps().setRps(context.getTaskQps());
                }else {
                    totalCountContext.getDagTaskRps().setRps(finalQps * task.getAgentNum());
                }
                if (context.getRpsRate() != 0){
                    totalCountContext.setRpsRate(context.getRpsRate());
                }
                counter.add(finalQps);
                log.debug(logMsg + "task begin final qps:{} task time:{}" + logSuffix, finalQps, task.getTime());
                IntStream.range(0, finalQps).forEach(i -> {
                    limiter.acquire();
                    pool.submit(() -> {
                        TaskContext taskCtx = new TaskContext();
                        taskCtx.setNum(dagIndex.incrementAndGet());
                        taskCtx.getAttachments().put(TASK_CTX_RECORD_LOG, !logIndices.isEmpty() ?
                                logIndices.contains(((finalJ - 1) % 10) * finalQps + i) : judgeByRateLog(finalLogRate));
                        taskCtx.getAttachments().put(TASK_CTX_LINE_FLAG, (finalJ - 1) * finalQps + i);
                        taskCtx.getAttachments().put(TASK_CTX_SCENE_QPS, task.getQps());
                        try {
                            //只会是 dag 任务
                            log.debug("context line flag: "+taskCtx.getAttachments().get(TASK_CTX_LINE_FLAG));
                            Result res = client.call(task, taskCtx, null, totalCountContext);
                            int code = res.getCode();

                            //链路整体是否执行成功
                            if (code == 0 || code == 200) {
                                tr.getSuccess().incrementAndGet();
                            } else {
                                tr.getFailure().incrementAndGet();
                            }
                        } catch (Throwable ex) {
                            tr.getFailure().incrementAndGet();
                            log.error(logMsg + "---pool submit error, i: " + i + ", " + logSuffix + ex.getMessage());
                        } finally {
                            counter.decrement();
                        }
                    });
                });

                long time = sw.elapsed(TimeUnit.MILLISECONDS);
                if (time < TIME_ONE_SECOND) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(TIME_ONE_SECOND - time);
                    } catch (Throwable ex) {
                        log.error(logMsg + "sleep error " + logSuffix + " error: " + ex.getMessage());
                    }
                    while (limiter.tryAcquire()) {
                        //把剩余的令牌都拿走
                    }
                }

                //proms打点
                dataCountService.recordProms(totalCountContext.getTmpTpsCounter(), totalCountContext.getTmpRpsCounter(), task);

                //推送频率 10s一次
                if (j % PUSH_STAT_RATE == 0) {
                    dataCountService.pushTotalCountData(totalCountContext, false);
                }

                //压测取消
                if (context.isCancel()) {
                    log.info(logMsg + "---------task is cancelled" + logSuffix);
                    // 手动结束通知
                    tr.setQuitByManual(true);
                    tr.setCancelType(context.getCancelType());
                    //退出阻塞
                    counter.reset();
                    break;
                }
            }

            //阻塞主线程直到所有调用结束
            this.aWait(counter);

            //任务结束最后一次把剩余数据都推送完成
            dataCountService.pushTotalCountData(totalCountContext, true);

            context.setFinish(true);
            contextMap.remove(task.getId());

            //压测结束清理缓存数据
            this.cleanLoadedDataCache(task);

            log.info(logMsg + "---------task has finished" + logSuffix);
        }
        //任务结果回调manager
        tr.setAddr(task.getAddr());
        try {
            Event.ins().post(tr);
        } catch (Exception e) {
            log.error("bench engine post finish failed:{}", e.getMessage());
        }
        return tr;
    }

    /**
     * 阻塞主线程
     */
    private void aWait(LongAdder counter) throws InterruptedException {
        //阻塞主线程直到所有调用结束
        while (counter.intValue() > 0) {
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    private TaskResult initTaskResult(Task task, Context context) {
        TaskResult tr = new TaskResult();
        tr.setSceneId(task.getSceneId());
        tr.setDebug(task.isDebug());
        tr.setId(task.getId());
        tr.setReportId(task.getReportId());
        tr.setContext(context);
        return tr;
    }

    private SceneTotalCountContext initCountCtx(Task task) {
        return SceneTotalCountContext.builder().
                reportId(task.getReportId())
                .sceneId(task.getSceneId())
                .sceneType(task.getSceneType().name())
                .taskId(task.getId()).
                agentNum(task.getAgentNum())
                .dagTaskRps(task.getDagTaskRps())
                .rpsRate(task.getRpsRate())
                .connectTaskNum(task.getConnectTaskNum())
                .lastTime(false)
                .totalReq(new LongAdder())
                .lossConnNum(new LongAdder())
                .totalTCount(new LongAdder())
                .totalSuccReq(new LongAdder())
                .totalErrReq(new LongAdder())
                .tmpRpsCounter(new LongAdder())
                .tmpTpsCounter(new LongAdder())
                .errCounterMap(new ConcurrentHashMap<>())
                .apiRtMap(new ConcurrentHashMap<>())
                .apiCountMap(new ConcurrentHashMap<>())
                .apiRpsMap(new ConcurrentHashMap<>())
                .apiTpsMap(new ConcurrentHashMap<>()).build();
    }

    private void notifyTaskRunning(int taskId) {
        TaskStatusBo statusBo = new TaskStatusBo();
        statusBo.setTaskId(taskId);
        statusBo.setTaskStatus(TaskStatus.Running);
        Event.ins().post(statusBo);
    }

    public void cancelTask(Task task) {
        task.getIds().forEach(taskId -> {
            TaskResult tr = contextMap.get(taskId);
            if (tr != null) {
                tr.setOpUser(task.getOpUser());
                Optional.of(tr).ifPresent(it -> it.getContext().setCancel(true));
            }
            //更新任务状态
            TaskStatusBo statusBo = new TaskStatusBo();
            statusBo.setSceneId(task.getSceneId());
            statusBo.setTaskId(taskId);
            statusBo.setTaskStatus(TaskStatus.Stopped);
            Event.ins().post(statusBo);
        });
    }

    /**
     * 手动调速
     */
    public void changeTaskQps(ChangeQpsReq req) {
        req.getDagTaskRpsList().forEach(dagTaskRps -> {
            TaskResult tr = contextMap.get(dagTaskRps.getTaskId());
            Optional.ofNullable(tr).ifPresent(it -> {
                it.getContext().setTaskQps(dagTaskRps.getRps());
                if (req.getRpsRate() != null){
                    it.getContext().setRpsRate(req.getRpsRate());
                }
            });
        });
    }


    /**
     * 加载 host 文件
     */
    public HostsFileResult loadHostsReq() {
        HostsFileResult rt = new HostsFileResult();
        List<String> hostsContent;
        try {
            hostsContent = HostsService.loadHostFile();
        } catch (Exception e) {
            log.error("load hosts file from machine error:{}", e.getMessage());
            hostsContent = new ArrayList<>();
        }
        rt.setHostsFile(gson.toJson(hostsContent));
        return rt;
    }

    /**
     * 修改更新host文件
     */
    public void editHostsFile(List<AgentHostReq> hostReqList) {

        List<HostBo> hostBoList = new ArrayList<>();

        hostReqList.forEach(hostReq -> {
            HostBo hostBo = new HostBo();
            hostBo.setDomain(hostReq.getDomain());
            hostBo.setIp(hostReq.getIp());
            hostBoList.add(hostBo);
        });

        HostsService.updateHostConfig(hostBoList);
    }

    /**
     * 删除host文件配置
     */
    public void delHostsFile(List<AgentHostReq> hostReqList) {

        List<HostBo> hostBoList = new ArrayList<>();

        hostReqList.forEach(hostReq -> {
            HostBo hostBo = new HostBo();
            hostBo.setDomain(hostReq.getDomain());
            hostBoList.add(hostBo);
        });

        HostsService.deleteDomainsConfig(hostBoList);
    }

    private Result singleDebugOnce(Task task, IClient client) {
        CommonReqInfo commonReqInfo = new CommonReqInfo();
        if (task.getType() != TaskType.dag) {
            commonReqInfo = getDebugParam(task);
        }
        return client.call(task, new TaskContext(), commonReqInfo, new SceneTotalCountContext());
    }

    private CommonReqInfo getDebugParam(Task task) {
        CommonReqInfo commonReqInfo = new CommonReqInfo();
        HttpData httpData = task.getHttpData();
        DubboData dubboData = task.getDubboData();
        if (task.getType() == TaskType.http) {
            if (httpData.getMethod().equalsIgnoreCase(Const.HTTP_GET)) {
                commonReqInfo.setParamsType(ReqParamType.Http_Get.code);
//                commonReqInfo.setGetOrFormParamsList(httpData.getParams());
                commonReqInfo.setQueryParamMap(httpData.httpGetTmpParams(httpData.getParams()));
                commonReqInfo.setParamJson(gson.toJson(httpData.httpGetParams()));
                commonReqInfo.setHeaders(httpData.getHeaders());
            } else if (httpData.getMethod().equalsIgnoreCase(Const.HTTP_POST)) {
                if (httpData.getContentType().equals(CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                    commonReqInfo.setParamsType(ReqParamType.Http_Post_Form.code);
//                    commonReqInfo.setGetOrFormParamsList(httpData.getParams());
                    commonReqInfo.setQueryParamMap(httpData.httpGetTmpParams(httpData.getParams()));
                    commonReqInfo.setParamJson(gson.toJson(httpData.httpGetParams()));
                } else {
                    commonReqInfo.setParamsType(ReqParamType.Http_Post_Json.code);
                    commonReqInfo.setParamJson(httpData.getPostParamJson());
                }
                commonReqInfo.setHeaders(httpData.getHeaders());
            }
        } else if (task.getType() == TaskType.dubbo) {
            commonReqInfo.setParamsType(ReqParamType.Dubbo.code);
            commonReqInfo.setParamJson(dubboData.getRequestBody());
        }
        return commonReqInfo;
    }

    private void cleanLoadedDataCache(Task task){
        //清理文件数据源缓存
        pool.submit(() -> {
            try {
                datasetService.processDataMapCache(task);
            } catch (InterruptedException e) {
                log.error("cleanLoadedDataCache task has finished remove data cache error:{}", e.getMessage());
            }
        });
        //清理流量数据缓存
        pool.submit(() -> TrafficFilter.cleanTrafficCache(task.getTrafficToPullConfList().getApiTrafficReqList().stream().
            map(PullApiTrafficReq::getTrafficConfigId).collect(Collectors.toList())));
    }
    private IClient getClient(TaskType type) {
        return ioc.getBean(type.name() + "MClient");
    }

}
