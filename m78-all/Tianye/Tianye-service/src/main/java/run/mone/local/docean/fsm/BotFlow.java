package run.mone.local.docean.fsm;

import com.google.gson.JsonObject;
import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.fsm.debug.DebugController;
import run.mone.local.docean.fsm.sync.SyncFlowStatusService;

import java.util.*;

@Slf4j
@Data
public abstract class BotFlow {

    private String flowRecordId;

    protected int id;

    private String name;

    protected Graph2<BotFlow> graph;

    protected LinkedHashMap<String, InputData> inputMap = new LinkedHashMap<>();

    protected LinkedHashMap<String, OutputData> outputMap = new LinkedHashMap<>();

    protected LinkedHashMap<String, InputData> batchMap = new LinkedHashMap<>();

    protected Map<String, String> flowMeta = new HashMap<>();

    protected boolean finish;

    private SyncFlowStatusService syncFlowStatusServices;

    //控制是否开启debug模式
    private DebugController debugController;

    private long startTime;


    public void init(FlowData data) {
        this.id = data.getId();
        this.flowRecordId = data.getFlowRecordId();
        this.name = data.getName();
        this.inputMap = data.getInputMap();
        this.outputMap = data.getOutputMap();
        this.batchMap = data.getBatchMap();
        this.flowMeta = data.getFlowMeta();
        debugController = new DebugController(data.isDebug());
        syncFlowStatusServices = Ioc.ins().getBean(SyncFlowStatusService.class);
    }

    protected void setDebug(boolean debug) {
        debugController.setDebug(debug);
    }

    protected void updateInputDataWithReferences(FlowContext context, boolean batch) {
    }


    protected void storeResultsInReferenceData(FlowContext context, JsonObject resObj) {
        storeResultsInReferenceData(context, resObj, false);
    }

    protected void storeResultsInReferenceData(FlowContext context, JsonObject resObj, boolean batch) {

    }


    @SneakyThrows
    public FlowRes execute0(FlowReq req, FlowContext context) {
        this.debugController.waitForDebug();
        return newExecute(req, context);
    }


    public FlowRes execute(FlowReq req, FlowContext context) {
        return FlowRes.success(null);
    }

    public void enter(FlowContext ctx, FlowReq req) {

    }

    public void exit(FlowContext ctx, FlowReq req, FlowRes res) {


    }

    public FlowRes newExecute(FlowReq req, FlowContext context) {
        return FlowRes.success("");
    }

    protected boolean isBatch() {
        boolean batch = true;
        if (null == batchMap || batchMap.isEmpty()) {
            batch = false;
        }
        return batch;
    }

    public abstract String getFlowName();

}
