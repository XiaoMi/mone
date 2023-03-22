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

package com.xiaomi.data.push.micloud;

import com.google.common.collect.LinkedListMultimap;
import com.google.gson.Gson;
import com.xiaomi.data.push.micloud.bo.response.ControlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@Service
public class ControlRequestSender {
    private Gson gson = new Gson();


    public ControlResponse sendControlRequest(String url, Object input, String accessKey, String secretKey) {
        String response = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
            headers.put("content-type", "application/json; charset=utf-8");
            headers.put("date", dateFormat.format(new Date()));
            return gson.fromJson(response, ControlResponse.class);
        } catch (URISyntaxException e) {
            log.info("params: {}", input);
            log.error("response: " + response);
            log.error(e.toString());
        } catch (Exception e) {
            log.info("params: {}", input);
            log.error("response: " + response);
            log.error(e.toString());
        }
        return null;
    }
}
