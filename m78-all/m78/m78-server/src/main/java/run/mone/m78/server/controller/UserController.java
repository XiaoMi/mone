package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.user.UserConfig;
import run.mone.m78.api.bo.user.UserDTO;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.bo.user.UserInfoVo;
import run.mone.m78.service.dao.entity.UserConfigPo;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.user.UserService;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.*;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/user")
@HttpApiModule(value = "UserController", apiController = UserController.class)
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private LoginService loginService;


    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取个人信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUserInfo")
    public Result<UserInfoVo> getUserInfo(HttpServletResponse response) throws IOException {
        UserInfoVo userInfo = userService.getUserInfo();
        if (null == userInfo) {
            log.warn("[UserController.getUserInfo] current user not have valid account info in session");
            return Result.fail(STATUS_BAD_REQUEST, "user not login in");
        }

        //访问首页时，就直接创建空间
        Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
            SessionAccount account = new SessionAccount();
            account.setUsername(userInfo.getUsername());
            account.setUserType(userInfo.getUserType());
            workspaceService.getOrCreateMyWorkspace(account);
        });

        return Result.success(userInfo);
    }


    //获取用户配置(class)
    @RequestMapping("/getUserConfig")
    @ResponseBody
    public Result<UserConfig> getUserConfig(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String userName = account.getUsername();
        UserConfig config = userService.getUserConfig(userName);
        if (config == null) {
            log.warn("[UserController.getDefaultConfig] No default config found");
            return Result.fail(STATUS_BAD_REQUEST, "No default config found");
        }
        return Result.success(config);
    }

    //更新用户配置(class)
    @RequestMapping("/updateUserConfig")
    @ResponseBody
    public Result<Void> updateUserConfig(HttpServletRequest request, @RequestBody UserConfigPo userConfigPo) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("[UserController.updateUserConfig] User not logged in");
            return Result.fail(STATUS_BAD_REQUEST, "User not logged in");
        }
        if (null == userConfigPo.getModelConfig()) {
            return Result.fail(STATUS_BAD_REQUEST, "config is empty");
        }
        try {
            userConfigPo.setUserName(account.getUsername());
            userService.updateOrInsertUserConfig(userConfigPo);
            return Result.success(null);
        } catch (Exception e) {
            log.error("[UserController.updateUserConfig] Error updating user config", e);
            return Result.fail(STATUS_INTERNAL_ERROR, "Error updating user config");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public Result<List<UserDTO>> getUser(HttpServletRequest request,
                                         HttpServletResponse response, @RequestParam(value = "keyword", required = false) String keyword) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return Result.success(userService.queryUserNames(account, keyword));
    }

    @ResponseBody
    @RequestMapping(value = "/getUserMiId", method = RequestMethod.GET)
    public Result<Long> getUserMiId(HttpServletRequest request){
        SessionAccount account = loginService.getAccountFromSession(request);
        Long miId = userService.getMiId(account);
        //测试环境数据不全，未转正的可能查无此人(灬ꈍ ꈍ灬)
        if (miId == null){
            return Result.fail(STATUS_NOT_FOUND,"MiId is not found, please check whether your account is bound with MiId");
        }
        return Result.success(miId);
    }
}
