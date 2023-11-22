package com.xiaomi.mone.tpc.node;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OperActionEnum;
import com.xiaomi.mone.tpc.common.param.NodeAddParam;
import com.xiaomi.mone.tpc.common.param.NodeResourceSyncParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.vo.NodeResourceVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeResourceRelEntity;
import com.xiaomi.mone.tpc.dao.entity.ResourceEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeResourceRelDao;
import com.xiaomi.mone.tpc.dao.impl.ResourceDao;
import com.xiaomi.mone.tpc.node.util.NodeUtil;
import com.xiaomi.mone.tpc.resource.util.NodeResourceRelUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/27 10:49
 */
@Slf4j
@Service
public class NodeResourceService {

    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private NodeResourceRelDao nodeResourceRelDao;

    /**
     * 节点数据同步
     * @param param
     * @return
     */
    public ResultVo<NodeResourceVo> sync(NodeResourceSyncParam param) {
        NodeEntity curNode = null;
        if (OperActionEnum.ADD.getCode().equals(param.getOperAction())) {
            NodeEntity parentNode = null;
            if (param.getParentNodeId() != null) {
                parentNode = nodeDao.getById(param.getParentNodeId(), NodeEntity.class);
                if (parentNode == null) {
                    return ResponseCode.OPER_FAIL.build();
                }
            } else {
                parentNode = nodeDao.getOneByOutId(param.getParentOutIdType(), param.getParentOutId());
                if (parentNode == null) {
                    return ResponseCode.NO_OPER_PERMISSION.build("父级节点不存在");
                }
            }
            curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
            if (curNode != null) {
                return ResponseCode.NO_OPER_PERMISSION.build("同步节点已存在");
            }
            NodeTypeEnum parentNodeType = NodeTypeEnum.getEnum(parentNode.getType());
            if (!parentNodeType.supportSubNodeType(param.getType())) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            List<Long> allParentIds = Lists.newArrayList();
            allParentIds.addAll(nodeHelper.getparentNodeIdList(parentNode.getContent()));
            allParentIds.add(parentNode.getId());
            NodeAddParam addParam = new NodeAddParam();
            BeanUtils.copyProperties(param, addParam);
            curNode = nodeHelper.buildCurNode(addParam, parentNode, allParentIds);
        } else if (OperActionEnum.EDIT.getCode().equals(param.getOperAction())) {
            if (param.getNodeId() != null) {
                curNode = nodeDao.getById(param.getNodeId(), NodeEntity.class);
                if (curNode == null) {
                    return ResponseCode.OPER_FAIL.build("当前节点不存在");
                }
            } else {
                curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
                if (curNode == null) {
                    return ResponseCode.OPER_FAIL.build("当前节点不存在");
                }
            }
            if (param.getEnv() != null) {
                curNode.setEnv(GsonUtil.gsonString(param.getEnv()));
            }
            curNode.setNodeName(param.getNodeName());
            curNode.setDesc(param.getDesc());
            curNode.setUpdaterId(param.getUserId());
            curNode.setUpdaterAcc(param.getAccount());
            curNode.setUpdaterType(param.getUserType());
        } else if (OperActionEnum.DELETE.getCode().equals(param.getOperAction())) {
            if (param.getNodeId() != null) {
                curNode = nodeDao.getById(param.getNodeId(), NodeEntity.class);
                if (curNode == null) {
                    return ResponseCode.OPER_FAIL.build("当前节点不存在");
                }
            } else {
                curNode = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
                if (curNode == null) {
                    return ResponseCode.OPER_FAIL.build("当前节点不存在");
                }
            }
        }
        NodeResourceVo nodeResourceVo = new NodeResourceVo();
        NodeEntity finalNodeEntity = curNode;
        List<NodeResourceRelEntity> entitieRels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(param.getResourceIds()) && (OperActionEnum.ADD.getCode().equals(param.getOperAction())
                || OperActionEnum.EDIT.getCode().equals(param.getOperAction()))) {
            List<ResourceEntity> resourceEntitys = resourceDao.getByIds(param.getResourceIds(), ResourceEntity.class);
            if (resourceEntitys == null || resourceEntitys.size() != param.getResourceIds().size()) {
                return ResponseCode.OPER_ILLEGAL.build("存在无效资源");
            }
            boolean checkResult = resourceEntitys.stream().filter(r -> (NodeTypeEnum.RES_GROUP_TYPE.getCode().equals(finalNodeEntity.getType()) && !r.getEnvFlag().equals(finalNodeEntity.getEnvFlag()))).findAny().isPresent();
            if (checkResult) {
                return ResponseCode.OPER_ILLEGAL.build("存在资源和节点不匹配");
            }
            for (ResourceEntity resourceEntity : resourceEntitys) {
                NodeResourceRelEntity entityRel = new NodeResourceRelEntity();
                entityRel.setType(resourceEntity.getType());
                entityRel.setStatus(0);
                entityRel.setCreaterId(param.getUserId());
                entityRel.setCreaterAcc(param.getAccount());
                entityRel.setCreaterType(param.getUserType());
                entityRel.setUpdaterId(param.getUserId());
                entityRel.setUpdaterAcc(param.getAccount());
                entityRel.setUpdaterType(param.getUserType());
                entityRel.setResourceId(resourceEntity.getId());
                entityRel.setResourceType(resourceEntity.getType());
                entityRel.setNodeId(finalNodeEntity.getId());
                entityRel.setNodeType(finalNodeEntity.getType());
                entitieRels.add(entityRel);
            }
        }
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if (OperActionEnum.ADD.getCode().equals(param.getOperAction())) {
                        nodeDao.insertWithException(finalNodeEntity);
                        entitieRels.forEach(nr -> nr.setNodeId(finalNodeEntity.getId()));
                        nodeResourceRelDao.batchInsertWithException(entitieRels);
                    } else if (OperActionEnum.EDIT.getCode().equals(param.getOperAction())) {
                        nodeDao.updateByIdWithExcption(finalNodeEntity);
                        if (!nodeResourceRelDao.deleteByNodeId(finalNodeEntity.getId())) {
                            throw new RuntimeException("节点关联资源删除失败");
                        }
                        entitieRels.forEach(nr -> nr.setNodeId(finalNodeEntity.getId()));
                        nodeResourceRelDao.batchInsertWithException(entitieRels);
                    } else if (OperActionEnum.DELETE.getCode().equals(param.getOperAction())) {
                        if (!nodeDao.deleteById(finalNodeEntity)) {
                            throw new RuntimeException("节点删除失败");
                        }
                        if (!nodeResourceRelDao.deleteByNodeId(finalNodeEntity.getId())) {
                            throw new RuntimeException("节点关联资源删除失败");
                        }
                    }
                }
            });
        } catch (Throwable e) {
            log.error("节点同步db操作失败 param={}", param, e);
            return ResponseCode.OPER_FAIL.build();
        }
        nodeResourceVo.setNodeVo(NodeUtil.toVo(finalNodeEntity));
        nodeResourceVo.setNodeResourceRelVos(NodeResourceRelUtil.toVoList(entitieRels));
        return ResponseCode.SUCCESS.build(nodeResourceVo);
    }

}
