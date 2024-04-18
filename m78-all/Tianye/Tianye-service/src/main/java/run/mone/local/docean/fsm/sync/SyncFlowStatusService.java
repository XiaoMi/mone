package run.mone.local.docean.fsm.sync;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.fsm.bo.SyncFlowStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    public ConcurrentHashMap<String, SyncFlowStatus> syncFlowStatusMap = new ConcurrentHashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private static Gson gson = new Gson();

    public void addSyncFlowStatusMap(String flowRecordId, SyncFlowStatus.SyncNodeInput nodeInputs, SyncFlowStatus.SyncNodeOutput nodeOutputs) {

    }

    public void updateFlowStatusMap(String flowRecordId, SyncFlowStatus.SyncNodeInput nodeInputs, SyncFlowStatus.SyncNodeOutput nodeOutputs) {

    }

    public void syncFinalRst(String flowRecordId, int finalStatus, long durationTime, SyncFlowStatus.EndFlowOutput endFlowOutput, SyncFlowStatus.SyncNodeInput nodeInput, SyncFlowStatus.SyncNodeOutput nodeOutput) {

    }

    private void syncM78(String flowRecordId, boolean removeFromMap) {
        SyncFlowStatus syncFlowStatus = syncFlowStatusMap.get(flowRecordId);
        syncFlowStatus.setTimestamp(System.currentTimeMillis());
        String body = gson.toJson(syncFlowStatus);
        log.info("sync flowStatus to m78:{}", body);

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        HttpClientV5.post(m78Server + "/open/api/v1/flow/flowStatus/notify", body, header, 10000);
        if (removeFromMap){
            log.info("flowRecordId {} is done.", flowRecordId);
            syncFlowStatusMap.remove(flowRecordId);
        }
    }


}
