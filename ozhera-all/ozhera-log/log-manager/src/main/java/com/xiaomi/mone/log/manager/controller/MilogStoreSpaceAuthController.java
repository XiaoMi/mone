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

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.StoreSpaceAuth;
import com.xiaomi.mone.log.manager.service.impl.MilogStoreSpaceAuthServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description store authorized
 * @date 2022/7/14 16:12
 */
@Controller
public class MilogStoreSpaceAuthController {

    @Resource
    private MilogStoreSpaceAuthServiceImpl milogStoreSpaceAuthService;

    @RequestMapping(path = "/milog/store/space/auth", method = "POST")
    public Result<String> storeSpaceAuth(StoreSpaceAuth storeSpaceAuth) {
        return Result.success(milogStoreSpaceAuthService.storeSpaceAuth(storeSpaceAuth));
    }
}
