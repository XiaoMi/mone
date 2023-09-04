package com.xiaomi.mone.tpc.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xiaomi.mone.tpc.login.common.util.MD5Util;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TokenUtil {

    private static final String athmStr_pre = "&0aG)";
    private static final String codes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+=-";

    /**
     * 获取注册码
     * @param len
     * @return
     */
    public static String getRegisterCode(int len) {
        Random random = new Random();
        StringBuilder registerCode = new StringBuilder();
        int codesLen = codes.length();
        for (int i = 0; i < len; i++) {
            int randNum = random.nextInt(10000000) % codesLen;
            registerCode.append(codes.charAt(randNum));
        }
        return registerCode.toString();
    }

    /**
     * 每天随机一个字符串
     * @return
     */
    private static String getRandomStrByDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Random random = new Random(calendar.getTimeInMillis());
        int len = random.nextInt(20);
        len += 10;
        StringBuilder randomStr = new StringBuilder();
        for (int i = 0; i < len; i++) {
            randomStr.append(codes.charAt(random.nextInt(76)));
        }
        return randomStr.toString();
    }


    /**
     * token生产
     * @param ttlSeconds
     * @param account
     * @param type
     * @return
     */
    public static String createToken(int ttlSeconds, String account, Integer type) {
        return createToken(ttlSeconds, account, type, getRandomStrByDate());
    }

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
     * @param account
     * @param type
     * @param token
     * @return
     */
    public static boolean verifyToken(String account, Integer type, String token) {
        return verifyToken(account, type, getRandomStrByDate(), token);
    }

    /**
     * token解析
     * @param account
     * @param type
     * @param athmStr
     * @param token
     * @return
     */
    public static boolean verifyToken(String account, Integer type, String athmStr, String token) {
        try {
            StringBuilder username = new StringBuilder();
            username.append(type).append("_").append(account);
            StringBuilder allAthmStr = new StringBuilder();
            allAthmStr.append(athmStr_pre).append(athmStr);
            Algorithm algorithm = Algorithm.HMAC256(MD5Util.md5(allAthmStr.toString()));
            JWTVerifier verify = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verify.verify(token);
            return username.toString().equals(decodedJWT.getClaim("username").asString());
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * token解析
     * @param token
     * @return
     */
    public static AuthUserVo parseAuthUserVo(String token) {
        return parseAuthUserVo(getRandomStrByDate(), token);
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
