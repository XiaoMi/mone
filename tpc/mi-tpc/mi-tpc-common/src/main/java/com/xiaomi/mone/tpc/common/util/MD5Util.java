package com.xiaomi.mone.tpc.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 15:21
 */
public class MD5Util {
    /**
     * 使用md5的算法进行加密
     */
    public static String md5(String plainText, String encode) {
        byte[] secretBytes = null;
        try {
            if (StringUtils.isBlank(encode)) {
                secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
            } else {
                secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes(Charset.forName(encode)));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static String md5(String plainText) {
        return md5(plainText, null);
    }

}
