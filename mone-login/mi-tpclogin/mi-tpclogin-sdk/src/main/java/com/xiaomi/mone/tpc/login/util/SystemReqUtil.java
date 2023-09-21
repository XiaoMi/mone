package com.xiaomi.mone.tpc.login.util;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/8/23 10:43
 */
public class SystemReqUtil {

    private static final Logger logger = LoggerFactory.getLogger(SystemReqUtil.class);

    /**
     * 申请用户token
     * @param reqUrl
     * @param sysName
     * @param token
     * @param account
     * @param ttlMills
     * @return
     */
    public static ResultVo<AuthUserVo> applyRequest(String reqUrl, String sysName, String token, String account, long ttlMills) {
        long now = System.currentTimeMillis();
        Map<String, String> getParams = new HashMap<>();
        getParams.put(ConstUtil.SYS_NAME, sysName);
        getParams.put(ConstUtil.REQ_TIME, now + "");
        getParams.put(ConstUtil.ACCOUNT, account);
        getParams.put(ConstUtil.TTL_MILLS, ttlMills + "");
        getParams.put(ConstUtil.SYS_SIGN, SignUtil.getSysSign(sysName, token, now, null, MD5Util.md5(account + ttlMills)));
        ResultVo<AuthUserVo> resultVo = HttpClientUtil.doHttpGet(reqUrl, getParams, new TypeToken<ResultVo<AuthUserVo>>(){});
        return resultVo;
    }


    /**
     * 数据请求
     * @param reqUrl
     * @param sysName
     * @param token
     * @return
     */
    public static <T> T dataRequest(String reqUrl, String sysName, String token, String userToken, Object param, TypeToken<T> type) {
        try {
            long now = System.currentTimeMillis();
            Map<String, String> headers = new HashMap<>();
            headers.put(ConstUtil.SYS_NAME, sysName);
            headers.put(ConstUtil.REQ_TIME, now + "");
            if (StringUtils.isNotBlank(userToken)) {
                headers.put(ConstUtil.USER_TOKEN, userToken);
            }
            headers.put(ConstUtil.SYS_SIGN, SignUtil.getSysSign(sysName, token, now, userToken, SignUtil.getDataSign(param)));
            return HttpClientUtil.doHttpPostJson(reqUrl, param, headers, type);
        } catch (Throwable e) {
            logger.error("SystemReqUtil.dataRequest执行异常", e);
            return null;
        }
    }


    public static ResultVo<AuthUserVo> authRequest(String authToken, boolean fullInfo) {
        Map<String,String> getParams = new HashMap<>();
        getParams.put("authToken", authToken);
        getParams.put("fullInfo", fullInfo + "");
        ResultVo<AuthUserVo> resultVo = HttpClientUtil.doHttpGet(ConstUtil.authTokenUrlVal, getParams, new TypeToken<ResultVo<AuthUserVo>>(){});
        logger.info("http.authRequest param={}, resultVo={}", getParams, resultVo);
        return resultVo;
    }

    public static ResultVo<AuthUserVo> authRequest(String sysName, String sysSign, String userToken, String reqTime, String dataSign, String url) {
        Map<String, String> getParams = new HashMap<>();
        getParams.put(ConstUtil.SYS_NAME, sysName);
        getParams.put(ConstUtil.SYS_SIGN, sysSign);
        if (StringUtils.isNotBlank(userToken)) {
            getParams.put(ConstUtil.USER_TOKEN, userToken);
        }
        if (StringUtils.isNotBlank(dataSign)) {
            getParams.put(ConstUtil.DATA_SIGN, dataSign);
        }
        getParams.put(ConstUtil.REQ_TIME, reqTime);
        ResultVo<AuthUserVo> resultVo = HttpClientUtil.doHttpGet(url, getParams, new TypeToken<ResultVo<AuthUserVo>>(){});
        logger.info("http.authRequest param={}, resultVo={}", getParams, resultVo);
        return resultVo;
    }

}
