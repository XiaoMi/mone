//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.xiaomi.youpin.gwdash.bo.NacosConfig;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.INacosConfigService;
//import com.xiaomi.youpin.gwdash.service.NacosService;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.RoleBo;
//import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
//import com.xiaomi.youpin.infra.rpc.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Service(interfaceClass = INacosConfigService.class,group = "${dubbo.group}")
//public class NacosConfigServiceImpl implements INacosConfigService {
//
//    @Autowired
//    private NacosService nacosService;
//
//    @Autowired
//    private UserService userService;
//
//    @Value("${hermes.project.name}")
//    private String projectName;
//
//    @NacosValue(value = "${allow.edit.by.rpc.data.ids:}",autoRefreshed = true)
//    private String allowEditDataIds;
//
//    @Override
//    public Result<String> updateNacosConfig(NacosConfig nacosConfig, String userName) {
//        if(nacosConfig == null || StringUtils.isBlank(nacosConfig.getDataId()) || StringUtils.isBlank(nacosConfig.getGroup()) || StringUtils.isBlank(nacosConfig.getNamespaceId()) || StringUtils.isBlank(userName)){
//            return Result.success(null);
//        }
//        if(!allowEditDataIds.contains(nacosConfig.getDataId()+",")){
//            return Result.success(CommonError.UnAuthorized.getMessage());
//        }
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(projectName);
//        queryRoleRequest.setUserName(userName);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//        List<String> canEditRoleNames = Arrays.asList(Consts.canEditNacosConfigRoles);
//        boolean cantEditNacos = roles.stream().noneMatch(role ->canEditRoleNames.contains(role.getName()));
//        if (cantEditNacos) {
//            return Result.success(CommonError.UnAuthorized.getMessage());
//        }
//        try {
//            return Result.success(nacosService.publishConfig(nacosConfig.getDataId(), nacosConfig.getGroup(), nacosConfig.getNamespaceId(), nacosConfig.getContent()));
//        } catch (Exception e) {
//            log.error("updateConfig error",e);
//            return Result.success("updateConfig error " + e.getMessage());
//        }
//    }
//}
