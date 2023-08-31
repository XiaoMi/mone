package com.xiaomi.mone.tpc.resource;

import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.ResourceStatusEnum;
import com.xiaomi.mone.tpc.common.enums.ResourceTypeEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeResourceRelEntity;
import com.xiaomi.mone.tpc.dao.entity.ResourceEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeResourceRelDao;
import com.xiaomi.mone.tpc.dao.impl.ResourceDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.resource.util.NodeResourceRelUtil;
import com.xiaomi.mone.tpc.resource.util.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class ResourceService {

    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeResourceRelDao nodeResourceRelDao;
    @Autowired
    private NodeHelper nodeHelper;

    /**
     * 资源池资源查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<ResourceVo>> pool(ResourceQryParam param) {
        PageDataVo<ResourceVo> pageData = param.buildPageDataVo();
        NodeEntity nodeEntity = null;
        if (param.getRelOutId() != null && param.getRelOutIdType() != null) {
            nodeEntity = nodeDao.getOneByOutId(param.getRelOutIdType(), param.getRelOutId());
        } else {
            nodeEntity = nodeDao.getById(param.getRelNodeId(), NodeEntity.class);
        }
        if (nodeEntity == null) {
            return ResponseCode.SUCCESS.build(pageData);
        }
        List<Long> parentIds = nodeHelper.getparentNodeIdList(nodeEntity.getContent());
        List<Long> types = new ArrayList<>();
        types.add(NodeTypeEnum.PRO_GROUP_TYPE.getCode().longValue());
        types.add(NodeTypeEnum.TOP_TYPE.getCode().longValue());
        List<NodeEntity> nodeEntities = nodeDao.getByIdsAndTypes(parentIds, types);
        if (CollectionUtils.isEmpty(nodeEntities)) {
            return ResponseCode.SUCCESS.build(pageData);
        }
        List<Long> poolIds = nodeEntities.stream().map(NodeEntity::getId).collect(Collectors.toList());
        List<ResourceEntity> entityList = resourceDao.getPoolByPage(poolIds, param.getType(), param.getRegion(), ResourceStatusEnum.ENABLE.getCode(), param.getKey1(), param.getResourceName(), nodeEntity.getEnvFlag(), pageData);
        pageData.setList(ResourceUtil.toVoList(entityList, false));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<ResourceVo>> list(ResourceQryParam param) {
        PageDataVo<ResourceVo> pageData = param.buildPageDataVo();
        //通过外部ID转换内部ID
        if (param.getRelOutId() != null && param.getRelOutIdType() != null) {
            NodeEntity node = nodeDao.getOneByOutId(param.getRelOutIdType(), param.getRelOutId());
            if (node == null) {
                return ResponseCode.OPER_ILLEGAL.build("关联节点不存在");
            }
            param.setRelNodeId(node.getId());
        }
        List<ResourceEntity> entityList = resourceDao.getListByPage(param.getPoolNodeId(),param.getRelNodeId(), param.getApplyId(), param.getType(), param.getRegion(), param.getStatus(), param.getKey1(), param.getResourceName(), param.getEnvFlag(), pageData);
        pageData.setList(ResourceUtil.toVoList(entityList, false));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<ResourceVo> getByRelId(ResourceQryParam param) {
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getById(param.getId(), NodeResourceRelEntity.class);
        if (nodeResourceRelEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        param.setId(nodeResourceRelEntity.getResourceId());
        return get(true, param);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<ResourceVo> get(boolean force, ResourceQryParam param) {
        ResourceEntity entity = resourceDao.getById(param.getId(), ResourceEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build("资源不存在");
        }
        List<Long> parentIds = null;
        if (param.getRelOutId() != null && param.getRelOutIdType() != null) {
            NodeEntity node = nodeDao.getOneByOutId(param.getRelOutIdType(), param.getRelOutId());
            if (node == null) {
                return ResponseCode.OPER_ILLEGAL.build("关联节点不存在");
            }
            parentIds = nodeHelper.getparentNodeIdList(node.getContent());
        } else if (param.getRelNodeId() != null) {
            NodeEntity node = nodeDao.getById(param.getRelNodeId(), NodeEntity.class);
            if (node == null) {
                return ResponseCode.OPER_ILLEGAL.build("关联节点不存在");
            }
            parentIds = nodeHelper.getparentNodeIdList(node.getContent());
        }
        NodeEntity proGroupNode = null;
        if (!CollectionUtils.isEmpty(parentIds)) {
            if (!parentIds.contains(entity.getPoolNodeId())) {
                return ResponseCode.OPER_ILLEGAL.build("资源和节点不匹配");
            }
            List<NodeEntity> nodeEntities = nodeDao.getByIdsAndType(parentIds, NodeTypeEnum.PRO_GROUP_TYPE.getCode());
            if (CollectionUtils.isEmpty(nodeEntities)) {
                return ResponseCode.OPER_ILLEGAL.build("没有找到项目组节点");
            }
            proGroupNode = nodeEntities.get(0);
        }
        NodeEntity nodeEntity = nodeDao.getById(entity.getPoolNodeId(),NodeEntity.class);
        if (nodeEntity == null || !NodeStatusEnum.ENABLE.getCode().equals(nodeEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("节点不存在或不可用");
        }
        if (!force && !nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        ResourceVo resourceVo = ResourceUtil.toVo(entity, true);
        if (proGroupNode != null) {
            resourceVo.setNodeCode(proGroupNode.getCode());
        }
        return ResponseCode.SUCCESS.build(resourceVo);
    }

    /**
     * 资源关联
     * @param param
     * @return
     */
    public ResultVo<NodeResourceRelVo> relation(boolean force, ResourceRelParam param) {
        ResourceEntity resourceEntity = resourceDao.getById(param.getResourceId(), ResourceEntity.class);
        if (resourceEntity == null || !ResourceStatusEnum.ENABLE.getCode().equals(resourceEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("资源不存在或不可用");
        }
        NodeEntity nodeEntity = nodeDao.getById(param.getId(),NodeEntity.class);
        if (nodeEntity == null || !NodeStatusEnum.ENABLE.getCode().equals(nodeEntity.getStatus())) {
            return ResponseCode.OPER_ILLEGAL.build("节点不存在或不可用");
        }
        if (nodeEntity.getEnvFlag() == null || !nodeEntity.getEnvFlag().equals(resourceEntity.getEnvFlag())) {
            return ResponseCode.OPER_ILLEGAL.build("环境和资源不匹配");
        }
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getOneByNodeIdAndResourceId(nodeEntity.getId(), resourceEntity.getId());
        if (nodeResourceRelEntity != null) {
            return ResponseCode.SUCCESS.build();
        }
        if (!NodeTypeEnum.supportResNodeType(nodeEntity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build("该节点不支持关联资源");
        }
        List<Long> parentIds = nodeHelper.getparentNodeIdList(nodeEntity.getContent());
        if (!parentIds.contains(resourceEntity.getPoolNodeId())) {
            return ResponseCode.OPER_ILLEGAL.build("该节点不能关联非上级资源池");
        }
        if (!force && !nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.OPER_ILLEGAL.build("非节点成员");
        }
        NodeResourceRelEntity entity = new NodeResourceRelEntity();
        entity.setType(resourceEntity.getType());
        entity.setStatus(0);
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setResourceId(param.getResourceId());
        entity.setResourceType(resourceEntity.getType());
        entity.setNodeId(nodeEntity.getId());
        entity.setNodeType(nodeEntity.getType());
        boolean res = nodeResourceRelDao.insert(entity);
        if (!res) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return ResponseCode.SUCCESS.build(NodeResourceRelUtil.toVo(entity));
    }

    /**
     * 资源解除关联
     * @param param
     * @return
     */
    public ResultVo delRelation(boolean force ,ResourceDelRelParam param) {

        NodeEntity nodeEntity = nodeDao.getById(param.getId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build("节点不存在");
        }
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getOneByNodeIdAndResourceId(param.getId(), param.getResourceId());
        if (nodeResourceRelEntity == null) {
            return ResponseCode.OPER_FAIL.build("不存在此关联信息");
        }
        //非上级节点成员
        if (!force && !nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.OPER_ILLEGAL.build("非节点成员");
        }
        if (!nodeResourceRelDao.deleteByNodeIdAndResourceId(param.getId(), param.getResourceId())) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build("success");
    }

    /**
     * @param param
     * @return
     */
    public ResultVo getRelation(ResourceRelGetParam param) {
        List<NodeResourceRelEntity> relationsByNodeId = nodeResourceRelDao.getRelationsByNodeId(param.getId());
        if (relationsByNodeId.isEmpty()) {
            return ResponseCode.SUCCESS.build();
        }
        List<String> resourceIds = new ArrayList<>();
        relationsByNodeId.stream().forEach(it -> {
            resourceIds.add(it.getResourceId().toString());
        });
        ResourceGetResourceOrderByType resources = new ResourceGetResourceOrderByType();
        resources.setId(param.getId());
        resources.setResourceIds(resourceIds.toString());
        return getResourceOrderByType(resources, param.isEncrypted());
    }

    /**
     * 资源添加
     * @param param
     * @return
     */
    public ResultVo add(ResourceAddParam param) {
        NodeEntity nodeEntity = nodeDao.getById(param.getNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!NodeTypeEnum.supportResPoolNode(nodeEntity.getType())) {
            return ResponseCode.OPER_ILLEGAL.build("该节点不支持资源池功能");
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        ResourceEntity entity = new ResourceEntity();
        entity.setType(param.getType());
        entity.setStatus(param.getStatus());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setResourceName(param.getResourceName());
        entity.setPoolNodeId(nodeEntity.getId());
        entity.setEnvFlag(param.getEnvFlag());
        entity.setApplyId(0L);
        entity.setKey1("");
        entity.setKey2("");
        entity.setContent(GsonUtil.gsonString(param.getArg()));
        entity.setIsOpenKc(param.getIsOpenKc());
        entity.setSid(param.getSid());
        entity.setKcUser(param.getKcUser());
        entity.setMfa(param.getMfa());
        entity.setRegion(param.getRegion());
        boolean result = resourceDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return ResponseCode.SUCCESS.build(ResourceUtil.toVo(entity, true));
    }

    /**
     * 资源编辑
     * @param param
     * @return
     */
    public ResultVo edit(ResourceEditParam param) {
        ResourceEntity resourceEntity = resourceDao.getById(param.getId(), ResourceEntity.class);
        if (resourceEntity == null || !resourceEntity.getType().equals(param.getType())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(resourceEntity.getPoolNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        ResourceEntity entity = new ResourceEntity();
        entity.setId(param.getId());
        entity.setStatus(param.getStatus());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setResourceName(param.getResourceName());
        entity.setContent(GsonUtil.gsonString(param.getArg()));
        entity.setIsOpenKc(param.getIsOpenKc());
        entity.setSid(param.getSid());
        entity.setKcUser(param.getKcUser());
        entity.setMfa(param.getMfa());
        boolean result = resourceDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 状态变更
     * @param param
     * @return
     */
    public ResultVo status(ResourceStatusParam param) {
        ResourceEntity resourceEntity = resourceDao.getById(param.getId(), ResourceEntity.class);
        if (resourceEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(resourceEntity.getPoolNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        ResourceEntity entity = new ResourceEntity();
        entity.setId(param.getId());
        entity.setStatus(param.getStatus());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        boolean result = resourceDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return ResponseCode.SUCCESS.build();
    }


    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo delete(ResourceDeleteParam param) {
        ResourceEntity resourceEntity = resourceDao.getById(param.getId(), ResourceEntity.class);
        if (resourceEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(resourceEntity.getPoolNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        NodeResourceRelEntity nodeResourceRelEntity = nodeResourceRelDao.getOneByResourceId(resourceEntity.getId());
        if (nodeResourceRelEntity != null) {
            return ResponseCode.OPER_FAIL.build("资源已被关联，不能删除");
        }
        resourceEntity.setUpdaterAcc(param.getAccount());
        resourceEntity.setUpdaterId(param.getUserId());
        resourceEntity.setUpdaterType(param.getUserType());
        boolean result = resourceDao.deleteById(resourceEntity);
        if (!result) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 根据类别获取对应data-source列表
     * @param param
     * @return
     */
    public ResultVo getTypeList(ResourceGetTypeListParam param) {
        NodeEntity nodeEntity = nodeDao.getById(param.getId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        List<Long> parentIds = nodeHelper.getparentNodeIdList(nodeEntity.getContent());
        List<Long> types = new ArrayList<>();
        types.add(NodeTypeEnum.PRO_GROUP_TYPE.getCode().longValue());
        types.add(NodeTypeEnum.TOP_TYPE.getCode().longValue());
        List<NodeEntity> nodeEntities = nodeDao.getByIdsAndTypes(parentIds, types);
        if (CollectionUtils.isEmpty(nodeEntities)) {
            return ResponseCode.SUCCESS.build();
        }
        List<Long> poolIds = nodeEntities.stream().map(NodeEntity::getId).collect(Collectors.toList());
        Integer type =  ResourceTypeEnum.getEnumByString(param.getType()).getCode();
        List<ResourceEntity> typeList = resourceDao.getPoolByPage(poolIds, type, param.getRegion(), ResourceStatusEnum.ENABLE.getCode(), null, null, nodeEntity.getEnvFlag(), param.buildPageDataVo());
        if (CollectionUtils.isEmpty(typeList)) {
            return ResponseCode.SUCCESS.build();
        }
        List<ArgCheck> res = new ArrayList<>();
        Class<? extends ArgCheck> clazz = ResourceTypeEnum.getEnum(type).getClazz();
        typeList.stream().forEach(typeEntity -> {
            Map map = GsonUtil.gsonToBean(typeEntity.getContent(), Map.class);
            map.put("id",typeEntity.getId());
            map.put("name",typeEntity.getResourceName());
            map.put("type",typeEntity.getType().toString());
            String mapJson =  GsonUtil.gsonString(map);
            ArgCheck argCheck = GsonUtil.gsonToBean(mapJson, clazz);
            res.add(argCheck);
        });
        return ResponseCode.SUCCESS.build(res);
    }

    public ResultVo getResourceOrderByType(ResourceGetResourceOrderByType param, boolean encrypted){
        NodeEntity nodeEntity = nodeDao.getById(param.getId(),NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        try {
            String resourceIds = param.getResourceIds().trim();
            resourceIds = StringUtils.replaceEach(resourceIds,new String[]{"[","]"},new String[]{"",""});
            String[] resourceIdsArray = resourceIds.split(",");
            List<Integer> finalResourceIds = new ArrayList<>();
            Arrays.stream(resourceIdsArray).forEach(
                    it->finalResourceIds.add(Integer.parseInt(it.trim()))
            );
            List<ResourceEntity> result = resourceDao.getResourceOrderByType(finalResourceIds);
            if (CollectionUtils.isEmpty(result)) {
                return ResponseCode.SUCCESS.build("");
            }
            Map<String,List<Object>> map2 = new HashMap<>();
            //组织返回格式
            result.stream().forEach(
                    it -> {
                        Class<? extends ArgCheck> clazz = ResourceTypeEnum.getEnum(it.getType()).getClazz();
                        Map map = GsonUtil.gsonToBean(it.getContent(), Map.class);
                        map.put("id",it.getId());
                        map.put("name",it.getResourceName());
                        map.put("type",it.getType().toString());
                        //添加kc相关配置
                        map.put("isOpenKc",it.getIsOpenKc());
                        map.put("sid",it.getSid());
                        map.put("kcUser",it.getKcUser());
                        map.put("mfa",it.getMfa());
                        String mapJson =  GsonUtil.gsonString(map);
                        ArgCheck argCheck = GsonUtil.gsonToBean(mapJson, clazz);
                        //密码字段加密处理
                        if (encrypted) {
                            argCheck.encrypted();
                        }
                        String typeMapKey = ResourceTypeEnum.getEnum(it.getType()).getDesc();
                        if (map2.containsKey(typeMapKey)) {
                            List<Object> typeLists = map2.get(typeMapKey);
                            typeLists.add(argCheck);
                        } else {
                            map2.put(typeMapKey,new ArrayList<>());
                            List<Object> typeLists = map2.get(typeMapKey);
                            typeLists.add(argCheck);
                        }
                    }
            );
            return ResponseCode.SUCCESS.build(map2);
        }catch (Exception e) {
            log.error("DataSourceService.getResourceOrderByType error : {}",e.toString());
            return ResponseCode.OPER_FAIL.build();
        }
    }

}
