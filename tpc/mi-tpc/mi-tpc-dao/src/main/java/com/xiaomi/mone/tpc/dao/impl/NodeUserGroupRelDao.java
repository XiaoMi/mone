package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.NodeUserGroupRelEntity;
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
public class NodeUserGroupRelDao extends BaseDao{

    public List<NodeUserGroupRelEntity> getListByPage(Long nodeId, Long userGroupId, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("node_id", nodeId);
        if (userGroupId != null) {
            sqlExpr = sqlExpr.andEquals("user_group_id", userGroupId);
        }
        return getListByPage(sqlExpr, pageData, NodeUserGroupRelEntity.class);
    }

    public NodeUserGroupRelEntity getOneByNodeIdAndUserGroupId(long nodeId, long userGroupId) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().
                andEquals("node_id", nodeId).
                andEquals("user_group_id", userGroupId);
        return fetch(sqlExpr, NodeUserGroupRelEntity.class);
    }

}
