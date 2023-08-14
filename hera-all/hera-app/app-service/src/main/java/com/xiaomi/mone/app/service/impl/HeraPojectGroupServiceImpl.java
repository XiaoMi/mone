package com.xiaomi.mone.app.service.impl;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupModel;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.api.service.HeraProjectGroupServiceApi;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.service.project.group.HeraProjectGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/6 10:58 上午
 */
@Slf4j
@Service(registry = "registryConfig", interfaceClass = HeraProjectGroupServiceApi.class, group = "${dubbo.group}")
public class HeraPojectGroupServiceImpl implements HeraProjectGroupServiceApi {

    @Autowired
    HeraProjectGroupService projectGroupService;

    @Override
    public Result<ProjectGroupTreeNode> getFullTree(Integer type) {
        return projectGroupService.getFullTree(type);
    }

    @Override
    public Result<ProjectGroupTreeNode> getTreeByUser(String user, Integer type, String projectGroupName,Integer level) {
        return projectGroupService.getTreeByUser(user,type,projectGroupName,level);
    }

    @Override
    public Result<List<HeraAppBaseInfoModel>> searchGroupApps(String user, Integer groupType,Integer projectGroupId, String appName, Integer page, Integer pageSize) {
        return projectGroupService.searchGroupApps(user,groupType,projectGroupId,appName,page,pageSize);
    }

    @Override
    public Result create(HeraProjectGroupDataRequest request) {
        return projectGroupService.create(request);
    }

    @Override
    public Result update(HeraProjectGroupDataRequest request) {
        return projectGroupService.update(request);
    }

    @Override
    public Result delete(Integer id) {
        return projectGroupService.delete(id);
    }

    @Override
    public Result<List<HeraProjectGroupModel>> searchChildGroups(String user, Integer groupType, Integer projectGroupId, Integer page, Integer pageSize){
        return projectGroupService.searchChildGroups(user,groupType,projectGroupId,page,pageSize);
    }

}
