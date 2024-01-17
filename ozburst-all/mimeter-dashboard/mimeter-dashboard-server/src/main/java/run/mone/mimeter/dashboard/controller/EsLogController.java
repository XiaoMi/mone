package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.common.PagedResp;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.*;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.EsLogService;
import run.mone.mimeter.dashboard.service.impl.LoginService;
import run.mone.mimeter.dashboard.common.ApiConsts;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(ApiConsts.API_PREFIX + ApiConsts.API_STAT_ROUTE)
@HttpApiModule(value = "ApiStatController", apiController = EsLogController.class)
public class EsLogController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private EsLogService esLogService;

    @HttpApiDoc(value = ApiConsts.API_PREFIX + ApiConsts.API_STAT_ROUTE + ApiConsts.LOG_ROUTE + ApiConsts.GENERAL_LIST_ENDPOINT, apiName = "搜索压测api采样日志",
            method = MiApiRequestMethod.POST, description = "搜索压测api采样日志")
    @PostMapping(ApiConsts.LOG_ROUTE + ApiConsts.GENERAL_LIST_ENDPOINT)
    public Result<PagedResp<List<ReqRespLogRecord>>> searchApiLogs(HttpServletRequest request,
                                                                   @RequestBody SearchApiLogReq searchApiLogReq) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            String logPrefix = "[ApiStatController]";
            log.warn(logPrefix + "searchApiLogs current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return esLogService.searchApiLogs(searchApiLogReq);
    }

}
