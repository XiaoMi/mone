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

package com.xiaomi.youpin.gwdash.common;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
public class HttpUtils {

    public static HttpResult get(String url, Map<String, String> headers, Map<String, String> params, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)  // CS 数据交互时间
                .setConnectTimeout(timeout) // 指建立连接的超时时间
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
            // request.setHeader("Content-Type", "application/json");
            // request.setHeader("Accept", "application/json");

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

    public static HttpResult get(String url, Map<String, String> headers, boolean isRedirectsEnabled, Map<String, String> params, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setRedirectsEnabled(isRedirectsEnabled)
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
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            log.debug("[HttpUtils.get] response: {}", responseContent);
            result = new HttpResult();
            result.setStatus(response.getStatusLine().getStatusCode());
            result.setContent(responseContent);
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
                .setSocketTimeout(timeout)  // CS 数据交互时间
                .setConnectTimeout(timeout) // 指建立连接的超时时间
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
            // request.setHeader("Content-Type", "application/json");
            // request.setHeader("Accept", "application/json");
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.setHeader(entry.getKey(), entry.getValue());
                }
            }

            request.setConfig(requestConfig);
            StringEntity requestEntity = new StringEntity(params, "UTF-8");
            request.setEntity(requestEntity);


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


    /**
     * 发送邮件
     *
     * @param urlStr
     * @param content
     * @return
     */
    public static boolean sendEmail(String urlStr, String address, String title, String content) {
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlStr + "&title=" + title + "&address=" + address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.addRequestProperty("Connection", "close");
            conn.getOutputStream().write(("body=" + content).getBytes());
            conn.getOutputStream().flush();
            String res = new String(ByteStreams.toByteArray(conn.getInputStream()));
            log.info("res:{}", res);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
        }
    }

}
