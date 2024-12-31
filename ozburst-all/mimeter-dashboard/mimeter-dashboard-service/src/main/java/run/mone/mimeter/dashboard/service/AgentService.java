package run.mone.mimeter.dashboard.service;

import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import run.mone.mimeter.dashboard.bo.agent.*;
import run.mone.mimeter.dashboard.bo.common.Result;

import java.util.List;

public interface AgentService {
    Result<List<AgentDTO>> getAgentListByTenant(String tenant);

    Result<AgentDTOList> getAllAgentList(GetAgentListReq req);

    Result<AgentApplyList> getApplyList(GetApplyListReq req);

    Result<List<OrgInfoVo>> getOrgList(String keyword);

    Result<List<AgentDTO>> getAllAvailableAgentList();

    Result<Boolean> applyAgent(AgentApplyReq req);

    Result<Boolean> refuseApply(Integer applyID);

    Result<Boolean> approveApply(Integer applyID);

    Result<Boolean> tenantForAgent(TenantForAgentReq req);

    Result<Boolean> applyAgentDomain(List<DomainApplyReq> req);

    Result<Boolean> applyAgentDomainByRate(DomainApplyByRateReq req);

    Result<DomainApplyList> getDomainApplyList(GetApplyListReq req);

    Result<Boolean> approveDomainApply(Integer applyID);

    Result<Boolean> refuseDomainApply(Integer applyID);

    Result<Boolean> hostForAgent(HostForAgentReq req);

    Result<Boolean> delHostForAgents(DelHostForAgentsReq req);

    Result<String> getAgentHostsFile(LoadHostsFileReq req);

    Result<Boolean> syncDomainConf();

}
