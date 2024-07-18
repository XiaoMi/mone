package run.mone.z.desensitization.service.common;

import com.alibaba.nacos.common.utils.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/31/24 8:15 PM
 */
public class HttpClientUtil {
    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String APPLICATION_JSON = "application/json";

    private static final String PREFIX = "Bearer ";

    private static final String UTF_8 = "UTF-8";

    private static CloseableHttpClient httpClient = null;
    private static RequestConfig requestConfig = null;

    static {
        // 设置配置请求参数
        requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 请求超时时间
                .setSocketTimeout(60000)// 数据读取超时时间
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        //connectionManager.setValidateAfterInactivity(TimeValue.ofMinutes(5));
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRedirectStrategy(new LaxRedirectStrategy()) // Follow Redirect 跟随重定向
                .build();
    }

    public static String doGet(String url) {
        return doGet(url, null, null, null);
    }

    public static String doGet(String url, Map<String, Object> param) {
        return doGet(url, param, null, null);
    }

    public static String doGet(String url, Map<String, Object> param, Map<String, Object> headerParamMap, Pair<String, String> headerTokenPair) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            // 创建 uri
            URIBuilder builder = new URIBuilder(url);
            if (MapUtils.isNotEmpty(param)) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, String.valueOf(param.get(key)));
                }
            }
            URI uri = builder.build();

            // 创建 http GET请求
            HttpGet httpGet = new HttpGet(uri);

            if (headerTokenPair != null) {
                httpGet.addHeader(headerTokenPair.getKey(), PREFIX + headerTokenPair.getValue());
            }

            if (null != headerParamMap && !headerParamMap.isEmpty()) {
                for (String key : headerParamMap.keySet()) {
                    if (!httpGet.containsHeader(key)) {
                        httpGet.addHeader(key, (String) headerParamMap.get(key));
                    }
                }
            }
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), UTF_8);
            } else {
                log.error("doGet error, response:{}", response);
            }
        } catch (Exception e) {
            log.error("doGet error, url:{}", url, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("doGet close response error:", e);
            }
        }
        return result;
    }

    public static String doPostString(String url, String body, Map<String, Object> headerParamMap) {
        try {
            HttpPost post = new HttpPost(url);
            if (headerParamMap != null && !headerParamMap.isEmpty()) {
                for (String key : headerParamMap.keySet()) {
                    if (!post.containsHeader(key)) {
                        post.addHeader(key, (String) headerParamMap.get(key));
                    }
                }
            }
            post.setEntity(new StringEntity(body, UTF_8));
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity resEntity = response.getEntity();
            if (resEntity == null) {
                return null;
            }
            return EntityUtils.toString(resEntity);
        } catch (Exception e) {
            log.error("error in post request,error message={}", e);
        }
        return null;
    }
}
