package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.common.param.UserGroupAddParam;
import com.xiaomi.mone.tpc.common.param.UserGroupDeleteParam;
import com.xiaomi.mone.tpc.common.param.UserGroupEditParam;
import com.xiaomi.mone.tpc.common.param.UserGroupQryParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserGroupVo;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupRelEntity;
import com.xiaomi.mone.tpc.dao.impl.UserGroupDao;
import com.xiaomi.mone.tpc.dao.impl.UserGroupRelDao;
import com.xiaomi.mone.tpc.node.NodeHelper;
import com.xiaomi.mone.tpc.user.util.UserGroupUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class UserGroupService implements UserGroupHelper{

    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserGroupRelDao userGroupRelDao;
    @Autowired
    private NodeHelper nodeHelper;

    public ResultVo<UserGroupVo> add(UserGroupAddParam param) {
        UserGroupEntity entity = userGroupDao.getOneByName(param.getGroupName());
        if (entity != null) {
            return ResponseCode.OPER_FAIL.build("名称重复");
        }
        entity = new UserGroupEntity();
        entity.setGroupName(param.getGroupName());
        entity.setDesc(param.getDesc());
        entity.setCreaterId(param.getUserId());
        entity.setCreaterAcc(param.getAccount());
        entity.setCreaterType(param.getUserType());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        //新增用户组
        boolean result = userGroupDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        UserGroupRelEntity relEntity = new UserGroupRelEntity();
        relEntity.setUserId(param.getUserId());
        relEntity.setAccount(param.getAccount());
        relEntity.setUserType(param.getUserType());
        relEntity.setGroupId(entity.getId());
        relEntity.setCreaterId(param.getUserId());
        relEntity.setCreaterAcc(param.getAccount());
        relEntity.setCreaterType(param.getUserType());
        relEntity.setUpdaterId(param.getUserId());
        relEntity.setUpdaterAcc(param.getAccount());
        relEntity.setUpdaterType(param.getUserType());
        //把当前用户添加到用户组-非强依赖
        userGroupRelDao.insert(relEntity);
        return ResponseCode.SUCCESS.build(UserGroupUtil.toVo(entity));
    }

    public ResultVo edit(UserGroupEditParam param) {
        UserGroupEntity entity = userGroupDao.getOneByName(param.getGroupName());
        if (entity != null && !param.getId().equals(entity.getId())) {
            return ResponseCode.OPER_FAIL.build("名称重复");
        }
        entity = userGroupDao.getById(param.getId(), UserGroupEntity.class);
        if (entity == null) {
            return ResponseCode.NO_OPER_PERMISSION.build();
        }
        //是否成员
        UserGroupRelEntity relEntity = userGroupRelDao.getByGroupIdAndUserId(entity.getId(), param.getUserId());
        if (relEntity == null) {
            //是否超管
            if (!nodeHelper.isTopMgr(param.getUserId())) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        entity = new UserGroupEntity();
        entity.setId(param.getId());
        entity.setGroupName(param.getGroupName());
        entity.setDesc(param.getDesc());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterType(param.getUserType());
        boolean result = userGroupDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    @Override
    public List<UserGroupVo> getMyUserGroupList(Long userId) {
        PageDataVo pageData = new PageDataVo();
        pageData.setPageSize(100);
        pageData.setPage(1);
        pageData.setPager(false);
        List<UserGroupEntity> userGroupEntities = userGroupDao.getListByPage(userId, null, null, pageData);
        return UserGroupUtil.toVoList(userGroupEntities);
    }

    @Override
    public List<Long> getMyUserGroupIds(Long userId) {
        List<UserGroupVo> userGroupVos = getMyUserGroupList(userId);
        if (CollectionUtils.isEmpty(userGroupVos)) {
            return null;
        }
        return userGroupVos.stream().map(UserGroupVo::getId).collect(Collectors.toList());
    }

    public ResultVo<PageDataVo<UserGroupVo>> list(UserGroupQryParam param) {
        PageDataVo<UserGroupVo> pageData = param.buildPageDataVo();
        List<UserGroupEntity> entityList = null;
        if (nodeHelper.isTopMgr(param.getUserId())) {
            entityList = userGroupDao.getListByPage(param.getGroupName(), pageData);
        } else {
            entityList = userGroupDao.getListByPage(param.getUserId(), param.getGroupName(), null, pageData);
        }
        pageData.setList(UserGroupUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

    public ResultVo<UserGroupVo> get(UserGroupQryParam param) {
        UserGroupEntity entity = userGroupDao.getById(param.getId(), UserGroupEntity.class);
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        return ResponseCode.SUCCESS.build(UserGroupUtil.toVo(entity));
    }

    public ResultVo delete(UserGroupDeleteParam param) {
        UserGroupEntity entity = userGroupDao.getById(param.getId(), UserGroupEntity.class);
        if (entity == null) {
            return ResponseCode.SUCCESS.build();
        }
        //是否成员
        UserGroupRelEntity relEntity = userGroupRelDao.getByGroupIdAndUserId(entity.getId(), param.getUserId());
        if (relEntity == null) {
            //是否超管
            if (!nodeHelper.isTopMgr(param.getUserId())) {
                return ResponseCode.NO_OPER_PERMISSION.build();
            }
        }
        entity.setUpdaterAcc(param.getAccount());
        entity.setUpdaterId(param.getUserId());
        entity.setUpdaterType(param.getUserType());
        boolean result = userGroupDao.deleteById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        //尽量删除关系
        userGroupRelDao.deleteByGroupId(param.getId());
        return ResponseCode.SUCCESS.build();
    }

}
