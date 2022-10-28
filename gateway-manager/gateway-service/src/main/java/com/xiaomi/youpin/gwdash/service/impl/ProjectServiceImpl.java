//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.alibaba.nacos.client.naming.utils.CollectionUtils;
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.service.*;
//import org.apache.dubbo.common.utils.StringUtils;
//import org.apache.dubbo.config.annotation.Service;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.util.cri.SqlExpression;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Service(interfaceClass = IProjectService.class,group = "staging")
//public class ProjectServiceImpl implements IProjectService {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectEnvDeploySettingService projectEnvDeploySettingService;
//
//    @Autowired
//    private ProjectEnvBuildSettingService projectEnvBuildSettingService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Override
//    public List<ProjectBo> getProjectByName(String keyword,boolean exactMatch) {
//        if(StringUtils.isBlank(keyword) || keyword.length()<2){
//            return new ArrayList<>();
//        }
//        SqlExpression sql;
//        if(exactMatch){
//            sql = Cnd.exps("name", "=", keyword.trim());
//        }else{
//            sql = Cnd.exps("name", "like", "%"+keyword.trim()+"%");
//        }
//        List<Project> projects = dao.query(Project.class, Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId()).and(sql).limit(1,10));
//        if (CollectionUtils.isEmpty(projects)) {
//            return new ArrayList<>();
//        }
//        List<ProjectBo> result = new ArrayList<>(projects.size());
//        projects.forEach(project -> {
//            ProjectBo projectBo = new ProjectBo();
//            projectBo.setId((int)project.getId());
//            projectBo.setName(project.getName());
//            projectBo.setDesc(project.getDesc());
//            projectBo.setGitAddress(project.getGitAddress());
//            result.add(projectBo);
//        });
//        return result;
//    }
//
//    @Override
//    public List<ProjectEnv2Bo> listProjectEnvByProject(Integer projectId) {
//        if(projectId == null || projectId <=0 ){
//            return Collections.emptyList();
//        }
//        List<ProjectEnv> projectEnvs = projectEnvService.getProjectEnvByProjectId(projectId);
//        if(CollectionUtils.isEmpty(projectEnvs)){
//            return Collections.emptyList();
//        }
//        Map<Long, ProjectPipeline> pipelineMap = Collections.emptyMap();
//        List<Long> pipelineIds = projectEnvs.stream().map(ProjectEnv::getPipelineId).filter(pipelineId-> pipelineId > 0 ).collect(Collectors.toList());
//        if(!CollectionUtils.isEmpty(pipelineIds)){
//            List<ProjectPipeline> pipelines = pipelineService.listProjectPipelineByIds(pipelineIds);
//            if(!CollectionUtils.isEmpty(pipelineIds)){
//                pipelineMap = pipelines.stream().collect(Collectors.toMap(ProjectPipeline::getEnvId, Function.identity(), (p1, p2) -> p1));
//            }
//        }
//        List<Long> projectEnvIds = projectEnvs.stream().map(ProjectEnv::getId).collect(Collectors.toList());
//        List<ProjectEnvDeploySetting> deploySettings = projectEnvDeploySettingService.listDeploySettingByEnvIds(projectEnvIds);
//        Map<Long,ProjectEnvDeploySetting> deploySettingMap = deploySettings.stream().collect(Collectors.toMap(ProjectEnvDeploySetting::getEnvId, Function.identity(),(s1,s2)->s1));
//        List<ProjectEnvBuildSetting> buildSettings = projectEnvBuildSettingService.listProjectEnvBuildSettingByEnvIds(projectEnvIds);
//        Map<Long, ProjectEnvBuildSetting> buildSettingMap = buildSettings.stream().collect(Collectors.toMap(ProjectEnvBuildSetting::getEnvId, Function.identity(), (s1, s2) -> s1));
//        List<ProjectEnv2Bo> result = new ArrayList<>();
//        Map<Long, ProjectPipeline> finalPipelineMap = pipelineMap;
//        projectEnvs.forEach(projectEnv -> {
//            ProjectEnv2Bo env2Bo = new ProjectEnv2Bo();
//            env2Bo.setId(projectEnv.getId());
//            env2Bo.setName(projectEnv.getName());
//            env2Bo.setProjectId(projectEnv.getProjectId());
//            env2Bo.setBranch(projectEnv.getBranch());
//            env2Bo.setProfile(projectEnv.getProfile());
//            env2Bo.setGroup(projectEnv.getGroup());
//            env2Bo.setAuthority(projectEnv.getAuthority());
//            env2Bo.setDeployType(projectEnv.getDeployType());
//            ProjectEnvDeploySetting deploySetting = deploySettingMap.get(projectEnv.getId());
//            if(deploySetting != null){
//                env2Bo.setJvmParams(deploySetting.getJvmParams());
//                env2Bo.setLabels(deploySetting.getLabels());
//            }
//            ProjectEnvBuildSetting buildSetting = buildSettingMap.get(projectEnv.getId());
//            if(buildSetting != null){
//                env2Bo.setJarDir(buildSetting.getJarDir());
//            }
//            ProjectPipeline projectPipeline = finalPipelineMap.get(projectEnv.getId());
//            if(projectPipeline != null && projectPipeline.getDeployInfo() != null && !CollectionUtils.isEmpty(projectPipeline.getDeployInfo().getDockerMachineList())){
//                List<DeployMachine> deployMachines = projectPipeline.getDeployInfo().getDockerMachineList();
//                List<String> ips = deployMachines.stream().map(Machine::getIp).filter(ip -> !StringUtils.isBlank(ip)).collect(Collectors.toList());
//                if(!CollectionUtils.isEmpty(ips)){
//                    env2Bo.setIps(org.apache.commons.lang3.StringUtils.join(ips,","));
//                }
//            }
//            result.add(env2Bo);
//        });
//
//        return result;
//    }
//}
