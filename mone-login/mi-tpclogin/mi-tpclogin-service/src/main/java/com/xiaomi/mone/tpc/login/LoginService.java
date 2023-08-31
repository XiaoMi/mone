package com.xiaomi.mone.tpc.login;

import com.xiaomi.mone.tpc.cache.Cache;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.enums.AccountStatusEnum;
import com.xiaomi.mone.tpc.common.enums.AccountTypeEnum;
import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.util.MD5Util;
import com.xiaomi.mone.tpc.common.vo.AuthAccountVo;
import com.xiaomi.mone.tpc.common.vo.LoginInfoVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.dao.entity.AccountEntity;
import com.xiaomi.mone.tpc.dao.impl.AccountDao;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.user.UserService;
import com.xiaomi.mone.tpc.util.EmailUtil;
import com.xiaomi.mone.tpc.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private Cache cache;
    @Value("${home.url}")
    private String homeUrl;
    @Autowired
    AccountDao accountDao;
    @Autowired
    private EmailHelper emailHelper;
    @Autowired
    private UserService userService;


    public ResultVo<LoginInfoVo> login(String pageUrl) {
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        List<AuthAccountVo> authAccountVos = LoginMgr.buildAuth2LoginInfos(pageUrl);
        loginInfoVo.setAuthAccountVos(authAccountVos);
        return ResponseCode.SUCCESS.build(loginInfoVo);
    }

    public ResultVo<AuthUserVo> code(String code, String source, String pageUrl) {
        LoginMgr mgr = LoginMgr.get(source);
        if (mgr == null) {
            return ResponseCode.OPER_ILLEGAL.build();
        }
        AuthUserVo authUserVo = mgr.getUserVo(code, pageUrl);
        if (authUserVo == null) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        Key key = Key.build(ModuleEnum.LOGIN).keys(authUserVo.getToken())
                .setTime(authUserVo.getExprTime(), TimeUnit.SECONDS);
        if (!cache.get().set(key, authUserVo)) {
            return ResponseCode.UNKNOWN_ERROR.build();
        }
        return ResponseCode.SUCCESS.build(authUserVo);
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
        userService.register(entity.getAccount(), accountTypeEnum.getUserType().getCode());
        return ResponseCode.SUCCESS.build();
    }

    public ResultVo<AuthUserVo> session(LoginSessionParam param) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getEnum(param.getType());
        if (accountTypeEnum.equals(AccountTypeEnum.EMAIL)) {
            if (!EmailUtil.check(param.getAccount())) {
                return ResponseCode.CHECK_FAILED.build("邮箱格式错误");
            }
        } else {
            return ResponseCode.CHECK_FAILED.build("账号类型暂不支持");
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
        authUserVo.setAccount(entity.getAccount());
        authUserVo.setUserType(accountTypeEnum.getUserType().getCode());
        authUserVo.setExprTime((int)(ModuleEnum.LOGIN.getUnit().toSeconds(ModuleEnum.LOGIN.getTime())));
        authUserVo.setToken(TokenUtil.createToken(authUserVo.getExprTime(), authUserVo.getAccount(), authUserVo.getUserType()));
        authUserVo.setName(entity.getName());
        Key key = Key.build(ModuleEnum.LOGIN).keys(authUserVo.getToken());
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
     * @return
     */
    public ResultVo<AuthUserVo> parseToken(String authToken, Boolean fullInfo) {
        AuthUserVo userVo = TokenUtil.parseAuthUserVo(authToken);
        if (userVo == null) {
            return ResponseCode.OPER_ILLEGAL.build("token失效或非法");
        }
        if (!fullInfo) {
            return ResponseCode.SUCCESS.build(userVo);
        }
        Key key = Key.build(ModuleEnum.LOGIN).keys(authToken);
        userVo = cache.get().get(key, AuthUserVo.class);
        if (userVo == null) {
            return ResponseCode.OPER_ILLEGAL.build("token数据不存在");
        }
        return ResponseCode.SUCCESS.build(userVo);
    }

}
