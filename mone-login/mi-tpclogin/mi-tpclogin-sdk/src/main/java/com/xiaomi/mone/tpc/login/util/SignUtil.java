package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.anno.AuthExclude;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/8/23 10:43
 */
public class SignUtil {

    private static final Logger logger = LoggerFactory.getLogger(SignUtil.class);

    /**
     * 系统签名
     * @param sysName
     * @param token
     * @param now
     * @param dataSign
     * @return
     */
    public static final String getSysSign(String sysName, String token, long now, String userToken, String dataSign) {
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(sysName).append(token).append(now);
        if (StringUtils.isNotBlank(userToken)) {
            signBuilder.append(userToken);
        }
        if (StringUtils.isNotBlank(dataSign)) {
            signBuilder.append(dataSign);
        }
        String  md5Val = MD5Util.md5(signBuilder.toString());
        logger.info("ReqSignUtil.getSysSign data={}, md5={}", signBuilder.toString(), md5Val);
        return md5Val;
    }

    /**
     * 数据签名
     * @param args
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static String getDataSign(Object... args) throws NoSuchFieldException, IllegalAccessException {
        if (args == null || args.length <= 0) {
            return null;
        }
        StringBuilder signBuilder = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            dataStr(arg, arg.getClass(), signBuilder);
        }
        if (signBuilder.length() <= 0) {
            return null;
        }
        String md5Val = MD5Util.md5(signBuilder.toString());
        logger.info("ReqSignUtil.getDataSign data={}, md5={}", signBuilder.toString(), md5Val);
        return md5Val;
    }

    public static void dataStr(Object arg, Class argClass, StringBuilder signBuilder) throws NoSuchFieldException, IllegalAccessException {
        if (arg == null) {
            return;
        }
        if (isPrimitive(argClass)) {
            signBuilder.append(arg.toString());
            return;
        }
        if (Date.class.isAssignableFrom(argClass)) {
            signBuilder.append(((Date)arg).getTime());
            return;
        }
        if (argClass.isArray()) {
            Object[] arrVal = (Object[])arg;
            for (Object val :arrVal) {
                if (val == null) {
                    continue;
                }
                dataStr(val, val.getClass(), signBuilder);
            }
            return;
        }
        if (List.class.isAssignableFrom(argClass)) {
            List list = (List)arg;
            for (Object val :list) {
                if (val == null) {
                    continue;
                }
                dataStr(val, val.getClass(), signBuilder);
            }
            return;
        }
        if (Map.class.isAssignableFrom(argClass)) {
            Map map = (Map)arg;
            TreeMap treemap = new TreeMap(map);
            Set<String> keys = treemap.keySet();
            Object keyVal = null;
            for (String key : keys) {
                keyVal = treemap.get(key);
                if (keyVal == null) {
                    continue;
                }
                dataStr(keyVal, keyVal.getClass(), signBuilder);
            }
            return;
        }
        //不支持Set
        if (Set.class.isAssignableFrom(argClass)) {
            return;
        }
        Map<String, Field> fieldMap = getFieldMap(argClass);
        for (String fieldName : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldName);
            Object fieldVal = field.get(arg);
            if (fieldVal == null) {
                continue;
            }
            dataStr(fieldVal, fieldVal.getClass(), signBuilder);
        }
    }

    private static final Map<Class, Map<String, Field>> clsFieldMap = new ConcurrentHashMap<>();

    private static Map<String, Field> getFieldMap(Class clazz) {
        Map<String, Field> fieldMap = clsFieldMap.get(clazz);
        if (fieldMap != null) {
            return fieldMap;
        }
        synchronized (clsFieldMap) {
            fieldMap = clsFieldMap.get(clazz);
            if (fieldMap != null) {
                return fieldMap;
            }
            fieldMap = getClazzFieldMap(clazz);
            for (Field field : fieldMap.values()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }
            fieldMap = new TreeMap<>(fieldMap);
            clsFieldMap.put(clazz, fieldMap);
        }
        return fieldMap;
    }

    private static Map<String, Field> getClazzFieldMap(Class clazz) {
        if (clazz == null || clazz.equals(Object.class)) {
            return new HashMap<>();
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null) {
            fields = new Field[0];
        }
        Map<String, Field> map = new HashMap<>();
        Map<String, Field> subMap = getClazzFieldMap(clazz.getSuperclass());
        if (!CollectionUtils.isEmpty(subMap)) {
            map.putAll(subMap);
        }
        Arrays.stream(fields).filter(field -> field.getAnnotation(AuthExclude.class) == null)
                .map(field -> {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    return field;
                }).forEach(field -> map.put(field.getName(), field));
        return map;
    }

    public static boolean isPrimitive(Class fClazz) {
        if (fClazz.isPrimitive()) {
            return true;
        }
        return Integer.class.equals(fClazz)
                || Byte.class.equals(fClazz)
                || Short.class.equals(fClazz)
                || Long.class.equals(fClazz)
                || Boolean.class.equals(fClazz)
                || Character.class.equals(fClazz)
                || String.class.equals(fClazz)
                || Float.class.equals(fClazz)
                || Double.class.equals(fClazz);
    }

    /**
     * 验签并获取信息
     * @param message
     * @param key
     * @return 验签成功返回数据，失败返回空串""
     * @throws SignatureException
     */
    private static String verifySignGetInfo(String message, String key, boolean isDecode) throws SignatureException {
        try {
            logger.debug("cas VerifySignGetInfo message:" + message);
            java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            if (StringUtils.isEmpty(message)) {
                return "";
            }
            String[] dataBox = message.split("#");

            String signature = dataBox[0];
            String data = dataBox[1];
            logger.debug("verifySignGetInfo signature:" + signature);
            logger.debug("verifySignGetInfo data:" + data);
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
            logger.error("VerifySignGetInfo err ", ex);
            throw new SignatureException(ex);
        }
    }

    public static String verifySignGetInfo(String data, String key) throws SignatureException {
        return verifySignGetInfo(data, key, true);
    }
}
