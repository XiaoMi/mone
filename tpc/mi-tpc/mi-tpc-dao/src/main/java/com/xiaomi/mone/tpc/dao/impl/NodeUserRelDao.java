package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class NodeUserRelDao extends BaseDao{

    /**
     * 通过用户ID和节点类型获取节点成员信息
     * @param userId
     * @param relType
     * @return
     */
    public NodeUserRelEntity getOneByUserIdAndNodeType(long userId, Integer nodeType, Integer relType) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().
                andEquals("user_id", userId).
                andEquals("node_type",nodeType);
        if (relType != null) {
            sqlExpr = sqlExpr.andEquals("type", relType);
        }
        return fetch(sqlExpr, NodeUserRelEntity.class);
    }

    /**
     * 获取我是管理员的最大等级的节点
     * @param userId
     * @return
     */
    public NodeUserRelEntity getMaxLevelNode(long userId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().
                andEquals("user_id", userId).
                andEquals("deleted",0);
        List<NodeUserRelEntity> list = dao.query(NodeUserRelEntity.class, Cnd.where(sqlExpr).asc("node_type").desc("create_time"), new Pager(1, 1));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public NodeUserRelEntity getOneByNodeIdAndUserId(long nodeId, long userId, Integer relType) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().
                andEquals("node_id", nodeId).
                andEquals("user_id", userId);
        if (relType != null) {
            sqlExpr= sqlExpr.andEquals("type", relType);
        }
        return fetch(sqlExpr, NodeUserRelEntity.class);
    }

    public List<NodeUserRelEntity> getListByNodeId(long nodeId, Integer relType) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().
                andEquals("node_id", nodeId).andEquals("type", relType);
        return query(sqlExpr, NodeUserRelEntity.class);
    }

    public List<NodeUserRelEntity> getListByNodeId(long nodeId, Integer relType, int limit) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id", nodeId);
        if (relType != null) {
            sqlExpr = sqlExpr.andEquals("type", relType);
        }
        return query(sqlExpr, NodeUserRelEntity.class, limit);
    }

    /**
     * @param nodeTypes
     * @param userId
     * @param relType
     * @return
     */
    public List<NodeUserRelEntity> getOneByNodeTypeAndUserId(List<Integer> nodeTypes, long userId, int relType) {
        long[] nodeTypeArr = nodeTypes.stream().mapToLong(e -> e.longValue()).toArray();
        return query(Cnd.cri().where().
                andEquals("type", relType).
                andIn("node_type", nodeTypeArr).
                andEquals("user_id", userId), NodeUserRelEntity.class);
    }

    /**
     * @param nodeType
     * @param userId
     * @param relType
     * @return
     */
    public NodeUserRelEntity getOneByNodeTypeAndUserId(Integer nodeType, long userId, int relType) {
        return fetch(Cnd.cri().where().
                andEquals("type", relType).
                andEquals("node_type", nodeType).
                andEquals("user_id", userId), NodeUserRelEntity.class);
    }

    /**
     * @param nodeId
     * @return
     */
    public NodeUserRelEntity getOneByNodeId(Long nodeId) {
        List<NodeUserRelEntity> nodeUserRelEntities = query(Cnd.cri().where().
                andEquals("node_id", nodeId), NodeUserRelEntity.class, 1);
        if (CollectionUtils.isEmpty(nodeUserRelEntities)) {
            return null;
        }
        return nodeUserRelEntities.get(0);
    }

    /**
     * @param nodeId
     * @return
     */
    public boolean deleteByNodeId(Long nodeId) {
        return delete(Cnd.cri().where().
                andEquals("node_id", nodeId), NodeUserRelEntity.class);
    }

    /**
     * @param nodeIds
     * @return
     */
    public boolean deleteByNodeIdsAndUserId(Collection<Long> nodeIds, Long userId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andInList("node_id", new ArrayList<>(nodeIds)).andEquals("user_id", userId);
        return delete(sqlExpr, NodeUserRelEntity.class);
    }


    /**
     * 仅仅获取一个，用来判断是否存在和nodeIds存在关系
     * @param nodeIds
     * @param userId
     * @param relType
     * @return
     */
    public NodeUserRelEntity getOneByNodeIdsAndUserId(Collection<Long> nodeIds, long userId, Integer relType) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return null;
        }
        long[] nodeIdArr = nodeIds.stream().mapToLong(id -> id.longValue()).toArray();
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIn("node_id", nodeIdArr).andEquals("user_id", userId);
        if (relType != null) {
            sqlExpr = sqlExpr.andEquals("type", relType);
        }
        List<NodeUserRelEntity> list = query(sqlExpr, NodeUserRelEntity.class, 1);
        if ( CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * nodeIds存在关系列表
     * @param nodeIds
     * @param userId
     * @param relType
     * @return
     */
    public List<NodeUserRelEntity> getByNodeIdsAndUserId(Collection<Long> nodeIds, long userId, Integer relType) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return null;
        }
        long[] nodeIdArr = nodeIds.stream().mapToLong(id -> id.longValue()).toArray();
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIn("node_id", nodeIdArr).andEquals("user_id", userId);
        if (relType != null) {
            sqlExpr = sqlExpr.andEquals("type", relType);
        }
        return query(sqlExpr, NodeUserRelEntity.class);
    }

    public List<NodeUserRelEntity> getListByPage(Long nodeId, Integer type, Long memberId, Integer tester, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id", nodeId);
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (memberId != null) {
            sqlExpr = sqlExpr.andEquals("user_id", memberId);
        }
        if (tester != null) {
            sqlExpr = sqlExpr.andEquals("tester", tester);
        }
        return getListByPage(sqlExpr, pageData, NodeUserRelEntity.class);
    }

}
