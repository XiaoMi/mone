package com.xiaomi.mone.tpc.permission;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.RoleTypeEnum;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.permission.util.PermissionUtil;
import com.xiaomi.mone.tpc.permission.util.RoleUtil;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.RoleVo;
import com.xiaomi.mone.tpc.dao.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Dao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
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
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RolePermissionRelDao rolePermissionRelDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private UserNodeRoleRelDao userNodeRoleRelDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private NodeDao nodeDao;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<RoleVo>> list(RoleQryParam param) {
        List<Long> allNodeIds = null;
        if (param.getNodeId() != null) {
            NodeEntity nodeEntity = nodeDao.getById(param.getId());
            if (nodeEntity == null) {
                return ResponseCode.OPER_FAIL.build("节点不存在或已停用");
            }
            allNodeIds = nodeHelper.getparentNodeIdList(nodeEntity.getContent());
            allNodeIds.add(nodeEntity.getId());
        }
        PageDataVo<RoleVo> pageData = param.buildPageDataVo();
        List<RoleEntity> roleEntities = roleDao.getListByPage(allNodeIds, param.getSystemId(), param.getRoleName(), param.getStatus(), pageData);
        if (!CollectionUtils.isEmpty(roleEntities)) {
            //当前节点的角色或上级节点可继承的角色
            roleEntities = roleEntities.stream().filter(roleEntity -> roleEntity.getNodeId().equals(param.getId()) || RoleTypeEnum.EXTENDS.getCode().equals(roleEntity.getType())).collect(Collectors.toList());
        }
        List<RoleVo> roleVos = RoleUtil.toVoList(roleEntities);
        if (!CollectionUtils.isEmpty(roleVos)) {
            Set<Long> systemIds = roleVos.stream().map(RoleVo::getSystemId).collect(Collectors.toSet());
            List<SystemEntity> systemEntityList = systemDao.getByIds(systemIds, SystemEntity.class);
            if (!CollectionUtils.isEmpty(systemEntityList)) {
                Map<Long, SystemEntity> systemEntityMap = systemEntityList.stream().collect(Collectors.toMap(SystemEntity::getId, e -> e));
                roleVos.stream().forEach(roleVo -> {
                    if (!systemEntityMap.containsKey(roleVo.getSystemId())) {
                        return;
                    }
                    roleVo.setSystemName(systemEntityMap.get(roleVo.getSystemId()).getSystemName());
                });
            }
        }
        pageData.setList(roleVos);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<RoleVo> get(RoleQryParam param) {
        RoleEntity roleEntity = roleDao.getById(param.getId(), RoleEntity.class);
        if (roleEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        SystemEntity systemEntity = systemDao.getById(roleEntity.getSystemId(), SystemEntity.class);
        if (systemEntity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        NodeEntity curNode = nodeDao.getById(roleEntity.getNodeId());
        RoleVo roleVo = RoleUtil.toVo(roleEntity);
        roleVo.setSystemName(systemEntity.getSystemName());
        roleVo.setNodeName(curNode != null ? curNode.getNodeName() : null);
        List<RolePermissionRelEntity> rolePermissionRelEntities = rolePermissionRelDao.getListByRoleId(roleEntity.getId());
        if (!CollectionUtils.isEmpty(rolePermissionRelEntities)) {
            Set<Long> permissionIds = rolePermissionRelEntities.stream().map(RolePermissionRelEntity::getPermissionId).collect(Collectors.toSet());
            List<PermissionEntity> permissionEntities = permissionDao.getByIds(permissionIds, PermissionEntity.class);
            roleVo.setPermissionVoList(PermissionUtil.toVoList(permissionEntities));
        }
        return ResponseCode.SUCCESS.build(roleVo);
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<RoleVo> add(RoleAddParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        NodeEntity curNode = nodeDao.getById(param.getNodeId());
        if (curNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("节点不存在");
        }
        SystemEntity systemEntity = systemDao.getById(param.getSystemId(), SystemEntity.class);
        if (systemEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        List<PermissionEntity> permissionEntitys = permissionDao.getListBySystemIdAndIds(param.getSystemId(), param.getPermissionIds());
        if (!CollectionUtils.isEmpty(param.getPermissionIds()) && (permissionEntitys == null || permissionEntitys.size() != param.getPermissionIds().size())) {
                return ResponseCode.OPER_ILLEGAL.build();
        }
        final List<RolePermissionRelEntity> rolePermissionRelEntities = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(permissionEntitys)) {
            permissionEntitys.stream().forEach(p -> {
                RolePermissionRelEntity entity = new RolePermissionRelEntity();
                entity.setCreaterId(param.getUserId());
                entity.setCreaterAcc(param.getAccount());
                entity.setCreaterType(param.getUserType());
                entity.setUpdaterId(param.getUserId());
                entity.setUpdaterAcc(param.getAccount());
                entity.setUpdaterType(param.getUserType());
                entity.setSystemId(systemEntity.getId());
                entity.setPermissionId(p.getId());
                rolePermissionRelEntities.add(entity);
            });
        }
        RoleEntity entity = new RoleEntity();
        entity.setType(param.getType());
        entity.setStatus(param.getStatus());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setSystemId(systemEntity.getId());
        entity.setRoleName(param.getRoleName());
        entity.setNodeId(curNode.getId());
        entity.setNodeType(curNode.getType());
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    roleDao.insertWithException(entity);
                    if (!CollectionUtils.isEmpty(rolePermissionRelEntities)) {
                        rolePermissionRelEntities.stream().forEach(rp -> {
                            rp.setRoleId(entity.getId());
                        });
                    }
                    rolePermissionRelDao.batchInsertWithExcp(rolePermissionRelEntities);
                }
            });
            return ResponseCode.SUCCESS.build(RoleUtil.toVo(entity));
        } catch (Throwable e) {
            log.error("角色及关系插入失败 param={}", param, e);
            return ResponseCode.OPER_FAIL.build();
        }
    }

    /**
     * 编辑
     * @param param
     * @return
     */
    public ResultVo edit(RoleEditParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        NodeEntity curNode = nodeDao.getById(param.getNodeId());
        if (curNode == null) {
            return ResponseCode.NO_OPER_PERMISSION.build("节点不存在");
        }
        RoleEntity roleEntity = roleDao.getById(param.getId(), RoleEntity.class);
        if (roleEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        List<PermissionEntity> permissionEntitys = permissionDao.getListBySystemIdAndIds(roleEntity.getSystemId(), param.getPermissionIds());
        if (!CollectionUtils.isEmpty(param.getPermissionIds()) && (permissionEntitys == null || permissionEntitys.size() != param.getPermissionIds().size())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        final List<RolePermissionRelEntity> rolePermissionRelEntities = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(permissionEntitys)) {
            permissionEntitys.stream().forEach(p -> {
                RolePermissionRelEntity entity = new RolePermissionRelEntity();
                entity.setRoleId(param.getId());
                entity.setCreaterId(param.getUserId());
                entity.setCreaterAcc(param.getAccount());
                entity.setCreaterType(param.getUserType());
                entity.setUpdaterId(param.getUserId());
                entity.setUpdaterAcc(param.getAccount());
                entity.setUpdaterType(param.getUserType());
                entity.setSystemId(roleEntity.getId());
                entity.setPermissionId(p.getId());
                rolePermissionRelEntities.add(entity);
            });
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(param.getId());
        entity.setType(param.getType());
        entity.setStatus(param.getStatus());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setRoleName(param.getRoleName());
        entity.setNodeId(curNode.getId());
        entity.setNodeType(curNode.getType());
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if(!rolePermissionRelDao.deleteByRoleId(param.getId())) {
                        throw new RuntimeException("删除角色权限关系失败roleId=" + param.getId());
                    }
                    roleDao.updateByIdWithExcption(entity);
                    rolePermissionRelDao.batchInsertWithExcp(rolePermissionRelEntities);
                }
            });
        } catch (Throwable e) {
            log.error("角色及关系更新失败 param={}", param);
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 状态变更
     * @param param
     * @return
     */
    public ResultVo status(RoleStatusParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        RoleEntity roleEntity = roleDao.getById(param.getId(), RoleEntity.class);
        if (roleEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(param.getId());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(param.getStatus());
        boolean result = roleDao.updateById(entity);
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
    public ResultVo delete(RoleDeleteParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        RoleEntity roleEntity = roleDao.getById(param.getId(), RoleEntity.class);
        if (roleEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        roleEntity.setUpdaterAcc(param.getAccount());
        roleEntity.setUpdaterId(param.getUserId());
        roleEntity.setUpdaterType(param.getUserType());
        try {
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    if(!userNodeRoleRelDao.deleteByRoleId(param.getId())) {
                        throw new RuntimeException("删除角色用户节点关系失败roleId=" + param.getId());
                    }
                    if(!rolePermissionRelDao.deleteByRoleId(param.getId())) {
                        throw new RuntimeException("删除角色权限关系失败roleId=" + param.getId());
                    }
                    if(!roleDao.deleteById(roleEntity)){
                        throw new RuntimeException("删除角色失败roleId=" + param.getId());
                    }
                }
            });
        } catch (Throwable e) {
            log.error("角色及关系删除失败 param={}", param);
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

}
