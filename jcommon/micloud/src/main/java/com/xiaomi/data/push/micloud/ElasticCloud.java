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
import com.xiaomi.data.push.micloud.bo.request.BookRequest;
import com.xiaomi.data.push.micloud.bo.request.CountRequest;
import com.xiaomi.data.push.micloud.bo.request.DetailRequest;
import com.xiaomi.data.push.micloud.bo.request.OfflineRequest;
import com.xiaomi.data.push.micloud.bo.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */

@Slf4j
@Service
public class ElasticCloud {
    @Autowired
    private ControlRequestSender controlRequestSender;
    private Gson gson = new Gson();
    private static final String CLOUD_URL = "https://127.0.0.1/api/gateway/host-apply/production";

    public Result<OfflineResponse> offlineInstance(boolean skipTicket, boolean skipCheckOffline, long ticketId, String[] hostnames,
                                                   String accessKey, String secretKey) {
        String url = CLOUD_URL + "/api/v1/host/offline";
        OfflineRequest request = new OfflineRequest();
        request.setSkipTicket(skipTicket);
        request.setSkipCheckOffline(skipCheckOffline);
        request.setTicketId(ticketId);
        request.setHostnames(hostnames);
        String response = sendRequest(url, request, accessKey, secretKey);
        return gson.fromJson(response, Result.class);
    }


    public Result<List<Detail>> detail(long childOrderId, String accessKey, String secretKey) {
        String url = CLOUD_URL + "/api/v1/host/done/detail";
        DetailRequest request = new DetailRequest();
        request.setChildOrderId(childOrderId);
        String response = sendRequest(url, request, accessKey, secretKey);
        return gson.fromJson(response, Result.class);
    }

    public Result<BookResponse> bookInstance(long childOrderId, long count, String accessKey, String secretKey) {
        String url = CLOUD_URL + "/api/v1/booking/book";
        BookRequest request = new BookRequest();
        request.setChildOrderId(childOrderId);
        request.setCount(count);
        String response = sendRequest(url, request, accessKey, secretKey);
        return gson.fromJson(response, Result.class);
    }

    public Result<CountResponse> getCount(long childOrderId, String accessKey, String secretKey) {
        String url = CLOUD_URL + "/api/v1/host/count";
        CountRequest request = new CountRequest();
        request.setChildOrderId(childOrderId);
        String response = sendRequest(url, request, accessKey, secretKey);
        return gson.fromJson(response, Result.class);
    }


    private String sendRequest(String url, Object input, String accessKey, String secretKey) {
        String response = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
            headers.put("content-type", "application/json; charset=utf-8");
            headers.put("date", dateFormat.format(new Date()));
            return response;
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
