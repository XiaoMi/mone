package run.mone.local.docean.fsm;

import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.data.push.graph.Vertex;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.fsm.sync.SyncFlowStatusService;
import run.mone.local.docean.service.exceptions.GenericServiceException;
import run.mone.local.docean.util.GraphUtils;
import run.mone.local.docean.util.TyCollectionUtils;
import run.mone.m78.api.enums.FlowNodeTypeEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:12
 * <p>
 * 图状态,直接会把一张图中定义的flow全跑完
 */
@Slf4j
public class NewGraphState extends BotState {

    public static final int MAX_GOTO_TIMES = 19;

    private final ConcurrentHashMap<String, Phaser> phaserMap = new ConcurrentHashMap<>();

    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    private final AtomicInteger taskSize = new AtomicInteger();

    private Graph2<BotFlow> graph;

    private List<Integer> reverseSortedNodes;

    private boolean isSpecifiedStartNodeId;

    @Override
    public BotRes execute(BotReq req, BotContext context) {
        if (req.isSingleNodeTest()) {
            return singleNodeExecute(req, context);
        }
        isSpecifiedStartNodeId = req.getSpecifiedStartNodeId() == null ? false : true;
        String flowIdRecordId = req.getFlowId() + ":" + req.getFlowRecordId();
        graph = buildGraph(req);
        initReverseSortedNodes(req);
        if (isSpecifiedStartNodeId) {
            graph = graph.getSubgraphFrom(req.getSpecifiedStartNodeId());
        }

        if (GraphUtils.hasCycle(graph)) {
            log.error("id:{} hasCycle", flowIdRecordId);
            return BotRes.failure("hasCycle");
        }

        context.setMsgConsumer((pair) -> {
            BotFlow botFlow = graph.getVertexData(pair.getKey());
            botFlow.getQuestionQueue().add(pair.getValue());
        });

        FlowContext flowContext = buildFlowContext(context, req);
        FlowReq flowReq = buildFlowReq(req);
        taskSize.set(reverseSortedNodes.size());
        Phaser mainPhaser = new Phaser(taskSize.get());
        //主要用来一起执行
        log.info("mainPhaser.size:{}", taskSize.get());
        try {
            initializePhaserMap();
            //遍历任务图
            executeGraphTraversal(graph, req, flowReq, context, flowContext, mainPhaser);
        } catch (Exception e) {
            log.error("graph bsfAll error.", e);
            mainPhaser.forceTermination();
            context.getBotRes().set(BotRes.failure(e.getMessage()));
        } finally {
            //这里会阻塞住
            waitForTaskCompletionAndCleanup(req, mainPhaser);
        }

        if (null != context.getBotRes().get()) {
            return context.getBotRes().get();
        }

        //有没完成的任务
        if (hasIncompleteTasks(context)) {
            return getBotResWithError(context, flowIdRecordId);
        }

        if (null != flowContext.getFlowRes()) {
            return processBotRequestAndSyncStatus(req, flowContext);
        }
        return BotRes.failure("error");
    }

    private void initReverseSortedNodes(BotReq req) {
        if (isSpecifiedStartNodeId) {
            reverseSortedNodes = graph.topologicalSortFrom(req.getSpecifiedStartNodeId());
        } else {
            reverseSortedNodes = graph.topologicalSort();
        }
        Collections.reverse(reverseSortedNodes);
        log.info("sortedNodes after reverse:{}", reverseSortedNodes);
    }

    //有没完成的任务
    private boolean hasIncompleteTasks(BotContext context) {
        return context.getFinishTaskNum().get() != taskSize.get();
    }

    //有没完成的任务
    private @NotNull BotRes getBotResWithError(BotContext context, String flowIdRecordId) {
        context.setError(true);
        String errorMsg = "flowRecordId:" + flowIdRecordId + " error: finish num:" + context.getFinishTaskNum().get() + " task size:" + taskSize.get();
        log.error(errorMsg);
        return BotRes.failure(errorMsg);
    }

    private void waitForTaskCompletionAndCleanup(BotReq req, Phaser mainThreadPhaser) {
        try {
            //主线程等待直到 任务超时
            mainThreadPhaser.awaitAdvanceInterruptibly(mainThreadPhaser.getPhase(), 120, TimeUnit.MINUTES);
            //这里等整个dag执行完成后，再清理本次dag调用产生Phaser
            terminateAllPhasers();
        } catch (Exception e) {
            log.error("[NewGraphState.execute], call interrupted exception countDownLatch wait exception task id:{}", req.getFlowRecordId());
        } finally {
            log.info("main phaser finish");
        }
    }

    private @NotNull BotRes processBotRequestAndSyncStatus(BotReq req, FlowContext flowContext) {
        //若图中未连接end节点，需强制FinalEnd
        if (!flowContext.isFinalEnd() && req.isSyncFlowStatusToM78()) {
            log.info("is not final end, {}", req.getFlowRecordId());
            int finalStatus = flowContext.getFlowRes().getCode() == 0 ? 2 : (flowContext.getFlowRes().getCode() == FlowRes.CANCEL ? 4 : 3);
            SyncFlowStatusService syncFlowStatusServices = Ioc.ins().getBean(SyncFlowStatusService.class);
            syncFlowStatusServices.syncFinalRst(req.getFlowRecordId(), finalStatus, 0, null,
                    null, null, req.getM78RpcAddr(), req.getMeta());
        }
        return BotRes.success(flowContext.getFlowRes());
    }


    private void initializePhaserMap() {
        reverseSortedNodes.forEach(nodeId -> {
            BotFlow botFlow = graph.getVertexData(nodeId);
            //获取前置节点
            List<Integer> predecessors = graph.getPredecessors(nodeId);
            log.info("[NewGraphState.execute], node id:{}, predecessors:{}", nodeId, predecessors);
            if (!predecessors.isEmpty()) {
                String latchKey = getLatchKey(botFlow.getId());
                phaserMap.put(latchKey, new Phaser(predecessors.size()));
            }
        });
    }

    private void executeGraphTraversal(Graph2<BotFlow> graph, BotReq req, FlowReq flowReq, BotContext context, FlowContext flowContext, Phaser mainThreadLatch) {
        try {
            for (Integer nodeId : reverseSortedNodes) {
                // 顶点结点数据
                final GraphNode nodeInfo = buildGraphNode(graph, nodeId);
                log.info("submit doWork task, nodeId:{}", nodeInfo.getId());
                // 提交异步任务到协程池
                CountDownLatch latch = new CountDownLatch(1);
                CompletableFuture.runAsync(() -> doWork(req, flowReq, nodeInfo, context, flowContext, mainThreadLatch, latch), pool);
                latch.await(1, TimeUnit.MINUTES);
            }
        } catch (Throwable e) {
            log.error("[NewGraphState.execute], bfsAll error:" + e.getMessage(), e);
            throw new GenericServiceException(-1, "submit node Task failed");
        }
    }

    private GraphNode buildGraphNode(Graph2<BotFlow> graph, int nodeId) {
        GraphNode nodeInfo = new GraphNode();
        nodeInfo.setId(nodeId);
        nodeInfo.setCurrentVertex(graph.getVertexData(nodeId));
        //当前顶点依赖的父顶点
        List<Integer> list = graph.getPredecessors(nodeId);
        nodeInfo.setDependList(list);
        //当前顶点子顶点
        List<Integer> childList = graph.getSuccessors(nodeId);
        nodeInfo.setChildList(childList.stream().map(graph::getVertexData).collect(Collectors.toList()));
        //初始化状态
        nodeInfo.setStatus(0);
        return nodeInfo;
    }

    private Graph2<BotFlow> buildGraph(BotReq req) {
        Graph2<BotFlow> graph = new Graph2<>();
        IntStream.range(0, req.getFlowDataList().size()).forEach(i -> {
            FlowData data = req.getFlowDataList().get(i);
            data.setFlowRecordId(req.getFlowRecordId());
            data.setFlowId(req.getFlowId());
            data.setExecuteType(req.getExecuteType());
            BotFlow botFlow = createBotFlow(data);
            botFlow.setGraph(graph);
            botFlow.setM78RpcAddr(req.getM78RpcAddr());
            graph.addVertex(new Vertex<>(data.getId(), botFlow));
        });
        req.getNodeEdges().forEach(it -> graph.addEdge(it.getSourceNodeId(), it.getTargetNodeId()));
        return graph;
    }


    private FlowReq buildFlowReq(BotReq req) {
        return FlowReq.builder()
                .m78RpcAddr(req.getM78RpcAddr())
                .singleNodeTest(req.isSingleNodeTest())
                .history(req.getHistory())
                .userName(req.getUserName())
                .syncFlowStatusToM78(req.isSyncFlowStatusToM78())
                .ifEdgeMap(req.getIfEdgeMap())
                .elseEdgeMap(req.getElseEdgeMap())
                .outgoingEdgesMap(req.getOutgoingEdgesMap())
                .meta(req.getMeta())
                .build();
    }

    private FlowContext buildFlowContext(BotContext context, BotReq req) {
        FlowContext flowContext = new FlowContext();
        flowContext.setQuestionQueue(context.getQuestionQueue());
        flowContext.setM78RpcAddr(req.getM78RpcAddr());
        flowContext.setBotContext(context);
        if (!CollectionUtils.isEmpty(req.getReferenceData())) {
            Map<Integer, Map<String, ? extends ItemData>> referenceData = new HashMap<>();
            req.getReferenceData().forEach((k, v) -> {
                referenceData.put(k, v);
            });
            flowContext.setReferenceData(referenceData);
        }
        if (isSpecifiedStartNodeId) {
            flowContext.setStartTime(System.currentTimeMillis());
        }
        return flowContext;
    }


    //将gotoNodeId节点以及之后已经执行了的节点，重新扔到线程池中(只支持前置一个节点的重试了)
    private boolean resetFlowAfterGoto(BotReq req, FlowReq flowReq, FlowRes res, BotFlow vertex, Graph2<BotFlow> graph, BotContext botContext, FlowContext flowContext, Phaser mainThreadLatch) {
        if (res.getCode() == FlowRes.GOTO) {
            int gotoFlowNodeId = Integer.parseInt(res.getAttachement().get("_goto_").toString());
            List<Integer> startVertices = GraphUtils.getStartVertices(graph);

            // 目前的工作流应该只有一个起点,这里过滤掉其他入度为0的节点
            int startVertex = startVertices.stream()
                    .filter(v -> graph.getVertexData(v).getNodeType().equals(FlowNodeTypeEnum.BEGIN.getDesc()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("没有开始节点!"));
            List<Integer> longestNoBranchSubPath = GraphUtils.getLongestNoBranchSubPath(graph, startVertex, vertex.getId());
            if (!CollectionUtils.isEmpty(longestNoBranchSubPath)) {
                int gotoIndex = TyCollectionUtils.indexOf(longestNoBranchSubPath, gotoFlowNodeId);
                // HINT: check manual confirm中的vertex对应的id是否在longestNoBranchSubPath上
                if (gotoIndex != -1 && gotoIndex != longestNoBranchSubPath.size() - 1) {
                    for (int resetIndex = longestNoBranchSubPath.size() - 1; resetIndex >= gotoIndex; resetIndex--) {
                        reconstructGraphNode(req, flowReq, graph, botContext, flowContext, mainThreadLatch, longestNoBranchSubPath.get(resetIndex), resetIndex != gotoIndex);
                    }
                }
            } else {
                throw new IllegalStateException("不能跳转到不在当前节点路径上的或有分叉的节点!");
            }
        }
        return true;
    }

    private void reconstructGraphNode(BotReq req, FlowReq flowReq, Graph2<BotFlow> graph, BotContext botContext, FlowContext flowContext, Phaser mainThreadLatch, int resetVertexId, boolean await) {
        log.info("resetFlowAfterGoto re doWork id:{}", resetVertexId);
        final GraphNode nodeInfo = buildGraphNode(graph, resetVertexId);
        BotFlow resetFlowNode = graph.getVertexData(resetVertexId);
        resetFlowNode.reset();
        mainThreadLatch.register();
        CompletableFuture.runAsync(() -> doWork(req, flowReq, nodeInfo, botContext, flowContext, mainThreadLatch, await, null), pool);
        botContext.getFinishTaskNum().decrementAndGet();
        log.info("-------->rem:{}", botContext.getFinishTaskNum().get());
    }

    public BotRes singleNodeExecute(BotReq req, BotContext context) {
        FlowData data = req.getFlowDataList().get(0);
        data.setFlowRecordId(req.getFlowRecordId());
        data.setSingleNodeTest(req.isSingleNodeTest());
        data.setExecuteType(req.getExecuteType());
        BotFlow botFlow = createBotFlow(data);
        BotRes botRes = new BotRes<>();
        FlowContext flowContext = new FlowContext();
        flowContext.setStartTime(System.currentTimeMillis());
        flowContext.setM78RpcAddr(req.getM78RpcAddr());
        context.setMsgConsumer((pair) -> {
            botFlow.getQuestionQueue().add(pair.getValue());
        });

        FlowReq flowReq = buildFlowReq(req);
        FlowRes flowRes = null;
        try {
            botFlow.enter(flowContext, flowReq);
            flowRes = botFlow.execute0(flowReq, flowContext);
            botRes.setData(flowRes.getData());
            botRes.setMessage(flowRes.getMessage());
            botRes.setCode(flowRes.getCode());
        } catch (Exception e) {
            botRes.setCode(-1);
            botRes.setMessage(e.getMessage());
            log.error("singleNodeExecute error ", e);
            botFlow.exit(flowContext, flowReq, FlowRes.failure("execution failed"));
            return botRes;
        }

        if (flowRes.getCode() != 0) {
            botRes = BotRes.failure(flowRes.getMessage());
            log.error("singleNodeExecute graph abnormal exit {}", flowRes);
            botFlow.exit(flowContext, flowReq, flowRes);
            return botRes;
        }
        botFlow.exit(flowContext, flowReq, flowRes);
        return botRes;
    }


    private BotFlow createBotFlow(FlowData data) {
        // 临时解
        if (data.getType().equals("llmFileUnderstand")) {
            data.setType("llm");
        }
        BotFlow botFlow = (BotFlow) ReflectUtils.getInstance(FlowService.flowMap.get(data.getType()));
        botFlow.init(data);
        return botFlow;
    }

    private static String getLatchKey(int taskId) {
        return String.valueOf(taskId);
    }

    private void doWork(BotReq req, FlowReq flowReq, GraphNode graphNode, BotContext botContext, FlowContext flowContext, Phaser mainPhaser, CountDownLatch latch) {
        doWork(req, flowReq, graphNode, botContext, flowContext, mainPhaser, true, latch);
    }

    private void doWork(BotReq req, FlowReq flowReq, GraphNode graphNode, BotContext botContext, FlowContext flowContext, Phaser mainPhaser, boolean await, CountDownLatch latch) {
        try {
            graphNode.getCurrentVertex().setState(FlowState.pending);
            if (null != latch) {
                latch.countDown();
            }
            if (await) {
                //当前任务结点阻塞，等待依赖任务完成
                QuitType quitType = await(phaserMap.get(getLatchKey(graphNode.getId())), graphNode.getId());
                if (handleAbnormalQuit(botContext, quitType)) {
                    mainPhaser.forceTermination();
                    return;
                }
            }
            log.info("doWork start. FlowRecordId:{}, NodeId:{}", req.getFlowRecordId(), graphNode.getId());
            //当前顶点内的任务
            graphNode.getCurrentVertex().setState(FlowState.running);
            Pair<Boolean, FlowRes> pair = executeBotFlowNode(req, flowReq, graphNode.getCurrentVertex(), botContext, flowContext, mainPhaser);
            releaseChildNodeLatches(graphNode, pair);
        } catch (Throwable ex) {
            log.error("[NewGraphState.doWork], error: ", ex);
            botContext.setError(true);
            //外边的没必要阻塞了,错误是不能接受的
            mainPhaser.forceTermination();
            if (null != latch) {
                latch.countDown();
            }
            botContext.getBotRes().compareAndSet(null, BotRes.failure(ex.getMessage()));
        } finally {
            graphNode.getCurrentVertex().setState(FlowState.finish);
            botContext.getFinishTaskNum().incrementAndGet();
            log.info("-----------add. nodeId:{} FinishTaskNum:{}", graphNode.getId(), botContext.getFinishTaskNum().get());
            mainPhaser.arriveAndDeregister();
            log.info("mainPhaser arrivedParties:{}", mainPhaser.getArrivedParties());
        }
    }

    private static boolean handleAbnormalQuit(BotContext botContext, QuitType quitType) {
        //不是正常退出的
        if (quitType.equals(QuitType.error)) {
            botContext.getBotRes().compareAndSet(null, BotRes.failure(QuitType.error.name()));
            return true;
        }

        //超时了
        if (quitType.equals(QuitType.timeout)) {
            botContext.getBotRes().compareAndSet(null, BotRes.failure(QuitType.timeout.name()));
            return true;
        }

        //强制退出,直接退出即可
        if (quitType.equals(QuitType.forceTermination)) {
            log.info("forceTermination");
            botContext.getBotRes().compareAndSet(null, BotRes.failure(QuitType.forceTermination.name()));
            return true;
        }
        return false;
    }

    private void releaseChildNodeLatches(GraphNode graphNode, Pair<Boolean, FlowRes> pair) {
        //goto 不开锁
        if (null != pair.getValue() && pair.getValue().getCode() == FlowRes.GOTO) {
            return;
        }
        if (pair.getKey()) {
            //依赖该任务的顶点列表
            graphNode.getChildList().forEach(it -> {
                //处理更新完每个依赖本顶点的 顶点任务后，释放其 latch
                Phaser phaser = phaserMap.get(getLatchKey(it.id));
                if (null != phaser) {
                    log.info("node:{} is done,countDown child id:{}", graphNode.getId(), it.id);
                    phaser.arrive();
                }
            });
        }
    }

    //返回true表示此节点执行ok，可以arrive了
    private Pair<Boolean, FlowRes> executeBotFlowNode(BotReq req, FlowReq flowReq, BotFlow vertex, BotContext context, FlowContext flowContext, Phaser mainPhaser) {
        if (context.isError()) {
            log.info("context is error.");
            vertex.setSkip(true);
            vertex.setFinish(true);
            return Pair.of(true, null);
        }
        //跳过前置节点为空的非begin节点
        if (CollectionUtils.isEmpty(vertex.getGraph().getPredecessors(vertex.getId())) && !"begin".equals(vertex.getFlowName())) {
            log.info("不在图中的节点 skip:{}", vertex.getId());
            vertex.setSkip(true);
            vertex.setFinish(true);
            return Pair.of(true, null);
        }

        if (!vertex.isFinish()) {
            //如果前驱节点都是被跳过的节点,则这个节点没有被执行的必要
            int vertexId = vertex.getId();
            List<Integer> preList = vertex.getGraph().getPredecessors(vertexId);
            if (!preList.isEmpty() && preList.stream().allMatch(preId -> vertex.getGraph().getVertexData(preId).skip)) {
                vertex.setSkip(true);
                vertex.setFinish(true);
                log.info("skip vertex id:{} name:{}", vertexId, vertex.getName());
                return Pair.of(true, null);
            }

            FlowRes res;
            try {
                flowContext.setCancel(context.getCancel());
                log.info("flowContext.isCancel :{}", flowContext.getCancel());
                // HINT: trigger node execution
                res = vertex.enter(flowContext, flowReq);
                if (res.getCode() == FlowRes.CANCEL) {
                    log.info("before execute,cancel flow:{}", res.getMessage());
                    return Pair.of(true, null);
                }
                if (vertex.skip) {
                    //若if和else分支都包含该节点，需要在实际执行时reset not skip
                    log.info("reset skip vertex id:{} name:{}", vertexId, vertex.getName());
                    vertex.setSkip(false);
                }
                res = vertex.execute0(flowReq, flowContext);
                //ManualConfirm会在execute返回CANCEL
                if (res.getCode() == FlowRes.CANCEL) {
                    log.info("after execute,cancel flow:{}", res.getMessage());
                    return Pair.of(true, null);
                }
            } catch (Exception e) {
                return Pair.of(handleFlowExecutionFailure(flowReq, vertex, context, flowContext, e), null);
            }
            if (res.getCode() == FlowRes.GOTO && flowContext.addAndGetNodeGoToTimes(vertexId) > MAX_GOTO_TIMES) {
                return Pair.of(handleMaximumGotoExceeded(flowReq, vertex, context, flowContext, vertexId, res), res);
            }
            if (res.getCode() != FlowRes.SUCCESS && res.getCode() != FlowRes.GOTO) {
                return Pair.of(handleFlowFailure(flowReq, vertex, context, flowContext, res), res);
            }
            vertex.exit(flowContext, flowReq, res);

            //跳转到指定flow(实现goto功能)
            return Pair.of(resetFlowAfterGoto(req, flowReq, res, vertex, graph, context, flowContext, mainPhaser), res);
        } else {
            log.warn("vertex.isFinish, node id:{}", vertex.id);
            return Pair.of(true, null);
        }
    }

    private static boolean handleFlowFailure(FlowReq flowReq, BotFlow vertex, BotContext context, FlowContext flowContext, FlowRes res) {
        context.setBotRes(BotRes.failure(res.getMessage()));
        log.error("graph abnormal exit {}", res);
        vertex.exit(flowContext, flowReq, res);
        context.setError(true);
        vertex.setFailed(true);
        return true;
    }

    private static boolean handleFlowExecutionFailure(FlowReq flowReq, BotFlow vertex, BotContext context, FlowContext flowContext, Exception e) {
        log.error("execution failed. exit! nested exception is:", e);
        vertex.exit(flowContext, flowReq, FlowRes.failure("execution failed"));
        context.setError(true);
        vertex.setFailed(true);
        vertex.setFinish(true);
        return true;
    }

    private static boolean handleMaximumGotoExceeded(FlowReq flowReq, BotFlow vertex, BotContext context, FlowContext flowContext, int vertexId, FlowRes res) {
        context.setBotRes(BotRes.failure("exceeded maximum goto count"));
        log.error("exceeded maximum goto count nodeId:{},count:{}", vertexId, flowContext.getNodeGoToTimes().get(vertexId));
        res.setCode(FlowRes.GOTO_EXCEED_ERROR);
        res.setMessage("exceeded maximum goto count");
        vertex.exit(flowContext, flowReq, res);
        context.setError(true);
        vertex.setFailed(true);
        return true;
    }


    //卡住线程
    private QuitType await(Phaser phaser, int nodeId) {
        //没有依赖
        if (null == phaser) {
            log.warn("phaser is null,nodeId:{}", nodeId);
            return QuitType.normal;
        }
        try {
            log.info("await before, nodeId:{},Phase:{}, UnarrivedParties:{}", nodeId, phaser.getPhase(), phaser.getUnarrivedParties());
            //容错处理.不至于线程不能归还
            phaser.awaitAdvanceInterruptibly(phaser.getPhase(), 120, TimeUnit.MINUTES);
            //forceTermination出来的
            if (phaser.getPhase() < 0 && phaser.getUnarrivedParties() > 0) {
                return QuitType.forceTermination;
            }
            log.info("await after, nodeId:{},Phase:{}", nodeId, phaser.getPhase());
            return QuitType.normal;
        } catch (TimeoutException timeoutException) {
            return QuitType.timeout;
        } catch (Throwable e) {
            log.error("await error:{}", e.getMessage());
            return QuitType.error;
        }
    }

    private void terminateAllPhasers() {
        phaserMap.forEach((key, value) -> value.forceTermination());
    }


}
