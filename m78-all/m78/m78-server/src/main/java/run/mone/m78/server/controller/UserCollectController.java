package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.bo.user.UserCollectReq;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.user.UserCollectService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/15 16:17
 */

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/userCollect")
@HttpApiModule(value = "UserCollectController", apiController = UserCollectController.class)
public class UserCollectController {

    @Resource
    private LoginService loginService;

    @Resource
    private UserCollectService userCollectService;

    // 点击收藏操作
    @ResponseBody
    @RequestMapping(value = "/applyCollect", method = RequestMethod.POST)
    public Result<Boolean> applyCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        log.info("applyCollect req:{}",req);
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return userCollectService.applyCollect(username,req);
    }

    @ResponseBody
    @RequestMapping(value = "/isCollect", method = RequestMethod.POST)
    public Result<Boolean> isCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        log.info("isCollect username:{},req :{}",username,req);
        return userCollectService.isCollect(username,req);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteCollect", method = RequestMethod.POST)
    public Result<Boolean> deleteCollect(HttpServletRequest request, @RequestBody UserCollectReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        log.info("isCollect username:{},req :{}",username,req);
        return userCollectService.deleteCollect(username,req);
    }

}
