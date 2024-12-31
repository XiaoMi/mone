package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.operationlog.GetOperationLogListReq;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogList;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.OperationLogService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static run.mone.mimeter.dashboard.exception.CommonError.InvalidParamError;

@Slf4j
@RestController
@RequestMapping("/api/bench/operationlog")
@HttpApiModule(value = "OperationLogController", apiController = OperationLogController.class)
public class OperationLogController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private OperationLogService operationLogService;


    @HttpApiDoc(apiName = "操作记录列表", value = "/api/bench/operationlog/list", method = MiApiRequestMethod.POST, description = "操作记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<OperationLogList> getOperationLogList(HttpServletRequest request,
                                                        @RequestBody GetOperationLogListReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[OperationLogController.getOperationLogList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[OperationLogController.getOperationLogList] param: {}", param);
        if (param == null) {
            return Result.fail(InvalidParamError);
        }
        return operationLogService.getOperationLogList(param);
    }


}
