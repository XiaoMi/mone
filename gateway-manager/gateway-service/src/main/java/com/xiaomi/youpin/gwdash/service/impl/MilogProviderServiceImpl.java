//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.bo.openApi.ProjectDeployInfoQuery;
//import com.xiaomi.youpin.gwdash.common.ProjectEnvStatusEnum;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectRole;
//import com.xiaomi.youpin.gwdash.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.compress.utils.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Service;
//import org.glassfish.jersey.internal.guava.Sets;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.Sqls;
//import org.nutz.dao.sql.Sql;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @author wtt
// * @version 1.0
// * @description
// * @date 2021/7/22 17:31
// */
//@org.springframework.stereotype.Service
//@Slf4j
//@Service(interfaceClass = MilogProviderService.class, retries = 3, group = "${mione.env}_${dubbo.group}")
//public class MilogProviderServiceImpl implements MilogProviderService {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private MachineManagementService machineService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Override
//    public MiLogMachineBo queryMachineInfoByProject(String projectName, String name) {
//        MiLogMachineBo miLogMachineBo = new MiLogMachineBo();
//        Project project = projectService.getProjectByName(projectName);
//        if (null != project) {
//            miLogMachineBo.setProjectId(project.getId());
//            List<ProjectEnv> projectEnvs = getProjectEnv(project.getId(), null, name);
//            if (CollectionUtils.isNotEmpty(projectEnvs)) {
//                ProjectEnv projectEnv = projectEnvs.get(0);
//                miLogMachineBo.setEnvId(projectEnv.getId());
//                miLogMachineBo.setMachineInfos(getMachineinfos(projectEnv.getPipelineId()));
//            }
//        }
//
//        return miLogMachineBo;
//    }
//
//    public List<ProjectEnv> getProjectEnv(Long projectId, Long projectEnvId, String envName) {
//        Cnd cnd = Cnd.where("project_id", "=", projectId);
//        if (null != projectEnvId) {
//            cnd.and("id", "=", projectEnvId);
//        }
//        if (StringUtils.isNotEmpty(envName)) {
//            cnd.and("name", "=", envName);
//        }
//        cnd.and("status", "!=", ProjectEnvStatusEnum.DELETE.getId());
//        List<ProjectEnv> projectEnv = dao.query(ProjectEnv.class, cnd);
//        return projectEnv;
//    }
//
//    public Set<MachineBo> getMachineinfos(long pipelineId) {
//        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
//        if (null != projectPipeline) {
//            DeployInfo deployInfo = projectPipeline.getDeployInfo();
//            List<DeployMachine> dockerMachineList = deployInfo.getDockerMachineList();
//            if (CollectionUtils.isNotEmpty(dockerMachineList)) {
//                return dockerMachineList.stream().filter(it -> it.getStatus() == 0).map(deployMachine -> {
//                    MachineBo machineBo = new MachineBo();
//                    BeanUtils.copyProperties(deployMachine, machineBo);
//                    return machineBo;
//                }).collect(Collectors.toSet());
//            }
//        }
//        return Sets.newHashSet();
//    }
//
//    @Override
//    public Page<ProjectDeployInfoDTO> queryProjectDeployInfoList(ProjectDeployInfoQuery query) {
//        // 查出所有机器
//        Page<MachineBo> machinePage = machineService.queryMachineListByPage(query);
//        List<ProjectDeployInfoDTO> projectDeployInfoDTOList = new ArrayList<>();
//        ProjectDeployInfoDTO projectDeployInfoDTO;
//        for (MachineBo machineBo : machinePage.getList()) {
//            projectDeployInfoDTO = new ProjectDeployInfoDTO();
//            projectDeployInfoDTO.setMachineId(machineBo.getId());
//            projectDeployInfoDTO.setHostName(machineBo.getHostname());
//            projectDeployInfoDTO.setGroup(machineBo.getGroup());
//            projectDeployInfoDTO.setIp(machineBo.getIp());
//            projectDeployInfoDTO.setUpdateTime(machineBo.getUtime());
//            projectDeployInfoDTO.setStatus(machineBo.getLabels().get(MachineLabels.Apps).indexOf(query.getProjectName()) == -1 ? 0 : 1);
//            projectDeployInfoDTOList.add(projectDeployInfoDTO);
//        }
//        Page<ProjectDeployInfoDTO> page = new Page<>(machinePage.getPage(), machinePage.getPageSize(), machinePage.getTotal(), projectDeployInfoDTOList, true);
//        return page;
//    }
//
//    @Override
//    public List<String> queryAppsByIp(String ip) {
//        Sql sql = Sqls.create("SELECT DISTINCT\n" +
//                "\tproject_id \n" +
//                "FROM\n" +
//                "\t`project_pipeline` \n" +
//                "WHERE\n" +
//                "\tJSON_CONTAINS(\n" +
//                "\t\tdeploy_info -> '$.dockerMachineList[*].ip',\n" +
//                "\t\t'\"" + ip + "\"',\n" +
//                "\t'$') AND JSON_CONTAINS( deploy_info -> '$.dockerMachineList[*].status', '0', '$' )");
//        sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
//            List<String> list = new ArrayList<>();
//            while (rs.next()) {
//                list.add(rs.getString("project_id"));
//            }
//            return list;
//        });
//        dao.execute(sql);
//        return sql.getList(String.class);
//    }
//
//    @Override
//    public List<MachineBo> queryIpsByAppId(Long projectId, Long projectEnvId, String envName) {
//        List<ProjectEnv> projectEnvs = getProjectEnv(projectId, projectEnvId, envName);
//        List<MachineBo> machineBos = Lists.newArrayList();
//        if (CollectionUtils.isNotEmpty(projectEnvs)) {
//            projectEnvs.forEach(projectEnv -> {
//                long pipelineId = projectEnv.getPipelineId();
//                machineBos.addAll(getMachineinfos(pipelineId));
//            });
//        }
//        return machineBos;
//    }
//
//    @Override
//    public List<SimplePipleEnvBo> querySimplePipleEnvBoByProjectId(Long projectId) {
//        Result<List<ProjectEnv>> list = projectEnvService.getList(projectId);
//        if (CollectionUtils.isNotEmpty(list.getData())) {
//            return list.getData().stream().map(projectEnv -> {
//                long pipelineId = projectEnv.getPipelineId();
//                SimplePipleEnvBo.SimplePipleEnvBoBuilder builder = SimplePipleEnvBo.builder();
//                ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
//                if (null != projectPipeline) {
//                    List<DeployMachine> envMachineBos = projectPipeline.getDeployInfo().getDockerMachineList();
//                    if (CollectionUtils.isNotEmpty(envMachineBos)) {
//                        builder.ips(envMachineBos.stream().filter(it -> it.getStatus() == 0).map(DeployMachine::getIp).collect(Collectors.toList()));
//                    }
//                }
//                return builder.id(projectEnv.getId()).name(projectEnv.getName()).build();
//            }).collect(Collectors.toList());
//        }
//        return Lists.newArrayList();
//    }
//
//    @Override
//    public List<ProjectMemberDTO> queryProjectIdsByUserName(String userName) {
//        List<ProjectRole> projectRoles = dao.query(ProjectRole.class, Cnd.where("userName", "=", userName));
//        if (CollectionUtils.isNotEmpty(projectRoles)) {
//            return projectRoles.stream().map(projectRole ->
//                    ProjectMemberDTO.builder()
//                            .projectId(projectRole.getProjectId())
//                            .roleType(projectRole.getRoleType()).build())
//                    .collect(Collectors.toList());
//        }
//        return Lists.newArrayList();
//    }
//
//    @Override
//    public List<Long> queryDeleteProjectId() {
//        List<Project> projectRoles = dao.query(Project.class, Cnd.where("status", "=", ProjectStatusEnum.DELETE.getId()));
//        return projectRoles.stream().map(Project::getId).collect(Collectors.toList());
//    }
//
//}
