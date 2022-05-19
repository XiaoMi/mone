package com.xiaomi.youpin.common.crypto;


import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Aes {

    // 加密
    public static String encrypt(String sSrc, String sKey, String ivParameter) throws Exception {
        return encrypt(sSrc, sKey, ivParameter, false);
    }


    public static String encrypt(String sSrc, String sKey, String ivParameter, boolean urlEncode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        String res = new BASE64Encoder().encode(encrypted);
        if (urlEncode) {
            return URLEncoder.encode(res, "utf-8");
        }
        return res;
    }


    // 解密
    public static String decrypt(String sSrc, String sKey, String ivParameter) {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return null;
        }
    }


}
