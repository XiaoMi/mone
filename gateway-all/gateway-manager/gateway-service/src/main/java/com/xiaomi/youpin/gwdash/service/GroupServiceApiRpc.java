package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoDTO;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoListDTO;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoListResultDTO;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntityDTO;
import com.xiaomi.youpin.gwdash.bo.openApi.GwUser;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Xirui Yang (yangxirui)
 * @version 1.0
 * @since 2022/5/19
 *
 * 用于重用dubbo consumer
 */
@Component
public class GroupServiceApiRpc {

    @Reference(check = false, interfaceClass = GroupServiceAPI.class, group = "${gw.intranet.group}")
    private GroupServiceAPI groupServiceAPI;

    @Resource
    private ApiGroupInfoService apiGroupInfoService;

    @Autowired
    private EnvConfig envConfig;

    public ApiGroupInfoListDTO describeGroups(int pageNo, int pageSize) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroups(pageNo, pageSize);
        }else{
            return this.apiGroupInfoService.describeGroups(pageNo, pageSize);
        }
    }

    public ApiGroupInfoListResultDTO describeGroupByName(List<GroupInfoEntityDTO> gids) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroupByName(gids);
        }else{
            return this.apiGroupInfoService.describeGroupByName(gids);
        }
    }

    public ApiGroupInfoListResultDTO describeGroupAll(List<GroupInfoEntityDTO> gids) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroupAll(gids);
        }else{
            return this.apiGroupInfoService.describeGroupAll(gids);
        }
    }

    public GwUser describeUserByName(String username) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeUserByName(username);
        }else{
            return this.apiGroupInfoService.describeUserByName(username);
        }
    }

    public ApiGroupInfoDTO describeGroupById(int gid) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroupById(gid);
        }else{
            return this.apiGroupInfoService.describeGroupById(gid);
        }
    }

    public ApiGroupInfoDTO getApiGroupByBaseUrl(String urlPrefix) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.getApiGroupByBaseUrl(urlPrefix);
        }else{
            return this.apiGroupInfoService.getApiGroupByBaseUrl(urlPrefix);
        }
    }

    public List<ApiGroupInfoDTO> describeGroupsByApiIds(List<Integer> apiIds) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroupsByApiIds(apiIds);
        }else{
            return this.apiGroupInfoService.describeGroupsByApiIds(apiIds);
        }
    }

    public List<ApiGroupInfoDTO> describeGroupsByIds(List<Integer> gids) {
        if(envConfig.isInternet()){
            return this.groupServiceAPI.describeGroupsByIds(gids);
        }else{
            return this.apiGroupInfoService.describeGroupsByIds(gids);
        }
    }
}
