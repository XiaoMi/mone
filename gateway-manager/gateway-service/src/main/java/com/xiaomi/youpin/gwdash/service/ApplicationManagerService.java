//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DeployMachine;
//import com.xiaomi.youpin.gwdash.bo.DeploySetting;
//import com.xiaomi.youpin.gwdash.bo.ProjectStatusEnum;
//import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
//import com.xiaomi.youpin.gwdash.dao.model.DockerInfoByIp;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @author tsingfu
// */
//@Service
//public class ApplicationManagerService {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private ProjectDeploymentService deploymentService;
//
//    private Lock lock = new ReentrantLock();
//
//    public boolean syncDockerInfoByIp(int pageSize) {
//        if(lock.tryLock()) {
//            try {
//                Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
//                int total = dao.count(Project.class, cnd);
//                int pages = (total / pageSize) + 1;
//                dao.clear("mione_docker_info_IP");
//                for (int i = 1; i <= pages; i++) {
//                    List<Project> projects = dao.query(Project.class, cnd, new Pager(i, pageSize));
//                    if (null != projects) {
//                        projects.stream().forEach(project -> {
//                            List<ProjectEnv> envs = projectEnvService.getProjectEnv(project.getId());
//                            if (null != envs) {
//                                envs.stream().forEach(env -> {
//                                    ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(env.getPipelineId()).getData();
//                                    if (null != projectPipeline) {
//                                        DeploySetting deploySetting = projectPipeline.getDeploySetting();
//                                        DeployInfo deployInfo = projectPipeline.getDeployInfo();
//                                        if (null != deployInfo) {
//                                            if (DeployTypeEnum.isDocker(deploySetting.getDeployType())) {
//                                                List<DeployMachine> dms = deployInfo.getDockerMachineList();
//                                                if (null != dms) {
//                                                    dms.stream().forEach(it -> {
//                                                        String ip = it.getIp();
//                                                        DockerInfoByIp dockerInfo = new DockerInfoByIp();
//                                                        dockerInfo.setIp(ip);
//                                                        dockerInfo.setPipelineId(projectPipeline.getId());
//                                                        dockerInfo.setProjectId(project.getId());
//                                                        dockerInfo.setProjectName(project.getName());
//                                                        dockerInfo.setEnvId(env.getId());
//                                                        dockerInfo.setEnvName(env.getName());
//                                                        dao.insert(dockerInfo);
//                                                    });
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }
//            } finally {
//                lock.unlock();
//            }
//            return true;
//        }
//        return false;
//    }
//
//    public boolean getSyncStatus() {
//        if(lock.tryLock()) {
//            try {
//                return true;
//            } finally {
//                lock.unlock();
//            }
//        }
//        return false;
//    }
//
//    public List<DockerInfoByIp> getApplicationsByIp(String ip) {
//        return dao.query(DockerInfoByIp.class, Cnd.where("ip", "=", ip));
//    }
//
//    public List<DockerInfoByIp> startApplicationsByIp(String ip) {
//        List<DockerInfoByIp> list = getApplicationsByIp(ip);
//        list.stream().forEach(it -> {
//            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(it.getPipelineId()).getData();
//            ProjectEnv projectEnv = projectEnvService.getProjectEnvById(it.getEnvId()).getData();
//            if (null != projectPipeline && null != projectEnv) {
//                deploymentService.dockerMachineOnlineWithIp(projectPipeline, projectEnv, ip);
//            }
//        });
//        return list;
//    }
//
//    public List<DockerInfoByIp> stopApplicationsByIp(String ip) {
//        List<DockerInfoByIp> list = getApplicationsByIp(ip);
//        list.stream().forEach(it -> {
//            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(it.getPipelineId()).getData();
//            ProjectEnv projectEnv = projectEnvService.getProjectEnvById(it.getEnvId()).getData();
//            if (null != projectPipeline && null != projectEnv) {
//                deploymentService.dockerMachineShutdownWithIp(projectPipeline, projectEnv, ip);
//            }
//        });
//        return list;
//    }
//}
