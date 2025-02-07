package run.mone.m78.common.youdao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

public class AuthV4Util {

    /**
     * 添加鉴权相关参数 -
     * appKey : 应用ID
     * salt : 随机值
     * curtime : 当前时间戳(秒)
     * signType : 签名版本
     * sign : 请求签名
     *
     * @param appKey    您的应用ID
     * @param appSecret 您的应用密钥
     * @param paramsMap 请求参数表
     */
    public static void addAuthParams(String appKey, String appSecret, Map<String, String[]> paramsMap)
            throws NoSuchAlgorithmException {
        String salt = UUID.randomUUID().toString();
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = calculateSign(appKey, appSecret, salt, curtime);
        paramsMap.put("appKey", new String[]{appKey});
        paramsMap.put("salt", new String[]{salt});
        paramsMap.put("curtime", new String[]{curtime});
        paramsMap.put("signType", new String[]{"v4"});
        paramsMap.put("sign", new String[]{sign});
    }

    /**
     * 计算鉴权签名 -
     * 计算方式 : sign = sha256(appKey + salt + curtime + appSecret)
     *
     * @param appKey    您的应用ID
     * @param appSecret 您的应用密钥
     * @param salt      随机值
     * @param curtime   当前时间戳(秒)
     * @return 鉴权签名sign
     */
    public static String calculateSign(String appKey, String appSecret, String salt, String curtime)
            throws NoSuchAlgorithmException {
        String strSrc = appKey + salt + curtime + appSecret;
        return encrypt(strSrc);
    }

    private static String encrypt(String strSrc) throws NoSuchAlgorithmException {
        byte[] bt = strSrc.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bt);
        byte[] bts = md.digest();
        StringBuilder des = new StringBuilder();
        for (byte b : bts) {
            String tmp = (Integer.toHexString(b & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}
