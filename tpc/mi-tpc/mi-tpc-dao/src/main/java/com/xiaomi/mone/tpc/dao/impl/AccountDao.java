package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.AccountEntity;
import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.dao.entity.FlagEntity;
import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class AccountDao extends BaseDao{

    public AccountEntity getOneByAccount(String account, Integer type) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where()
                .andEquals("account", account)
                .andEquals("type", type);
        return fetch(sqlExpr, AccountEntity.class);
    }

}
