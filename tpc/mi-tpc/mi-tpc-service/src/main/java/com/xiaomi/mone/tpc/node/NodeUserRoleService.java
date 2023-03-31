package com.xiaomi.mone.tpc.node;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.NodeUserRoleRelTypeEnum;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleAddParam;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleDeleteParam;
import com.xiaomi.mone.tpc.common.param.NodeUserRoleQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.node.util.NodeUserRoleRelUtil;
import com.xiaomi.mone.tpc.common.vo.UserNodeRoleRelVo;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.dao.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class NodeUserRoleService {

    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserNodeRoleRelDao userNodeRoleRelDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserGroupDao userGroupDao;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<UserNodeRoleRelVo>> list(NodeUserRoleQryParam param) {
        PageDataVo<UserNodeRoleRelVo> pageData = param.buildPageDataVo();
        List<UserNodeRoleRelEntity> entityList = userNodeRoleRelDao.getListByPage(param.getSystemId(), param.getNodeId(), param.getRoleId(), param.getType(), param.getMemberId(), pageData);
        List<UserNodeRoleRelVo> voList = NodeUserRoleRelUtil.toVoList(entityList);
        if (!CollectionUtils.isEmpty(voList)) {
            // 补充角色名称
            Set<Long> roleIds = voList.stream().map(UserNodeRoleRelVo::getRoleId).collect(Collectors.toSet());
            List<RoleEntity> roleEntities = roleDao.getByIds(roleIds, RoleEntity.class);
            if (roleEntities == null) {
                roleEntities = Lists.newArrayList();
            }
            Map<Long, RoleEntity> roleMap = roleEntities.stream().collect(Collectors.toMap(RoleEntity::getId,  r -> r));
            RoleEntity roleEntity = null;
            for (UserNodeRoleRelVo vo : voList) {
                roleEntity = roleMap.get(vo.getRoleId());
                if (roleEntity == null) {
                    continue;
                }
                vo.setRoleName(roleEntity.getRoleName());
            }
            //补充用户组名称
            Set<Long> groupIds = voList.stream().filter(e -> NodeUserRoleRelTypeEnum.GROUP.getCode().equals(e.getType())).map(UserNodeRoleRelVo::getUserId).collect(Collectors.toSet());
            List<UserGroupEntity> userGroupEntities = userGroupDao.getByIds(groupIds, UserGroupEntity.class);
            if (userGroupEntities == null) {
                userGroupEntities = Lists.newArrayList();
            }
            Map<Long, UserGroupEntity> groupMap = userGroupEntities.stream().collect(Collectors.toMap(UserGroupEntity::getId, e -> e));
            UserGroupEntity userGroupEntity = null;
            for (UserNodeRoleRelVo vo : voList) {
                if (!NodeUserRoleRelTypeEnum.GROUP.getCode().equals(vo.getType())) {
                    continue;
                }
                userGroupEntity = groupMap.get(vo.getUserId());
                if (userGroupEntity == null) {
                    continue;
                }
                vo.setGroupName(userGroupEntity.getGroupName());
            }
        }
        pageData.setList(voList);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<UserNodeRoleRelVo> add(NodeUserRoleAddParam param) {
        NodeEntity nodeEntity = nodeDao.getById(param.getNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        //检查重复授权
        List<UserNodeRoleRelEntity> userNodeRoleRelEntities = userNodeRoleRelDao.getListByNodeIdAndMemberIdAndRoleIds(param.getNodeId(), param.getMemberId(), param.getType(), param.getRoleIds());
        if (!CollectionUtils.isEmpty(userNodeRoleRelEntities)) {
            List<Long> existRoleIds = userNodeRoleRelEntities.stream().map(UserNodeRoleRelEntity::getRoleId).collect(Collectors.toList());
            param.getRoleIds().removeAll(existRoleIds);
            if (param.getRoleIds().isEmpty()) {
                return ResponseCode.SUCCESS.build();
            }
        }
        UserNodeRoleRelEntity entity = new UserNodeRoleRelEntity();
        entity.setNodeId(nodeEntity.getId());
        entity.setNodeType(nodeEntity.getType());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterType(param.getUserType());
        entity.setCreaterAcc(param.getAccount());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        if (NodeUserRoleRelTypeEnum.GROUP.getCode().equals(param.getType())) {
            UserGroupEntity userGroupEntity = userGroupDao.getById(param.getMemberId(), UserGroupEntity.class);
            if (userGroupEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            entity.setType(NodeUserRoleRelTypeEnum.GROUP.getCode());
            entity.setUserId(userGroupEntity.getId());
        } else {
            UserEntity userEntity = userDao.getById(param.getMemberId(), UserEntity.class);
            if (userEntity == null) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            entity.setType(NodeUserRoleRelTypeEnum.USER.getCode());
            entity.setUserId(userEntity.getId());
            entity.setUserType(userEntity.getType());
            entity.setAccount(userEntity.getAccount());
        }
        List<RoleEntity> roleEntitys = roleDao.getByIds(param.getRoleIds(), RoleEntity.class);
        if (CollectionUtils.isEmpty(roleEntitys) || roleEntitys.size() != param.getRoleIds().size()) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        List<Long> allNodeIds = nodeHelper.getparentNodeIdList(nodeEntity.getContent());
        allNodeIds.add(nodeEntity.getId());
        List<UserNodeRoleRelEntity> entities = Lists.newArrayList();
        for (RoleEntity roleEntity : roleEntitys) {
            if (!allNodeIds.contains(roleEntity.getNodeId())) {
                return ResponseCode.OPER_ILLEGAL.build();
            }
            UserNodeRoleRelEntity newEntity = new UserNodeRoleRelEntity();
            BeanUtils.copyProperties(entity, newEntity);
            newEntity.setRoleId(roleEntity.getId());
            newEntity.setSystemId(roleEntity.getSystemId());
            entities.add(newEntity);
        }
        boolean result = userNodeRoleRelDao.batchInsert(entities);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(NodeUserRoleRelUtil.toVo(entity));
    }

    /**
     * 删除
     * @param param
     * @return
     */
    public ResultVo delete(NodeUserRoleDeleteParam param) {
        UserNodeRoleRelEntity userNodeRoleRelEntity = userNodeRoleRelDao.getById(param.getId(), UserNodeRoleRelEntity.class);
        if (userNodeRoleRelEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        NodeEntity nodeEntity = nodeDao.getById(userNodeRoleRelEntity.getNodeId(), NodeEntity.class);
        if (nodeEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        if (!nodeHelper.isMgrOrSuperMgr(param.getUserId(), nodeEntity)) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        userNodeRoleRelEntity.setUpdaterAcc(param.getAccount());
        userNodeRoleRelEntity.setUpdaterId(param.getUserId());
        userNodeRoleRelEntity.setUpdaterType(param.getUserType());
        boolean result = userNodeRoleRelDao.deleteById(userNodeRoleRelEntity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

}
