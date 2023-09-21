package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.enums.NodeChangeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeUserRelDao;
import com.xiaomi.mone.tpc.dao.impl.UserDao;
import com.xiaomi.mone.tpc.node.change.ProNodeChangeHelper;
import com.xiaomi.mone.tpc.node.util.NodeUserRelUtil;
import com.xiaomi.mone.tpc.node.util.NodeUtil;
import com.xiaomi.mone.tpc.user.UserHelper;
import com.xiaomi.mone.tpc.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 16:19
 */
@Slf4j
@Service
public class NodeUserService implements NodeUserHelper {

    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;
    @Autowired
    private NodeHelper nodehelper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserHelper userHelper;
    @Resource
    private ProNodeChangeHelper proNodeChangeHelper;

    @Override
    public List<NodeUserRelVo> list(NodeVo nodeVo) {
        List<NodeUserRelEntity> nodeUserRelList = nodeUserRelDao.getListByNodeId(nodeVo.getId(), null, 100);
        return NodeUserRelUtil.toVoList(nodeUserRelList);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeUserRelVo>> list(NodeUserQryParam param) {
        PageDataVo<NodeUserRelVo> pageData = param.buildPageDataVo();
        if (param.getNodeId() == null) {
            return ResponseCode.SUCCESS.build(pageData);
        }
        List<NodeUserRelEntity> entityList = nodeUserRelDao.getListByPage(param.getNodeId(), param.getType(), param.getMemberId(), param.getTester(), pageData);
        pageData.setList(NodeUserRelUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<NodeUserRelVo> get(NodeUserQryParam param) {
        NodeUserRelEntity entity = nodeUserRelDao.getById(param.getId(), NodeUserRelEntity.class);
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        return ResponseCode.SUCCESS.build(NodeUserRelUtil.toVo(entity));
    }

    public ResultVo batchOper(NodeUserBatchOperParam param) {
        UserVo userVo = null;
        if (param.getMemberId() != null) {
            UserEntity userEntity = userDao.getById(param.getMemberId(), UserEntity.class);
            if (userEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            userVo = UserUtil.toVo(userEntity, true);
        } else {
            userVo = userHelper.register(param.getMemberAcc(), param.getMemberAccType());
            if (userVo == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        }
        Set<Long> allNodeIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(param.getAddNodeIds())) {
            allNodeIds.addAll(param.getAddNodeIds());
        }
        if (!CollectionUtils.isEmpty(param.getDelNodeIds())) {
            allNodeIds.addAll(param.getDelNodeIds());

        }
        List<NodeUserRelEntity> nodeUserRelEntities = nodeUserRelDao.getByNodeIdsAndUserId(allNodeIds, userVo.getId(), null);
        if (nodeUserRelEntities == null) {
            nodeUserRelEntities = new ArrayList<>();
        }
        boolean checkResult = nodeUserRelEntities.stream().filter(rel -> !rel.getNodeType().equals(param.getNodeType())).findAny().isPresent();
        if (checkResult) {
            return ResponseCode.OPER_ILLEGAL.build("操作节点和节点类型不匹配");
        }
        Set<Long> realAddNodeIds = new HashSet<>();
        Map<Long, NodeUserRelEntity> nodeUserRelEntityMap = nodeUserRelEntities.stream().collect(Collectors.toMap(NodeUserRelEntity::getNodeId, rel -> rel));
        if (!CollectionUtils.isEmpty(param.getAddNodeIds())) {
            for (Long addNodeId : param.getAddNodeIds()) {
                if (nodeUserRelEntityMap.containsKey(addNodeId)) {
                    continue;
                }
                realAddNodeIds.add(addNodeId);
            }
        }
        Set<Long> realDelNodeIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(param.getDelNodeIds())) {
            for (Long delNodeId : param.getDelNodeIds()) {
                if (!nodeUserRelEntityMap.containsKey(delNodeId)) {
                    continue;
                }
                realDelNodeIds.add(delNodeId);
            }
        }
        List<NodeUserRelEntity> addNodeUserRelEntities = new ArrayList<>();
        for (Long addNodeId : realAddNodeIds) {
            NodeUserRelEntity entity = new NodeUserRelEntity();
            entity.setType(param.getType());
            entity.setCreaterId(param.getUserId());
            entity.setCreaterAcc(param.getAccount());
            entity.setCreaterType(param.getUserType());
            entity.setUpdaterId(param.getUserId());
            entity.setUpdaterAcc(param.getAccount());
            entity.setUpdaterType(param.getUserType());
            entity.setUserId(userVo.getId());
            entity.setUserType(userVo.getType());
            entity.setAccount(userVo.getAccount());
            entity.setNodeId(addNodeId);
            entity.setNodeType(param.getNodeType());
            if (NodeTypeEnum.PRO_TYPE.getCode().equals(param.getNodeType())) {
                entity.setTester(param.getTester());
            }
            addNodeUserRelEntities.add(entity);
        }
        if (!CollectionUtils.isEmpty(addNodeUserRelEntities) || !CollectionUtils.isEmpty(realDelNodeIds)) {
            Long userId = userVo.getId();
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if (!CollectionUtils.isEmpty(realDelNodeIds) && !nodeUserRelDao.deleteByNodeIdsAndUserId(realDelNodeIds, userId)) {
                        throw new RuntimeException("批量删除失败");
                    }
                    if (!CollectionUtils.isEmpty(addNodeUserRelEntities)) {
                        nodeUserRelDao.batchInsertWithException(addNodeUserRelEntities);
                    }
                }
            });
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 添加
     * @param force
     * @param param
     * @return
     */
    @Override
    public ResultVo<NodeUserRelVo> add(boolean force, NodeUserAddParam param) {
        UserVo userVo = null;
        if (param.getMemberId() != null) {
            UserEntity userEntity = userDao.getById(param.getMemberId(), UserEntity.class);
            if (userEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            userVo = UserUtil.toVo(userEntity, true);
        } else {
            userVo = userHelper.register(param.getMemberAcc(), param.getMemberAccType());
            if (userVo == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        }
        NodeEntity nodeEntity = null;
        if (param.getNodeId() != null) {
            nodeEntity = nodeDao.getById(param.getNodeId(), NodeEntity.class);
            if (nodeEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        } else {
            nodeEntity = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
            if (nodeEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        }
        if (!force && !nodehelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            log.info("用户{}在节点{}添加成员，用户不是当前节点或上级节点管理员", param.getAccount(), nodeEntity.getId());
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (!NodeTypeEnum.getEnum(nodeEntity.getType()).supportNodeUserType(param.getType())) {
            log.info("用户{}在节点{}添加用户{}为{}类型成员，节点类型{}不支持", param.getAccount(), nodeEntity.getId(), userVo.getAccount(), param.getType(), nodeEntity.getType());
            return ResponseCode.OPER_ILLEGAL.build();
        }
        return realAdd(param, param.getType(), param.getTester(), userVo, NodeUtil.toVo(nodeEntity));
    }

    private ResultVo<NodeUserRelVo> realAdd(BaseParam param, Integer type, Integer tester, UserVo userVo, NodeVo nodeVo) {
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getOneByNodeIdAndUserId(nodeVo.getId(), userVo.getId(), null);
        if (nodeUserRelEntity != null) {
            return ResponseCode.OPER_FAIL.build("用户已经是节点成员");
        }
        NodeUserRelEntity entity = new NodeUserRelEntity();
        entity.setType(type);
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setUserId(userVo.getId());
        entity.setUserType(userVo.getType());
        entity.setAccount(userVo.getAccount());
        entity.setNodeId(nodeVo.getId());
        entity.setNodeType(nodeVo.getType());
        if (NodeTypeEnum.PRO_TYPE.getCode().equals(nodeVo.getType())) {
            entity.setTester(tester);
        }
        boolean result = nodeUserRelDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        proNodeChangeHelper.change(NodeChangeEnum.ADD, nodeVo);
        return ResponseCode.SUCCESS.build(NodeUserRelUtil.toVo(entity));
    }

    @Override
    public ResultVo<List<NodeUserRelVo>> updateProjectMember(BaseParam param, List<NodeUserRelVo> relVos, NodeVo nodeVo) {
        if (relVos == null) {
            relVos = new ArrayList<>();
        }
        Map<String, NodeUserRelEntity> relEntityMap = new HashMap<>();
        for (NodeUserRelVo relVo : relVos) {
            if (relVo.getType() == null) {
                continue;
            }
            String fullAcc = com.xiaomi.mone.tpc.login.util.UserUtil.getFullAccount(relVo.getAccount(), relVo.getUserType());
            NodeUserRelEntity relEntity = relEntityMap.get(fullAcc);
            if (relEntity != null) {
                if (NodeUserRelTypeEnum.MANAGER.getCode().equals(relEntity.getType())) {
                    continue;
                }
                if (!NodeUserRelTypeEnum.MANAGER.getCode().equals(relVo.getType())) {
                    continue;
                }
            }
            relEntity = new NodeUserRelEntity();
            relEntity.setType(relVo.getType());
            relEntity.setCreaterId(param.getUserId());
            relEntity.setCreaterAcc(param.getAccount());
            relEntity.setCreaterType(param.getUserType());
            relEntity.setUpdaterId(param.getUserId());
            relEntity.setUpdaterAcc(param.getAccount());
            relEntity.setUpdaterType(param.getUserType());
            UserVo userVo = userHelper.register(relVo.getAccount(), relVo.getUserType());
            if (userVo != null) {
                relEntity.setUserId(userVo.getId());
            }
            relEntity.setAccount(relVo.getAccount());
            relEntity.setUserType(relVo.getUserType());
            relEntity.setNodeId(nodeVo.getId());
            relEntity.setNodeType(nodeVo.getType());
            relEntity.setTester(0);
            relEntityMap.put(fullAcc, relEntity);
        }
        try {
            List<NodeUserRelEntity> relEntities = new ArrayList<>(relEntityMap.values());
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if (!nodeUserRelDao.deleteByNodeId(nodeVo.getId())) {
                        throw new RuntimeException("节点成员删除失败nodeId=" + nodeVo.getId());
                    }
                    if (!CollectionUtils.isEmpty(relEntityMap)) {
                        nodeUserRelDao.batchInsertWithException(relEntities);
                    }
                }
            });
            return ResponseCode.SUCCESS.build(NodeUserRelUtil.toVoList(relEntities));
        } catch (Throwable e) {
            log.error("同步添加项目成员失败nodeVo={}", nodeVo, e);
            return ResponseCode.OPER_FAIL.build();
        }
    }

    /**
     * 编辑
     * @param param
     * @return
     */
    @Override
    public ResultVo edit(boolean force, NodeUserEditParam param) {
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getById(param.getId(), NodeUserRelEntity.class);
        if (nodeUserRelEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(nodeUserRelEntity.getNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!force && !nodehelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            log.info("用户{}在节点{}修改成员，用户不是当前节点或上级节点管理员", param.getAccount(), nodeEntity.getId());
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (!NodeTypeEnum.getEnum(nodeEntity.getType()).supportNodeUserType(param.getType())) {
            log.info("用户{}在节点{}修改用户{}为{}类型成员，节点类型{}不支持", param.getAccount(), nodeEntity.getId(), nodeUserRelEntity.getAccount(), param.getType(), nodeEntity.getType());
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeUserRelEntity entity = new NodeUserRelEntity();
        entity.setId(param.getId());
        entity.setType(param.getType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        if (NodeTypeEnum.PRO_TYPE.getCode().equals(nodeEntity.getType())) {
            entity.setTester(param.getTester());
        }
        boolean result = nodeUserRelDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo delete(NodeUserDeleteParam param) {
        NodeUserRelEntity nodeUserRelEntity = null;
        NodeEntity nodeEntity = null;
        if (param.getId() != null) {
            nodeUserRelEntity = nodeUserRelDao.getById(param.getId(), NodeUserRelEntity.class);
            if (nodeUserRelEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            nodeEntity = nodeDao.getById(nodeUserRelEntity.getNodeId(), NodeEntity.class);
            if (nodeEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        } else {
            nodeEntity = nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
            if (nodeEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            UserEntity userEntity = userDao.getOneByAccount(param.getDelAcc(), param.getDelUserType());
            if (userEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            nodeUserRelEntity = nodeUserRelDao.getOneByNodeIdAndUserId(nodeEntity.getId(), userEntity.getId(), null);
            if (nodeUserRelEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
        }
        if (!nodehelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            log.info("用户{}在节点{}删除成员，用户不是当前节点或上级节点管理员", param.getAccount(), nodeEntity.getId());
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        nodeUserRelEntity.setUpdaterAcc(param.getAccount());
        nodeUserRelEntity.setUpdaterId(param.getUserId());
        nodeUserRelEntity.setUpdaterType(param.getUserType());
        boolean result = nodeUserRelDao.deleteById(nodeUserRelEntity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        proNodeChangeHelper.change(NodeChangeEnum.DEL, NodeUtil.toVo(nodeEntity));
        return ResponseCode.SUCCESS.build();
    }

}
