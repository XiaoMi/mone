package com.xiaomi.mone.tpc.login.util;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * zgf
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static volatile CloseableHttpClient httpClient = null;

    static {
        init();
    }

    public static void init() {
        if (httpClient == null) {
            synchronized (HttpClientUtil.class) {
                if (httpClient == null) {
                    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
                    // 总连接池数量
                    connectionManager.setMaxTotal(100);
                    // setConnectTimeout：设置建立连接的超时时间
                    // setConnectionRequestTimeout：从连接池中拿连接的等待超时时间
                    // setSocketTimeout：发出请求后等待对端应答的超时时间
                    RequestConfig requestConfig = RequestConfig.custom()
                            .setConnectTimeout(2000)
                            .setConnectionRequestTimeout(2000)
                            .setSocketTimeout(4000)
                            .build();
                    HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();
                    httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig)
                            .setRetryHandler(retryHandler).build();
                }
            }
        }
    }

    public static <T> T doHttpGet(String authTokenUrl, Map<String, String> getParams, TypeToken<T> type) {
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(authTokenUrl);
            if (null != getParams && !getParams.isEmpty()) {
                List<NameValuePair> list = new ArrayList<>();
                for (Map.Entry<String, String> param : getParams.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                uriBuilder.setParameters(list);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    return GsonUtil.gsonToBean(resStr, type);
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-get-请求异常", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> T doHttpPostJson(String reqUrl, Object param, Map<String, String> headers, TypeToken<T> type) {
        HttpEntity httpEntity = null;
        if (param != null) {
            httpEntity = new StringEntity(GsonUtil.gsonString(param), "utf-8");
        }
        return doHttpPost(reqUrl, httpEntity, headers, type);
    }


    private static <T> T doHttpPost(String reqUrl, HttpEntity httpEntity, Map<String, String> headers, TypeToken<T> type) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(reqUrl);
            if (httpEntity != null) {
                httpPost.setEntity(httpEntity);
            }
            httpPost.addHeader("Content-Type", "application/json");
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    String resStr = EntityUtils.toString(entity, "utf-8");
                    if (type.getRawType().equals(String.class)) {
                        return (T)resStr;
                    }
                    return GsonUtil.gsonToBean(resStr, type);
                }
            }
        } catch (Exception e) {
            logger.error("CloseableHttpClient-post-请求异常", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}