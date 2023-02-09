package com.xiaomi.mone.tpc.user;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.enums.UserStatusEnum;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.dao.impl.UserDao;
import com.xiaomi.mone.tpc.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Cache cache;

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
