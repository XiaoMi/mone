package run.mone.hera.operator.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class HttpClientUtil {
	
	private PoolingHttpClientConnectionManager pool =null;
	private int TIMEOUT = 5 * 1000;
	private int MAX_HTTP_TOTAL_CONNECTION = 1000;
	private int MAX_CONNECTION_PER_HOST = 200;
	private RequestConfig requestConfig = null;
	private CloseableHttpClient singleHttpClient = null;

	private HttpClientUtil() {
		requestConfig = RequestConfig.custom()
	                .setSocketTimeout(TIMEOUT)
	                .setConnectTimeout(TIMEOUT)
	                .setConnectionRequestTimeout(TIMEOUT)
	                .build();
		
		//
		ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				return 20*1000;
			}
		};
		//
		pool =  new PoolingHttpClientConnectionManager();
		pool.setMaxTotal(MAX_HTTP_TOTAL_CONNECTION);
		pool.setDefaultMaxPerRoute(MAX_CONNECTION_PER_HOST);
	    singleHttpClient = HttpClients.custom().setConnectionManager(pool)
	    		.setKeepAliveStrategy(keepAliveStrategy)
	    		.setMaxConnTotal(MAX_HTTP_TOTAL_CONNECTION)
	    		.setMaxConnPerRoute(MAX_CONNECTION_PER_HOST)
	    		.setDefaultRequestConfig(requestConfig)
	    		.setUserAgent("Mozilla/4.0")
	    		.build();
	}
	
	/**
	 * 获取HttpClient实例
	 * @return
	 */
	public static HttpClientUtil getInstance(){
		return HttpUtilSingle.instance;
	}
	
	/**
	 * 线程池中获取获取一个CloseableHttpClient
	 * @return
	 */
	public CloseableHttpClient getHttpClient(){

		return singleHttpClient;
	}
	
	/**
	 * 释放CloseableHttpResponse
	 * 打印调用详细日志
	 * @param hur
	 * @param chr
	 * @param startTime
	 */
	public static void closeResponse(HttpUriRequest hur, CloseableHttpResponse chr, long startTime) {
		String url = "http://aaaa.com";
		int stateCode = -1;
		long endTime = System.currentTimeMillis();
		long useTime = endTime - startTime;
		try {
			
			if (null != hur) {
				url = hur.getURI().getPath();
			}
			
			if (null != chr) {
				stateCode = chr.getStatusLine().getStatusCode();
				chr.close();
			}
		} catch (Exception e) {

		}
		
	}
	
	/**
	 * 获取默认的RequestConfig
	 * @return
	 */
	public RequestConfig getRequestConfig() {
		
		return requestConfig;
	}
	
	/**
	 * 单例模式
	 *
	 */
	private static class HttpUtilSingle {
		private static HttpClientUtil instance = new HttpClientUtil();
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static long currentTimeMillis() {
		
		return System.currentTimeMillis();
	}

	public static String sendPostRequest(String url, String body, Map<String,String> headers) {
		CloseableHttpClient client = getInstance().getHttpClient();
		try {
			RequestBuilder requestBuilder = RequestBuilder.post(url);
			if(headers != null) {
				for(String headerKey : headers.keySet()) {
					requestBuilder.setHeader(headerKey, headers.get(headerKey));
				}
			}
			requestBuilder.setConfig(HttpClientUtil.getInstance().getRequestConfig());
			if (StringUtils.isNotEmpty(body)) {
				StringEntity entity = new StringEntity(body, Charset.forName("UTF-8"));
				requestBuilder.setEntity(entity);
			}
			HttpUriRequest httpUriRequest = requestBuilder.build();
			String responseBody = null;
			CloseableHttpResponse response = null;
			try {
				response = client.execute(httpUriRequest);
				responseBody = EntityUtils.toString(response.getEntity(), "utf-8");
				log.info("response body : "+responseBody);
				return responseBody;
			} catch (Exception e) {
				log.error(e.getMessage() + ":" + url);
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			log.error("调用post失败：url : "+url + " body : "+body, e);
		}
		return null;
	}
}
