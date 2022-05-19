package com.xiaomi.data.push.client;

import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public abstract class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static Pair<Integer, String> get(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(500)
                .setConnectionRequestTimeout(500)
                .build();
        HttpGet get = new HttpGet(url);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            get.setConfig(requestConfig);
            response = httpClient.execute(get);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            logger.debug("http get url:{} body:{} result:{}", url, responseContent);
            return Pair.of(response.getStatusLine().getStatusCode(), responseContent);
        } catch (Exception e) {
            return Pair.of(500, e.getMessage());
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

    public static String get(String url, Map<String, String> headers) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(500)
                .setConnectionRequestTimeout(500)
                .build();
        HttpGet get = new HttpGet(url);

        headers.entrySet().stream().forEach(it -> get.setHeader(it.getKey(), it.getValue()));

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            get.setConfig(requestConfig);
            response = httpClient.execute(get);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            logger.debug("http get url:{} body:{} result:{}", url, responseContent);
            return responseContent;
        } catch (Exception e) {
            return e.toString();
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


    public static String post(String url, String body, Map<String, String> headers, int timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();


        HttpPost post = new HttpPost(url);
        headers.entrySet().forEach(it -> {
            post.setHeader(it.getKey(), it.getValue());
        });


        try {
            post.setEntity(new StringEntity(body));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            post.setConfig(requestConfig);
            response = httpClient.execute(post);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
//            logger.debug("http post url:{} body:{} result:{}", url, body, responseContent);
            return responseContent;
        } catch (Exception e) {
            return e.toString();
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


    public static String post(String url, String body) {
        return post(url, body, Maps.newHashMap(), 1000);
    }

    public static String post(String url, String body, int timeout) {
        return post(url, body, Maps.newHashMap(), timeout);
    }
}
