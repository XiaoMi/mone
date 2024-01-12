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

package run.mone.mimeter.engine.client.base;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.xiaomi.data.push.antlr.expr.Expr;
import com.xiaomi.data.push.graph.Graph;
import com.xiaomi.data.push.graph.Vertex;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.data.push.schedule.task.graph.TaskVertexData;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.StringUtils;
import common.Replacer;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import run.mone.event.Event;
import run.mone.mimeter.dashboard.bo.sceneapi.OutputOriginEnum;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.NodeInfo;
import run.mone.mimeter.engine.agent.bo.data.Result;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskResult;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.bo.TaskStatus;
import run.mone.mimeter.engine.common.ErrorCode;
import run.mone.mimeter.engine.common.Safe;
import run.mone.mimeter.engine.service.DatasetService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static common.Const.TASK_CTX_LINE_FLAG;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/30
 */
@Component(name = "dagMClient")
@Slf4j
public class DagClient implements IClient {

    /**
     * 使用jdk19的协程池
     */
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    private static final String UP_PARAM_METHOD = "updateParam";

    @Resource
    private Ioc ioc;

    @Resource
    private DatasetService datasetService;

    private static final ConcurrentHashMap<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();


    @Override
    public Result call(Task task, TaskContext context, CommonReqInfo commonReqInfo, SceneTotalCountContext totalCountContext) {
        Stopwatch sw = Stopwatch.createStarted();

        GraphTaskContext<NodeInfo> taskContext = task.getDagInfo();
        log.debug("taskContext info :{}", taskContext);
        //构建任务图
        Graph<TaskVertexData<NodeInfo>> graph = new Graph<>(taskContext.getTaskList().size());
        //添加顶点
        taskContext.getTaskList().forEach(it -> {
            it.getData().getTask().setDataMap(task.getDataMap());
            graph.addVertex(new Vertex(it.getIndex(), it));
        });
        //基于接口任务依赖关系添加边
        taskContext.getDependList().forEach(it -> graph.addEdge(it.getFrom(), it.getTo()));
        //主线程等待n个任务顶点执行完成
        final int taskSize = taskContext.getTaskList().size();
        CountDownLatch mainThreadLatch = new CountDownLatch(taskSize);
        List<String> latchKeyList = new ArrayList<>(16);
        try {
            graph.getVertexMap().forEach((key, value) -> {
                List<Integer> list = graph.dependList(key);
                log.debug("task id:{} {}", value.getData().getTask().getId(), list.size());
                if (list.size() > 0) {
                    String latchKey = getLatchKey(context.getNum(), value.getData().getTask().getId());
                    latchKeyList.add(latchKey);
                    latchMap.put(latchKey, new CountDownLatch(list.size()));
                }
            });
            //遍历任务图
            graph.bfsAll((v, d) -> {
                try {
                    //顶点结点数据
                    final NodeInfo nodeInfo = d.getData();
                    d.setTaskId(nodeInfo.getId());

                    //当前顶点的边，依赖的顶点
                    List<Integer> list = graph.dependList(d.getIndex());
                    d.setDependList(list);
                    //获取 set 边/子任务顶点
                    List<Integer> childList = graph.getAdj()[v];
                    d.setChildList(childList.stream().map(graph::getVertexData).collect(Collectors.toList()));

                    //初始化状态
                    d.setStatus(TaskStatus.Init.code);

                    //提交异步发压任务到协程池
                    CompletableFuture.runAsync(() -> doWork(d, context, mainThreadLatch, totalCountContext), pool);
                    return true;
                } catch (Throwable e) {
                    log.error("bfsAll error:" + e.getMessage(), e);
                    if (task.isDebug()) {
                        //当前顶点内的任务
                        Task currTask = d.getData().getTask();
                        TaskResult tr = new TaskResult();
                        tr.setDebug(currTask.isDebug());
                        tr.setId(currTask.getId());
                    }
                    return false;
                }
            });
        } finally {
            try {
                //主线程等待直到tag 任务超时
                mainThreadLatch.await(task.getTimeout(), TimeUnit.MILLISECONDS);

                //这里等整个dag执行完成后，再清理本次dag调用产生的latch
                clearLatch(latchKeyList);
            } catch (InterruptedException e) {
                log.error("[DagClient] call interrupted exception countDownLatch wait exception task id:" + task.getId() + ", report id:" + task.getReportId() + ", scene id:" +
                        task.getSceneId() + ", serial id:" + task.getSerialLinkID() + "; " + e.getMessage());
            }

            long time = sw.elapsed(TimeUnit.MILLISECONDS);
            if (time > 10000) {
                log.info("call dag client:{} use time:{}", task.getId() + ":" + context.getNum(), time);
            }

            //有没完成的任务
            if (context.getFinishTaskNum().get() != taskSize) {
                context.setError(true);
                return Result.fail(ErrorCode.ERROR_505.code, ErrorCode.ERROR_505.message, false);
            }
        }
        return Result.success(true);
    }

    private void doWork(TaskVertexData<NodeInfo> d, TaskContext context, CountDownLatch mainThreadLatch, SceneTotalCountContext errorContext) {
        try {
            //当前任务结点阻塞，等待依赖任务完成，参数拿全 (finally 的safe.run中会更新本结点参数)
            log.debug("doWork begin:{},latch num:{}", d.getData().getTask().getId(), latchMap.get(getLatchKey(context.getNum(), d.getData().getTask().getId())));
            await(latchMap.get(getLatchKey(context.getNum(), d.getData().getTask().getId())));
            log.debug("doWork await finish:{},latch num:{}", d.getData().getTask().getId(), latchMap.get(getLatchKey(context.getNum(), d.getData().getTask().getId())));

            //返回数据
            MutableObject resData = new MutableObject();
            //请求是否成功
            MutableObject success = new MutableObject(false);

            try {
                //当前顶点内的任务
                Task currTask = d.getData().getTask();
                TaskResult tr = new TaskResult();
                tr.setDebug(currTask.isDebug());
                tr.setId(currTask.getId());

                IClient client = getClient(currTask.getType());
                Result res;
                try {
                    //replace with the param dataset
                    CommonReqInfo commonReqInfo = null;
                    try {
                        commonReqInfo = datasetService.processTaskParam(currTask, (Integer) context.getAttachments().getOrDefault(TASK_CTX_LINE_FLAG, 1), currTask.isDebug());
                    } catch (Exception e) {
                        log.error("processTaskParam error,e:{}", e.getMessage());
                    }
                    res = client.call(currTask, context, commonReqInfo, errorContext);

                    if (currTask.isDebug()) {
                        tr.setCode(res.getCode());
                        tr.setOk(res.isOk());
                        tr.setCommonReqInfo(res.getCommonReqInfo());
                        tr.setResult(res.getData().toString());
                        tr.setRespHeaders(res.getRespHeaders());
                        if (!res.isOk() && res.getTriggerCp() != null) {
                            tr.setTriggerCpInfo(res.getTriggerCp());
                        }
                        if (!res.isOk() && res.getTriggerFilterCondition() != null) {
                            tr.setTriggerFilterCondition(res.getTriggerFilterCondition());
                        }
                        tr.setRt(res.getRt());
                        tr.setSize(res.getSize());
                        tr.getSuccess().incrementAndGet();
                    }
                    success.setObj(res.isSuccess());
                    resData.setObj(res.getData());
                } catch (Throwable e) {
                    log.error("call http error:" + e.getMessage(), e);
                    if (currTask.isDebug()) {
                        tr.setCode(ErrorCode.ERROR_500.code);
                        tr.setResult(e.getMessage());
                        tr.getFailure().incrementAndGet();
                    }
                    context.setError(true);
                    success.setObj(false);
                    resData.setObj(e.getMessage());
                }
                //任务执行成功
                d.setStatus(TaskStatus.Success.code);

                if (currTask.isDebug()) {
                    //异步推送更新调试结果
                    tr.setDebug(currTask.isDebug());
                    tr.setId(currTask.getId());
                    Event.ins().post(tr);
                }
            } catch (Throwable ex) {
                log.error(ex.getMessage());
                context.setError(true);
                throw new RuntimeException(ex);
            } finally {
                //依赖该任务的顶点列表
                log.debug("cur task id:{},child list:{}", d.getData().getTask().getId(), d.getChildList());
                d.getChildList().forEach(it -> {
                    //需要先将参数塞入线程本地变量
                    if (success.getObj()) {
                        //当前顶点任务执行成功，需要回填返回数据
                        //子节点信息
                        final NodeInfo childNI = it.getData();
                        //遍历处理参数表达式，将当前顶点取到的结果 更新替换到下游依赖结点的入参
                        Safe.run(() -> {
                            Map<String, List<Replacer>> replacerMap = new HashMap<>(8);
                            childNI.getExprMap().forEach((key, v) -> {
                                //强制使用为字符串
                                boolean forceStr = false;
                                //需要从这里取值(哪些子节点依赖我的结果,在这里update进去)
                                if (key.getIndex() == d.getIndex()) {
                                    log.debug("antlr version:{}", Expr.version());
                                    Object value;
                                    if (key.getOrigin() == OutputOriginEnum.BODY_TXT.code) {
                                        //文本类型，直接赋值
                                        value = resData.getObj().toString();
                                    } else {
                                        //json类型，解析
                                        if (key.getExpr().contains("|")) {
                                            //存在该标识符说明为list中取值
                                            //表达式 例如：params.json().get(data).get(goodIds)|0.string
                                            try {
                                                String[] exprArr = key.getExpr().split("\\|", 2);
                                                value = Expr.params(resData.getObj().toString(), exprArr[0]);
                                                JsonArray valList = (JsonArray) value;
                                                if (exprArr[1].contains(".")) {
                                                    String[] indexAndType = exprArr[1].split("\\.", 2);
                                                    value = Util.getListValByType(valList, Integer.parseInt(indexAndType[0]), indexAndType[1]);
                                                } else {
                                                    value = valList.get(Integer.parseInt(exprArr[1]));
                                                }
                                            } catch (Exception e) {
                                                value = "";
                                                log.error("parse get list val error,expr:{},cause by:{}", key.getExpr(), e.getMessage());
                                            }
                                        } else {
                                            try {
                                                value = Expr.params(resData.getObj().toString(), key.getExpr());
                                            } catch (Exception e) {
                                                log.error("parse params error,obj:{},expr:{},cause by:{}", resData.getObj(), key.getExpr(), e.getMessage());
                                                value = "";
                                            }
//                                        log.debug("debug parse param，expr:{},res:{},value:{}", key, resData.getObj().toString(), value);
                                            //强制指定为字符串
                                            if (key.getExpr().endsWith("getAsString()")) {
                                                forceStr = true;
                                            }
                                        }
                                    }
                                    log.debug("context.getAttachments().put before key :{}", key);
                                    if (StringUtils.isNotEmpty(key.getName())) {
                                        log.debug("context.getAttachments().put key :{}", key);
                                        context.getAttachments().put(key.getName(), value);
                                    }
                                    List<String> putValueExpr = key.getPutValueExpr();
                                    if (putValueExpr.size() > 0) {
                                        //httpData(dubboData)(数据协议类型)->0(index)->name(参数名)
                                        int paramIndex = Integer.parseInt(putValueExpr.get(1));
                                        String paramName = putValueExpr.get(2);
                                        List<Replacer> replacer = new ArrayList<>(Arrays.asList(new Replacer(childNI.getTask().getId(), paramIndex, paramName, value, forceStr)));
                                        List<Replacer> replacers = replacerMap.putIfAbsent(paramName, replacer);
                                        if (replacers != null) {
                                            replacers.addAll(replacer);
                                        }
                                    }
                                }
                            });
                            if (replacerMap.size() != 0) {
                                if (context.getAttachments().get("replacerMap") != null) {
                                    replacerMap.entrySet().stream().forEach(entry -> {
                                        List<Replacer> replacerList = ((Map<String, List<Replacer>>)context.getAttachments().get("replacerMap")).putIfAbsent(entry.getKey(), entry.getValue());
                                        if (replacerList != null) {
                                            replacerList.addAll(entry.getValue());
                                        }
                                    });
                                } else {
                                    context.getAttachments().put("replacerMap", replacerMap);
                                }
                            }
                        });
                    }
                    //处理更新完每个依赖本顶点的 顶点任务后，释放其 latch
                    log.debug("cur child task id:{}，context:{},latch num:{}", it.getData().getTask().getId(), context.getNum(), getLatchKey(context.getNum(), it.getData().getTask().getId()));

                    //在单个顶点拥有多个父顶点的情况下，直接remove会导致下游同样以该顶点为子顶点的子任务无法再对完成顶点完成 countDown 动作，导致该latch泄漏，对应顶点的awit操作将持续阻塞直到超时。
                    //CountDownLatch latch = latchMap.remove(getLatchKey(context.getNum(), it.getData().getTask().getId()));
                    CountDownLatch latch = latchMap.get(getLatchKey(context.getNum(), it.getData().getTask().getId()));
                    if (null != latch) {
                        latch.countDown();
                    }
                });
            }
        } catch (Throwable ex) {
            context.setError(true);
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        } finally {
            //当前顶点执行完成后需要释放一个latch
            context.getFinishTaskNum().incrementAndGet();
            mainThreadLatch.countDown();
        }
    }

    private void await(CountDownLatch latch) {
        if (null == latch) {
            return;
        }
        try {
            //容错处理.不至于线程不能归还
            latch.await(30, TimeUnit.SECONDS);
        } catch (Throwable e) {
            log.error("await error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void clearLatch(List<String> latchKeys) {
        latchKeys.forEach(latchMap::remove);
    }

    private static String getLatchKey(int num, int taskId) {
        return num + "_" + taskId;
    }

    private IClient getClient(TaskType type) {
        return ioc.getBean(type.name() + "MClient");
    }
}
