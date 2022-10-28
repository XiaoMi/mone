package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.GroupInfoEntity;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntityDTO;
import com.xiaomi.youpin.gwdash.common.GroupInfoResult;

import java.util.List;
import java.util.Map;

public interface GroupInfoService {
    GroupInfoEntity queryGroupById(Integer id);

    int createGroup(GroupInfoEntity group);

    List<GroupInfoEntity> getAllGroups();

    List<GroupInfoEntityDTO> getAllGroupDTOs();

    int getTotalAmount();

    List<GroupInfoEntity> getGroupByPage(int offset, int pageSize);

    List<GroupInfoEntity>  getGroupInfoByGids(String gids);

    GroupInfoResult<Boolean> updateGroup(String username, String tenant, List<Long> gids);

    void updateGroupInfo(GroupInfoEntity entity);

    int createGroupWithId(GroupInfoEntity group);

    List<Map<String,String>> getOwnGroups(String userName);

}
