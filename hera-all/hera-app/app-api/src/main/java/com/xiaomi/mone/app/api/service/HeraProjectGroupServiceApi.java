package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupModel;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.common.Result;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/6 10:26 上午
 */
public interface HeraProjectGroupServiceApi {

    Result<ProjectGroupTreeNode> getFullTree(Integer type);

    Result<ProjectGroupTreeNode> getTreeByUser(String user,Integer type,String projectGroupName,Integer level);

    Result<List<HeraAppBaseInfoModel>> searchGroupApps(String user, Integer groupType, Integer projectGroupId, String appName,Integer page, Integer pageSize);

    Result create(HeraProjectGroupDataRequest request);

    Result update(HeraProjectGroupDataRequest request);

    Result delete(Integer id);

    Result<List<HeraProjectGroupModel>> searchChildGroups(String user, Integer groupType, Integer projectGroupId, Integer page, Integer pageSize);

}
