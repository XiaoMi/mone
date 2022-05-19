package com.xiaomi.data.push.micloud;

import com.google.common.collect.LinkedListMultimap;
import com.google.gson.Gson;
import com.xiaomi.data.push.micloud.bo.response.ControlResponse;
import com.xiaomi.fusion.cloud.auth.core.auth.Signer;
import com.xiaomi.fusion.cloud.auth.core.enums.HttpMethod;
import com.xiaomi.fusion.cloud.auth.core.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

            URI uri = new URI(url);
            LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
            headers.put("content-type", "application/json; charset=utf-8");
            headers.put("date", dateFormat.format(new Date()));
            String signature = Signer.getAuthorizationHeader(HttpMethod.POST, uri, headers, accessKey, secretKey);
            headers.put("authorization", signature);
            response = HttpUtil.postJson(uri.toString(), input, headers);
            return gson.fromJson(response, ControlResponse.class);
        } catch (URISyntaxException e) {
            log.info("params: {}", input);
            log.error("response: " + response);
            log.error(e.toString());
        } catch (IOException e) {
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
