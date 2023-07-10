package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.login.LoginService;
import com.xiaomi.mone.tpc.login.common.param.*;
import com.xiaomi.mone.tpc.login.common.vo.EnumData;
import com.xiaomi.mone.tpc.login.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.login.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.util.Auth2Util;
import com.xiaomi.mone.tpc.util.CookieUtil;
import com.xiaomi.mone.tpc.login.util.TokenUtil;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    public ResultVo<AuthUserVo> code(@RequestParam(name = "code", required = true) String code,
                                     @RequestParam(name = "source", required = true) String source,
                                     @RequestParam(name = "pageUrl", required = true) String pageUrl,
                                     @RequestParam(name = "state", required = false) String state,
                                     @RequestParam(name = "vcode", required = false) String vcode) throws Throwable {
        ResultVo<AuthUserVo> resultVo = loginService.code(code, source, vcode, state, pageUrl);
        if (resultVo.success()) {
            if (StringUtils.isBlank(vcode)) {
                CookieUtil.setCookieUrl(resultVo.getData(), pageUrl);
            } else {
                Auth2Util.setCookieUrl(resultVo.getData(), pageUrl);
            }
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

    @RequestMapping(value = "/register_code")
    public ResultVo registerCode(@RequestBody LoginRegisterCodeParam param) {
        return loginService.registerCode(param);
    }


    @RequestMapping(value = "/session")
    public ResultVo<AuthUserVo> session(@RequestBody LoginSessionParam param) throws Throwable {
        ResultVo<AuthUserVo> resultVo = loginService.session(param);
        if (resultVo.success()) {
            if (StringUtils.isBlank(param.getVcode())) {
                CookieUtil.setCookieUrl(resultVo.getData(), param.getPageUrl());
            } else {
                Auth2Util.setCookieUrl(resultVo.getData(), param.getPageUrl());
            }
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
     * C端调用拦截
     * @param authToken
     * @param fullInfo
     * @return
     */
    @RequestMapping(value = "/token/parse")
    public ResultVo<AuthUserVo> parseToken(@RequestParam(name = "authToken") String authToken, @RequestParam(name = "fullInfo", defaultValue = "false") Boolean fullInfo) {
        return loginService.parseToken(authToken, fullInfo);
    }

}
