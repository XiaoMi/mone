/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.mock.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    });

    private static final int HEX_VALUE_COUNT = 16;

    public static String getMD5(byte[] bytes) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
        if (messageDigest != null) {
            return new BigInteger(1, messageDigest.digest(bytes)).toString(HEX_VALUE_COUNT);
        }

        throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
    }

    public static String getMD5(String value, String encode) {
        try {
            return getMD5(value.getBytes(encode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5(String value) {
        try {
            return getMD5(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
