package run.mone.local.docean.fsm;

import com.google.gson.*;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.local.docean.enums.FlowValueTypeEnum;
import run.mone.local.docean.fsm.bo.SyncFlowStatus;
import run.mone.local.docean.fsm.bo.*;
import run.mone.local.docean.fsm.debug.DebugController;
import run.mone.local.docean.fsm.sync.SyncFlowStatusService;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.JsonElementUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
public abstract class BotFlow {

    private int executeType;

    private String nodeType;

    private String flowId;

    private String flowRecordId;

    protected int id;

    protected String name;

    protected Graph2<BotFlow> graph;

    protected ConcurrentHashMap<String, InputData> inputMap = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<String, OutputData> outputMap = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<String, InputData> batchMap = new ConcurrentHashMap<>();

    protected Map<String, String> flowMeta = new ConcurrentHashMap<>();

    protected boolean finish;

    protected boolean failed;

    //直接跳过了
    protected boolean skip;

    //独享问题队列
    private LinkedBlockingQueue<String> questionQueue = new LinkedBlockingQueue<>();

    @Getter
    private AtomicReference<FlowState> state = new AtomicReference<>(FlowState.pending);

    public static final int MAX_BATCH_TIMES = 200;

    public void setState(FlowState flowState) {
        state.set(flowState);
    }

    public boolean isSkip() {
        return skip;
    }

    private SyncFlowStatusService syncFlowStatusServices;

    //控制是否开启debug模式
    private DebugController debugController;

    private long startTime;

    protected boolean batch;

    protected String rstSchema;

    private boolean singleNodeTest;

    private boolean generateCode;

    private String m78RpcAddr;

    protected LinkedBlockingQueue<String> getQuestionQueueBasedOnVersion(FlowContext context, int version) {
        //int version = context.getBotContext().getMsgVersion();
        if (version == 1) {
            return this.getQuestionQueue();
        } else{
            return context.getQuestionQueue();
        }
    }


    public void init(FlowData data) {
        this.id = data.getId();
        this.nodeType = data.getType();
        this.executeType = data.getExecuteType();
        this.flowId = data.getFlowId();
        this.flowRecordId = data.getFlowRecordId();
        this.singleNodeTest = data.isSingleNodeTest();
        this.name = data.getName();
        this.inputMap = data.getInputMap();
        this.outputMap = data.getOutputMap();
        this.batchMap = data.getBatchMap();
        this.flowMeta = data.getFlowMeta();
        debugController = new DebugController(data.isDebug());
        syncFlowStatusServices = Ioc.ins().getBean(SyncFlowStatusService.class);
    }

    //重置
    public void reset() {
        log.info("reset flow:{}", this.id);
        this.skip = false;
        this.finish = false;
        this.state.set(FlowState.pending);
    }

    public void clearSyncFlowStatus(List<Integer> nodeIds, String m78RpcAddr) {
        syncFlowStatusServices.clearSyncFlowStatusMapByNodeIds(flowRecordId, nodeIds, m78RpcAddr);
    }

    //处理批处理单次的batch类型的input入参，for单次execute
    protected void updateBatchInputsWithOutputData(InputData inData, OutputData outData) {
        String name = inData.getName();
        log.info("updateBatchInputsWithOutputData name:{}, referenceName:{}, value:{}", name, inData.getReferenceName(), outData.getValue());
        this.inputMap.entrySet().forEach(entry -> {
            InputData inputData = entry.getValue();
            if (inputData.isBatchType()) {
                if (inputData.getReferenceName().equals(name)) {
                    inputData.setValue(outData.getValue());
                    log.info("direct reference name:{}", name);
                } else if (inputData.getReferenceName().startsWith(name)) {
                    String field = inputData.getReferenceName();
                    String fieldParam = field.substring(field.indexOf(".") + 1);
                    if (field.indexOf("0.") != -1) {
                        fieldParam = field.substring(field.indexOf("0.") + 2);
                    }
                    JsonElement je = JsonElementUtils.queryFieldValue(outData.getValue(), fieldParam);
                    log.info("sub referenceName:{}, name:{},value:{}", field, inputData.getName(), je.toString());
                    inputData.setValue(je);
                }
            }
        });
    }

    //批处理全部跑完后，汇总inputs
    protected void updateBatchInputsWithOutputData(FlowReq req, FlowContext context, Map<String, List<OutputData>> dataMap) {
        dataMap.entrySet().forEach(j -> {
            List<OutputData> dataList = j.getValue();
            this.inputMap.entrySet().forEach(entry -> {
                InputData inputData = entry.getValue();
                if (inputData.isBatchType() && (inputData.getReferenceName().equals(j.getKey())
                        || (inputData.getReferenceName().contains(".") && inputData.getReferenceName().startsWith(j.getKey())))) {
                    JsonArray array = new JsonArray();
                    dataList.forEach(i -> {
                        if (inputData.getReferenceName().contains(".")) {
                            array.add(JsonElementUtils.queryFieldValue(i.getValue(), inputData.getReferenceName().substring(inputData.getReferenceName().indexOf(".") + 1)));
                        } else {
                            array.add(i.getValue());
                        }
                    });
                    log.info("updateBatchInputsWithOutputData add:{}", array);
                    inputData.setValue(array);
                }
            });
            log.info("updateBatchInputsWithOutputData done.{}", inputMap);
            updateInputDataWithReferences(context, true);
        });
        if (req.isSyncFlowStatusToM78() && !isBatch()) {
            SyncFlowStatus.SyncNodeInput syncNodeInput = null;
            if (context.getFlowRes().getInputs().containsKey(id)) {
                syncNodeInput = (SyncFlowStatus.SyncNodeInput) context.getFlowRes().getInputs().get(id);
            }
            syncFlowStatusServices.addSyncFlowStatusMap(flowRecordId, syncNodeInput, SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(req.getM78RpcAddr()).nodeId(id).nodeName(name).status(1).build(), false);
        }
    }

    protected void setDebug(boolean debug) {
        debugController.setDebug(debug);
    }

    protected void updateInputDataWithReferences(FlowContext context, boolean batch) {
        List<SyncFlowStatus.SyncNodeInputDetail> inputDetails = new ArrayList<>();
        this.inputMap.entrySet().forEach(entry -> {

            InputData inputData = entry.getValue();

            if (inputData.isTypeReference()) {
                inputData.setValue(context.queryFieldValueFromReferenceData(inputData.getFlowId(), inputData.getReferenceName()));
            }
            if ("imageReference".equals(inputData.getType())) {
                String imageUrl = context.queryFieldValueFromReferenceData(inputData.getFlowId(), inputData.getReferenceName()).getAsString();
                log.info("updateInputDataWithReferences imageUrl:{}", imageUrl);
                inputData.setValue(new JsonPrimitive(imageUrl));
            }
            if (inputData.isType2Reference()) {
                inputData.setValue2(context.queryFieldValueFromReferenceData(inputData.getFlowId2(), inputData.getReferenceName2()));
            }

            if (StringUtils.isNotEmpty(inputData.getReferenceName2())) {
                inputData.setValue2(context.queryFieldValueFromReferenceData(inputData.getFlowId2(), inputData.getReferenceName2()));
            }

            FlowRes flowRes = context.getFlowRes();
            if (null == flowRes) {
                flowRes = new FlowRes<>();
                context.setFlowRes(flowRes);
            }
            if (inputData.isOriginalInput() && (!inputData.isBatchType() || batch)) {
                SyncFlowStatus.SyncNodeInputDetail input = SyncFlowStatus.SyncNodeInputDetail.builder()
                        .name(entry.getKey()).value(JsonElementUtils.getValue(inputData.getValue())).valueType(inputData.getValueType())
                        .name2(inputData.getReferenceName2()).value2(JsonElementUtils.getValue(inputData.getValue2())).type2(inputData.getType2())
                        .operator(inputData.getOperator()).build();
                inputDetails.add(input);
            }
        });

        context.getFlowRes().getInputs().put(this.id, SyncFlowStatus.SyncNodeInput.builder().flowId(this.flowId).nodeId(this.id).executeType(this.executeType).nodeType(this.nodeType).inputDetails(inputDetails).build());
    }


    protected void storeResultsInReferenceData(FlowContext context, JsonObject resObj) {
        storeResultsInReferenceData(context, resObj, false, null);
    }

    //把结果存储到引用数据中
    protected SyncFlowStatus.SyncNodeOutput storeResultsInReferenceData(FlowContext context, JsonObject resObj, boolean batch, Integer nodeStatus) {
        List<SyncFlowStatus.SyncNodeOutputDetail> outputDetails = new ArrayList<>();
        Pair<String, String> batchInfo = Pair.of("", "");

        if (this.outputMap.size() == 1 && this.outputMap.containsKey("xw_output")) {
            String key = "xw_output";
            this.outputMap.put("xw_output", OutputData.builder().name(key).value(resObj).valueType("object").build());
            if (!batch) {
                SyncFlowStatus.SyncNodeOutputDetail outputDetail = SyncFlowStatus.SyncNodeOutputDetail.builder()
                        .name("xw_output")
                        .value(resObj.toString())
                        .valueType("object").build();
                outputDetails.add(outputDetail);
            }
        } else {
            this.outputMap.entrySet().forEach(it -> {
                String key = it.getKey();
                if (resObj == null) {
                    log.warn("storeResultsInReferenceData,resObj is null");
                    return;
                }
                JsonElement data = resObj.get(key);
                if (key.equals("outputList") && batch) {
                    data = resObj;
                }
                if (null == data) {
                    log.warn("storeResultsInReferenceData nodeId:{}, key:{} is empty", this.id, it.getKey());
                } else {
                    if (batch && it.getValue().getValueType().startsWith("Array")) {
                        batchInfo.setKey(it.getKey());
                        batchInfo.setValue(it.getValue().getValueType());
                        JsonElement finalData = data;
                        this.outputMap.compute(it.getKey(), (k, v) -> {
                            if (v.isEmptyValue()) {
                                JsonArray array = new JsonArray();
                                array.add(finalData);
                                log.info("init output");
                                return OutputData.builder().name(it.getKey()).value(array).valueType(it.getValue().getValueType()).schema(it.getValue().getSchema()).build();
                            } else {
                                v.getValue().getAsJsonArray().add(finalData);
                                log.info("add output");
                                return OutputData.builder().name(it.getKey()).value(v.getValue()).valueType(it.getValue().getValueType()).schema(it.getValue().getSchema()).build();
                            }
                        });
                    } else {
                        if (it.getValue().getValueType().startsWith("Array") && data.isJsonPrimitive()) {
                            parseAndAddJsonArrayToOutputMap(data, it.getValue());
                        } else {
                            this.outputMap.put(it.getKey(), OutputData.builder().name(it.getKey()).value(data).valueType(it.getValue().getValueType()).build());
                        }
                    }

                    if (!batch) {
                        SyncFlowStatus.SyncNodeOutputDetail outputDetail = SyncFlowStatus.SyncNodeOutputDetail.builder()
                                .name(it.getKey())
                                .value(JsonElementUtils.getValue(data))
                                .valueType(it.getValue().getValueType()).build();
                        outputDetails.add(outputDetail);
                    }
                }
            });
        }

        context.getReferenceData().put(this.id, this.outputMap);

        if (batch) {
            SyncFlowStatus.SyncNodeOutputDetail outputDetail = SyncFlowStatus.SyncNodeOutputDetail.builder()
                    .name(batchInfo.getKey())
                    .value(JsonElementUtils.getValue(this.outputMap.get(batchInfo.getKey()).getValue()))
                    .valueType(batchInfo.getValue()).build();
            outputDetails.add(outputDetail);
        }

        SyncFlowStatus.SyncNodeOutput syncNodeOutput = SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(context.getM78RpcAddr()).nodeId(this.id).nodeName(name).status(nodeStatus == null ? 2 : nodeStatus).outputDetails(outputDetails).build();
        context.getFlowRes().getOutputs().put(this.id, syncNodeOutput);
        log.info("final output: {},{}", this.id, context.getFlowRes().getOutputs().get(this.id));
        return syncNodeOutput;
    }

    protected void updateSyncFlowStatusAfterEachBatch(FlowReq req, SyncFlowStatus.SyncNodeOutput syncNodeOutput, Map<String, List<OutputData>> dataMap) {
        if (batch && req.isSyncFlowStatusToM78()) {
            List<SyncFlowStatus.SyncNodeInputDetail> inputDetails = new ArrayList<>();

            //batch类型的input从dataMap获取
            dataMap.entrySet().forEach(j -> {
                List<OutputData> dataList = j.getValue();
                this.inputMap.entrySet().forEach(entry -> {
                    InputData inputData = entry.getValue();
                    if (inputData.isBatchType() && (inputData.getReferenceName().equals(j.getKey())
                            || (inputData.getReferenceName().contains(".") && inputData.getReferenceName().startsWith(j.getKey())))) {
                        JsonArray array = new JsonArray();
                        dataList.forEach(i -> {
                            if (inputData.getReferenceName().contains(".")) {
                                array.add(JsonElementUtils.queryFieldValue(i.getValue(), inputData.getReferenceName().substring(inputData.getReferenceName().indexOf(".") + 1)));
                            } else {
                                array.add(i.getValue());
                            }
                        });
                        log.info("updateSyncFlowStatusAfterEachBatch add:{}", array);
                        SyncFlowStatus.SyncNodeInputDetail syncInput = SyncFlowStatus.SyncNodeInputDetail.builder()
                                .name(entry.getKey())
                                .value(JsonElementUtils.getValue(array))
                                .valueType(inputData.getValueType())
                                .build();
                        inputDetails.add(syncInput);
                    }
                });
            });

            //非batch类型的直接从input获取
            this.inputMap.entrySet().stream().filter(i -> i.getValue().isOriginalInput() && !i.getValue().isBatchType()).forEach(entry -> {
                InputData inputData = entry.getValue();
                SyncFlowStatus.SyncNodeInputDetail syncInput = SyncFlowStatus.SyncNodeInputDetail.builder()
                        .name(entry.getKey())
                        .value(JsonElementUtils.getValue(inputData.getValue()))
                        .valueType(inputData.getValueType())
                        .build();
                inputDetails.add(syncInput);
            });
            if (syncNodeOutput.getStatus() == 2) {
                syncNodeOutput.setDurationTime(System.currentTimeMillis() - startTime);
            }
            SyncFlowStatus.SyncNodeInput syncNodeInput = SyncFlowStatus.SyncNodeInput.builder().flowId(this.flowId).nodeId(this.id).executeType(this.executeType).nodeType(this.nodeType).inputDetails(inputDetails).build();
            syncFlowStatusServices.addSyncFlowStatusMap(flowRecordId, syncNodeInput, syncNodeOutput, false);//isSingleNodeTest() && syncNodeOutput.getStatus()!=1 ? true : false);
        }
    }

    private void parseAndAddJsonArrayToOutputMap(JsonElement data, OutputData outputData) {
        String dataString = data.getAsString();
        if (dataString != null && !dataString.isEmpty()) {
            try {
                JsonElement jsonElement = JsonParser.parseString(dataString);
                if (jsonElement.isJsonArray()) {
                    JsonArray array = jsonElement.getAsJsonArray();
                    this.outputMap.put(outputData.getName(), OutputData.builder().name(outputData.getName()).value(array).valueType(outputData.getValueType()).build());
                } else {
                    log.error("parseAndAddJsonArrayToOutputMap, Expected a JSON array but got: " + dataString);
                }
            } catch (JsonSyntaxException e) {
                log.error("parseAndAddJsonArrayToOutputMap, Invalid JSON: " + dataString);
            }
        }
    }

    public FlowRes execute0(FlowReq req, FlowContext context) {
        try {
            this.debugController.waitForDebug();
            this.rstSchema = getRstSchema();
            checkAndSetGenerateCodeFlagBasedOnSchema();
            return newExecute(req, context);
        } catch (Exception e){
            return FlowRes.failure(e.getMessage());
        }
    }

    private void checkAndSetGenerateCodeFlagBasedOnSchema() {
        if (StringUtils.isNotEmpty(this.rstSchema)) {
            JsonObject object = JsonParser.parseString(this.rstSchema).getAsJsonArray().get(0).getAsJsonObject();
            String type = object.get("valueType").getAsString();
            if (type.equals(FlowValueTypeEnum.CODE.getName())) {
                this.generateCode = true;
            }
        }
    }

    public String getRstSchema() {
        return "";
    }


    public FlowRes execute(FlowReq req, FlowContext context) {
        return FlowRes.success(null);
    }

    public FlowRes enter(FlowContext ctx, FlowReq req) {
        //被取消掉了
        if (ctx.getCancel().get()) {
            log.info("{} is cancel", getFlowName());
            cancel(ctx, req);
            FlowRes res = new FlowRes(FlowRes.CANCEL, "cancel");
            ctx.setFinalEnd(true);
            ctx.setFlowRes(res);
            return res;
        }
        startTime = System.currentTimeMillis();
        log.info("enter flow..... flowName:{},id:{},flowRecordId:{},startTime:{}", getFlowName(), id, flowRecordId, startTime);
        updateInputDataWithReferences(ctx, false);
        if (req.isSyncFlowStatusToM78()) {
            SyncFlowStatus.SyncNodeInput syncNodeInput = null;
            if (ctx.getFlowRes().getInputs().containsKey(id)) {
                syncNodeInput = (SyncFlowStatus.SyncNodeInput) ctx.getFlowRes().getInputs().get(id);
            }
            int status = "manualConfirm".equals(getFlowName()) ? 5 : 1;
            syncFlowStatusServices.addSyncFlowStatusMap(flowRecordId, syncNodeInput,
                    SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(req.getM78RpcAddr()).nodeId(id).status(status).nodeName(name).build(), false);
        }
        return FlowRes.success("");
    }

    public void exit(FlowContext ctx, FlowReq req, FlowRes res) {
        long durationTime = System.currentTimeMillis() - startTime;
        setFinish(true);
        setState(FlowState.finish);
        log.info("exit flow..... flowName:{},id:{},flowRecordId:{},durationTime:{}", getFlowName(), id, flowRecordId, durationTime);
        if (req.isSyncFlowStatusToM78()) {
            if (res.getCode() != 0 && res.getCode() != FlowRes.GOTO) {
                SyncFlowStatus.SyncNodeOutput syncNodeOutput = (SyncFlowStatus.SyncNodeOutput) ctx.getFlowRes().getOutputs().get(id);
                ctx.setFinalEnd(true);
                long totalDuration = System.currentTimeMillis() - ctx.getStartTime();
                this.getSyncFlowStatusServices().syncFinalRst(this.getFlowRecordId(), 3, totalDuration, null,
                        null,
                        SyncFlowStatus.SyncNodeOutput.builder().m78RpcAddr(req.getM78RpcAddr()).nodeId(id).nodeName(name).status(3).errorInfo(res.getMessage()).durationTime(durationTime).outputDetails(null == syncNodeOutput ? null : syncNodeOutput.getOutputDetails()).build(), null, req.getMeta());
                return;
            }
            SyncFlowStatus.SyncNodeOutput syncNodeOutput = null;
            if (ctx.getFlowRes().getOutputs().containsKey(id)) {
                syncNodeOutput = (SyncFlowStatus.SyncNodeOutput) ctx.getFlowRes().getOutputs().get(id);
                syncNodeOutput.setDurationTime(durationTime);
            } else {
                syncNodeOutput = SyncFlowStatus.SyncNodeOutput.builder()
                        .durationTime(durationTime)
                        .m78RpcAddr(req.getM78RpcAddr())
                        .nodeId(id)
                        .nodeName(name)
                        .status(res.getCode() == 0 ? 2 : 3)
                        .build();
            }
            if (isSingleNodeTest()) {
                syncFlowStatusServices.syncFinalRst(this.getFlowRecordId(), res.getCode() == 0 ? 2 : 3, durationTime,
                        null, null, syncNodeOutput, req.getM78RpcAddr(), req.getMeta());
            } else {
                syncFlowStatusServices.addSyncFlowStatusMap(flowRecordId, null, syncNodeOutput, isSingleNodeTest() ? true : false);
            }
        }
    }

    public FlowRes newExecute(FlowReq req, FlowContext context) {
        if (!isBatch()) {
            //单次执行
            log.info("start [{}] 单次执行", getFlowName());
            return execute(req, context);
        }
        log.info("start [{}] 批量执行", getFlowName());
        //int batchMaxTimes = batchMap.get(CommonConstants.TY_BATCH_MAX_TIMES_MARK).getValue().getAsInt();
        Map<InputData, JsonElement> batchParamMap = new HashMap<>();
        //batch参数校验并获取关联的具体值
        int batchTimes = 0;
        final AtomicLong batchTimeInterval = new AtomicLong(0);
        for (Map.Entry<String, InputData> entry : batchMap.entrySet()) {
            if (CommonConstants.TY_BATCH_TIME_INTERVAL_MARK.equals(entry.getKey()) && entry.getValue().getValue() != null && StringUtils.isNotBlank(entry.getValue().getValue().getAsString())) {
                batchTimeInterval.set(entry.getValue().getValue().getAsInt());
                log.info("batchTimeInterval:{}", batchTimeInterval);
                continue;
            }
            if (!CommonConstants.TY_BATCH_MAX_TIMES_MARK.equals(entry.getKey()) && !CommonConstants.TY_BATCH_TIME_INTERVAL_MARK.equals(entry.getKey())) {
                InputData batchInputData = entry.getValue();
                if (batchInputData.getType().equals("reference") || batchInputData.getType().equals("imageReference")) {
                    JsonElement jsonElement = context.queryFieldValueFromReferenceData(batchInputData.getFlowId(), batchInputData.getReferenceName());
                    log.info("set batchParam, name:{},value:{}", batchInputData.getName(), jsonElement.toString());
                    if (!jsonElement.isJsonArray()) {
                        log.error("Looping is not allowed for non-arrays, name:{}", batchInputData.getName());
                        return FlowRes.failure("Looping is not allowed for non-arrays, name:" + batchInputData.getName());
                    }
                    batchTimes = batchTimes == 0 ? jsonElement.getAsJsonArray().size() : Math.min(batchTimes, jsonElement.getAsJsonArray().size());
                    batchParamMap.put(batchInputData, jsonElement);
                } else {
                    if (isSingleNodeTest() && batchInputData.getValue().isJsonPrimitive()) {
                        JsonArray originalArray = JsonParser.parseString(batchInputData.getValue().getAsString()).getAsJsonArray();
                        batchInputData.setValue(originalArray);
                    }
                    batchTimes = batchTimes == 0 ? batchInputData.getValue().getAsJsonArray().size() : Math.min(batchTimes, batchInputData.getValue().getAsJsonArray().size());
                    batchParamMap.put(batchInputData, batchInputData.getValue());
                }
            }
        }
        log.info("batchTimes:{}, {}", batchTimes, batchParamMap);
        FlowRes res = FlowRes.success(outputMap);

        if (batchTimes < 1) {
            return res;
        }

        batchTimes = Math.min(MAX_BATCH_TIMES, batchTimes);

        Map<String, List<OutputData>> outputData = new HashMap<>();
        for (int i = 0; i < batchTimes; i++) {
            if (context.getCancel().get()){
                log.warn("cancel batch...flowRecordId:{}", this.flowRecordId);
                res = FlowRes.cancel();
                break;
            }
            for (Map.Entry<InputData, JsonElement> entry : batchParamMap.entrySet()) {
                JsonElement jsonElement = entry.getValue().getAsJsonArray().get(i);
                updateBatchInputsWithOutputData(entry.getKey(), OutputData.builder().value(jsonElement).type("batch").build());
                outputData.compute(entry.getKey().getName(), (k, v) -> {
                    if (v == null) {
                        List<OutputData> list = new ArrayList<>();
                        list.add(OutputData.builder().value(jsonElement).type("batch").build());
                        return list;
                    } else {
                        v.add(OutputData.builder().value(jsonElement).type("batch").build());
                    }
                    return v;
                });
            }
            log.info("start cycle, {},{}", i);
            FlowRes currentRes = execute(req, context);
            if (currentRes.getCode() != 0) {
                res = FlowRes.failure(currentRes.getMessage());
                break;
            }

            SyncFlowStatus.SyncNodeOutput syncNodeOutput = storeResultsInReferenceData(context, (JsonObject) currentRes.getData(), true, (i < batchTimes - 1) ? 1 : 2);
            // batch单次处理完成后，推送本次的出入参
            updateSyncFlowStatusAfterEachBatch(req, syncNodeOutput, outputData);

            try {
                if (batchTimeInterval.get() > 0 && i < batchTimes - 1) {
                    log.info("batchTimeInterval sleep:{}", batchTimeInterval);
                    TimeUnit.MILLISECONDS.sleep(batchTimeInterval.get());
                }
            } catch (Exception e) {
            }
        }
        updateBatchInputsWithOutputData(req, context, outputData);

        log.info("batch output: {}", outputMap);
        return res;
    }

    protected boolean isBatch() {
        boolean batch = true;
        if (null == batchMap || batchMap.isEmpty()) {
            batch = false;
        }
        this.batch = batch;
        return batch;
    }

    public <T> T getValueFromInputMapWithDefault(String key, T defaultValue, Class<T> type) {
        if (inputMap.containsKey(key)) {
            try {
                JsonElement element = inputMap.get(key).getValue();
                if (element != null) {
                    if (type == Boolean.class) {
                        return type.cast("true".equals(element.getAsString()));
                    } else if (type == Integer.class) {
                        return type.cast(element.getAsInt());
                    } else if (type == Double.class) {
                        return type.cast(element.getAsDouble());
                    } else if (type == String.class) {
                        return type.cast(element.getAsString());
                    } else if (type == JsonObject.class) {
                        return type.cast(element.getAsJsonObject());
                    }
                }
            } catch (Exception ignore) {
                log.error("getValueFromInputMapWithDefault {}", ignore);
            }
        }
        return defaultValue;
    }

    public void cancel(FlowContext ctx, FlowReq req) {
        long durationTime = System.currentTimeMillis() - ctx.getStartTime();
        log.info("cancel flow..... id:{},flowRecordId:{},durationTime:{}", id, this.getFlowRecordId(), durationTime);
        if (req.isSyncFlowStatusToM78()) {
            this.getSyncFlowStatusServices().syncFinalRst(this.getFlowRecordId(), 4, durationTime, null,
                    null, null, req.getM78RpcAddr(), req.getMeta());
        }
    }

    public void skipAndFinishNonMatchingSourceSubNodeBotFlows(FlowReq req, String matchedSubNodeId) {
        if (req.isSingleNodeTest()){
            return;
        }
        //从req.getOutgoingEdgesMap()中获取本节点id对应的List<NodeEdge>，对这个list进行循环，将NodeEdge中sourceSubNodeId 不为intentId的对象对应的targetNodeId的botFlow都设置为skip和Finish，参考PreconditionFlow
        req.getOutgoingEdgesMap().getOrDefault(id, Collections.emptyList())
                .stream()
                .filter(edge -> !edge.getSourceSubNodeId().equals(matchedSubNodeId))
                .forEach(edge -> {
                    BotFlow targetBotFlow = this.graph.getVertexData(edge.getTargetNodeId());
                    targetBotFlow.setFinish(true);
                    targetBotFlow.setSkip(true);
                    log.info("skipAndFinishNonMatchingSourceSubNodeBotFlows :{}", edge.getTargetNodeId());
                });
    }

    public abstract String getFlowName();

}
