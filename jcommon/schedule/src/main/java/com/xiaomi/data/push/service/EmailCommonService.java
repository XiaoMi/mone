//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xiaomi.data.push.service;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailCommonService {
    private static final Logger log = LoggerFactory.getLogger(EmailCommonService.class);
    private static String URL = "http://127.0.0.1/mail/send";

    public EmailCommonService() {
    }

    public static void send(String addressList, String title, String body) {
        String postBody = "title=" + title + "&body=" + body + "&address=" + addressList + "&locale=";
        String post = HttpClientV2.post(URL, postBody, Maps.newHashMap(), 5000);
        log.info("send email params: [{}] result: [{}]", postBody, post);
    }
}
