/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.bug.service;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.bug.domain.Record;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/9/5
 */
public class RecordServiceTest extends BaseTest {


    @Test
    public void testRecord() {
        RecordService service = Ioc.ins().getBean(RecordService.class);
        service.record(new Record());
    }
}
