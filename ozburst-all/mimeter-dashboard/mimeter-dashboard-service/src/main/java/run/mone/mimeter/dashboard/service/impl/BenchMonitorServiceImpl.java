package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiami.mione.tesla.k8s.service.K8sProxyService;
import com.xiaomi.faas.func.api.MimeterService;
import com.xiaomi.mione.tesla.k8s.bo.PodNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.scene.SceneTaskAppsBo;
import run.mone.mimeter.dashboard.bo.agent.AgentMonitorBo;
import run.mone.mimeter.dashboard.bo.agent.AgentMonitorInfo;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.common.HttpDao;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.ReportInfoMapper;
import run.mone.mimeter.dashboard.mapper.SceneApiInfoMapper;
import run.mone.mimeter.dashboard.mapper.SceneInfoMapper;
import run.mone.mimeter.dashboard.mapper.SerialLinkMapper;
import run.mone.mimeter.dashboard.service.BenchMonitorService;
import run.mone.mimeter.dashboard.pojo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BenchMonitorServiceImpl implements BenchMonitorService {

    @Autowired
    private HttpDao httpDao;


    /**
     * 前后拓宽时间
     */
    private static final Long expendTime = (long) (5 * 1000);

    @Autowired
    private ReportInfoMapper reportInfoMapper;

    @Autowired
    private SerialLinkMapper serialLinkMapper;

    @Autowired
    private SceneApiInfoMapper apiInfoMapper;

    private static final Gson gson = Util.getGson();

    @DubboReference(registry = "stRegistry",check = false,group = "staging",timeout = 3000)
    private MimeterService mimeterServiceSt;

    @DubboReference(registry = "olRegistry",check = false,group = "online",timeout = 3000)
    private MimeterService mimeterServiceStOl;

    @DubboReference(check = false,group = "${ref.k8s.service.group}",timeout = 3000)
    private K8sProxyService k8sProxyService;

    @Autowired
    private SceneInfoMapper sceneInfoMapper;

    @Override
    public Result<SceneTaskAppsBo> getAppListByReportID(Integer sceneId, String reportId, Boolean realTime) {

        SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(sceneId);
        if (sceneInfo == null){
            return Result.success(new SceneTaskAppsBo());
        }

        String flag = sceneId + "_" + reportId;
        com.xiaomi.youpin.infra.rpc.Result res;
        try {
            if (sceneInfo.getSceneEnv() == Constants.SCENE_ENV_ST){
                res = mimeterServiceSt.getApps(flag);
            }else if (sceneInfo.getSceneEnv() == Constants.SCENE_ENV_OL){
                res = mimeterServiceStOl.getApps(flag);
            }else {
                return Result.success(new SceneTaskAppsBo());
            }
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
        if (res.getCode() == 0) {
            if (res.getData() == null){
                return Result.success(new SceneTaskAppsBo());
            }
            SceneTaskAppsBo sceneTaskAppsBo = null;
            try {
                sceneTaskAppsBo = gson.fromJson(res.getData().toString(), new TypeToken<SceneTaskAppsBo>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                log.error("getAppListByReportID failed,e:{}",e.getMessage());
                return Result.success(new SceneTaskAppsBo());
            }
            ReportInfoExample example = new ReportInfoExample();
            example.createCriteria().andReportIdEqualTo(reportId);
            List<ReportInfo> reportInfoList = reportInfoMapper.selectByExample(example);

            if (reportInfoList == null || reportInfoList.size() == 0) {
                return Result.fail(CommonError.InvalidParamError);
            }
            ReportInfo reportInfo = reportInfoList.get(0);

            sceneTaskAppsBo.setFromTime(reportInfo.getCreateTime().getTime() - expendTime);
            if (realTime) {
                //实时
                sceneTaskAppsBo.setToTime(null);
            } else {
                if (reportInfo.getFinishTime() != null){
                    sceneTaskAppsBo.setToTime(reportInfo.getFinishTime() + expendTime);
                }
            }
            sceneTaskAppsBo.getSerialLinks().forEach(linkTaskAppsBo -> {
                SerialLink serialLink = serialLinkMapper.selectByPrimaryKey(Integer.parseInt(linkTaskAppsBo.getSerialLinkId()));
                linkTaskAppsBo.setSerialLinkName(serialLink.getName());
                linkTaskAppsBo.getApis().forEach(apiTaskAppsBo -> {
                    SceneApiInfo apiInfo = apiInfoMapper.selectByPrimaryKey(Integer.parseInt(apiTaskAppsBo.getApiId()));
                    apiTaskAppsBo.setApiName(apiInfo.getApiName());
                });
            });
            sceneTaskAppsBo.setSceneTask(flag);
            return Result.success(sceneTaskAppsBo);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Result<AgentMonitorBo> getAgentInfosByReport(String reportId, Boolean realTime) {
        AgentMonitorBo agentMonitorBo = new AgentMonitorBo();

        List<AgentMonitorInfo> agentInfos = new ArrayList<>();
        ReportInfoExample example = new ReportInfoExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        List<ReportInfo> reportInfoList = reportInfoMapper.selectByExampleWithBLOBs(example);

        if (reportInfoList == null || reportInfoList.size() == 0) {
            return Result.fail(CommonError.InvalidParamError);
        }
        ReportInfo reportInfo = reportInfoList.get(0);

        String agentJson = reportInfo.getAgents();
        List<String> agentAddrs = gson.fromJson(agentJson, new TypeToken<List<String>>() {
        }.getType());

        log.info("!!! agent info 1:{}",agentJson);
        if (agentAddrs == null){
            return Result.success(agentMonitorBo);
        }

        agentMonitorBo.setFromTime(reportInfo.getCreateTime().getTime());
        if (realTime) {
            agentMonitorBo.setToTime(null);
        } else {
            if (reportInfo.getFinishTime() != null){
                agentMonitorBo.setToTime(reportInfo.getFinishTime() + expendTime);
            }else {
                agentMonitorBo.setToTime(System.currentTimeMillis());
            }
        }

        List<String> podIps = agentAddrs.stream().map(addr -> addr.substring(0,addr.indexOf(":"))).collect(Collectors.toList());

        log.info("!!! agent info 1:{}",gson.toJson(podIps));

        List<PodNode> agents = k8sProxyService.getNodeIP(podIps).getData();

        if (agents.size() != 0){
            agents.forEach(agent ->{
                AgentMonitorInfo agentMonitorInfo = new AgentMonitorInfo();
                agentMonitorInfo.setNodeIp(agent.getNodeIP());
                agentMonitorInfo.setPodIp(agent.getPodIP());
                agentInfos.add(agentMonitorInfo);
            });
        }
        agentMonitorBo.setAgentMonitorInfos(agentInfos);
        return Result.success(agentMonitorBo);
    }
}
