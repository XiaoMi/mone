package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.common.enums.NodeUserRoleRelTypeEnum;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.UserNodeRoleRelEntity;
import lombok.extern.slf4j.Slf4j;
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
public class UserNodeRoleRelDao extends BaseDao{

    @Autowired
    private Cache cache;

    public UserNodeRoleRelEntity getOneByMemberId(Long memberId, Integer type) {
        List<UserNodeRoleRelEntity> entityList = query(Cnd.cri().where().andEquals("user_id", memberId).
                andEquals("type", type), UserNodeRoleRelEntity.class, 1);
        if (CollectionUtils.isEmpty(entityList)) {
            return null;
        }
        return entityList.get(0);
    }

    public List<UserNodeRoleRelEntity> getListByNodeIdAndMemberIdAndRoleIds(Long nodeId, Long memberId, Integer type, Collection<Long>roleIds) {
        long[] roleIdArr = roleIds.stream().mapToLong(Long::longValue).toArray();
        List<UserNodeRoleRelEntity> entityList = query(Cnd.cri().where().
                andEquals("node_id", nodeId).
                andEquals("user_id", memberId).
                andEquals("type", type).andIn("role_id", roleIdArr), UserNodeRoleRelEntity.class, 1);
        return entityList;
    }

    public UserNodeRoleRelEntity getOneByNodeId(Long nodeId) {
        List<UserNodeRoleRelEntity> entityList = query(Cnd.cri().where().andEquals("node_id", nodeId), UserNodeRoleRelEntity.class, 1);
        if (CollectionUtils.isEmpty(entityList)) {
            return null;
        }
        return entityList.get(0);
    }

    public boolean deleteByRoleId(Long roleId) {
        return delete(Cnd.cri().where().andEquals("role_id",roleId), UserNodeRoleRelEntity.class);
    }

    public boolean deleteByNodeId(Long nodeId) {
        return delete(Cnd.cri().where().andEquals("node_id",nodeId), UserNodeRoleRelEntity.class);
    }

    public List<UserNodeRoleRelEntity> getListByPage(Long systemId, Long nodeId, Long roleId, Integer type, Long memberId, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id", nodeId);
        if (systemId != null) {
            sqlExpr = sqlExpr.andEquals("system_id", systemId);
        }
        if (roleId != null) {
            sqlExpr = sqlExpr.andEquals("role_id", roleId);
        }
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (memberId != null) {
            sqlExpr = sqlExpr.andEquals("user_id", memberId);
        }
        return getListByPage(sqlExpr, pageData, UserNodeRoleRelEntity.class);
    }

    /**
     * 这个条件很难做缓存
     * @param systemId
     * @param nodeIds
     * @param userId
     * @param groupIds
     * @return
     */
    public List<UserNodeRoleRelEntity> getListByUserIdsAndGroupIds(Long systemId, List<Long> nodeIds, Long userId, List<Long> groupIds) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("system_id", systemId).andInList("node_id", nodeIds);
        SqlExpressionGroup userSqlExpr = Cnd.cri().where().andEquals("user_id", userId).andEquals("type", NodeUserRoleRelTypeEnum.USER.getCode());
        if (!CollectionUtils.isEmpty(groupIds)) {
            SqlExpressionGroup groupSqlExpr = Cnd.cri().where().andInList("user_id", groupIds).andEquals("type", NodeUserRoleRelTypeEnum.GROUP.getCode());
            sqlExpr = sqlExpr.and(Cnd.cri().where().and(userSqlExpr).or(groupSqlExpr));
        } else {
            sqlExpr = sqlExpr.and(userSqlExpr);
        }
        return query(sqlExpr, UserNodeRoleRelEntity.class, 100);
    }

}
