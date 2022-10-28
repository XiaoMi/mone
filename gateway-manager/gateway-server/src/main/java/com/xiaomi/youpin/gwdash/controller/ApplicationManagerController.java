//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.annotation.OperationLog;
//import com.xiaomi.youpin.gwdash.dao.model.DockerInfoByIp;
//import com.xiaomi.youpin.gwdash.service.ApplicationManagerService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/application")
//public class ApplicationManagerController {
//
//    @Autowired
//    private ApplicationManagerService managerService;
//
//
//    /**
//     * 同步ip下对应的应用和部署环境
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/application/docker/sync", method = {RequestMethod.GET})
//    public boolean syncDockerInfoByIp() {
//        return managerService.syncDockerInfoByIp(20);
//    }
//
//    /**
//     * 获取同步状态
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/application/docker/sync/status", method = {RequestMethod.GET})
//    public boolean getSyncStatus() {
//        return managerService.getSyncStatus();
//    }
//
//    /**
//     * 获取ip下的所有应用
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/application/ip/docker/get", method = {RequestMethod.GET})
//    public List<DockerInfoByIp> getApplicationsByIp(HttpServletResponse response, @RequestParam(value = "ip") String ip) {
//        return managerService.getApplicationsByIp(ip);
//    }
//
//    /**
//     * 启动ip下所有应用
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/application/ip/docker/start", method = {RequestMethod.GET})
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    public List<DockerInfoByIp> startApplicationsByIp(HttpServletResponse response, @RequestParam(value = "ip") String ip) {
//        return managerService.startApplicationsByIp(ip);
//    }
//
//    /**
//     * 下线ip下所有应用
//     *
//     * @return
//     */
//    @RequestMapping(value = "/api/application/ip/docker/stop", method = {RequestMethod.GET})
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    public List<DockerInfoByIp> stopApplicationsByIp(HttpServletResponse response, @RequestParam(value = "ip") String ip) {
//        return managerService.stopApplicationsByIp(ip);
//    }
//}
