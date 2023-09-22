package com.xiaomi.mone.tpc.login;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.api.service.UserFacade;
import com.xiaomi.mone.tpc.auth2.Auth2Helper;
import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.param.UserRegisterParam;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.dao.entity.AccountEntity;
import com.xiaomi.mone.tpc.dao.impl.AccountDao;
import com.xiaomi.mone.tpc.login.common.enums.AccountStatusEnum;
import com.xiaomi.mone.tpc.login.common.enums.AccountTypeEnum;
import com.xiaomi.mone.tpc.login.common.param.*;
import com.xiaomi.mone.tpc.login.common.util.GsonUtil;
import com.xiaomi.mone.tpc.login.common.util.MD5Util;
import com.xiaomi.mone.tpc.login.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.login.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.login.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.login.common.vo.ResultVo;
import com.xiaomi.mone.tpc.login.util.Auth2Util;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.util.EmailUtil;
import com.xiaomi.mone.tpc.util.TokenUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private Cache cache;
    @NacosValue("${home.url:http://localhost:80}")
    private String homeUrl;
    @Autowired
    AccountDao accountDao;
    @Autowired
    private EmailHelper emailHelper;
    @Autowired
    private Auth2Helper auth2Helper;
    @Autowired
    private UserFacade userFacade;


    public ResultVo<LoginInfoVo> login(String pageUrl) {
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        List<AuthAccountVo> authAccountVos = LoginMgr.buildAuth2LoginInfos(pageUrl, null, null);
        loginInfoVo.setAuthAccountVos(authAccountVos);
        return ResponseCode.SUCCESS.build(loginInfoVo);
    }

    public ResultVo<AuthUserVo> code(String code, String source, String vcode, String state, String pageUrl) {
        if (!auth2Helper.checkVcode(vcode)) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        LoginMgr mgr = LoginMgr.get(source);
        if (mgr == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        ResultVo<AuthUserVo> authUserVoRst = mgr.getUserVo(code, pageUrl, vcode, state);
        if (!authUserVoRst.success()) {
            return authUserVoRst;
        }
        AuthUserVo authUserVo = authUserVoRst.getData();
        UserRegisterParam registerParam = new UserRegisterParam();
        registerParam.setAccount(authUserVo.getAccount());
        registerParam.setUserType(authUserVo.getUserType());
        registerParam.setContent(GsonUtil.gsonString(authUserVo));
        Result<UserVo> result = userFacade.register(registerParam);
        if (result.getCode() != 0) {
            return ResponseCode.OPER_FAIL.build(result.getMessage());
        }
        authUserVo.setState(state);
        Key key = null;
        if (StringUtils.isNotBlank(vcode)) {
            if (StringUtils.isBlank(authUserVo.getEmail())) {
                return ResponseCode.NO_EMAIL_FAILED.build();
            }
            authUserVo.setCode(Auth2Util.genVcode(vcode, authUserVo.getAccount(), authUserVo.getUserType()));
            key = Key.build(ModuleEnum.AUTH2_CODE_USER).keys(authUserVo.getCode());
        } else {
            key = Key.build(ModuleEnum.LOGIN).keys(authUserVo.getToken()).setTime(authUserVo.getExprTime(), TimeUnit.SECONDS);
        }
        if (!cache.get().set(key, authUserVo)) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        return authUserVoRst;
    }

    public ResultVo logout(AuthTokenVo authToken) {
        if (authToken == null) {
            return ResponseCode.SUCCESS.build();
        }
        Key key = Key.build(ModuleEnum.LOGIN).keys(authToken.getAuthToken());
        cache.get().delete(key);
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 账号注册检查
     * @param param
     * @return
     */
    public ResultVo registerCheck(LoginCheckParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity != null) {
            return ResponseCode.CHECK_FAILED.build("账号已经存在");
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 账号检查
     * @param param
     * @return
     */
    public ResultVo accountCheck(LoginCheckParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo register(LoginRegisterParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity != null) {
            return ResponseCode.CHECK_FAILED.build("账号已经存在");
        }
        String registerCode = cache.get().get(Key.build(ModuleEnum.REGISTER_CODE).keys(param.getAccount(), param.getType()), String.class);
        if (!param.getRegisterCode().equals(registerCode)) {
            return ResponseCode.CHECK_FAILED.build("注册码过期或错误");
        }
        entity = new AccountEntity();
        entity.setType(param.getType());
        entity.setAccount(param.getAccount());
        entity.setPwd(MD5Util.md5(param.getPassword()));
        entity.setName(param.getName());
        entity.setStatus(AccountStatusEnum.ENABLE.getCode());
        boolean result = accountDao.insert(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        UserRegisterParam registerParam = new UserRegisterParam();
        registerParam.setAccount(entity.getAccount());
        registerParam.setUserType(accountTypeEnum.getUserType());
        Result<UserVo> userVoResult = userFacade.register(registerParam);
        if (userVoResult.getCode() != 0) {
            return ResponseCode.OPER_FAIL.build(userVoResult.getMessage());
        }
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo registerCode(LoginRegisterCodeParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity != null) {
            return ResponseCode.CHECK_FAILED.build("账号已经存在");
        }
        String registerCode = TokenUtil.getRegisterCode(6);
        if (!cache.get().set(Key.build(ModuleEnum.REGISTER_CODE).keys(param.getAccount(), param.getType()), registerCode)) {
            return ResponseCode.OPER_FAIL.build("请稍后重试");
        }
        if (!emailHelper.sendRegisterCode(param.getAccount(), registerCode)) {
            return ResponseCode.OPER_ILLEGAL.build("邮件发送失败");
        }
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo<AuthUserVo> session(LoginSessionParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (AccountTypeEnum.EMAIL.equals(accountTypeEnum)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        if (!auth2Helper.checkVcode(param.getVcode())) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity == null) {
            return ResponseCode.CHECK_FAILED.build("账号不存在");
        }
        String pwdMd5 = MD5Util.md5(param.getPassword());
        if (!pwdMd5.equals(entity.getPwd())) {
            return ResponseCode.CHECK_FAILED.build("密码或账号错误");
        }
        if (AccountStatusEnum.DISABLE.getCode().equals(entity.getStatus())) {
            return ResponseCode.USER_DISABLED.build();
        }
        AuthUserVo authUserVo = new AuthUserVo();
        authUserVo.setExprTime((int)(ModuleEnum.LOGIN.getUnit().toSeconds(ModuleEnum.LOGIN.getTime())));
        authUserVo.setAccount(entity.getAccount());
        authUserVo.setUserType(accountTypeEnum.getUserType());
        authUserVo.setToken(TokenUtil.createToken(authUserVo.getExprTime(), authUserVo.getAccount(), authUserVo.getUserType()));
        if (AccountTypeEnum.EMAIL.equals(accountTypeEnum)) {
            authUserVo.setEmail(param.getAccount());
        }
        authUserVo.setState(param.getState());
        authUserVo.setName(entity.getName());
        Key key = null;
        //auth2 模式登陆，code缓存
        if (StringUtils.isNotBlank(param.getVcode())) {
            authUserVo.setCode(Auth2Util.genVcode(param.getVcode(), entity.getAccount(), entity.getType()));
            key = Key.build(ModuleEnum.AUTH2_CODE_USER).keys(authUserVo.getCode());
        } else {
            key = Key.build(ModuleEnum.LOGIN).keys(authUserVo.getToken()).setTime(authUserVo.getExprTime(), TimeUnit.SECONDS);
        }
        if (!cache.get().set(key, authUserVo)) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        return ResponseCode.SUCCESS.build(authUserVo);
    }

    /**
     * 找回密码
     * @param param
     * @return
     */
    public ResultVo find(LoginFindParam param) {
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build("账号不存在");
        }
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
            StringBuilder fullUrl = new StringBuilder();
            fullUrl.append(homeUrl).append("/user-manage/password-reset")
                    .append("?account=").append(param.getAccount())
                    .append("&type=").append(param.getType())
                    .append("&token=").append(TokenUtil.createToken(300, param.getAccount(), param.getType()));
            if (!emailHelper.sendResetPwd(param.getAccount(), fullUrl.toString())) {
                return ResponseCode.OPER_ILLEGAL.build("邮件发送失败");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
        }
        return ResponseCode.SUCCESS.build();
    }

    /**
     * 密码重置
     * @param param
     * @return
     */
    public ResultVo pwdReset(LoginPwdResetParam param) {
        if (!TokenUtil.verifyToken(param.getAccount(), param.getType(), param.getToken())) {
            return ResponseCode.OPER_ILLEGAL.build("token失效或非法");
        }
        AccountEntity entity = accountDao.getOneByAccount(param.getAccount(), param.getType());
        if (entity == null) {
            return ResponseCode.OPER_ILLEGAL.build("账号不存在");
        }
        entity.setPwd(MD5Util.md5(param.getNewPwd()));
        boolean result = accountDao.updateById(entity);
        if (!result) {
            return ResponseCode.OPER_FAIL.build();
        }
        return ResponseCode.SUCCESS.build();
    }


    /**
     * token解析
     * @param authToken
     * @param fullInfo
     * @return
     */
    public ResultVo<AuthUserVo> parseToken(String authToken, Boolean fullInfo) {
        AuthUserVo userVo = TokenUtil.parseAuthUserVo(authToken);
        if (userVo == null) {
            return ResponseCode.OPER_ILLEGAL.build("token失效或非法");
        }
        Key key = Key.build(ModuleEnum.LOGIN).keys(authToken);
        userVo = cache.get().get(key, AuthUserVo.class);
        if (userVo == null) {
            return ResponseCode.OPER_ILLEGAL.build("token数据不存在");
        }
        return ResponseCode.SUCCESS.build(userVo);
    }

}
