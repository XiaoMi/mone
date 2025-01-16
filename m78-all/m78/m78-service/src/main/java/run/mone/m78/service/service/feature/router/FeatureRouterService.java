package run.mone.m78.service.service.feature.router;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.api.bo.flow.*;
import run.mone.m78.api.bo.invokeHistory.InvokeWayEnum;
import run.mone.m78.api.constant.FeatureRouterConstant;
import run.mone.m78.api.enums.FlowRunStatusEnum;
import run.mone.m78.api.enums.FlowNodeTypeEnum;
import run.mone.m78.api.enums.InputValueTypeEnum;
import run.mone.m78.common.Constant;
import run.mone.m78.service.common.*;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dao.entity.table.ChatInfoPoTableDef;
import run.mone.m78.service.dao.entity.table.ConnectionInfoTableDef;
import run.mone.m78.service.dao.mapper.ChatInfoMapper;
import run.mone.m78.service.dao.mapper.ConnectionInfoMapper;
import run.mone.m78.service.dao.mapper.FeatureRouterMapper;
import run.mone.m78.service.database.DataSourceConfig;
import run.mone.m78.service.database.SqlExecutor;
import run.mone.m78.service.database.SqlParseUtil;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.exceptions.InternalException;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.api.GroovyService;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.flow.FlowRecordService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.version.VersionService;
import run.mone.m78.service.utils.ValueTypeUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.FeatureRouterConstant.*;
import static run.mone.m78.service.dao.entity.ChatInfoTypeEnum.SCRIPT;
import static run.mone.m78.service.dao.entity.ChatInfoTypeEnum.SQL;
import static run.mone.m78.service.dao.entity.FeatureRouterTypeEnum.*;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INNER_QUERY_ERROR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 15:00
 */
@Service
@Slf4j
public class FeatureRouterService extends ServiceImpl<FeatureRouterMapper, FeatureRouter> {

    private static final Gson gson = GsonUtils.gson;

    @Resource
    private ChatInfoMapper chatInfoMapper;


    @Resource
    private FlowService flowService;

    @Resource
    private FlowRecordService flowRecordService;

    @Resource
    private SqlExecutor sqlExecutor;

    @Resource
    private ConnectionInfoMapper connectionInfoMapper;

    @Resource
    private DataSourceConfig dataSourceConfig;

    @Value("${m78_host:mone.test.mi.com}")
    private String m78Host;

    @Value("${m78_port:}")
    private String m78Port;

    @Resource
    private GroovyService groovyService;

    @Resource
    private VersionService versionService;

    @Resource
    private FlowTestResMapHolder flowTestResMapHolder;


    /**
     * 根据FeatureRouterReq请求获取特定的FeatureRouter实体，通过该实体的labelId查询ChatInfoPo，执行与ChatInfoPo关联的SQL查询，并返回查询结果。
     */
    @SneakyThrows
    public Result<List<Map<String, Object>>> query(FeatureRouterReq req) {
        FeatureRouter fr = this.getById(req.getId());
        Long labelId = fr.getLabelId();
        ChatInfoPo chatInfo = chatInfoMapper.selectOneByCondition(ChatInfoPoTableDef.CHAT_INFO_PO.ID.eq(labelId).and(ChatInfoPoTableDef.CHAT_INFO_PO.USER.eq(req.getUserName())));
        if (chatInfo == null) {
            throw new InvalidArgumentException("请求错误，用户:" + req.getUserName() + ", 尝试查询的导出接口没有对应的chat记录!");
        }
        ChatInfoTypeEnum type = ChatInfoTypeEnum.getTypeEnumByCode(chatInfo.getType());
        if (type == SCRIPT) {
            return execScript(fr, req, chatInfo);
        } else {
            //sql
            return execSQL(fr, req, chatInfo);
        }
    }

    /**
     * 执行Probot操作的方法。
     *
     * @param req 包含userName, botId, input的JSON对象。
     * @return Result<JsonObject> 包含执行结果的响应对象。
     * @throws UserAuthException        如果请求中缺少userName。
     * @throws InvalidArgumentException 如果请求中缺少botId或input，或者无法找到对应的probot记录或featureRouter记录。
     * @throws Exception                如果解析结果数据为JSON时出错。
     */
    @SneakyThrows
    public Result<JsonObject> executeProbot(JsonObject req) {
        /**
         * req: {userName,botId,input}
         */
        log.info("query Probot with req:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(UserAuthException::new).getAsString();
        String botId = Optional.ofNullable(req.get("botId")).orElseThrow(InvalidArgumentException::new).getAsString();
        String input = Optional.ofNullable(req.get("input")).orElseThrow(InvalidArgumentException::new).getAsString();

        BotService botService = ApplicationContextProvider.getBean(BotService.class);

        M78Bot m78Bot = botService.getById(botId);
        if (m78Bot == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的probot记录!");
        }

        // 校验Athena客户端版本
        if (versionService.checkVersion(req)) {
            return Result.fail(GeneralCodes.ParamError, Constant.CLIENT_VERSION_ERROR_MSG);
        }

        Result<String> res = botService.executeBot(null, Long.parseLong(botId), input, userName, UUIDUtil.generateType1UUID().toString(), req);
        if (res.getData() == null) {
            return Result.fail(STATUS_INNER_QUERY_ERROR, "没有有效的返回结果，请尝试调试bot后重新导出!");
        }
        JsonObject resp = new JsonObject();
        try {
            resp.add("result", GsonUtils.gson.fromJson(res.getData(), JsonObject.class));
        } catch (Exception e) {
            log.warn("failed to parse res data as json, will use primitive form!");
            resp.add("result", new JsonPrimitive(res.getData()));
        }

        //异步调用记录
        botService.recordInvokeHistory(Long.valueOf(botId), userName, input, (resp == null) ? "" : gson.toJson(resp), InvokeWayEnum.INTERFACE.getCode());

        return Result.success(resp);
    }

    /**
     * 查询流程信息
     *
     * @param req      包含userName, botId和input的JsonObject请求参数
     * @param testOnly 是否仅进行测试
     * @return 包含流程记录ID和结果的JsonObject
     * @throws UserAuthException        如果userName为空
     * @throws InvalidArgumentException 如果flowId或input为空，或找不到对应的flow记录或featureRouter记录
     * @throws InternalException        如果flowRecordId无效
     */
    @SneakyThrows
    public Result<JsonObject> queryFlow(JsonObject req, boolean testOnly) {
        /**
         * req: {userName,botId,input}
         */
        log.info("query flow with req:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(UserAuthException::new).getAsString();
        String flowId = Optional.ofNullable(req.get("flowId")).orElseThrow(InvalidArgumentException::new).getAsString();
        String flowRecordId = Optional.ofNullable(req.get("flowRecordId")).orElse(new JsonPrimitive("")).getAsString();
        JsonObject input = Optional.ofNullable(req.get("input")).orElseThrow(InvalidArgumentException::new).getAsJsonObject();

        FlowBasePo flow = flowService.getById(flowId);
        if (flow == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的flow记录!");
        }
        FeatureRouter fr = this.getOne(new QueryWrapper().eq("label_id", Long.parseLong(flowId)));
        if (fr == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的导出featureRouter记录!");
        }

        Set<String> keys = input.keySet();
        Map<String, JsonElement> param = new HashMap<>();
        keys.stream()
                .forEach(it -> {
                    if (input.has(it)) {
                        param.put(it, input.get(it));
                    }
                });
        if (StringUtils.isBlank(flowRecordId)) {
            Result<FlowTestRes> res = flowService.testFlow(FlowTestParam.builder()
                    .flowId(Integer.parseInt(flowId))
                    .executeType(FlowExecuteTypeEnum.OPEN_API_FLOW.getCode())
                    .inputs(param)
                    .userName(userName)
                    .build());
            if (res.getData() == null) {
                return Result.fail(STATUS_INNER_QUERY_ERROR, "没有有效的返回结果，请尝试调试bot后重新导出!");
            }
            if (testOnly) {
                JsonObject flowRecordIdResp = new JsonObject();
                flowRecordIdResp.addProperty("flowRecordId", res.getData().getFlowRecordId());
                return Result.success(flowRecordIdResp);
            }
            flowRecordId = res.getData().getFlowRecordId();
        }
        FlowTestResMapHolder.FLOW_TEST_RES_MAP.putIfAbsent(flowRecordId, false);
        Boolean finished = FlowTestResMapHolder.FLOW_TEST_RES_MAP.get(flowRecordId);
        if (finished == null) {
            throw new InternalException("Invalid flow recordId, should not happen!");
        }
        int loopCnt = 0;
        while (!finished && loopCnt < 360) {//临时 待改造
            // keep wait until finished
            log.info("waiting flow test {} for the flow to be finished, loopCnt:{}", flowRecordId, loopCnt);
            TimeUnit.MILLISECONDS.sleep(500);
            finished = FlowTestResMapHolder.FLOW_TEST_RES_MAP.get(flowRecordId);
            loopCnt++;
        }
        FlowTestResMapHolder.FLOW_TEST_RES_MAP.remove(flowRecordId);
        SyncFlowStatus.EndFlowOutput flowRes = flowRecordService.getEndFlowOutputByRecordId(flowRecordId);
        JsonObject resp = new JsonObject();
        try {
            resp.addProperty("flowRecordId", flowRecordId);
            resp.add("result", GsonUtils.gson.toJsonTree(flowRes));
        } catch (Exception e) {
            log.warn("failed to parse res data as json, will use primitive form!");
            resp.add("result", new JsonPrimitive(flowRes.getAnswerContent()));
        }
        return Result.success(resp);
    }

    /**
     * 同步查询流程状态
     *
     * @param req      请求的Json对象
     * @param testOnly 是否仅用于测试
     * @return 包含查询结果的Result对象
     */
    @SneakyThrows
    public Result<? extends Object> querySyncFlow(JsonObject req, boolean testOnly) {
        Result<JsonObject> exeFlowResult = this.exeFlow(req);
        if (exeFlowResult.getCode() != 0) {
            return exeFlowResult;
        }
        String flowRecordId = exeFlowResult.getData().get("flowRecordId").getAsString();
        Boolean finished = FlowTestResMapHolder.FLOW_TEST_RES_MAP.get(flowRecordId);
        int loopCnt = 0;
        while (!finished && loopCnt < 3600) {//临时 待改造
            // keep wait until finished
            log.info("waiting flow test {} for the flow to be finished, loopCnt:{}", flowRecordId, loopCnt);
            TimeUnit.MILLISECONDS.sleep(500);
            finished = FlowTestResMapHolder.FLOW_TEST_RES_MAP.get(flowRecordId);
            loopCnt++;
        }
        return queryResult(exeFlowResult.getData());
    }

    /**
     * 异步查询流程状态
     *
     * @param req      包含查询参数的JsonObject，必须包含userName, flowId和flowRecordId
     * @param testOnly 是否仅用于测试
     * @return 包含查询结果的JsonObject
     * @throws UserAuthException        如果userName为空
     * @throws InvalidArgumentException 如果flowId或flowRecordId为空，或未找到对应的flow记录或featureRouter记录
     */
    @SneakyThrows
    public Result<JsonObject> queryFlowAsync(JsonObject req, boolean testOnly) {
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(UserAuthException::new).getAsString();
        String flowId = Optional.ofNullable(req.get("flowId")).orElseThrow(InvalidArgumentException::new).getAsString();
        String flowRecordId = Optional.ofNullable(req.get("flowRecordId")).orElseThrow(InvalidArgumentException::new).getAsString();
        FlowBasePo flow = flowService.getById(flowId);
        if (flow == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的flow记录!");
        }
        FeatureRouter fr = this.getOne(new QueryWrapper().eq("label_id", Long.parseLong(flowId)));
        if (fr == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的导出featureRouter记录!");
        }

        boolean finished = flowTestResMapHolder.getStatus(flowRecordId, false);
        int loopCnt = 0;
        while (!finished && loopCnt < 3600) {//临时 待改造
            // keep wait until finished
            log.info("waiting flow test {} for the flow to be finished, loopCnt:{}", flowRecordId, loopCnt);
            TimeUnit.MILLISECONDS.sleep(500);
            finished = flowTestResMapHolder.getStatus(flowRecordId, false);
            loopCnt++;
        }
        FlowTestRecordPo flowTestRecordByRecordId = flowRecordService.getFlowTestRecordByRecordId(flowRecordId);
        if (FlowRunStatusEnum.RUN_SUCCEED.getCode() == flowTestRecordByRecordId.getStatus() && flowTestRecordByRecordId.getEndFlowOutput() != null) {
            flowTestResMapHolder.updateStatus(flowRecordId, true);
        }
        JsonObject resp = new JsonObject();
        try {
            resp.addProperty("flowRecordId", flowRecordId);
            resp.add("result", GsonUtils.gson.toJsonTree(flowTestRecordByRecordId.getEndFlowOutput()));
        } catch (Exception e) {
            log.warn("failed to parse res data as json, will use primitive form!");
            resp.add("result", new JsonPrimitive(flowTestRecordByRecordId.getEndFlowOutput().getAnswerContent()));
        }
        return Result.success(resp);
    }

    /**
     * 执行流程并返回结果
     *
     * @param req 包含userName, flowId和input的JsonObject
     * @return 包含flowRecordId的JsonObject结果
     * @throws UserAuthException        如果userName为空
     * @throws InvalidArgumentException 如果flowId或input为空，或未找到对应的flow或featureRouter记录
     */
    @SneakyThrows
    public Result<JsonObject> exeFlow(JsonObject req) {
        /**
         * req: {userName,botId,input}
         */
        log.info("query flow with req:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(UserAuthException::new).getAsString();
        String flowId = Optional.ofNullable(req.get("flowId")).orElseThrow(InvalidArgumentException::new).getAsString();
        JsonObject input = Optional.ofNullable(req.get("input")).orElseThrow(InvalidArgumentException::new).getAsJsonObject();

        FlowBasePo flow = flowService.getById(flowId);
        if (flow == null) {
            throw new InvalidArgumentException("请求错误，用户:" + userName + ", 尝试查询的导出接口没有找到对应的flow记录!");
        }
        Set<String> keys = input.keySet();
        Map<String, JsonElement> param = new HashMap<>();
        keys.stream()
                .forEach(it -> {
                    if (input.has(it)) {
                        param.put(it, input.get(it));
                    }
                });
        Result<FlowTestRes> res = flowService.testFlow(FlowTestParam.builder()
                .flowId(Integer.parseInt(flowId))
                .executeType(FlowExecuteTypeEnum.OPEN_API_FLOW.getCode())
                .inputs(param)
                .userName(userName)
                .build());
        if (res.getData() == null) {
            return Result.fail(STATUS_INNER_QUERY_ERROR, "没有有效的返回结果，请尝试调试bot后重新导出!");
        }
        String flowRecordId = res.getData().getFlowRecordId();
        FlowTestResMapHolder.FLOW_TEST_RES_MAP.putIfAbsent(flowRecordId, false);
        JsonObject resp = new JsonObject();
        try {
            resp.addProperty("flowRecordId", flowRecordId);
        } catch (Exception e) {
            log.warn("failed to parse res data as json, will use primitive form!");
            resp.add("result", new JsonPrimitive(res.getData().toString()));
        }
        return Result.success(resp);
    }

    /**
     * 查询流程结果
     *
     * @param req 包含请求参数的JsonObject对象，必须包含flowRecordId
     * @return 包含流程状态和结果的Result对象
     * @throws UserAuthException 如果请求中缺少flowRecordId
     */
    @SneakyThrows
    public Result<Object> queryResult(JsonObject req) {
        log.info("queryResult flow with req:{}", req);
        String flowRecordId = Optional.ofNullable(req.get("flowRecordId")).orElseThrow(UserAuthException::new).getAsString();
        FlowTestRecordPo flowTestRecordPo = flowRecordService.getFlowTestRecordByRecordId(flowRecordId);
        HashMap resp = new HashMap<>();
        try {
            resp.put("flowStatus", flowTestRecordPo.getStatus());
            resp.put("flowRecordId", flowRecordId);
            if (flowTestRecordPo.getStatus() == FlowRunStatusEnum.RUN_SUCCEED.getCode()) {
                resp.put("result", convertFlowOutputDetailsToMap(flowTestRecordPo));
            } else if (flowTestRecordPo.getStatus() == FlowRunStatusEnum.RUN_FAILED.getCode()) {
                resp.put("fail", convertErrorInfoToMap(flowTestRecordPo));
            }
            return Result.success(resp);
        } catch (Exception e) {
            log.error("failed to parse res data as json, will use primitive form!", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }


    /**
     * 将FlowTestRecordPo对象中的错误信息转换为Map
     *
     * @param flowTestRecordPo 包含节点输出信息的FlowTestRecordPo对象
     * @return 包含节点错误信息的Map，键为节点名称，值为错误信息
     */
    public Map<String, String> convertErrorInfoToMap(FlowTestRecordPo flowTestRecordPo) {
        Map<String, String> errorInfoMap = new HashMap<>();
        String json = GsonUtils.gson.toJson(flowTestRecordPo.getNodeOutputsMap());
        if (flowTestRecordPo != null && flowTestRecordPo.getNodeOutputsMap() != null) {
            Map<String, SyncFlowStatus.SyncNodeOutput> nodeOutputsMap = GsonUtils.gson.fromJson(json, new TypeToken<Map<String, SyncFlowStatus.SyncNodeOutput>>() {
            }.getType());
            nodeOutputsMap.forEach((key, value) -> {
                if (StringUtils.isNotBlank(value.getErrorInfo())) {
                    errorInfoMap.put(key, value.getErrorInfo());
                }
            });
        }
        return errorInfoMap;
    }

    /**
     * 将FlowTestRecordPo对象中的结束节点输出详情转换为Map
     *
     * @param flowTestRecordPo 流程测试记录对象
     * @return 包含结束节点输出详情的Map
     */
    public Map<String, Object> convertFlowOutputDetailsToMap(FlowTestRecordPo flowTestRecordPo) {
        Result<Pair<FlowBasePo, FlowSettingPo>> flowPoByBase = flowService.queryFlowPoByBaseId(flowTestRecordPo.getFlowBaseId());
        FlowSettingPo flowSettingPo = flowPoByBase.getData().getValue();
        
        if (flowSettingPo == null || CollectionUtils.isEmpty(flowSettingPo.getNodes())) {
            log.warn("Flow setting or nodes is empty for flow: {}", flowTestRecordPo.getFlowBaseId());
            return new HashMap<>();
        }

        NodeInfo endNodeInfo = flowSettingPo.getNodes().stream()
            .filter(node -> FlowNodeTypeEnum.END.getDesc().equals(node.getNodeType()))
            .findFirst()
            .orElse(null);
        
        if (endNodeInfo == null || CollectionUtils.isEmpty(endNodeInfo.getOutputs())) {
            log.warn("End node or outputs not found for flow: {}", flowTestRecordPo.getFlowBaseId());
            return new HashMap<>();
        }

        Map<Integer, NodeInfo> nodeInfoMap = flowSettingPo
            .getNodes()
            .stream()
            .collect(Collectors.toMap(NodeInfo::getId, Function.identity()));

        HashMap<String, InputValueTypeEnum> outPutInfoTypes = new HashMap<>();
        endNodeInfo.getOutputs().forEach(nodeOutputInfo -> {
            if (StringUtils.isNotBlank(nodeOutputInfo.getReferenceName())) {
                InputValueTypeEnum valueType = findValueType(nodeOutputInfo, nodeInfoMap);
                if (valueType == null) {
                    valueType = InputValueTypeEnum.getEnumByName(nodeOutputInfo.getValueType());
                }
                outPutInfoTypes.put(nodeOutputInfo.getName(), valueType);
            }
        });

        Map<String, Object> resultMap = new HashMap<>();
        if (flowTestRecordPo != null && 
            flowTestRecordPo.getEndFlowOutput() != null && 
            flowTestRecordPo.getEndFlowOutput().getEndFlowOutputDetails() != null) {
            
            for (SyncFlowStatus.EndFlowOutputDetail detail : flowTestRecordPo.getEndFlowOutput().getEndFlowOutputDetails()) {
                if (detail != null && StringUtils.isNotBlank(detail.getName())) {
                    resultMap.put(detail.getName(), 
                        ValueTypeUtils.convertValueByTypeToObject(
                            removeQuotes(detail.getValue()), 
                            outPutInfoTypes.getOrDefault(detail.getName(), InputValueTypeEnum.STRING)
                        )
                    );
                }
            }
        }
        return resultMap;
    }

    /**
     * 移除字符串开头和结尾的引号
     *
     * @param value 需要处理的字符串
     * @return 处理后的字符串，如果字符串不以引号开头和结尾，则返回原字符串
     */
    //如果value以"开头并同时结尾，将开头和结尾的"替换成空
    public String removeQuotes(String value) {
        if (value != null && value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private InputValueTypeEnum findValueType(NodeOutputInfo nodeOutputInfo, Map<Integer, NodeInfo> nodeInfoMap) {
        if (nodeOutputInfo == null) {
            return null;
        }

        int nodeId = nodeOutputInfo.getReferenceNodeId();
        String fieldName = nodeOutputInfo.getReferenceName();
        
        NodeInfo nodeInfo = nodeInfoMap.get(nodeId);
        if (nodeInfo == null) {
            log.warn("Node not found for id: {}", nodeId);
            return null;
        }

        try {
            String fieldType = JsonElementUtils.findValueTypeFromNodeInfo(nodeInfo, fieldName);
            return InputValueTypeEnum.getEnumByName(fieldType);
        } catch (Throwable e) {
            log.warn("Failed to find value type for field: {} in node: {}", fieldName, nodeId);
            return null;
        }
    }


    private Result<List<Map<String, Object>>> execScript(FeatureRouter fr, FeatureRouterReq req, ChatInfoPo chatInfo) {
        JsonObject params = new JsonObject();
        Map<String, Object> incomingReqData = req.getReqData();
        if (MapUtils.isNotEmpty(incomingReqData)) {
            incomingReqData.forEach((key, value) -> {
                JsonElement obj = GsonUtils.gson.toJsonTree(value);
                params.add(key, obj);
            });
        }
        Object res = groovyService.invoke(chatInfo.getMappingContent(), "execute", ImmutableMap.of(), params, null);
        log.info("invoke script res:{}", res);
        if (res instanceof JsonObject) {
            JsonObject apiResult = (JsonObject) res;
            return Result.success(ResultUtils.convertApiResultToJsonMaps(apiResult));
        }
        return Result.success(Lists.newArrayList());
    }

    private Result<List<Map<String, Object>>> execSQL(FeatureRouter fr, FeatureRouterReq req, ChatInfoPo chatInfo) throws JSQLParserException {
        ConnectionInfo connectionInfo = dataSourceConfig.DEFAULT;
        String connectionId = "";
        Map<String, String> chatInfoMeta = chatInfo.getChatInfoMeta();
        if (MapUtils.isNotEmpty(chatInfoMeta)) {
            connectionId = chatInfoMeta.get(DATASOURCE_ID);
        }
        if (StringUtils.isNotBlank(connectionId)) {
            connectionInfo = connectionInfoMapper.selectOneByCondition(ConnectionInfoTableDef.CONNECTION_INFO.ID.eq(connectionId).and(ConnectionInfoTableDef.CONNECTION_INFO.USER_NAME.eq(req.getUserName())));
        }

        //TODO$ 这个sql得修改成,导出的那个sql(有些是问号,有些不是)
        // 目前可直接将传入的reqData更新到whereParts中
        String sql = chatInfo.getMappingContent();
        Pair<String, List<String>> tableNameAndColumns = SqlParseUtil.parseTableNameAndColumns(sql);
        String tableName = tableNameAndColumns.getLeft();
        QueryContext queryContext = new QueryContext(connectionInfo);

        // add count
        Map<String, Object> reqData = req.getReqData();
        if (reqData == null) {
            reqData = new HashMap<>();
        }

        String routerMetaType = (String) fr.getRouterMeta().getOrDefault(ROUTER_META_TYPE, ROUTER_TYPE_R);
        String tablePkName = sqlExecutor.getTablePkName(connectionInfo, tableName);
        Row r = new Row();
        reqData.forEach(r::set);
        List<Map<String, Object>> res = Lists.newArrayList();
        switch (routerMetaType) {
            case ROUTER_TYPE_C:
                // 将reqData中内容作为插入参数，生成Row 插入tableAndColumns中的表中
                int[] inserted = sqlExecutor.execBatchInsert(queryContext, tableName, ImmutableList.of(r));
                res.add(ImmutableMap.of("affected", Arrays.stream(inserted).sum()));
                break;
            case ROUTER_TYPE_U:
                // 将reqData中内容作为更新参数，生成Row 更新到tableAndColumns中的表中
                int updated = sqlExecutor.execBatchUpdate(queryContext, tableName, ImmutableList.of(r));
                res.add(ImmutableMap.of("affected", updated));
                break;
            case ROUTER_TYPE_D:
                // 根据reqData中的主键id，删除tableAndColumns中的行
                int deleted = sqlExecutor.execBatchDelete(queryContext, tableName, tablePkName, ImmutableList.of(r.getLong(tablePkName)));
                res.add(ImmutableMap.of("affected", deleted));
                break;
            case ROUTER_TYPE_R:
            default:
                reqData.putIfAbsent("lowerBound", "0");
                reqData.putIfAbsent("upperBound", "5000");
                reqData.putAll(fr.getRouterMeta());
                Pair<Integer, String> totalAndSql = constructQuerySql(sql, connectionInfo, reqData);
                List<Map<String, Object>> data = sqlExecutor.exec(totalAndSql.getRight(), new QueryContext(connectionInfo));
                res.add(ImmutableMap.of("data", data, "total", totalAndSql.getLeft()));
        }
        return Result.success(res);
    }


    /**
     * 根据传入的FeatureRouterReq对象和账户信息保存特性路由。
     * 根据不同的路由类型调用不同的处理方法。
     *
     * @param featureRouterReq 特性路由请求对象
     * @param account          账户信息
     * @return 如果处理成功返回true，否则返回false
     */
    public boolean save(FeatureRouterReq featureRouterReq, String account) {
        // setup po
        Map<String, Object> routerMeta = new HashMap<>();

        FeatureRouterTypeEnum routerType = FeatureRouterTypeEnum.getTypeEnumByCode(featureRouterReq.getRouterType());

        if (PROBOT == routerType || FLOW == routerType) {
            return handleProbotCreate(routerType, featureRouterReq, account, routerMeta);
        }

        if (CHAT_BASED == routerType) {
            return handleChatBasedCreate(featureRouterReq, account, routerMeta);
        }

        return false;
    }

    private boolean handleProbotCreate(FeatureRouterTypeEnum routerType, FeatureRouterReq featureRouterReq, String account, Map<String, Object> routerMeta) {
        if (MapUtils.isNotEmpty(featureRouterReq.getReqData())) {
            routerMeta.putAll(featureRouterReq.getReqData());
        }
        // HINT: for open api
        String userName = "system";
        if (StringUtils.isNotBlank(account)) {
            userName = account;
        }

        FeatureRouter featureRouter = FeatureRouter.builder()
                .labelId(featureRouterReq.getLabelId()) // HINT: botId or flowId
                .name(featureRouterReq.getName())
                .userName(userName)
                .routerMeta(routerMeta)
                .type(routerType.getCode()) // HINT: type must be set
                .build();
        return super.save(featureRouter);
    }

    private boolean handleChatBasedCreate(FeatureRouterReq featureRouterReq, String account, Map<String, Object> routerMeta) {
        ChatInfoPo chatInfo = chatInfoMapper.selectOneById(featureRouterReq.getLabelId());
        if (!account.equals(chatInfo.getUser())) {
            throw new InvalidArgumentException("传入的label id所属用户与当前用户不符!");
        }
        ChatInfoTypeEnum type = ChatInfoTypeEnum.getTypeEnumByCode(chatInfo.getType());
        //sql
        if (SQL == type) {
            try {
                List<Map<String, Object>> columnNames = SqlParseUtil.getColumnNames(chatInfo.getMappingContent());
                if (CollectionUtils.isNotEmpty(columnNames)) {
                    routerMeta = columnNames.stream()
                            .flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue(), (o1, o2) -> o2));
                }
                if (MapUtils.isNotEmpty(featureRouterReq.getReqData())) {
                    routerMeta.putAll(featureRouterReq.getReqData());
                }
            } catch (JSQLParserException e) {
                throw new InternalException(e);
            }
        }

        if (SCRIPT == type) {
            try {
                String params = chatInfo.getChatInfoMeta().get("params");
                routerMeta.putAll(Splitter.on(",").splitToList(params).stream().collect(Collectors.toMap(it -> it, it -> "")));
                log.info("routerMeta:{} params:{}", routerMeta, params);
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        // HINT: setup CRUD featureRouter
        List<FeatureRouter> featureRouters = new ArrayList<>();
        FeatureRouter featureRouterR = FeatureRouter.builder()
                .labelId(featureRouterReq.getLabelId())
                .name(featureRouterReq.getName())
                .userName(account)
                .routerMeta(routerMeta)
                .build();
        featureRouters.add(featureRouterR);
        if (SQL == type) {
            FeatureRouter featureRouterC = FeatureRouter.builder()
                    .labelId(featureRouterReq.getLabelId())
                    .name(FeatureRouterConstant.ROUTER_NAME_PREFIX_FOR_C + featureRouterReq.getName())
                    .userName(account)
                    .routerMeta(setupRouterMetaForFeatureRouterC(chatInfo))
                    .build();
            FeatureRouter featureRouterU = FeatureRouter.builder()
                    .labelId(featureRouterReq.getLabelId())
                    .name(FeatureRouterConstant.ROUTER_NAME_PREFIX_FOR_U + featureRouterReq.getName())
                    .userName(account)
                    .routerMeta(setupRouterMetaForFeatureRouterU(chatInfo))
                    .build();
            FeatureRouter featureRouterD = FeatureRouter.builder()
                    .labelId(featureRouterReq.getLabelId())
                    .name(FeatureRouterConstant.ROUTER_NAME_PREFIX_FOR_D + featureRouterReq.getName())
                    .userName(account)
                    .routerMeta(setupRouterMetaForFeatureRouterD(chatInfo))
                    .build();
            featureRouters.add(featureRouterC);
            featureRouters.add(featureRouterU);
            featureRouters.add(featureRouterD);
        }
        // setup meta for chatInfo
        Map<String, String> chatInfoMeta = chatInfo.getChatInfoMeta();
        if (chatInfoMeta == null) {
            chatInfoMeta = new HashMap<>();
            chatInfo.setChatInfoMeta(chatInfoMeta);
        } else {
            chatInfoMeta = new HashMap<>(chatInfoMeta);
        }
        chatInfoMeta.put(DATASOURCE_ID, Optional.ofNullable(String.valueOf(featureRouterReq.getDatasourceId())).orElse(""));
        return super.saveBatch(featureRouters);
    }

    /**
     * 根据给定的FeatureRouter对象、用户名和probot标志生成cURL命令的URL
     *
     * @param featureRouter FeatureRouter对象，用于生成请求数据
     * @param userName      用户名，如果不为空则设置到FeatureRouter对象中
     * @param probot        标志是否为probot，如果为true则生成probot相关的cURL命令
     * @return 生成的cURL命令的URL字符串，如果featureRouter为空则返回空字符串
     */
    public String getCurlUrl(FeatureRouter featureRouter, String userName, boolean probot) {
        if (featureRouter == null) {
            return "";
        }
        if (StringUtils.isNotBlank(userName)) {
            featureRouter.setUserName(userName);
        }
        FeatureRouterReq req = MappingUtils.map(featureRouter, FeatureRouterReq.class);
        req.setReqData(featureRouter.getRouterMeta());
        StringBuilder sb = new StringBuilder();
        sb.append(CURL_PREFIX);
        sb.append(" -d ");
        sb.append(" '");
        if (probot) {
            String body = GsonUtils.gson.toJson(featureRouter.getRouterMeta());
            sb.append(StringEscapeUtils.unescapeHtml4(body));
        } else {
            sb.append(GsonUtils.gson.toJson(req));
        }
        sb.append("' ");
        sb.append("https://");
        sb.append(m78Host);
        if (StringUtils.isNotBlank(m78Port)) {
            sb.append(":");
            sb.append(m78Port);
        }
        if (probot) {
            sb.append(CURL_SUFFIX_BOT);
        } else {
            sb.append(CURL_SUFFIX);
        }
        return sb.toString();
    }

    private Map<String, Object> setupRouterMetaForFeatureRouterD(ChatInfoPo chatInfo) {
        // HINT: 运行态时判断chatInfo所关联的表是否有auto increment的主键, 并依此做删除, 记录该主键名
        String originalSql = chatInfo.getMappingContent();
        try {
            Pair<String, List<String>> tableNameAndColumns = SqlParseUtil.parseTableNameAndColumns(originalSql);
            Map<String, Object> res = new HashMap<>();
            res.put(ROUTER_META_TYPE, "D");
            // TODO: nasty for now
            res.put("id", "");
            return res;
        } catch (JSQLParserException e) {
            log.error("Error while try to setup featureRouterD for chatInfo:{}", chatInfo.getId(), e);
            // HINT: do not block others
            return null;
        }

    }

    private Map<String, Object> setupRouterMetaForFeatureRouterU(ChatInfoPo chatInfo) {
        String originalSql = chatInfo.getMappingContent();
        try {
            Pair<String, List<String>> tableNameAndColumns = SqlParseUtil.parseTableNameAndColumns(originalSql);
            Map<String, Object> res = new HashMap<>();
            res.put(ROUTER_META_TYPE, "U");
            List<String> cols = tableNameAndColumns.getRight();
            if (CollectionUtils.isNotEmpty(cols)) {
                Map<String, String> params = cols.stream().collect((Collectors.toMap(it -> it, it -> "")));
                res.putAll(params);
            }
            return res;
        } catch (JSQLParserException e) {
            log.error("Error while try to setup featureRouterU for chatInfo:{}", chatInfo.getId(), e);
            // HINT: do not block others
            return null;
        }
    }

    private Map<String, Object> setupRouterMetaForFeatureRouterC(ChatInfoPo chatInfo) {
        // 此处记候选参数信息，在调用时根据reqData中的参数定义构建Rows用于插入
        String originalSql = chatInfo.getMappingContent();
        try {
            Pair<String, List<String>> tableNameAndColumns = SqlParseUtil.parseTableNameAndColumns(originalSql);
            Map<String, Object> res = new HashMap<>();
            res.put(ROUTER_META_TYPE, "C");
            res.put(ROUTER_META_STORE, GsonUtils.gson.toJson(tableNameAndColumns));
            List<String> cols = tableNameAndColumns.getRight();
            if (CollectionUtils.isNotEmpty(cols)) {
                Map<String, String> params = cols.stream().collect((Collectors.toMap(it -> it, it -> "")));
                res.putAll(params);
            }
            return res;
        } catch (JSQLParserException e) {
            log.error("Error while try to setup featureRouterC for chatInfo:{}", chatInfo.getId(), e);
            // HINT: do not block others
            return null;
        }
    }

    private Pair<Integer, String> constructQuerySql(String originalSql, ConnectionInfo connectionInfo, Map<String, Object> reqData) throws JSQLParserException {
        String countSql = SqlParseUtil.transformSelectToCount(originalSql);
        log.info("count sql:{}", countSql);
        List<Map<String, Object>> countResult = sqlExecutor.exec(countSql, new QueryContext(connectionInfo));
        Integer total = Integer.valueOf(countResult.get(0).get("COUNT(*)").toString());
        // add limit
        originalSql = SqlParseUtil.addLimitToSelectSql(originalSql, Integer.parseInt((String) reqData.get("lowerBound")), Integer.parseInt((String) reqData.get("upperBound")));
        log.info("query sql:{}", originalSql);

        Map<String, Object> newConditions = new HashMap<>();
        if (MapUtils.isNotEmpty(reqData)) {
            Set<Map.Entry<String, Object>> entries = reqData.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                newConditions.put(entry.getKey(), entry.getValue());
            }
        }

        String modifiedSql = originalSql;
        if (MapUtils.isNotEmpty(newConditions)) {
            modifiedSql = SqlParseUtil.updateSqlWhereParts(originalSql, newConditions);
        }
        return Pair.of(total, modifiedSql);
    }


}
