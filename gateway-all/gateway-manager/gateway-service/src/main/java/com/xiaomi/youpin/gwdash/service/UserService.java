/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.service;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.GWAccount;
import com.xiaomi.youpin.gwdash.bo.GroupInfoEntity;
import com.xiaomi.youpin.gwdash.dao.mapper.GwUserInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.GwUserInfo;
import com.xiaomi.youpin.gwdash.dao.model.UserInfo;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.AccountRegisterRequest;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Service(group = "${owner.dubbo.group}", interfaceClass = GWUserServiceAPI.class, timeout = 2000)
public class UserService implements GWUserServiceAPI {

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}", timeout = 4000)
    private AccountService accountService;

    @Autowired
    private GroupInfoService groupInfoService;

    @Resource
    private GwUserInfoMapper userInfoDao;

    @Autowired
    private Redis redis;

    @Resource
    private TenantComponent tenementComponent;

    private static final String LOCK_POST = "lock";
    private static final String LOCK_VALUE = "OK";

    public Account queryUserByName(String name) {
        Account account = accountService.queryUserByName(name);
        return account;
    }

    public Account queryUserById(int id) {
        Account account = accountService.queryUserById(id);
        return account;
    }

    public List<RoleBo> getRoleByProjectName(QueryRoleRequest request) {
        return accountService.getRoleByProjectName(request);
    }

    public List<RoleBo> getAllRolesByProjectName(String projectName) {
        return null;
    }

    public List<Account> getAllAccountList() {
        return accountService.getAllAccountList();
    }

    public String generateToken(Long userId) {
        return null;
    }

    public Account registerAccount(AccountRegisterRequest request) {
        return accountService.registerAccount(request);
    }

    public List<ResourceBo> getUserResource(String userName, String projectName) {
        return null;
    }

    public List<String> describeOwnerGids(String userName) {
        String gids = describeOwnerGid(userName);
        return Lists.newArrayList(gids.split("_"));
    }

    public String describeOwnerGid(String userName) {
        String tenant = tenementComponent.getTenement();
        GwUserInfo gwUserInfo = userInfoDao.queryUserByName2(userName, tenant);
        return gwUserInfo.getGids();
    }

    public List<GroupInfoEntity> describeOwnerGroupInfo(String userName) {
        String gids = describeOwnerGid(userName);
        log.debug("describeOwnerGroupInfo gids:[{}]", gids);
        List<GroupInfoEntity> groupInfoByGids = groupInfoService.getGroupInfoByGids(gids);
        log.debug("describeOwnerGroupInfo groupInfoByGids:[{}]", groupInfoByGids);
        return groupInfoByGids;
    }

    public UserInfo describeUserByUserName(String userName, String tenant) {
        GWAccount gwAccount = queryGWUserByName(userName, tenant);
        if (gwAccount == null || gwAccount.getId() == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(Integer.parseInt(gwAccount.getId() + ""));
        userInfo.setUserName(gwAccount.getUserName());
        userInfo.setPhone(gwAccount.getPhone());
        userInfo.setGid(gwAccount.getGid());

        return userInfo;
    }

    public boolean updateUser(UserInfo userInfo, String tenant) {
        GwUserInfo gwUserInfo = userInfoDao.queryUserByName2(userInfo.getUserName(), tenant);

        gwUserInfo.setUserPhone(userInfo.getPhone());
        gwUserInfo.setGids(userInfo.getGid());

        userInfoDao.updateByPrimaryKey(gwUserInfo);

        return true;
    }

    public GWAccount queryGWUserByName(String name) {
        GWAccount gwAccount = null;

        gwAccount = userInfoDao.queryAccountByName(name);
        log.info("queryGWUserByName name:[{}], gwAccount:[{}]", name, gwAccount);
        if (gwAccount == null || gwAccount.getId() == null) {
            return gwAccount;

        }

        gwAccount.setGidInfos(getGroupInfoByGids(gwAccount.getGid()));
        return gwAccount;

    }

    @Override
    public GWAccount queryGWUserByName(String name, String tenant) {
        GWAccount gwAccount = null;
        gwAccount = userInfoDao.queryAccountByName(name, tenant);
        log.info("queryGWUserByName name:[{}], gwAccount:[{}]", name, gwAccount);
        if (gwAccount == null || gwAccount.getId() == null) {
            return gwAccount;

        }
        gwAccount.setGidInfos(getGroupInfoByGids(gwAccount.getGid()));
        return gwAccount;

    }

    private List<GroupInfoEntity> getGroupInfoByGids(String gids) {
        log.info("gids : [{}]", gids);
        if (StringUtils.isEmpty(gids)) {
            return new ArrayList<>();
        }
        List<GroupInfoEntity> groupInfoByGids = groupInfoService.getGroupInfoByGids(gids);

        return groupInfoByGids;
    }

    public GWAccount queryGWUserById(int id) {
        GWAccount gwAccount = userInfoDao.queryAccountById(id);
        log.info("queryGWUserById id:[{}], gwAccount:[{}]", id, gwAccount);
        if (gwAccount == null || gwAccount.getId() == null) {
            return gwAccount;

        }

        gwAccount.setGidInfos(getGroupInfoByGids(gwAccount.getGid()));
        return gwAccount;
    }

    @Override
    public GWAccount registerAccount(String userName, String userPhone, String tenant) {
        log.info("registerAccount username:[{}], userPhone:[{}]", userName, userPhone, tenant);
        GWAccount gwAccount = null;
        if ((gwAccount = queryGWUserByName(userName, tenant)) != null) {
            return gwAccount;
        }

        // 增加锁处理
        String lockKey = userName + "-" + LOCK_POST;

        // 轮训十次获取锁
        for (int i = 0; i < 10; i++) {
            if (LOCK_VALUE.equals(redis.setNx(lockKey, userName, 1000))) {
                try {
                    if ((gwAccount = queryGWUserByName(userName, tenant)) != null) {
                        log.info("registerAccount getLock gwAccount:[{}]", gwAccount);
                        return gwAccount;
                    }
                    log.info("registerAccount getLock username:[{}], userPhone:[{}]", userName, userPhone);
                    GwUserInfo gwUserInfo = new GwUserInfo();
                    gwUserInfo.setUserName(userName);
                    gwUserInfo.setUserPhone(userPhone);
                    gwUserInfo.setGids("0");
                    gwUserInfo.setStatus((byte) 0);
                    gwUserInfo.setCreateDate(new Date());
                    gwUserInfo.setModifyDate(new Date());
                    gwUserInfo.setTenement(tenant);

                    userInfoDao.insert(gwUserInfo);

                    return queryGWUserByName(userName, tenant);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    redis.del(lockKey);
                }
            }

            try {
                // 每次轮训间隔100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return gwAccount;
    }
}
