package com.xiaomi.data.push.crypto;


import com.xiaomi.youpin.common.crypto.Aes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class YoupinAes {


    // 加密
    public static String encrypt(String sSrc, String sKey, String ivParameter) throws Exception {
        return Aes.encrypt(sSrc,sKey,ivParameter);
    }


    // 解密
    public static String decrypt(String sSrc, String sKey, String ivParameter) {
        return Aes.decrypt(sSrc,sKey,ivParameter);
    }


}
