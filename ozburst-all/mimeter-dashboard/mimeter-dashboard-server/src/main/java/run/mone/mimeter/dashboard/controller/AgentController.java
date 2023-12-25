package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.mimeter.dashboard.bo.agent.*;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.AgentService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/bench/agent")
@HttpApiModule(value = "AgentController", apiController = AgentController.class)
public class AgentController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AgentService agentService;

    @HttpApiDoc(value = "/api/bench/agent/getAgentList", apiName = "获取本组压测机列表详情", method = MiApiRequestMethod.POST, description = "获取压测机列表详情")
    @RequestMapping(value = "/getAgentList", method = RequestMethod.POST)
    public Result<List<AgentDTO>> getAgentList(
            HttpServletRequest request,String tenant) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getAgentList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        if (tenant == null || tenant.equals("")){
            tenant = account.getTenant();
        }
        return agentService.getAgentListByTenant(tenant);
    }

    @HttpApiDoc(value = "/api/bench/agent/getAllAgentList", apiName = "获取全部压测机列表详情", method = MiApiRequestMethod.POST, description = "获取全部压测机列表详情")
    @RequestMapping(value = "/getAllAgentList", method = RequestMethod.POST)
    public Result<AgentDTOList> getAllAgentList(
            HttpServletRequest request,GetAgentListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getAllAgentList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.getAllAgentList(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/getApplyList", apiName = "获取申请记录列表", method = MiApiRequestMethod.POST, description = "获取申请记录列表")
    @RequestMapping(value = "/getApplyList", method = RequestMethod.POST)
    public Result<AgentApplyList> getApplyList(
            HttpServletRequest request, GetApplyListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getApplyList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.getApplyList(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/getOrgList", apiName = "获取部门组织列表", method = MiApiRequestMethod.POST, description = "获取部门组织列表")
    @RequestMapping(value = "/getOrgList", method = RequestMethod.POST)
    public Result<List<OrgInfoVo>> getOrgList(
            HttpServletRequest request, String orgName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getOrgList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.getOrgList(orgName);
    }

    @HttpApiDoc(value = "/api/bench/agent/getAllAvailableAgentList", apiName = "获取全部可选压测机列表详情", method = MiApiRequestMethod.POST, description = "获取全部可选压测机列表详情")
    @RequestMapping(value = "/getAllAvailableAgentList", method = RequestMethod.POST)
    public Result<List<AgentDTO>> getAllAvailableAgentList(
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.获取全部可选压测机列表详情] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.getAllAvailableAgentList();
    }

    @HttpApiDoc(value = "/api/bench/agent/applyAgent", apiName = "批量申请压测机", method = MiApiRequestMethod.POST, description = "申请压测机")
    @RequestMapping(value = "/applyAgent", method = RequestMethod.POST)
    public Result<Boolean> applyAgent(
            HttpServletRequest request,
            @RequestBody AgentApplyReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.applyAgent] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.setApplier(account.getUsername());
        return agentService.applyAgent(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/approveApply", apiName = "通过压测机申请", method = MiApiRequestMethod.POST, description = "通过压测机申请")
    @RequestMapping(value = "/approveApply", method = RequestMethod.POST)
    public Result<Boolean> approveApply(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "applyID", required = true, description = "申请记录id", defaultValue = "66")
            Integer applyID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.approveApply] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.approveApply(applyID);
    }

    @HttpApiDoc(value = "/api/bench/agent/refuseApply", apiName = "拒绝压测机申请", method = MiApiRequestMethod.POST, description = "拒绝压测机申请")
    @RequestMapping(value = "/refuseApply", method = RequestMethod.POST)
    public Result<Boolean> refuseApply(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "applyID", required = true, description = "申请记录id", defaultValue = "66")
            Integer applyID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.refuseApply] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.refuseApply(applyID);
    }

    @HttpApiDoc(value = "/api/bench/agent/tenantForAgent", apiName = "给压测机打租户", method = MiApiRequestMethod.POST, description = "给压测机打租户")
    @RequestMapping(value = "/tenantForAgent", method = RequestMethod.POST)
    public Result<Boolean> tenantForAgent(
            HttpServletRequest request,
            TenantForAgentReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.tenantForAgent] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.tenantForAgent(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/applyAgentDomain", apiName = "批量绑定压测机域名", method = MiApiRequestMethod.POST, description = "批量绑定压测机域名")
    @RequestMapping(value = "/applyAgentDomain", method = RequestMethod.POST)
    public Result<Boolean> applyAgentDomain(
            HttpServletRequest request,
            @RequestBody List<DomainApplyReq> req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.applyAgentDomain] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.forEach(domainApplyReq -> domainApplyReq.setApplier(account.getUsername()));
        return agentService.applyAgentDomain(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/applyAgentDomainByRate", apiName = "根据比例批量绑定压测机域名", method = MiApiRequestMethod.POST, description = "根据比例批量绑定压测机域名")
    @RequestMapping(value = "/applyAgentDomainByRate", method = RequestMethod.POST)
    public Result<Boolean> applyAgentDomainByRate(
            HttpServletRequest request,
            @RequestBody DomainApplyByRateReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.applyAgentDomainByRate] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.setApplier(account.getUsername());
        return agentService.applyAgentDomainByRate(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/getDomainApplyList", apiName = "获取域名绑定申请记录列表", method = MiApiRequestMethod.POST, description = "获取域名绑定申请记录列表")
    @RequestMapping(value = "/getDomainApplyList", method = RequestMethod.POST)
    public Result<DomainApplyList> getDomainApplyList(
            HttpServletRequest request, @RequestBody GetApplyListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getDomainApplyList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.getDomainApplyList(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/approveDomainApply", apiName = "通过域名绑定申请", method = MiApiRequestMethod.POST, description = "通过压测机申请")
    @RequestMapping(value = "/approveDomainApply", method = RequestMethod.POST)
    public Result<Boolean> approveDomainApply(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "applyID", required = true, description = "申请记录id", defaultValue = "66")
            Integer applyID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.approveDomainApply] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.approveDomainApply(applyID);
    }

    @HttpApiDoc(value = "/api/bench/agent/refuseDomainApply", apiName = "拒绝域名绑定申请", method = MiApiRequestMethod.POST, description = "拒绝压测机申请")
    @RequestMapping(value = "/refuseDomainApply", method = RequestMethod.POST)
    public Result<Boolean> refuseDomainApply(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "applyID", required = true, description = "申请记录id", defaultValue = "66")
            Integer applyID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.refuseDomainApply] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.refuseDomainApply(applyID);
    }

    @HttpApiDoc(value = "/api/bench/agent/hostForAgent", apiName = "直接给一台压测机绑定域名", method = MiApiRequestMethod.POST, description = "直接给一台压测机绑定域名")
    @RequestMapping(value = "/hostForAgent", method = RequestMethod.POST)
    public Result<Boolean> hostForAgent(
            HttpServletRequest request,
            @RequestBody HostForAgentReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.hostForAgent] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.hostForAgent(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/delHostForAgents", apiName = "批量删除压测机某个域名绑定", method = MiApiRequestMethod.POST, description = "批量删除压测机某个域名绑定")
    @RequestMapping(value = "/delHostForAgents", method = RequestMethod.POST)
    public Result<Boolean> delHostForAgents(
            HttpServletRequest request,
            @RequestBody DelHostForAgentsReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.delHostForAgents] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return agentService.delHostForAgents(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/getAgentHostsFile", apiName = "获取某台机器hosts文件内容", method = MiApiRequestMethod.POST, description = "获取某台机器hosts文件内容")
    @RequestMapping(value = "/getAgentHostsFile", method = RequestMethod.POST)
    public Result<String> getAgentHostsFile(
            HttpServletRequest request,
            @RequestBody LoadHostsFileReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.getDomainApplyList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        if (req.getAgentIp().isEmpty()){
            return Result.success("");
        }
        return agentService.getAgentHostsFile(req);
    }

    @HttpApiDoc(value = "/api/bench/agent/syncDomainConf", apiName = "同步发压机域名绑定配置", method = MiApiRequestMethod.POST, description = "同步发压机域名绑定配置")
    @RequestMapping(value = "/syncDomainConf", method = RequestMethod.POST)
    public Result<Boolean> syncDomainConf(
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[AgentController.syncDomainConf] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return agentService.syncDomainConf();
    }
}
