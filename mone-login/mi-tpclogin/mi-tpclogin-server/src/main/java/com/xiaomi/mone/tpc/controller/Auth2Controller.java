package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.auth2.Auth2Service;
import com.xiaomi.mone.tpc.login.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/oauth")
public class Auth2Controller {

    @Autowired
    private Auth2Service auth2Service;

    /**
     * C端调用
     * @param pageUrl
     * @param cid
     * @return
     */
    @RequestMapping(value = "authorize")
    public ResultVo<LoginInfoVo> authorize(@RequestParam(name = "redirect_uri") String pageUrl,
            @RequestParam(name = "client_id") String cid, @RequestParam(name = "state", required = false) String state) {
        return auth2Service.authorize(pageUrl, cid, state);
    }

    /**
     * S端调用
     * @param code
     * @return
     */
    @RequestMapping(value = "/token")
    public Map token(@RequestParam(name = "code") String code) {
        return auth2Service.token(code);
    }

    /**
     * S端调用
     * @param headers
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/api/user")
    public Map apiUser(@RequestHeader HttpHeaders headers) throws Throwable {
        if (!headers.containsKey("Authorization")) {
            return new HashMap();
        }
        String authToken = headers.get("Authorization").get(0);
        String[] authArr = authToken.split(" ");
        if (authArr.length != 2) {
            return new HashMap();
        }
        return auth2Service.apiUser(authToken.split(" ")[1]);
    }

}
