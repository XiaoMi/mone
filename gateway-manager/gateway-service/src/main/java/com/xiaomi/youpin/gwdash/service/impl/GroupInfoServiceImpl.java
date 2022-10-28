package com.xiaomi.youpin.gwdash.service.impl;


import com.google.common.collect.Maps;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntity;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntityDTO;
import com.xiaomi.youpin.gwdash.common.GroupInfoConsts;
import com.xiaomi.youpin.gwdash.common.GroupInfoResult;
import com.xiaomi.youpin.gwdash.dao.GroupDao;
import com.xiaomi.youpin.gwdash.dao.mapper.GWGroupInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.GroupInfo;
import com.xiaomi.youpin.gwdash.dao.model.UserInfo;
import com.xiaomi.youpin.gwdash.service.GroupInfoService;
import com.xiaomi.youpin.gwdash.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class GroupInfoServiceImpl implements GroupInfoService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserService userService;

    @Resource
    private GWGroupInfoMapper gwGroupInfoMapper;

    @Override
    public GroupInfoEntity queryGroupById(Integer id) {
        if (id == null) {
            return null;
        }
        GroupInfo groupInfo = groupDao.queryGroupById(id);
        if (groupInfo == null) {
            return null;
        }
        GroupInfoEntity group = new GroupInfoEntity();
        BeanUtils.copyProperties(groupInfo, group);
        return group;
    }

    @Override
    public int createGroup(GroupInfoEntity group) {
        GroupInfo groupInfo = new GroupInfo();
        BeanUtils.copyProperties(group,groupInfo);
        return groupDao.addGroup(groupInfo);
    }

    @Override
    public int createGroupWithId(GroupInfoEntity group) {
        GroupInfo groupInfo = new GroupInfo();
        BeanUtils.copyProperties(group,groupInfo);
        return gwGroupInfoMapper.insertWithId(groupInfo);
    }


    @Override
    public List<Map<String,String>> getOwnGroups(String userName){
        List<Map<String,String>> result = Lists.newArrayList();
        if(StringUtils.isBlank(userName)){
            return result;
        }

        List<GroupInfoEntity> entities = userService.describeOwnerGroupInfo(userName);
        if(entities == null || entities.size() == 0){
            return result;
        }

        entities.stream().forEach(t -> {
            Map<String,String> temp = Maps.newHashMap();
            temp.put("id", t.getId()+"");
            temp.put("groupName", t.getName());

            result.add(temp);
        });

        return result;
    }

    @Override
    public List<GroupInfoEntityDTO> getAllGroupDTOs() {
        List<GroupInfo> groups = groupDao.getGroups();
        if (groups == null || groups.size() == 0) {
            return null;
        }
        List<GroupInfoEntityDTO> groupList = new ArrayList<>(groups.size());
        for (GroupInfo e : groups) {
            GroupInfoEntityDTO group = new GroupInfoEntityDTO();
            BeanUtils.copyProperties(e, group);
            groupList.add(group);
        }
        return groupList;
    }


    @Override
    public List<GroupInfoEntity> getAllGroups() {
        List<GroupInfo> groups = groupDao.getGroups();
        if (groups == null || groups.size() == 0) {
            return null;
        }
        List<GroupInfoEntity> groupList = new ArrayList<>(groups.size());
        for (GroupInfo e : groups) {
            GroupInfoEntity group = new GroupInfoEntity();
            BeanUtils.copyProperties(e, group);
            groupList.add(group);
        }
        return groupList;
    }

    @Override
    public int getTotalAmount() {
        return groupDao.getTotalAmount();
    }

    @Override
    public List<GroupInfoEntity> getGroupInfoByGids(String gids) {
       return groupDao.getGroupInfoByGids(gids);
    }

    @Override
    public List<GroupInfoEntity> getGroupByPage(int pageNumber, int pageSize) {
        List<GroupInfo> groups = groupDao.getGroupsByPage(pageNumber, pageSize);
        if (groups == null || groups.size() == 0) {
            return null;
        }
        List<GroupInfoEntity> groupList = new ArrayList<>(groups.size());
        for (GroupInfo e : groups) {
            GroupInfoEntity group = new GroupInfoEntity();
            BeanUtils.copyProperties(e, group);
            groupList.add(group);
        }
        return groupList;
    }

    public Boolean updateGroup(GroupInfo group){
        GroupInfo groupInfo = groupDao.queryGroupById(group.getId());
        groupInfo.setModifyDate(new Date());
        if(group.getName()!=null){
            groupInfo.setName(group.getName());
        }
        if(group.getDescription()!=null){
            groupInfo.setDescription(group.getDescription());
        }
        boolean success=groupDao.updateGroup(groupInfo);
        return success;

    }
    public  Boolean deleteGroup(int gid){
        GroupInfo groupInfo =groupDao.queryGroupById(gid);
        groupInfo.setModifyDate(new Date());
        groupInfo.setStatus(GroupInfoConsts.STATUS_INVALID);
        boolean success=groupDao.deleteGroup(groupInfo);
       return success;
    }

    @Override
    public GroupInfoResult<Boolean> updateGroup(String username, String tenant, List<Long> gids) {
        log.info("updateGroup:{},{}", username, gids);
        if (username == null || gids == null) {
            return new GroupInfoResult(-1, "fail", false);
        }
        UserInfo user = userService.describeUserByUserName(username, tenant);
        if (user != null) {
            StringBuffer sb = new StringBuffer();
            for (Long g : gids) {
                if (sb.length() == 0) {
                    sb.append(g);
                } else {
                    sb.append("_" + g);
                }
            }

            user.setGid(sb.toString());
        }
        boolean b = userService.updateUser(user, tenant);
        if (b) {
            return new GroupInfoResult(0, "success", true);
        } else {
            return new GroupInfoResult(-1, "fail", false);
        }
    }

    @Override
    public void updateGroupInfo(GroupInfoEntity entity) {
        GroupInfo groupInfo = new GroupInfo();
        BeanUtils.copyProperties(entity,groupInfo);

        groupDao.updateGroup(groupInfo);
    }
}
