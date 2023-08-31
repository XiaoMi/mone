package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupRelEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class UserGroupRelDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public boolean insert(BaseEntity entity) {
        UserGroupRelEntity userGroupRelEntity = (UserGroupRelEntity)entity;
        boolean result = super.insert(userGroupRelEntity);
        if (result) {
            Key bigKey = Key.build(ModuleEnum.USER_GROUP_REL);
            Key key = Key.build(ModuleEnum.USER_GROUP_REL_LIST_UID).keys(userGroupRelEntity.getUserId());
            cache.get().delete(bigKey, key);
        }
        return result;
    }

    @Override
    public boolean deleteById(BaseEntity entity) {
        UserGroupRelEntity userGroupRelEntity = (UserGroupRelEntity)entity;
        boolean result = super.deleteById(entity);
        if (result) {
            Key bigKey = Key.build(ModuleEnum.USER_GROUP_REL);
            Key key = Key.build(ModuleEnum.USER_GROUP_REL_LIST_UID).keys(userGroupRelEntity.getUserId());
            cache.get().delete(bigKey, key);
        }
        return result;
    }

    public List<UserGroupRelEntity> getListByPage(Long groupId, Long memberId, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("group_id", groupId);
        if (memberId != null) {
            sqlExpr = sqlExpr.andEquals("user_id", memberId);
        }
        return getListByPage(sqlExpr, pageData, UserGroupRelEntity.class);
    }

    public boolean deleteByGroupId(Long groupId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("group_id", groupId);
        boolean result = delete(sqlExpr, UserGroupRelEntity.class);
        if (result) {
            Key bigKey = Key.build(ModuleEnum.USER_GROUP_REL);
            cache.get().delete(bigKey);
        }
        return true;
    }

    public UserGroupRelEntity getByGroupIdAndUserId(Long groupId, Long userId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("group_id", groupId)
                .andEquals("user_id", userId);
        return fetch(sqlExpr, UserGroupRelEntity.class);
    }

    public List<UserGroupRelEntity> getListByUserId(Long userId) {
        //缓存获取
        Key bigKey = Key.build(ModuleEnum.USER_GROUP_REL);
        Key key = Key.build(ModuleEnum.USER_GROUP_REL_LIST_UID).keys(userId);
        List<UserGroupRelEntity> userGroupRelEntities = cache.get().gets(bigKey, key, UserGroupRelEntity.class);
        if (userGroupRelEntities != null) {
            return userGroupRelEntities;
        }
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("user_id", userId);
        userGroupRelEntities =  query(sqlExpr, UserGroupRelEntity.class);
        if (userGroupRelEntities != null) {
            cache.get().set(bigKey, key, userGroupRelEntities);
        } else {
            // 防穿透
            cache.get().set(bigKey, key, Lists.newArrayList());
        }
        return userGroupRelEntities;
    }

}
