package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.common.CommonEnum;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.sla.GetSlaListReq;
import run.mone.mimeter.dashboard.bo.sla.PerRuleItem;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;
import run.mone.mimeter.dashboard.bo.sla.SlaList;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.SlaService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static run.mone.mimeter.dashboard.bo.sla.DegreeEnum.*;
import static run.mone.mimeter.dashboard.bo.sla.SlaBusinessGroupEnum.InternetGame;
import static run.mone.mimeter.dashboard.bo.sla.SlaBusinessGroupEnum.InternetService;
import static run.mone.mimeter.dashboard.bo.sla.SlaRuleItemEnum.*;
import static run.mone.mimeter.dashboard.bo.sla.SlaRuleItemTypeEnum.BusinessMetrics;
import static run.mone.mimeter.dashboard.bo.sla.SlaRuleItemTypeEnum.MonitorMetrics;
import static run.mone.mimeter.dashboard.exception.CommonError.InvalidParamError;


@Slf4j
@RestController
@RequestMapping("/api/bench/sla")
@HttpApiModule(value = "SlaController", apiController = SlaController.class)
public class SlaController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SlaService slaService;


    @HttpApiDoc(apiName = "sla新增", value = "/api/bench/sla/new", method = MiApiRequestMethod.POST, description = "sla新增")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Result<Integer> newSla(HttpServletRequest request,
                                  @RequestBody SlaDto param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.newSla] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.setCreator(account.getUsername());
        param.setUpdater(account.getUsername());

        log.info("[SlaController.newSla] param: {}", param);
        return slaService.newSla(param);
    }

    @HttpApiDoc(apiName = "sla更新", value = "/api/bench/sla/update", method = MiApiRequestMethod.POST, description = "sla更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Boolean> updateSla(HttpServletRequest request,
                                     @RequestBody SlaDto param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.updateSla] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.setUpdater(account.getUsername());

        log.info("[SlaController.updateSla] param: {}", param);
        return slaService.updateSla(param);
    }

    @HttpApiDoc(apiName = "sla列表", value = "/api/bench/sla/list", method = MiApiRequestMethod.POST, description = "sla列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<SlaList> getSlaList(HttpServletRequest request,
                                      @RequestBody GetSlaListReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.getSlaList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[SlaController.getSlaList] param: {}", param);
        if (param == null) {
            return Result.fail(InvalidParamError);
        }
        return slaService.getSlaList(param);
    }

    @HttpApiDoc(apiName = "sla删除", value = "/api/bench/sla/del", method = MiApiRequestMethod.DELETE, description = "sla删除")
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public Result<Boolean> delSla(HttpServletRequest request,
                                  @RequestParam("id") int id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.delSla] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[SlaController.delSla] param: {}", id);
        return slaService.delSla(id);
    }

    @HttpApiDoc(apiName = "sla批量删除", value = "/api/bench/sla/multiDel", method = MiApiRequestMethod.POST, description = "sla批量删除")
    @RequestMapping(value = "/multiDel", method = RequestMethod.POST)
    public Result<Boolean> multiDelSla(HttpServletRequest request,
                                       @RequestBody List<Integer> ids) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.multiDelSla] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[SlaController.multiDelSla] param: {}", ids);
        return slaService.multiDelSla(ids);
    }

    @HttpApiDoc(apiName = "获取sla详情", value = "/api/bench/sla/detail", method = MiApiRequestMethod.GET, description = "获取sla详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Result<SlaDto> detail(HttpServletRequest request,
                                 @RequestParam("id") int id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.detail] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[SlaController.detail] param: {}", id);
        return slaService.getSlaById(id);
    }

    @HttpApiDoc(apiName = "sla业务分类枚举", value = "/api/bench/sla/businessgroup/list", method = MiApiRequestMethod.GET, description = "sla业务分类枚举")
    @RequestMapping(value = "/businessgroup/list", method = RequestMethod.GET)
    public Result<List<CommonEnum>> businessGroupList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.businessGroupList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        List<CommonEnum> list = Arrays.asList(
                new CommonEnum(InternetService.businessGroupCname, InternetService.businessGroupName),
                new CommonEnum(InternetGame.businessGroupCname, InternetGame.businessGroupName)
        );
        return Result.success(list);
    }

    @HttpApiDoc(apiName = "sla指标敏感程度枚举", value = "/api/bench/sla/degree/list", method = MiApiRequestMethod.GET, description = "sla指标敏感程度枚举")
    @RequestMapping(value = "/degree/list", method = RequestMethod.GET)
    public Result<List<CommonEnum>> degreeList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.degreeList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        List<CommonEnum> list = Arrays.asList(
                new CommonEnum(Sensitive.code, Sensitive.degreeCname),
                new CommonEnum(Tolerable.code, Tolerable.degreeCname),
                new CommonEnum(NotSensitive.code, NotSensitive.degreeCname)
        );
        return Result.success(list);
    }

    @HttpApiDoc(apiName = "sla指标枚举", value = "/api/bench/sla/ruleItem/list", method = MiApiRequestMethod.GET, description = "sla指标枚举")
    @RequestMapping(value = "/ruleItem/list", method = RequestMethod.GET)
    public Result<List<PerRuleItem>> ruleItemList(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.ruleItemList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        List<PerRuleItem> ruleItemList = new ArrayList<>();

        PerRuleItem businessMetrics = new  PerRuleItem();
        businessMetrics.setRuleItemTypeCname(BusinessMetrics.ruleItemTypeCname);
        businessMetrics.setRuleItemTypeName(BusinessMetrics.ruleItemTypeName);
        List<CommonEnum> businessMetricsList = Arrays.asList(
                new CommonEnum(SuccessRate.ruleItemCname, SuccessRate.ruleItemName, SuccessRate.unit),
                new CommonEnum(P99ResponseTime.ruleItemCname, P99ResponseTime.ruleItemName, P99ResponseTime.unit),
                new CommonEnum(AvgResponseTime.ruleItemCname, AvgResponseTime.ruleItemName, AvgResponseTime.unit),
                new CommonEnum(RequestPerSecond.ruleItemCname, RequestPerSecond.ruleItemName, RequestPerSecond.unit)
        );
        businessMetrics.setRuleList(businessMetricsList);
        ruleItemList.add(businessMetrics);

        PerRuleItem monitorMetrics = new  PerRuleItem();
        monitorMetrics.setRuleItemTypeCname(MonitorMetrics.ruleItemTypeCname);
        monitorMetrics.setRuleItemTypeName(MonitorMetrics.ruleItemTypeName);
        List<CommonEnum> monitorMetricsList = Arrays.asList(
                new CommonEnum(CpuUtilization.ruleItemCname, CpuUtilization.ruleItemName, CpuUtilization.unit),
                new CommonEnum(MemoryUtilization.ruleItemCname, MemoryUtilization.ruleItemName, MemoryUtilization.unit),
                new CommonEnum(Load5Average.ruleItemCname, Load5Average.ruleItemName, Load5Average.unit),
                new CommonEnum(Load5Max.ruleItemCname, Load5Max.ruleItemName, Load5Max.unit),
                new CommonEnum(DropConnectionAverage.ruleItemCname, DropConnectionAverage.ruleItemName, DropConnectionAverage.unit),
                new CommonEnum(DropConnectionMax.ruleItemCname, DropConnectionMax.ruleItemName, DropConnectionMax.unit)
        );
        monitorMetrics.setRuleList(monitorMetricsList);
        ruleItemList.add(monitorMetrics);

        return Result.success(ruleItemList);
    }

}
