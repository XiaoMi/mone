package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class UserDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public boolean insert(BaseEntity entity) {
        UserEntity userEntity = (UserEntity)entity;
        boolean result = super.insert(entity);
        if (result) {
            //删除缓存
            Key key = Key.build(ModuleEnum.USER_ACC_TYPE).keys(userEntity.getAccount(), userEntity.getType());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean updateById(BaseEntity entity) {
        UserEntity userEntity = (UserEntity)entity;
        try {
            boolean result = super.updateByIdWithExcption(entity);
            if (result) {
                //删除缓存
                Key key = Key.build(ModuleEnum.USER_ACC_TYPE).keys(userEntity.getAccount(), userEntity.getType());
                cache.get().delete(key);
            }
            return result;
        } catch (Throwable e) {
            log.error("UserDao.update={}",entity, e);
            return false;
        }
    }

    public UserEntity getOneByAccount(String account, int userType) {
        //缓存获取
        Key key = Key.build(ModuleEnum.USER_ACC_TYPE).keys(account, userType);
        UserEntity userEntity = cache.get().get(key, UserEntity.class);
        if (userEntity != null) {
            if (userEntity.getId() == null) {
                return null;
            }
            return userEntity;
        }
        userEntity = fetch(Cnd.cri().where().andEquals("account",account)
                .andEquals("type", userType), UserEntity.class);
        if (userEntity != null) {
            //缓存存储
            cache.get().set(key, userEntity);
        } else {
            cache.get().set(key, new UserEntity());
        }
        return userEntity;
    }

    public List<UserEntity> getListByPage(String account, Integer type, Integer status, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(account)) {
            SqlExpressionGroup subSqlExpr = new SqlExpressionGroup();
            subSqlExpr = subSqlExpr.orLike("account", account);
            subSqlExpr = subSqlExpr.orLike("json_value(content,'$.name')", account);
            sqlExpr.and(subSqlExpr);
        }
        return getListByPage(sqlExpr, pageData, UserEntity.class);
    }

}
