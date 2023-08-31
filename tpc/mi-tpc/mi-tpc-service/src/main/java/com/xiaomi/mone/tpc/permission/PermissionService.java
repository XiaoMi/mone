package com.xiaomi.mone.tpc.permission;

import com.xiaomi.mone.tpc.common.param.PermissionAddParam;
import com.xiaomi.mone.tpc.common.param.PermissionDeleteParam;
import com.xiaomi.mone.tpc.common.param.PermissionEditParam;
import com.xiaomi.mone.tpc.common.param.PermissionQryParam;
import com.xiaomi.mone.tpc.dao.entity.PermissionEntity;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.dao.impl.PermissionDao;
import com.xiaomi.mone.tpc.dao.impl.RolePermissionRelDao;
import com.xiaomi.mone.tpc.dao.impl.SystemDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.permission.util.PermissionUtil;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.PermissionVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
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
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class PermissionService {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private NodeHelper nodeHelper;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private RolePermissionRelDao rolePermissionRelDao;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public ResultVo<PageDataVo<PermissionVo>> list(PermissionQryParam param) {
        PageDataVo<PermissionVo> pageData = param.buildPageDataVo();
        List<PermissionEntity> permissionEntityList = permissionDao.getListByPage(param.getSystemId(), param.getPermissionName(), param.getPath(), pageData);
        List<PermissionVo> permissionVos = PermissionUtil.toVoList(permissionEntityList);
        if (!CollectionUtils.isEmpty(permissionVos)) {
            Set<Long> systemIds = permissionVos.stream().map(PermissionVo::getSystemId).collect(Collectors.toSet());
            List<SystemEntity> systemEntityList = systemDao.getByIds(systemIds, SystemEntity.class);
            if (!CollectionUtils.isEmpty(systemEntityList)) {
                Map<Long, SystemEntity> systemEntityMap = systemEntityList.stream().collect(Collectors.toMap(SystemEntity::getId, e -> e));
                permissionVos.stream().forEach(permissionVo -> {
                    if (!systemEntityMap.containsKey(permissionVo.getSystemId())) {
                        return;
                    }
                    permissionVo.setSystemName(systemEntityMap.get(permissionVo.getSystemId()).getSystemName());
                });
            }
        }
        pageData.setList(permissionVos);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 单个查询
     * @param param
     * @return
     */
    public ResultVo<PermissionVo> get(PermissionQryParam param) {
        PermissionEntity permissionEntity = permissionDao.getById(param.getId(), PermissionEntity.class);
        if (permissionEntity == null) {
            return ResponseCode.SUCCESS.build();
        }
        SystemEntity systemEntity = systemDao.getById(permissionEntity.getSystemId(), SystemEntity.class);
        if (systemEntity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        PermissionVo permissionVo = PermissionUtil.toVo(permissionEntity);
        permissionVo.setSystemName(systemEntity.getSystemName());
        return ResponseCode.SUCCESS.build(permissionVo);
    }

    /**
     * 添加
     * @param param
     * @return
     */
    public ResultVo<PermissionVo> add(PermissionAddParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        PermissionEntity permissionEntity = permissionDao.getOneBySystemIdAndPath(param.getSystemId(), param.getPath());
        if (permissionEntity != null) {
            return ResponseCode.OPER_FAIL.build("同一个系统下，path重复");
        }
        SystemEntity systemEntity = systemDao.getById(param.getSystemId(), SystemEntity.class);
        if (systemEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        PermissionEntity entity = new PermissionEntity();
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        entity.setSystemId(systemEntity.getId());
        entity.setPermissionName(param.getPermissionName());
        entity.setPath(param.getPath());
        boolean result = permissionDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build(PermissionUtil.toVo(entity));
    }

    /**
     * 编辑
     * @param param
     * @return
     */
    public ResultVo edit(PermissionEditParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        PermissionEntity permissionEntity = permissionDao.getById(param.getId(), PermissionEntity.class);
        if (permissionEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        String oldPath = permissionEntity.getPath();
        PermissionEntity existPermissionEntity = permissionDao.getOneBySystemIdAndPath(permissionEntity.getSystemId(), param.getPath());
        if (existPermissionEntity != null && !existPermissionEntity.getId().equals(permissionEntity.getId())) {
            return ResponseCode.OPER_FAIL.build("同一个系统下，path重复");
        }
        permissionEntity.setDesc(param.getDesc());
        permissionEntity.setUpdaterId(param.getUserId());
        permissionEntity.setUpdaterAcc(param.getAccount());
        permissionEntity.setUpdaterType(param.getUserType());
        permissionEntity.setPermissionName(param.getPermissionName());
        permissionEntity.setPath(param.getPath());
        boolean result = permissionDao.updateById(oldPath, permissionEntity);
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
    public ResultVo delete(PermissionDeleteParam param) {
        if (!nodeHelper.isTopMgr(param.getUserId())) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        PermissionEntity permissionEntity = permissionDao.getById(param.getId(), PermissionEntity.class);
        if (permissionEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        permissionEntity.setUpdaterAcc(param.getAccount());
        permissionEntity.setUpdaterId(param.getUserId());
        permissionEntity.setUpdaterType(param.getUserType());
        boolean result = permissionDao.deleteById(permissionEntity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        rolePermissionRelDao.deleteByPermissionId(permissionEntity.getId());
        return ResponseCode.SUCCESS.build();
    }

}
