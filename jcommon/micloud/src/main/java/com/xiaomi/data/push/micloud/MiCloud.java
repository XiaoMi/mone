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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedListMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.data.push.micloud.bo.request.CatalystRequest;
import com.xiaomi.data.push.micloud.bo.request.Control;
import com.xiaomi.data.push.micloud.bo.request.OrderInfo;
import com.xiaomi.data.push.micloud.bo.response.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */

@Slf4j
@Service
public class MiCloud {

    @Value("${micloud.url:}")
    private String micloudUrl;

    @Value("${catalyst.url:}")
    private String catalystUrl;
    
    private Gson gson = new Gson();

    private static final String CONTROL_URL = "https://127.0.0.1/api/gateway/host-control/production/api/v1/host/control";
    private static final String STATUS_URL = "https://127.0.0.1/api/gateway/host-control/production/api/v1/host/status";
    private String[] machineRunningStatus = {Constants.RUNNING, Constants.STARTING, Constants.REBOOTING};
    private String[] machineStoppedStatus = {"正在停止", "已经停止", "正在终止", "已经终止", "正在销毁", "已经销毁", "已经挂起", "已经暂停"};

    public String getProviderInfo(String cloudProvider, String token) {
        if (StringUtils.isEmpty(cloudProvider) || StringUtils.isEmpty(token)) {
            log.warn("cloud provider or token is empty");
            return null;
        }
        String url = micloudUrl + "/auth/v1/order/info/" + cloudProvider;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", token);
        return HttpClientV5.get(url, headers, 3000);
    }

    public SubmitOrder submitOrder(List<OrderInfo> orderinfos, String token) {
        if (orderinfos == null || StringUtils.isEmpty(token)) {
            log.warn("orderInfos is null or token is empty");
            return null;
        }
        log.info(orderinfos.toString());

        String url = micloudUrl + "/auth/v1/order/submit";

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(orderinfos);

            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS)
                .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "order_type=xbudget_order&order_infos=" + jsonString);

            Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            log.info(responseString);
            return gson.fromJson(responseString, SubmitOrder.class);
        } catch (JsonProcessingException e) {
            log.error(e.toString());
        } catch (IOException e) {
            log.error(e.toString());
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    public OrderDetail orderDetail(int orderId, String token) {
        if (StringUtils.isEmpty(token)) {
            log.warn("token is empty");
            return null;
        }
        String url = micloudUrl + "/auth/v1/order/detail?order_id=" + orderId;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", token)
                .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            log.info(responseString);
            return gson.fromJson(responseString, OrderDetail.class);
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (JsonSyntaxException e) {
            log.error("gson parse error", e);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    public PriceResponse getPrice(String suitId, String siteId) {
        try {
            Map<String, String> headers = new HashMap<>();
            String response = HttpClientV5.get(micloudUrl + "/api/v1/merge_orderng/region/suit/price?suit_id=" + suitId + "&site_id=" + siteId, headers);
            return gson.fromJson(response, PriceResponse.class);
        } catch (Throwable e) {
            log.error("get price:", e);
        }
        return null;
    }

    public CatalystResponse initMachine(CatalystRequest request) {
        try {
            String url = catalystUrl + "/catalyst/api/v1/host";
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            String body = gson.toJson(request);
            String response = HttpClientV5.post(url, body, headers);
            return gson.fromJson(response, CatalystResponse.class);
        } catch (JsonSyntaxException e) {
            log.error("gson parse error", e);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    public CatalystResponse machineInfo(String sequence) {
        try {
            String url = catalystUrl + "/catalyst/api/v1/host/" + sequence;
            Map<String, String> headers = new HashMap<>();
            String response = HttpClientV5.get(url, headers);
            return gson.fromJson(response, CatalystResponse.class);
        } catch (JsonSyntaxException e) {
            log.error("gson parse error", e);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    public boolean isMachineRunning(String hostname, ControlResponse status) {
        log.info("isMachineRunning hostname:{}  status:{}", hostname, status);
        if (status == null || status.getData() == null || status.getData().isEmpty()
            || status.getData().get(0).getMessage() == null) {
            return false;
        }
        String message = status.getData().get(0).getMessage();
        for (String runningStatus : machineRunningStatus) {
            if (message.equals(runningStatus)) {
                log.info("isMachineRunning hostname:{}  message equals {}", hostname, runningStatus);
                return true;
            }
        }
        return false;
    }

    public boolean isMachineOff(String hostname, ControlResponse status) {
        log.info("isMachineOff hostname:{}  status:{}", hostname, status);
        if (status == null || status.getData() == null || status.getData().isEmpty()
            || status.getData().get(0).getMessage() == null) {
            return false;
        }
        String message = status.getData().get(0).getMessage();
        for (String stoppedStatus : machineStoppedStatus) {
            if (message.equals(stoppedStatus)) {
                log.info("isMachineOff hostname:{}  message equals {}", hostname, stoppedStatus);
                return true;
            }
        }
        return false;
    }

    public ControlResponse getStatus(String accessKey, String secretKey, String[] hostnames) {
        return sendControlRequest(STATUS_URL, hostnames, accessKey, secretKey);
    }

    public ControlResponse powerOn(String accessKey, String secretKey, String reason, String[] hostnames) {
        Control control = new Control();
        control.setReason(reason);
        control.setAction(Constants.POWER_ON);
        control.setHostnames(hostnames);
        return sendControlRequest(CONTROL_URL, control, accessKey, secretKey);
    }

    public ControlResponse powerOff(String accessKey, String secretKey, String reason, String[] hostnames) {
        Control control = new Control();
        control.setReason(reason);
        control.setAction(Constants.POWER_OFF);
        control.setHostnames(hostnames);
        return sendControlRequest(CONTROL_URL, control, accessKey, secretKey);
    }

    public ControlResponse reboot(String accessKey, String secretKey, String reason, String[] hostnames) {
        Control control = new Control();
        control.setReason(reason);
        control.setAction(Constants.REBOOT);
        control.setHostnames(hostnames);
        return sendControlRequest(CONTROL_URL, control, accessKey, secretKey);
    }

    private ControlResponse sendControlRequest(String url, Object input, String accessKey, String secretKey) {
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
