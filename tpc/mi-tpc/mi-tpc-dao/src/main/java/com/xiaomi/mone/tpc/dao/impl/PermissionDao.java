package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.PermissionEntity;
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
public class PermissionDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public boolean insert(BaseEntity entity) {
        PermissionEntity permissionEntity = (PermissionEntity)entity;
        boolean result = super.insert(permissionEntity);
        if (result) {
            Key key = Key.build(ModuleEnum.PERMISSION_PATH).keys(permissionEntity.getSystemId(), permissionEntity.getPath());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean deleteById(BaseEntity entity) {
        PermissionEntity permissionEntity = (PermissionEntity)entity;
        boolean result = super.deleteById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.PERMISSION_PATH).keys(permissionEntity.getSystemId(), permissionEntity.getPath());
            cache.get().delete(key);
        }
        return result;
    }

    public boolean updateById(String oldPath, PermissionEntity entity) {
        boolean result = super.updateById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.PERMISSION_PATH).keys(entity.getSystemId(), oldPath);
            cache.get().delete(key);
        }
        return result;
    }

    public List<PermissionEntity> getListByPage(Long systemId, String name, String path, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (systemId != null) {
            sqlExpr = sqlExpr.andEquals("system_id", systemId);
        }
        if (StringUtils.isNotBlank(name)) {
            sqlExpr = sqlExpr.andLike("permission_name", name);
        }
        if (StringUtils.isNotBlank(path)) {
            sqlExpr = sqlExpr.andLike("path", path);
        }
        return getListByPage(sqlExpr, pageData, PermissionEntity.class);
    }

    public PermissionEntity getOneBySystemIdAndPath(Long systemId, String path) {
        //缓存获取
        Key key = Key.build(ModuleEnum.PERMISSION_PATH).keys(systemId, path);
        PermissionEntity permissionEntity = cache.get().get(key, PermissionEntity.class);
        if (permissionEntity != null) {
            if (permissionEntity.getId() == null) {
                return null;
            }
            return permissionEntity;
        }
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("system_id", systemId)
                .andEquals("path", path);
        permissionEntity = fetch(sqlExpr, PermissionEntity.class);
        if (permissionEntity != null) {
            cache.get().set(key, permissionEntity);
        } else {
            cache.get().set(key, new PermissionEntity());
        }
        return permissionEntity;
    }

    public PermissionEntity getOneBySystemId(Long systemId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("system_id", systemId);
        List<PermissionEntity> permissionEntitys = query(sqlExpr, PermissionEntity.class, 1);
        return CollectionUtils.isEmpty(permissionEntitys) ? null : permissionEntitys.get(0);
    }

    public List<PermissionEntity> getListBySystemIdAndIds(Long systemId, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        long[] idArr = ids.stream().mapToLong(Long::longValue).toArray();
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("system_id", systemId)
                .andIn("id", idArr);
        return query(sqlExpr, PermissionEntity.class);
    }

}
