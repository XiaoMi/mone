package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.springframework.stereotype.Repository;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class UserDao extends BaseDao{

    public UserEntity getOneByAccount(String account, int userType) {
        UserEntity userEntity = fetch(Cnd.cri().where().andEquals("account",account)
                .andEquals("type", userType), UserEntity.class);
        return userEntity;
    }
}
