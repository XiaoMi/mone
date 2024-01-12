package run.mone.mimeter.dashboard.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.mimeter.dashboard.bo.agent.*;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.common.HttpDao;
import run.mone.mimeter.dashboard.common.HttpResult;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.AgentApplyInfoMapper;
import run.mone.mimeter.dashboard.mapper.AgentInfoMapper;
import run.mone.mimeter.dashboard.mapper.DomainApplyInfoMapper;
import run.mone.mimeter.dashboard.service.AgentService;
import run.mone.mimeter.dashboard.pojo.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.DEFAULT_PAGE_SIZE;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Autowired
    private AgentInfoMapper agentInfoMapper;

    @Autowired
    private AgentApplyInfoMapper agentApplyInfoMapper;

    @Autowired
    private DomainApplyInfoMapper domainApplyInfoMapper;
    @DubboReference(registry = "stRegistry", check = false, group = "staging-open", version = "1.0")
    private UserOrgFacade userOrgFacade;

    @DubboReference(registry = "stRegistry", check = false, group = "staging-open", version = "1.0")
    private NodeFacade nodeFacade;

    /**
     * 心跳超时 15s
     */
    private static final int timeout_period = 15 * 1000;

    private static final Gson gson = new Gson();

    /**
     * 公用租户
     */
    private static final String PUB_TENANT = "MI";


    /**
     * bench manager addr
     */
    @NacosValue(autoRefreshed = true, value = "${bench.api_manager_url}")
    private String apiSvrUrl;

    @Autowired
    private HttpDao httpDao;

    /**
     * 每 10s 检查agent机器状态
     */
    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this::checkAgentStatus, 0, 30, TimeUnit.SECONDS);
    }

    public void checkAgentStatus() {
        try {
            long now = System.currentTimeMillis();
            AgentInfoExample example = new AgentInfoExample();
            example.createCriteria().andIpIsNotNull();
            List<AgentInfo> agentInfos = agentInfoMapper.selectByExample(example);
            agentInfos.forEach(agentInfo -> {
                long lastBeatTime = agentInfo.getUtime();
                if ((now - lastBeatTime) >= timeout_period) {
                    agentInfo.setEnable(false);
                    agentInfoMapper.updateByPrimaryKey(agentInfo);
                }
            });
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public Result<List<AgentDTO>> getAgentListByTenant(String tenant) {
        AgentInfoExample example = new AgentInfoExample();
        example.createCriteria().andTenantEqualTo(tenant).andIpIsNotNull().andEnableEqualTo(true);
        example.or(example.createCriteria().andTenantEqualTo(PUB_TENANT).andIpIsNotNull().andEnableEqualTo(true));
        List<AgentInfo> agentInfos = agentInfoMapper.selectByExampleWithBLOBs(example);

        return Result.success(agentInfos.stream().map(agentInfo -> {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentInfo, agentDTO);
            if (agentInfo.getDomainConf() != null) {
                agentDTO.setDomainConfs(gson.fromJson(agentInfo.getDomainConf(), new TypeToken<List<DomainConf>>() {
                }.getType()));
            } else {
                agentDTO.setDomainConfs(new ArrayList<>());
            }
            return agentDTO;
        }).collect(Collectors.toList()));
    }

    @Override
    public Result<AgentDTOList> getAllAgentList(GetAgentListReq req) {
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        AgentInfoExample totalExp = new AgentInfoExample();
        totalExp.createCriteria().andIpIsNotNull().andNodeIpIsNotNull().andEnableEqualTo(true);

        AgentInfoExample example = new AgentInfoExample();
        example.createCriteria().andIpIsNotNull().andNodeIpIsNotNull().andEnableEqualTo(true);
        example.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        List<AgentInfo> agentInfos = agentInfoMapper.selectByExampleWithBLOBs(example);

        AgentDTOList agentDTOList = new AgentDTOList();
        agentDTOList.setPage(req.getPage());
        agentDTOList.setPageSize(req.getPageSize());

        List<AgentDTO> list = agentInfos.stream().map(agentInfo -> {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentInfo, agentDTO);
            if (agentInfo.getDomainConf() != null) {
                agentDTO.setDomainConfs(gson.fromJson(agentInfo.getDomainConf(), new TypeToken<List<DomainConf>>() {
                }.getType()));
            } else {
                agentDTO.setDomainConfs(new ArrayList<>());
            }
            return agentDTO;
        }).collect(Collectors.toList());

        agentDTOList.setList(list);
        agentDTOList.setTotal(agentInfoMapper.countByExample(totalExp));

        return Result.success(agentDTOList);
    }

    @Override
    public Result<AgentApplyList> getApplyList(GetApplyListReq req) {
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        AgentApplyInfoExample totalExp = new AgentApplyInfoExample();
        AgentApplyInfoExample.Criteria totalCriteria = totalExp.createCriteria();

        AgentApplyInfoExample applyInfoExample = new AgentApplyInfoExample();
        AgentApplyInfoExample.Criteria exampleCriteria = applyInfoExample.createCriteria();

        if (req.getApplyUser() != null && !req.getApplyUser().equals("")) {
            exampleCriteria.andApplyUserEqualTo(req.getApplyUser());
            totalCriteria.andApplyUserEqualTo(req.getApplyUser());
        }

        if (req.getApplyStatus() != null) {
            exampleCriteria.andApplyStatusEqualTo(req.getApplyStatus());
            totalCriteria.andApplyStatusEqualTo(req.getApplyStatus());
        }
        applyInfoExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        AgentApplyList agentApplyList = new AgentApplyList();
        agentApplyList.setPage(req.getPage());
        agentApplyList.setPageSize(req.getPageSize());

        List<AgentApplyInfo> applyInfoList = agentApplyInfoMapper.selectByExample(applyInfoExample);
        if (applyInfoList == null || applyInfoList.size() == 0) {
            return Result.success(agentApplyList);
        }
        List<AgentApplyDTO> applyDTOS = new ArrayList<>(applyInfoList.size());

        applyInfoList.forEach(agentApplyInfo -> {
            AgentApplyDTO dto = new AgentApplyDTO();
            BeanUtils.copyProperties(agentApplyInfo, dto);
            applyDTOS.add(dto);
        });
        agentApplyList.setList(applyDTOS);
        agentApplyList.setTotal(agentApplyInfoMapper.countByExample(totalExp));
        return Result.success(agentApplyList);
    }

    @Override
    public Result<List<OrgInfoVo>> getOrgList(String keyword) {
        NodeOrgQryParam param = new NodeOrgQryParam();
        param.setAccount("system");
        param.setUserType(0);
        param.setOrgName(keyword);
        PageDataVo<OrgInfoVo> vo = nodeFacade.orgList(param).getData();
        return Result.success(vo.getList());
    }

    @Override
    public Result<List<AgentDTO>> getAllAvailableAgentList() {
        AgentInfoExample example = new AgentInfoExample();
        example.createCriteria().andIpIsNotNull().andNodeIpIsNotNull().andEnableEqualTo(true).andTenantIsNull();
        example.or(example.createCriteria().andIpIsNotNull().andNodeIpIsNotNull().andEnableEqualTo(true).andTenantEqualTo(""));
        List<AgentInfo> agentInfos = agentInfoMapper.selectByExample(example);

        return Result.success(agentInfos.stream().map(agentInfo -> {
            AgentDTO agentDTO = new AgentDTO();
            BeanUtils.copyProperties(agentInfo, agentDTO);
            return agentDTO;
        }).collect(Collectors.toList()));
    }

    @Override
    public Result<Boolean> applyAgent(AgentApplyReq req) {
        req.getAgentIDs().forEach(agentId -> {
            AgentInfo agentInfo = agentInfoMapper.selectByPrimaryKey(agentId);
            if (agentInfo == null) {
                return;
            }
            NullParam param = new NullParam();
            param.setAccount(req.getApplier());
            param.setUserType(0);
            OrgInfoVo orgInfoVo = userOrgFacade.getOrgByAccount(param).getData();
            if (orgInfoVo == null) {
                return;
            }
            AgentApplyInfoExample example = new AgentApplyInfoExample();
            //该组织已申请过该机器,待审核
            example.createCriteria().andAgentIpEqualTo(agentInfo.getNodeIp()).andApplyOrgIdEqualTo(orgInfoVo.getIdPath()).andApplyStatusEqualTo(ApplyStatusEnum.UnAuditing.statusCode);
            List<AgentApplyInfo> applyInfos = agentApplyInfoMapper.selectByExample(example);
            if (applyInfos != null && applyInfos.size() != 0) {
                return;
            }
            AgentApplyInfo apply = new AgentApplyInfo();
            apply.setApplyUser(req.getApplier());
            apply.setApplyOrgId(orgInfoVo.getIdPath());
            apply.setApplyOrgName(orgInfoVo.getNamePath());
            apply.setAgentIp(agentInfo.getNodeIp());
            apply.setAgentHostname(agentInfo.getHostname());
            apply.setApplyStatus(ApplyStatusEnum.UnAuditing.statusCode);
            apply.setCtime(System.currentTimeMillis());

            agentApplyInfoMapper.insert(apply);
        });

        return Result.success(true);
    }

    @Override
    public Result<Boolean> refuseApply(Integer applyID) {
        AgentApplyInfo applyInfo = agentApplyInfoMapper.selectByPrimaryKey(applyID);
        if (applyInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        applyInfo.setApplyStatus(ApplyStatusEnum.ApplyRefuse.statusCode);
        agentApplyInfoMapper.updateByPrimaryKey(applyInfo);

        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> approveApply(Integer applyID) {
        AgentApplyInfo applyInfo = agentApplyInfoMapper.selectByPrimaryKey(applyID);
        if (applyInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        AgentInfoExample example = new AgentInfoExample();
        example.createCriteria().andNodeIpEqualTo(applyInfo.getAgentIp());
        List<AgentInfo> agentInfo = agentInfoMapper.selectByExample(example);
        if (agentInfo == null || agentInfo.size() != 1) {
            return Result.fail(CommonError.InvalidParamError);
        }
        AgentInfo agent = agentInfo.get(0);
        if (agent.getTenant() != null && !agent.getTenant().equals("")) {
            return Result.fail(CommonError.AgentAlreadyGetTenant);
        }
        agent.setTenant(applyInfo.getApplyOrgId());
        agent.setTenantCn(applyInfo.getApplyOrgName());
        agentInfoMapper.updateByPrimaryKey(agent);

        //更新工单
        applyInfo.setApplyStatus(ApplyStatusEnum.ApplyPass.statusCode);
        agentApplyInfoMapper.updateByPrimaryKey(applyInfo);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> tenantForAgent(TenantForAgentReq req) {
        AgentInfo agentInfo = agentInfoMapper.selectByPrimaryKey(req.getAgentID());
        if (agentInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        agentInfo.setTenant(req.getTenant());
        agentInfo.setTenantCn(req.getTenantCn());

        agentInfoMapper.updateByPrimaryKey(agentInfo);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> applyAgentDomain(List<DomainApplyReq> req) {
        List<DomainApplyInfo> domainApplyInfoList = new ArrayList<>();
        AtomicBoolean errorIp = new AtomicBoolean(false);
        req.forEach(domainApplyReq -> {
            domainApplyReq.getAgentIPs().forEach(agentIp -> {
                AgentInfoExample example = new AgentInfoExample();
                example.createCriteria().andIpEqualTo(agentIp);
                List<AgentInfo> agentInfos = agentInfoMapper.selectByExample(example);
                if (agentInfos == null || agentInfos.size() == 0) {
                    errorIp.set(true);
                }
            });

            if (errorIp.get()) {
                return;
            }
            DomainApplyInfo apply = new DomainApplyInfo();
            apply.setApplyStatus(ApplyStatusEnum.UnAuditing.statusCode);
            apply.setApplyUser(domainApplyReq.getApplier());
            apply.setCtime(System.currentTimeMillis());
            apply.setDomain(domainApplyReq.getDomain());
            apply.setIp(domainApplyReq.getIp());

            apply.setAgentIpList(gson.toJson(domainApplyReq.getAgentIPs()));
            domainApplyInfoList.add(apply);
        });
        if (errorIp.get()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        domainApplyInfoMapper.batchInsert(domainApplyInfoList);

        return Result.success(true);
    }

    @Override
    public Result<Boolean> applyAgentDomainByRate(DomainApplyByRateReq req) {
        List<DomainApplyReq> domainApplyReqList = new ArrayList<>(req.getDomainIpAndRates().size());
        //申请的全部机器
        List<String> agentIps = req.getAgentIPs();
        if (agentIps.size() == 0 || agentIps.size() == 1) {
            return Result.fail(CommonError.InvalidAgentNumError);
        }
        //总机器数
        int totalAgent = agentIps.size();
        List<Integer> flagList = new ArrayList<>();
        int tmpSumRate = 0;
        for (DomainIpAndRate ipRate :
                req.getDomainIpAndRates()) {
            //比例分隔标记
            tmpSumRate += ipRate.getRate();
            double rate = tmpSumRate / 100d;
            int index = (int) (totalAgent * rate) - 1;
            flagList.add(index);
        }

        int pointer = -1;
        for (int i = 0; i < req.getDomainIpAndRates().size(); i++) {
            DomainApplyReq domainApplyReq = new DomainApplyReq();
            domainApplyReq.setApplier(req.getApplier());
            domainApplyReq.setDomain(req.getDomain());
            domainApplyReq.setIp(req.getDomainIpAndRates().get(i).getIp());
            domainApplyReq.setAgentIPs(agentIps.subList(pointer + 1, flagList.get(i)));
            pointer = flagList.get(i);
            domainApplyReqList.add(domainApplyReq);
        }
        return this.applyAgentDomain(domainApplyReqList);
    }

    @Override
    public Result<DomainApplyList> getDomainApplyList(GetApplyListReq req) {
        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        DomainApplyInfoExample totalExp = new DomainApplyInfoExample();
        DomainApplyInfoExample.Criteria totalCriteria = totalExp.createCriteria();

        DomainApplyInfoExample applyInfoExample = new DomainApplyInfoExample();
        DomainApplyInfoExample.Criteria exampleCriteria = applyInfoExample.createCriteria();

        if (req.getApplyUser() != null && !req.getApplyUser().equals("")) {
            exampleCriteria.andApplyUserEqualTo(req.getApplyUser());
            totalCriteria.andApplyUserEqualTo(req.getApplyUser());
        }

        if (req.getApplyStatus() != null) {
            exampleCriteria.andApplyStatusEqualTo(req.getApplyStatus());
            totalCriteria.andApplyStatusEqualTo(req.getApplyStatus());
        }
        applyInfoExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        DomainApplyList agentApplyList = new DomainApplyList();
        agentApplyList.setPage(req.getPage());
        agentApplyList.setPageSize(req.getPageSize());

        List<DomainApplyInfo> applyInfoList = domainApplyInfoMapper.selectByExampleWithBLOBs(applyInfoExample);
        if (applyInfoList == null || applyInfoList.size() == 0) {
            return Result.success(agentApplyList);
        }
        List<DomainApplyDTO> applyDTOS = new ArrayList<>(applyInfoList.size());

        applyInfoList.forEach(agentApplyInfo -> {
            DomainApplyDTO dto = new DomainApplyDTO();
            BeanUtils.copyProperties(agentApplyInfo, dto);
            dto.setAgentIpList(gson.fromJson(agentApplyInfo.getAgentIpList(), new TypeToken<List<String>>() {
            }.getType()));
            applyDTOS.add(dto);
        });
        agentApplyList.setList(applyDTOS);
        agentApplyList.setTotal(domainApplyInfoMapper.countByExample(totalExp));
        return Result.success(agentApplyList);
    }

    @Override
    @Transactional
    public Result<Boolean> approveDomainApply(Integer applyID) {
        DomainApplyInfo applyInfo = domainApplyInfoMapper.selectByPrimaryKey(applyID);
        if (applyInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        DomainApplyReq agentReq = new DomainApplyReq();
        agentReq.setDomain(applyInfo.getDomain());
        agentReq.setIp(applyInfo.getIp());
        agentReq.setAgentIPs(gson.fromJson(applyInfo.getAgentIpList(), new TypeToken<List<String>>() {
        }.getType()));

        HttpResult result;
        try {
            result = httpDao.post(apiSvrUrl + "/edit/hosts", gson.toJson(agentReq));
        } catch (Exception e) {
            log.error("[TaskService.submitTask] failed to submit task, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.APIServerError);
        }

        if (result.getCode() == 200) {
            //更新工单
            applyInfo.setApplyStatus(ApplyStatusEnum.ApplyPass.statusCode);
            domainApplyInfoMapper.updateByPrimaryKey(applyInfo);

            //更新机器domain字段
            CompletableFuture.runAsync(() -> callbackUpdateAgentHosts(agentReq), pool);

            return Result.success(true);
        }
        return Result.fail(CommonError.InvalidParamError);
    }

    /**
     * 发压机host更新成功后，回调保存各个机器域名绑定记录(新增或修改)
     */
    private void callbackUpdateAgentHosts(DomainApplyReq agentReq) {
        List<String> agentIpList = agentReq.getAgentIPs();
        if (agentIpList != null) {
            agentIpList.forEach(agentIp -> {
                AgentInfoExample example = new AgentInfoExample();
                example.createCriteria().andIpEqualTo(agentIp);
                List<AgentInfo> agentInfoList = agentInfoMapper.selectByExampleWithBLOBs(example);
                //机器存在
                if (agentInfoList != null && agentInfoList.size() != 0) {
                    AgentInfo agentInfo = agentInfoList.get(0);
                    if (agentInfo.getEnable()) {
                        List<DomainConf> domainConfs;
                        if (agentInfo.getDomainConf() == null) {
                            //暂无域名配置
                            domainConfs = new ArrayList<>();
                            DomainConf domainConf = new DomainConf();
                            domainConf.setDomain(agentReq.getDomain());
                            domainConf.setIp(agentReq.getIp());
                            domainConfs.add(domainConf);
                            agentInfo.setDomainConf(gson.toJson(domainConfs));
                        } else {
                            //已有域名绑定配置
                            domainConfs = gson.fromJson(agentInfo.getDomainConf(), new TypeToken<List<DomainConf>>() {
                            }.getType());
                            boolean find = false;
                            for (DomainConf domainConf :
                                    domainConfs) {
                                if (agentReq.getDomain().equals(domainConf.getDomain())) {
                                    find = true;
                                    //更新该域名绑定的ip
                                    domainConf.setIp(agentReq.getIp());
                                    break;
                                }
                            }
                            if (!find) {
                                //无相同域名，追加
                                DomainConf domainConf = new DomainConf();
                                domainConf.setDomain(agentReq.getDomain());
                                domainConf.setIp(agentReq.getIp());
                                domainConfs.add(domainConf);
                            }
                            agentInfo.setDomainConf(gson.toJson(domainConfs));
                        }
                        //更新
                        agentInfoMapper.updateByPrimaryKeyWithBLOBs(agentInfo);
                    }
                }
            });
        }
    }

    /**
     * 发压机host更新成功后，回调保存各个机器域名绑定记录(新增或修改)
     */
    private void callbackDelAgentHosts(DelHostForAgentsReq agentReq) {
        List<String> agentIpList = agentReq.getAgentIps();
        if (agentIpList != null) {
            agentIpList.forEach(agentIp -> {
                AgentInfoExample example = new AgentInfoExample();
                example.createCriteria().andIpEqualTo(agentIp);
                List<AgentInfo> agentInfoList = agentInfoMapper.selectByExampleWithBLOBs(example);
                //机器存在
                if (agentInfoList != null && agentInfoList.size() != 0) {
                    AgentInfo agentInfo = agentInfoList.get(0);
                    if (agentInfo.getEnable()) {
                        List<DomainConf> domainConfs;
                        if (agentInfo.getDomainConf() == null) {
                            //暂无域名配置
                        } else {
                            //已有域名绑定配置
                            domainConfs = gson.fromJson(agentInfo.getDomainConf(), new TypeToken<List<DomainConf>>() {
                            }.getType());
                            //过滤删除的域名配置
                            domainConfs = domainConfs.stream().filter(domainConf -> !agentReq.getDomain().equals(domainConf.getDomain().trim())).collect(Collectors.toList());
                            agentInfo.setDomainConf(gson.toJson(domainConfs));
                        }
                        //更新
                        agentInfoMapper.updateByPrimaryKeyWithBLOBs(agentInfo);
                    }
                }
            });
        }
    }

    @Override
    public Result<Boolean> refuseDomainApply(Integer applyID) {
        DomainApplyInfo applyInfo = domainApplyInfoMapper.selectByPrimaryKey(applyID);
        if (applyInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        //更新工单
        applyInfo.setApplyStatus(ApplyStatusEnum.ApplyRefuse.statusCode);
        domainApplyInfoMapper.updateByPrimaryKey(applyInfo);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> hostForAgent(HostForAgentReq req) {
        HttpResult result;
        try {
            result = httpDao.post(apiSvrUrl + "/manual/edit/hosts", gson.toJson(req));
        } catch (Exception e) {
            log.error("[TaskService.hostForAgent] failed to submit task, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {

            DomainApplyReq agentReq = new DomainApplyReq();
            agentReq.setDomain(req.getDomain());
            agentReq.setIp(req.getIp());
            agentReq.setAgentIPs(Collections.singletonList(req.getAgentIp()));

            CompletableFuture.runAsync(() -> callbackUpdateAgentHosts(agentReq), pool);

            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Result<Boolean> delHostForAgents(DelHostForAgentsReq req) {

        HttpResult result;
        try {
            result = httpDao.post(apiSvrUrl + "/del/hosts", gson.toJson(req));
        } catch (Exception e) {
            log.error("[TaskService.delHostForAgents] failed to del host from agent, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {
            //异步更新删除域名绑定配置
            CompletableFuture.runAsync(() -> callbackDelAgentHosts(req), pool);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Result<String> getAgentHostsFile(LoadHostsFileReq req) {
        HttpResult result;
        try {
            result = httpDao.post(apiSvrUrl + "/load/hosts", gson.toJson(req));
        } catch (Exception e) {
            log.error("[TaskService.getAgentHostsFile] failed to del host from agent, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {
            return Result.success(result.getData());
        }
        return Result.fail(CommonError.UnknownError);
    }

    /**
     * 同步域名绑定配置到压测机
     */
    @Override
    public Result<Boolean> syncDomainConf() {
        AgentInfoExample example = new AgentInfoExample();

        example.createCriteria().andEnableEqualTo(true);
        List<AgentInfo> agentInfoList = agentInfoMapper.selectByExampleWithBLOBs(example);
        if (agentInfoList == null || agentInfoList.size() == 0) {
            return Result.success(true);
        }
        SyncHostsReq syncHostsReq = new SyncHostsReq();
        List<AgentHostsConf> agentHostsConfList = new ArrayList<>();
        List<DomainConf> domainConfList;

        for (AgentInfo agentInfo :
                agentInfoList) {
            if (agentInfo.getDomainConf() != null) {
                //存在绑定域名配置，需要同步到压测机
                AgentHostsConf hostsConf = new AgentHostsConf();
                hostsConf.setAgentIp(agentInfo.getIp());
                domainConfList = gson.fromJson(agentInfo.getDomainConf(), new TypeToken<List<DomainConf>>() {
                }.getType());
                hostsConf.setDomainConfs(domainConfList);
                agentHostsConfList.add(hostsConf);
            }
        }
        //调用manager的请求数据
        //{"agentHostsConfList":[{"agentIp":"127.0.0.1","domainConfs":[{}]}]}
        syncHostsReq.setAgentHostsConfList(agentHostsConfList);

        HttpResult result;
        try {
            result = httpDao.post(apiSvrUrl + "/sync/hosts", gson.toJson(syncHostsReq));
        } catch (Exception e) {
            log.error("[TaskService.getAgentHostsFile] failed to del host from agent, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.UnknownError);
        }
        if (result.getCode() == 200) {
            return Result.success(true);
        }
        return Result.fail(CommonError.APIServerError);
    }
}
