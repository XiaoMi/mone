package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class NodeDao extends BaseDao{

    @Autowired
    private Cache cache;

    @Override
    public boolean deleteById(BaseEntity entity) {
        NodeEntity nodeEntity = (NodeEntity)entity;
        boolean result = super.deleteById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.NODE).keys(nodeEntity.getId());
            cache.get().delete(key);
        }
        return result;
    }

    @Override
    public boolean updateById(BaseEntity entity) {
        boolean result = super.updateById(entity);
        if (result) {
            Key key = Key.build(ModuleEnum.NODE).keys(entity.getId());
            cache.get().delete(key);
        }
        return result;
    }

    public NodeEntity getById(Long id) {
        if (id == null) {
            return null;
        }
        //缓存获取
        Key key = Key.build(ModuleEnum.NODE).keys(id);
        NodeEntity nodeEntity = cache.get().get(key, NodeEntity.class);
        if (nodeEntity != null) {
            return nodeEntity;
        }
        nodeEntity = super.getById(id, NodeEntity.class);
        if (nodeEntity != null) {
            cache.get().set(key, nodeEntity);
        }
        return nodeEntity;
    }

    public List<NodeEntity> getByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<Long> newIds = Lists.newArrayList();
        List<NodeEntity> nodeEntities = Lists.newArrayList();
        for (Long id : ids) {
            //缓存获取
            Key key = Key.build(ModuleEnum.NODE).keys(id);
            NodeEntity nodeEntity = cache.get().get(key, NodeEntity.class);
            if (nodeEntity != null) {
                nodeEntities.add(nodeEntity);
                continue;
            }
            newIds.add(id);
        }
        if (newIds.isEmpty()) {
            return nodeEntities;
        }
        List<NodeEntity> rList = super.getByIds(newIds, NodeEntity.class);
        if (!CollectionUtils.isEmpty(rList)) {
            nodeEntities.addAll(rList);
            for (NodeEntity nodeEntity : rList) {
                Key key = Key.build(ModuleEnum.NODE).keys(nodeEntity.getId());
                cache.get().set(key, nodeEntity);
            }
        }
        return nodeEntities;
    }

    public NodeEntity getOneByCode(String code) {
        List<NodeEntity> nodeEntities = query(Cnd.cri().where().andEquals("code", code), NodeEntity.class, 1);
        if (CollectionUtils.isEmpty(nodeEntities)) {
            return null;
        }
        return nodeEntities.get(0);
    }

    public NodeEntity getOneByCode(String code, Integer type) {
        if (StringUtils.isBlank(code) || type == null) {
            return null;
        }
        return fetch(Cnd.cri().where().andEquals("code", code).andEquals("type", type), NodeEntity.class);
    }

    public NodeEntity getOneByOutId(Integer outIdType, Long outId) {
        if (outIdType == null || outId == null || outId == 0L) {
            log.info("NodeDao.getyOneByOutId请求参数错误outIdtype={},outId={}", outIdType, outId);
            return null;
        }
        return fetch(Cnd.cri().where().andEquals("out_id_type", outIdType)
                .andEquals("out_id", outId), NodeEntity.class);
    }

    public List<NodeEntity> getyOneByOutIds(Integer outIdType, List<Long> outIds) {
        if (outIdType == null || CollectionUtils.isEmpty(outIds)) {
            log.info("NodeDao.getyOneByOutIds请求参数错误outIdtype={},outIds={}", outIdType, outIds);
            return null;
        }
        return query(Cnd.cri().where().andEquals("out_id_type", outIdType)
                .andInList("out_id", outIds), NodeEntity.class);
    }

    public NodeEntity getOneByType(int nodeType) {
        return fetch(Cnd.cri().where().andEquals("type", nodeType), NodeEntity.class);
    }

    public NodeEntity getOneByTypeAndName(int nodeType, String nodeName) {
        List<NodeEntity> nodeEntities = query(Cnd.cri().where().andEquals("type", nodeType).andEquals("node_name",nodeName), NodeEntity.class, 1);
        if (CollectionUtils.isEmpty(nodeEntities)) {
            return null;
        }
        return nodeEntities.get(0);
    }

    public NodeEntity getOneByParentId(Long parentId) {
        List<NodeEntity> nodeEntities = query(Cnd.cri().where().andEquals("parent_id", parentId), NodeEntity.class, 1);
        if (CollectionUtils.isEmpty(nodeEntities)) {
            return null;
        }
        return nodeEntities.get(0);
    }

    public List<NodeEntity> getByParentId(Long parentId) {
        return getByParentId(parentId, 200);
    }

    public List<NodeEntity> getByParentId(Long parentId, int limit) {
        return query(Cnd.cri().where().andEquals("parent_id", parentId), NodeEntity.class, limit);
    }

    public List<NodeEntity> getByTypesAndName(List<Integer> types, String nodeName) {
        if (CollectionUtils.isEmpty(types)) {
            return null;
        }
        long[] typeArr = types.stream().mapToLong(e -> e.longValue()).toArray();
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIn("type", typeArr);
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr = sqlExpr.andLike("node_name", nodeName);
        }
        return query(sqlExpr, NodeEntity.class, 100);
    }

    public List<NodeEntity> getByIdsAndName(List<Long> ids, String nodeName) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        long[] idArr = ids.stream().mapToLong(e -> e.longValue()).toArray();
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIn("id", idArr);
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr = sqlExpr.andLike("node_name", nodeName);
        }
        return query(sqlExpr, NodeEntity.class, 100);
    }

    public List<NodeEntity> getByIdsAndType(List<Long> ids, Integer type) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andInList("id", ids);
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        return query(sqlExpr, NodeEntity.class, 100);
    }

    public List<NodeEntity> getByIdsAndTypes(List<Long> ids, List<Long> types) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andInList("id", ids);
        sqlExpr = sqlExpr.andInList("type", types);
        return query(sqlExpr, NodeEntity.class, 100);
    }

    public List<NodeEntity> getListByPage(Long parentId, String nodeName, Integer type, Integer status, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (parentId != null) {
            sqlExpr = sqlExpr.andEquals("parent_id", parentId);
        }
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr = sqlExpr.andLike("node_name", nodeName);
        }
        return getListByPage(sqlExpr, pageData, NodeEntity.class);
    }

    /**
     * 查询关联了外部信息的节点列表
     * @param outType
     * @param type
     * @param status
     * @param pageData
     * @return
     */
    public List<NodeEntity> getOutListByPage(Integer outType, Integer type, Integer status, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        sqlExpr.andEquals("out_id_type", outType);
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        return getListByPage(sqlExpr, pageData, NodeEntity.class);
    }


    public List<NodeEntity> getListByPageByOrgIdAndUserId(String orgId, Long userId, Long parentId, String nodeName, Integer type, Integer relType, Integer status, PageDataVo pageData) {
        if (StringUtils.isBlank(orgId) && userId == null) {
            return getListByPage(parentId, nodeName, type, status, pageData);
        }
        Map<String,Object> param = Maps.newHashMap();
        setParam(param, "orgId", orgId);
        setParam(param, "userId", userId);
        setParam(param, "parentId", parentId);
        setParam(param, "type", type);
        setParam(param, "relType", relType);
        setParam(param, "status", status);
        if (StringUtils.isNotBlank(nodeName)) {
            setParam(param, "nodeName", "%" + nodeName + "%");
        }
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select node.* from node_entity node ");
        if (StringUtils.isNotBlank(orgId)) {
            sqlExpr.append(",flag_entity flag ");
        }
        if (userId != null) {
            sqlExpr.append(",node_user_rel_entity rel ");
        }
        sqlExpr.append("where node.deleted=0 ");
        if (StringUtils.isNotBlank(orgId)) {
            sqlExpr.append("and node.id=flag.parent_id and flag.type=1 and flag.deleted=0 ");
            sqlExpr.append("and flag.flag_key=@orgId ");
        }
        if (userId != null) {
            sqlExpr.append("and node.id=rel.node_id and rel.deleted=0 ");
            sqlExpr.append("and rel.user_id=@userId ");
            if (relType != null) {
                sqlExpr.append("and rel.type=@relType ");
            }
        }
        if (parentId != null) {
            sqlExpr.append("and node.parent_id=@parentId ");
        }
        if (type != null) {
            sqlExpr.append("and node.type=@type ");
        }
        if (status != null) {
            sqlExpr.append("and node.status=@status ");
        }
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr.append("and node.node_name like @nodeName ");
        }
        sqlExpr.append("order by create_time desc");
        return getListByPage(sqlExpr, param, pageData, NodeEntity.class);
    }

    public List<NodeEntity> getListByPageByOrgIdsAndUserId(String[] orgIds, Long userId, Long parentId, String nodeName, Integer type, Integer status, PageDataVo pageData) {
        if ((orgIds==null || orgIds.length == 0) && userId == null) {
            return getListByPage(parentId, nodeName, type, status, pageData);
        }
        StringBuilder fullOrgIds = new StringBuilder();
        if (orgIds!=null && orgIds.length > 0) {
            StringBuilder orgIdPath = new StringBuilder();
            for (int idx = 0; idx < orgIds.length; idx++) {
                orgIdPath.append(orgIds[idx]);
                fullOrgIds.append("'").append(orgIdPath.toString()).append("'");
                if (idx != orgIds.length - 1) {
                    fullOrgIds.append(",");
                }
                if (idx != orgIds.length - 1) {
                    orgIdPath.append("/");
                }
            }
        }
        Map<String,Object> param = Maps.newHashMap();
        setParam(param, "userId", userId);
        setParam(param, "parentId", parentId);
        setParam(param, "type", type);
        setParam(param, "status", status);
        if (StringUtils.isNotBlank(nodeName)) {
            setParam(param, "nodeName", "%" + nodeName + "%");
        }
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select node.* from node_entity node ");
        if (userId != null) {
            sqlExpr.append("LEFT JOIN node_user_rel_entity rel ON node.id=rel.node_id ");
        }
        if (fullOrgIds.length() > 0) {
            sqlExpr.append("LEFT JOIN flag_entity flag ON node.id=flag.parent_id ");
        }
        sqlExpr.append("where node.deleted=0 ");
        if (fullOrgIds.length() > 0 && userId != null) {
            sqlExpr.append("and (( ");
            sqlExpr.append("flag.type=2 and flag.deleted=0 ");
            sqlExpr.append("and flag.flag_key in (").append(fullOrgIds).append(") ");
            sqlExpr.append(") ");
            sqlExpr.append("or ( ");
            sqlExpr.append("rel.deleted=0 ");
            sqlExpr.append("and rel.user_id=@userId ");
            sqlExpr.append(")) ");
        } else if (fullOrgIds.length() > 0) {
            sqlExpr.append("and flag.type=2 and flag.deleted=0 ");
            sqlExpr.append("and flag.flag_key in (").append(fullOrgIds).append(") ");
        } else if (userId != null) {
            sqlExpr.append("and rel.deleted=0 ");
            sqlExpr.append("and rel.user_id=@userId ");
        }
        if (parentId != null) {
            sqlExpr.append("and node.parent_id=@parentId ");
        }
        if (type != null) {
            sqlExpr.append("and node.type=@type ");
        }
        if (status != null) {
            sqlExpr.append("and node.status=@status ");
        }
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr.append("and node.node_name like @nodeName ");
        }
        sqlExpr.append("group by node.id order by create_time desc");
        String str =sqlExpr.toString();
        return getListByPage(sqlExpr, param, pageData, NodeEntity.class);
    }

    public List<NodeEntity> getListByPageByUserGroupIdsAndUserId(List<Long> userGroupIds, Long userId, Long parentId, String nodeName, Integer type, Integer status, PageDataVo pageData) {
        if (CollectionUtils.isEmpty(userGroupIds) && userId == null) {
            return getListByPage(parentId, nodeName, type, status, pageData);
        }
        Map<String,Object> param = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(userGroupIds)) {
            setParam(param, "userGroupIds", userGroupIds);
        }
        setParam(param, "userId", userId);
        setParam(param, "parentId", parentId);
        setParam(param, "type", type);
        setParam(param, "status", status);
        if (StringUtils.isNotBlank(nodeName)) {
            setParam(param, "nodeName", "%" + nodeName + "%");
        }
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select node.* from node_entity node ");
        if (userId != null) {
            sqlExpr.append("LEFT JOIN node_user_rel_entity rel ON node.id=rel.node_id ");
        }
        if (!CollectionUtils.isEmpty(userGroupIds)) {
            sqlExpr.append("LEFT JOIN node_user_group_rel_entity nugrel ON node.id=nugrel.node_id ");
        }
        sqlExpr.append("where node.deleted=0 ");
        if (!CollectionUtils.isEmpty(userGroupIds) && userId != null) {
            sqlExpr.append("and rel.deleted=0 ");
            sqlExpr.append("and (nugrel.deleted=0 OR nugrel.id IS NULL) ");
            sqlExpr.append("and (nugrel.user_group_id in (").append(StringUtils.join(userGroupIds, ",")).append(") ");
            sqlExpr.append("or rel.user_id=@userId) ");
        } else if (!CollectionUtils.isEmpty(userGroupIds)) {
            sqlExpr.append("and nugrel.deleted=0 ");
            sqlExpr.append("and nugrel.user_group_id in (").append(StringUtils.join(userGroupIds, ",")).append(") ");
        } else if (userId != null) {
            sqlExpr.append("and rel.deleted=0 ");
            sqlExpr.append("and rel.user_id=@userId ");
        }
        if (parentId != null) {
            sqlExpr.append("and node.parent_id=@parentId ");
        }
        if (type != null) {
            sqlExpr.append("and node.type=@type ");
        }
        if (status != null) {
            sqlExpr.append("and node.status=@status ");
        }
        if (StringUtils.isNotBlank(nodeName)) {
            sqlExpr.append("and node.node_name like @nodeName ");
        }
        sqlExpr.append("group by node.id order by create_time desc");
        return getListByPage(sqlExpr, param, pageData, NodeEntity.class);
    }

}
