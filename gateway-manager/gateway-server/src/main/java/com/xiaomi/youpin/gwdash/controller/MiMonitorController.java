//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.mone.monitor.service.GrafanaApiService;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.service.MiMonitorService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author gaoxihui
// * @date 2021/7/10 5:40 下午
// */
//@Slf4j
//@RestController
//public class MiMonitorController {
//
//    @Autowired
//    MiMonitorService miMonitorService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @GetMapping(value = "/api/mimonitor/getGrafanaUrl" , produces = "text/html;charset=utf-8")
//    public String getGrafanaUrlByAppName(long id){
//        log.info("MiMonitorController.getGrafanaUrlByAppName request id: {}", id);
//        Project project = projectService.getProjectById(id).getData();
//        if (null != project) {
//            return miMonitorService.getGrafanaUrl(project.getId() + "_" + project.getName());
//        }
//        return null;
//    }
//}
