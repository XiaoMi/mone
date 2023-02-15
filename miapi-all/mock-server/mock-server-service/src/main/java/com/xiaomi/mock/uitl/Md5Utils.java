package com.xiaomi.mock.uitl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
    };

    private static final int HEX_VALUE_COUNT = 16;

    public static String getMD5(byte[] bytes) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
        if (messageDigest != null) {
            return new BigInteger(1, messageDigest.digest(bytes)).toString(HEX_VALUE_COUNT);
        }

        throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
    }

    public static String getMD5(String value) {
        try {
            return getMD5(value.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
