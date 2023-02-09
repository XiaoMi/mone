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
package com.xiaomi.mone.dubbo.mock.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtil {

    private static final class CloseableHttpClientHolder {
        static final CloseableHttpClient closeableHttpClient = createHttpClient();
    }

    private static CloseableHttpClient getHttpClient(){
        return CloseableHttpClientHolder.closeableHttpClient;
    }

    private static CloseableHttpClient createHttpClient(){
        //连接池默认设置不改了
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }

    public static String doGet(String url) throws IOException{
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse =getHttpClient().execute(httpGet);

        return EntityUtils.toString(httpResponse.getEntity(),"utf-8");
    }

}
