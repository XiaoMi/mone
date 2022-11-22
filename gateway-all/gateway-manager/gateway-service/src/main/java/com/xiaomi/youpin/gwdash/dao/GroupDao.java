package com.xiaomi.youpin.gwdash.dao;

import com.xiaomi.youpin.gwdash.bo.GroupInfoEntity;
import com.xiaomi.youpin.gwdash.common.GroupInfoConsts;
import com.xiaomi.youpin.gwdash.dao.model.GroupInfo;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GroupDao {

    @Autowired
    private Dao dao;

    @Autowired
    private TenantComponent tenementComponent;


    public int addGroup(GroupInfo groupInfo) {
        if (groupInfo == null) {
            return -1;
        }
        if (StringUtils.isEmpty(groupInfo.getName()) || StringUtils.isEmpty(groupInfo.getDescription())) {
            return -1;
        }
        Date now = new Date();
        groupInfo.setCreationDate(now);
        groupInfo.setModifyDate(now);
        groupInfo.setTenement(this.tenementComponent.getTenement());
        try {
            dao.insert(groupInfo);
        } catch (Exception e) {
            log.info(e.toString());
            return -1;
        }
        return groupInfo.getId();
    }

    public GroupInfo queryGroupById(Integer id) {
        if (id == null) {
            return null;
        }
        Cnd condition = Cnd.where("id", "=", id);
        return dao.fetch(GroupInfo.class, condition);
    }


    public boolean updateGroup(GroupInfo group) {
        if (group == null) {
            return false;
        }

        Date now = new Date();
        group.setModifyDate(now);
        int count = dao.update(group);
        if (count == 1) {
            return true;
        }
        return false;
    }

    public List<GroupInfo> getGroups() {
        List<GroupInfo> list = dao.query(GroupInfo.class,
                Cnd.where("status", "=", GroupInfoConsts.STATUS_VALID)
                        .and("tenement","=",this.tenementComponent.getTenement())
        );
        return list;
    }

    public int getTotalAmount() {
        return dao.count(GroupInfo.class, Cnd.where("tenement", "=", this.tenementComponent.getTenement()));
    }

    public List<GroupInfo> getGroupsByPage(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize < 0) {
            return null;
        }
        Cnd condition = Cnd.where("tenement","=",this.tenementComponent.getTenement()).limit(pageNumber, pageSize);
        return dao.query(GroupInfo.class, condition);
    }

    public Boolean deleteGroup(GroupInfo groupInfo) {
        int count = dao.update(groupInfo);
        return count > 0;
    }

    /**
     * gidStr 1_2_3 gid按照_分割
     *
     * @param gidStr
     * @return
     */
    public List<GroupInfoEntity> getGroupInfoByGids(String gidStr) {
        if (gidStr == null || gidStr.equals("")) {
            return new ArrayList<>();
        }
        List<String> gids = Arrays.asList(gidStr.split("_"));
        if (gids.size() == 0) {
            return new ArrayList<>();
        }

        log.info("getGroupInfoByGids gidStr:[{}], gids:[{}]", gidStr, gids);

        if (gids == null || gids.size() == 0) {
            return new ArrayList<>();
        }

        List<GroupInfoEntity> gidInfos = gids.stream().map(it2 -> {
            GroupInfo groupInfo = this.queryGroupById(Integer.valueOf(it2));
            GroupInfoEntity group = new GroupInfoEntity();
            if (groupInfo == null) {
                return group;
            }
            BeanUtils.copyProperties(groupInfo, group);
            return group;
        }).collect(Collectors.toList());

        log.debug("getGroupInfoByGids gidInfos:[{}]", gidInfos);
        return gidInfos;
    }

    public List<GroupInfo> getGroupByName(String groupName) {
        return dao.query(GroupInfo.class, Cnd.where("status", "=", "0")
                        .and("tenement","=",this.tenementComponent.getTenement())
                .and("name", "=", groupName));
    }
}
