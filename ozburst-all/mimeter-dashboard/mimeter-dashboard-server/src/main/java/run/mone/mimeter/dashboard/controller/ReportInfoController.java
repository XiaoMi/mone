package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.common.PagedResp;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.bo.report.RmReportReq;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.ReportInfoService;
import run.mone.mimeter.dashboard.service.SceneService;
import run.mone.mimeter.dashboard.service.SceneSnapshotService;
import run.mone.mimeter.dashboard.service.impl.LoginService;
import run.mone.mimeter.dashboard.common.ApiConsts;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static run.mone.mimeter.dashboard.bo.common.Constants.DEFAULT_API_TIMEOUT;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
@Slf4j
@RestController
@RequestMapping(ApiConsts.API_PREFIX + ApiConsts.REPORT_INFO_ROUTE)
@HttpApiModule(value = "ReportInfoController", apiController = ReportInfoController.class)
public class ReportInfoController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneSnapshotService sceneSnapshotService;

    private final String logPrefix = "[ReportInfoController]";

    @HttpApiDoc(value = ApiConsts.API_PREFIX + ApiConsts.REPORT_INFO_ROUTE + ApiConsts.GENERAL_LIST_ENDPOINT, apiName = "获取压测报告列表",
            method = MiApiRequestMethod.GET, description = "获取压测报告列表")
    @GetMapping(ApiConsts.GENERAL_LIST_ENDPOINT)
    public Result<PagedResp<List<ReportInfoBo>>> listReports(HttpServletRequest request,
                                                               @HttpApiDocClassDefine("keyword")
                                                               @RequestParam(required = false) String keyword,
                                                               @HttpApiDocClassDefine("pageNo")
                                                               @RequestParam(required = false) Integer pageNo,
                                                               @HttpApiDocClassDefine("pageSize")
                                                               @RequestParam(required = false) Integer pageSize)
            throws ExecutionException, InterruptedException, TimeoutException {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn(this.logPrefix + "newScene current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        CompletableFuture<Result<List<ReportInfoBo>>> cf1 = CompletableFuture.supplyAsync(
                () -> this.reportInfoService.listReports(account.getTenant(), keyword, pageNo, pageSize)
        );
        CompletableFuture<Result<Long>> cf2 = CompletableFuture.supplyAsync(
                () -> this.reportInfoService.countReports(account.getTenant(), keyword, pageNo, pageSize)
        );
        CompletableFuture.allOf(cf1, cf2).get(DEFAULT_API_TIMEOUT, TimeUnit.MILLISECONDS);
        List<ReportInfoBo> reportList = cf1.get().getData();
        PagedResp<List<ReportInfoBo>> resp = new PagedResp<>();

        resp.setTotal(cf2.get().getData());
        resp.setData(reportList);
        return Result.success(resp);
    }

    @HttpApiDoc(value = ApiConsts.API_PREFIX + ApiConsts.REPORT_INFO_ROUTE + ApiConsts.GENERAL_DETAILS_ENDPOINT, apiName = "获取压测报告详情",
            method = MiApiRequestMethod.GET, description = "获取压测报告详情")
    @GetMapping(ApiConsts.GENERAL_DETAILS_ENDPOINT)
    public Result<ReportInfoBo> reportDetails(HttpServletRequest request,
                                                @HttpApiDocClassDefine(value = "sceneId")
                                                @RequestParam(required = false) Long sceneId,
                                                @HttpApiDocClassDefine(value = "reportId", required = true)
                                                @RequestParam String reportId) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn(this.logPrefix + "reportDetails current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        ReportInfoBo report = this.reportInfoService.getReportDetails(sceneId, reportId).getData();
        ReportInfoBo obj = new ReportInfoBo();
        BeanUtils.copyProperties(report, obj);
        return Result.success(obj);
    }

    @HttpApiDoc(value = ApiConsts.API_PREFIX + ApiConsts.REPORT_INFO_ROUTE + ApiConsts.GENERAL_DELETE_ENDPOINT, apiName = "删除压测报告",
            method = MiApiRequestMethod.DELETE, description = "删除压测报告")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<Integer> removeReports(HttpServletRequest request,
                                         @RequestBody RmReportReq rmReportReq) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn(this.logPrefix + "removeReports current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return this.reportInfoService.removeReports(rmReportReq.getReportIds());
    }

}
