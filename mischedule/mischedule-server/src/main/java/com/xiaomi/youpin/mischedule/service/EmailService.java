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

package com.xiaomi.youpin.mischedule.service;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailService {
    private static String URL = "http://support.d.xiaomi.net/mail/send";

    /**
     * wiki: https://wiki.n.miui.com/pages/viewpage.action?pageId=30214417
     *
     * @param addressList: 分号分隔 eg. "gaoyibo@xxxx.com;zhangxiuhua@xxxx.com"
     * @param title:       email title
     * @param body:        email body，支持 HTML
     * @return
     */
    public static void send(String addressList, String title, String body) {
        String postBody = "title=" + title
                + "&body=" + body
                + "&address=" + addressList
                + "&locale=";

        String post = HttpClientV2.post(URL, postBody, Maps.newHashMap(), 5000);

        log.info("send email params: [{}] result: [{}]", postBody, post);
    }
}

