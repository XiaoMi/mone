package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.invokeHistory.*;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.invokeHistory.M78InvokeHistoryService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/invokeHistory")
@HttpApiModule(value = "InvokeHistoryController", apiController = InvokeHistoryController.class)
public class InvokeHistoryController {

    @Autowired
    private LoginService loginService;

    @Resource
    private M78InvokeHistoryService m78InvokeHistoryService;


    //分页查询SummaryPerday的list
    @RequestMapping(value = "/listPerdayInfoByBotId", method = RequestMethod.POST)
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> getList(HttpServletRequest request,
                                                                  @RequestBody InvokePerdayListByBotIdReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return m78InvokeHistoryService.listPerdayInfoByBotId(req);
    }

    //分页查询SummaryPerday的list
    @RequestMapping(value = "/listPerdayInfos", method = RequestMethod.POST)
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> listSummaryPerday(HttpServletRequest request,
                                                                            @RequestBody InvokePerdayListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return m78InvokeHistoryService.listPerdayInfo(req);
    }


    //分页查询HistoryDetail的list
    @RequestMapping(value = "/listHistoryDetails", method = RequestMethod.POST)
    public Result<ListResult<M78InvokeHistoryDetailInfo>> listHistoryDetails(HttpServletRequest request,
                                                                             @RequestBody InvokeHistoryListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return m78InvokeHistoryService.listHistoryDetails(req);
    }

    //admin查询所有的bot/flow/plugin的调用情况
    @RequestMapping(value = "/listPerdayInfosByAdmin", method = RequestMethod.POST)
    public Result<ListResult<M78InvokeSummaryPerdayInfo>> listPerdayInfosByAdmin(HttpServletRequest request,
                                                                            @RequestBody InvokePerdayListReq req){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (!account.isAdmin()){
            return Result.fail(GeneralCodes.NotAuthorized,"not authorized");
        }

        return m78InvokeHistoryService.listPerdayInfosByAdmin(req);
    }

    //admin查询平台30天/15天/7天/昨天的总访问数、用户数
    @RequestMapping(value = "/listAllPerdayInfos", method = RequestMethod.POST)
    public Result<List<AllM78InvokeSummaryPerdayInfo>> listAllPerdayInfos(HttpServletRequest request,
                                                                          @RequestBody InvokePerdayListByAdminReq req){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (!account.isAdmin()){
            return Result.fail(GeneralCodes.NotAuthorized,"not authorized");
        }
        return m78InvokeHistoryService.listAllPerdayInfos(req);
    }

    @RequestMapping(value = "/fixBotImageUrl", method = RequestMethod.POST)
    public void fixImageUrl() {
        m78InvokeHistoryService.fixBotImageUrl();
    }

    @RequestMapping(value = "/fixPluginImageUrl", method = RequestMethod.POST)
    public void fixPluginImageUrl() {
        m78InvokeHistoryService.fixPluginImageUrl();
    }

    @RequestMapping(value = "/fixWorkspaceImageUrl", method = RequestMethod.POST)
    public void fixWorkspaceImageUrl() {
        m78InvokeHistoryService.fixWorkspaceImageUrl();
    }

    @RequestMapping(value = "/fixFlowImageUrl", method = RequestMethod.POST)
    public void fixFlowImageUrl() {
        m78InvokeHistoryService.fixFlowImageUrl();
    }

}

