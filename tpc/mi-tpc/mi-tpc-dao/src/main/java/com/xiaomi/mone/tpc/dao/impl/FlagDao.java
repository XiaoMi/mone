package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
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
public class FlagDao extends BaseDao{

    public List<FlagEntity> getListByPage(Long parentId, Integer type, String flagName, String flagKey, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("parent_id", parentId)
                .andEquals("type", type);
        if (StringUtils.isNotBlank(flagName)) {
            sqlExpr = sqlExpr.andLike("flag_name", flagName);
        }
        if (StringUtils.isNotBlank(flagKey)) {
            sqlExpr = sqlExpr.andLike("flag_key", flagKey);
        }
        return getListByPage(sqlExpr, pageData, FlagEntity.class);
    }

    public List<FlagEntity> getListByNodeId(Long nodeId, Integer type) {
        return query(Cnd.cri().where().andEquals("parent_id", nodeId)
                .andEquals("type", type), FlagEntity.class);
    }

    public List<FlagEntity> getListByNodeIds(Collection<Long> nodeIds, Integer type) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return null;
        }
        return query(Cnd.cri().where().andInList("parent_id", new ArrayList<>(nodeIds))
                .andEquals("type", type), FlagEntity.class, 1000);
    }

    public boolean deleteByNodeId(Long nodeId, Integer type) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("parent_id", nodeId);
        if (type != null) {
            sqlExpr=sqlExpr.andEquals("type", type);
        }
        return delete(sqlExpr, FlagEntity.class);
    }

    public FlagEntity getOneByFlagKey(Long nodeId, Integer type, String flagKey) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("parent_id", nodeId)
                .andEquals("type", type)
                .andEquals("flag_key", flagKey);
        List<FlagEntity> flagEntities = query(sqlExpr, FlagEntity.class, 1);
        if (CollectionUtils.isEmpty(flagEntities)) {
            return null;
        }
        return flagEntities.get(0);
    }

    public FlagEntity getOneByFlagNameAndkey(Long nodeId, Integer type, String flagName, String flagKey) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("parent_id", nodeId)
                .andEquals("type", type)
                .andEquals("flag_name", flagName)
                .andEquals("flag_key", flagKey);
        List<FlagEntity> flagEntities = query(sqlExpr, FlagEntity.class, 1);
        if (CollectionUtils.isEmpty(flagEntities)) {
            return null;
        }
        return flagEntities.get(0);
    }

}
