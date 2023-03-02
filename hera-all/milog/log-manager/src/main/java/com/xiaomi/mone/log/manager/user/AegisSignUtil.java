package com.xiaomi.mone.log.manager.user;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * 验签工具类
 *
 * @author lidenger
 **/
public class AegisSignUtil {

    private static final Logger log = LoggerFactory.getLogger(AegisSignUtil.class);

    /**
     * 验签并获取信息
     *
     * @param message
     * @param key
     * @return 验签成功返回数据，失败返回空串""
     * @throws SignatureException
     */
    public static String verifySignGetInfo(String message, String key, boolean isDecode) throws SignatureException {
        try {
            log.debug("aegis VerifySignGetInfo message:" + message);
            java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            if (StringUtils.isEmpty(message)) {
                return "";
            }
            String[] dataBox = message.split("#");

            String signature = dataBox[0];
            String data = dataBox[1];
            log.debug("verifySignGetInfo signature:" + signature);
            log.debug("verifySignGetInfo data:" + data);
            byte[] signBytes = signature.getBytes();
            byte[] dataBytes = data.getBytes();
            if (isDecode) {
                signBytes = decoder.decode(signature);
                dataBytes = decoder.decode(data);
            }
            String pkey = key.replace("\\n", "");
            pkey = pkey.replace("\n", "");
            pkey = pkey.replace("-----BEGIN PUBLIC KEY-----", "");
            pkey = pkey.replace("-----END PUBLIC KEY-----", "");
            byte[] keyBytes = decoder.decode(pkey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(spec);
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(pubKey);
            sign.update(dataBytes);
            if (sign.verify(signBytes)) {
                return new String(dataBytes);
            } else {
                return "";
            }
        } catch (Exception ex) {
            log.error("VerifySignGetInfo err ", ex);
            throw new SignatureException(ex);
        }
    }

    public static String verifySignGetInfo(String data, String key) throws SignatureException {
        return verifySignGetInfo(data, key, true);
    }

    /**
     * 整理public key，
     * 正确的key放到前面，减少重试次数
     *
     * @param originalKeys    原始key数组
     * @param correctKeyIndex 正确key坐标
     * @return 已整理的key数组
     */
    public static String[] clearUpKeys(String[] originalKeys, int correctKeyIndex) {
        // 参数错误或不需要做调整
        if (originalKeys.length <= 1 || correctKeyIndex <= 0 || correctKeyIndex >= originalKeys.length) {
            return originalKeys;
        }
        // 脱离原始数组的改变对当前算法的干扰
        String[] tempKeys = new String[originalKeys.length];
        System.arraycopy(originalKeys, 0, tempKeys, 0, tempKeys.length);
        String[] keys = new String[tempKeys.length];
        String correctKey = tempKeys[correctKeyIndex];
        int index = 1;
        for (int i = 0; i < tempKeys.length; i++) {
            if (i == correctKeyIndex) {
                continue;
            }
            keys[index++] = tempKeys[i];
        }
        keys[0] = correctKey;
        return keys;
    }


}
