/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.controller;

import cn.hutool.core.bean.BeanUtil;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.vo.MilogUserVo;
import com.xiaomi.mone.log.manager.user.MoneUser;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.mvc.MvcContext;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/3 14:40
 */
@Controller
public class MilogLoginController {


    /**
     * Obtain user information - only after login
     *
     * @return
     */
    @RequestMapping(path = "/", method = "get")
    public Result<MilogUserVo> queryUserInfo() {
        MoneUser currentUser = MoneUserContext.getCurrentUser();
        MilogUserVo milogUserVo = new MilogUserVo();
        if (null != currentUser) {
            BeanUtil.copyProperties(currentUser, milogUserVo, true);
        }
        return Result.success(milogUserVo);
    }

    /**
     * User exit cleanup information
     *
     * @param mvcContext
     * @return
     */
    @RequestMapping(path = "/milog/user/logout", method = "get")
    public Result<String> logOut(MvcContext mvcContext) {
        mvcContext.session().invalidate();
        MoneUserContext.clear();
        return Result.success(Config.ins().get("tpc_logout_url", ""));
    }
}
