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

    public ResultVo<PageDataVo<UserVo>> list(UserQryParam param, boolean isFront) {
        PageDataVo<UserVo> pageData = param.buildPageDataVo();
        List<UserEntity> entityList = userDao.getListByPage(param.getUserAcc(), param.getType(), param.getStatus(), pageData);
        pageData.setList(UserUtil.toVoList(entityList, isFront));
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
        UserVo userVo = UserUtil.toVo(entity, true);
        if (nodeUserRelEntity != null) {
            userVo.setTopMgr(true);
        }
        return ResponseCode.SUCCESS.build(userVo);
    }

    public ResultVo<UserVo> get(UserQryParam param) {
        UserEntity entity = userDao.getById(param.getId(), UserEntity.class);
        return ResponseCode.SUCCESS.build(UserUtil.toVo(entity, true));
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

    public ResultVo<UserVo> registerV2(String account, Integer userType, String content, Integer initUserStat) {
        UserEntity entity = userDao.getOneByAccount(account, userType);
        if (entity != null) {
            if (UserStatusEnum.DISABLE.getCode().equals(entity.getStatus())) {
                log.warn("用户已停用 entity={}", entity);
                return ResponseCode.USER_DISABLED.build("账号是停用状态，请联系管理员启用");
            }
            if (StringUtils.isNotBlank(content)) {
                userDao.updateById(entity.updateForContent(content));
            }
            return ResponseCode.SUCCESS.build(UserUtil.toVo(entity, false));
        }
        //加锁处理，防止重复注册
        Key key = Key.build(ModuleEnum.USER_ACC_TYPE_LOCK).keys(account, userType);
        boolean lock = cache.get().lock(key, 2, TimeUnit.SECONDS);
        if (!lock) {
            return ResponseCode.OPER_FAIL.build("注册用户失败，请稍后重试");
        }
        try {
            entity = userDao.getOneByAccount(account, userType);
            if (entity != null) {
                if (UserStatusEnum.DISABLE.getCode().equals(entity.getStatus())) {
                    log.warn("用户已停用 entity={}", entity);
                    return ResponseCode.USER_DISABLED.build("账号是停用状态，请联系管理员启用");
                }
                return ResponseCode.SUCCESS.build(UserUtil.toVo(entity, false));
            }
            UserVo userVo = convertUserVo(userType, account, content, initUserStat);
            entity = UserUtil.toEntity(userVo);
            boolean result = userDao.insert(entity);
            if (!result) {
                log.error("注册用户失败 entity={}", entity);
                return ResponseCode.OPER_FAIL.build("注册用户失败，请稍后重试");
            }
            if (UserStatusEnum.DISABLE.getCode().equals(initUserStat)) {
                log.warn("用户已停用 entity={}", entity);
                return ResponseCode.USER_DISABLED.build("账号是停用状态，请联系管理员启用");
            }
            return ResponseCode.SUCCESS.build(UserUtil.toVo(entity, false));
        } finally{
            cache.get().unlock(key);
        }
    }

    @Override
    public UserVo register(String account, Integer userType, String content) {
        UserEntity entity = userDao.getOneByAccount(account, userType);
        if (entity != null) {
            if (!UserStatusEnum.ENABLE.getCode().equals(entity.getStatus())) {
                log.warn("用户已停用 entity={}", entity);
                return null;
            }
            if (StringUtils.isNotBlank(content)) {
                userDao.updateById(entity.updateForContent(content));
            }
            return UserUtil.toVo(entity, false);
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
                return UserUtil.toVo(entity, false);
            }
            UserVo userVo = convertUserVo(userType, account, content, UserStatusEnum.ENABLE.getCode());
            entity = UserUtil.toEntity(userVo);
            boolean result = userDao.insert(entity);
            if (!result) {
                log.error("注册用户失败 entity={}", entity);
                return null;
            }
            return UserUtil.toVo(entity, false);
        } finally{
            cache.get().unlock(key);
        }
    }

    private UserVo convertUserVo(Integer userType, String account, String content, Integer initUserStat) {
        UserVo userVo = new UserVo();
        userVo.setType(userType);
        userVo.setAccount(account);
        userVo.setCreaterAcc(account);
        userVo.setUpdaterAcc(account);
        userVo.setCreaterType(userType);
        userVo.setUpdaterType(userType);
        userVo.setContent(content);
        userVo.setStatus(initUserStat);
        return userVo;
    }

}
