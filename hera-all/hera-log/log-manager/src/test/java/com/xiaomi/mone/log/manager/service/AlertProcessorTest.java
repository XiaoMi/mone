package com.xiaomi.mone.log.manager.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/22 15:25
 */
@Slf4j
public class AlertProcessorTest {

    public Gson gson = new Gson();

    @Test
    public void sendCardMessageTest() throws UnsupportedEncodingException {
        Ioc.ins().init("com.xiaomi");
    }

    private String[] splitString(String S) {
        if (StringUtils.isEmpty(S)) {
            return null;
        }
        return S.split("[,]");
    }
}
