package com.xiaomi.mone.tpc.system;

import com.xiaomi.mone.tpc.common.enums.FlagTypeEnum;
import com.xiaomi.mone.tpc.common.enums.SystemStatusEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.util.MD5Util;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.dao.entity.*;
import com.xiaomi.mone.tpc.dao.impl.*;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class SystemService implements SystemHelper {

    @Autowired
    private SystemDao systemDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private FlagDao flagDao;
    @Autowired
    private UserDao userDao;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<SystemVo>> list(SystemQryParam param) {
        PageDataVo<SystemVo> pageData = param.buildPageDataVo();
        if (nodeHelper.isTopMgr(param.getUserId())) {
            List<SystemEntity> entityList = systemDao.getListByPage(param.getSystemName(), param.getStatus(), pageData);
            pageData.setList(SystemUtil.toVoList(entityList));
        } else {
            List<SystemEntity> entityList = systemDao.getListByPage(param.getUserId(), param.getSystemName(), param.getStatus(), pageData);
            pageData.setList(SystemUtil.toVoList(entityList));
        }
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<SystemVo> getByCond(SystemQryParam param) {
        SystemEntity entity = null;
        if (param.getId() != null) {
            entity = systemDao.getById(param.getId(), SystemEntity.class);
        } else if (!StringUtils.isEmpty(param.getSystemName())) {
            entity = systemDao.getOneByName(param.getSystemName());
            if (entity != null && param.getStatus() != null && !entity.getStatus().equals(param.getStatus())) {
                return ResponseCode.SUCCESS.build();
            }
        }
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        SystemVo systemVo = SystemUtil.toVo(entity);
        return ResponseCode.SUCCESS.build(systemVo);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<SystemVo> get(SystemQryParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        SystemEntity entity = systemDao.getById(param.getId(), SystemEntity.class);
        SystemVo systemVo = SystemUtil.toVo(entity);
        List<FlagEntity> flagEntitys = flagDao.getListByNodeId(param.getId(), FlagTypeEnum.SYS_MGR.getCode());
        if (!CollectionUtils.isEmpty(flagEntitys)) {
            List<UserVo> userVos = flagEntitys.stream().map(flag -> {
                UserVo userVo = new UserVo();
                userVo.setId(Long.parseLong(flag.getFlagKey()));
                userVo.setAccount(flag.getFlagName());
                userVo.setType(Integer.parseInt(flag.getFlagVal()));
                userVo.setCreaterId(flag.getCreaterId());
                userVo.setCreaterAcc(flag.getCreaterAcc());
                userVo.setCreaterType(flag.getCreaterType());
                userVo.setUpdaterId(flag.getUpdaterId());
                userVo.setUpdaterAcc(flag.getUpdaterAcc());
                userVo.setUpdaterType(flag.getUpdaterType());
                userVo.setCreateTime(flag.getCreateTime().getTime());
                userVo.setUpdateTime(flag.getUpdateTime().getTime());
                return userVo;
            }).collect(Collectors.toList());
            systemVo.setUserVos(userVos);
        }
        return ResponseCode.SUCCESS.build(systemVo);
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<SystemVo> add(SystemAddParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        if (param.getSystemName().contains("/")) {
            return ResponseCode.OPER_FAIL.build("名称含有非法字符");
        }
        SystemEntity entity = systemDao.getOneByName(param.getSystemName());
        if (entity != null) {
            return ResponseCode.OPER_FAIL.build("系统名称重复");
        }
        entity = new SystemEntity();
        entity.setSystemName(param.getSystemName());
        entity.setDesc(param.getDesc());
        entity.setSystemToken(createSysToken(param.getSystemName(), param.getAccount()));
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(param.getStatus());
        boolean result = systemDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(SystemUtil.toVo(entity));
    }

    /**
     * 编辑
     * @param param
     * @return
     */
    public ResultVo<SystemVo> edit(SystemEditParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        if (param.getSystemName().contains("/")) {
            return ResponseCode.OPER_FAIL.build("名称含有非法字符");
        }
        SystemEntity entity = systemDao.getOneByName(param.getSystemName());
        if (entity != null && !param.getId().equals(entity.getId())) {
            return ResponseCode.OPER_FAIL.build("系统名称重复");
        }
        entity = systemDao.getById(param.getId(), SystemEntity.class);
        //清除缓存适用
        String oldSystemName = entity.getSystemName();
        entity.setSystemName(param.getSystemName());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(param.getStatus());
        boolean result = systemDao.updateById(oldSystemName, entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 状态变更
     * @param param
     * @return
     */
    public ResultVo<SystemVo> status(SystemStatParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        SystemEntity entity = systemDao.getById(param.getId(), SystemEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setStatus(param.getStatus());
        boolean result = systemDao.updateById(entity.getSystemName(), entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * token变更
     * @param param
     * @return
     */
    public ResultVo<SystemVo> token(SystemTokenParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        SystemEntity entity = systemDao.getById(param.getId(), SystemEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setSystemToken(createSysToken(entity.getSystemName(), param.getAccount()));
        boolean result = systemDao.updateById(entity.getSystemName(), entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 删除系统
     * @param param
     * @return
     */
    public ResultVo<SystemVo> delete(SystemDeleteParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        SystemEntity entity = systemDao.getById(param.getId(), SystemEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        PermissionEntity permissionEntity = permissionDao.getOneBySystemId(entity.getId());
        if (permissionEntity != null) {
            return ResponseCode.OPER_FAIL.build("存在关联的权限点");
        }
        RoleEntity roleEntity = roleDao.getOneBySystemId(entity.getId());
        if (roleEntity != null) {
            return ResponseCode.OPER_FAIL.build("存在关联的角色");
        }
        if (!flagDao.deleteByNodeId(param.getId(), FlagTypeEnum.SYS_MGR.getCode())) {
            return ResponseCode.OPER_FAIL.build();
        }
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        boolean result = systemDao.deleteById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }


    /**
     * 添加用户
     * @param param
     * @return
     */
    public ResultVo addUser(SystemUserParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        SystemEntity entity = systemDao.getById(param.getId(), SystemEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        UserEntity userEntity = userDao.getById(param.getOperUserId(), UserEntity.class);
        if (userEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        //已经存在
        FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getOperUserId().toString());
        if (flagEntity != null) {
            return ResponseCode.SUCCESS.build();
        }
        flagEntity = new FlagEntity();
        flagEntity.setFlagName(userEntity.getAccount());
        flagEntity.setCreaterId(param.getUserId());
        flagEntity.setCreaterAcc(param.getAccount());
        flagEntity.setCreaterType(param.getUserType());
        flagEntity.setUpdaterId(param.getUserId());
        flagEntity.setUpdaterAcc(param.getAccount());
        flagEntity.setUpdaterType(param.getUserType());
        flagEntity.setType(FlagTypeEnum.SYS_MGR.getCode());
        flagEntity.setParentId(param.getId());
        flagEntity.setFlagKey(userEntity.getId().toString());
        flagEntity.setFlagVal(userEntity.getType().toString());
        if (!flagDao.insert(flagEntity)) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 删除用户
     * @param param
     * @return
     */
    public ResultVo delUser(SystemUserParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getUserId().toString());
            if (flagEntity == null) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        //不存在
        FlagEntity flagEntity = flagDao.getOneByFlagKey(param.getId(), FlagTypeEnum.SYS_MGR.getCode(), param.getOperUserId().toString());
        if (flagEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        if (!flagDao.deleteById(flagEntity)) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }


    @Override
    public SystemVo getVoByToken(String system, String token) {
        SystemEntity entity = systemDao.getOneByName(system);
        if (entity == null || !SystemStatusEnum.ENABLE.getCode().equals(entity.getStatus())) {
            log.warn("系统不存在或停用system={}, token={}, entity={}", system, token, entity);
            return null;
        }
        if (!entity.getSystemToken().equals(token)) {
            log.warn("系统token错误system={}, token={}, entity={}", system, token, entity);
            return null;
        }
        return SystemUtil.toVo(entity);
    }

    /**
     * 产生系统token
     * @param systemName
     * @param userAccount
     * @return
     */
    @Override
    public String createSysToken(String systemName, String userAccount) {
        StringBuilder builder = new StringBuilder();
        builder.append(systemName);
        builder.append(userAccount);
        builder.append(System.currentTimeMillis());
        return MD5Util.md5(builder.toString());
    }
}
