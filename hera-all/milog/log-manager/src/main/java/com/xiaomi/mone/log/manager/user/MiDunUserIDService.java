package com.xiaomi.mone.log.manager.user;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.SignatureException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/7 10:49
 */
@Slf4j
@Service
public class MiDunUserIDService implements UserIDService {

    public final static String AEGIS_SDK_PUBLIC_KEY = "aegis_sdk_public_key";
    public final static String AEGIS_SDK_PUBLIC_KEY_HERA = "aegis_sdk_public_key_hera";
    public final static String AEGIS_SDK_PUBLIC_KEY_HERA2 = "aegis_sdk_public_key_hera2";
    public final static String AEGIS_SDK_PUBLIC_KEY_MONE = "aegis_sdk_public_key_mone";

    @Override
    public String findUserId(String signature) {
        String publicKeyMilog = Config.ins().get(AEGIS_SDK_PUBLIC_KEY, "");
        String publicKeyHera = Config.ins().get(AEGIS_SDK_PUBLIC_KEY_HERA, "");
        String publicKeyHera2 = Config.ins().get(AEGIS_SDK_PUBLIC_KEY_HERA2, "");
        String publicKeyMone = Config.ins().get(AEGIS_SDK_PUBLIC_KEY_MONE, "");
        String milogResult = queryResultToAegis(signature, publicKeyMilog);
        String heraResult = queryResultToAegis(signature, publicKeyHera);
        String hera2Result = queryResultToAegis(signature, publicKeyHera2);
        String moneResult = queryResultToAegis(signature, publicKeyMone);
        return StringUtils.isNotEmpty(milogResult) ? milogResult :
                StringUtils.isNotEmpty(heraResult) ? heraResult :
                        StringUtils.isNotEmpty(hera2Result) ? hera2Result : moneResult;
    }

    private String queryResultToAegis(String signature, String publicKey) {
        String result = "";
        if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(publicKey)) {
            return result;
        }
        try {
            result = AegisSignUtil.verifySignGetInfo(signature, publicKey);
        } catch (SignatureException e) {
            log.error(String.format("midun express user exception,public_key:%s", publicKey), e);
        }
        return result;
    }
}
