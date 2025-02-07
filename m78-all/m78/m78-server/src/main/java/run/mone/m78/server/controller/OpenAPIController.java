package run.mone.m78.server.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.paginate.Page;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.mone.ai.minimax.bo.T2AProResponse;
import run.mone.ai.z.dto.ZKnowledgeBaseFileBlockDTO;
import run.mone.ai.z.dto.ZKnowledgeRes;
import run.mone.m78.api.FeatureRouterProvider;
import run.mone.m78.api.bo.bot.ReqBotListDto;
import run.mone.m78.api.bo.chat.ChatTopicSearchReq;
import run.mone.m78.api.bo.feature.router.FeatureRouterDTO;
import run.mone.m78.api.bo.feature.router.FeatureRouterReq;
import run.mone.m78.api.bo.feature.router.asyncCallTask.GetAsyncTaskRes;
import run.mone.m78.api.bo.flow.FlowOperateParam;
import run.mone.m78.api.bo.knowledge.KnowledgeBaseBlockDTO;
import run.mone.m78.api.bo.multiModal.audio.textVoice.TextToVoiceParam;
import run.mone.m78.api.bo.multiModal.audio.textVoice.VoiceToTextParam;
import run.mone.m78.api.enums.FlowOperateCmdEnum;
import run.mone.m78.api.enums.ImageTypeEnum;
import run.mone.m78.common.Constant;
import run.mone.m78.server.config.auth.app.AppPermission;
import run.mone.m78.service.agent.rebot.TemplateUtils;
import run.mone.m78.service.app.SessionAccountHolder;
import run.mone.m78.service.bo.category.CategoryVo;
import run.mone.m78.service.bo.chat.ChatTopicBo;
import run.mone.m78.service.bo.feishu.EventMessage;
import run.mone.m78.service.bo.knowledge.KnowledgeCreateV2Req;
import run.mone.m78.service.bo.plugin.BotPluginBo;
import run.mone.m78.service.bo.plugin.BotPluginDetailBo;
import run.mone.m78.service.bo.task.CustomTaskBO;
import run.mone.m78.service.bo.user.*;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.*;
import run.mone.m78.service.dto.ApiKeyDTO;
import run.mone.m78.service.dto.M78CodeGenerationInfoDTO;
import run.mone.m78.service.dto.knowledge.KnowledgeBaseFileResDto;
import run.mone.m78.service.dto.presetQuestion.PresetQuestionRes;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.api.ApiKeyService;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.categoty.CategoryService;
import run.mone.m78.service.service.chat.ChatDBService;
import run.mone.m78.service.service.code.generation.info.M78CodeGenerationInfoService;
import run.mone.m78.service.service.feature.router.FeatureRouterService;
import run.mone.m78.service.service.feature.router.asyncCallTask.M78OpenapiAsyncTaskService;
import run.mone.m78.service.service.fileserver.RemoteFileService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.gray.GrayService;
import run.mone.m78.service.service.im.ImService;
import run.mone.m78.service.service.issue.IssueService;
import run.mone.m78.service.service.knowledge.KnowledgeService;
import run.mone.m78.service.service.multiModal.audio.AudioService;
import run.mone.m78.service.service.multiModal.voice.VoiceService;
import run.mone.m78.service.service.task.TaskService;
import run.mone.m78.service.service.user.UserCollectService;
import run.mone.m78.service.service.user.UserLoginService;
import run.mone.m78.service.vo.BotSimpleVo;
import run.mone.m78.service.vo.BotVo;
import run.mone.m78.service.vo.ImportChatPo;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.AgentConstant.AGENT_PORT;
import static run.mone.m78.api.constant.CommonConstant.OPEN_API_PREFIX;
import static run.mone.m78.service.common.GsonUtils.gson;
import static run.mone.m78.service.dao.entity.FeatureRouterTypeEnum.PROBOT;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/20/24 10:59
 */
@Slf4j
@RestController
@RequestMapping(value = OPEN_API_PREFIX)
@HttpApiModule(value = "OpenAPIController", apiController = OpenAPIController.class)
public class OpenAPIController {

    @Resource
    private FeatureRouterService featureRouterService;

    @Resource
    private FeatureRouterProvider featureRouterProvider;

    @Resource
    private ChatgptService chatgptService;

    @Resource
    private BotService botService;

    @Resource
    private UserCollectService userCollectService;

    @Resource
    private M78OpenapiAsyncTaskService m78OpenapiAsyncTaskService;

    @Resource
    private UserLoginService userLoginService;

    @Resource
    private ChatDBService chatDBService;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AudioService audioService;

    @Resource
    private Redis redis;

    @Resource
    private TaskService taskService;

    @Resource
    private M78CodeGenerationInfoService codeGenerationInfoService;


    @Resource
    private IssueService issueService;

    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private ImService imService;

    @Resource
    private GrayService grayService;

    @Resource
    private ApiKeyService apiKeyService;

    @Value("${export.botId:0}")
    private Long exportMaxBotId;

    @Value("${export.flowId}")
    private Long exportMaxFlowId;

    @Resource
    private FlowService flowService;

    @Autowired
    private RemoteFileService fileService;

    @Value("${mxz.intention.appid:}")
    private String mxzIntentionAppId;

    @Value("${mxz.intention.appkey:}")
    private String mxzIntentionAppKey;

    @Value("${mxz.intention.method:}")
    private String mxzIntentionMethod;

    @Value("${mxz.intention.url:}")
    private String mxzIntentionUrl;

    //创建issue(class)
    @PostMapping("/issue/create")
    @ResponseBody
    @HttpApiDoc(apiName = "创建Issue", value = "/open-api/v1/issue/create", method = MiApiRequestMethod.POST, description = "创建Issue")
    public Result<Boolean> createIssue(HttpServletRequest request, @RequestBody M78Issue issue) {
        boolean created = issueService.createIssue(issue);
        return Result.success(created);
    }


    //查询issue列表,只有状态是1的,记得生成@PostMapping(class)
    @PostMapping("/issue/list")
    @ResponseBody
    @HttpApiDoc(apiName = "查询Issue列表", value = "/open-api/v1/issue/list", method = MiApiRequestMethod.POST, description = "查询状态为1的Issue列表")
    public Result<List<M78Issue>> getIssuesWithStateOne() {
        List<M78Issue> issues = issueService.getIssuesWithStateOne();
        return Result.success(issues);
    }


    //更新issue状态(class)
    @PostMapping("/issue/updateStatus")
    @ResponseBody
    @HttpApiDoc(apiName = "更新Issue状态", value = "/open-api/v1/issue/updateStatus", method = MiApiRequestMethod.POST, description = "更新Issue状态")
    public Result<Boolean> updateIssueStatus(@RequestParam("issueId") Integer issueId, @RequestParam("newState") Integer newState) {
        boolean updated = issueService.updateIssueStatus(issueId, newState);
        return Result.success(updated);
    }


    //上传生成code信息到数据库(class)
    @PostMapping("/uploadCodeInfo")
    @ResponseBody
    @HttpApiDoc(apiName = "上传生成code信息", value = "/open-api/v1/uploadCodeInfo", method = MiApiRequestMethod.POST, description = "上传生成code信息到数据库")
    public Result<Boolean> uploadCodeInfo(HttpServletRequest request, @RequestBody M78CodeGenerationInfoDTO codeInfo) {
        M78CodeGenerationInfo info = new M78CodeGenerationInfo();
        BeanUtils.copyProperties(codeInfo, info);
        // 兼容历史字段
        if (codeInfo.getTotalCodeLines() > 0) {
            info.setTotalLinesCount(codeInfo.getTotalCodeLines());
        }
        if (StringUtils.isNotEmpty(codeInfo.getSystemVersion())) {
            info.setOsVersion(codeInfo.getSystemVersion());
        }
        boolean result = codeGenerationInfoService.uploadCodeInfo(info);
        return Result.success(result);
    }


    //根据用户查询用户今天生成的代码行数(class)
    @PostMapping("/user/todayCodeLines")
    @ResponseBody
    @HttpApiDoc(apiName = "查询用户今天生成的代码行数", value = "/open-api/v1/user/todayCodeLines", method = MiApiRequestMethod.POST, description = "根据用户查询用户今天生成的代码行数")
    public Result<Long> getTodayCodeLinesByUser(@RequestParam("userName") String userName) {
        long codeLines = codeGenerationInfoService.getTodayCodeLinesByUser(userName);
        return Result.success(codeLines);
    }


    //按用户分组,查询每个用户的生成代码的行数,支持分页(class)
    @PostMapping("/userCodeLines")
    @ResponseBody
    @HttpApiDoc(apiName = "按用户分组查询生成代码行数", value = "/open-api/v1/userCodeLines", method = MiApiRequestMethod.POST, description = "按用户分组查询每个用户的生成代码行数，支持分页")
    public Result<Page<M78CodeGenerationInfo>> getUserCodeLinesGroupedByUser(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        Page<M78CodeGenerationInfo> result = codeGenerationInfoService.getUserCodeLinesGroupedByUser(currentPage, pageSize);
        return Result.success(result);
    }


    @PostMapping("/feature/router/query")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/router/query", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    public Result<List<Map<String, Object>>> executeSqlQuery(HttpServletRequest request, @RequestBody FeatureRouterReq req) {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        if (StringUtils.isBlank(req.getUserName())) {
            throw new InvalidArgumentException("须传递用户名!");
        }
        return featureRouterService.query(req);
    }

    @PostMapping("/feature/router/probot/query")
    @ResponseBody
    @CrossOrigin
    public Result<?> exeBotQuery(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        String reqId = getRequestId(req);

        log.info("calling bot with param:{} reqId:{} userName:{}", req, reqId, req.get("userName"));

        Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();

        //校验Authorization
        if (exportMaxBotId < req.get("botId").getAsLong()) {
            String apiKey = request.getHeader("Authorization") == null ? null : request.getHeader("Authorization");
            if (StringUtils.isEmpty(apiKey)) {
                return Result.fail(STATUS_FORBIDDEN, "需要Authorization");
            }
            M78Bot m78Bot = botService.getById(req.get("botId").getAsString());
            List<ApiKeyDTO> apiKeyDTOList = apiKeyService.getApiKeysByApiKey(apiKey);
            if (CollectionUtils.isEmpty(apiKeyDTOList)) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            if (!apiKeyDTOList.get(0).getTypeId().equals(m78Bot.getWorkspaceId())) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            req.addProperty("openApiBot", gson.toJson(m78Bot));
        }

        //异步
        if (req.has("isAsync") && req.get("isAsync").getAsBoolean()) {
            return m78OpenapiAsyncTaskService.submit(req);
        }

        //同步
        Result jsonObjectResult = featureRouterService.executeProbot(req);
        if (jsonObjectResult.getCode() != 0) {
            return jsonObjectResult;
        } else {
            return Result.success(gson.toJson(jsonObjectResult.getData()));
        }
    }

    @GetMapping("/feature/router/getTaskById")
    @ResponseBody
    @CrossOrigin
    public Result<GetAsyncTaskRes> getTaskById(@RequestParam("taskId") String taskId) throws IOException {
        return m78OpenapiAsyncTaskService.getTaskById(taskId);
    }

    private static String getRequestId(JsonObject req) {
        try {
            String reqId = "";
            if (req.has("reqId")) {
                reqId = req.get("reqId").getAsString();
            }
            return reqId;
        } catch (Throwable ignore) {

        }
        return "";
    }

    @PostMapping("/feature/router/flow/getId")
    @ResponseBody
    @CrossOrigin
    public Result<String> getExeFlowId(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return Result.success(gson.toJson(featureRouterService.queryFlow(req, true).getData()));
    }

    @PostMapping("/feature/router/flow/query")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/flow/query", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    @CrossOrigin
    public Result<String> queryFlow(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        /*
        if (exportMaxFlowId < req.get("flowId").getAsLong()) {
            String apiKey = request.getHeader("Authorization") == null ? null : request.getHeader("Authorization");
            if (StringUtils.isEmpty(apiKey)) {
                return Result.fail(STATUS_FORBIDDEN, "需要Authorization");
            }
            FlowBasePo flowBasePo = flowService.getById(req.get("flowId").getAsString());
            if (flowBasePo == null) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
            List<ApiKeyDTO> apiKeyDTOList = apiKeyService.getApiKeysByApiKey(apiKey);
            if (CollectionUtils.isEmpty(apiKeyDTOList)) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            if (!apiKeyDTOList.get(0).getTypeId().equals(flowBasePo.getWorkSpaceId())) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
        }
         */
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return Result.success(gson.toJson(featureRouterService.queryFlow(req, false).getData()));
    }

    @PostMapping("/feature/router/flow/query/json")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/flow/query/json", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    @CrossOrigin
    public Result<Object> queryFlowJson(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        Result<JsonObject> res = featureRouterService.queryFlow(req, false);

        // 创建一个新的Gson实例，禁用HTML转义
        Gson gsonNoEscaping = new GsonBuilder().disableHtmlEscaping().create();

        // 将JsonObject转换为JSON字符串，禁用转义
        String resStr = gsonNoEscaping.toJson(res.getData());

        // 解析JSON字符串为JsonElement
        JsonElement jsonElement = JsonParser.parseString(resStr);

        // 递归处理JsonElement，去除字符串值中的转义双引号
        JsonElement cleanedElement = cleanJsonElement(jsonElement);

        // 将清理后的JsonElement转换为Map
        Map<String, Object> resultMap = gsonNoEscaping.fromJson(cleanedElement, Map.class);

        return Result.success(resultMap);
    }

    private JsonElement cleanJsonElement(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String value = element.getAsString();
            value = value.replaceAll("\\\"", "");
            return new JsonPrimitive(value);
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonObject newObj = new JsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                newObj.add(entry.getKey(), cleanJsonElement(entry.getValue()));
            }
            return newObj;
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            JsonArray newArr = new JsonArray();
            for (JsonElement e : arr) {
                newArr.add(cleanJsonElement(e));
            }
            return newArr;
        }
        return element;
    }

    @PostMapping("/feature/router/flow/querySync")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/flow/querySync", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    @CrossOrigin
    public Result<? extends Object> querySyncFlow(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        if (exportMaxFlowId < req.get("flowId").getAsLong()) {
            String apiKey = request.getHeader("Authorization") == null ? null : request.getHeader("Authorization");
            if (StringUtils.isEmpty(apiKey)) {
                return Result.fail(STATUS_FORBIDDEN, "需要Authorization");
            }
            FlowBasePo flowBasePo = flowService.getById(req.get("flowId").getAsString());
            if (flowBasePo == null) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
            List<ApiKeyDTO> apiKeyDTOList = apiKeyService.getApiKeysByApiKey(apiKey);
            if (CollectionUtils.isEmpty(apiKeyDTOList)) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            if (!apiKeyDTOList.get(0).getTypeId().equals(flowBasePo.getWorkSpaceId())) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
        }
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return featureRouterService.querySyncFlow(req, false);
    }

    @PostMapping("/feature/router/flow/async/query")
    @ResponseBody
    @CrossOrigin
    public Result<String> exeFlowAsync(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return Result.success(gson.toJson(featureRouterService.queryFlowAsync(req, false).getData()));
    }

    @PostMapping("/feature/router/flow/exe")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/flow/exe", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    @CrossOrigin
    public Result<String> exeFlow(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        if (exportMaxFlowId < req.get("flowId").getAsLong()) {
            String apiKey = request.getHeader("Authorization") == null ? null : request.getHeader("Authorization");
            if (StringUtils.isEmpty(apiKey)) {
                return Result.fail(STATUS_FORBIDDEN, "需要Authorization");
            }
            FlowBasePo flowBasePo = flowService.getById(req.get("flowId").getAsString());
            if (flowBasePo == null) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
            List<ApiKeyDTO> apiKeyDTOList = apiKeyService.getApiKeysByApiKey(apiKey);
            if (CollectionUtils.isEmpty(apiKeyDTOList)) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            if (!apiKeyDTOList.get(0).getTypeId().equals(flowBasePo.getWorkSpaceId())) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
        }
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return Result.success(gson.toJson(featureRouterService.exeFlow(req).getData()));
    }

    @PostMapping("/feature/router/flow/queryResult")
    @ResponseBody
    @HttpApiDoc(apiName = "开放接口调用FeatureRouter", value = "/open-api/v1/feature/flow/queryResult", method = MiApiRequestMethod.POST, description = "调用FeatureRouter")
    @CrossOrigin
    public Result<Object> queryResult(HttpServletRequest request) throws IOException {
        // TODO: 需要增加权限控制，目前为了方便演示使用用户名做限制
        String body = request.getReader().lines().collect(Collectors.joining());
        JsonObject req = gson.fromJson(body, JsonObject.class);
        log.info("calling bot with param:{}", req);
        if (exportMaxFlowId < req.get("flowId").getAsLong()) {
            String apiKey = request.getHeader("Authorization") == null ? null : request.getHeader("Authorization");
            if (StringUtils.isEmpty(apiKey)) {
                return Result.fail(STATUS_FORBIDDEN, "需要Authorization");
            }
            FlowBasePo flowBasePo = flowService.getById(req.get("flowId").getAsString());
            if (flowBasePo == null) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
            List<ApiKeyDTO> apiKeyDTOList = apiKeyService.getApiKeysByApiKey(apiKey);
            if (CollectionUtils.isEmpty(apiKeyDTOList)) {
                return Result.fail(STATUS_FORBIDDEN, "Authorization不匹配");
            }
            if (!apiKeyDTOList.get(0).getTypeId().equals(flowBasePo.getWorkSpaceId())) {
                return Result.fail(STATUS_FORBIDDEN, "参数错误");
            }
        }
        String userName = Optional.ofNullable(req.get("userName")).orElseThrow(() -> new InvalidArgumentException("须传递用户名!")).getAsString();
        return Result.success(featureRouterService.queryResult(req).getData());
    }

    @RequestMapping(value = "/feature/router/flow/resume", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public Result<Boolean> resumePausedFlow(@RequestBody FlowOperateParam flowOperateParam) {
        String cmd = Optional.ofNullable(flowOperateParam.getCmd()).orElseThrow(InvalidArgumentException::new);
        if (!FlowOperateCmdEnum.MANUAL_CONFIRM_FLOW.getName().equals(cmd)) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "cmd须为manualConfirmFlow");
        }
        return operateFlow(flowOperateParam);
    }

    @RequestMapping(value = "/feature/router/flow/cancel", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public Result<Boolean> cancelFlow(@RequestBody FlowOperateParam flowOperateParam) {
        String cmd = Optional.ofNullable(flowOperateParam.getCmd()).orElseThrow(InvalidArgumentException::new);
        if (!FlowOperateCmdEnum.CANCEL_FLOW.getName().equals(cmd)) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "cmd须为cancelFlow");
        }
        return operateFlow(flowOperateParam);
    }

    private Result<Boolean> operateFlow(FlowOperateParam flowOperateParam) {
        // HINT: flowRecordId and flowId is required
        String userName = Optional.ofNullable(flowOperateParam.getUserName()).orElseThrow(UserAuthException::new);
        Integer flowId = Optional.ofNullable(flowOperateParam.getFlowId()).orElseThrow(InvalidArgumentException::new);
        Integer flowRecordId = Optional.ofNullable(flowOperateParam.getFlowRecordId()).orElseThrow(InvalidArgumentException::new);
        String cmd = Optional.ofNullable(flowOperateParam.getCmd()).orElseThrow(InvalidArgumentException::new);
        FlowBasePo flow = flowService.getById(flowId);
        if (flow == null) {
            return Result.fail(STATUS_NOT_FOUND, "未找到id为" + flowId + "的flow");
        }
        Result<Boolean> res = flowService.operateFlow(flowOperateParam);
        log.info("operateFlow, userName:{}, flowId:{}, flowRecordId:{}, cmd:{}, res:{}", userName, flowId, flowRecordId, cmd, res);
        return res;
    }


    //获取当前机器ip,并返回String格式(class)
    @GetMapping("/machine/ip")
    @ResponseBody()
    @HttpApiDoc(apiName = "获取当前机器IP", value = "/open-api/v1/machine/ip", method = MiApiRequestMethod.POST, description = "获取当前机器的IP地址")
    public Result<String> getMachineIp(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取IP地址失败", e);
            throw new RuntimeException("无法获取机器IP地址");
        }
        return Result.success(ipAddress + ":" + AGENT_PORT);
    }

    //向chatgpt提问题,并返回结果(class)
    @PostMapping("/chatgpt/query")
    @ResponseBody()
    @HttpApiDoc(apiName = "向ChatGPT提问", value = "/open-api/v1/chatgpt/query", method = MiApiRequestMethod.POST, description = "向ChatGPT提出问题并获取答案")
    public Result<String> askChatGpt(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String promptName = (String) params.get("promptName");
        Map<String, String> paramMap = (Map<String, String>) params.get("params");
        List<String> keys = (List<String>) params.get("keys");

        String model = params.getOrDefault("__model", "").toString();
        log.info("model:{} params:{}", model, params);
        String modelTemperature = (String) params.getOrDefault("__model_temperature", "");
        if (StringUtils.isBlank(promptName)) {
            throw new InvalidArgumentException("必须提供promptName参数！");
        }
        if (paramMap == null || paramMap.isEmpty()) {
            throw new InvalidArgumentException("必须提供params参数！");
        }
        if (keys == null || keys.isEmpty()) {
            throw new InvalidArgumentException("必须提供keys参数！");
        }

        return chatgptService.call3(promptName, paramMap, keys, model, modelTemperature);
    }

    // 调用FeatureRouterProvider的FeatureRouterDetailById方法，根据id查询结果
    @PostMapping("/feature/router/detail")
    @ResponseBody()
    @HttpApiDoc(apiName = "查询FeatureRouter详情", value = "/open-api/v1/feature/router/detail", method = MiApiRequestMethod.POST, description = "根据ID查询FeatureRouter的详细信息")
    public Result<FeatureRouterDTO> getFeatureRouterDetailById(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long id = (Long) params.get("id");
        if (id == null) {
            throw new InvalidArgumentException("必须提供id参数！");
        }
        FeatureRouterDTO featureRouterDTO = featureRouterProvider.getFeatureRouterDetailById(id);
        return Result.success(featureRouterDTO);
    }

    @GetMapping("/bot/test")
    public Result<String> test(@RequestParam("botId") Long botId, @RequestParam("input") String input, @RequestParam("userName") String username, @RequestParam("topicId") String topicId) {
        return botService.executeBot(null, botId, input, username, topicId);
    }

    @PostMapping("/bot/executeBotWithParams")
    public Result<String> executeBotWithParams(@RequestBody Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            throw new InvalidArgumentException("必须提供参数！");
        }
        if (!params.containsKey("botId")) {
            throw new InvalidArgumentException("必须提供botId参数！");
        }
        if (!params.containsKey("userName")) {
            throw new InvalidArgumentException("必须提供username参数！");
        }
        if (!params.containsKey("topicId")) {
            throw new InvalidArgumentException("必须提供topicId参数！");
        }
        Long botId = Long.valueOf(params.get("botId").toString());
        String input = params.get("input").toString();
        String username = params.get("userName").toString();
        String topicId = params.get("topicId").toString();
        BotVo botVo = botService.getBotDetailByBotId(botId);
        Map<String, String> promptParams = null;
        if (params.containsKey("promptParams") && params.get("promptParams") != null) {
            promptParams = gson.fromJson(params.get("promptParams").toString(), new TypeToken<Map<String, String>>() {
            }.getType());
            String newBotSetting = TemplateUtils.renderTemplate2(botVo.getBotSetting().getSetting(), promptParams);
            String newPrompt = TemplateUtils.renderTemplate2(botVo.getBotSetting().getCustomizePrompt(), promptParams);
            botVo.getBotSetting().setSetting(newBotSetting);
            botVo.getBotSetting().setCustomizePrompt(newPrompt);
        }
        return botService.executeBot(botVo, botId, input, username, topicId);
    }

    @GetMapping("/bot/listBotByUser")
    @HttpApiDoc(apiName = "查询机器人列表", value = "/open-api/v1/bot/listBotByUser", method = MiApiRequestMethod.GET, description = "根据用户名查询机器人列表")
    public Result<List<BotSimpleVo>> listBotByUser(@RequestParam("username") String username) {
        return botService.listBotByUser(username);
    }

    @GetMapping("/bot/detail")
    public Result<String> getBotDetail(HttpServletRequest request,
                                       @HttpApiDocClassDefine(value = "botName", description = "机器人名") @RequestParam(value = "botName") String botName) {
        BotVo botDetailByBotName = botService.getBotDetailByBotName(botName);
        if (botDetailByBotName != null) {
            List<BotPluginBo> botPluginList = botDetailByBotName.getBotPluginList();
            if (CollectionUtils.isNotEmpty(botPluginList)) {
                List<BotPluginDetailBo> mergedPluginList = botPluginList.stream()
                        .flatMap(l -> l.getPluginDetailList().stream())
                        .toList();
                return Result.success(gson.toJson(mergedPluginList));
            }
        }
        return Result.success("");
    }

    @GetMapping("/getPresetQuestion")
    @HttpApiDoc(value = "/api/v1/bot/getPresetQuestion", method = MiApiRequestMethod.GET, apiName = "获取bot预设问题")
    public Result<PresetQuestionRes> getPresetQuestion(HttpServletRequest request, @RequestParam("botId") Long botId,
                                                       @RequestParam("topicId") String topicId, @RequestParam("input") String input) {
        return botService.getPresetQuestion("zhangxiaowei6", botId, input, topicId);
    }

    /**
     * 该方法用于获取机器人列表
     * 使用POST请求方式,映射路径为"/list"
     * 接收一个ReqBotListDto类型的请求体参数,用于传递查询条件
     * 返回一个Result<Page<BotVo>>类型的结果,其中包含分页后的机器人列表数据
     * 在处理请求之前,会进行跨域资源共享(CORS)的配置
     * 该方法还添加了一些API文档注释,用于生成API文档
     */
    @PostMapping("/list")
    @HttpApiDoc(value = "/api/v1/bot/list", method = MiApiRequestMethod.POST, apiName = "获取机器人")
    @CrossOrigin
    public Result<Page<BotVo>> listBot(HttpServletRequest request,
                                       @RequestBody ReqBotListDto reqBotListDto) {
        return Result.success(botService.listBot(reqBotListDto, reqBotListDto.getUsername()));
    }

    @PostMapping("/featureRouter/create")
    @ResponseBody
    @CrossOrigin
    @HttpApiDoc(apiName = "创建FeatureRouter", value = "/open-apis/v1/ai-plugin-new/featureRouter/create", method = MiApiRequestMethod.POST, description = "创建FeatureRouter")
    public Result<Boolean> create(HttpServletRequest request, @RequestBody FeatureRouterReq featureRouterReq) {
        FeatureRouterTypeEnum routerType = FeatureRouterTypeEnum.getTypeEnumByCode(featureRouterReq.getRouterType());
        if (PROBOT != routerType) {
            throw new InvalidArgumentException("本接口只能创建Probot类型的featureRouter!");
        }
        boolean created = featureRouterService.save(featureRouterReq, null);
        return Result.success(created);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @HttpApiDoc(apiName = "用户注册", value = "/open-apis/v1/ai-plugin-new/register", method = MiApiRequestMethod.POST, description = "用户注册")
    public Result<String> register(@Validated @RequestBody UserLoginReq userLoginReq) {
        return userLoginService.register(userLoginReq);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @HttpApiDoc(apiName = "用户登录", value = "/open-apis/v1/ai-plugin-new/login", method = MiApiRequestMethod.POST, description = "用户登录")
    public Result<UserLoginRes> login(@Validated @RequestBody UserLoginReq userLoginReq, HttpServletResponse response) {
        Result<UserLoginRes> res = userLoginService.login(userLoginReq);
        if (res.getCode() == 0 && StringUtils.isNotBlank(res.getData().getToken())) {
            Cookie tokenCookie = new Cookie(Constant.M78_TOKEN, res.getData().getToken());
            tokenCookie.setPath("/");
            Cookie appIdCookie = new Cookie(Constant.M78_APP_ID, res.getData().getAppId().toString());
            appIdCookie.setPath("/");
            response.addCookie(tokenCookie);
            response.addCookie(appIdCookie);
        }
        return res;
    }

    @RequestMapping(value = "/google/login", method = RequestMethod.POST)
    @HttpApiDoc(apiName = "Google三方登录", value = "/open-apis/v1/ai-plugin-new/login", method = MiApiRequestMethod.POST, description = "用户登录")
    public Result<UserLoginRes> googleLogin(@Validated @RequestBody GoogleUserLoginReq googleUserLoginReq, HttpServletResponse response) {
        Result<UserLoginRes> res = userLoginService.googleLogin(googleUserLoginReq);
        if (res.getCode() == 0 && StringUtils.isNotBlank(res.getData().getToken())) {
            Cookie tokenCookie = new Cookie(Constant.M78_TOKEN, res.getData().getToken());
            tokenCookie.setPath("/");
            Cookie appIdCookie = new Cookie(Constant.M78_APP_ID, res.getData().getAppId().toString());
            appIdCookie.setPath("/");
            response.addCookie(tokenCookie);
            response.addCookie(appIdCookie);
        }
        return res;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @HttpApiDoc(apiName = "用户登出", value = "/open-apis/v1/ai-plugin-new/logout", method = MiApiRequestMethod.POST, description = "用户登出")
    public Result<String> loginOut(@Validated @RequestBody UserLoginReq userLoginReq) {
        return userLoginService.loginOut(userLoginReq);
    }

    @RequestMapping(value = "/authToken", method = RequestMethod.POST)
    public Result<BizUserInfo> authToken(@Validated @RequestBody CheckLoginReq checkLoginReq) {
        return userLoginService.authToken(checkLoginReq);
    }

    @AppPermission
    @RequestMapping(value = "/chattopic/findOrCreate", method = RequestMethod.POST)
    public Result<List<ChatTopicPo>> findOrCreateChatTopics(HttpServletRequest request, @RequestBody ChatTopicSearchReq searchReq) {
        BizUserInfo account = SessionAccountHolder.getAccount();
        searchReq.setAppId(account.getAppId());
        searchReq.setUsername(account.getUserName());
        return chatDBService.findOrCreateChatTopics(searchReq);
    }

    @GetMapping("/test/redis/set")
    public Result<Long> redisSet() {
        String s = redis.setV2("test-key", "0", 1000 * 60);
        return Result.success(Long.parseLong(redis.get("test-key")));
    }

    // 文字转语音,文字的UTF-8编码长度不能超过2048
    @PostMapping(value = "/textToVoiceV2")
    public Result<String> textToVoiceV2(@RequestBody TextToVoiceParam param, HttpServletRequest request) {
        return voiceService.textToVoiceV2(param);
    }

    @PostMapping(value = "/textToVoice")
    public Result<String> textToVoice(@RequestBody TextToVoiceParam param) {
        return voiceService.textToVoice(param);
    }

    @PostMapping("/base64/to/word")
    @HttpApiDoc(value = "/base64/to/word", method = MiApiRequestMethod.POST, apiName = "bas64语音转文字")
    public Result<String> voiceBase64ToWord(HttpServletRequest request,
                                            @HttpApiDocClassDefine(value = "voice", description = "语音base64") @RequestBody VoiceToTextParam voice
    ) {
        String voice1 = voice.getVoice();
        return voiceService.voiceToWord(voice1);
    }

    // 点击收藏操作
    @ResponseBody
    @RequestMapping(value = "/applyCollect", method = RequestMethod.POST)
    public Result<Boolean> applyCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        log.info("applyCollect req:{}", req);
        // TODO: 改造成通用版
        /*CheckLoginReq checkLoginReq = new CheckLoginReq();
        checkLoginReq.setAppId(req.getAppId());
        checkLoginReq.setToken(req.getToken());
        Result<BizUserInfo> bizUserInfoResult = userLoginService.authToken(checkLoginReq);
        BizUserInfo account = bizUserInfoResult.getData();*/
        String account = req.getUserName();
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account;
        return userCollectService.applyCollect(username, req);
    }

    @ResponseBody
    @RequestMapping(value = "/isCollect", method = RequestMethod.POST)
    public Result<Boolean> isCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        log.info("isCollect req:{}", req);
        // TODO: 改造成通用版
        /*CheckLoginReq checkLoginReq = new CheckLoginReq();
        checkLoginReq.setAppId(req.getAppId());
        checkLoginReq.setToken(req.getToken());
        Result<BizUserInfo> bizUserInfoResult = userLoginService.authToken(checkLoginReq);
        BizUserInfo account = bizUserInfoResult.getData();*/
        String account = req.getUserName();
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account;
        log.info("isCollect username:{},req :{}", username, req);
        return userCollectService.isCollect(username, req);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteCollect", method = RequestMethod.POST)
    public Result<Boolean> deleteCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        log.info("deleteCollect req:{}", req);
        /*CheckLoginReq checkLoginReq = new CheckLoginReq();
        checkLoginReq.setAppId(req.getAppId());
        checkLoginReq.setToken(req.getToken());
        Result<BizUserInfo> bizUserInfoResult = userLoginService.authToken(checkLoginReq);
        BizUserInfo account = bizUserInfoResult.getData();*/
        String account = req.getUserName();
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account;
        log.info("isCollect username:{},req :{}", username, req);
        return userCollectService.deleteCollect(username, req);
    }


    @GetMapping("/list")
    @HttpApiDoc(value = "/api/v1/category/list", method = MiApiRequestMethod.GET, apiName = "获取分类列表")
    public Result<List<CategoryVo>> list(HttpServletRequest request, @HttpApiDocClassDefine(value = "type", description = "分类类型") @RequestParam(value = "type", required = false) Integer type) {
        return Result.success(categoryService.listCategory(type));

    }

    @PostMapping("/textToSpeech")
    public Result<byte[]> textToSpeech(@RequestBody TextToSpeechReq textToSpeechReq) {
        if (textToSpeechReq == null || StringUtils.isEmpty(textToSpeechReq.getText())) {
            return Result.fail(STATUS_BAD_REQUEST, "text is empty");
        }
        return Result.success(audioService.textToSpeech(textToSpeechReq));
    }

    @PostMapping("/T2APro")
    public Result<T2AProResponse> T2APro(@RequestBody TextToSpeechReq textToSpeechReq) {
        if (textToSpeechReq == null || StringUtils.isEmpty(textToSpeechReq.getText())) {
            return Result.fail(STATUS_BAD_REQUEST, "text is empty");
        }
        return Result.success(audioService.T2APro(textToSpeechReq));
    }

    @PostMapping("/speechToText")
    public Result<String> speechToText(@RequestBody byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Result.fail(STATUS_BAD_REQUEST, "text is empty");
        }
        return Result.success(audioService.callArsClient(bytes, "wav"));
    }

    @PostMapping("/speechToText1")
    public Result<String> speechToText1(HttpServletRequest request, @RequestParam("text") MultipartFile audioFile) {
        try {
            return Result.success(audioService.callArsClient(audioFile.getBytes(), "wav"));
        } catch (Exception e) {
            return Result.fail(STATUS_BAD_REQUEST, "text is empty");
        }
    }

    @PostMapping("/speechToText2")
    public Result<String> speechToText2(HttpServletRequest request, @RequestBody SpeechToTextReq speechToTextReq) {
        try {
            if (speechToTextReq == null || StringUtils.isEmpty(speechToTextReq.getText())) {
                return Result.fail(STATUS_BAD_REQUEST, "text is empty");
            }
            log.info("speechToTextReq2:{}", gson.toJson(speechToTextReq));
            byte[] decodedBytes = Base64.getDecoder().decode(speechToTextReq.getText());
            return Result.success(audioService.callArsClient(decodedBytes, speechToTextReq.getFormat()));
        } catch (Exception e) {
            log.error("speechToText2 error", e);
            return Result.fail(STATUS_BAD_REQUEST, "text is empty");
        }
    }

    @PostMapping("/createTask")
    @ResponseBody
    public Result<Long> createTask(HttpServletRequest request, @RequestBody CustomTaskBO task) {
        try {
            log.info("createDatasource:{}", task);
            long id = taskService.createTask(task);
            return Result.success(id);
        } catch (Exception e) {
            log.error("createDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/executeTask", method = RequestMethod.POST)
    public Result<String> executeTask(HttpServletRequest request,
                                      @RequestBody CustomTaskBO task) {
        try {
            log.info("executeTaskDatasource:{}", task);
            return Result.success(taskService.executeTask(task));
        } catch (Exception e) {
            log.error("executeDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
    public Result<Boolean> deleteTask(HttpServletRequest request,
                                      @RequestBody CustomTaskBO task) {
        try {
            log.info("deleteTaskDatasource:{}", task);
            return Result.success(taskService.deleteTask(task));
        } catch (Exception e) {
            log.error("deleteTaskDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @PostMapping("/feature/router/create")
    @ResponseBody
    public Result<Boolean> exportFlow(HttpServletRequest request, @RequestBody FeatureRouterReq featureRouterReq) {
        boolean created = featureRouterService.save(featureRouterReq, featureRouterReq.getUserName());
        return Result.success(created);
    }

    // 创建知识库(落库版本)
    @PostMapping(value = "/createKnowledgeBase")
    public Result<Long> createKnowledge(@RequestBody KnowledgeCreateV2Req req, HttpServletRequest request) {
        return knowledgeService.createKnowledgeBase(req, request.getParameter("userName"));
    }

    // 单个知识库下的知识列表
    @RequestMapping(value = "/file/myList", method = RequestMethod.GET)
    public Result<List<KnowledgeBaseFileResDto>> getFileList(@RequestParam Long knowledgeBaseId, @RequestParam String userName, HttpServletRequest request) {
        return knowledgeService.listKnowledgeBaseFiles(knowledgeBaseId, null, userName);
    }

    //单个知识库下面知识的block列表
    @GetMapping(value = "/listKnowledgeFileBlock")
    public Result<List<KnowledgeBaseBlockDTO>> listKnowledgeFileBlock(@RequestParam Long knowledgeId,
                                                                      @RequestParam Long knowledgeFileId,
                                                                      @RequestParam String userName, HttpServletRequest request) {
        Result<List<ZKnowledgeBaseFileBlockDTO>> listResult = knowledgeService.listKnowledgeFileBlock(knowledgeId, knowledgeFileId, userName);
        if (listResult == null || listResult.getData() == null) {
            return Result.success(new ArrayList<>());
        }
        List<KnowledgeBaseBlockDTO> result;
        boolean gray = grayService.gray2Knowledge(knowledgeId);
        result = listResult.getData().stream().map(item -> {
            KnowledgeBaseBlockDTO knowledgeBaseBlockDTO = new KnowledgeBaseBlockDTO();
            BeanUtils.copyProperties(item, knowledgeBaseBlockDTO);
            if (gray) {
                knowledgeBaseBlockDTO.setId(item.getBlockId());
            } else {
                knowledgeBaseBlockDTO.setId(item.getId().toString());
            }
            return knowledgeBaseBlockDTO;
        }).toList();

        return Result.success(result);
    }

    // 删除单个知识库下的知识里的block
    @PostMapping(value = "/deleteKnowledgeFileBlock")
    public Result<Boolean> deleteKnowledgeFileBlock(@RequestParam Long knowledgeId, @RequestParam Long knowledgeFileId,
                                                    @RequestParam String knowledgeFileBlockId, @RequestParam String userName, HttpServletRequest request) {
        return knowledgeService.deleteKnowledgeFileBlock(knowledgeId, knowledgeFileId, knowledgeFileBlockId, userName);
    }

    @PostMapping(value = "/searchKnowledge")
    public Result<List<ZKnowledgeRes>> searchKnowledge(HttpServletRequest request, @RequestParam Long knowledgeId, @RequestParam String queryText, @RequestParam String userName) {
        return knowledgeService.searchKnowledge(knowledgeId, queryText, userName);
    }

    //删除一条ChatMessage(project)
    @RequestMapping(value = "/chatmessage/delete", method = RequestMethod.POST)
    public Result<Void> deleteChatMessage(@RequestParam int messageId,
                                          @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.deleteChatMessageById(messageId, userName);
    }

    //插入一条ChatMessage(project)
    @RequestMapping(value = "/chatmessage/add", method = RequestMethod.POST)
    public Result<ChatMessagePo> insertChatMessage(@RequestBody ChatMessagePo chatMessage) {
        if (StringUtils.isEmpty(chatMessage.getUserName())) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        long now = System.currentTimeMillis();
        chatMessage.setState(1);
        chatMessage.setCtime(now);
        chatMessage.setUtime(now);
        return chatDBService.insertNewChatMessage(chatMessage);
    }

    //创建一个新的chatTopic(project)
    @RequestMapping(value = "/chattopic/add", method = RequestMethod.POST)
    public Result<ChatTopicPo> createChatTopic(@RequestBody ChatTopicPo chatTopic) {
        String userName = chatTopic.getUserName();
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.createChatTopic(chatTopic, userName);
    }

    //删除一个chatTopic(class)
    @RequestMapping(value = "/chattopic/delete", method = RequestMethod.GET)
    public Result<Void> deleteChatTopic(@RequestParam int topicId,
                                        @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.deleteChatTopicById(topicId, userName);
    }

    //查询某个chatTopic下的所有chatMessage(class)
    @RequestMapping(value = "/chatmessage/list", method = RequestMethod.GET)
    public Result<List<ChatMessagePo>> listChatMessagesByTopicId(@RequestParam int topicId, @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.getAllChatMessagesByTopicIdDesc(topicId, userName);
    }

    //查询某个userName下的所有messageTopic(project)
    @RequestMapping(value = "/messagetopic/list", method = RequestMethod.GET)
    public Result<List<ChatTopicPo>> listMessageTopicsByUserName(@RequestParam(value = "type", defaultValue = "0", required = false) int type,
                                                                 @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.getAllChatTopicsByUserName(userName, type);
    }

    @RequestMapping(value = "/messagetopic/detail", method = RequestMethod.GET)
    public Result<ChatTopicBo> messageTopicDetail(@RequestParam int topicId, @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.chatTopicDetail(topicId, userName);
    }

    //按topic id清空chatMessage,需要验证userName(project)
    @RequestMapping(value = "/message/clear", method = RequestMethod.GET)
    public Result<Void> clearChatMessages(@RequestParam int topicId, @RequestParam String userName) {
        if (StringUtils.isEmpty(userName)) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.clearChatMessagesByTopicId(topicId, userName);
    }

    //修改chatTopic信息(project)
    @RequestMapping(value = "/chattopic/update", method = RequestMethod.POST)
    public Result<ChatTopicPo> updateChatTopic(@RequestBody ChatTopicPo chatTopic) {
        if (StringUtils.isEmpty(chatTopic.getUserName())) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.updateChatTopic(chatTopic);
    }

    @RequestMapping(value = "/chat/import", method = RequestMethod.POST)
    public Result<Void> chatImport(@RequestBody ImportChatPo po) {
        if (StringUtils.isEmpty(po.getUserName())) {
            return Result.fail(GeneralCodes.ParamError, "userName is empty");
        }
        return chatDBService.importTopicAndChat(po);
    }

    @PostMapping("/feishu/msg/event")
    @HttpApiDoc(apiName = "飞书消息事件", value = "/api/v1/botplugin/feishu/msg/event", method = MiApiRequestMethod.POST, description = "飞书消息事件")
    public String feishuMsgEvent(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        log.info("feishu event msg:{}", body);
        // 这段代码是为了配置事件订阅
        if (body.containsKey("challenge")) {
            return "{ \n" +
                    "    \"challenge\": \"" + body.get("challenge") + "\" \n" +
                    "} ";
        }
        String json = gson.toJson(body);

        EventMessage message = gson.fromJson(json, EventMessage.class);
        return imService.sendMsg(message);
    }

    @RequestMapping(value = "/getBotByMeta", method = RequestMethod.GET)
    public Result<List<BotVo>> getBotByMeta(HttpServletRequest request,
                                            @RequestParam("metaKey") String metaKey) {
        if (StringUtils.isBlank(metaKey)) {
            return Result.fail(STATUS_FORBIDDEN, "meta信息不能为空！");
        }
        return Result.success(botService.getBotByMeta(metaKey));
    }

    @PostMapping(value = "/image/upload")
    @HttpApiDoc(apiName = "图片上传", value = "", method = MiApiRequestMethod.POST, description = "图片上传")
    public Result<String> uploadBotImageNoResize(String base64, Boolean isInner, HttpServletRequest request) {
        String url = fileService.uploadImageFileByBase64(ImageTypeEnum.NORMAL_IMAGE, base64, isInner);
        if (url != null) {
            return Result.success(url);
        }
        return Result.fail(GeneralCodes.InternalError, "upload image error");
    }

    @PostMapping(value = "/mxz/intention")
    @HttpApiDoc(apiName = "米小助意图识别", value = "", method = MiApiRequestMethod.POST, description = "米小助意图识别")
    public String mxzIntention(@RequestBody String query, HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("query", query);

        Map<String, Object> header = new HashMap<>();
        header.put("appid", mxzIntentionAppId);
        header.put("method", mxzIntentionMethod);
        header.put("appkey", mxzIntentionAppKey);
        String signSb = mxzIntentionAppId + jsonObject.toString() + mxzIntentionAppKey;
        String sign = DigestUtils.md5Hex(signSb).toUpperCase();

        header.put("sign", sign);
        header.put("key", null);

        Map<String, Object> dataNode = new HashMap<>();
        dataNode.put("header", header);
        dataNode.put("body", jsonObject.toString());

        String dataStr = GsonUtils.gson.toJson(dataNode);
        String base64Str = org.apache.commons.codec.binary.Base64.encodeBase64String(dataStr.getBytes());

        Map<String, String> params = new HashMap<>();
        params.put("url", mxzIntentionUrl);
        params.put("body", base64Str);
        String response = callPostApiWithOkHttp(params);
        return response;
    }

    //通过okhttp调用post接口
    public String callPostApiWithOkHttp(Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();
        String url = params.get("url");
        if (StringUtils.isBlank(url)) {
            throw new InvalidArgumentException("必须提供url参数！");
        }
        String jsonBody = params.get("body");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return response.message();
            }
            return response.body().string();
        } catch (IOException e) {
            log.error("调用POST接口失败", e);
            return null;
        }
    }

}
