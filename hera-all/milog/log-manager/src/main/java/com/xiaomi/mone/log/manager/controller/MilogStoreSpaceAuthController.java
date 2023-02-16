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
 * @description store 授权
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
