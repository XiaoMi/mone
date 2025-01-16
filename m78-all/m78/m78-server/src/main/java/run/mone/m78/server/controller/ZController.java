package run.mone.m78.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.ai.z.dto.PageReq;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.user.UserService;
import run.mone.m78.service.service.z.ZService;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;

/**
 * @author
 * @date 2024/2/23
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/z")
public class ZController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;

    @Autowired
    private ZService zService;

    @RequestMapping(value = {"/prompts"}, method = RequestMethod.POST)
    public Result<List<String>> getPromptNames(HttpServletRequest request,
                                               @RequestBody PageReq req) {
        try {
            SessionAccount account = loginService.getAccountFromSession(request);
            String zToken = userService.getUserZToken(account.getUsername()).get();
            return Result.success(zService.getPromptNames(req, zToken));
        } catch (Exception e) {
            return Result.fromException(e);
        }
    }


    @RequestMapping(value = {"/promptData"}, method = RequestMethod.POST)
    public Result<String> getPromptByName(HttpServletRequest request,
                                          @RequestParam("name") String name) {
        try {
            SessionAccount account = loginService.getAccountFromSession(request);
            String zToken = userService.getUserZToken(account.getUsername()).get();
            return Result.success(zService.getPromptByName(name, zToken));
        } catch (Exception e) {
            return Result.fromException(e);
        }
    }

}
