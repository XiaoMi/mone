package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.gateway.manager.bo.user.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.UserInfo;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.impl.UserV1Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bench/user")
@HttpApiModule(value = "UserController", apiController = UserController.class)
public class UserController {

    @Autowired
    UserV1Service userService;

    @ResponseBody
    @RequestMapping("/getUserInfo")
    @HttpApiDoc(value = "/api/bench/user/getUserInfo", apiName = "获取用户信息", method = MiApiRequestMethod.POST, description = "获取用户信息")
    public Result<UserInfo> userInfo(HttpServletRequest request) {
        try {
            return Result.success(userService.getUserInfo());
        } catch (Exception e) {
            log.error("UserController.userInfo", e);
            return Result.fail(CommonError.UnknownError);
        }
    }

    @RequestMapping(value = "/getAllPartnerList", method = RequestMethod.GET)
    @HttpApiDoc(value = "/api/bench/user/getAllPartnerList", apiName = "获取用户列表", method = MiApiRequestMethod.POST, description = "获取用户列表")
    public Result<List<UserInfoVo>> getAllPartnerList(@RequestParam("username") String username,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws IOException {
        if (username.length() < 3) {
            return Result.fail(-1, "请输入三个以上字母搜索");
        }
        AuthUserVo userVo = UserUtil.getUser();
        List<UserInfoVo> accounts = userService.qryUserList(username, null, userVo);
        return Result.success(accounts);
    }

}
