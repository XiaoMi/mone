package com.xiaomi.mone.tpc.auth2;

import com.xiaomi.mone.tpc.api.service.SystemFacade;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.login.common.enums.SystemStatusEnum;
import com.xiaomi.mone.tpc.common.param.SystemQryParam;
import com.xiaomi.mone.tpc.common.vo.*;
import com.xiaomi.mone.tpc.login.LoginMgr;
import com.xiaomi.mone.tpc.login.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.Auth2Util;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @project: mi-tpclogin
 * @author: zgf1
 * @date: 2022/10/27 17:27
 */
@Slf4j
@Service
public class Auth2Service implements Auth2Helper {

    @Autowired
    private SystemFacade systemFacade;
    @Autowired
    private Cache cache;

    public ResultVo<LoginInfoVo> authorize(String pageUrl, String cid, String state) {
        SystemQryParam param = new SystemQryParam();
        param.setSystemName(cid);
        param.setStatus(SystemStatusEnum.ENABLE.getCode());
        Result<SystemVo> result = systemFacade.getByCond(param);
        if (result == null || result.getData() == null) {
            return com.xiaomi.mone.tpc.login.common.vo.ResponseCode.OPER_FAIL.build("请求系统非法");
        }
        SystemVo systemVo = result.getData();
        String vcode = Auth2Util.genVcode(systemVo.getSystemName(), systemVo.getSystemToken(), state);
        boolean setResult = cache.get().set(Key.build(ModuleEnum.AUTH2_VCODE).keys(vcode), "OK");
        if (!setResult) {
            return com.xiaomi.mone.tpc.login.common.vo.ResponseCode.OPER_FAIL.build();
        }
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        List<com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo> authAccountVos = LoginMgr.buildAuth2LoginInfos(pageUrl, vcode, state);
        loginInfoVo.setAuthAccountVos(authAccountVos);
        loginInfoVo.setVcode(vcode);
        loginInfoVo.setState(state);
        loginInfoVo.setPageUrl(pageUrl);
        return com.xiaomi.mone.tpc.login.common.vo.ResponseCode.SUCCESS.build(loginInfoVo);
    }


    public Map token(String code) {
        if (StringUtils.isEmpty(code)) {
            return new HashMap();
        }
        Key key = Key.build(ModuleEnum.AUTH2_CODE_USER).keys(code);
        AuthUserVo authUserVo = cache.get().get(key, AuthUserVo.class);
        if (authUserVo == null) {
            return new HashMap();
        }
        key = Key.build(ModuleEnum.LOGIN).keys(authUserVo.getToken()).setTime(authUserVo.getExprTime(), TimeUnit.SECONDS);
        if (!cache.get().set(key, authUserVo)) {
            return new HashMap();
        }
        Map response = new HashMap();
        response.put("access_token", authUserVo.getToken());
        response.put("token_type", "bearer");
        response.put("scope", "user");
        return response;
    }

    public Map apiUser(String token) {
        Key key = Key.build(ModuleEnum.LOGIN).keys(token);
        AuthUserVo authUserVo = cache.get().get(key, AuthUserVo.class);
        if (authUserVo == null) {
            return new HashMap();
        }
        Map response = new HashMap();
        response.put("expires_in", authUserVo.getExprTime());
        StringBuilder username = new StringBuilder();
        username.append(authUserVo.getAccount()).append("(").append(UserTypeEnum.getEnum(authUserVo.getUserType()).getDesc()).append(")");
        response.put("username", username.toString());
        response.put("userType", authUserVo.getUserType());
        response.put("avatar_url", authUserVo.getAvatarUrl());
        response.put("name", authUserVo.getName());
        response.put("email", authUserVo.getEmail());
        return response;
    }

    @Override
    public boolean checkVcode(String vcode) {
        if (StringUtils.isBlank(vcode)) {
            return true;
        }
        String ok = cache.get().get(Key.build(ModuleEnum.AUTH2_VCODE).keys(vcode), String.class);
        return StringUtils.isNotBlank(ok);
    }
}
