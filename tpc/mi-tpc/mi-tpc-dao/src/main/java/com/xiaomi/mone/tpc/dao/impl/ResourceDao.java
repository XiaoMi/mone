package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.ResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class ResourceDao extends BaseDao{

    public List<ResourceEntity> getListByPage(Long poolNodeId, Long nodeId, Long applyId, Integer type, Integer region, Integer status, String key1, String resourceName, Integer envFlag, PageDataVo pageData) {
        if (nodeId == null) {
            return getPoolByPage(poolNodeId, applyId, type, region, status, key1, resourceName, envFlag, pageData);
        } else {
            return getRelResByPage(poolNodeId, nodeId, applyId, type, region, status, key1, resourceName,envFlag, pageData);
        }
    }

    private List<ResourceEntity> getRelResByPage(Long poolNodeId, Long nodeId, Long applyId, Integer type, Integer region, Integer status, String key1, String resourceName, Integer envFlag, PageDataVo pageData) {
        Map<String, Object> params = Maps.newHashMap();
        setParam(params, "poolNodeId", poolNodeId);
        setParam(params, "nodeId", nodeId);
        setParam(params, "applyId", applyId);
        setParam(params, "type", type);
        setParam(params, "region", region);
        setParam(params, "status", status);
        setParam(params, "envFlag", envFlag);
        setParam(params, "key1", key1);
        if (StringUtils.isNotBlank(resourceName)) {
            setParam(params, "resourceName", "%" + resourceName + "%");
        }
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select res.* from resource_entity res, node_resource_rel_entity rel where rel.resource_id=res.id and res.deleted=0 and rel.deleted=0");
        sqlExpr.append(" and rel.node_id=@nodeId");
        if (poolNodeId != null) {
            sqlExpr.append(" and res.pool_node_id=@poolNodeId");
        }
        if (applyId != null) {
            sqlExpr.append(" and res.apply_id=@applyId");
        }
        if (type != null) {
            sqlExpr.append(" and res.type=@type");
        }
        if (envFlag != null) {
            sqlExpr.append(" and res.env_flag=@envFlag");
        }
        if (status != null) {
            sqlExpr.append(" and res.region=@region");
        }
        if (status != null) {
            sqlExpr.append(" and res.status=@status");
        }
        if (StringUtils.isNotBlank(key1)) {
            sqlExpr.append(" and res.key1=@key1");
        }
        if (StringUtils.isNotBlank(resourceName)) {
            sqlExpr.append(" and res.resource_name like @resourceName");
        }
        sqlExpr.append(" order by create_time desc");
        return getListByPage(sqlExpr, params, pageData, ResourceEntity.class);
    }

    private List<ResourceEntity> getPoolByPage(Long poolNodeId, Long applyId, Integer type, Integer region, Integer status, String key1, String resourceName, Integer envFlag, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (poolNodeId != null) {
            sqlExpr = sqlExpr.andEquals("pool_node_id", poolNodeId);
        }
        if (applyId != null) {
            sqlExpr = sqlExpr.andEquals("apply_id", applyId);
        }
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (region != null) {
            sqlExpr = sqlExpr.andEquals("region", region);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(key1)) {
            sqlExpr = sqlExpr.andEquals("key1", key1);
        }
        if (StringUtils.isNotBlank(resourceName)) {
            sqlExpr = sqlExpr.andLike("resource_name", resourceName);
        }
        if (envFlag != null) {
            sqlExpr = sqlExpr.andEquals("env_flag", envFlag);
        }
        return getListByPage(sqlExpr, pageData, ResourceEntity.class);
    }

    public List<ResourceEntity> getPoolByPage(List<Long> poolNodeIds, Integer type, Integer region, Integer status, String key1, String resourceName, Integer envFlag, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        sqlExpr = sqlExpr.andInList("pool_node_id",poolNodeIds);
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (region != null) {
            sqlExpr = sqlExpr.andEquals("region", region);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(key1)) {
            sqlExpr = sqlExpr.andEquals("key1", key1);
        }
        if (StringUtils.isNotBlank(resourceName)) {
            sqlExpr = sqlExpr.andLike("resource_name", resourceName);
        }
        if (envFlag != null) {
            sqlExpr = sqlExpr.andEquals("env_flag", envFlag);
        }
        return getListByPage(sqlExpr, pageData, ResourceEntity.class);
    }

    public ResourceEntity getOneByPoolNodeId(Long poolNodeId) {
        List<ResourceEntity> resourceEntities = query(Cnd.cri().where().andEquals("pool_node_id", poolNodeId), ResourceEntity.class, 1);
        if (CollectionUtils.isEmpty(resourceEntities)) {
            return null;
        }
        return resourceEntities.get(0);
    }

    public List<ResourceEntity> getResourceOrderByType(List<Integer> resourceIds) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (resourceIds.size() > 0) {
            sqlExpr = sqlExpr.andInIntList("id", resourceIds);
        }
        sqlExpr = sqlExpr.andEquals("status", 0);
        sqlExpr = sqlExpr.andEquals("deleted",0);
        Cnd cnd = Cnd.where(sqlExpr);
        List<ResourceEntity> res = dao.query(ResourceEntity.class, cnd.desc("create_time"));
        return res;
    }

}
