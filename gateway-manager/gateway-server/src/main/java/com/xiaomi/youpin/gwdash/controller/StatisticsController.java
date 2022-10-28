//package com.xiaomi.youpin.gwdash.controller;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DeployMachine;
//import com.xiaomi.youpin.gwdash.bo.DeploySetting;
//import com.xiaomi.youpin.gwdash.bo.ProjectStatusEnum;
//import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
//import com.xiaomi.youpin.gwdash.common.GwCache;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.Machine;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.PipelineService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import lombok.extern.slf4j.Slf4j;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 　@description: 统计
// * 　@author zhenghao
// *
// */
//@RestController
//@Slf4j
//public class StatisticsController {
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private GwCache cache;
//
//    /**
//     * 项目总数，总发布次数，最近7天成功率
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/statistics", method = {RequestMethod.POST, RequestMethod.GET})
//    public Result<Object> statistics() {
//        Map<String, Object> map = pipelineService.statistics();
//        log.info("PipelineController statistics result:{}", map);
//        return Result.success(map);
//    }
//
//    /**
//     * 项目类型，各环节发布比
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/statisticsChart", method = {RequestMethod.POST, RequestMethod.GET})
//    public Result<Object> statisticsChart() {
//        Map<String, Object> map = pipelineService.statisticsChart();
//        log.info("PipelineController statisticsChart result:{}", map);
//        return Result.success(map);
//    }
//
//    /**
//     * 7日内发布成功失败次数和发布次数
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/statistics7days", method = {RequestMethod.POST, RequestMethod.GET})
//    public Result<Object> statistics7days() {
//        Map<Object, Object> map = pipelineService.statistics7days();
//        log.info("PipelineController statistics7days result:{}", map);
//        return Result.success(map);
//    }
//
//    /**
//     * 最近12个月部署次数.
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/deployMonth", method = {RequestMethod.GET})
//    public Result<Object> deployMonth() {
//        Map<Object, Object> map = pipelineService.deployMonth();
//        log.info("PipelineController deployMonth result:{}", map);
//        return Result.success(map);
//    }
//
//    /**
//     * 统计所有应用部署部署情况
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/projects/info", method = {RequestMethod.GET})
//    public void projectsInfo(HttpServletResponse response, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) throws IOException {
//        Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
//        int total = dao.count(Project.class, cnd);
//        int pages = (total / pageSize) + 1;
//        StringBuffer tables = new StringBuffer();
//        tables.append("project,env");
//        for (int i = 1; i <= pages; i++) {
//            List<Project> projects = dao.query(Project.class, cnd, new Pager(i, pageSize));
//            if (null != projects) {
//                projects.stream().forEach(project -> {
//                    List<ProjectEnv> envs = projectEnvService.getProjectEnv(project.getId());
//                    if (null != envs && envs.size() > 0) {
//                        envs.stream().forEach(env -> {
//                            tables.append("\n" + project.getId() + "_" + project.getName() + "," + env.getId() + "_" + env.getName());
//                        });
//                    } else {
//                        tables.append("\n" + project.getId() + "_" + project.getName() + "," + "env is null");
//                    }
//                });
//            }
//        }
//        String filename = "ProjectsInfo.csv";
//        response.setContentType("text/plain");
//        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
//        OutputStream out = response.getOutputStream();
//        out.write(tables.toString().getBytes());
//        out.close();
//    }
//
//    /**
//     * 统计所有应用部署部署情况
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/statistics/projects/docker/info", method = {RequestMethod.GET})
//    public void projectsDockerInfo(HttpServletResponse response, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) throws IOException {
//        Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
//        int total = dao.count(Project.class, cnd);
//        int pages = (total / pageSize) + 1;
//        Gson gson = new Gson();
//        StringBuffer tables = new StringBuffer();
//        tables.append("project,env,cpu,machine_num,mem(单位:B),machine_list");
//        for (int i = 1; i <= pages; i++) {
//            List<Project> projects = dao.query(Project.class, cnd, new Pager(i, pageSize));
//            if (null != projects) {
//                projects.stream().forEach(project -> {
//                    List<ProjectEnv> envs = projectEnvService.getProjectEnv(project.getId());
//                    if (null != envs) {
//                        envs.stream().forEach(env -> {
//                            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(env.getPipelineId()).getData();
//                            if (null != projectPipeline) {
//                                DeploySetting deploySetting = projectPipeline.getDeploySetting();
//                                DeployInfo deployInfo = projectPipeline.getDeployInfo();
//                                if (null != deployInfo) {
//                                    if (DeployTypeEnum.isDocker(deploySetting.getDeployType())) {
//                                        List<DeployMachine> dms = deployInfo.getDockerMachineList();
//                                        if (null != dms) {
//                                            String machineList = gson.toJson(dms.stream().map(it -> {
//                                                String ip = it.getIp();
//                                                Machine machine = dao.fetch(Machine.class, Cnd.where("ip", "=", ip));
//                                                return null == machine ? ip : ip + "(" + machine.getHostname() + ")";
//                                            }).collect(Collectors.toList()));
//                                            tables.append("\n" + project.getId() + "_" + project.getName() + "," + env.getId() + "_" + env.getName() + "," + deploySetting.getDockerCup() + "," + dms.size() + "," + deploySetting.getDockerMem() + "," + machineList.replace(",", "#"));
//                                        }
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        }
//        String filename = "ProjectsDockerInfo.csv";
//        response.setContentType("text/plain");
//        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
//        OutputStream out = response.getOutputStream();
//        out.write(tables.toString().getBytes());
//        out.close();
//    }
//}
