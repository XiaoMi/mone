/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaomi.mone.http.docs.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

public class HttpUtils {

    public static void post(String url, Map<String, String> headers, String params, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();

        Gson gson = new Gson();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
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

                if (null != params && !params.isEmpty()){
                    Map<String,String> paramsMap = gson.fromJson(params,new TypeToken<Map<String, String>>() {}.getType());
                    List<NameValuePair> paramList = new ArrayList<>();
                    paramsMap.forEach((key, value) -> paramList.add(new BasicNameValuePair(key,value)));
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
                HttpResult.fail(state);
            }
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
    }
}
