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

package com.xiaomi.miapi.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.common.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Slf4j
public class HttpUtils {

    public static HttpResult get(String url, Map<String, String> headers, Map<String, String> params, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();


        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;


        HttpResult result = null;

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet request = new HttpGet(uriBuilder.build().toASCIIString());
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }

            request.setConfig(requestConfig);

            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                log.error("[HttpUtils.get]: failed to request: {}, headers: {}, params: {} response status: {}",
                        url, headers, params, state);
                return HttpResult.fail(state);
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            log.debug("[HttpUtils.get] response: {}", responseContent);

            result = HttpResult.success(HttpStatus.SC_OK, responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static HttpResult post(String url, Map<String, String> headers, String params, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();


        HttpResult result = null;
        Gson gson = new Gson();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            HttpPost request = new HttpPost(uriBuilder.build().toASCIIString());
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }

            request.setConfig(requestConfig);
            StringEntity requestEntity;

            if (request.containsHeader("form_data") && "true".equals(request.getFirstHeader("form_data").getValue())){

                if (StringUtils.isNotEmpty(params)){
                    Map<String,String> paramsMap = gson.fromJson(params,new TypeToken<Map<String, String>>() {}.getType());
                    List<NameValuePair> paramList = new ArrayList<>();
                    paramsMap.forEach((key, value) -> {
                        paramList.add(new BasicNameValuePair(key,value));
                    });
                    HttpEntity httpEntity = new UrlEncodedFormEntity(paramList,"UTF-8");
                    request.setEntity(httpEntity);
                }
            }else {
                requestEntity = new StringEntity(params, "UTF-8");
                request.setEntity(requestEntity);
            }

            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                log.error("[HttpUtils.post]: failed to request: {}, headers: {}, params: {}, response status: {}",
                        url, headers, params, state);
                return HttpResult.fail(state);
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");

            result = HttpResult.success(HttpStatus.SC_OK, responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
