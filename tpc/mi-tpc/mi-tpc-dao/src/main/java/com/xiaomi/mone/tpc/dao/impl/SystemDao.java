package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class SystemDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public <T extends BaseEntity> boolean deleteById(BaseEntity entity) {
        SystemEntity systemEntity = (SystemEntity)entity;
        boolean result = super.deleteById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.SYSTEM_NAME).keys(systemEntity.getSystemName());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean insert(BaseEntity entity) {
        SystemEntity systemEntity = (SystemEntity)entity;
        boolean result = super.insert(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.SYSTEM_NAME).keys(systemEntity.getSystemName());
            cache.get().delete(key);
        }
        return result;
    }

    public boolean updateById(String oldSystemName, SystemEntity entity) {
        boolean result = super.updateById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.SYSTEM_NAME).keys(oldSystemName);
            cache.get().delete(key);
        }
        return result;
    }

    public SystemEntity getOneByName(String systemName) {
        //缓存获取
        Key key = Key.build(ModuleEnum.SYSTEM_NAME).keys(systemName);
        SystemEntity systemEntity = cache.get().get(key, SystemEntity.class);
        if (systemEntity != null) {
            if (systemEntity.getId() == null) {
                return null;
            }
            return systemEntity;
        }
        systemEntity = fetch(Cnd.cri().where().andEquals("system_name", systemName), SystemEntity.class);
        if (systemEntity != null) {
            //缓存存储
            cache.get().set(key, systemEntity);
        } else {
            cache.get().set(key, new SystemEntity());
        }
        return systemEntity;
    }

    public List<SystemEntity> getListByPage(String systemName, Integer status, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(systemName)) {
            sqlExpr = sqlExpr.andLike("system_name", systemName);
        }
        return getListByPage(sqlExpr, pageData, SystemEntity.class);
    }

    public List<SystemEntity> getListByPage(Long userId, String systemName, Integer status, PageDataVo pageData) {
        Map<String,Object> param = Maps.newHashMap();
        setParam(param, "userId", userId.toString());
        setParam(param, "systemName", "%" + systemName + "%");
        setParam(param, "status", status);
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select system.* from system_entity system ,flag_entity flag ");
        sqlExpr.append("where system.deleted=0 and flag.deleted=0 and flag.type=4 and system.id=flag.parent_id ");
        sqlExpr.append("and flag.flag_key=@userId ");
        if (status != null) {
            sqlExpr.append("and system.status=@status ");
        }
        if (StringUtils.isNotBlank(systemName)) {
            sqlExpr.append("and system.system_name like @systemName ");
        }
        sqlExpr.append("order by system.create_time desc");
        return getListByPage(sqlExpr, param, pageData, SystemEntity.class);
    }


}
