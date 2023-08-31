package com.xiaomi.mone.tpc.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xiaomi.mone.tpc.common.param.ApprovalStatusParam;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import com.xiaomi.mone.tpc.common.util.MD5Util;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TokenUtil {

    private static final String athmStr_pre = "&*Y)@#";
    private static final String codes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+=-";


    /**
     * token生产
     * @param ttlSeconds
     * @param account
     * @param type
     * @return
     */
    public static String createToken(int ttlSeconds, String account, Integer type, String athmStr) {
        StringBuilder username = new StringBuilder();
        username.append(type).append("_").append(account);
        Date date = new Date(System.currentTimeMillis() + 1000L * ttlSeconds);
        StringBuilder allAthmStr = new StringBuilder();
        allAthmStr.append(athmStr_pre).append(athmStr);
        Algorithm algorithm = Algorithm.HMAC256(MD5Util.md5(allAthmStr.toString()));
        return JWT.create()
                .withClaim("username", username.toString())
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * token解析
     * @param athmStr
     * @param token
     * @return
     */
    public static AuthUserVo parseAuthUserVo(String athmStr, String token) {
        try {
            StringBuilder allAthmStr = new StringBuilder();
            allAthmStr.append(athmStr_pre).append(athmStr);
            Algorithm algorithm = Algorithm.HMAC256(MD5Util.md5(allAthmStr.toString()));
            JWTVerifier verify = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verify.verify(token);
            String value = decodedJWT.getClaim("username").asString();
            int pos = value.indexOf('_');
            AuthUserVo userVo = new AuthUserVo();
            userVo.setUserType(Integer.parseInt(value.substring(0, pos)));
            userVo.setAccount(value.substring(pos + 1));
            return userVo;
        } catch (Throwable e) {
        }
        return null;
    }

}
