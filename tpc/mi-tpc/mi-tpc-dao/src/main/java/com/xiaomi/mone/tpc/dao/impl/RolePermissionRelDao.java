package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.dao.entity.RolePermissionRelEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
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
public class RolePermissionRelDao extends BaseDao{

    @Autowired
    private Cache cache;

    public boolean batchInsertWithExcp(List<RolePermissionRelEntity> entities) {
        boolean result = super.batchInsertWithException(entities);
        if (result && !CollectionUtils.isEmpty(entities)) {
            Key key = Key.build(ModuleEnum.ROLE_PERMISSION_REL_LIST_RID).keys(entities.get(0).getRoleId());
            cache.get().delete(key);
        }
        return result;
    }

    public boolean deleteByRoleId(Long roleId) {
        boolean result = super.delete(Cnd.cri().where().andEquals("role_id",roleId), RolePermissionRelEntity.class);
        if (result) {
            Key key = Key.build(ModuleEnum.ROLE_PERMISSION_REL_LIST_RID).keys(roleId);
            cache.get().delete(key);
        }
        return result;
    }

    public List<RolePermissionRelEntity> getListByPermissionId(Long permissionId) {
        return query(Cnd.cri().where().andEquals("permission_id",permissionId), RolePermissionRelEntity.class);
    }

    public boolean deleteByPermissionId(Long permissionId) {
        List<RolePermissionRelEntity> rolePermissionRelEntities = getListByPermissionId(permissionId);
        boolean result =  super.delete(Cnd.cri().where().andEquals("permission_id",permissionId), RolePermissionRelEntity.class);
        if (result && !CollectionUtils.isEmpty(rolePermissionRelEntities)) {
            List<Key> keys = Lists.newArrayList();
            rolePermissionRelEntities.stream().forEach(e -> {
                Key key = Key.build(ModuleEnum.ROLE_PERMISSION_REL_LIST_RID).keys(e.getRoleId());
                keys.add(key);
            });
            cache.get().delete(keys);
        }
        return true;
    }

    public List<RolePermissionRelEntity> getListByRoleId(Long roleId) {
        Key key = Key.build(ModuleEnum.ROLE_PERMISSION_REL_LIST_RID).keys(roleId);
        List<RolePermissionRelEntity> rolePermissionRelEntities = cache.get().gets(key, RolePermissionRelEntity.class);
        if (rolePermissionRelEntities != null) {
            return rolePermissionRelEntities;
        }
        rolePermissionRelEntities = query(Cnd.cri().where().andEquals("role_id", roleId), RolePermissionRelEntity.class, 500);
        if (rolePermissionRelEntities != null) {
            cache.get().set(key, rolePermissionRelEntities);
        } else {
            cache.get().set(key, Lists.newArrayList());
        }
        return rolePermissionRelEntities;
    }

}
