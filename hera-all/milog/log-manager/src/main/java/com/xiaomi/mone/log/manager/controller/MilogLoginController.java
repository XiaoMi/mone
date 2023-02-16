package com.xiaomi.mone.log.manager.controller;

import cn.hutool.core.bean.BeanUtil;
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
     * 获取用户信息--米盾登录后才会有
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
     * 用户退出清理信息
     *
     * @param mvcContext
     * @return
     */
    @RequestMapping(path = "/milog/user/logout", method = "get")
    public Result<String> logOut(MvcContext mvcContext) {
        mvcContext.session().invalidate();
        return Result.success("success");
    }
}
