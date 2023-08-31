package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.param.UserQryParam;
import com.xiaomi.mone.tpc.common.param.UserStatusParam;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.impl.NodeUserRelDao;
import com.xiaomi.mone.tpc.dao.impl.UserDao;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.user.util.UserUtil;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 16:56
 */
@Slf4j
@Service
public class UserService implements UserHelper {

    @Autowired
    private UserDao userDao;
    @Autowired
    private NodeUserRelDao nodeUserRelDao;
    @Autowired
    private Cache cache;

    public ResultVo<PageDataVo<UserVo>> list(UserQryParam param) {
        PageDataVo<UserVo> pageData = param.buildPageDataVo();
        List<UserEntity> entityList = userDao.getListByPage(param.getUserAcc(), param.getType(), param.getStatus(), pageData);
        pageData.setList(UserUtil.toVoList(entityList));
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 我的信息查询
     * @param param
     * @return
     */
    public ResultVo<UserVo> my(NullParam param) {
        UserEntity entity = userDao.getById(param.getUserId(), UserEntity.class);
        NodeUserRelEntity nodeUserRelEntity = nodeUserRelDao.getOneByUserIdAndNodeType(param.getUserId(), NodeTypeEnum.TOP_TYPE.getCode(), NodeUserRelTypeEnum.MANAGER.getCode());
        UserVo userVo = UserUtil.toVo(entity);
        if (nodeUserRelEntity != null) {
            userVo.setTopMgr(true);
        }
        return ResponseCode.SUCCESS.build(userVo);
    }

    public ResultVo<UserVo> get(UserQryParam param) {
        UserEntity entity = userDao.getById(param.getId(), UserEntity.class);
        return ResponseCode.SUCCESS.build(UserUtil.toVo(entity));
    }
    public ResultVo<UserVo> status(UserStatusParam param) {
        UserEntity entity = userDao.getById(param.getId(), UserEntity.class);
        if (entity == null) {
            return ResponseCode.OPER_FAIL.build();
        }
        entity.setStatus(param.getStatus());
        boolean result = userDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }

    public UserVo getVoByAccount(String account, Integer userType) {
        if (StringUtils.isBlank(account) || UserTypeEnum.getEnum(userType) == null) {
            return null;
        }
        UserEntity entity = userDao.getOneByAccount(account, userType);
        if (entity == null || !UserStatusEnum.ENABLE.getCode().equals(entity.getStatus())) {
            log.warn("用户不存在或停用 account={}, userType={}, entity={}", account, userType, entity);
            return null;
        }
        return UserUtil.toVo(entity);
    }

    @Override
    public UserVo register(String account, Integer userType) {
        UserEntity entity = userDao.getOneByAccount(account, userType);
        if (entity != null) {
            if (!UserStatusEnum.ENABLE.getCode().equals(entity.getStatus())) {
                log.warn("用户已停用 entity={}", entity);
                return null;
            }
            return UserUtil.toVo(entity);
        }
        //加锁处理，防止重复注册
        Key key = Key.build(ModuleEnum.USER_ACC_TYPE_LOCK).keys(account, userType);
        boolean lock = cache.get().lock(key, 2, TimeUnit.SECONDS);
        if (!lock) {
            return null;
        }
        try {
            entity = userDao.getOneByAccount(account, userType);
            if (entity != null) {
                return UserUtil.toVo(entity);
            }
            UserVo userVo = convertUserVo(userType, account);
            entity = UserUtil.toEntity(userVo);
            boolean result = userDao.insert(entity);
            if (!result) {
                log.error("注册用户失败 entity={}", entity);
                return null;
            }
            return UserUtil.toVo(entity);
        } finally{
            cache.get().unlock(key);
        }
    }

    private UserVo convertUserVo(Integer userType, String account) {
        UserVo userVo = new UserVo();
        userVo.setType(userType);
        userVo.setAccount(account);
        userVo.setCreaterAcc(account);
        userVo.setUpdaterAcc(account);
        userVo.setCreaterType(userType);
        userVo.setUpdaterType(userType);
        return userVo;
    }

}
