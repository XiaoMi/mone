package run.mone.local.docean.fsm.sync;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.NetUtils;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import run.mone.local.docean.context.TianyeContext;
import run.mone.local.docean.fsm.bo.SyncFlowNodeStatus;
import run.mone.local.docean.fsm.bo.SyncFlowStatus;
import run.mone.local.docean.protobuf.AiMessage;
import run.mone.local.docean.protobuf.AiResult;
import run.mone.local.docean.rpc.DoceanRpcClient;
import run.mone.local.docean.rpc.TianyeCmd;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author wmin
 * @date 2024/3/4
 */
@Service
@Slf4j
public class SyncFlowStatusService {

    @Value("${m78.server.addr}")
    private String m78Server;

    //flowRecordId <-> syncFlowStatus
    private ConcurrentHashMap<String, SyncFlowStatus> syncFlowStatusMap = new ConcurrentHashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private static Gson gson = new Gson();

    private Cache<String, String> flowRecordIdM78IpCache;

    @Resource
    private DoceanRpcClient client;

    public void clearSyncFlowStatusMapByNodeIds(String flowRecordId, List<Integer> nodeIds, String m78RpcServer) {
        log.info("clearSyncFlowStatusMapByNodeIds flowRecordId:{},nodeIds:{}", flowRecordId, nodeIds);
        if (syncFlowStatusMap.containsKey(flowRecordId)) {
            nodeIds.forEach(i -> {
                Optional.ofNullable(syncFlowStatusMap.get(flowRecordId))
                        .map(SyncFlowStatus::getNodeInputsMap)
                        .ifPresent(nodeInputsMap -> nodeInputsMap.remove(i));
                Optional.ofNullable(syncFlowStatusMap.get(flowRecordId))
                        .map(SyncFlowStatus::getNodeOutputsMap)
                        .ifPresent(nodeInputsMap -> nodeInputsMap.remove(i));
            });
            log.info("clearSyncFlowStatusMapByNodeIds syncM78FlowStatus.flowRecordId:{}", flowRecordId);
            syncM78FlowStatus(flowRecordId, false, System.currentTimeMillis(), m78RpcServer);
        }
    }

    public void addSyncFlowStatusMap(String flowRecordId, SyncFlowStatus.SyncNodeInput nodeInputs, SyncFlowStatus.SyncNodeOutput nodeOutputs, boolean isEnd) {
        Safe.runAndLog(() -> {
            if (null == nodeInputs && null == nodeOutputs) {
                return;
            }
            long timestamp = System.currentTimeMillis();
            //pool.submit(() -> {
            updateFlowStatusMap(flowRecordId, nodeInputs, nodeOutputs);
            boolean removeFromMap = (nodeOutputs!=null && StringUtils.isBlank(nodeOutputs.getErrorInfo())) ? false : true;
            syncM78FlowStatus(flowRecordId, isEnd ? true : removeFromMap, timestamp, nodeOutputs.getM78RpcAddr());

            //需要保证nodeInputs和nodeOutputs是同一个nodeId的
            SyncFlowNodeStatus syncFlowNodeStatus = SyncFlowNodeStatus.builder().flowRecordId(flowRecordId)
                    .nodeId(null == nodeInputs ? nodeOutputs.getNodeId() : nodeInputs.getNodeId())
                    .nodeInput(null == nodeInputs ? null : nodeInputs.getInputDetails())
                    .nodeOutput(nodeOutputs.getOutputDetails())
                    .durationTime(nodeOutputs.getDurationTime())
                    .status(nodeOutputs.getStatus())
                    .errorInfo(nodeOutputs.getErrorInfo())
                    .timestamp(timestamp).build();
            //todo syncM78NodeStatus(syncFlowNodeStatus);
            //});
        });

    }

    private void updateFlowStatusMap(String flowRecordId, SyncFlowStatus.SyncNodeInput nodeInputs, SyncFlowStatus.SyncNodeOutput nodeOutputs) {
        if (!syncFlowStatusMap.containsKey(flowRecordId)) {
            SyncFlowStatus syncFlowStatus = SyncFlowStatus.builder().flowRecordId(flowRecordId).build();
            if (nodeInputs != null) {
                syncFlowStatus.getNodeInputsMap().put(nodeInputs.getNodeId(), nodeInputs);
                if (StringUtils.isBlank(syncFlowStatus.getFlowId()) && StringUtils.isNotBlank(nodeInputs.getFlowId())){
                    syncFlowStatus.setFlowId(nodeInputs.getFlowId());
                }
                if (StringUtils.isBlank(syncFlowStatus.getTyIp())){
                    syncFlowStatus.setTyIp(NetUtils.getLocalHost());
                }
                if (syncFlowStatus.getExecuteType() == null){
                    syncFlowStatus.setExecuteType(nodeInputs.getExecuteType());
                }
            }
            if (nodeOutputs != null) {
                syncFlowStatus.getNodeOutputsMap().put(nodeOutputs.getNodeId(), nodeOutputs);
            }
            syncFlowStatusMap.put(flowRecordId, syncFlowStatus);
        }

         SyncFlowStatus syncFlowStatus = syncFlowStatusMap.get(flowRecordId);
        if (nodeInputs != null) {
            syncFlowStatus.getNodeInputsMap().put(nodeInputs.getNodeId(), nodeInputs);
        }
        if (nodeOutputs != null) {
            syncFlowStatus.getNodeOutputsMap().compute(nodeOutputs.getNodeId(), (k, v) -> {
                if (v == null) {
                    return nodeOutputs;
                } else {
                    v.setStatus(nodeOutputs.getStatus());
                    v.setOutputDetails(nodeOutputs.getOutputDetails());
                    v.setErrorInfo(nodeOutputs.getErrorInfo());
                    v.setDurationTime(nodeOutputs.getDurationTime());
                    return v;
                }
            });
        }

    }

    public void syncFinalCancelRst(String flowRecordId, int finalStatus, long durationTime, SyncFlowStatus.SyncNodeOutput nodeOutput) {
        updateFlowStatusMap(flowRecordId, null, nodeOutput);
        syncFlowStatusMap.get(flowRecordId).setEndFlowStatus(finalStatus);
        syncFlowStatusMap.get(flowRecordId).setDurationTime(durationTime);
        addSyncFlowStatusMap(flowRecordId, null, nodeOutput, true);
    }

    public void syncFinalRst(String flowRecordId, int finalStatus, long durationTime, SyncFlowStatus.EndFlowOutput endFlowOutput, SyncFlowStatus.SyncNodeInput nodeInput, SyncFlowStatus.SyncNodeOutput nodeOutput, String m78RpcAddr, Map<String, String> meta) {
        long timestamp = System.currentTimeMillis();
        updateFlowStatusMap(flowRecordId, nodeInput, nodeOutput);
        if (syncFlowStatusMap.containsKey(flowRecordId) && !CollectionUtils.isEmpty(meta)) {
            syncFlowStatusMap.get(flowRecordId).setMeta(meta);
        }
        syncFlowStatusMap.get(flowRecordId).setEndFlowStatus(finalStatus);
        if (durationTime > 0) {
            syncFlowStatusMap.get(flowRecordId).setDurationTime(durationTime);
        }
        if (endFlowOutput != null) {
            syncFlowStatusMap.get(flowRecordId).setEndFlowOutput(endFlowOutput);
        }
        syncM78FlowStatus(flowRecordId, finalStatus == 5 ? false : true, timestamp, StringUtils.isNotBlank(m78RpcAddr) ? m78RpcAddr : nodeOutput.getM78RpcAddr());
    }

    public void syncFinalRst(String flowRecordId, int finalStatus, long durationTime, SyncFlowStatus.EndFlowOutput endFlowOutput, SyncFlowStatus.SyncNodeInput nodeInput, SyncFlowStatus.SyncNodeOutput nodeOutput, String m78RpcAddr) {
        syncFinalRst(flowRecordId, finalStatus, durationTime, endFlowOutput, nodeInput, nodeOutput, m78RpcAddr, Collections.emptyMap());
    }

    public void syncM78FlowStatus(String flowRecordId, boolean removeFromMap, long timestamp, String m78RpcAddr) {
        SyncFlowStatus syncFlowStatus = syncFlowStatusMap.get(flowRecordId);
        syncM78FlowStatus(syncFlowStatus, flowRecordId, removeFromMap, timestamp, Maps.newHashMap(), "FLOW_EXECUTE_STATUS", m78RpcAddr);
    }

    public void syncM78FlowStatus(SyncFlowStatus syncFlowStatus, String flowRecordId, boolean removeFromMap, long timestamp, Map<String, String> meta, String messageType, String m78RpcAddr) {
        syncFlowStatus.setTimestamp(timestamp);
        syncFlowStatus.setMeta(CollectionUtils.isEmpty(meta)?syncFlowStatus.getMeta():meta);
        syncFlowStatus.setMessageType(messageType);
        String body = gson.toJson(syncFlowStatus);
        log.info("sync flowStatus to m78:{}", body);

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        AiMessage remoteMsg = AiMessage.newBuilder()
                .setCmd("FLOW_STATUS")
                .setFrom(TianyeContext.ins().getUserName())
                .setData(body)
                .build();
        if (StringUtils.isNotBlank(flowRecordIdM78IpCache.getIfPresent(flowRecordId))){
            m78RpcAddr = flowRecordIdM78IpCache.getIfPresent(flowRecordId);
            log.info("flowRecordIdM78IpCache is not null, flowRecordId:{}, m78RpcAddr:{}", flowRecordId, m78RpcAddr);
        }
        AiResult result = client.req(TianyeCmd.messageReq, m78RpcAddr, remoteMsg);

        log.info("call m78 notify , flowRecordId:{}, m78RpcAddr:{}, res:{}", flowRecordId, m78RpcAddr, result);
        if (removeFromMap) {
            log.info("flowRecordId {} is done.", flowRecordId);
            syncFlowStatusMap.remove(flowRecordId);
            flowRecordIdM78IpCache.invalidate(flowRecordId);
        }
    }

    public SyncFlowStatus getFlowStatus(String flowRecordId, String m78RpcAddress) {
        //如果m78RpcAddress不为空，则替换flowRecordIdM78IpCache key为flowRecordId value为m78RpcAddress
        if (StringUtils.isNotBlank(m78RpcAddress)) {
            flowRecordIdM78IpCache.put(flowRecordId, m78RpcAddress);
        }
        return syncFlowStatusMap.get(flowRecordId);
    }

    @PostConstruct
    public void init() {
        this.flowRecordIdM78IpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(5000)
                .build();
    }

}
