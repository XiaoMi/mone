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
package com.xiaomi.mone.log.stream;

import com.xiaomi.mone.log.stream.sink.TeslaSink;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/8 15:10
 */
@Slf4j
public class TeslaSinkTest {

    @Test
    public void test() {
        Ioc.ins().init("com.xiaomi");
        TeslaSink flinkService = Ioc.ins().getBean(TeslaSink.class);
        Map<String, Object> map = new HashMap<>();
        flinkService.execute(map);
    }
}
