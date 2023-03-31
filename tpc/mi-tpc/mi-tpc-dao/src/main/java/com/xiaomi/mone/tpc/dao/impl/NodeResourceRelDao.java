package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeResourceRelEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class NodeResourceRelDao extends BaseDao{

    public NodeResourceRelEntity getOneByResourceId(Long resourceId) {
            SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("resource_id", resourceId);
            List<NodeResourceRelEntity> nodeResourceRelEntities = query(sqlExpr, NodeResourceRelEntity.class, 1);
            if (CollectionUtils.isEmpty(nodeResourceRelEntities)) {
                return null;
            }
            return nodeResourceRelEntities.get(0);
    }

    public NodeResourceRelEntity getOneByNodeId(Long nodeId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id", nodeId);
        List<NodeResourceRelEntity> nodeResourceRelEntities = query(sqlExpr, NodeResourceRelEntity.class, 1);
        if (CollectionUtils.isEmpty(nodeResourceRelEntities)) {
            return null;
        }
        return nodeResourceRelEntities.get(0);
    }

    public NodeResourceRelEntity getOneByNodeIdAndResourceId(Long nodeId, Long resourceId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("node_id", nodeId)
                .andEquals("resource_id", resourceId);
        return fetch(sqlExpr, NodeResourceRelEntity.class);
    }

    public List<NodeResourceRelEntity> getRelationsByNodeId(Long nodeId){
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id",nodeId);
        return query(sqlExpr, NodeResourceRelEntity.class);
    }

    public boolean deleteByNodeIdAndResourceId(Long nodeId, Long resourceId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("node_id",nodeId)
                .andEquals("resource_id",resourceId);
        return super.delete(sqlExpr,NodeResourceRelEntity.class);
    }

    public boolean deleteByNodeId(Long nodeId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("node_id",nodeId);
        return super.delete(sqlExpr,NodeResourceRelEntity.class);
    }

}
