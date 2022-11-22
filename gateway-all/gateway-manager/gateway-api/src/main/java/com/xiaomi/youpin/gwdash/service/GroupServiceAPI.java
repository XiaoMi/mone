package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.bo.openApi.GwUser;

import java.util.List;

/**
 * @author jiangzheng3
 * @version 1.0
 * @description: 分组相关API
 * @date 2022/2/28 16:58
 */
public interface GroupServiceAPI {

    GwUser describeUserByName(String username);

    ApiGroupInfoListResultDTO describeGroupByName(List<GroupInfoEntityDTO> gids);

    ApiGroupInfoListDTO describeGroups(int pageNo, int pageSize);

    ApiGroupInfoListResultDTO describeGroupAll(List<GroupInfoEntityDTO> gids);

    ApiGroupInfoDTO describeGroupById(int gid);

    List<ApiGroupInfoDTO> describeGroupsByIds(List<Integer> gids);

    List<ApiGroupInfoDTO> describeGroupsByApiIds(List<Integer> apiIds);

    ApiGroupInfoDTO getApiGroupByBaseUrl(String urlPrefix);
}
