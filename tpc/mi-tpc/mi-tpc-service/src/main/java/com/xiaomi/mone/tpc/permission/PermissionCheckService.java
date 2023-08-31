package com.xiaomi.mone.tpc.permission;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.RoleStatusEnum;
import com.xiaomi.mone.tpc.common.param.TpcCheckParam;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.SystemVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.dao.impl.*;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.system.SystemService;
import com.xiaomi.mone.tpc.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class PermissionCheckService {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private UserGroupRelDao userGroupRelDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private UserNodeRoleRelDao userNodeRoleRelDao;
    @Autowired
    private SystemService systemService;
    @Autowired
    private UserService userService;
    @Autowired
    private RolePermissionRelDao rolePermissionRelDao;

    public ResultVo check(TpcCheckParam param) {
        SystemVo systemVo = systemService.getVoByToken(param.getSystem(), param.getToken());
        if (systemVo == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("系统不存在或已停用");
        }
        PermissionEntity permissionEntity = permissionDao.getOneBySystemIdAndPath(systemVo.getId(), param.getPath());
        if (permissionEntity == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("权限点不存在");
        }
        UserVo userVo = userService.register(param.getAccount(), param.getUserType());
        if (userVo == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("用户已停用");
        }
        //查询用户关联的用户组列表
        List<Long> groupIds = null;
        List<UserGroupRelEntity> userGroupRelEntities = userGroupRelDao.getListByUserId(userVo.getId());
        if (!CollectionUtils.isEmpty(userGroupRelEntities)) {
            groupIds = userGroupRelEntities.stream().map(UserGroupRelEntity::getGroupId).distinct().collect(Collectors.toList());
        }
        // 查询关联的节点列表
        NodeEntity currNode = param.getNodeId() != null ? nodeDao.getById(param.getNodeId()) : nodeDao.getOneByOutId(param.getOutIdType(), param.getOutId());
        if (currNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("租户空间不存在");
        }
        List<NodeEntity> allNodeEntitys = Lists.newArrayList();
        allNodeEntitys.add(currNode);
        List<NodeEntity> nodeEntities = nodeDao.getByIds(nodeHelper.getparentNodeIdList(currNode.getContent()));
        if (!CollectionUtils.isEmpty(nodeEntities)) {
            allNodeEntitys.addAll(nodeEntities);
        }
        List<Long> nodeIds = allNodeEntitys.stream().filter(node -> NodeTypeEnum.supportMemberNode(node.getType()))
                .filter(node -> NodeStatusEnum.ENABLE.getCode().equals(node.getStatus())).map(NodeEntity::getId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nodeIds)) {
            return ResponseCode.NO_OPER_PERMISSION.build("租户空间不可用");
        }
        // 查询关联的角色列表(no cache)
        List<UserNodeRoleRelEntity> userNodeRoleRelEntities = userNodeRoleRelDao.getListByUserIdsAndGroupIds(systemVo.getId(), nodeIds, userVo.getId(), groupIds);
        if (CollectionUtils.isEmpty(userNodeRoleRelEntities)) {
            log.info("系统{}路径{}，用户没有找到关联的角色", systemVo.getSystemName(), param.getPath(), userVo.getAccount());
            return ResponseCode.NO_OPER_PERMISSION.build("没有关联的角色");
        }
        Set<Long> roleIds = userNodeRoleRelEntities.stream().map(UserNodeRoleRelEntity::getRoleId).collect(Collectors.toSet());
        List<RoleEntity> roleEntities = getEnableRoleList(roleIds);
        if (CollectionUtils.isEmpty(roleEntities)) {
            log.info("系统{}路径{}，角色不存在或停用roleIds={}", systemVo.getSystemName(), param.getPath(), userVo.getAccount(), roleIds);
            return ResponseCode.NO_OPER_PERMISSION.build("角色不存在或停用");
        }
        //可用的角色集合
        roleIds = roleEntities.stream().map(RoleEntity::getId).collect(Collectors.toSet());
        boolean exit =  existRelRoleIdsAndPermissionId(roleIds, permissionEntity.getId());
        if (!exit) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 可用的角色列表
     * @param roleIds
     * @return
     */
    private List<RoleEntity> getEnableRoleList(Collection<Long> roleIds) {
        List<RoleEntity> roleEntities = roleDao.getByIds(roleIds);
        if (CollectionUtils.isEmpty(roleEntities)) {
            return null;
        }
        return roleEntities.stream()
                .filter(entity -> RoleStatusEnum.ENABLE.getCode().equals(entity.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 判断角色是否存在权限点
     * @param roleIds
     * @param permissionId
     * @return
     */
    public boolean existRelRoleIdsAndPermissionId(Collection<Long> roleIds, Long permissionId) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        List<RolePermissionRelEntity> rolePermissionRelEntities;
        for (Long roleId : roleIds) {
            rolePermissionRelEntities = rolePermissionRelDao.getListByRoleId(roleId);
            if (CollectionUtils.isEmpty(rolePermissionRelEntities)) {
                continue;
            }
            if (rolePermissionRelEntities.stream().anyMatch(e -> e.getPermissionId().equals(permissionId))) {
                return true;
            }
        }
        return false;
    }

}
