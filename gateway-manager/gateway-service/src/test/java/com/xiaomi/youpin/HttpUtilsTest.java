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

package com.xiaomi.youpin;

import com.xiaomi.youpin.gwdash.common.HttpUtils;
import com.xiaomi.youpin.ks3.KsyunService;
import org.junit.Test;

public class HttpUtilsTest {


    @Test
    public void testSendMail() {
        HttpUtils.sendEmail("http://127.0.0.1:80/mail/send?mailType=OTHER","zhangzhiyong1@xiaomi.com","tttttt","ccccccc");
    }


    @Test
    public void testHttpDownload() {
        KsyunService ksyunService = new KsyunService("http://127.0.0.1:80");
        ksyunService.setToken("");
        byte[] data = ksyunService.getFileByKey(".jar");
        System.out.println(data.length);
    }
}
