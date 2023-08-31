package com.xiaomi.mone.tpc.node;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.param.NodeUserGroupAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserGroupDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserGroupQryParam;
import com.xiaomi.mone.tpc.common.vo.NodeUserGroupRelVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.NodeEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserGroupRelEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeDao;
import com.xiaomi.mone.tpc.dao.impl.NodeUserGroupRelDao;
import com.xiaomi.mone.tpc.dao.impl.UserGroupDao;
import com.xiaomi.mone.tpc.node.util.NodeUserGroupRelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 16:19
 */
@Slf4j
@Service
public class NodeUserGroupService {

    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeUserGroupRelDao nodeUserGroupRelDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private NodeHelper nodeHelper;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<NodeUserGroupRelVo>> list(NodeUserGroupQryParam param) {
        PageDataVo<NodeUserGroupRelVo> pageData = param.buildPageDataVo();
        List<NodeUserGroupRelEntity> entityList = nodeUserGroupRelDao.getListByPage(param.getNodeId(), param.getUserGroupId(), pageData);
        List<NodeUserGroupRelVo> vos = NodeUserGroupRelUtil.toVoList(entityList);
        pageData.setList(vos);
        if (!CollectionUtils.isEmpty(vos)) {
            Set<Long> userGroupIds = vos.stream().map(NodeUserGroupRelVo::getUserGroupId).collect(Collectors.toSet());
            List<UserGroupEntity> userGroupEntities = userGroupDao.getByIds(userGroupIds, UserGroupEntity.class);
            if (!CollectionUtils.isEmpty(userGroupEntities)) {
                Map<Long, UserGroupEntity> userGroupEntityMap = userGroupEntities.stream().collect(Collectors.toMap(UserGroupEntity::getId, uge -> uge));
                vos.forEach(vo -> {
                    UserGroupEntity uge = userGroupEntityMap.get(vo.getUserGroupId());
                    if (uge != null) {
                        vo.setUserGroupName(uge.getGroupName());
                    }
                });
            }
        }
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<NodeUserGroupRelVo> add(NodeUserGroupAddParam param) {
        UserGroupEntity userGroupEntity = userGroupDao.getById(param.getUserGroupId(), UserGroupEntity.class);
        if (userGroupEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build("用户组不存在或停用");
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
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            log.info("用户{}在节点{}添加用户组，用户不是当前节点或上级节点管理员", param.getAccount(), nodeEntity.getId());
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (!NodeTypeEnum.getEnum(nodeEntity.getType()).supportNodeUserType(NodeUserRelTypeEnum.MEMBER.getCode())) {
            log.info("用户{}在节点{}添加用户组，节点类型{}不支持", param.getAccount(), nodeEntity.getId(), nodeEntity.getType());
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeUserGroupRelEntity entity = nodeUserGroupRelDao.getOneByNodeIdAndUserGroupId(nodeEntity.getId(), userGroupEntity.getId());
        if (entity != null) {
            return ResponseCode.OPER_FAIL.build("用户组已添加");
        }
        entity = new NodeUserGroupRelEntity();
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setUserGroupId(userGroupEntity.getId());
        entity.setNodeId(nodeEntity.getId());
        entity.setNodeType(nodeEntity.getType());
        boolean result = nodeUserGroupRelDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(NodeUserGroupRelUtil.toVo(entity));
    }

    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo delete(NodeUserGroupDeleteParam param) {
        NodeUserGroupRelEntity nodeUserGroupRelEntity = nodeUserGroupRelDao.getById(param.getId(), NodeUserGroupRelEntity.class);
        if (nodeUserGroupRelEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(nodeUserGroupRelEntity.getNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            log.info("用户{}在节点{}删除用户组，用户不是当前节点或上级节点管理员", param.getAccount(), nodeEntity.getId());
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        nodeUserGroupRelEntity.setUpdaterAcc(param.getAccount());
        nodeUserGroupRelEntity.setUpdaterId(param.getUserId());
        nodeUserGroupRelEntity.setUpdaterType(param.getUserType());
        boolean result = nodeUserGroupRelDao.deleteById(nodeUserGroupRelEntity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

}
