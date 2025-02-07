package run.mone.m78.service.common;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:34 PM
 */
@Slf4j
public class MD5Utils {

    /**
     * MD5字符串的前缀，防止外界通过猜测MD5算法破解
     */
    private static final String MD5_FLAG = "m78_ligoudan";

    private static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Init MD5 error", e);
        }
        return null;
    }

    public static String encrypt(String str) {
        try {
            MessageDigest md = getDigest();
            String md5 = MD5_FLAG + str;
            byte[] digest = md.digest(md5.getBytes("UTF-8"));
            return byteToString(digest);
        } catch (UnsupportedEncodingException e) {
            log.error("Generate MD5 error", e);
        }
        return null;
    }

    private static String byteToString(byte[] digest) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String tempStr = Integer.toHexString(digest[i] & 0xFF);
            if (tempStr.length() == 1) {
                sb.append("0").append(tempStr);
            } else {
                sb.append(tempStr);
            }
        }
        return sb.toString().toUpperCase();

    }
}
