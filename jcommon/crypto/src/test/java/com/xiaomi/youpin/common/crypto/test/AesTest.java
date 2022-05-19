package com.xiaomi.youpin.common.crypto.test;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AesTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("++++", "utf-8"));
    }
}
