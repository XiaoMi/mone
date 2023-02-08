package com.xiaomi.youpin.gateway.function.imp;

import com.xiaomi.youpin.gateway.http.Http;
import lombok.extern.slf4j.Slf4j;
import org.nutz.http.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class HttpImp implements Http {

    @Override
    public String get(String url) {
        Response res = org.nutz.http.Http.get(url);
        return res.getContent();
    }

    @Override
    public String post(String url, Map<String, Object> params, int timeout) {
        String res = org.nutz.http.Http.post(url, params, timeout);
        return res;
    }


}
