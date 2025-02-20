package run.mone.local.docean.fsm;

import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.data.push.graph.Vertex;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import run.mone.local.docean.fsm.bo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 16:12
 * <p>
 * 图状态,直接会把一张图中定义的flow全跑完
 */
@Slf4j
public class GraphState extends BotState {

    public static final int MAX_GOTO_TIMES = 19;

    @Getter
    private BotFlow currBotFlow;


    @Override
    public BotRes execute(BotReq req, BotContext context) {
        if (req.isSingleNodeTest()) {
            return singleNodeExecute(req, context);
        }
        boolean isStartNodeActive = req.getSpecifiedStartNodeId() == null ? true : false;
        int n = req.getFlowDataList().size();
        Graph2<BotFlow> graph = new Graph2<>();
        IntStream.range(0, n).forEach(i -> {
            FlowData data = req.getFlowDataList().get(i);
            data.setFlowRecordId(req.getFlowRecordId());
            data.setFlowId(req.getFlowId());
            data.setExecuteType(req.getExecuteType());
            BotFlow botFlow = createBotFlow(data);
            botFlow.setGraph(graph);
            botFlow.setM78RpcAddr(req.getM78RpcAddr());
            graph.addVertex(new Vertex<>(req.getFlowDataList().get(i).getId(), botFlow));
        });
        req.getNodeEdges().forEach(it -> graph.addEdge(it.getSourceNodeId(), it.getTargetNodeId()));
        //图转成list(执行逻辑比较简单)
        List<Integer> list = graph.topologicalSort();
        FlowReq flowReq = FlowReq.builder()
                .m78RpcAddr(req.getM78RpcAddr())
                .history(req.getHistory())
                .userName(req.getUserName())
                .syncFlowStatusToM78(req.isSyncFlowStatusToM78())
                .ifEdgeMap(req.getIfEdgeMap())
                .elseEdgeMap(req.getElseEdgeMap())
                .outgoingEdgesMap(req.getOutgoingEdgesMap())
                .build();
        FlowContext flowContext = new FlowContext();
        flowContext.setQuestionQueue(context.getQuestionQueue());
        flowContext.setM78RpcAddr(req.getM78RpcAddr());
        BotRes botRes = null;

        for (int j = 0; j < list.size(); ) {
            int i = list.get(j);
            BotFlow vertex = graph.getVertexData(i);

            //跳过前置节点为空的非begin节点
            if (CollectionUtils.isEmpty(vertex.getGraph().getPredecessors(vertex.getId())) && !"begin".equals(vertex.getFlowName())){
                log.info("不在图中的节点 {}", vertex.getId());
                vertex.setSkip(true);
                vertex.setFinish(true);
                j++;
                continue;
            }
            if (req.getSpecifiedStartNodeId() != null && req.getSpecifiedStartNodeId() == vertex.getId()) {
                isStartNodeActive = true;
            }
            if (!isStartNodeActive) {
                log.info("Not activated yet. skip vertex id:{} name:{}", vertex.getId(), vertex.getName());
                j++;
                continue;
            }
            this.currBotFlow = vertex;
            //如果前驱多个节点 且 包含非skip的，则set unfinished
            if (vertex.isFinish()) {
                int vertexId = vertex.getId();
                List<Integer> preList = vertex.getGraph().getPredecessors(vertexId);
                if (preList.size() > 1) {
                    if (preList.stream().filter(preId -> !vertex.getGraph().getVertexData(preId).skip && !"precondition".equals(vertex.getGraph().getVertexData(preId).getFlowName())).findAny().isPresent()) {
                        vertex.finish = false;
                        log.info("reset unfinished, {}", vertexId);
                    }
                }
            }

            if (!vertex.isFinish()) {
                //如果前驱节点都是被跳过的节点,则这个节点没有被执行的必要
                int vertexId = vertex.getId();
                List<Integer> preList = vertex.getGraph().getPredecessors(vertexId);
                if (preList.size()>0 && preList.stream().allMatch(preId -> vertex.getGraph().getVertexData(preId).skip)){
                    vertex.setSkip(true);
                    vertex.setFinish(true);
                    log.info("skip vertex id:{} name:{}", vertexId, vertex.getName());
                    continue;
                }

                FlowRes res;
                try {
                    flowContext.setCancel(context.getCancel());
                    log.info("flowContext.isCancel :{}", flowContext.getCancel());
                    res = vertex.enter(flowContext, flowReq);
                    if (res.getCode() == FlowRes.CANCEL) {
                        log.info("cancel flow:{}", res.getMessage());
                        break;
                    }
                    if (vertex.skip) {
                        //若if和else分支都包含该节点，需要在实际执行时reset not skip
                        log.info("reset skip vertex id:{} name:{}", vertexId, vertex.getName());
                        vertex.setSkip(false);
                    }
                    res = vertex.execute0(flowReq, flowContext);
                } catch (Exception e) {
                    log.error("execution failed. exit! nested exception is:", e);
                    vertex.exit(flowContext, flowReq, FlowRes.failure("execution failed"));
                    break;
                }
                if (res.getCode() == FlowRes.GOTO && flowContext.addAndGetNodeGoToTimes(vertexId) > MAX_GOTO_TIMES) {
                    botRes = BotRes.failure("exceeded maximum goto count");
                    log.error("exceeded maximum goto count nodeId:{},count:{}", vertexId, flowContext.getNodeGoToTimes().get(vertexId));
                    res.setCode(FlowRes.GOTO_EXCEED_ERROR);
                    res.setMessage("exceeded maximum goto count");
                    vertex.exit(flowContext, flowReq, res);
                    break;
                }
                if (res.getCode() != 0 && res.getCode() != FlowRes.GOTO) {
                    botRes = BotRes.failure(res.getMessage());
                    log.error("graph abnormal exit {}", res);
                    vertex.exit(flowContext, flowReq, res);
                    break;
                }
                vertex.exit(flowContext, flowReq, res);

                //跳转到指定flow(实现goto功能)
                int newIndex = resetFlowAfterGoto(res, list, graph, j);
                if (newIndex == j) {
                    j++;
                } else {
                    j = newIndex;
                }

            } else {
                j++;
            }
            log.info("--------wm final j {}", j);
        }
        if (null != botRes) {
            return botRes;
        }

        if (null != flowContext.getFlowRes()) {
            //若图中未连接end节点，需强制FinalEnd
            if (!flowContext.isFinalEnd() && req.isSyncFlowStatusToM78()){
                log.info("is not final end, {}", req.getFlowRecordId());
                int finalStatus = flowContext.getFlowRes().getCode() == 0 ? 2 : (flowContext.getFlowRes().getCode() == FlowRes.CANCEL ? 4 : 3);
                currBotFlow.getSyncFlowStatusServices().syncFinalRst(req.getFlowRecordId(), finalStatus, 0, null,
                        null, null, req.getM78RpcAddr(), req.getMeta());
            }
            return BotRes.success(flowContext.getFlowRes());
        }
        return BotRes.failure("error");
    }

    private static int resetFlowAfterGoto(FlowRes res, List<Integer> list, Graph2<BotFlow> graph, int j) {
        if (res.getCode() == FlowRes.GOTO) {
            int gotoFlowId = Integer.valueOf(res.getAttachement().get("_goto_").toString());
            log.info("resetFlowAfterGoto gotoFlowId:{},nodeIds:{}", gotoFlowId, list);
            int index = list.indexOf(gotoFlowId);
            if (-1 == index) {
                return j;
            }
            List<Integer> clearNodeIds = new ArrayList<>();
            for (int k = index; k < list.size(); k++) {
                BotFlow resetVertex = graph.getVertexData(list.get(k));
                resetVertex.reset();
                clearNodeIds.add(list.get(k));
                if (k == list.size()-1){
                    resetVertex.clearSyncFlowStatus(clearNodeIds, resetVertex.getM78RpcAddr());
                }
            }
            return index;
        }
        return j;
    }

    public BotRes singleNodeExecute(BotReq req, BotContext context) {
        FlowData data = req.getFlowDataList().get(0);
        data.setFlowRecordId(req.getFlowRecordId());
        data.setSingleNodeTest(req.isSingleNodeTest());
        BotFlow botFlow = createBotFlow(data);
        BotRes botRes = new BotRes<>();
        FlowContext flowContext = new FlowContext();
        flowContext.setStartTime(System.currentTimeMillis());
        flowContext.setQuestionQueue(context.getQuestionQueue());
        flowContext.setM78RpcAddr(req.getM78RpcAddr());
        FlowReq flowReq = FlowReq.builder().userName(req.getUserName())
                .syncFlowStatusToM78(req.isSyncFlowStatusToM78())
                .singleNodeTest(req.isSingleNodeTest())
                .ifEdgeMap(req.getIfEdgeMap())
                .elseEdgeMap(req.getElseEdgeMap())
                .outgoingEdgesMap(req.getOutgoingEdgesMap())
                .m78RpcAddr(req.getM78RpcAddr()).build();
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
        BotFlow botFlow = (BotFlow) ReflectUtils.getInstance(FlowService.flowMap.get(data.getType()));
        botFlow.init(data);
        return botFlow;
    }
}
