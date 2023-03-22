package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import com.xiaomi.mone.tpc.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.LoginService;
import com.xiaomi.mone.tpc.login.util.TokenUtil;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/enum/list")
    public ResultVo<Map<String, List<EnumData>>> enumList(@RequestBody NullParam param) {
        Map<String,List<EnumData>> map = EnumUtil.getMapList();
        return ResponseCode.SUCCESS.build(map);
    }

    @RequestMapping(value = "/login")
    public ResultVo<LoginInfoVo> login(@RequestParam(name = "pageUrl", required = true) String pageUrl) {
        return loginService.login(pageUrl);
    }

    @RequestMapping(value = "/code")
    public ResultVo<AuthUserVo> code(@RequestParam(name = "code", required = true) String code, @RequestParam(name = "source", required = true) String source, @RequestParam(name = "pageUrl", required = true) String pageUrl, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ResultVo<AuthUserVo> resultVo = loginService.code(code, source, pageUrl);
        if (resultVo.success()) {
            TokenUtil.setCookieUrl(resultVo.getData(), pageUrl);
        }
        return resultVo;
    }

    @RequestMapping(value = "/logout")
    public ResultVo logout(HttpServletRequest request) {
        AuthTokenVo authToken = TokenUtil.parseAuthToken(request);
        return loginService.logout(authToken);
    }

    @RequestMapping(value = "/register/check")
    public ResultVo registerCheck(@RequestBody LoginCheckParam param) {
        return ResponseCode.SUCCESS.build(loginService.registerCheck(param));
    }

    @RequestMapping(value = "/account/check")
    public ResultVo accountCheck(@RequestBody LoginCheckParam param) {
        return ResponseCode.SUCCESS.build(loginService.accountCheck(param));
    }

    @RequestMapping(value = "/register")
    public ResultVo register(@RequestBody LoginRegisterParam param) {
        return loginService.register(param);
    }


    @RequestMapping(value = "/session")
    public ResultVo<AuthUserVo> session(@RequestBody LoginSessionParam param, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ResultVo<AuthUserVo> resultVo = loginService.session(param);
        if (resultVo.success()) {
            TokenUtil.setCookieUrl(resultVo.getData(), param.getPageUrl());
        }
        return resultVo;
    }

    @RequestMapping(value = "/find")
    public ResultVo find(@RequestBody LoginFindParam param) {
        return loginService.find(param);
    }

    @RequestMapping(value = "/pwd/reset")
    public ResultVo pwdReset(@RequestBody LoginPwdResetParam param) {
        return loginService.pwdReset(param);
    }


    /**
     * 客户端调用拦截
     * @param authToken
     * @param fullInfo
     * @return
     */
    @RequestMapping(value = "/token/parse")
    public ResultVo<AuthUserVo> parseToken(@RequestParam(name = "authToken") String authToken, @RequestParam(name = "fullInfo", defaultValue = "false") Boolean fullInfo) {
        return loginService.parseToken(authToken, fullInfo);
    }

}
