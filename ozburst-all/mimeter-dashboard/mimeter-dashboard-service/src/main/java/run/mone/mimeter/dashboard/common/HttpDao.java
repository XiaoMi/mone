package run.mone.mimeter.dashboard.common;

import com.google.gson.Gson;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.exception.CommonException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Repository
public class HttpDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDao.class);

    private static final Gson gson = Util.getGson();

    private RequestConfig requestConfig;

    @PostConstruct
    public void init() {
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(20000)
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(20000)
                .build();
    }

    public HttpResult get(String url, Map<String, String> params) {
        Gson gson = new Gson();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;


        HttpResult result = null;

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for(Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }

            HttpGet request = new HttpGet(uriBuilder.build().toASCIIString());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setConfig(requestConfig);

            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                LOGGER.error("[HttpDao.get]: failed to request: {}, params: {} response status: {}",
                        url, params, state);
                return null;
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            LOGGER.debug("[HttpDao.get] response: {}", responseContent);
            result = gson.fromJson(responseContent, HttpResult.class);
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

    public HttpResult post(String url, String paramJson) throws Exception{
        HttpResult result = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            HttpPost request = new HttpPost(uriBuilder.build().toASCIIString());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setConfig(requestConfig);

            StringEntity requestEntity = new StringEntity(paramJson, "UTF-8");
            request.setEntity(requestEntity);


            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                LOGGER.error("[HttpDao.post]: failed to request: {}, params: {} response status: {}",
                        url, paramJson, state);
                throw new CommonException(CommonError.APIServerError.code, "请求API服务失败:" + state);
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            result = gson.fromJson(responseContent, HttpResult.class);
        } catch (Exception e) {
            LOGGER.error("http call error,err:{}",e.getMessage());
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                LOGGER.error("http call error,err:{}",e.getMessage());
                throw e;
            }
        }

        return result;
    }

    public HttpResult postWithUpload(String url, Map<String, String> params, String path) {
        Gson gson = new Gson();
        HttpResult result = null;
        LOGGER.debug("path:{}", path);

        File file = new File(path);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            HttpPost request = new HttpPost(uriBuilder.build().toASCIIString());
//            request.setHeader("Content-Type", "multipart/form-data");
            request.setHeader("Accept", "application/json");
            request.setConfig(requestConfig);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("fileData", file, ContentType.DEFAULT_BINARY, path);
            for(Map.Entry<String, String> entry : params.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
            }
            HttpEntity requestEntity = builder.build();
            request.setEntity(requestEntity);


            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                LOGGER.error("[HttpDao.postWithUpload]: failed to request: {}, params: {}, file path: {}, response status: {}",
                        url, params, path, state);
                throw new CommonException(CommonError.APIServerError.code, "请求API服务失败:" + state);
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            result = gson.fromJson(responseContent, HttpResult.class);
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

    public HttpResult delete(String url, Map<String, String> params) {
        Gson gson = new Gson();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;


        HttpResult result = null;

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for(Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }

            HttpDelete request = new HttpDelete(uriBuilder.build().toASCIIString());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            request.setConfig(requestConfig);

            response = httpClient.execute(request);

            int state = response.getStatusLine().getStatusCode();
            if (state != HttpStatus.SC_OK) {
                LOGGER.error("[HttpDao.delete]: failed to request: {}, params: {} response status: {}",
                        url, params, state);
                return null;
            }

            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            LOGGER.debug("[HttpDao.delete] response: {}", responseContent);
            result = gson.fromJson(responseContent, HttpResult.class);
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
