//package com.xiaomi.youpin.gwdash.controller;
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.xiaomi.aegis.utils.AegisFacade;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.RoleBo;
//import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
//import com.xiaomi.youpin.hermes.filter.ApplicationContextProvider;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jasig.cas.client.validation.Assertion;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
///**
// * Prometheus 监控
// * @author gaoxihui
// * @date 2021/7/1 9:35 下午
// */
//@Slf4j
//@Controller
//public class PrometheusController {
//
//    private static final String PROMETHEUS_GRAFANA_ROLE = "prometheus-grafana";
//
//    @Autowired
//    UserService userService;
//
//    @NacosValue(value = "${grafana.domain.name:NoConfig}", autoRefreshed = true)
//    private String grafanaDomainName;
//
//    @RequestMapping(value = "/gwdash/prometheus/grafana", method = RequestMethod.GET)
//    public void dashBoard(HttpServletRequest request, HttpServletResponse response,String grafanaUri) throws IOException {
//
//        log.info("Prometheus#grafana param config grafanaDomainName  = " + grafanaDomainName);
//
//        if(StringUtils.isEmpty(grafanaDomainName) || "NoConfig".equals(grafanaDomainName)){
//            log.error("Prometheus#grafana: grafanaDomainName nacos config error,no domainName found!");
//            return;
//        }
//        log.info("Prometheus#grafana: grafanaDomainName config : {}",grafanaDomainName);
//
//        if(StringUtils.isEmpty(grafanaUri)){
//            log.error("Prometheus#grafana: menu config error,no grafanaUri param found!");
//        }
//
//        try {
//
//            if(!checkUserPermission(request)){
//                response.sendError(401, "Prometheus#grafana: unlogin or Permission Forbidden");
//                return;
//            }
//
//            String grafanaUriStr = StringUtils.isEmpty(grafanaUri) ? "" : grafanaUri;
//            log.info("Prometheus#grafana param grafanaUriStr = " + grafanaUriStr);
//            String dashBoardUrl = new StringBuffer(grafanaDomainName).append(grafanaUriStr).toString();
//            response.sendRedirect(dashBoardUrl);
//
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//        }
//    }
//
//    private boolean checkUserPermission(HttpServletRequest request){
//
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            log.error("Prometheus#grafana checkUserPermission: no valid user-info found in request!");
//            return false;
//        }
//        String userName = user.getUser();
//
//
//        if(StringUtils.isEmpty(userName)){
//            log.error("Prometheus#grafana checkUserPermission: no valid userName found!");
//            return false;
//        }
//
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(ApplicationContextProvider.projectName);
//        queryRoleRequest.setUserName(userName);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//
//        if(roles != null && roles.size() > 0 && roles.parallelStream()
//                .filter(e -> e.getName().contains(PROMETHEUS_GRAFANA_ROLE)).findAny().orElse(null) != null){
//            return true;
//        }
//        return false;
//
//    }
//}
