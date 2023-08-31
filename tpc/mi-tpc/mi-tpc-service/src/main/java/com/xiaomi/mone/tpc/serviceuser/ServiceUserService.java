package com.xiaomi.mone.tpc.serviceuser;

import com.xiaomi.mone.tpc.common.enums.SystemStatusEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.util.MD5Util;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.dao.impl.AccountDao;
import com.xiaomi.mone.tpc.dao.impl.SystemDao;
import com.xiaomi.mone.tpc.login.util.SignUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.user.UserHelper;
import com.xiaomi.mone.tpc.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ServiceUserService {

    @Autowired
    AccountDao accountDao;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private UserHelper userHelper;

    /**
     * token解析
     * @param sysName
     * @param sysSign
     * @param userToken
     * @param reqTime
     * @return
     */
    public ResultVo<AuthUserVo> parseToken(String sysName, String sysSign, String userToken, long reqTime, String dataSign) {
        long now = System.currentTimeMillis();
        //5分钟失效
        if (reqTime + TimeUnit.MINUTES.toMillis(5) <= now) {
            log.info("rpcParseToken系统{}请求过期{}", sysName, reqTime);
            return ResponseCode.OPER_ILLEGAL.build("请求过期");
        }
        SystemEntity systemEntity =  systemDao.getOneByName(sysName);
        if (systemEntity == null || SystemStatusEnum.DISABLE.getCode().equals(systemEntity.getStatus())) {
            log.info("rpcParseToken系统{}不存在或停用", sysName);
            return ResponseCode.OPER_ILLEGAL.build("请求系统非法");
        }
        String genSysSign = SignUtil.getSysSign(sysName, systemEntity.getSystemToken(), reqTime, userToken, dataSign);
        if (!genSysSign.equals(sysSign)) {
            log.info("rpcParseToken系统{}签名{}错误", sysName, sysSign);
            return ResponseCode.OPER_ILLEGAL.build("请求系统非法");
        }
        /**
         * 仅验证请求系统的合法性
         */
        if (StringUtils.isBlank(userToken)) {
            return ResponseCode.SUCCESS.build();
        }
        /**
         * 验证用户的合法性
         */
        AuthUserVo userVo = TokenUtil.parseAuthUserVo(systemEntity.getSystemToken(), userToken);
        if (userVo == null) {
            log.info("rpcParseToken系统{}用户Token={}非法或失效", sysName, userToken);
            return ResponseCode.OPER_ILLEGAL.build("token失效或非法");
        }
        return ResponseCode.SUCCESS.build(userVo);
    }


    /**
     * token解析
     * @param sysName
     * @param sysSign
     * @param account
     * @param reqTime
     * @param ttlMills
     * @return
     */
    public ResultVo<AuthUserVo> apply(String sysName, String sysSign, String account, long reqTime, long ttlMills) {
        /**
         * 系统验证
         */
        ResultVo<AuthUserVo> resultVo = parseToken(sysName, sysSign, null, reqTime, MD5Util.md5(account + ttlMills));
        if (!resultVo.success()) {
            return resultVo;
        }
        SystemEntity systemEntity =  systemDao.getOneByName(sysName);
        if (systemEntity == null) {
            return ResponseCode.OPER_ILLEGAL.build("系统错误");
        }
        StringBuilder serviceAcc = new StringBuilder();
        serviceAcc.append(sysName).append("/").append(account);
        UserVo userVo = userHelper.register(serviceAcc.toString(), UserTypeEnum.SERVICE_TYPE.getCode());
        if (userVo == null) {
            return ResponseCode.OPER_FAIL.build("申请失败");
        }
        int ttlSeconds = (int)(ttlMills / 1000L);
        String userToken = TokenUtil.createToken(ttlSeconds, userVo.getAccount(), userVo.getType(), systemEntity.getSystemToken());
        AuthUserVo authUserVo = new AuthUserVo();
        authUserVo.setToken(userToken);
        authUserVo.setExprTime(ttlSeconds);
        authUserVo.setAccount(userVo.getAccount());
        authUserVo.setUserType(userVo.getType());
        return ResponseCode.SUCCESS.build(authUserVo);
    }

}
