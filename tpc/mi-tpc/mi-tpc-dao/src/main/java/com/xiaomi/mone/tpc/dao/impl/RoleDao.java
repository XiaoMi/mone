package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class RoleDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public boolean updateById(BaseEntity entity) {
        boolean result = super.updateById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.ROLE).keys(entity.getId());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean deleteById(BaseEntity entity) {
        RoleEntity roleEntity = (RoleEntity)entity;
        boolean result = super.deleteById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.ROLE).keys(entity.getId());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean updateByIdWithExcption(BaseEntity entity) {
        RoleEntity roleEntity = (RoleEntity)entity;
        boolean result = super.updateByIdWithExcption(roleEntity);
        if (result) {
            Key key = Key.build(ModuleEnum.ROLE).keys(entity.getId());
            cache.get().delete(key);
        }
        return true;
    }

    public RoleEntity getById(Long id) {
        if (id == null) {
            return null;
        }
        //缓存获取
        Key key = Key.build(ModuleEnum.ROLE).keys(id);
        RoleEntity roleEntity = cache.get().get(key, RoleEntity.class);
        if (roleEntity != null) {
            return roleEntity;
        }
        roleEntity = super.getById(id, RoleEntity.class);
        if (roleEntity != null) {
            cache.get().set(key, roleEntity);
        }
        return roleEntity;
    }

    public List<RoleEntity> getByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<Long> newIds = Lists.newArrayList();
        List<RoleEntity> roleEntities = Lists.newArrayList();
        for (Long id : ids) {
            //缓存获取
            Key key = Key.build(ModuleEnum.ROLE).keys(id);
            RoleEntity roleEntity = cache.get().get(key, RoleEntity.class);
            if (roleEntity != null) {
                roleEntities.add(roleEntity);
                continue;
            }
            newIds.add(id);
        }
        if (newIds.isEmpty()) {
            return roleEntities;
        }
        List<RoleEntity> rList = super.getByIds(newIds, RoleEntity.class);
        if (!CollectionUtils.isEmpty(rList)) {
            roleEntities.addAll(rList);
            for (RoleEntity roleEntity : rList) {
                Key key = Key.build(ModuleEnum.ROLE).keys(roleEntity.getId());
                cache.get().set(key, roleEntity);
            }
        }
        return roleEntities;
    }

    public List<RoleEntity> getListByPage(List<Long> nodeIds, Long systemId, String name, Integer status, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (!CollectionUtils.isEmpty(nodeIds)) {
            sqlExpr = sqlExpr.andInList("node_id", nodeIds);
        }
        if (systemId != null) {
            sqlExpr = sqlExpr.andEquals("system_id", systemId);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(name)) {
            sqlExpr = sqlExpr.andLike("role_name", name);
        }
        return getListByPage(sqlExpr, pageData, RoleEntity.class);
    }

    public RoleEntity getOneBySystemId(Long systemId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("system_id", systemId);
        List<RoleEntity> roleEntities = query(sqlExpr, RoleEntity.class, 1);
        return CollectionUtils.isEmpty(roleEntities) ? null : roleEntities.get(0);
    }

}
