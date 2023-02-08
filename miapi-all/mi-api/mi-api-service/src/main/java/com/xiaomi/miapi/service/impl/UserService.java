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

package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.vo.UserInfoVo;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.AccountRegisterRequest;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}", timeout = 4000)
    private AccountService accountService;

    public Account queryUserByName(String name) {
        Account account =accountService.queryUserByName(name);

        return account;
    }

    public boolean assginWorkRoleForMiApi(Long userId){
        return accountService.assginWorkRoleForMiApi(userId);
    }

    public UserInfoVo queryUserById(int id) {
        Account account = accountService.queryUserById(id);
        QueryRoleRequest request = new QueryRoleRequest();
        request.setProjectName(Consts.PROJECT_NAME);
        request.setUserName(account.getUserName());
        List<RoleBo> roleBos = accountService.getRoleByProjectName(request);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(account,userInfoVo);
        userInfoVo.setRoleBos(roleBos);
        return userInfoVo;
    }

    public Account getUserById(int id){
        return accountService.queryUserById(id);
    }

    public List<RoleBo> getRoleByProjectName(QueryRoleRequest request){
        return accountService.getRoleByProjectName(request);
    }

    public Account registerAccount(AccountRegisterRequest request) {
        return accountService.registerAccount(request);
    }

}
