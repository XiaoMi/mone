/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
