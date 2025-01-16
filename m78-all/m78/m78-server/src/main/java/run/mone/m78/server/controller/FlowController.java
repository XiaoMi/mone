package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.flow.*;
import run.mone.m78.api.enums.FlowConditionOperatorEnum;
import run.mone.m78.api.enums.FlowOperateCmdEnum;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.ws.FlowRecordSessionHolder;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dto.FlowPublishDto;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.flow.FlowDBService;
import run.mone.m78.service.service.flow.FlowRecordService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import java.util.Set;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Slf4j
@RestController
@HttpApiModule(value = "FlowController", apiController = FlowController.class)
@RequestMapping(value = API_PREFIX + "/flow")
public class FlowController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private FlowService flowService;
    @Autowired
    private FlowRecordService flowRecordService;

    @Resource
    private FlowDBService flowDBService;

    @Resource
    private WorkspaceService workspaceService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result<Integer> createFlow(HttpServletRequest request, @RequestBody FlowInfo flowInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowInfo.getFlowBaseInfo().setCreator(account.getUsername());
        return flowDBService.createFlow(flowInfo);
    }

    @RequestMapping(value = "/update/{scale}", method = RequestMethod.POST)
    public Result<Boolean> updateFlow(HttpServletRequest request, @RequestBody FlowInfo flowInfo, @PathVariable String scale) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return flowDBService.updateFlow(flowInfo, scale);
    }


    //根据id删除flow(class)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<Boolean> deleteFlow(HttpServletRequest request, @RequestParam Integer flowBaseId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return flowDBService.deleteFlowById(flowBaseId);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public Result<Integer> copyFlow(HttpServletRequest request, @RequestParam("originalId") int originalId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return flowDBService.copyFlow(originalId, account);
    }

    //根据FlowQry查询flowlist(class)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<Page<FlowInfo>> queryFlowList(HttpServletRequest request, @RequestBody FlowQryParam flowQryParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowQryParam.setUserName(account.getUsername());
        return flowDBService.queryFlowList(flowQryParam, account);
    }

    @HttpApiDoc(value = "/api/v1/flow/publishedList", method = MiApiRequestMethod.POST, apiName = "查询上架的flow列表")
    @RequestMapping(value = "/publishedList", method = RequestMethod.POST)
    public Result<Page<FlowPublishInfo>> queryPublishedFlowList(HttpServletRequest request, @RequestBody FlowQryParam flowQryParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowQryParam.setUserName(account.getUsername());
        flowQryParam.setPublishStatus(1);
        return flowDBService.queryPublishFlowList(flowQryParam, account);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Result<FlowInfo> queryFlowDetail(HttpServletRequest request, @RequestParam("id") int flowBaseId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return flowDBService.queryFlowInfoByBaseId(flowBaseId, account);
    }

    //import flow
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public Result<Integer> importFlow(HttpServletRequest request, @RequestBody FlowInfo flowInfo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowInfo.getFlowBaseInfo().setCreator(account.getUsername());
        return flowDBService.importFlow(flowInfo);
    }


    //test
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Result<FlowTestRes> testFlow(HttpServletRequest request, @RequestBody FlowTestParam flowTestParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowTestParam.setUserName(account.getUsername());
        return flowService.testFlow(flowTestParam);
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public Result<Void> cancelFlow(HttpServletRequest request, @RequestBody FlowOperateParam flowOperateParam) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowOperateParam.setUserName(account.getUsername());
        flowOperateParam.setCmd(FlowOperateCmdEnum.CANCEL_FLOW.getName());
        if (!flowService.operateFlow(flowOperateParam).getData()) {
            FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(
                    flowOperateParam.getFlowRecordId() + "", GsonUtils.gson.toJson(flowService.buildEndStatus()), WebsocketMessageType.FLOW_EXECUTE_STATUS);
        }
        return Result.success(null);
    }

    @RequestMapping(value = "/operate", method = RequestMethod.POST)
    public Result<Void> operateFlow(HttpServletRequest request, @RequestBody FlowOperateParam flowOperateParam) {
        if (FlowOperateCmdEnum.MODIFY_PARAM.getName().equals(flowOperateParam.getCmd()) &&
                (flowOperateParam.getNodeId() == null || MapUtils.isEmpty(flowOperateParam.getMeta()))) {
            return Result.fail(STATUS_BAD_REQUEST, "nodeId or meta is null");
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        flowOperateParam.setUserName(account.getUsername());
        if (!flowService.operateFlow(flowOperateParam).getData()) {
            FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(
                    flowOperateParam.getFlowRecordId() + "", GsonUtils.gson.toJson(flowService.buildEndStatus()), WebsocketMessageType.FLOW_EXECUTE_STATUS);
        }
        return Result.success(null);
    }

    //publish flow，入参为flowBaseId(class)
    @HttpApiDoc(value = "/api/v1/flow/publish", method = MiApiRequestMethod.POST, apiName = "上架flow")
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public Result<Boolean> publishFlow(HttpServletRequest request, @RequestBody FlowPublishDto flowPublishDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (flowPublishDto.getFlowId() == null) {
            return Result.fail(GeneralCodes.ParamError, "flowId不能为空");
        }
        if (flowPublishDto.getPublish() == null) {
            return Result.fail(GeneralCodes.ParamError, "publish不能为空");
        }
        if (flowPublishDto.getPublish() && CollectionUtils.isEmpty(flowPublishDto.getFlowCategorys())) {
            return Result.fail(GeneralCodes.ParamError, "flowCategory不能为空");
        }
        return flowDBService.publishFlow(flowPublishDto);
    }


    @RequestMapping(value = {"/flowStatus/stream"}, method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> ask(HttpServletRequest request,
                                          @RequestBody FlowStatusStreamParam flowStatusStreamParam) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());

        SseEmitter emitter = flowService.chatStream(flowStatusStreamParam);

        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

    //notify
    @RequestMapping(value = "/flowStatus/notify", method = RequestMethod.POST)
    public Result<Void> notify(HttpServletRequest request, @RequestBody SyncFlowStatus syncFlowStatus) {
        log.info("enter flow notify, " + syncFlowStatus.getFlowRecordId());
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
//        websocketDeal(syncFlowStatus);
        return Result.success(flowService.notifyFlowStatus(syncFlowStatus));
    }

    @HttpApiDoc(value = "/api/v1/flow/queryTestRecords", method = MiApiRequestMethod.POST, apiName = "查询flow执行历史")
    @RequestMapping(value = "/queryTestRecords", method = RequestMethod.POST)
    public Result<Page<FlowTestRecordDTO>> queryFlowRecordsByFlowId(HttpServletRequest request, @RequestBody FlowQryParam flowQryParam) {
        log.info("flowQryParam={}", flowQryParam);
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
//        websocketDeal(syncFlowStatus);
        return Result.success(flowRecordService.queryFlowRecordsByFlowId(flowQryParam));
    }

    //根据valueType获取对应的operator集合
    @RequestMapping(value = "/condition/operators", method = RequestMethod.GET)
    public Result<Set<FlowConditionOperatorEnum.OperatorInfo>> getOperatorsByValueType(HttpServletRequest request, @RequestParam(value = "valueType", required = false) String valueType) {
        return Result.success(FlowConditionOperatorEnum.getOperatorsByValueType(valueType));
    }

}
